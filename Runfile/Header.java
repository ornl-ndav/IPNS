package IPNS.Runfile;

import java.io.*; 
import java.lang.Math;

/**
This class is a utility class for the IPNS.Runfile package.  Access to the
member variables is allowed only to members of this package.  It allows 
a logical separation for information in the two block run file header.

*/
/*
 *
 * $Log$
 * Revision 5.6  2001/04/03 20:45:07  hammonds
 * added detector dataSource and minID tables.
 *
 * Revision 5.5  2001/03/15 17:24:53  hammonds
 * Added stuff to handle new dcalib info ( det. size, rotations, crate info...).
 *
 * Revision 5.3  2000/05/22 18:37:21  hammonds
 * Fixed problem reading run numbers out of vesion 2 files
 *
 * Revision 5.2  2000/02/16 01:26:21  hammonds
 * Changed code to handle glad LPSDs.  Have decided to handle all detectors with a common interface.  Most Lpsd<...> classes will be removed.  LpsdDetIDMap will stay since it implements old functionality.
 *
 * Revision 5.1  2000/02/11 23:16:37  hammonds
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

public class Header implements Cloneable {
    protected TableType controlParameter = new TableType();
    protected TableType detectorMapTable = new TableType();
    protected TableType timeFieldTable = new TableType();
    protected TableType timeScaleTable = new TableType();
    protected TableType timeShiftTable = new TableType();
    protected TableType areaStartTable = new TableType();
    protected TableType timeDelayTable = new TableType();
    protected int histStartAddress;
    protected int numOfBlocks;
    protected int offsetToFree;
    protected int versionNumber;
    protected TableType detectorAngle = new TableType();
    protected TableType flightPath = new TableType();
    protected TableType detectorHeight = new TableType();
    protected TableType detectorType = new TableType();
    protected TableType controlTable = new TableType();
    protected TableType seqHistWidth = new TableType();
    protected short nDet;
    protected String userName = new String("");
    protected String runTitle = new String("");
    protected int runNum;
    protected int nextRun;
    protected String startDate = new String("01-JAN-80");
    protected String startTime = new String("00:00:00");
    protected String endDate = new String("01-JAN-80");;
    protected String endTime = new String("00:00:00");
    protected char protStatus = '\0';
    protected char varToMonitor = '\0';
    protected int presetMonitorCounts;
    protected int elapsedMonitorCounts;
    protected short numOfCyclesPreset;
    protected short numOfCyclesCompleted;
    protected int runAfterFinished;
    protected int totalMonitorCounts;
    protected int detCalibFile;
    protected char detLocUnit;
    protected char pseudoTimeUnit;
    protected double sourceToSample;
    protected double sourceToChopper;
    protected int moderatorCalibFile;
    protected short groupToMonitor;
    protected short channelToMonitor;
    protected short numOfHistograms;
    protected short numOfTimeFields;
    protected short numOfControl;
    protected short controlFlag;
    protected short clockShift;
    protected int totalChannels;
    protected int numOfPulses;
    protected int sizeOfDataArea;
    protected int hardwareTMin;
    protected int hardwareTMax;
    protected int hardTimeDelay;
    protected short numOfX;
    protected short numOfY;
    protected short numOfWavelengths;
    protected int maxWavelength;
    protected int minWavelength;
    protected double dta;
    protected double dtd;
    protected double omega;
    protected double chi;
    protected double phi;
    protected double xLeft;
    protected double xRight;
    protected double yLower;
    protected double yUpper;
    protected double xDisplacement;
    protected double yDisplacement;
    protected double xLength;
    protected short areaChannelWidth;
    protected short areaDoubleInterval;
    protected int addressOf1DData;
    protected int addressOf2DData;
    protected int endOfOverflow;
    protected int channels1D;
    protected short numOfOverflows;
    protected double clockPeriod;
    protected double energyIn;
    protected double energyOut;
    protected short numOfSeqHist;
    protected double protonCurrent;
    protected short areaBinning;
    protected short microprocessor;
    protected short numOfLockouts;
    protected int firstOverflow;
    protected int expNum;
    protected int firstRun;
    protected int lastRun;
    protected int defaultRun;
    protected short samplePos;
    protected short numOfHeadBlocks;
    protected short overflowSort;
    protected double standardClock;
    protected double lpsdClock;
    protected int numOfElements;
    protected String iName;
    protected int MagicNumber;
    protected TableType messageRegion = new TableType();
    protected TableType discSettings = new TableType();
    protected TableType PSD_IDMap = new TableType();
    protected TableType lpsdStartTable = new TableType();
    protected TableType detectorStartTable = new TableType();
    protected TableType lpsdMapTable = new TableType();
    protected TableType lpsdTimeFieldTable = new TableType();
    protected TableType lpsdAngle = new TableType();
    protected TableType lpsdFlightPath = new TableType();
    protected TableType lpsdHeight = new TableType();
    protected TableType lpsdType = new TableType();
    protected TableType lpsdLength = new TableType();
    protected TableType lpsdWidth = new TableType();
    protected TableType lpsdDepth = new TableType();
    protected TableType detectorLength = new TableType();
    protected TableType detectorWidth = new TableType();
    protected TableType detectorDepth = new TableType();
    protected TableType detectorSGMap = new TableType();
    protected TableType detCoordSys = new TableType();
    protected TableType detectorRot1 = new TableType();
    protected TableType detectorRot2 = new TableType();
    protected TableType detectorEfficiency = new TableType();
    protected TableType psdOrder = new TableType();
    protected TableType numSegs1 = new TableType();
    protected TableType numSegs2 = new TableType();
    protected TableType crateNum = new TableType();
    protected TableType slotNum = new TableType();
    protected TableType inputNum = new TableType();
    protected TableType dataSource = new TableType();
    protected TableType minID = new TableType();

    // --------------------------- readUnsignedInteger -------------------

    protected int readUnsignedInteger(RandomAccessFile inFile,
				      int length) throws IOException {

	byte b[] = new byte[length];
	int c[] = new int[length];
	int nBytesRead = inFile.read(b, 0, length);
	//	for ( int i = 0; i < length; i++ ){
	//	    System.out.println ( "b[" + i + "] = " + b[i] );
	//	}
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

    // --------------------------- readUnsignedInteger -------------------

    protected long readUnsignedLong(RandomAccessFile inFile,
				      int length) throws IOException {

	byte b[] = new byte[length];
	int c[] = new int[length];
	int nBytesRead = inFile.read(b, 0, length);
	long num = 0;
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

    // ---------------------------- ReadVAXReal4 ------------------------

    protected double ReadVAXReal4(RandomAccessFile inFile)
	throws IOException {

        int length = 4;
        long hi_mant, low_mant, exp, sign;
        double f_val;
        long val = (long )readUnsignedInteger(inFile, length);
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
		Math.pow(2.0, (double)exp );

        if ( sign == 1 )
	    f_val = -f_val;
        return f_val;
    }


public static void main(String[] args) throws IOException{
	System.out.println("Runfile Name is " + args[0]);
	RandomAccessFile runfile = new RandomAccessFile(
		args[0], "r");
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
	System.out.println( "iName: " + iName );
	Header header = new Header(runfile, iName );
	runfile.close();

	System.out.println("controlParameter:  " + 
				header.controlParameter.location +
				", " + header.controlParameter.size);
	System.out.println("detectorMapTable:  " + 
				header.detectorMapTable.location +
				", " + header.detectorMapTable.size);
	System.out.println("timeFieldTable:    " + 
				header.timeFieldTable.location +
				", " + header.timeFieldTable.size);
	System.out.println("timeScaleTable:    " + 
				header.timeScaleTable.location +
				", " + header.timeScaleTable.size);
	System.out.println("timeShiftTable:    " + 
				header.timeShiftTable.location +
				", " + header.timeShiftTable.size);
	System.out.println("areaStartTable:    " + 
				header.areaStartTable.location +
				", " + header.areaStartTable.size);
	System.out.println("timeDelayTable:    " + 
				header.timeDelayTable.location +
				", " + header.timeDelayTable.size);
	System.out.println("histStartAddress:  " + 
				header.histStartAddress );
	System.out.println("numOfBlocks:       " + 
				header.numOfBlocks );
	System.out.println("offsetToFree:      " + 
				header.offsetToFree );
	System.out.println("versionNumber:     " + 
				header.versionNumber );
	System.out.println("detectorAngle:     " + 
				header.detectorAngle.location +
				", " + header.detectorAngle.size);
	System.out.println("flightPath:        " + 
				header.flightPath.location +
				", " + header.flightPath.size);
	System.out.println("detectorHeight:    " + 
				header.detectorHeight.location +
				", " + header.detectorHeight.size);
	System.out.println("detectorType:      " + 
				header.detectorType.location +
				", " + header.detectorType.size);
	System.out.println("controlTable:      " + 
				header.controlTable.location +
				", " + header.controlTable.size);
	System.out.println("seqHistWidth:      " + 
				header.seqHistWidth.location +
				", " + header.seqHistWidth.size);
	System.out.println("nDet:              " + 
				header.nDet );
	System.out.println("userName:          " + 
				header.userName );
	System.out.println("runTitle:          " + 
				header.runTitle );
	System.out.println("runNum:          " + 
				header.runNum );
	System.out.println("nextRun:          " + 
				header.nextRun );
	System.out.println("startDate:         " + 
				header.startDate );
	System.out.println("startTime:         " + 
				header.startTime );
	System.out.println("endDate:           " + 
				header.endDate );
	System.out.println("endTime:           " + 
				header.endTime );
	System.out.println("protStatus:        " + 
				(int)header.protStatus );
	System.out.println("varToMonitor:      " + 
				header.varToMonitor );
	System.out.println("presetMonitorCounts:  " + 
				header.presetMonitorCounts );
	System.out.println("elapsedMonitorCounts: " + 
				header.elapsedMonitorCounts );
	System.out.println("numOfCyclesPreset:    " + 
				header.numOfCyclesPreset );
	System.out.println("numOfCyclesCompleted: " + 
				header.numOfCyclesCompleted );
	System.out.println("runAfterFinished:     " + 
				header.runAfterFinished );
	System.out.println("totalMonitorCounts:   " + 
				header.totalMonitorCounts );
	System.out.println("detCalibFile:         " + 
				header.detCalibFile );
	System.out.println("detLocUnit:           " + 
				header.detLocUnit );
	System.out.println("pseudoTimeUnit:       " + 
				header.pseudoTimeUnit );
	System.out.println("sourceToSample:       " + 
				header.sourceToSample );
	System.out.println("sourceToChopper:      " + 
				header.sourceToChopper );
	System.out.println("moderatorCalibFile:   " + 
				header.moderatorCalibFile );
	System.out.println("groupToMonitor:       " + 
				header.groupToMonitor );
	System.out.println("channelToMonitor:     " + 
				header.channelToMonitor );
	System.out.println("numOfHistograms:      " + 
				header.numOfHistograms );
	System.out.println("numOfTimeFields:      " + 
				header.numOfTimeFields );
	System.out.println("numOfControl:         " + 
				header.numOfControl );
	System.out.println("controlFlag:          " + 
				header.controlFlag );
	System.out.println("clockShift:           " + 
				header.clockShift );
	System.out.println("totalChannels:        " + 
				header.totalChannels );
	System.out.println("numOfPulses:          " + 
				header.numOfPulses );
	System.out.println("sizeOfDataArea:       " + 
				header.sizeOfDataArea );
	System.out.println("hardwareTMin:         " + 
				header.hardwareTMin );
	System.out.println("hardwareTMax:         " + 
				header.hardwareTMax );
	System.out.println("hardTimeDelay:        " + 
				header.hardTimeDelay );
	System.out.println("numOfX:               " + 
				header.numOfX );
	System.out.println("numOfY:               " + 
				header.numOfY );
	System.out.println("numOfWavelengths:     " + 
				header.numOfWavelengths );
	System.out.println("maxWavelength:        " + 
				header.maxWavelength );
	System.out.println("minWavelength:        " + 
				header.minWavelength );
	System.out.println("dta:                  " + 
				header.dta );
	System.out.println("dtd:                  " + 
				header.dtd );
	System.out.println("omega                 " + 
				header.omega );
	System.out.println("chi:                  " + 
				header.chi );
	System.out.println("phi:                  " + 
				header.phi );
	System.out.println("xLeft:                " + 
				header.xLeft );
	System.out.println("xRight:               " + 
				header.xRight );
	System.out.println("yLower:               " + 
				header.yLower );
	System.out.println("yUpper:               " + 
				header.yUpper );
	System.out.println("xDisplacement:        " + 
				header.xDisplacement );
	System.out.println("yDisplacement:        " + 
				header.yDisplacement );
	System.out.println("xLength:              " + 
				header.xLength );
	System.out.println("areaChannelWidth:     " + 
				header.areaChannelWidth );
	System.out.println("areaDoubleInterval:   " + 
				header.areaDoubleInterval );
	System.out.println("addressOf1DData       " + 
				header.addressOf1DData );
	System.out.println("addressOf2DData       " + 
				header.addressOf2DData );
	System.out.println("endOfOverflow:        " + 
				header.endOfOverflow );
	System.out.println("channels1D:           " + 
				header.channels1D );
	System.out.println("numOfOverflows:       " + 
				header.numOfOverflows );
	System.out.println("clockPeriod:          " + 
				header.clockPeriod );
	System.out.println("energyIn:             " + 
				header.energyIn );
	System.out.println("energyOut:            " + 
				header.energyOut );
	System.out.println("numOfSeqHist:         " + 
				header.numOfSeqHist );
	System.out.println("protonCurrent:        " + 
				header.protonCurrent );
	System.out.println("areaBinning:          " + 
				header.areaBinning );
	System.out.println("microprocessor:       " + 
				header.microprocessor );
	System.out.println("numOfLockouts:        " + 
				header.numOfLockouts );
	System.out.println("firstOverflow:        " + 
				header.firstOverflow );
	System.out.println("expNum:               " + 
				header.expNum );
	System.out.println("firstRun:             " + 
				header.firstRun );
	System.out.println("lastRun:              " + 
				header.lastRun );
	System.out.println("defaultRun:           " + 
				header.defaultRun );
	System.out.println("samplePos:            " + 
				header.samplePos );
	System.out.println("numOfHeadBlocks:      " + 
				header.numOfHeadBlocks );
	System.out.println("overflowSort:         " + 
				header.overflowSort );
	System.out.println("messageRegion:     " + 
				header.messageRegion.location +
				", " + header.messageRegion.size);
	System.out.println("discSettings:      " + 
				header.discSettings.location +
				", " + header.discSettings.size);
	System.out.println("PSD_IDMap:         " + 
				header.PSD_IDMap.location +
				", " + header.PSD_IDMap.size);
	System.out.println("lpsdStartTable      " + 
				header.lpsdStartTable.location +
				", " + header.lpsdStartTable.size);
	System.out.println("lpsdMapTable       " + 
				header.lpsdMapTable.location +
				", " + header.lpsdMapTable.size);
	System.out.println("lpsdTimeFieldTable:" + 
				header.lpsdTimeFieldTable.location +
				", " + header.lpsdTimeFieldTable.size);
	System.out.println("lpsdAngle:     " + 
				header.lpsdAngle.location +
				", " + header.lpsdAngle.size);
	System.out.println("lpsdFlightPath:        " + 
				header.lpsdFlightPath.location +
				", " + header.lpsdFlightPath.size);
	System.out.println("lpsdHeight:    " + 
				header.lpsdHeight.location +
				", " + header.lpsdHeight.size);
	System.out.println("lpsdType:      " + 
				header.lpsdType.location +
				", " + header.lpsdType.size);
	System.out.println("standardClock:        " + 
				header.standardClock );
	System.out.println("lpsdClock:            " + 
				header.lpsdClock );
	System.out.println("numOfElements:           " + 
				header.numOfElements );
	System.out.println("detectorLength:       " + 
				header.detectorLength.location +
				", " + header.detectorLength.size);
	System.out.println("detectorWidth:        " + 
				header.detectorWidth.location +
				", " + header.detectorWidth.size);
	System.out.println("detectorDepth:        " +  
				header.detectorDepth.location +
				", " + header.detectorDepth.size);
	System.out.println("iName:                " +
			        header.iName );
	System.out.println("detectorSGMap:        " +  
				header.detectorSGMap.location +
				", " + header.detectorSGMap.size);
	System.out.println("detCoordSys:          " +  
				header.detCoordSys.location +
				", " + header.detCoordSys.size);
	System.out.println("detectorRot1:         " +  
				header.detectorRot1.location +
				", " + header.detectorRot1.size);
	System.out.println("detectorRot2:         " +  
				header.detectorRot2.location +
				", " + header.detectorRot2.size);
	System.out.println("detectorEfficiency:   " +  
				header.detectorEfficiency.location +
				", " + header.detectorEfficiency.size);
	System.out.println("psdOrder:             " +  
				header.psdOrder.location +
				", " + header.psdOrder.size);
	System.out.println("numSegs1:             " +  
				header.numSegs1.location +
				", " + header.numSegs1.size);
	System.out.println("numSegs2:             " +  
				header.numSegs2.location +
				", " + header.numSegs2.size);

	}

    protected Header() {
    }

    protected Header( RandomAccessFile runfile, String iName ) 
	throws IOException {
	this( runfile );
	if ( versionNumber < 5 ) {
	    this.iName = iName;
	    if ( (this.iName).equalsIgnoreCase( "glad") || 
		 (this.iName).equalsIgnoreCase( "lpsd") ) {
		int nLpsdCrates = nDet/8192 + 1;
		this.numOfElements = this.nDet;
		//		this.nDet = (short)((nDet - (nLpsdCrates -1) * 8192)/64 + 
		//		    (nLpsdCrates - 1) * 80);
		LpsdDetIdMap lpsdDetIdMap = new LpsdDetIdMap( runfile, this);
		int numDets = 0;
		for ( int ii = 0; ii < lpsdDetIdMap.NumOfBanks(); ii++ ) {
		    numDets += lpsdDetIdMap.DetsInBank( ii ).length;
		}
	        this.nDet = (short)numDets;
	    }
	    else {
		this.numOfElements = this.nDet;
	    }
	}
		
    }

    protected Header(RandomAccessFile runfile) throws IOException{
	int i;
	long filePosition;
	filePosition = runfile.getFilePointer();

	runfile.seek(68);
	int vers = runfile.readInt();
	//	System.out.println( "Version: " + vers );
	
	System.out.println( "Version " + vers + " file" );
	if ( vers > 16777215 ) {       // Version < 4 was little endian
	    LoadV4(runfile);
	}
	else {
	    LoadV5(runfile);
	}
	
	runfile.seek(filePosition);
	}


    void LoadV4(RandomAccessFile runfile) throws IOException {
	int i;

	runfile.seek(0);  
        controlParameter.location = readUnsignedInteger(runfile, 4);
        controlParameter.size = readUnsignedInteger(runfile, 4);
        detectorMapTable.location = readUnsignedInteger(runfile, 4);
        detectorMapTable.size = readUnsignedInteger(runfile, 4);
        timeFieldTable.location = readUnsignedInteger(runfile, 4);
        timeFieldTable.size = readUnsignedInteger(runfile, 4);
        timeScaleTable.location = readUnsignedInteger(runfile, 4);
        timeScaleTable.size = readUnsignedInteger(runfile, 4);
        timeShiftTable.location = readUnsignedInteger(runfile, 4);
        timeShiftTable.size = readUnsignedInteger(runfile, 4);
        areaStartTable.location = readUnsignedInteger(runfile, 4);
        areaStartTable.size = readUnsignedInteger(runfile, 4);
        timeDelayTable.location = readUnsignedInteger(runfile, 4);
        timeDelayTable.size = readUnsignedInteger(runfile, 4);
	histStartAddress = readUnsignedInteger(runfile, 4);
	numOfBlocks = readUnsignedInteger(runfile, 4);
	offsetToFree = readUnsignedInteger(runfile, 4);
	versionNumber = readUnsignedInteger(runfile, 4);
        detectorAngle.location = readUnsignedInteger(runfile, 4);
        detectorAngle.size = readUnsignedInteger(runfile, 4);
        flightPath.location = readUnsignedInteger(runfile, 4);
        flightPath.size = readUnsignedInteger(runfile, 4);
        detectorHeight.location = readUnsignedInteger(runfile, 4);
        detectorHeight.size = readUnsignedInteger(runfile, 4);
        detectorType.location = readUnsignedInteger(runfile, 4);
        detectorType.size = readUnsignedInteger(runfile, 4);
        controlTable.location = readUnsignedInteger(runfile, 4);
        controlTable.size = readUnsignedInteger(runfile, 4);
        seqHistWidth.location = readUnsignedInteger(runfile, 4);
        seqHistWidth.size = readUnsignedInteger(runfile, 4);
	nDet = (short)readUnsignedInteger(runfile, 2);
	StringBuffer tempUserName = new StringBuffer(20);
	for (i=0; i < 20; i++){
		tempUserName.append((char)runfile.readByte());
		}
	userName = tempUserName.toString();
	StringBuffer tempRunTitle = new StringBuffer(80);
	for (i=0; i < 80; i++){
		tempRunTitle.append((char)runfile.readByte());
		}
	runTitle = tempRunTitle.toString();
	if (versionNumber >= 3)
	  runNum = readUnsignedInteger(runfile, 4);
	else{
	  StringBuffer tempRunNum = new StringBuffer(4);
	  for (i=0; i < 4; i++){
		tempRunNum.append((char)runfile.readByte());
		}
	  if ( tempRunNum.toString() != null )
	  runNum = (int)Integer.parseInt(tempRunNum.toString());
	  }
	if (versionNumber >= 2) {
	  nextRun = readUnsignedInteger(runfile, 4);
	}
	else{
	  StringBuffer tempNextRun = new StringBuffer(4);
	  for (i=0; i < 3; i++){
		tempNextRun.append((char)runfile.readByte());
		}
	  //	  System.out.println( "nextrun"+tempNextRun+"MMMM");
	  if ( tempNextRun.toString() != null )
	  nextRun = (int)Integer.parseInt(tempNextRun.toString());
  	  }
	StringBuffer tempStartDate = new StringBuffer(9);
	for (i=0; i < 9; i++){
		tempStartDate.append((char)runfile.readByte());
		}
	startDate = tempStartDate.toString();
	StringBuffer tempStartTime = new StringBuffer(8);
	for (i=0; i < 8; i++){
		tempStartTime.append((char)runfile.readByte());
		}
	startTime = tempStartTime.toString();
	StringBuffer tempEndDate = new StringBuffer(9);
	for (i=0; i < 9; i++){
		tempEndDate.append((char)runfile.readByte());
		}
	endDate = tempEndDate.toString();
	StringBuffer tempEndTime = new StringBuffer(8);
	for (i=0; i < 8; i++){
		tempEndTime.append((char)runfile.readByte());
		}
	endTime = tempEndTime.toString();
	protStatus = (char)runfile.readByte();
	varToMonitor = (char)runfile.readByte();
	
	presetMonitorCounts = readUnsignedInteger(runfile, 4);
	elapsedMonitorCounts = readUnsignedInteger(runfile, 4);
	numOfCyclesPreset = (short)readUnsignedInteger(runfile, 2);
	numOfCyclesCompleted = (short)readUnsignedInteger(runfile, 2);
	if (versionNumber >= 2)
	  runAfterFinished = readUnsignedInteger(runfile, 4);
	else{
	  StringBuffer tempRunAfter = new StringBuffer(4);
	  for (i=0; i < 4; i++){
		tempRunAfter.append((char)runfile.readByte());
		}
	  if ( tempRunAfter.toString() != null )
	  runAfterFinished = (int)Integer.parseInt(tempRunAfter.toString());
	  }

	totalMonitorCounts = readUnsignedInteger(runfile, 4);

	if (versionNumber >= 3)
	  detCalibFile = readUnsignedInteger(runfile, 4);
	else{
	  StringBuffer tempdCalib = new StringBuffer(4);
	  for (i=0; i < 4; i++){
		tempdCalib.append((char)runfile.readByte());
		}
	  detCalibFile = Integer.parseInt(tempdCalib.toString());
	  }
	detLocUnit = (char)runfile.readByte();
 	pseudoTimeUnit = (char)runfile.readByte();
	runfile.seek(292);  
	sourceToSample = ReadVAXReal4(runfile);
	sourceToChopper = ReadVAXReal4(runfile);
	if (versionNumber >= 3)
	  moderatorCalibFile = readUnsignedInteger(runfile, 4);
	else{
	  StringBuffer tempmCalib = new StringBuffer(4);
	  for (i=0; i < 4; i++){
		tempmCalib.append((char)runfile.readByte());
		}
	  moderatorCalibFile = Integer.parseInt(tempmCalib.toString());
	  }

	groupToMonitor = (short)readUnsignedInteger(runfile, 2);
	channelToMonitor = (short)readUnsignedInteger(runfile, 2);
	numOfHistograms = (short)readUnsignedInteger(runfile, 2);
	numOfTimeFields = (short)readUnsignedInteger(runfile, 2);
	numOfControl = (short)readUnsignedInteger(runfile, 2);
	controlFlag = (short)readUnsignedInteger(runfile, 2);
	clockShift = (short)readUnsignedInteger(runfile, 2);
	totalChannels = readUnsignedInteger(runfile, 4);
	numOfPulses = readUnsignedInteger(runfile, 4);
	sizeOfDataArea = readUnsignedInteger(runfile, 4);
	hardwareTMin = readUnsignedInteger(runfile, 4);
	hardwareTMax = readUnsignedInteger(runfile, 4);
	hardTimeDelay = readUnsignedInteger(runfile, 4);
	numOfX = (short)readUnsignedInteger(runfile, 2);
	numOfY = (short)readUnsignedInteger(runfile, 2);
	numOfWavelengths = (short)readUnsignedInteger(runfile, 2);
	maxWavelength = readUnsignedInteger(runfile, 4);
	minWavelength = readUnsignedInteger(runfile, 4);
	dta = ReadVAXReal4(runfile);
	dtd = ReadVAXReal4(runfile);
	omega = ReadVAXReal4(runfile);
	chi = ReadVAXReal4(runfile);
	phi = ReadVAXReal4(runfile);
	xLeft = ReadVAXReal4(runfile);
	xRight = ReadVAXReal4(runfile);
	yLower = ReadVAXReal4(runfile);
	yUpper = ReadVAXReal4(runfile);
	xDisplacement = ReadVAXReal4(runfile);
	yDisplacement = ReadVAXReal4(runfile);
	xLength = ReadVAXReal4(runfile);
	areaChannelWidth = (short)readUnsignedInteger(runfile, 2);
	areaDoubleInterval = (short)readUnsignedInteger(runfile, 2);
	addressOf1DData = readUnsignedInteger(runfile, 4);
	addressOf2DData = readUnsignedInteger(runfile, 4);
	endOfOverflow = readUnsignedInteger(runfile, 4);
	channels1D = readUnsignedInteger(runfile, 4);
	numOfOverflows = (short)readUnsignedInteger(runfile, 2);
	clockPeriod = ReadVAXReal4(runfile);
	runfile.seek(462);
	energyIn = ReadVAXReal4(runfile);
	energyOut = ReadVAXReal4(runfile);
	numOfSeqHist = (short)readUnsignedInteger(runfile, 2);
	protonCurrent = ReadVAXReal4(runfile);
	areaBinning = (short)readUnsignedInteger(runfile, 2);
	microprocessor = (short)readUnsignedInteger(runfile, 2);
	numOfLockouts = (short)readUnsignedInteger(runfile, 2);
	firstOverflow = readUnsignedInteger(runfile, 4);
	if (versionNumber >= 3){
	  expNum = readUnsignedInteger(runfile, 4);
	  firstRun = readUnsignedInteger(runfile, 4);
	  lastRun = readUnsignedInteger(runfile, 4);
	  samplePos = (short)readUnsignedInteger(runfile, 2);
	  defaultRun = readUnsignedInteger(runfile, 4);
	  }
	else{
	  StringBuffer tempExpNum = new StringBuffer(4);
	  for (i=0; i < 4; i++){
	      byte temp = runfile.readByte();
	      if ( temp != 0 ){ 
		  tempExpNum.append((char)temp);
	      }
	      else {
		  tempExpNum.append("0");
	      }
	  }
	  if ( tempExpNum.toString() != null && 
	       (tempExpNum.toString()).length() != 0 ) {
	     expNum = Integer.parseInt(tempExpNum.toString().trim());
	  }
	  else {
	      expNum = 0;
	  }
	  StringBuffer tempFirstRun = new StringBuffer(4);
	  for (i=0; i < 4; i++){
	      byte temp = runfile.readByte();
	      if ( temp != 0 ){ 
		  tempFirstRun.append((char)temp);
	      }
	      else {
		  tempFirstRun.append("0");
	      }
	      //		tempFirstRun.append((char)runfile.readByte());
	  }
	  if ( tempFirstRun.toString() != null )
	     firstRun = Integer.parseInt(tempFirstRun.toString());
	  StringBuffer tempLastRun = new StringBuffer(4);
	  for (i=0; i < 4; i++){
	      byte temp = runfile.readByte();
	      if ( temp != 0 ){ 
		  tempLastRun.append((char)temp);
	      }
	      else {
		  tempLastRun.append("0");
	      }
	      //		tempLastRun.append((char)runfile.readByte());
	  }
	  if ( tempLastRun.toString() != null )
	     lastRun = Integer.parseInt(tempLastRun.toString());
	  samplePos = (short)readUnsignedInteger(runfile, 2);
	  StringBuffer tempDefaultRun = new StringBuffer(4);
	  for (i=0; i < 4; i++){
	      byte temp = runfile.readByte();
	      if ( temp != 0 ){ 
		  tempDefaultRun.append((char)temp);
	      }
	      else {
		  tempDefaultRun.append("0");
	      }
	      //		tempDefaultRun.append((char)runfile.readByte());
		}
	  if ( tempDefaultRun.toString() != null )
	     defaultRun = Integer.parseInt(tempDefaultRun.toString());
	  }
	numOfHeadBlocks = (short)readUnsignedInteger(runfile, 2);
	overflowSort = (short)readUnsignedInteger(runfile, 2);
	runfile.seek(512);
	messageRegion.location =readUnsignedInteger(runfile, 4);
	messageRegion.size =readUnsignedInteger(runfile, 4);
	discSettings.location =readUnsignedInteger(runfile, 4);
	discSettings.size =readUnsignedInteger(runfile, 4);
	PSD_IDMap.location =readUnsignedInteger(runfile, 4);
	PSD_IDMap.size =readUnsignedInteger(runfile, 4);
	if (versionNumber < 4 ) {
		standardClock = 0.125;
		lpsdClock = 0.5;

		
		}
	else {
		runfile.seek(632);
		standardClock = ReadVAXReal4(runfile);
		lpsdClock = ReadVAXReal4(runfile);
		}


    }

    void LoadV5(RandomAccessFile runfile ) throws IOException {
		runfile.seek(0);
		controlParameter.location = runfile.readInt();
		controlParameter.size = runfile.readInt();
		detectorMapTable.location = runfile.readInt();
		detectorMapTable.size = runfile.readInt();
		timeFieldTable.location = runfile.readInt();
		timeFieldTable.size = runfile.readInt();
		timeScaleTable.location = runfile.readInt();
		timeScaleTable.size = runfile.readInt( );
		timeShiftTable.location = runfile.readInt();
		timeShiftTable.size = runfile.readInt();
		areaStartTable.location = runfile.readInt();
		areaStartTable.size = runfile.readInt();
		timeDelayTable.location = runfile.readInt();
		timeDelayTable.size = runfile.readInt( );
		histStartAddress = runfile.readInt( );
		numOfBlocks = runfile.readInt( );
		offsetToFree = runfile.readInt( );
		versionNumber = runfile.readInt( );
		detectorAngle.location = runfile.readInt( );
		detectorAngle.size = runfile.readInt( );
		flightPath.location = runfile.readInt( );
		flightPath.size = runfile.readInt( );
		detectorHeight.location = runfile.readInt( );
		detectorHeight.size = runfile.readInt( );
		detectorType.location = runfile.readInt( );
		detectorType.size = runfile.readInt( );
		controlTable.location = runfile.readInt( );
		controlTable.size = runfile.readInt( );
		seqHistWidth.location = runfile.readInt( );
		seqHistWidth.size = runfile.readInt( );
		nDet = runfile.readShort( );
		byte[] temp = new byte[20];
		runfile.read(temp, 0, 20);
		userName = new String(temp);
		temp = new byte[80];
		runfile.read(temp, 0, 80);
		runTitle = new String(temp);
		runNum = runfile.readInt( );
		nextRun = runfile.readInt();
		temp = new byte[9];
		runfile.read(temp, 0, 9);
		startDate = new String(temp);
		temp = new byte[8];
		runfile.read(temp, 0, 8);
		startTime = new String(temp);
		temp = new byte[9];
		runfile.read(temp, 0, 9);
		endDate = new String(temp);
		temp = new byte[8];
		runfile.read(temp, 0, 8);
		endTime = new String(temp);
		protStatus = (char)runfile.readByte();
		varToMonitor = (char)runfile.readByte();
		presetMonitorCounts = runfile.readInt( );
		elapsedMonitorCounts = runfile.readInt( );
		numOfCyclesPreset = runfile.readShort( );
		numOfCyclesCompleted = runfile.readShort(  );
		runAfterFinished = runfile.readInt( );
		totalMonitorCounts = runfile.readInt( );
		detCalibFile = runfile.readInt( );
		detLocUnit = (char)runfile.readByte( );
		pseudoTimeUnit = (char)runfile.readByte( );
		runfile.seek(292);
		sourceToSample = runfile.readFloat( );
		sourceToChopper = runfile.readFloat( );
		moderatorCalibFile = runfile.readInt( );
		groupToMonitor = runfile.readShort( );
		channelToMonitor = runfile.readShort( );
		numOfHistograms = runfile.readShort( );
		numOfTimeFields = runfile.readShort( );
		numOfControl = runfile.readShort( );
		controlFlag = runfile.readShort( );
		clockShift = runfile.readShort( );
 		totalChannels = runfile.readInt( );
		numOfPulses = runfile.readInt( );
		sizeOfDataArea = runfile.readInt( );
		hardwareTMin = runfile.readInt( );
		hardwareTMax = runfile.readInt( );
		hardTimeDelay = runfile.readInt( );
		numOfX = runfile.readShort( );
		numOfY = runfile.readShort( );
		numOfWavelengths = runfile.readShort( );
		maxWavelength = runfile.readInt( );
		minWavelength = runfile.readInt( );
		dta = (double)runfile.readFloat();
		dtd = (double)runfile.readFloat();
		omega = (double)runfile.readFloat();
		chi = (double)runfile.readFloat();
		phi = (double)runfile.readFloat();
		xLeft = (double)runfile.readFloat();
		xRight = (double)runfile.readFloat();
		yLower = (double)runfile.readFloat();
		yUpper = (double)runfile.readFloat();
		xDisplacement = (double)runfile.readFloat();
		yDisplacement = (double)runfile.readFloat();
		xLength = (double)runfile.readFloat();
		areaChannelWidth = runfile.readShort( );
		areaDoubleInterval = runfile.readShort( );
		addressOf1DData = runfile.readInt( );
		addressOf2DData = runfile.readInt( );
		endOfOverflow = runfile.readInt( );
		channels1D = runfile.readInt( );
		numOfOverflows = runfile.readShort( );
		clockPeriod = (double)runfile.readFloat();
		runfile.seek(462);
		energyIn = (double)runfile.readFloat();
		energyOut = (double)runfile.readFloat();
		numOfSeqHist = runfile.readShort();
		protonCurrent = (double)runfile.readFloat();
		areaBinning = runfile.readShort(  );
		microprocessor = runfile.readShort( );
		numOfLockouts = runfile.readShort( );
		firstOverflow = runfile.readInt( );
		expNum = runfile.readInt( );
		firstRun = runfile.readInt( );
		lastRun = runfile.readInt( );
		samplePos = runfile.readShort( );
		defaultRun = runfile.readInt( );
		numOfHeadBlocks = runfile.readShort( );
		overflowSort = runfile.readShort( );
		runfile.seek( 512 );
		messageRegion.location = runfile.readInt( );
		messageRegion.size =  runfile.readInt( );
		discSettings.location = runfile.readInt( );
		discSettings.size = runfile.readInt( );
		PSD_IDMap.location = runfile.readInt( );
		PSD_IDMap.size = runfile.readInt( );
		lpsdStartTable.location = runfile.readInt();
		lpsdStartTable.size = runfile.readInt();
		detectorStartTable.location = runfile.readInt();
		detectorStartTable.size = runfile.readInt();
		lpsdAngle.location = runfile.readInt();
		lpsdAngle.size = runfile.readInt();
		lpsdFlightPath.location = runfile.readInt();
		lpsdFlightPath.size = runfile.readInt();
		lpsdHeight.location = runfile.readInt();
		lpsdHeight.size = runfile.readInt();
		lpsdType.location = runfile.readInt();
		lpsdType.size = runfile.readInt();
		lpsdLength.location = runfile.readInt();
		lpsdLength.size = runfile.readInt();
		lpsdWidth.location = runfile.readInt();
		lpsdWidth.size = runfile.readInt();
		lpsdDepth.location = runfile.readInt();
		lpsdDepth.size = runfile.readInt();
		runfile.seek(632);
		standardClock = (double)runfile.readFloat();
		lpsdClock = (double)runfile.readFloat();
		//		numOfLpsds = runfile.readInt();
		runfile.seek(700);
		detectorLength.location = runfile.readInt();
		detectorLength.size = runfile.readInt();
		detectorWidth.location = runfile.readInt();
		detectorWidth.size = runfile.readInt();
		detectorDepth.location = runfile.readInt();
		detectorDepth.size = runfile.readInt();
		temp = new byte[4];
		runfile.read(temp, 0, 4);
		iName = new String(temp);
		detectorSGMap.location = runfile.readInt();
		detectorSGMap.size = runfile.readInt();
		detCoordSys.location = runfile.readInt();
		detCoordSys.size = runfile.readInt();
		detectorRot1.location = runfile.readInt();
		detectorRot1.size = runfile.readInt();
		detectorRot2.location = runfile.readInt();
		detectorRot2.size = runfile.readInt();
		detectorEfficiency.location = runfile.readInt();
		detectorEfficiency.size = runfile.readInt();
		psdOrder.location = runfile.readInt();
		psdOrder.size = runfile.readInt();
		numSegs1.location = runfile.readInt();
		numSegs1.size = runfile.readInt();
		numSegs2.location = runfile.readInt();
		numSegs2.size = runfile.readInt();
		crateNum.location = runfile.readInt();
		crateNum.size = runfile.readInt();
		slotNum.location = runfile.readInt();
		slotNum.size = runfile.readInt();
		inputNum.location = runfile.readInt();
		inputNum.size = runfile.readInt();
		dataSource.location = runfile.readInt();
		dataSource.size = runfile.readInt();
		minID.location = runfile.readInt();
		minID.size = runfile.readInt();
		
    }

    public void Write( RandomAccessFile runfile ) {
		try {
		runfile.seek(0);
		runfile.writeInt( controlParameter.location );
		runfile.writeInt( controlParameter.size );
		runfile.writeInt( detectorMapTable.location );
		runfile.writeInt( detectorMapTable.size );
		runfile.writeInt( timeFieldTable.location );
		runfile.writeInt( timeFieldTable.size );
		runfile.writeInt( timeScaleTable.location );
		runfile.writeInt( timeScaleTable.size );
		runfile.writeInt( timeShiftTable.location );
		runfile.writeInt( timeShiftTable.size );
		runfile.writeInt( areaStartTable.location );
		runfile.writeInt( areaStartTable.size );
		runfile.writeInt( timeDelayTable.location );
		runfile.writeInt( timeDelayTable.size );
		runfile.writeInt( histStartAddress );
		runfile.writeInt( numOfBlocks );
		runfile.writeInt( offsetToFree );
		runfile.writeInt( versionNumber );
		runfile.writeInt( detectorAngle.location );
		runfile.writeInt( detectorAngle.size );
		runfile.writeInt( flightPath.location );
		runfile.writeInt( flightPath.size );
		runfile.writeInt( detectorHeight.location );
		runfile.writeInt( detectorHeight.size );
		runfile.writeInt( detectorType.location );
		runfile.writeInt( detectorType.size );
		runfile.writeInt( controlTable.location );
		runfile.writeInt( controlTable.size );
		runfile.writeInt( seqHistWidth.location );
		runfile.writeInt( seqHistWidth.size );
		runfile.writeShort( nDet );
 		runfile.writeBytes( userName );
		runfile.writeBytes( runTitle );
		runfile.writeInt( runNum );
		runfile.writeInt(nextRun );
		runfile.writeBytes( startDate );
		runfile.writeBytes( startTime );
		runfile.writeBytes( endDate );
		runfile.writeBytes( endTime );
		runfile.writeBytes( String.valueOf(protStatus) );
		runfile.writeBytes( String.valueOf(varToMonitor) );
		runfile.writeInt( presetMonitorCounts );
		runfile.writeInt( elapsedMonitorCounts );
		runfile.writeShort( numOfCyclesPreset );
		runfile.writeShort( numOfCyclesCompleted );
		runfile.writeInt( runAfterFinished );
		runfile.writeInt( totalMonitorCounts );
		runfile.writeInt( detCalibFile );
		runfile.writeBytes( String.valueOf(detLocUnit) );
		runfile.writeBytes( String.valueOf(pseudoTimeUnit) );
		runfile.seek(292);
		runfile.writeFloat( (float)sourceToSample );
		runfile.writeFloat( (float)sourceToChopper );
		runfile.writeInt( moderatorCalibFile );
		runfile.writeShort( groupToMonitor );
		runfile.writeShort( channelToMonitor );
		runfile.writeShort( numOfHistograms );
		runfile.writeShort( numOfTimeFields );
		runfile.writeShort( numOfControl );
		runfile.writeShort( controlFlag );
		runfile.writeShort( clockShift );
		runfile.writeInt( totalChannels );
		runfile.writeInt( numOfPulses );
		runfile.writeInt( sizeOfDataArea );
		runfile.writeInt( hardwareTMin );
		runfile.writeInt( hardwareTMax );
		runfile.writeInt( hardTimeDelay );
		runfile.writeShort( numOfX );
		runfile.writeShort( numOfY );
		runfile.writeShort( numOfWavelengths );
		runfile.writeInt( maxWavelength );
		runfile.writeInt( minWavelength );
		runfile.writeFloat( (float)dta );
		runfile.writeFloat( (float)dtd );
		runfile.writeFloat( (float)omega );
		runfile.writeFloat( (float)chi );
		runfile.writeFloat( (float)phi );
		runfile.writeFloat( (float)xLeft );
		runfile.writeFloat( (float)xRight );
		runfile.writeFloat( (float)yLower );
		runfile.writeFloat( (float)yUpper );
		runfile.writeFloat( (float)xDisplacement );
		runfile.writeFloat( (float)yDisplacement );
		runfile.writeFloat( (float)xLength );
		runfile.writeShort( areaChannelWidth );
		runfile.writeShort( areaDoubleInterval );
		runfile.writeInt( addressOf1DData );
		runfile.writeInt( addressOf2DData );
		runfile.writeInt( endOfOverflow );
		runfile.writeInt( channels1D );
		runfile.writeShort( numOfOverflows );
		runfile.writeFloat( (float)clockPeriod );
		runfile.seek(462);
		runfile.writeFloat( (float)energyIn );
		runfile.writeFloat( (float)energyOut );
		runfile.writeShort( numOfSeqHist );
		runfile.writeFloat( (float)protonCurrent );
		runfile.writeShort( areaBinning );
		runfile.writeShort( microprocessor );
		runfile.writeShort( numOfLockouts );
		runfile.writeInt( firstOverflow );
		runfile.writeInt( expNum );
		runfile.writeInt( firstRun );
		runfile.writeInt( lastRun );
		runfile.writeShort( samplePos );
		runfile.writeInt( defaultRun );
		runfile.writeShort( numOfHeadBlocks );
		runfile.writeShort( overflowSort );
		runfile.seek( 512 );
		runfile.writeInt( messageRegion.location );
		runfile.writeInt( messageRegion.size );
		runfile.writeInt( discSettings.location );
		runfile.writeInt( discSettings.size );
		runfile.writeInt( PSD_IDMap.location );
		runfile.writeInt( PSD_IDMap.size );
		runfile.writeInt( lpsdStartTable.location );
		runfile.writeInt( lpsdStartTable.size );
		runfile.writeInt( detectorStartTable.location );
		runfile.writeInt( detectorStartTable.size );
		runfile.writeInt( lpsdAngle.location );
		runfile.writeInt( lpsdAngle.size );
		runfile.writeInt( lpsdFlightPath.location );
		runfile.writeInt( lpsdFlightPath.size );
		runfile.writeInt( lpsdHeight.location );
		runfile.writeInt( lpsdHeight.size );
		runfile.writeInt( lpsdType.location );
		runfile.writeInt( lpsdType.size );
		runfile.writeInt( lpsdLength.location );
		runfile.writeInt( lpsdLength.size );
		runfile.writeInt( lpsdWidth.location );
		runfile.writeInt( lpsdWidth.size );
		runfile.writeInt( lpsdDepth.location );
		runfile.writeInt( lpsdDepth.size );
		runfile.seek(632);
		runfile.writeFloat( (float)standardClock );
		runfile.writeFloat( (float)lpsdClock );
		runfile.seek(700);
		runfile.writeInt( detectorLength.location );
		runfile.writeInt( detectorLength.size );
		runfile.writeInt( detectorWidth.location );
		runfile.writeInt( detectorWidth.size );
		runfile.writeInt( detectorDepth.location );
		runfile.writeInt( detectorDepth.size );
		runfile.writeBytes( iName.substring( 0, 4 ) );
		runfile.writeInt( detectorSGMap.location );
		runfile.writeInt( detectorSGMap.size );
		runfile.writeInt( detCoordSys.location );
		runfile.writeInt( detCoordSys.size );
		runfile.writeInt( detectorRot1.location );
		runfile.writeInt( detectorRot1.size );
		runfile.writeInt( detectorRot2.location );
		runfile.writeInt( detectorRot2.size );
		runfile.writeInt( detectorEfficiency.location );
		runfile.writeInt( detectorEfficiency.size );
		runfile.writeInt( psdOrder.location );
		runfile.writeInt( psdOrder.size );
		runfile.writeInt( numSegs1.location );
		runfile.writeInt( numSegs1.size );
		runfile.writeInt( numSegs2.location );
		runfile.writeInt( numSegs2.size );
		runfile.writeInt( crateNum.location );
		runfile.writeInt( crateNum.size );
		runfile.writeInt( slotNum.location );
		runfile.writeInt( slotNum.size );
		runfile.writeInt( inputNum.location );
		runfile.writeInt( inputNum.size );
		runfile.writeInt( dataSource.location );
		runfile.writeInt( dataSource.size );
		runfile.writeInt( minID.location );
		runfile.writeInt( minID.size );
		runfile.seek(1532);
		runfile.writeInt( (int)0 );
		
		
	}
	catch (IOException ex) {
  	}
    }

    public Object clone() {
	try {
	    Header copy = (Header)super.clone();

	    return copy;

	} catch (CloneNotSupportedException ex ) { 
	    throw new Error ( "Error Cloning Header Object" );
	}
    }

}
