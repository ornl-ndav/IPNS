package IPNS.Runfile;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
This class is designed to provide an interface to IPNS run files for versions
V4 and earlier.  The Runfile constructor loads information from the run file
header and the tables that it describes.  Access metods are provided to give
access to this information and to the scattering data stored in the file.
Some of the methods for this class return arrays that have an empty zeroth
element.  As a rule, if the array is indexed to detector or subgroup ID it is
indexed starting at one.  If the array contains other types of data it is
indexed starting at zero.

@author  John P Hammonds
@author  Richard J. Goyette
@version 4.0beta5
*/

public class Runfile {
    /** Flag to indicate if file is left open */ boolean leaveOpen;
    /** The file name associated with this Runfile object. */
    protected String runfileName;
    /** The FileStream containing the data. */   RandomAccessFile runfile;
    /** The runfile header */ 			protected Header header; 
    /** The detector mapping table */		DetectorMap[] detectorMap =
	new DetectorMap[1];
    /** The time field table */			TimeField[] timeField =
	new TimeField[1];
    /** Array of detector angles */		float[] detectorAngle;
    /** Array of flight path lengths */		float[] flightPath;
    /** Array of detector heights */		float[] detectorHeight;
    /** Array of detector types */		short[] detectorType;
    /** Array of time Scale parameters */        float[] timeScale;
    /** Array of time shift parameters */        float[] timeShift;
    /** Array of area detector start Times */    float[] areaStartTime;
    /** Array of time dela y parameters */        float[] timeDelay;
    /** Stored Message Region */                 String messageRegion;
    /** PSD Mapping Table */                     LpsdDetIdMap lpsdIDMap;
    /** Discriminator Levels */			DiscLevel[] discriminator;
    /** List of subgroup by ID */		int[] subgroupIDList;
    /** Maximum Subgroup ID in a histogram */    int[] maxSubgroupID;
    /** Minimum Subgroup ID in a histogram */    int[] minSubgroupID;
    /** Subgroup Map Table */			int[][] subgroupMap;
    /** Version Number */                       protected int versNum;
    /** run properties stored in message area */
                                               protected Properties properties;
    /** lpsd Detector Map Table*/              protected LpsdMap[] lpsdMap;
    /** lpsd Time Field Table */               protected LpsdTimeField[] 
	                                                        lpsdTimeField;
    /** lpsd ChannelStartTimeTable */          protected LpsdChannelStartTime 
	                                                        lpsdCst;
    /** Array of lpsd angles */		        protected float[] lpsdAngle;
    /** Array of lpsd flight path lengths */	protected float[] 
	                                                       lpsdFlightPath;
    /** Array of detector heights */		protected float[] lpsdHeight;
    /** Array of detector types */		protected short[] lpsdType;


// --------------------------- readUnsignedInteger -------------------

  protected int readUnsignedInteger(RandomAccessFile inFile,
   int length) throws IOException {

    byte b[] = new byte[length];
    int c[] = new int[length];
    int nBytesRead = inFile.read(b, 0, length);
    int num = 0;
    for (int i = 0; i < length; ++i) {
       if(b[i] < 0) {
        c[i] = b[i] + 256;
      }
      else {
        c[i] = b[i];
      }
      num += c[i] * (int)Math.pow(256.0, (double)i);
    }
    return num;
  }

    //-------------------- ReadIntegerArray ----------------------------------
    protected int[] ReadIntegerArray(RandomAccessFile inFile, int wordSize,
				     int numWords)
	throws IOException {
	int[] data = new int[numWords+1];
	byte[] bdata = new byte[numWords * wordSize];

	int nbytes = inFile.read(bdata, 0, numWords * wordSize);
	for (int i=0; i<numWords; i++){
	    for (int j=0; j<wordSize; j++) {
		int byteIndex = i * wordSize + j;
		if ( bdata[byteIndex] < 0 ) {
		    data[i+1] += (int)(bdata[byteIndex] + 256) * 
			(int)Math.pow(256.0, j);
		}
		else {
		    data[i+1] += (int)(bdata[byteIndex] * Math.pow(256.0, j));
		}
	    }
	}
	return data;
    }

    //-------------------- ReadVaxReal4Array ---------------------------------

    protected float[] ReadVAXReal4Array(RandomAccessFile inFile, int numWords)
	throws IOException {
	int[] data;
	float[] fdata = new float[numWords+1];
	data = ReadIntegerArray (inFile, 4, numWords);

	for (int iword = 1; iword < numWords + 1; iword++) {
	    long hi_mant, low_mant, exp, sign;
	    double f_val;
	    long val = (long)data[iword];
	    if (val < 0) {
		val = val + (long)Math.pow(2.0, (double)32);
	    }
	    /* add 128 to put in the implied 1 */
	    hi_mant  = (val & 127) + 128;
	    val      = val >> 7;
	    /* exponent is "excess 128" */
	    exp      = ((int)(val & 255)) - 128;
	    val      = val >> 8;

	    sign     = val & 1;
	    low_mant = val >> 1;
	    /* This could also be a "reserved" operand of some sort?*/
	    if ( exp == -128 )
		f_val = 0;
	    else
		f_val = (hi_mant / 256.0 + low_mant / 16777216.0) *
		    (float)Math.pow(2.0, (double)exp );

	    if ( sign == 1 )
		f_val = -f_val;

	    fdata[iword] = (float)f_val;
	} 
	return fdata;
    }

    protected short[] ReadShortArray(RandomAccessFile inFile, int numWords)
	throws IOException {
	short[] sdata = new short[numWords + 1];
	int[] data;  
	data = ReadIntegerArray( inFile, 2, numWords );
	for (int i = 1; i <= numWords; i++) {
	    sdata[i] = (short)data[i];
	}
	return sdata;
    }

    /**
       This function provides a test method for this class' functionality.  It 
       will provide a sampling of the information that is retrieved as a new 
       Runfile Object is created.  It accepts a filename as the first command 
       line argument.

       @param args - The first command line parameter is the runfile name.  
       This parameter should contain the file path unless the file is in
       the current directory.

    */
    public static void main(String[] args) throws IOException {
	int i;
	int[] subgroups;
	Runfile runFile = new Runfile(args[0]);
	System.out.println(runFile.UserName());
	if (runFile.header.nDet > 0 ){
	for (i=1; i <= runFile.header.nDet; i++){
	    System.out.println("theta, l, h, iType: " 
			       + (float)runFile.detectorAngle[i]
			       + " " + (float)runFile.flightPath[i] + " "
			       + (float)runFile.detectorHeight[i] + " "
			       + runFile.detectorType[i]);
	}
	float data[];
	data = runFile.Get1DSpectrum(1, 1);
	float[]  energies = runFile.TimeChannelBoundaries( 100, 1);
	for (i = 1; i <= runFile.MaxSubgroupID(runFile.NumOfHistograms())
		 ; i++){
	    System.out.print( i + " - ");
	    int[] ids = runFile.IdsInSubgroup(i);
	    for ( int k = 0; k < ids.length; k++){
		System.out.print(" " + ids[k] );
	    }
	    System.out.println(runFile.IsSubgroupBeamMonitor(i));
	}
	}
	for ( Enumeration propNames= runFile.propertyNames(); 
	      propNames.hasMoreElements();  ) {
	    String key = (String)propNames.nextElement();
	    System.out.println( key + ":" + runFile.getProperty( key ) );
	}
    }


    /**
       Default Constructor with empty Runfile.
    */
    public Runfile() {
	header = new Header();
    }

    /**
       This method is a constructor method for the class.  On creation the 
       method will load data from the Runfile's header.  Other access methods 
       are provided to retrieve the loaded data.

       @param runfile - This argument should contain the file path unless the 
       file is in the current directory.
    */
    public Runfile( String infileName ) throws IOException {
	int i;

	RandomAccessFile runfile = new RandomAccessFile ( infileName, "r");
	runfileName = new String(infileName);
	int slashIndex = runfileName
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = runfileName.substring( slashIndex+1, slashIndex + 5 );

	header = new Header(runfile, iName  );
	timeField[0] = new TimeField();
	runfile.seek(68);  
	if ( header.versionNumber > 4 ) {
	    System.out.println( "Version Number > 4" );
	    System.out.println( "Object creation passed to higher version" );
	    LoadV5( runfile );
	}
	else {
	    System.out.println( "Version Number < = 4" );
	    System.out.println( "Object creation Handled by Runfile v4" );
	    LoadV4( runfile )
;
	}

    }

    void LoadV4( RandomAccessFile runfile ) throws IOException {
	int i;
	if ( header.nDet > 0 ) {
	    detectorMap = new DetectorMap[this.header.detectorMapTable.size/4
					 + 1];
	    for (i=1; i <= this.header.detectorMapTable.size/4; i++){
		detectorMap[i] = new DetectorMap(runfile, i, header);
	    }

	    timeField = new TimeField[this.header.timeFieldTable.size/16 + 1];
	    for (i=1; i <= this.header.timeFieldTable.size/16; i++){
		timeField[i] = new TimeField(runfile, i, header);
	    }

	    runfile.seek(this.header.detectorAngle.location);
	    detectorAngle = ReadVAXReal4Array(runfile, 
					      header.detectorAngle.size/4);

	    runfile.seek(this.header.flightPath.location);
	    flightPath = ReadVAXReal4Array(runfile, header.flightPath.size/4);

	    runfile.seek(this.header.detectorHeight.location);
	    detectorHeight = ReadVAXReal4Array(runfile, 
					       header.detectorHeight.size/4);

	    runfile.seek(this.header.detectorType.location);
	    detectorType = ReadShortArray(runfile, header.detectorType.size/2);

	    if (header.versionNumber > 3) {
		runfile.seek(this.header.discSettings.location);
		int size = header.nDet*2;
		short[] tdisc = ReadShortArray(runfile, size );
		discriminator = new DiscLevel[this.header.nDet + 1];
		for (i = 1; i <= this.header.nDet; i++) {
		    discriminator[i] = new DiscLevel();
		    discriminator[i].lowerLevel = tdisc[(i-1)*2 + 1];
		    discriminator[i].upperLevel = tdisc[(i-1)*2 + 2];
		}
	    }

	    this.leaveOpen = false;
	    subgroupIDList = new int[ header.nDet * header.numOfHistograms + 
				    1 ];
	    subgroupMap = new int[1][1];
	    for (i=0; i < subgroupIDList.length; i++ )
		subgroupIDList[i] = -1;
	    int group = 0;
	    maxSubgroupID = new int[ header.numOfHistograms + 1 ];
	    minSubgroupID = new int[ header.numOfHistograms + 1 ];
	    for (int nHist = 1; nHist <= header.numOfHistograms; nHist++) {
		minSubgroupID[nHist] = group + 1;
		for (int nDet = 1; nDet <= header.nDet; nDet++ ) {
		    int index = ( (nHist -1) * header.nDet ) + nDet;
		    int tfType = detectorMap[index].tfType;
		    if ((subgroupIDList[index] == -1) && (tfType != 0)) {
			group++;
			subgroupIDList[index] = group;
			int[][] tempMap = new int[group + 1][];
			System.arraycopy(subgroupMap, 0, tempMap, 0, 
					 subgroupMap.length);
			int[] idList = {0, nDet};
			for (int k = nDet+1; k <= header.nDet; k++) {
			    int index2 = ( (nHist -1) * header.nDet ) + k;
			    int tfType2 = detectorMap[index2].tfType;
			    if ( ( detectorMap[index2].address == 
				   detectorMap[index].address) &&
				 (tfType2 != 0) ){
				subgroupIDList[index2] = group;
				int[] tempList = new int[ idList.length + 1];
				System.arraycopy(idList, 0, tempList, 0, 
						 idList.length);
				idList = tempList;
				idList[idList.length - 1] = k;
			    }
			}
			subgroupMap = tempMap;
			subgroupMap[group] = new int[idList.length - 1];
			System.arraycopy(idList, 1, subgroupMap[group], 0, 
					 idList.length-1);
		    }
		}
		maxSubgroupID[nHist] = group;
	    }
	}
	else {
	    detectorMap = new DetectorMap[0];
	    timeField = new TimeField[0];
	    detectorAngle = new float[0];
	    detectorHeight = new float[0];
	    detectorType = new short[0];
	    flightPath = new float[0];
	}
	if (header.numOfLpsds > 0 ) {
	    try {
		lpsdMap = new LpsdMap[ this.header.lpsdMapTable.size/4 + 1 ];
		for ( i = 1; i <= this.header.lpsdMapTable.size/4; i++ ) {
		    lpsdMap[i] = new LpsdMap( runfile, i, header );
		}
		lpsdTimeField = 
		    new LpsdTimeField[ this.header.lpsdTimeFieldTable.size/4
						  + 1 ];
		for ( i = 1; i <= this.header.lpsdTimeFieldTable.size/4; 
		      i++ ) {
		    lpsdTimeField[i] = new LpsdTimeField( runfile, i, header );

		}
		lpsdIDMap = new LpsdDetIdMap( runfile, header );
		lpsdCst = new LpsdChannelStartTime ( runfile, header );

		runfile.seek(this.header.lpsdAngle.location);
		lpsdAngle = ReadVAXReal4Array(runfile, 
					      header.lpsdAngle.size/4);

		runfile.seek(this.header.lpsdFlightPath.location);
		lpsdFlightPath = 
		    ReadVAXReal4Array(runfile, 
				      header.lpsdFlightPath.size/4);

		runfile.seek(this.header.lpsdHeight.location);
		lpsdHeight = ReadVAXReal4Array(runfile, 
					       header.lpsdHeight.size/4);

		runfile.seek(this.header.lpsdType.location);
		lpsdType = ReadShortArray(runfile, header.lpsdType.size/2);

	    }
	    catch ( LpsdNotFound ex ) {
		System.out.println( "Runfile catching LpsdNotFound Exception\n"
				    + "*****" + ex );
	    }


	}
	properties = new Properties();
	runfile.seek( this.header.messageRegion.location );
	byte[] messageBytes = new byte[this.header.messageRegion.size];

	runfile.read( messageBytes );
	String messageText = new String(messageBytes);
	int lastStart = 0;
	for ( int ii = 0; ii < messageBytes.length - 1; ii++ ) {
	    if ( messageBytes[ii] == 13 && messageBytes[ii+1] == 10 ) {
		String line = new String( messageBytes, lastStart, 
					  ii - lastStart );
		int colonIndex = line.indexOf( ":" );
		String key = line.substring( 0, colonIndex ).trim();
		String value = line.substring( colonIndex + 1, 
					       line.length() ).trim();
		properties.put( key, value );
		lastStart = ii + 2;
	    }
	}
	runfile.close();

   }

    void LoadV5( RandomAccessFile runfile ) throws IOException {
	int i;
	if ( header.nDet > 0 ) {
	    runfile.seek(this.header.detectorAngle.location);
	    detectorAngle = new float[header.detectorAngle.size / 4 + 1];
	    for ( i = 1; i <= header.detectorAngle.size / 4; i++ ) {
		detectorAngle[i] = runfile.readFloat();
	    }

	    runfile.seek(this.header.flightPath.location);
	    flightPath = new float[header.flightPath.size / 4 + 1];
	    for ( i = 1; i <= header.flightPath.size / 4; i++ ) {
		flightPath[i] = runfile.readFloat();
	    }

	    runfile.seek(this.header.detectorHeight.location);
	    detectorHeight = new float[header.detectorHeight.size / 4 + 1];
	    for ( i = 1; i <= header.detectorHeight.size / 4; i++ ) {
		detectorHeight[i] = runfile.readFloat();
	    }

	    runfile.seek(this.header.detectorType.location);
	    detectorType = new short[header.detectorType.size / 2 + 1];
	    for ( i = 1; i <= header.detectorType.size / 2; i++ ) {
		detectorType[i] = runfile.readShort();
	    }
	}
	if ( header.numOfLpsds > 0 ) {













	    runfile.seek(this.header.lpsdAngle.location);
	    lpsdAngle = new float[header.lpsdAngle.size / 4 + 1];
	    for ( i = 1; i <= header.lpsdAngle.size / 4; i++ ) {
		lpsdAngle[i] = runfile.readFloat();
	    }

	    runfile.seek(this.header.lpsdFlightPath.location);
	    lpsdFlightPath = new float[header.lpsdFlightPath.size / 4 + 1];
	    for ( i = 1; i <= header.lpsdFlightPath.size / 4; i++ ) {
		lpsdFlightPath[i] = runfile.readFloat();
	    }

	    runfile.seek(this.header.lpsdHeight.location);
	    lpsdHeight = new float[header.lpsdHeight.size / 4 + 1];
	    for ( i = 1; i <= header.lpsdHeight.size / 4; i++ ) {
		lpsdHeight[i] = runfile.readFloat();
	    }

	    runfile.seek(this.header.lpsdType.location);
	    lpsdType = new short[header.lpsdType.size / 2 + 1];
	    for ( i = 1; i <= header.lpsdType.size / 2; i++ ) {
		lpsdType[i] = runfile.readShort();
	    }

















	}

    }

    /**
       @return The version Number for this Runfile.
    */
    public int VersionNumber(){
	return this.header.versionNumber;
    }

    /**
       @return The number of standard detectors for this instrument
    */
    public int NumDet(){
	return this.header.nDet;
    }

    /**
       @return The number of LPSDs for this instrument
    */
    public int NumLpsd(){
	return this.header.numOfLpsds;
    }

    /**
       @return The title stored for this run.
    */
    public String RunTitle(){
	return this.header.runTitle;
    }

    /**
       @return The user name that was stored in the run file header.
    */ 
    public String UserName(){
	return this.header.userName;
    }

    /**
       @return The run number for this file.
    */
    public int RunNumber(){
	return this.header.runNum;
    }

    /**
       @return the run number to be started at the end of this run.
    */
    public int NextRun(){
	return this.header.nextRun;
    }

    /**
       @return the date the run was first started.
    */
    public String StartDate(){
	return this.header.startDate;
    }

    /**
       @return The time the run was started.
    */
    public String StartTime(){
	return this.header.startTime;
    }

    /**
       @return The date that the run ended.
    */
    public String EndDate(){
	return this.header.endDate;
    }

    /**
       @return The time that the run ended.
    */
    public String EndTime(){
	return this.header.endTime;
    }

    /**
       @return The protection status for the file.
    */
    public char ProtectionStatus(){
	return this.header.protStatus;
    }

    /**
       @return The variable to be monitored for runtime sequencing.
    */
    public char VarToMonitor(){
	return this.header.varToMonitor;
    }

    /**
       @return The number of preset monitor counts used for runtime sequencing.
    */
    public int PresetMonitorCounts(){
	return this.header.presetMonitorCounts;
    }

    /**
       @return The number of elapsed monitor counts in the last cycle
    */
    public int ElapsedMonitorCounts(){
	return this.header.elapsedMonitorCounts;
    }

    /**
       @return The number of preset cycles used in runtime sequencing.
    */
    public short NumOfCyclesPreset(){
	return this.header.numOfCyclesPreset;
    }

    /**
       @return The number of cycles completed.
    */
    public short NumOfCyclesCompleted(){
	return this.header.numOfCyclesCompleted;
    }

    /**
       @return The Run to be started after the last cycle of this run.
    */
    public int RunAfterFinished(){
	return this.header.runAfterFinished;
    }

    /**
       @return The total number of monitor counts during acquisition.
    */
    public int TotalMonitorCounts(){
	return this.header.totalMonitorCounts;
    }

    /**
       @return The detector calibration file number used to configure this run.
    */
    public int DetectorCalibFile(){
	return this.header.detCalibFile;
    }

    /**
       @return The unit for referencing the detector location.
    */
    public char DetLocUnit(){
	return this.header.detLocUnit;
    }

    /**
       @return The Pseudo-time unit used to focus detectors.
    */
    public char PseudoTimeUnit(){
	return this.header.pseudoTimeUnit;
    }

    /**
       @return The source to sample distance
    */
    public double SourceToSample(){
	return this.header.sourceToSample;
    }

    /**
       Calculates source to sample time for the nominal energy of neutron.
       This routine is only meaningful for chopper instruments since the
       routine requires a nominal energy to be given in runfile.
       @return The source to sample distance
    */
    public double SourceToSampleTime(){
	double timeToSample = SourceToSample() / Math.sqrt(EnergyIn()/5227060.0);
	return timeToSample;
    }

    /**
       Calculates source to sample time for a given energy of neutron.
       @param energy - energy value for the calculation
       @return The source to sample distance
    */
    public double SourceToSampleTime(double energy){
	double timeToSample = SourceToSample() / Math.sqrt(energy/5227060.0);
	return this.header.sourceToSample;
    }

    /**
       @return The source to chopper distance
    */
    public double SourceToChopper(){
	return this.header.sourceToChopper;
    }

    /**
       @return The moderator calibration file number used to set up this run
    */
    public int ModeratorCalibFile(){
	return this.header.moderatorCalibFile;
    }

    /**
       @return The group to monitor for runtime sequencing
    */
    public short GroupToMonitor(){
	return this.header.groupToMonitor;
    }

    /**
       @return The channel to monitor for runtime sequencing.
    */
    public short ChannelToMonitor(){
	return this.header.channelToMonitor;
    }

    /**
       @return The number of histograms in the run.
    */
    public short NumOfHistograms(){
	return this.header.numOfHistograms;
    }

    /**
       @return The number of time fields in the time field table.
    */
    public short NumOfTimeFields(){
	return this.header.numOfTimeFields;
    }

    /**
       @return The number of control parameters in the control table.
    */
    public short NumOfControl(){
	return this.header.numOfControl;
    }

    /**
       @return The control flag word.
    */
    public short ControlFlag(){
	return this.header.controlFlag;
    }

    /**
       @return The clock shift needed.
    */
    public short ClockShift(){
	return this.header.clockShift;
    }

    /**
       @return The total number of data channels in the runfile.
    */
    public int TotalChannels(){
	return this.header.totalChannels;
    }

    /**
       @return The number of accelerator pulses for this run.
    */
    public int NumOfPulses(){
	return this.header.numOfPulses;
    }

    /**
       @return The size of the data area.
    */
    public int SizeOfDataArea(){
	return this.header.sizeOfDataArea;
    }

    /**
       @return The minimum time for processing data in the hardware.
    */
    public int HardwareTMin(){
	return this.header.hardwareTMin;
    }

    /**
       @return The maximum time for processing data in the hardware.
    */
    public int HardwareTMax(){
	return this.header.hardwareTMax;
    }

    /**
       @return The hardware time delay.
    */
    public int HardTimeDelay(){
	return this.header.hardTimeDelay;
    }

    /**
       @return The number of x channels in and area detector.
    */
    public short NumOfX(){
	return this.header.numOfX;
    }

    /**
       @return The number of y channels in and area detector.
    */
    public short NumOfY(){
	return this.header.numOfY;
    }

    /**
       @return The number of wavelength channels for an area detector.
    */
    public short NumOfWavelengths(){
	return this.header.numOfWavelengths;
    }

    /**
       @return The minimum wavelength for an area detector.
    */
    public int MinWavelength(){
	return this.header.minWavelength;
    }

    /**
       @return The maximum wavelength for an area detector.
    */
    public int MaxWavelength(){
	return this.header.maxWavelength;
    }

    /**
       @return The dta.
    */
    public double Dta(){
	return this.header.dta;
    }

    /**
       @return The dtd.
    */
    public double Dtd(){
	return this.header.dtd;
    }

    /**
       @return The Omega.
    */
    public double Omega(){
	return this.header.omega;
    }

    /**
       @return The chi angle for SCD.
    */
    public double Chi(){
	return this.header.chi;
    }

    /**
       @return The phi angle for SCD.
    */
    public double Phi(){
	return this.header.phi;
    }

    /**
       @return The distance from detector center to the left of an area 
       detector.
    */
    public double XLeft(){
	return this.header.xLeft;
    }

    /**
       @return The distance from detector center to the right of an area 
       detector.
    */
    public double XRight(){
	return this.header.xRight;
    }

    /**
       @return The distance from detector center to the bottom of an area 
       detector.
    */
    public double YLower(){
	return this.header.yLower;
    }

    /**
       @return The distance from detector center to the top of an area 
       detector.
    */
    public double YUpper(){
	return this.header.yUpper;
    }

    /**
       @return The horizontal displacement of an area detector from beam 
       center.
    */
    public double XDisplacement(){
	return this.header.xDisplacement;
    }

    /**
       @return The vertical displacement of an area detector from beam center.
    */
    public double YDisplacement(){
	return this.header.yDisplacement;
    }

    /**
       @return The time channel width for area detectors.
    */
    public short AreaChannelWidth(){
	return this.header.areaChannelWidth;
    }

    /**
       @return The time channel doubling width for area detetors.
    */
    public short AreaDoubleInterval(){
	return this.header.areaDoubleInterval;
    }

    /**
       @return The clock period for area detector data acquisition system
    */
    public double AreaClock(){
	return this.header.clockPeriod;
    }

    /**
       @return The incident energy for a chopper spectrometer.
    */
    public double EnergyIn(){
	return this.header.energyIn;
    }

    /**
       @return The scatered energy for inverse geometry machine.
    */
    public double EnergyOut(){
	return this.header.energyOut;
    }

    /**
       @return The number of sequential histograms.
    */
    public short NumOfSeqHist(){
	return this.header.numOfSeqHist;
    }

    /**
       @return The proton current.
    */
    public double ProtonCurrent(){
	return this.header.protonCurrent;
    }

    /**
       @return The area binning type
    */
    public short AreaBinning(){
	return this.header.areaBinning;
    }

    /**
       @return The number of times the histogramming proccessor locked out.  
       This is and indication of possible data loss.
    */
    public short NumOfLockouts(){
	return this.header.numOfLockouts;
    }

    /**
       @return The experiment number for a multirun experiment.
    */
    public int ExpNum(){
	return this.header.expNum;
    }

    /**
       @return The first run in a multirun experiment
    */
    public int FirstRun(){
	return this.header.lastRun;
    }

    /**
       @return The default run number.  Time fields and grouping info for 
       this run were copied from the default run.
    */
    public int DefaultRun(){
	return this.header.defaultRun;
    }

    /**
       @return The sample position for a multiple sample well instrument.
    */
    public short SamplePosition(){
	return this.header.samplePos;
    }

    /**
       @return The number of blocks in the runfile header.
    */
    public int NumOfHeadBlocks(){
	return this.header.numOfHeadBlocks;
    }

    /**
       @return The clock period for standard detectors.
    */
    public double StandardClock(){
	return this.header.standardClock;
    }

    /**
       @return The clock period for LPSD detectors.
    */
    public double LpsdClock(){
	return this.header.lpsdClock;
    }

    /**
       This method retrieves the effective scattering angle for a given 
       detector.  For a detector that is not time foused this routine 
       retrieves the actual angle.  For a time focuesed detector this 
       retrieves the average angle of the focused group of detectors.
       @param detID The detector ID.
       @return The scattering angle.
    */
    public double DetectorAngle(int detID, int hist ){
	double dAngle;
	double angle_sum= 0.0, angleSign;
	int nDetsThisType = 0;
	int index = (hist - 1) * header.nDet + detID;
	int tfType = detectorMap[index].tfType;

	if (tfType == 0) return detectorAngle[detID];

	if ((timeField[tfType].timeFocusBit > 0) && 
	    (header.pseudoTimeUnit == 'D')) { 
	    for ( int nid = 1; nid <= header.nDet; nid++ ) {
		int nindex = (hist - 1) * header.nDet + nid;
		int tempType = detectorMap[nindex].tfType;

		if ( tempType == tfType )
		    {
			angle_sum += Math.abs(detectorAngle[nid]);
			nDetsThisType++;
		    }
	    }
	    angleSign = detectorAngle[detID]/
		Math.abs(detectorAngle[detID]);
	    dAngle = angle_sum / nDetsThisType * angleSign;
	    
	}
	else {
	    dAngle = this.detectorAngle[detID];
	}
	return dAngle;
    }

    /**
       This method retrieves the actual scattering angle for a given detector. 
       @param detID The detector ID.
       @return The scattering angle.
    */
    public double RawDetectorAngle(int detID){
	return this.detectorAngle[detID];
    }

    /**
       This method retrieves the effective flight path length for a given 
       detector.  For a detector that is not time foused this routine 
       retrieves the actual length.  For a time focuesed detector this 
       retrieves the focused flight path length.
       @param detID The detector ID.
       @return The focused flight path length if pseudoUnit = I.
       The raw flight path length otherwise.
    */
    public double FlightPath(int detID, int hist){
	double fp;
	int index = (hist - 1) * header.nDet + detID;
	int tfType = detectorMap[index].tfType;

	if (tfType == 0) return flightPath[detID];

	if ((timeField[tfType].timeFocusBit > 0) && 
	    (header.pseudoTimeUnit == 'I')) { 
	    if (flightPath[detID] > 3.0 ) {
		fp = 4.0;
	    }
	    else {
		fp = 2.5;
	    }
	}
	else {
	    fp = flightPath[detID];
	}
 
	return fp;
    }

    /**
       This method retrieves the actual flight path length for a given 
       detector.  
       @param detID The detector ID.
       @return The flight path length.
    */
    public double RawFlightPath(int detID){
	return this.flightPath[detID];
    }

    /**
       This method retrieves the height of the detector above the scattering 
       plane.  
       @param detID The detector ID.
       @return The height of the detector above the scattering plane.
    */
    public double DetectorHeight(int detID){
	return this.detectorHeight[detID];
    }

    /**
       This method retrieves the detector type.  
       @param detID The detector ID.
       @return The detector type.
    */
    public short DetectorType(int detID){
	return this.detectorType[detID];
    }

    /**
       Retrieves the spectrum of a 1D detector.  This method opens and closes 
       the file on each call.
       @param subgroup Subgroup ID to be retrieved.
       @return The retrieved spectrum.
    */
    public float[] Get1DSpectrum(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return null;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return Get1DSpectrum(id, hist);
    }

    /**
       Retrieves the spectrum of a 1D detector.  This method opens and closes 
       the file on each call.
       @param detID Detector ID to be retrieved.
       @param hist Histogram number for data to retrieved.
       @return The retrieved spectrum.
    */
    public float[] Get1DSpectrum(int detID, int hist) throws IOException{
	int numOfTimeChannels;
	int i;
	float[] data;
	byte[] bdata;
	int tfType;
	int index, offset;
	int wordLength;

	if ( leaveOpen == false){
	    System.out.println("GetSpectrum1D opening file");
	    runfile = new RandomAccessFile(runfileName, "r");
	}
	index = detID + (hist-1) * this.header.nDet;
	tfType = this.detectorMap[index].tfType;
	numOfTimeChannels = this.timeField[tfType].NumOfChannels();
	data = new float[numOfTimeChannels];
	if (header.numOfWavelengths > 0) {
	    offset = this.detectorMap[index].address 
		+ this.header.totalChannels * 2 
		+ this.header.histStartAddress + 4;
	}
	else {
	    offset = this.detectorMap[index].address + 
		this.header.histStartAddress + 4;
	}

	runfile.seek(offset);
	if (numOfTimeChannels !=0){
	    if (this.header.versionNumber < 4){
		wordLength = 2;
	    }
	    else{
		wordLength = 4;
	    }
	    bdata = new byte[numOfTimeChannels * wordLength];
	    int nbytes = runfile.read( bdata, 0, 
				       numOfTimeChannels * wordLength);
	    for (i = 0; i < numOfTimeChannels; i++){
		for (int j = 0; j < wordLength; j++) {
		    int byteIndex = i * wordLength + j;
		    if ( bdata[byteIndex] < 0 ) {
			data[i] += (bdata[byteIndex] + 256) * 
			    Math.pow(256.0, j);
		    }
		    else {
			data[i] += bdata[byteIndex] * Math.pow(256.0, j);
		    }
		}
	    }
	}
	if (!leaveOpen ){
	    runfile.close();
	}
	return data; 
    }

    /**
       Retrieves the sum of counts in a 1D detector.  This method opens and 
       closes the file on each call.
       @param subgroup Subgroup ID to be retrieved.
       @return The retrieved spectrum.
    */
    public float Get1DSum(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return -1;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return Get1DSum(id, hist);
    }

    /**
       Retrieves the sum of counts in a 1D detector.  This method opens and 
       closes the file on each call.
       @param detID Detector ID to be retrieved.
       @param hist Histogram number for data to retrieved.
       @return The sum.
    */
    public float Get1DSum(int detID, int hist) throws IOException{
	int numOfTimeChannels;
	int i;
	float data;
	byte[] bdata;
	int tfType;
	int index, offset;
	int wordLength;

	if ( leaveOpen == false){
	    runfile = new RandomAccessFile(runfileName, "r");
	}
	index = detID + (hist-1) * this.header.nDet;
	tfType =  this.detectorMap[index].tfType;
	numOfTimeChannels = this.timeField[tfType].NumOfChannels();
	data = -1;
	if (header.numOfWavelengths > 0) {
	    offset = this.detectorMap[index].address 
		+ this.header.totalChannels * 2 
		+ this.header.histStartAddress;
	}
	else {
	    offset = this.detectorMap[index].address + 
		this.header.histStartAddress;
	}

	runfile.seek(offset);
	if (numOfTimeChannels !=0){
	    data = 0;
	    wordLength = 4;
	   
	    bdata = new byte[wordLength];
	    int nbytes = runfile.read( bdata, 0, 
				       wordLength);
	    for (int j = 0; j < wordLength; j++) {
		int byteIndex = j;
		if ( bdata[byteIndex] < 0 ) {
		    data  += (bdata[byteIndex] + 256) * 
			Math.pow(256.0, j);
		}
		else {
		    data += bdata[byteIndex] * Math.pow(256.0, j);
		}
	    }
	}
	if (!leaveOpen ){
	    runfile.close();
	}
	return data; 
    }

    /**
       Check to see if a detector is binned in the specified histogram.
       @param id - Detector to be checked.
       @param hist - Histogram to be checked.
       @return true if binned, false if not binned.
    */

    public boolean IsBinned( int id, int hist ) {
	if ( id > header.nDet || hist > header.numOfHistograms ) return false;

	int index = (hist - 1) * header.nDet + id;
	if ( detectorMap[index].address > 0 ) return true;
	if ( detectorMap[index].address == 0 && detectorMap[index].tfType > 0 )
	    return true;

	return false;
    }

    /**
       Get number of data channels for a given detector.
       @param subgroup Subgroup ID to be retrieved.
       @return Number of channels binned.
    */
    public int NumChannelsBinned(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return -1;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return NumChannelsBinned(id, hist);
    }

    /**
       Get number of data channels for a given detector.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Number of channels binned.
    */
    public int NumChannelsBinned( int id, int hist )  {
	int index = ( hist - 1 ) * header.nDet + id;
	int tfType = detectorMap[index].tfType;
	int nch = (int)(timeField[tfType].NumOfChannels());
	return nch;
    }

    /**
       Minimum Binned time in time field table.
       @param subgroup Subgroup ID to be retrieved.
       @return Raw minumum time in the time field table.
    */
    public double MinBinned(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return -9999;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return MinBinned(id, hist);
    }

    /**
       Minimum Binned time in time field table.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Raw minumum time in the time field table.
    */
    public double MinBinned( int id, int hist ) {
	int index = ( hist - 1 ) * header.nDet + id;
	int tfType = detectorMap[index].tfType;
	return timeField[tfType].tMin;
    }
   
    /**
       Maximum Binned time in time field table.
       @param subgroup Subgroup ID to be retrieved.
       @return Raw maxumum time in the time field table.
    */
    public double MaxBinned(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return -9999.;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return MaxBinned(id, hist);
    }

    /**
       Maximum Binned time in time field table.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Raw maxumum time in the time field table.
    */

    public double MaxBinned( int id, int hist ) {
	int index = ( hist - 1 ) * header.nDet + id;
	int tfType = detectorMap[index].tfType;
	return timeField[tfType].tMax;
    }
   
    /**
       Trigger to leave the file open for subsequent reads.  This is not 
       required but will speed up file access if a large number of spectra 
       are retrieved.  Care must be used when using this routine.  It is not 
       used to initialize the object or retrieving header information.  It 
       leaves the file open to speed up getting spectra data.  After getting 
       data or between long pauses Close Method should be used.
    */
    public void LeaveOpen() {
	if (leaveOpen == true ) return; 
	try {
	    runfile = new RandomAccessFile ( runfileName, "r");
	}
	catch ( IOException e ) {
	    System.out.println("Problem Opening File: " + runfileName );
	}
	leaveOpen = true;
    }
      
    /** Closes files opened with LeaveOpen.
     */
    public void Close() {
	if (leaveOpen == false ) return; 
	try {
	    runfile.close();
	}
	catch ( IOException e ) {
	    System.out.println("Problem Closing File: " + runfileName );
	}
	leaveOpen = false;
    }
      
    /**
       Retrieves the time channel boundaries for a given spectrum.  Note that 
       since this is histogram data, there is one more boundary value than 
       there are elements in a spectrum.
       @param subgroup Subgroup ID to be retrieved.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] TimeChannelBoundaries(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return null;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return TimeChannelBoundaries(id, hist);
    }

    /**
       Retrieves the time channel boundaries for a given spectrum.  Note that 
       since this is histogram data, there is one more boundary value than 
       there are elements in a spectrum.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] TimeChannelBoundaries(int id, int hist){
	float us_correction;
	if ( id > header.nDet || hist > header.numOfHistograms ) return null;
	int index = header.nDet * (hist - 1) + id;

	if (detectorMap[index].tfType == 0 ) return null;
   
	int tfType = detectorMap[index].tfType;
	int numberOfChannels = timeField[tfType].NumOfChannels();
	float[] channel = new float[numberOfChannels + 1];

	float min = (float)timeField[tfType].tMin;
	float max = (float)timeField[tfType].tMax;
	float step = (float)timeField[tfType].tStep;

	float timeToSample = (float)(header.sourceToSample / 
				     Math.sqrt( header.energyIn/5227060.0F));

	switch (header.pseudoTimeUnit ) {

	case ('I'): {
	    if ( timeField[tfType].timeFocusBit == 0 ) {  //Det not focused
		us_correction = - (float)(header.hardTimeDelay * 
					  header.standardClock) + timeToSample;
	    }
	    else {						//Det focused
		us_correction = timeToSample;
	    }
	    min = min + us_correction;
	    max = max + us_correction;
	}
	default: {
	    min = min;
	    max = max;
	}
	}
	for (int chan = 0; chan <= numberOfChannels; chan++) {
	    channel[chan] = (float)( min + chan * step );
	}
	return channel;
    }

    /**
       Retrieves the energy channel boundaries for a given spectrum.  Note 
       that since this is histogram data, there is one more boundary value 
       than there are elements in a spectrum.
       @param subgroup Subgroup ID to be retrieved.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] EnergyChannelBoundaries(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return null;
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return EnergyChannelBoundaries(id, hist);
    }

    /**
       Retrieves the energy channel boundaries for a given spectrum.  Note 
       that since this is histogram data, there is one more boundary value 
       than there are elements in a spectrum.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Array of boundary values with 1 + number of channels values.
    */
    public float[] EnergyChannelBoundaries( int detID, int hist ) {
	float[] timeBounds;

	timeBounds = TimeChannelBoundaries( detID, hist );
	float[] energyChannels = new float[timeBounds.length];
	float timeToSample = (float)(header.sourceToSample / 
				     Math.sqrt( header.energyIn/5227060.0F));
   
	for (int i = 0; i < timeBounds.length; i++ ) {
	    energyChannels[i] = (float)(header.energyIn - 5227060.0F * 
					Math.pow(FlightPath(detID, hist) 
				     / (timeBounds[i] - timeToSample), 2.0)); 
	}
	return energyChannels;
    }

    /** 
	Retrieves the lower level discriminator for a detector.
	@param id - id of the detector
	@return Value of the lower level discriminator.
    */
    public short LowerLevelDisc(int id) {
	if (id < 1 || id > header.nDet ) return -1;
	if (header.versionNumber < 4 ) return (short)0;
	return discriminator[id].lowerLevel;
    }

    /** 
	Retrieves the upper level discriminator for a detector.
	@param id - id of the detector
	@return Value of the upper level discriminator.
    */
    public short UpperLevelDisc(int id) {
	if (id < 1 || id > header.nDet ) return -1;
	if (header.versionNumber < 4 ) return (short)0;
	return discriminator[id].upperLevel;
    }

    /** Retrieve a list that maps detectors to Subgroup IDs.  Note that this
	array starts at index 1 since it references information by detector 
	ID.  The list for all histograms is given here in a one dimentional
	array.  The index for a detector in a paricular detector histogram can
	be given by:
	index = (hist -1)*NumDet() + id;
	@return Array of mapped values
    */
    public int[] subgroupIDList() {
	return subgroupIDList;
    }

    /** Determines if detectors have been binned within a particular histogram.
	This is a rare occurance but anything is possible with users.
    */
    public boolean IsHistogramGrouped(int hist){
	if (minSubgroupID[hist] > maxSubgroupID[hist]) {
	   return false;
	   }
	else {
	   return true;
	   }
    }

    /** Retrieves the minimum subgroup ID in a histogram.  The subgroup IDs 
	are continuous accross the histogram boundaries.  i.e. The fist 
	subgroup ID in histogram 2 = 1 + the subgroup ID for histogram 1.
	@param hist - Requested histogram number.
	@return The ID of the maximum Subgroup ID for the histogram.
    */
    public int MinSubgroupID( int hist ) {
	return minSubgroupID[hist];
    }

    /** Retrieves the maximum subgroup ID in a histogram.  The subgroup IDs 
	are continuous accross the histogram boundaries.  i.e. The fist 
	subgroup ID in histogram 2 = 1 + the subgroup ID for histogram 1.
	@param hist - Requested histogram number.
	@return The ID of the maximum Subgroup ID for the histogram.
    */
    public int MaxSubgroupID( int hist ) {
	return maxSubgroupID[hist];
    }

    /**
       Retrieves a list of IDs mapped to a subgroup.  Note that this is a 0
       indexed array since there is no reference to a paticular group or ID for
       this array.
       @param int sg - subgroup number
       @return A list of detector IDs
    */
    public int[] IdsInSubgroup( int sg ) {
	return subgroupMap[sg];
    }

   /**
       Checks to see if an ID is a beam monitor.
       @param id - detector ID.
       @return boolean true if ID is a beam monitor.
   */
   public boolean IsIDBeamMonitor( int id ) {
	if (detectorAngle[id] == 0.0 || detectorAngle[id] == 180.0 || 
	detectorAngle[id] == -180.0 ) 
	   return true;
	else
	   return false;
	}
   
   /**
       Checks to see if a subgroup is a beam monitor.
       @param sg - detector subgroup.
       @return boolean true if subgroup is a beam monitor.
   */
   public boolean IsSubgroupBeamMonitor( int sg ) {
	for ( int ii = 0; ii < subgroupMap[sg].length; ii++ ) {
	    int id = subgroupMap[sg][ii];
	    if (detectorAngle[id] == 0 || detectorAngle[id] == 180 ||
		detectorAngle[id] == -180 ) 
	       return true;
            }
	    return false;
	}

    /**
       Compares this runfile to another Runfile object to see if they have 
       the same structure.  i.e. detector map and time field table are the 
       same.
       @ param runFile - runfile to compare
       @ return true if the same structure and false if the structure is 
       different.
    */
    public boolean isEqual( Runfile runFile ) {
	boolean mapEqual, tfTableEqual, answer;
	
	mapEqual = false;
	tfTableEqual = false;
	answer = false;
	
	if ( this.detectorMap.length == runFile.detectorMap.length ) {
	    int dMapLength = this.detectorMap.length;
	    for ( int ii = 1; ii < dMapLength; ii++ ) {
		if ( this.detectorMap[ii].isEqual(runFile.detectorMap[ii]) )
		    mapEqual = true;
	    }
	}
	if ( this.timeField.length == runFile.timeField.length ) {
	    int tfLength = this.timeField.length;
	    for ( int ii = 1; ii < tfLength; ii++ ) {
		if ( this.timeField[ii].isEqual(runFile.timeField[ii]) )
		    tfTableEqual = true;
	    }
	}

    answer = mapEqual && tfTableEqual;
    return answer;

    }
    /**
       Compares this runfile to a list of Runfile objects to see if they have 
       the same structure.  i.e. detector map and time field table are the 
       same.
       @ param runFile[] - list of runfiles to compare
       @ return true if the same structure and false if the structure is 
       different.
    */
    public boolean isEqual( Runfile[] runFile ) {

	boolean answer;
	
	answer = true;
	if ( runFile.length <= 0 ) return false;

	for ( int ii = 0; ii < runFile.length; ii++ ) {
	    if ( !this.isEqual(runFile[ii]) ) answer = false;
	}
	
	return answer;
    }


    /**
       Returns a list of property Names (keys) from the runfile
    */

    public Enumeration propertyNames() {
	return properties.propertyNames();
    }

    /**
       Returns the value of a property given a property name (key).
     */
    public String getProperty( String key ) {
	return properties.getProperty( key );
    }

    /**
       This method retrieves the scattering angle for a given lpsd. 
       @param detID The detector ID.
       @return The scattering angle.
    */
    public double LpsdAngle(int detID) throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	return this.lpsdAngle[detID];
    }

    /**
       This method retrieves the scattering angle for a given lpsd. 
       @param bank The detector bank.
       @param det  The detector number in the bank
       @param element The element number in the detector
       @return The scattering angle.
    */
    public double LpsdAngle(int bank, int det, int element) 
	throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	int lpsdId = LpsdMapToId( bank, det, element, 1 );
	return this.lpsdAngle[lpsdId];
    }

    /**
       This method retrieves the flight path length for a given lpsd. 
       @param detID The detector ID.
       @return The flight path length.
    */
    public double LpsdFlightPath(int detID) throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	return this.lpsdFlightPath[detID];
    }

    /**
       This method retrieves the flight path length for a given lpsd. 
       @param bank The detector bank.
       @param det  The detector number in the bank
       @param element The element number in the detector
       @return The flight path length.
    */
    public double LpsdFlightPath(int bank, int det, int element) 
	throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	int lpsdId = LpsdMapToId( bank, det, element, 1 );
	return this.lpsdFlightPath[lpsdId];
    }

    /**
       This method retrieves the height above the scattering plane for a 
       given lpsd. 
       @param detID The detector ID.
       @return The height of the detecor above the scattering plane.
    */
    public double LpsdHeight(int detID) throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	return this.lpsdHeight[detID];
    }

    /**
       This method retrieves the height above the scattering plane for a 
       given lpsd. 
       @param bank The detector bank.
       @param det  The detector number in the bank
       @param element The element number in the detector
       @return The height of the detecor above the scattering plane.
    */
    public double LpsdHeight( int bank, int det, int element ) 
	throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	int lpsdId = LpsdMapToId( bank, det, element, 1 );
	return this.lpsdHeight[lpsdId];
    }

    /**
       This method retrieves the detector type of the LPSD.  
       @param detID The detector ID.
       @return The lpsd type.
    */
    public short LpsdType(int detID) throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	return this.lpsdType[detID];
    }

    /**
       This method retrieves the detector type of the LPSD.  
       @param bank The detector bank.
       @param det  The detector number in the bank
       @param element The element number in the detector
        @return The lpsd type.
    */
    public short LpsdType(int bank, int det, int element) throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	int lpsdId = LpsdMapToId( bank, det, element, 1 );
	return this.lpsdType[lpsdId];
    }

    /**
       @return The Number of LPSD banks
    */
    public int NumOfLpsdBanks() throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	return lpsdIDMap.NumOfBanks();
    }

    /**
       @return The Number of LPSD banks
    */
    public int NumOfLpsdsInBank( int bankNum ) throws LpsdNotFound {
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	return ( lpsdIDMap.DetsInBank( bankNum )).length;
    }

    /**
       Retrieves the spectrum of a Linear Position Sensitive Detector.  This 
       method opens and closes the file on each call unless LeaveOpen() has 
       been called to leave the file open.
       @param LpsdId
       @param hist
       @return the retrieved spectrum
    */
    public float[] GetLpsdSpectrum( int lpsdId, int hist )     
	throws LpsdNotFound, IOException {
	float[] data;
	byte[] bdata;
	int index, offset;
	int tfType;
	int numOfTimeChannels;
	int wordLength;

	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	if ( lpsdId <= 0 ) return new float[0];
	if ( !leaveOpen ) {
	    System.out.println( "GetLpsdSpectrum opening file" );
	    try {
		runfile = new RandomAccessFile( runfileName, "r" );
	    }
	    catch ( IOException ex ) {
		System.out.println( "Trouble Opening File" );
		throw ex;
	    }
	}

	int maxElements = ( header.numOfLpsds - 1 )/80 * 8192 + 
	    (header.numOfLpsds - ((( header.numOfLpsds - 1 )/80 ) * 80 )) * 64;
	index = lpsdId + ( hist -1 ) * maxElements;
	tfType = this.lpsdMap[index].tfType;
	if ( tfType == 0 ) return new float[0];
	numOfTimeChannels = this.lpsdTimeField[tfType].NumOfChannels() - 2;
	data = new float[numOfTimeChannels];
	if ( header.numOfWavelengths > 0 ) {
	    offset = this.lpsdMap[index].address 
		+ this.header.totalChannels * 2
		+ this.header.histStartAddress + 4;
	}
	else {
	    offset = this.lpsdMap[index].address + 
		this.header.histStartAddress + 4;
	}

	runfile.seek( offset );
	if ( numOfTimeChannels != 0 ) {
	    if ( this.header.versionNumber < 4 ) {
		wordLength = 2;
	    }
	    else {
		wordLength = 4;
	    }
	    bdata = new byte[ (numOfTimeChannels - 2) * wordLength ];
	    int nbytes = runfile.read( bdata, 0, 
				       (numOfTimeChannels - 2) * wordLength );
	    for ( int ii = 0; ii < (numOfTimeChannels - 2); ii++ ) {
		for ( int jj = 0; jj < wordLength; jj++ ) {
		    int byteIndex = ii * wordLength + jj;
		    if ( bdata[ byteIndex ] < 0 ) {
			data[ii] += ( bdata[byteIndex] + 256 ) *
			    Math.pow( 256.0, jj );
		    }
		    else {
			data[ii] +=  bdata[byteIndex] * Math.pow(256.0, jj );
		    }
		}
	    }
	}

	if ( !leaveOpen ) {
	    runfile.close();
	}
	return data;
    }

    public float[] GetLpsdSpectrum( int bank, int det, int element, 
				    int hist ) throws LpsdNotFound, 
				    IOException  {

	int lpsdId = LpsdMapToId( bank, det, element, hist );
	return GetLpsdSpectrum( lpsdId, hist );

    }

    int LpsdMapToId( int bank, int det, int element, int hist ) {
	if ( lpsdIDMap.map[bank][det - 1].minID <= 0 ) return -1;
	int maxElements = ( header.numOfLpsds - 1 )/80 * 8192 + 
	    (header.numOfLpsds - ((( header.numOfLpsds - 1 )/80 ) * 80 )) * 64;
	int lpsdId =  lpsdIDMap.map[bank][det - 1].minID + ( element - 1 )
	    + ( hist - 1 ) * maxElements;
	return lpsdId;
    }

    public float[] LpsdTimeChannelBoundaries( int lpsdId, int hist ) 
	throws LpsdNotFound {
	int index;
	int tfType;
	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound();
	}
	if ( lpsdId <= 0 ) return new float[0];
	int maxElements = ( header.numOfLpsds - 1 )/80 * 8192 + 
	    (header.numOfLpsds - ((( header.numOfLpsds - 1 )/80 ) * 80 )) * 64;
	index = lpsdId + ( hist -1 ) * maxElements;
	tfType = this.lpsdMap[index].tfType;
	if ( tfType == 0 ) return new float[0];
	float[] times = new float[ (lpsdCst.table[ tfType - 1 ]).length];
	for (int ii = 0; ii < times.length; ii++ ) {
	    times[ ii ] = lpsdCst.table[ tfType - 1 ][ii];
	}
	return times;
    }

    public float[] LpsdTimeChannelBoundaries( int bank, int det, int element, 
					      int hist ) throws LpsdNotFound {
	int lpsdId = LpsdMapToId( bank, det, element, hist );
	return LpsdTimeChannelBoundaries( lpsdId, hist );
    }
}
