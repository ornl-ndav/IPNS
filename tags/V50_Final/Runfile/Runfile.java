
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
@version 5.1
*/
/*
 *
 * $Log$
 * Revision 5.23  2001/06/27 20:45:23  hammonds
 * Added code for adding segment information to expand the use of this package for use with area detectors.
 * Added a number of new detector types including area detectors used on SAD, SAND and SCD.
 *
 * Revision 5.22  2001/04/09 18:45:20  hammonds
 * Added functions for dataSource and minID.
 *
 * Revision 5.21  2001/04/03 20:46:09  hammonds
 * added detector dataSource and minID tables.
 *
 * Revision 5.20  2001/03/15 17:24:56  hammonds
 * Added stuff to handle new dcalib info ( det. size, rotations, crate info...).
 *
 * Revision 5.19  2001/02/27 21:07:28  hammonds
 * add fujnction to return the instrument type.
 *
 * Revision 5.18  2000/10/04 21:58:10  hammonds
 * Added routines IsPulseHeight to determine if a detector or subgroup has been binned pulse height data.
 *
 * Revision 5.17  2000/08/17 14:25:29  hammonds
 * Added routine AreaTimeSlice to read time slice data from an area detector.
 *
 * Revision 5.16  2000/07/11 03:32:44  hammonds
 * Changed DetectorAngle() to return average angle for time focused spectrometer runs.  Also added RawDetectorHeight() method to balance out the calls to locate a detector.
 *
 * Revision 5.15  2000/06/01 16:23:31  hammonds
 * Corrected Get1DSum for new runfiles (byte order was wrong).  Changed solidAngle() to SolidAngle().
 *
 * Revision 5.12  2000/03/11 03:06:19  hammonds
 * Fixed small problem with TimeScales for V<4 and with SourceToSampleTime( float)
 *
 * Revision 5.11  2000/03/10 04:10:37  hammonds
 * Changed code to use energy conversion factor from the new PhysicalConstants class.  Small changes
 *
 * Revision 5.9  2000/02/26 18:20:36  hammonds
 * Changed Get1DSpectum to not change byte order for Version >= 5
 *
 * Revision 5.8  2000/02/25 04:07:42  hammonds
 * Made changes to detector subgroups
 *
 * Revision 5.7  2000/02/23 23:33:27  hammonds
 * Fixed Some problems with subgroup maps in new files.
 *
 * Revision 5.6  2000/02/23 04:52:50  hammonds
 * Made changes to fix problems with setting up a run from a default run.  Subgroup Map was not being read and there was a problem with disc levels.
 *
 * Revision 5.5  2000/02/22 04:10:40  hammonds
 * Have deleted obsolete lpsd code.
 *
 * Revision 5.4  2000/02/18 03:41:05  hammonds
 * Removed lpsd member variables whose classes have been removed from the package.
 *
 * Revision 5.3  2000/02/17 21:20:41  hammonds
 * Minor bug fixes.
 *
 * Revision 5.2  2000/02/16 01:43:14  hammonds
 * Changed code to handle glad LPSDs.  Changes will soon result in a common interface for all detectors.  Most of the Lpsd<...> classes will be removed in favor of a unified interface references have been commented out for now.  References to Header member variable numOfLpsd have been changed to nDet or numOfElements as appropriate.  Lpsds will be treated as a multi-element detector.
 *
 * Revision 5.1  2000/02/11 23:16:45  hammonds
 * Added code to read/write discriminator levels in the new files.  Changed type for disc level methods to int.  Added many parameters to specify detector posistion in the runfile.
 *
 * Revision 5.0  2000/01/10 22:39:48  hammonds
 * Made update to allow this package to be used with the new newrun package.
 * These updates were generally made to allow for better operation when creating
 * a run via a default run do that parameters are fed out of Runfile properly and
 * into RunfileBuilder properly.
 * HeadText was modified to allow the displayed runfile to change without creating
 * a new instance of the Text object.
 *
 *
 */

public class Runfile implements Cloneable {
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
    /** Segment Map Table */                      Segment[][] segmentMap;
    /** Version Number */                       protected int versNum;
    /** run properties stored in message area */
                                               protected Properties properties;
    /** Array of lpsd angles */		        protected float[] lpsdAngle;
    /** Array of lpsd flight path lengths */	protected float[] 
	                                                       lpsdFlightPath;
    /** Array of detector heights */		protected float[] lpsdHeight;
    /** Array of detector types */		protected short[] lpsdType;
    /** Array of detector Segments */           protected Segment[] segments;
    float[] detectorLength = new float[0];
    float[] detectorWidth = new float[0];
    float[] detectorDepth = new float[0];
    short[] detCoordSys = new short[0];
    float[] detectorRot1 = new float[0];
    float[] detectorRot2 = new float[0];
    float[] detectorEfficiency = new float[0];
    int[] psdOrder = new int[0];
    int[] numSegs1 = new int[0];
    int[] numSegs2 = new int[0];
    int[] crateNum = new int[0];
    int[] slotNum = new int[0];
    int[] inputNum = new int[0];
    int[] dataSource = new int[0];
    int[] minID = new int[0];
    //-----------------------------------------------------------------
    public static final float[] 
	LENGTH = {0.0F, 7.62F, 45.72F, 22.86F, 11.43F, 91.44F, 38.1F, 38.1F,
		  12.7F, 3.81F, 12.7F, 0.30F, 0.20F, 0.40F, 0.40F};
    public static final float[] 
	WIDTH = {0.0F, 7.62F, 2.377F, 2.377F, 2.377F, 2.377F, 1.074F, 1.074F, 
		 0.493F, 3.81F, 3.81F, 0.30F, 0.20F, 0.40F, 0.40F };
    public static final float[] 
	DEPTH = {0.0F, 3.81F, 2.377F, 2.377F, 2.377F, 2.377F, 1.074F, 1.074F,
		 0.493F, 2.54F, 2,54F, 2.54F, 2.54F, 2.54F, 2.54F};
    public static final float[] 
	EFFICIENCY = {0.0F, 0.001F, 1.00F, 1.00F, 1.00F, 1.00F, 1.00F, 1.00F,
		 1.00F, 0.001F, 0.001F, 1.00F, 1.00F, 1.00F, 1.00F};
    public static final int[]
	PSD_DIMENSION = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
    public static final int[]
	NUM_OF_SEGS_1 = { 0, 1, 1, 1, 1, 8, 1, 32, 1, 1, 1, 85, 64, 128, 128 };
    public static final int[]
	NUM_OF_SEGS_2 = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 87, 64, 128, 128 };
    public static final int[] SEGMENT_SELECT = {
	1, 2, 4, 8, 16, 32, 64, 128, 256 };
    public static final String[] TYPE_DESCRIPTION ={"Not a detector",
						    "3\" pancake monitor",
						    "1\" x 18\"",
		 				    "1\" x 9\"",
						    "1\" x 4.5\"",
						    "1\" x 36\" LPSD",
						    "0.5\" x 15\"",
						    "0.5\" x 15\" LPSD",
						    "0.25\" x 5\"",
						    "1.5\" pancake monitor",
						    "OrdellaBeam Monitor",
						    "SCD Anger Camera",
						    "Ordella 2210 SAD 20cm",
						    "Ordella 2400 SbAND 40cm",
						    "Ordella 2410 SAND 40cm"
    
    };
    static double MEV_FROM_VEL = 
	PhysicalConstants.meV_FROM_m_PER_microsec_CONST;

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

    /** Utility method for reading an integer table from a Version 5 Runfile
     */
    private int[] readV5IntTable( ) {
	int[] data = new int[1];

	return data;

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
	    System.out.println("id, theta, l, h, Type: " 
			       + i + "  " 
			       + (float)runFile.detectorAngle[i]
			       + " " + (float)runFile.flightPath[i] + " "
			       + (float)runFile.detectorHeight[i] + " "
			       + runFile.detectorType[i] + " " 
			       + runFile.IsPulseHeight(i,1));
	}
		System.out.println(  runFile.MinSubgroupID(1) + "  " +
				     runFile.MaxSubgroupID(runFile.NumOfHistograms() ));
	for (i = runFile.MinSubgroupID(1); i <= runFile.MaxSubgroupID(runFile.NumOfHistograms())
		 ; i++){
	    System.out.print( i + " - ");
	    int[] ids = runFile.IdsInSubgroup(i);
	    for ( int k = 0; k < ids.length; k++){
		System.out.print(" " + ids[k] );
	    }
	    System.out.println(runFile.IsSubgroupBeamMonitor(i));
	}
	}
	System.out.println( runFile.NumElements() );
	float data[];
	data = runFile.Get1DSpectrum(1, 1);
	float[]  energies = runFile.TimeChannelBoundaries( 3, 1);

	float[] time = runFile.TimeChannelBoundaries( 1, 1 );
	System.out.println( "Mon 1: " + time[0] + ", " + time[time.length-1]);
	//	time = runFile.TimeChannelBoundaries( 2, 1 );
	//System.out.println( "Mon 2: " + time[0] + ", " + time[time.length-1]);
	time = runFile.TimeChannelBoundaries( 4, 1 );
	System.out.println( "Det 5: " + time[0] + ", " + time[time.length-1]);

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
	runfile.close();
    }

    void LoadV4( RandomAccessFile runfile ) throws IOException {
	int i;
	/*
	if ( header.nDet > 0 ) {
	*/
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

	    if ( (this.header.iName).equalsIgnoreCase("scd0") ||
		 (this.header.iName).equalsIgnoreCase("sad0") ||
		 (this.header.iName).equalsIgnoreCase("sad1") ||
		 (this.header.iName).equalsIgnoreCase("sand") ) {
		float[] tDetectorAngle = new float[ detectorAngle.length + 1];
		float[] tFlightPath = new float[ flightPath.length + 1];
		float[] tDetectorHeight = new float[ detectorHeight.length+ 1];
		short[] tDetectorType = new short[ detectorType.length + 1];
		DetectorMap[] tDetectorMap = new DetectorMap[detectorMap.length + 1];
		System.arraycopy(detectorAngle, 0 , tDetectorAngle, 0, 
				 detectorAngle.length );
		System.arraycopy(flightPath, 0 , tFlightPath, 0, 
				 flightPath.length );
		System.arraycopy(detectorHeight, 0 , tDetectorHeight, 0, 
				 detectorHeight.length );
		System.arraycopy(detectorType, 0 , tDetectorType, 0,				 detectorType.length );
		System.arraycopy(detectorMap, 0 , tDetectorMap, 0, 
				 detectorMap.length );
		detectorAngle = tDetectorAngle;
		flightPath = tFlightPath;
	 	detectorHeight = tDetectorHeight;
		detectorType = tDetectorType;
		detectorMap = tDetectorMap;
		detectorAngle[detectorAngle.length - 1] = (float)header.dta;
		flightPath[flightPath.length - 1] = (float)header.dtd;
		detectorHeight[detectorHeight.length - 1] = 
		    (float)header.yDisplacement;
		detectorMap[detectorMap.length - 1] = new DetectorMap();
		if ( (this.header.iName).equalsIgnoreCase("scd0") ){
		    detectorType[ detectorType.length - 1 ] = 11;
		}
		else if ( (this.header.iName).equalsIgnoreCase("sad0") ||
			  (this.header.iName).equalsIgnoreCase("sad1")){
		    detectorType[ detectorType.length - 1 ] = 12;

		}
		else if ( (this.header.iName).equalsIgnoreCase("sand") ){
		    detectorType[ detectorType.length - 1 ] = 13;

		}

	    }

	    lpsdIDMap = new LpsdDetIdMap( runfile, header );
	    detectorLength = new float[header.nDet + 1];
	    detectorWidth = new float[header.nDet + 1];
	    detectorDepth = new float[header.nDet + 1];
	    detectorEfficiency = new float[header.nDet + 1];
	    psdOrder = new int[header.nDet + 1];
	    numSegs1 = new int[header.nDet + 1];
	    numSegs2 = new int[header.nDet + 1];
	    crateNum = new int[header.nDet + 1];
	    slotNum = new int[header.nDet + 1];
	    inputNum = new int[header.nDet + 1];
	    dataSource = new int[header.nDet + 1];
	    minID = new int[header.nDet + 1];
	    segments = new Segment[header.numOfElements + 1];
	    for ( int ii = 1; ii <= header.nDet; ii++ ) {
		if ( detectorAngle[ii] == 0.0F &&
		     flightPath[ii] == 0.0F &&
		     detectorType[ii] == 0 ) {
		}
		else if ( header.iName.equalsIgnoreCase( "hrcs" ) || 
			  header.iName.equalsIgnoreCase( "lrcs" ) ) {
		    switch (detectorType[ii]){
		    case 0: {
			if ( ii < 3 )
			detectorType[ii] = 1;
			break;
		    }
		    case 1: {
			detectorType[ii] = 2;
			break;
		    }
		    case 2: {
			detectorType[ii] = 3;
			break;
		    }
		    case 3: {
			detectorType[ii] = 4;
			break;
		    }
		    }
		    psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
		    numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
		    detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
		    detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
		    detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
		    segments[ii] = new Segment();
		    segments[ii].detID = ii; 
		    segments[ii].row = 1; 
		    segments[ii].column = 1; 
		    segments[ii].length = LENGTH[detectorType[ii]]; 
		    segments[ii].width = WIDTH[detectorType[ii]]; 
		    segments[ii].depth = DEPTH[detectorType[ii]]; 
		    segments[ii].efficiency = 
			EFFICIENCY[detectorType[ii]]; 
		}
		else if ( header.iName.equalsIgnoreCase( "gppd" ) ) {
		    switch (detectorType[ii]){
		    case 1: {
 			detectorType[ii] = 9;
			break;
		    }
		    case 2: {
			detectorType[ii] = 6;
			break;
		    }
		    }
		    psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
		    numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
		    detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
		    detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
		    detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
		    segments[ii].detID = ii; 
		    segments[ii].row = 1; 
		    segments[ii].column = 1; 
		    segments[ii].length = LENGTH[detectorType[ii]]; 
		    segments[ii].width = WIDTH[detectorType[ii]]; 
		    segments[ii].depth = DEPTH[detectorType[ii]]; 
		    segments[ii].efficiency = 
			EFFICIENCY[detectorType[ii]]; 
		}
		else if ( header.iName.equalsIgnoreCase( "sepd" ) ) {
		    switch (detectorType[ii]){
		    case 1: {
			detectorType[ii] = 6;
			break;
		    }
		    case 2: {
			detectorType[ii] = 9;
			break;
		    }
		    }
		    psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
		    numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
		    detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
		    detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
		    detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
		    segments[ii].detID = ii; 
		    segments[ii].row = 1; 
		    segments[ii].column = 1; 
		    segments[ii].length = LENGTH[detectorType[ii]]; 
		    segments[ii].width = WIDTH[detectorType[ii]]; 
		    segments[ii].depth = DEPTH[detectorType[ii]]; 
		    segments[ii].efficiency = 
			EFFICIENCY[detectorType[ii]]; 
		}
		else if ( header.iName.equalsIgnoreCase( "qens" ) ) {
		    switch (detectorType[ii]){
		    case 0: {
			detectorType[ii] = 8;
			break;
		    }
		    case 1: {
			detectorType[ii] = 10;
			break;
		    }
		    }
		    psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
		    numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
		    detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
		    detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
		    detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
		    segments[ii].detID = ii; 
		    segments[ii].row = 1; 
		    segments[ii].column = 1; 
		    segments[ii].length = LENGTH[detectorType[ii]]; 
		    segments[ii].width = WIDTH[detectorType[ii]]; 
		    segments[ii].depth = DEPTH[detectorType[ii]]; 
		    segments[ii].efficiency = 
			EFFICIENCY[detectorType[ii]]; 
		}
		else if ( header.iName.equalsIgnoreCase( "hipd" ) ) {
		    switch (detectorType[ii]){
		    case 0: {
			detectorType[ii] = 9;
			break;
		    }
		    case 1: {
			detectorType[ii] = 6;
			break;
		    }
		    }
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			segments[ii].detID = ii; 
			segments[ii].row = 1; 
			segments[ii].column = 1; 
			segments[ii].length = LENGTH[detectorType[ii]]; 
			segments[ii].width = WIDTH[detectorType[ii]]; 
			segments[ii].depth = DEPTH[detectorType[ii]]; 
			segments[ii].efficiency = 
			    EFFICIENCY[detectorType[ii]]; 
		}
		else if ( header.iName.equalsIgnoreCase( "chex" ) ) {
		    switch (detectorType[ii]){
		    case 0: {
			detectorType[ii] = 6;
			break;
		    }
		    case 1: {
			detectorType[ii] = 10;
			break;
		    }
		    }
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			segments[ii].detID = ii; 
			segments[ii].row = 1; 
			segments[ii].column = 1; 
			segments[ii].length = LENGTH[detectorType[ii]]; 
			segments[ii].width = WIDTH[detectorType[ii]]; 
			segments[ii].depth = DEPTH[detectorType[ii]]; 
			segments[ii].efficiency = 
			    EFFICIENCY[detectorType[ii]]; 
		}
		if ( header.iName.equalsIgnoreCase( "scd0") ) {
		    switch (detectorType[ii]){
		    case 1:{
			detectorType[ii] = 1;
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			segments[ii] = new Segment();
			segments[ii].detID = ii; 
			segments[ii].row = 1; 
			segments[ii].column = 1; 
			segments[ii].length = LENGTH[detectorType[ii]]; 
			segments[ii].width = WIDTH[detectorType[ii]]; 
			segments[ii].depth = DEPTH[detectorType[ii]]; 
			segments[ii].efficiency = 
			    EFFICIENCY[detectorType[ii]]; 
			break;
		    }
		    case 11: {
			detectorType[ii] = 11;
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			for ( int segY = 0; segY < header.numOfY; segY++) {
			    for ( int segX = 0; segX < header.numOfX; segX++) {
				int index = ii + segX + segY *( header.numOfX);
				System.out.println("index: " + index );
				segments[index] = new Segment();
				segments[index].detID = ii; 
				segments[index].row = segX; 
				segments[index].column = segY; 
				segments[index].length = 
				    LENGTH[detectorType[ii]/header.numOfY]; 
				segments[index].width = 
				    WIDTH[detectorType[ii]/header.numOfX]; 
				segments[index].depth = 
				    DEPTH[detectorType[ii]]; 
				segments[index].efficiency = 
				    EFFICIENCY[detectorType[ii]]; 
			    }
			}
			break;		    }
		    }
		    
		}
		if ( header.iName.equalsIgnoreCase( "sad0") ||
		     header.iName.equalsIgnoreCase( "sad1") ) {
		    switch (detectorType[ii]){
		    case 1: {
			detectorType[ii] = 9;
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			segments[ii] = new Segment();
			segments[ii].detID = ii; 
			segments[ii].row = 1; 
			segments[ii].column = 1; 
			segments[ii].length = LENGTH[detectorType[ii]]; 
			segments[ii].width = WIDTH[detectorType[ii]]; 
			segments[ii].depth = DEPTH[detectorType[ii]]; 
			segments[ii].efficiency = 
			    EFFICIENCY[detectorType[ii]]; 
			break;
		    }
		    case 12: {
			detectorType[ii] = 12;
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			for ( int segY = 0; segY < header.numOfY; segY++) {
			    for ( int segX = 0; segX < header.numOfX; segX++) {
				int index = ii + segX + segY *( header.numOfY);
				segments[index] = new Segment();
				segments[index].detID = ii; 
				segments[index].row = segX; 
				segments[index].column = segY; 
				segments[index].length = 
				    LENGTH[detectorType[ii]/header.numOfY]; 
				segments[index].width = 
				    WIDTH[detectorType[ii]/header.numOfX]; 
				segments[index].depth = 
				    DEPTH[detectorType[ii]]; 
				segments[index].efficiency = 
				    EFFICIENCY[detectorType[ii]]; 
			    }
			}
			break;
		    }
		    }
		}
		if ( header.iName.equalsIgnoreCase( "sand") ){
		    switch (detectorType[ii]){
		    case 0: 
		    case 1: {
			detectorType[ii] = 9;
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			segments[ii] = new Segment();
			segments[ii].detID = ii; 
			segments[ii].row = 1; 
			segments[ii].column = 1; 
			segments[ii].length = LENGTH[detectorType[ii]]; 
			segments[ii].width = WIDTH[detectorType[ii]]; 
			segments[ii].depth = DEPTH[detectorType[ii]]; 
			segments[ii].efficiency = 
			    EFFICIENCY[detectorType[ii]]; 
			break;
		    }
		    case 13: {
			detectorType[ii] = 13;
			psdOrder[ii] = Runfile.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = Runfile.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = Runfile.NUM_OF_SEGS_2[detectorType[ii]];
			detectorLength[ii] = Runfile.LENGTH[detectorType[ii]];
			detectorWidth[ii] = Runfile.WIDTH[detectorType[ii]];
			detectorDepth[ii] = Runfile.DEPTH[detectorType[ii]];
			for ( int segY = 0; segY < header.numOfY; segY++) {
			    for ( int segX = 0; segX < header.numOfX; segX++) {
				int index = ii + segX + segY *( header.numOfY);
				segments[index] = new Segment();
				segments[index].detID = ii; 
				segments[index].row = segX; 
				segments[index].column = segY; 
				segments[index].length = 
				    LENGTH[detectorType[ii]/header.numOfY]; 
				segments[index].width = 
				    WIDTH[detectorType[ii]/header.numOfX]; 
				segments[index].depth = 
				    DEPTH[detectorType[ii]]; 
				segments[index].efficiency = 
				    EFFICIENCY[detectorType[ii]]; 
			    }
			}
			break;
		    }
		    }
		}


	    }
	    if ( header.iName.equalsIgnoreCase( "glad" ) || 
		 header.iName.equalsIgnoreCase( "lpsd" ) ) {
		psdOrder = new int[header.numOfElements + 1];
		numSegs1 = new int[header.numOfElements + 1];
		numSegs2 = new int[header.numOfElements + 1];
		crateNum = new int[header.numOfElements + 1];
		slotNum = new int[header.numOfElements + 1];
		inputNum = new int[header.numOfElements + 1];
		dataSource = new int[header.numOfElements + 1];
		minID = new int[header.numOfElements + 1];
		int detNum = 0;
		for ( int jj = 0; jj < lpsdIDMap.NumOfBanks(); jj++ ) {
		    int[] dets= lpsdIDMap.DetsInBank(jj);
		    for ( int kk = 0; kk < dets.length;
			  kk++ ) {
			detNum++;
			int tminID = lpsdIDMap.MinIdForDet(jj,dets[kk]);
			int tcrate = lpsdIDMap.CrateForDet(jj,dets[kk]);
			int tslot = lpsdIDMap.SlotForDet(jj,dets[kk]);	
			int tinput = lpsdIDMap.InputForDet(jj,dets[kk]);
			if ( tminID != 0 && tcrate != 0 && tslot != 0 &&
			     tinput != 0 ) {
			    if ( jj == 0 ) {
				psdOrder[tminID] = 1;
				numSegs1[tminID] = 1;
				numSegs2[tminID] = 1;
				segments[tminID] = new Segment();
				segments[tminID].detID = detNum; 
				segments[tminID].row = 1;
 				segments[tminID].column = 1; 
				segments[tminID].length = LENGTH[1]; 
				segments[tminID].width = WIDTH[1]; 
				segments[tminID].depth = DEPTH[1]; 
				segments[tminID].efficiency = EFFICIENCY[1]; 
			    }
			    else {
				for ( int ll = 0; ll < 64; ll++ ) {
				    psdOrder[tminID + ll] = 1;
				    numSegs1[tminID + ll] = 1;
				    numSegs2[tminID + ll] = 1;
				    crateNum[tminID + ll] = tcrate;
				    slotNum[tminID + ll] = tslot;
				    inputNum[tminID + ll] = tinput;
				    psdOrder[tminID + ll] = 1;
				    numSegs1[tminID + ll] = 1;
				    numSegs2[tminID + ll] = 1;
				    segments[tminID + ll] = new Segment();
				    segments[tminID + ll].detID = detNum; 
				    segments[tminID + ll].row = ll; 
				    segments[tminID + ll].column = 1; 
				    segments[tminID + ll].length = 
					LENGTH[7]/ 64; 
				    segments[tminID + ll].width = 
					WIDTH[7]; 
				    segments[tminID + ll].depth = 
					DEPTH[7]; 
				    segments[tminID + ll].efficiency = 
					EFFICIENCY[7]; 
				}
				    }
			}
		    }
		}
	    }
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
	    subgroupIDList = 
		new int[ header.numOfElements * header.numOfHistograms + 1 ];
	    subgroupMap = new int[1][1];
	    segmentMap = new Segment[1][1];
	    for (i=0; i < subgroupIDList.length; i++ )
		subgroupIDList[i] = -1;
	    int group = 0;
	    maxSubgroupID = new int[ header.numOfHistograms + 1 ];
	    minSubgroupID = new int[ header.numOfHistograms + 1 ];
	    for (int nHist = 1; nHist <= header.numOfHistograms; nHist++) {
		minSubgroupID[nHist] = group + 1;
		if((!(this.header.iName).equalsIgnoreCase("scd0")&&
		    !(this.header.iName).equalsIgnoreCase("sad0")&&
		    !(this.header.iName).equalsIgnoreCase("sad1")&&
		    !(this.header.iName).equalsIgnoreCase("sand")) ){
		    for (int nDet = 1; nDet <= header.numOfElements; nDet++ ) {
			int index = ( (nHist -1) * header.numOfElements ) + nDet;
			int tfType = detectorMap[index].tfType;
			if ((subgroupIDList[index] == -1) && (tfType != 0)) {
			    group++;
			    subgroupIDList[index] = group;
			    int[][] tempMap = new int[group + 1][];
			    System.arraycopy(subgroupMap, 0, tempMap, 0, 
					     subgroupMap.length);
			    int[] idList = {0, nDet};
			    Segment[][] tsegMap = new Segment[group + 1][];
			    System.arraycopy(segmentMap, 0 , tsegMap, 0,
					     segmentMap.length );
			    Segment[] segList = {segments[nDet]};
			    for (int k = nDet+1; k <= header.numOfElements
				     ; k++) {
				
				int index2 = 
				    ( (nHist -1) * header.numOfElements ) + k;
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
				    Segment[] tsegList = 
					new Segment[segList.length + 1];
				    System.arraycopy( segList, 0, tsegList,
						      0, segList.length);
				    segList = tsegList;
				    segList[segList.length - 1] = 
					segments[k];
				    
				}
			    }
			    subgroupMap = tempMap;
			    subgroupMap[group] = new int[idList.length - 1];
			    System.arraycopy(idList, 1, subgroupMap[group], 0, 
					     idList.length-1);
			    segmentMap = tsegMap;
			    segmentMap[group] = segList;
			}
		    }
		}
		else {
		    for (int nDet = 1; nDet <= header.nDet; nDet++ ) {
			if (nDet < header.nDet ) {
			    int index = ( (nHist -1) * header.numOfElements ) + nDet;
			    int tfType = detectorMap[index].tfType;
			    if ((subgroupIDList[index] == -1) && (tfType != 0)) {
				group++;
				subgroupIDList[index] = group;

				int[][] tempMap = new int[group + 1][];
				System.arraycopy(subgroupMap, 0, tempMap, 0, 
						 subgroupMap.length);
				int[] idList = {0,nDet};
				Segment[][] tsegMap = new Segment[group + 1][];
				System.arraycopy(segmentMap, 0 , tsegMap, 0,
						 segmentMap.length );
				Segment[] segList = {segments[nDet]};
				for (int k = nDet+1; k <= header.nDet; k++) {
				    if ( k < header.nDet ) {
				    int index2 = 
					((nHist -1)*header.numOfElements ) + k;
				    int tfType2 = detectorMap[index2].tfType;
				    if ( ( detectorMap[index2].address == 
					   detectorMap[index].address) &&
					 (tfType2 != 0) ){
					subgroupIDList[index2] = group;
					int[] tempList=
					    new int[idList.length+1];
					System.arraycopy(idList, 0, tempList, 
							 0, idList.length);
					idList = tempList;
					idList[idList.length - 1] = k;
					Segment[] tsegList = 
					    new Segment[segList.length + 1];
					System.arraycopy( segList, 0, tsegList,
							  0, segList.length);
					segList = tsegList;
					segList[segList.length - 1] = 
					    segments[k];
				    
				    }
				    }
				}
				subgroupMap = tempMap;
				subgroupMap[group] = new int[idList.length - 1];
				System.arraycopy(idList, 1, subgroupMap[group], 0, 
						 idList.length-1);
				segmentMap = tsegMap;
				segmentMap[group] = segList;
			    }
			}
			     else { 
				System .out.println("Adding groups for Area "+
						    "elements " + group + 
						    " already." );
				int segX = numSegs1[nDet];
				int segY = numSegs2[nDet];
				int [][] tempMap = new int[group +
							   (segX*segY) + 1][];
				Segment[][] tsegmentMap = 
				    new Segment[group + (segX*segY) + 1][1];
				System.arraycopy( subgroupMap, 0,
						  tempMap, 0,
						  subgroupMap.length );
				System.arraycopy( segmentMap, 0,
						  tsegmentMap, 0,
						  segmentMap.length );
				subgroupMap = tempMap;
				segmentMap = tsegmentMap;
				 for ( int ny = 0; ny < segY; ny++ ) {
				    for ( int nx=0; nx < segX; nx++ ) {
					 int aindex = nx + ny*(segX);
					 int[] idList = { nDet,nx, ny};
					 subgroupMap[group + aindex + 1] = 
					      new int[2]; 
					 subgroupMap[group+aindex+1][0] = nDet;
					 segmentMap[group+aindex+1][0] = 
					     segments[header.nDet - 1+ aindex];
					 }
				    }
				group = group + segX * segY;
			     }
		    }
		}
 		maxSubgroupID[nHist] = group;
	    }

	runfile.seek(this.header.timeScaleTable.location);
	short[] timeScaleTemp = new short[this.header.timeScaleTable.size/2 + 1];
	timeScale = new float[this.header.timeScaleTable.size/2 + 1];
	timeScaleTemp = ReadShortArray(runfile,
					 header.timeScaleTable.size/2);
	for (i = 1; i <= this.header.timeScaleTable.size/2; i++) {
	    timeScale[i] = (float)(1 + timeScaleTemp[i]/ Math.pow( 2, 15));
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

   }

    void LoadV5( RandomAccessFile runfile ) throws IOException {
	int i;
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
	detectorAngle = new float[header.detectorAngle.size / 4 + 1];
	byte[] bArray = new byte[ this.header.detectorAngle.size ];
	runfile.read( bArray );
	ByteArrayInputStream bArrayIS = 
	    new ByteArrayInputStream( bArray );
	DataInputStream dataStream = new DataInputStream( bArrayIS );
	for ( i = 1; i <= header.detectorAngle.size / 4; i++ ) {
	    detectorAngle[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();
	
	runfile.seek(this.header.flightPath.location);
	bArray = new byte[ this.header.flightPath.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	flightPath = new float[header.flightPath.size / 4 + 1];
	for ( i = 1; i <= header.flightPath.size / 4; i++ ) {
	    flightPath[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorHeight.location);
	bArray = new byte[ this.header.detectorHeight.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorHeight = new float[header.detectorHeight.size / 4 + 1];
	for ( i = 1; i <= header.detectorHeight.size / 4; i++ ) {
	    detectorHeight[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorType.location);
	bArray = new byte[ this.header.detectorType.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorType = new short[header.detectorType.size / 2 + 1];
	for ( i = 1; i <= header.detectorType.size / 2; i++ ) {
	    detectorType[i] = dataStream.readShort();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorLength.location);
	bArray = new byte[ this.header.detectorLength.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorLength = new float[header.detectorLength.size / 4 + 1];
	for ( i = 1; i <= header.detectorLength.size / 4; i++ ) {
	    detectorLength[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorWidth.location);
	bArray = new byte[ this.header.detectorWidth.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorWidth = new float[header.detectorWidth.size / 4 + 1];
	for ( i = 1; i <= header.detectorWidth.size / 4; i++ ) {
	    detectorWidth[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorDepth.location);
	bArray = new byte[ this.header.detectorDepth.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorDepth = new float[header.detectorDepth.size / 4 + 1];
	for ( i = 1; i <= header.detectorDepth.size / 4; i++ ) {
	    detectorDepth[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detCoordSys.location);
	bArray = new byte[ this.header.detCoordSys.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detCoordSys = new short[header.detCoordSys.size / 2 + 1];
	for ( i = 1; i <= header.detCoordSys.size / 2; i++ ) {
	    detCoordSys[i] = dataStream.readShort();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorRot1.location);
	bArray = new byte[ this.header.detectorRot1.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorRot1 = new float[header.detectorRot1.size / 4 + 1];
	for ( i = 1; i <= header.detectorRot1.size / 4; i++ ) {
	    detectorRot1[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorRot2.location);
	bArray = new byte[ this.header.detectorRot2.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorRot2 = new float[header.detectorRot2.size / 4 + 1];
	for ( i = 1; i <= header.detectorRot2.size / 4; i++ ) {
	    detectorRot2[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.detectorEfficiency.location);
	bArray = new byte[ this.header.detectorEfficiency.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	detectorEfficiency = 
	    new float[header.detectorEfficiency.size / 4 + 1];
	for ( i = 1; i <= header.detectorEfficiency.size / 4; i++ ) {
	    detectorEfficiency[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.psdOrder.location);
	bArray = new byte[ this.header.psdOrder.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	psdOrder = new int[header.psdOrder.size / 4 + 1];
	for ( i = 1; i <= header.psdOrder.size / 4; i++ ) {
	    psdOrder[i] = dataStream.readInt();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.numSegs1.location);
	bArray = new byte[ this.header.numSegs1.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	numSegs1 = new int[header.numSegs1.size / 4 + 1];
	for ( i = 1; i <= header.numSegs1.size / 4; i++ ) {
	    numSegs1[i] = dataStream.readInt();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.numSegs2.location);
	bArray = new byte[ this.header.numSegs2.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	numSegs2 = new int[header.numSegs2.size / 4 + 1];
	for ( i = 1; i <= header.numSegs2.size / 4; i++ ) {
	    numSegs2[i] = dataStream.readInt();
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.crateNum.location);
	bArray = new byte[ this.header.crateNum.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	if (header.crateNum.size > 0 ) {
	    crateNum = new int[header.crateNum.size / 4 + 1];
	    for ( i = 1; i <= header.crateNum.size / 4; i++ ) {
		crateNum[i] = dataStream.readInt();
	    }
	}
	else {
	    crateNum = new int[header.nDet + 1];
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.slotNum.location);
	bArray = new byte[ this.header.slotNum.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	if ( header.slotNum.size > 0 ) { 
	    slotNum = new int[header.slotNum.size / 4 + 1];
	    for ( i = 1; i <= header.slotNum.size / 4; i++ ) {
		slotNum[i] = dataStream.readInt();
	    }
	}
	else {
	    slotNum = new int[header.nDet + 1];
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.inputNum.location);
	bArray = new byte[ this.header.inputNum.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	if ( header.inputNum.size > 0 ) {
	    inputNum = new int[header.inputNum.size / 4 + 1];
	    for ( i = 1; i <= header.inputNum.size / 4; i++ ) {
		inputNum[i] = dataStream.readInt();
	    }
	}
	else {
	    inputNum = new int[header.nDet + 1];
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.dataSource.location);
	bArray = new byte[ this.header.dataSource.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	if ( header.dataSource.size > 0 ) {
	    dataSource = new int[header.dataSource.size / 4 + 1];
	    for ( i = 1; i <= header.dataSource.size / 4; i++ ) {
		dataSource[i] = dataStream.readInt();
	    }
	}
	else {
	    dataSource = new int[header.nDet + 1];
	    for ( i = 1; i <= header.nDet; i++ ) {
		minID[i] = 0;
	    }
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.minID.location);
	bArray = new byte[ this.header.minID.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	if ( header.minID.size > 0 ) {
	    minID = new int[header.minID.size / 4 + 1];
	    for ( i = 1; i <= header.minID.size / 4; i++ ) {
		minID[i] = dataStream.readInt();
	    }
	}
	else {
	    minID = new int[header.nDet + 1];
	    for ( i = 1; i <= header.nDet; i++ ) {
		minID[i] = i;
	    }
	}
	bArrayIS.close();
	dataStream.close();

	runfile.seek(this.header.discSettings.location);
	bArray = new byte[ this.header.discSettings.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	discriminator = new DiscLevel[this.header.discSettings.size/8 + 1];
	for (i = 1; i <= this.header.discSettings.size/8; i++) {
	    discriminator[i] = new DiscLevel();
	    discriminator[i].lowerLevel = dataStream.readInt();
	    discriminator[i].upperLevel = dataStream.readInt();
	    }
	bArrayIS.close();
	dataStream.close();

	segments = new Segment[1];
	segments[0] = new Segment();
	for (int ii = 1; ii <= header.nDet; ii++ ) {
	    int segs = minID[ii] + numSegs1[ii] * numSegs2[ii];
	    if ( segments.length < ( segs + 1 ) ){
		Segment [] tsegments =new Segment[segs + 1];
		System.arraycopy( segments, 0 , tsegments, 0, 
				  segments.length);
		segments = tsegments;
	    }
	    for ( int segY = 0; segY <= numSegs1[ii]; segY ++ ) {
		for ( int segX = 0; segX <= numSegs2[ii]; segX ++ ) {
		    int segID = minID[ii] + segX + segY * numSegs2[ii];
		    segments[segID] = 
			new Segment( ii, segX, segY, 
				     detectorLength[ii]/numSegs1[ii],
				     detectorWidth[ii]/numSegs2[ii],
				     detectorDepth[ii],
				     detectorEfficiency[ii]
				     );
		    
		}
	    }
	}
	runfile.seek(this.header.detectorSGMap.location );
	int [][] IDMap = 
	    new int[this.header.numOfHistograms][this.header.nDet];
	subgroupIDList = 
	    new int[this.header.nDet *this.header.numOfHistograms +1];
	
	minSubgroupID = new int[this.header.numOfHistograms + 1];
	maxSubgroupID = new int[this.header.numOfHistograms + 1];
	for ( i = 0; i < this.header.numOfHistograms; i++ ) {
	    for (int jj = 0; jj < this.header.nDet; jj++ ) {
		IDMap[i][jj] = runfile.readInt();
		subgroupIDList[(i)* this.header.nDet + jj]  = IDMap[i][jj];
		if ( IDMap[i][jj] > maxSubgroupID[i + 1]){
		    maxSubgroupID[i + 1] = IDMap[i][jj];
		}
		if ( (IDMap[i][jj] < minSubgroupID[i + 1] 
		      && IDMap[i][jj] > 0 ) 
		     || minSubgroupID[i + 1] == 0 ) {
		    minSubgroupID[i+1] = IDMap[i][jj];
		}
	    }
	}
	subgroupMap = 
	    new int[maxSubgroupID[this.header.numOfHistograms]+ 1][];
	segmentMap =
	    new Segment[maxSubgroupID[this.header.numOfHistograms]+ 1][];
	for ( i = 1 ; i <= maxSubgroupID[this.header.numOfHistograms]; i++ ) {
	    int[] idList = new int[0];
	    Segment[] segList = new Segment[0];
	    for ( int jj = 0; jj < this.header.numOfHistograms; jj++ ) {
		for ( int kk = 0; kk < this.header.nDet; kk++ ) {
		    if ( IDMap[jj][kk] == i ) {
			int[] temp = new int[ idList.length + 1];
			System.arraycopy( idList, 0, temp, 0, idList.length );
			temp[idList.length] = kk + 1;
 			idList = temp;
			Segment[] tseg = new Segment[ segList.length + 1];
			tseg[segList.length] = segments[ minID[kk + 1]];
			segList = tseg;
		    } 
		}
	    }
	    subgroupMap[i] = idList;
	    segmentMap[i] = segList;
	}

	runfile.seek(this.header.timeScaleTable.location);
	bArray = new byte[ this.header.timeScaleTable.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new DataInputStream( bArrayIS );
	timeScale = new float[this.header.timeScaleTable.size/4 + 1];
	for (i = 1; i <= this.header.timeScaleTable.size/4; i++) {
	    timeScale[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();
	/*	for ( int ii = 1; ii <= header.nDet; ii++ ) {
	    if ( psdOrder[ii] == 1 ) {
		header.numOfElements += numSegs1[ii];
		System.out.println( "Num Segs: " + ii + " " + numSegs1[ii]);
	    }
	    else if ( psdOrder[ii] == 2 ) {
		header.numOfElements += numSegs1[ii] * numSegs2[ii];
	    }
	}
	*/
	header.numOfElements = header.nDet;

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
       @return The number of detector elements for this instrument
    */
    public int NumElements(){
	return this.header.numOfElements;
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
	double timeToSample = 
	    SourceToSample() / Math.sqrt(EnergyIn()/MEV_FROM_VEL);
	return timeToSample;
    }

    /**
       Calculates source to sample time for a given energy of neutron.
       @param energy - energy value for the calculation
       @return The source to sample distance
    */
    public double SourceToSampleTime(double energy){
	double timeToSample = 
	    SourceToSample() / Math.sqrt(energy/MEV_FROM_VEL);
	return timeToSample;
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

	if ((timeField[tfType].timeFocusBit > 0)) { 
	    if (header.pseudoTimeUnit == 'D') { 
		for ( int nid = 1; nid <= header.nDet; nid++ ) {
		    int nindex = (hist - 1) * header.nDet + nid;
		    int tempType = detectorMap[nindex].tfType;
		    
		    if ( tempType == tfType )
			{
			    angle_sum += Math.abs(detectorAngle[nid]);
			    nDetsThisType++;
			}
		}
	    }
	    else if (header.pseudoTimeUnit == 'I') { 
		for ( int nid = 1; nid <= header.nDet; nid++ ) {
		    int nindex = (hist - 1) * header.nDet + nid;
		    int tempType = detectorMap[nindex].tfType;

		    //		    System.out.println( "Sizeof subgroupIDList = " + 
		    //		subgroupIDList.length );
		    if ( tempType == tfType && 
			 subgroupIDList[index] == 
			 subgroupIDList[nindex] )
			{
			    angle_sum += Math.abs(detectorAngle[nid]);
			    nDetsThisType++;
			}
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
       This method retrieves the height of the detector above the scattering 
       plane.  
       @param detID The detector ID.
       @return The height of the detector above the scattering plane.
    */
    public double RawDetectorHeight(int detID){
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
       This method retrieves the detector data Source.  
       @param detID The detector ID.
       @return The detector dataSource.
     */
    public int DataSource( int detID ) {
	return this.dataSource[detID];
    }

    /**
       This method retrieves the detector minimum ID.  
       @param detID The detector ID.
       @return The detector minimum ID.
     */
    public int MinID( int detID ) {
	return this.minID[detID];
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
	if ( !((psdOrder[segmentMap[subgroup][0].detID] == 2) && (header.versionNumber < 5 )) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) 
		     && subgroup >= MinSubgroupID(i)) hist =  i;
	    }
	    
	    return Get1DSpectrum(id, hist);
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    int numWaves = header.numOfWavelengths;
	    float stepWave = (maxWave - minWave)/numWaves;

	    float[] data = new float[header.numOfWavelengths];
		for ( int ii = 0; ii < header.numOfWavelengths; ii++ ) {
		    data[ii] = ii* stepWave * segmentMap[subgroup][0].row * segmentMap[subgroup][0].column;
		}
	    return data;
	}
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

	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
	    index = detID + (hist-1) * this.header.nDet;
	    if (detectorMap[index].tfType == 0 ) {
		System.out.println( "invalid id in Get1DSpectrum(id,hist), " +
				    "returning null");
		return null;
	    }
	
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
		if ( this.header.versionNumber < 5 ) {
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
	    else {
		byte[] bArray = new byte[ 4 * numOfTimeChannels ];
		runfile.read( bArray );
		ByteArrayInputStream bArrayIS = 
		    new ByteArrayInputStream( bArray );
		DataInputStream dataStream = new DataInputStream( bArrayIS );
		for ( i = 0; i < numOfTimeChannels; i++ ) {
		    data[i] = dataStream.readInt();
		}
		bArrayIS.close();
		dataStream.close();
	    }
	}
	if (!leaveOpen ){
	    runfile.close();
	}
	return data; 
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    int numWaves = header.numOfWavelengths;
	    float stepWave = (maxWave - minWave)/numWaves;

	    data = new float[header.numOfWavelengths];;
		for ( int ii = 0; ii < header.numOfWavelengths; ii++ ) {
		    data[ii] = ii* stepWave *detID ;
		}
	    return data;
	}
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
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) 
		     && subgroup >= MinSubgroupID(i)) hist =  i;
	    }
	    
	    return Get1DSum(id, hist);
	}
	else {
	    return 2.0f;
	}
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

	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
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
	    if (this.header.versionNumber <= 4) {
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
	    }
	    else {
		int idata = runfile.readInt();
		data = (float)idata;
	    }
	    if (!leaveOpen ){
		runfile.close();
	    }
	    return data; 
	}
	else {
	    return 2.0f;
	}
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
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) 
		     && subgroup >= MinSubgroupID(i)) hist =  i;
	    }
	    
	    return NumChannelsBinned(id, hist);
	}
	else {
	    return header.numOfWavelengths;
	}
    }

    /**
       Get number of data channels for a given detector.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Number of channels binned.
    */
    public int NumChannelsBinned( int id, int hist )  {
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.nDet + id;
	    int tfType = detectorMap[index].tfType;
	    int nch = (int)(timeField[tfType].NumOfChannels());
	    return nch;
	}
	else {
	    return header.numOfWavelengths;
	}

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
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return MinBinned(id, hist);
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return minWave;
	}
    }

    /**
       Minimum Binned time in time field table.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Raw minumum time in the time field table.
    */
    public double MinBinned( int id, int hist ) {
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.nDet + id;
	    int tfType = detectorMap[index].tfType;
	    return timeField[tfType].tMin;
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return minWave;
	}
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
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) 
		     && subgroup >= MinSubgroupID(i)) hist =  i;
	    }
	    
	    return MaxBinned(id, hist);
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return maxWave;
	}
    }

    /**
       Maximum Binned time in time field table.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Raw maxumum time in the time field table.
    */

    public double MaxBinned( int id, int hist ) {
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.nDet + id;
	    int tfType = detectorMap[index].tfType;
	    return timeField[tfType].tMax;
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return maxWave;
	}
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

 	if ( !((psdOrder[segmentMap[subgroup][0].detID] == 2) && (header.versionNumber < 5 )) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) 
		     && subgroup >= MinSubgroupID(i)) hist =  i;
	    }
	    
	    return TimeChannelBoundaries(id, hist);
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    int numWaves = header.numOfWavelengths;
	    float stepWave = (maxWave - minWave)/numWaves;

	    float[] channel = new float[header.numOfWavelengths + 1];
		for ( int ii = 0; ii < header.numOfWavelengths + 1; ii++ ) {
		    channel[ii] = (minWave + ii* stepWave);
		}
	    return channel;
	}
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
	if ( id > header.numOfElements || hist > header.numOfHistograms ) return null;
	int index = header.nDet * (hist - 1) + id;

	if ( !((psdOrder[0] == 2) && (header.versionNumber < 5 )) ) {
	    if (detectorMap[index].tfType == 0 ) {
		System.out.println( "invalid id in TimeChannelBoundaries" +
				    "(id,hist), returning null");
		return null;
	    }
	
	    int tfType = detectorMap[index].tfType;
	    int numberOfChannels = timeField[tfType].NumOfChannels();
	    float[] channel = new float[numberOfChannels + 1];
	    
	    float min = (float)timeField[tfType].tMin;
	    float max = (float)timeField[tfType].tMax;
	    float step = (float)timeField[tfType].tStep;
	    
	    float timeToSample = (float)(header.sourceToSample / 
					 Math.sqrt( header.energyIn/MEV_FROM_VEL));
	    
	    switch (header.pseudoTimeUnit ) {
		
	    case ('I'): {
		if ( timeField[tfType].timeFocusBit == 0 ) {  //Det not focused
		    if ( header.versionNumber < 5 ) {
			us_correction = - (float)(header.hardTimeDelay * 
						  header.standardClock) + 
			    (float)(Math.floor(timeToSample/header.standardClock) *
				    header.standardClock);
		    }
		    else {
			us_correction = 0;
		    }
		}
		else {						//Det focused
		    us_correction = timeToSample;
		}
		min = min + us_correction;
		max = max + us_correction;
		break;
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
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    int numWaves = header.numOfWavelengths;
	    float stepWave = (maxWave - minWave)/numWaves;

	    float[] channel = new float[header.numOfWavelengths + 1];
		for ( int ii = 0; ii < header.numOfWavelengths; ii++ ) {
		    channel[ii] = minWave + ii* stepWave;
		}
	    return channel;
	}
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
				     Math.sqrt( header.energyIn/MEV_FROM_VEL));
   
	for (int i = 0; i < timeBounds.length; i++ ) {
	    energyChannels[i] = (float)(header.energyIn - MEV_FROM_VEL * 
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
     public int LowerLevelDisc(int id) {
	if (id < 1 || id > header.nDet ) return -1;
	if (header.versionNumber < 4 ) return (short)0;
	return discriminator[id].lowerLevel;
    }

    /** 
	Retrieves the upper level discriminator for a detector.
	@param id - id of the detector
	@return Value of the upper level discriminator.
    */
    public int UpperLevelDisc(int id) {
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

       if ( !(header.iName).equalsIgnoreCase("glad"))
	if ( header.versionNumber < 5 && psdOrder[segmentMap[sg][0].detID] > 1 ) return false;
	for ( int ii = 0; ii < subgroupMap[sg].length; ii++ ) {
	    int id = subgroupMap[sg][ii];
	    if (detectorAngle[id] == 0 || detectorAngle[id] == 180 ||
		detectorAngle[id] == -180 ) 
	       return true;
            }
	    return false;
	}

   public boolean IsPulseHeight( int id, int hist ) {
       if ( !(header.iName).equalsIgnoreCase("glad"))
       if ( header.versionNumber < 5 && psdOrder[id] > 1 ) return false;
	int index = header.nDet * (hist - 1) + id;
	if (detectorMap[index].tfType == 0 ) return false;
   	int tfType = detectorMap[index].tfType;
	if ( timeField[tfType].pulseHeightBit > 0 ) {
	    return true;
	}
       
	else
	   return false;
	}
   
   /**
       Checks to see if a subgroup is a beam monitor.
       @param sg - detector subgroup.
       @return boolean true if subgroup is a beam monitor.
   */
    public boolean IsPulseHeight( int sg ) {
       if ( !(header.iName).equalsIgnoreCase("glad"))
	if ( header.versionNumber < 5 && psdOrder[segmentMap[sg][0].detID] > 1 ) return false;
	int hist = 0;
	for ( int ii = 0; ii < subgroupMap[sg].length; ii++ ) {
	    int id = subgroupMap[sg][ii];
	    for ( int jj = 1; jj <= header.numOfHistograms; jj++ ) {
		if ( sg <= MaxSubgroupID(jj) ) hist = jj;
	    }
	    if ( IsPulseHeight( id, hist ))
		return true;
	}
	return false;
    }
    
    /** Returns Instrument Type
     */
    public int InstrumentType() {
	return InstrumentType.getIPNSInstType( header.iName );
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
       Returns the solid angle for a given detector or group of detectors in
       a subgroup
    */
    public float SolidAngle( int subgroup ) {
	return (float)subgroupMap[subgroup].length;
    }

    /**
       Returns the solid angle for a given detector ID
    */
    public float SolidAngle( int detID, int hist ) {
	return 1.0f;
    }

    /**
       Returns the time field type for a given detector or group of detectors 
       in a subgroup
    */
    public int TimeFieldType( int subgroup ) throws IOException
    {
	int hist=0;

	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet)
	    return -1;
	
	int[] idsInSg = IdsInSubgroup(subgroup);
	int id = idsInSg[0];
	if ( !((psdOrder[segmentMap[subgroup][0].detID] == 2) && (header.versionNumber < 5 )) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) && subgroup >= MinSubgroupID(i))
		    hist =  i;
	    }
	    
	    return TimeFieldType(id, hist);
	}
	else {
	    return -1;
	}
    }

    /**
       Returns the time field type for a given detector ID
    */
    public int TimeFieldType( int detID, int hist) throws IOException
    {
	int  index;
	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
	
	    index  = detID + (hist-1) * this.header.nDet;
	    return this.detectorMap[index].tfType;
	}
	else {
	    return -1;
	    }
    }

    /**
       Returns a 2D array that contains data from time slice sliceNum
     */
    public float[][] AreaTimeSlice( int sliceNum ) throws IOException {
	int numWavelength = header.numOfWavelengths;
	int numX = header.numOfX;
	int numY = header.numOfY;
	int areaStartAddress;
	int sliceInterval;

	if ( leaveOpen == false){
	    System.out.println("GetSpectrum1D opening file");
	    runfile = new RandomAccessFile(runfileName, "r");
	}
	
	float[][] slice = new float[numY][numX];

	if ( header.versionNumber <= 4 ) {
	    
	    areaStartAddress = header.histStartAddress;
	    sliceInterval = (header.totalChannels*2)/(header.numOfWavelengths);
	    runfile.seek( areaStartAddress + ( sliceNum - 1 ) * sliceInterval + 2);

	    int wordLength = 2;
	    byte[] bdata;
	    bdata = new byte[numX *numY * wordLength];
	    int nbytes = runfile.read( bdata, 0, 
				       numX * numY * wordLength);

	    for (int ix = 0; ix < numX; ix++){
		for (int iy = 0; iy < numY; iy++ ) {
		    for (int j = 0; j < wordLength; j++) {
			int byteIndex = (iy * numX + ix) * wordLength + j;
			if ( bdata[byteIndex] < 0 ) {
			    slice[iy][ix] += (bdata[byteIndex] + 256) * 
				Math.pow(256.0, j);
			}
			else {
			    slice[iy][ix] += bdata[byteIndex] * Math.pow(256.0, j);
			}
		    }
		}
	    }
	    
	}

	if (!leaveOpen ){
	    runfile.close();
	}
	return slice;
    }

}