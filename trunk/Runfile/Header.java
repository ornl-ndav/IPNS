package IPNS.Runfile;

//import.RandomAccessRunfile; 
import java.io.IOException;
import java.io.DataInputStream; 
import java.lang.Math;

/**
This class is a utility class for the IPNS.Runfile package.  Access to the
member variables is allowed only to members of this package.  It allows 
a logical separation for information in the two block run file header.

*/
/*
 *
 * $Log$
 * Revision 5.24  2003/03/30 04:12:53  hammonds
 * Switch reading to use the new RunfileInputStream and RandomAccessRunfile so that changes can be made to offload the differences in data types.
 *
 * Further modifications have been made here to take advantage of some of the changes in these classes.
 *
 * Revision 5.23  2002/10/28 22:57:30  hammonds
 * Speed/memory improvements.  This is from change of code structure.
 *
 * Revision 5.22  2002/08/05 14:50:52  hammonds
 * Changed SCD detector angle from 90 to -90 in V3 or earlier files
 * Fixed problem loading POSY and PNE0 files
 *
 * Revision 5.21  2002/07/02 14:25:19  hammonds
 * Took out a  diagnostic print line.
 *
 * Revision 5.20  2002/01/08 19:57:00  hammonds
 * Added code to support the following parameters:
 * 	filterType
 * 	sampleEnv
 * 	detectorConfig
 * 	runType
 *
 * Revision 5.19  2002/01/03 19:48:40  hammonds
 * Added instrument type to runfile header
 *
 * Revision 5.18  2001/12/20 21:45:33  hammonds
 * Added change for setting number of detectors/segments for POSY1&2
 *
 * Revision 5.17  2001/11/26 15:19:29  hammonds
 * Added printStackTrace in caught exceptions.
 *
 * Revision 5.16  2001/11/02 16:33:01  hammonds
 * Take out name extracted from filename in main.
 *
 * Revision 5.15  2001/10/31 22:03:01  hammonds
 * Added a rewrite method to allow modifications to header parameters.
 *
 * Revision 5.14  2001/08/03 19:05:09  hammonds
 * Added reading the number of elements in a file.
 *
 * Revision 5.13  2001/07/24 16:04:08  hammonds
 * Changes in some of the set methods.
 *
 * Revision 5.12  2001/07/24 14:57:19  hammonds
 * Fixes to methods that set header parameters.
 *
 * Revision 5.11  2001/07/20 21:41:01  hammonds
 * Fixed some problems with setting usermname and runtitle
 *
 * Revision 5.10  2001/07/20 21:34:30  hammonds
 * Made changes to default username and runtitle so that everything writes OK without other setups.
 *
 * Revision 5.9  2001/07/20 20:03:56  hammonds
 * Added methods for setting the values in the Header from the Runfile.  Setting these involves.  Also made some small changes that will allow a runfile with a header to be constructed from the main method of RunfileBuilder.
 *
 * Revision 5.8  2001/07/18 18:41:46  hammonds
 * Added a request for debug messages to be printed.  Added extra output in the main method.
 *
 * Revision 5.7  2001/06/27 20:43:04  hammonds
 * Added code to allow for area detector data.
 *
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
    protected int versionNumber = 5;
    protected TableType detectorAngle = new TableType();
    protected TableType flightPath = new TableType();
    protected TableType detectorHeight = new TableType();
    protected TableType detectorType = new TableType();
    protected TableType controlTable = new TableType();
    protected TableType seqHistWidth = new TableType();
    protected short nDet;
    protected String userName = new String("          " +"          " );
    protected String runTitle = new String("          "+"          "+
					   "          "+"          "+
					   "          "+"          "+
					   "          "+"          ");
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
    protected String iName = new String("    ");
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
    int instrumentType = 0;
    int filterType = 0;
    int sampleEnv = 0;
    int detectorConfig = 0;
    int runType = 0;


    // --------------------------- readUnsignedInteger -------------------

      protected int readUnsignedInteger(DataInputStream inFile,
				      int length) throws IOException {

	int zero=0;
	byte b[] = new byte[length];
	int c[] = new int[length];
	int nBytesRead = inFile.read(b, zero, length);
	int num = zero;
	int i;
	int tfs = 256;
	c[0] = b[0];
	c[1] = b[1];
	if ( c[0] < zero) c[0] += tfs;
	if ( c[1] < zero) c[1] += tfs;
	num += c[0];
	num += (c[1] << 8);
	if ( length == 4 ) {
	    c[2] = b[2];
	    c[3] = b[3];
	    if ( c[2] < zero) c[2] += tfs;
	    if ( c[3] < zero) c[3] += tfs;
	    num += (c[2] << 16);
	    num += (c[3] << 24);
	}
	return num;
    }
  
    // --------------------------- readUnsignedInteger -------------------
  /*  
     protected int readUnsignedInteger(RandomAccessRunfile inFile,
				      int length) throws IOException {

	int zero=0;
	byte b[] = new byte[length];
	int c[] = new int[length];
	int nBytesRead = inFile.read(b, zero, length);
	int num = zero;
	int i;
	int tfs = 256;
	c[0] = b[0];
	c[1] = b[1];
	if ( c[0] < zero) c[0] += tfs;
	if ( c[1] < zero) c[1] += tfs;
	num += c[0];
	num += (c[1] << 8);
	if ( length == 4 ) {
	    c[2] = b[2];
	    c[3] = b[3];
	    if ( c[2] < zero) c[2] += tfs;
	    if ( c[3] < zero) c[3] += tfs;
	    num += (c[2] << 16);
	    num += (c[3] << 24);
	}
	return num;
		}
  */  
    // --------------------------- readUnsignedLong -------------------
  /*    
    protected long readUnsignedLong(RandomAccessRunfile inFile,
				    int length) throws IOException {
	
	byte b[] = new byte[length];
	int c[] = new int[length];
	int nBytesRead = inFile.read(b, 0, length);
	long num = 0;
	int i;
	c[0] = b[0];
	c[1] = b[1];
	if ( c[0] < 0) c[0]+=256;
	if ( c[1] < 0) c[1]+=256;
	num += c[0];
	num += c[1] << 8;
	if ( length == 4 ) {
	    c[2] = b[2];
	    c[3] = b[3];
	    if ( c[2] < 0) c[2]+=256;
	    if ( c[3] < 0) c[3]+=256;
	    num += c[2] << 16;
	    num += c[3] << 24;
	}
	return num;
    }
  */
    // ---------------------------- ReadVAXReal4 ------------------------

  //    protected double ReadVAXReal4(RandomAccessRunfile inFile)
  //throws IOException {

  //    int length = 4;
  //    long hi_mant, low_mant, exp, sign;
  //    double f_val;
  //    long val = (long )readUnsignedInteger(inFile, length);
  //    if (val < 0) {
  //    val = val + 4294967296L;
  //    /*	    val = val + (long)Math.pow(2.0, 32.0);*/
  //}
  ///* add 128 to put in the implied 1 */
  //    hi_mant  = (val & 127) + 128;
  //    val      = val >> 7;
  ///* exponent is "excess 128" */
  //    exp      = ((int)(val & 255)) - 128;
  //    val      = val >> 8;
  //
  //    sign     = val & 1;
  //    low_mant = val >> 1;
  ///* This could also be a "reserved" operand of some sort?*/
  //    if ( exp == -128 )
  //    f_val = 0;
  //    else
  //    f_val = ((hi_mant /256.0) + (low_mant/16777216.0)) *
  //	Math.pow(2.0, (double)exp );
  //
  //    if ( sign == 1 )
  //    f_val = -f_val;
  //    return f_val;
  //}


public static void main(String[] args) throws IOException{
    System.setProperty( "Runfile_Debug", "yes" );
	System.out.println("Runfile Name is " + args[0]);
	RandomAccessRunfile runfile = new RandomAccessRunfile(
		args[0], "r");
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
//	System.out.println( "iName: " + iName );
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
	System.out.println("dataSource:             " +  
				header.dataSource.location +
				", " + header.dataSource.size);
	System.out.println("minID:             " +  
				header.minID.location +
				", " + header.minID.size);
	System.out.println("instrumentType:                " +
			        header.instrumentType );
	System.out.println("filterType:                    " +
			        header.filterType );
	System.out.println("sampleEnv:                     " +
			        header.sampleEnv );
	System.out.println("detectorConfig:                " +
			        header.detectorConfig );
	System.out.println("runType:                       " +
			        header.runType );

	}

    protected Header() {
    }

    protected Header( RandomAccessRunfile runfile, String iName ) 
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
	    else if ( (this.iName).equalsIgnoreCase("scd0") ||
		      (this.iName).equalsIgnoreCase("sad0") ||
		      (this.iName).equalsIgnoreCase("sad1") ||
		      (this.iName).equalsIgnoreCase("posy") ||
		      (this.iName).equalsIgnoreCase("pne0") ||
		      (this.iName).equalsIgnoreCase("sand") )
	    {
		this.numOfElements = this.nDet + this.numOfX * this.numOfY;
		this.nDet = (short)(this.nDet + 1);
	    }
	    else {
		this.numOfElements = this.nDet;
	    }
	    if ( (this.iName).equalsIgnoreCase("scd0")) {
		this.dta = -dta;
	    }
	}
		
	if (versionNumber < 6 ) {
	    instrumentType = 
		InstrumentType.getIPNSInstType(this.iName);
	}
    }

    protected Header(RandomAccessRunfile runfile) throws IOException{
	int i;
	long filePosition;
	StringBuffer tempStrBuff = new StringBuffer();
	String tempStr = new String();
	filePosition = runfile.getFilePointer();

	runfile.seek(68);
	int vers = runfile.readInt();
	if ( (System.getProperty("Runfile_Debug", "no" ))
	     .equalsIgnoreCase("yes") ) {
	    System.out.println( "Version: " + vers );
	    System.out.println( "Version" + vers + " file" );
	}

	runfile.seek(0);
	controlParameter.location = runfile.readRunInt();
	controlParameter.size = runfile.readRunInt();
	detectorMapTable.location = runfile.readRunInt();
	detectorMapTable.size = runfile.readRunInt();
	timeFieldTable.location = runfile.readRunInt();
	timeFieldTable.size = runfile.readRunInt();
	timeScaleTable.location = runfile.readRunInt();
	timeScaleTable.size = runfile.readRunInt( );
	timeShiftTable.location = runfile.readRunInt();
	timeShiftTable.size = runfile.readRunInt();
	areaStartTable.location = runfile.readRunInt();
	areaStartTable.size = runfile.readRunInt();
	timeDelayTable.location = runfile.readRunInt();
	timeDelayTable.size = runfile.readRunInt( );
	histStartAddress = runfile.readRunInt( );
	numOfBlocks = runfile.readRunInt( );
	offsetToFree = runfile.readRunInt( );
	versionNumber = runfile.readRunInt( );
	detectorAngle.location = runfile.readRunInt( );
	detectorAngle.size = runfile.readRunInt( );
	flightPath.location = runfile.readRunInt( );
	flightPath.size = runfile.readRunInt( );
	detectorHeight.location = runfile.readRunInt( );
	detectorHeight.size = runfile.readRunInt( );
	detectorType.location = runfile.readRunInt( );
	detectorType.size = runfile.readRunInt( );
	controlTable.location = runfile.readRunInt( );
	controlTable.size = runfile.readRunInt( );
	seqHistWidth.location = runfile.readRunInt( );
	seqHistWidth.size = runfile.readRunInt( );
	nDet = runfile.readRunShort( );
	userName = runfile.readRunString(20);
	runTitle = runfile.readRunString(80);
	runNum = runfile.readRunFileNum();
	nextRun = runfile.readRunFileNum();
	startDate = runfile.readRunString(9);
	startTime = runfile.readRunString(8);
	endDate = runfile.readRunString(9);
	endTime = runfile.readRunString(8);
	protStatus = (char)runfile.readByte();
	varToMonitor = (char)runfile.readByte();
	presetMonitorCounts = runfile.readRunInt();
	elapsedMonitorCounts = runfile.readRunInt();
	numOfCyclesPreset = runfile.readRunShort( );
	numOfCyclesCompleted = runfile.readRunShort(  );
	runAfterFinished = runfile.readRunFileNum();
	totalMonitorCounts = runfile.readRunInt( );
	detCalibFile = runfile.readRunFileNum();
	detLocUnit = (char)runfile.readByte( );
	pseudoTimeUnit = (char)runfile.readByte( );
	runfile.seek(292);
	sourceToSample = runfile.readRunFloat( );
	sourceToChopper = runfile.readRunFloat( );
	moderatorCalibFile = runfile.readRunFileNum();
	groupToMonitor = runfile.readRunShort( );
	channelToMonitor = runfile.readRunShort( );
	numOfHistograms = runfile.readRunShort( );
	numOfTimeFields = runfile.readRunShort( );
	numOfControl = runfile.readRunShort( );
	controlFlag = runfile.readRunShort( );
	clockShift = runfile.readRunShort( );
	totalChannels = runfile.readRunInt( );
	numOfPulses = runfile.readRunInt( );
	sizeOfDataArea = runfile.readRunInt( );
	hardwareTMin = runfile.readRunInt( );
	hardwareTMax = runfile.readRunInt( );
	hardTimeDelay = runfile.readRunInt( );
	numOfX = runfile.readRunShort( );
	numOfY = runfile.readRunShort( );
	numOfWavelengths = runfile.readRunShort( );
	maxWavelength = runfile.readRunInt( );
	minWavelength = runfile.readRunInt( );
	dta = (double)runfile.readRunFloat();
	dtd = (double)runfile.readRunFloat();
	omega = (double)runfile.readRunFloat();
	chi = (double)runfile.readRunFloat();
	phi = (double)runfile.readRunFloat();
	xLeft = (double)runfile.readRunFloat();
	xRight = (double)runfile.readRunFloat();
	yLower = (double)runfile.readRunFloat();
	yUpper = (double)runfile.readRunFloat();
	xDisplacement = (double)runfile.readRunFloat();
	yDisplacement = (double)runfile.readRunFloat();
	xLength = (double)runfile.readRunFloat();
	areaChannelWidth = runfile.readRunShort( );
	areaDoubleInterval = runfile.readRunShort( );
	addressOf1DData = runfile.readRunInt( );
	addressOf2DData = runfile.readRunInt( );
	endOfOverflow = runfile.readRunInt( );
	channels1D = runfile.readRunInt( );
	numOfOverflows = runfile.readRunShort( );
	clockPeriod = (double)runfile.readRunFloat();
	runfile.seek(462);
	energyIn = (double)runfile.readRunFloat();
	energyOut = (double)runfile.readRunFloat();
	numOfSeqHist = runfile.readRunShort();
	protonCurrent = (double)runfile.readRunFloat();
	areaBinning = runfile.readRunShort(  );
	microprocessor = runfile.readRunShort( );
	numOfLockouts = runfile.readRunShort( );
	firstOverflow = runfile.readRunInt( );
	expNum = runfile.readRunFileNum();
	firstRun = runfile.readRunFileNum();
	lastRun = runfile.readRunFileNum();
	samplePos = (short)runfile.readRunShort();
	defaultRun = runfile.readRunFileNum();
	numOfHeadBlocks = runfile.readRunShort( );
	overflowSort = runfile.readRunShort( );
	runfile.seek( 512 );
	messageRegion.location = runfile.readRunInt( );
	messageRegion.size =  runfile.readRunInt( );
	discSettings.location = runfile.readRunInt( );
	discSettings.size = runfile.readRunInt( );
	PSD_IDMap.location = runfile.readRunInt( );
	PSD_IDMap.size = runfile.readRunInt( );

	if ( versionNumber < 5 ) {       // Version < 4 was little endian
	  if (versionNumber < 4 ) {      // old das on VAX
	    standardClock = 0.125;
	    lpsdClock = 0.5;
	  }
	  else {                         // new das w VAX
	    runfile.seek(632);
	    standardClock = runfile.readRunFloat();
	    lpsdClock = runfile.readFloat();
	  }
	  //	  LoadV4(runfile);
	}
	else {
	    LoadV5(runfile);
	}
	
	runfile.seek(filePosition);
	}

  /*    void LoadV4(RandomAccessRunfile runfile) throws IOException {
	int i;
	int temp;
	StringBuffer tempStrBuff = new StringBuffer();
	


	}*/

    void LoadV5(RandomAccessRunfile runfile ) throws IOException {
		byte[] temp = new byte[20];
		runfile.seek(632);
		standardClock = (double)runfile.readFloat();
		lpsdClock = (double)runfile.readFloat();
       		numOfElements = runfile.readInt();
		if( numOfElements == 0 ) {
		    numOfElements = nDet;
		}
		runfile.seek(700);
		detectorLength.location = runfile.readInt();
		detectorLength.size = runfile.readInt();
		detectorWidth.location = runfile.readInt();
		detectorWidth.size = runfile.readInt();
		detectorDepth.location = runfile.readInt();
		detectorDepth.size = runfile.readInt();
		/*		temp = new byte[4];
		runfile.read(temp, 0, 4);
		iName = new String(temp);
		*/

		iName = runfile.readRunString(4);
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
		if (versionNumber >= 6 ) {
		    instrumentType = runfile.readInt();
		    filterType = runfile.readInt();
		    sampleEnv = runfile.readInt();
		    detectorConfig = runfile.readInt();
		    runType = runfile.readInt();
		}
    }

   protected int set( String element, double val  ) {
	int errval = 0;
	if (element.equalsIgnoreCase( "sourceToSample") ){
	    sourceToSample = val;
	}
	else if ( element.equalsIgnoreCase( "sourceToChopper") ){
	    sourceToChopper = val;
	}
	else if ( element.equalsIgnoreCase( "dta") ) {
	    dta = val;
	}
	else if ( element.equalsIgnoreCase( "dtd") ) {
	    dtd = val;
	}
	else if ( element.equalsIgnoreCase( "omega") ) {
	    omega = val;
	}
	else if ( element.equalsIgnoreCase( "chi") ) {
	    chi = val;
	}
	else if ( element.equalsIgnoreCase( "phi") ) {
	    phi = val;
	}
	else if ( element.equalsIgnoreCase( "xLeft") ) {
	    xLeft = val;
	}
	else if ( element.equalsIgnoreCase( "xRight") ) {
	    xRight = val;
	}
	else if ( element.equalsIgnoreCase( "yLower") ) {
	    yLower = val;
	}
	else if ( element.equalsIgnoreCase( "yUpper") ) {
	    yUpper = val;
	}
	else if ( element.equalsIgnoreCase( "xDisplacement") ) {
	    xDisplacement = val;
	}
	else if ( element.equalsIgnoreCase( "yDisplacement") ) {
	    yDisplacement = val;
	}
	else if ( element.equalsIgnoreCase( "xLength") ) {
	    xLength = val;
	}
	else if ( element.equalsIgnoreCase( "clockPeriod") ) {
	    clockPeriod = val;
	}
	else if ( element.equalsIgnoreCase( "energyIn") ) {
	    energyIn = val;
	}
	else if ( element.equalsIgnoreCase( "energyOut") ) {
	    energyOut = val;
	}
	else if ( element.equalsIgnoreCase( "protonCurrent") ) {
	    protonCurrent = val;
	}
	else if ( element.equalsIgnoreCase( "standardClock") ) {
	    standardClock = val;
	}
	else if ( element.equalsIgnoreCase( "lpsdClock")) {
	    lpsdClock = val;
	}
	else if ( ( element.equalsIgnoreCase( "nDet") ||
	    element.equalsIgnoreCase( "numOfCyclesPreset") ||
	    element.equalsIgnoreCase( "numOfCyclesCompleted") ||
	    element.equalsIgnoreCase( "groupToMonitor") ||
	    element.equalsIgnoreCase( "channelToMonitor") ||
	    element.equalsIgnoreCase( "numOfHistograms") ||
	    element.equalsIgnoreCase( "numOfTimeFields") ||
	    element.equalsIgnoreCase( "numOfControl") ||
	    element.equalsIgnoreCase( "controlFlag") ||
	    element.equalsIgnoreCase( "clockShift") ||
	    element.equalsIgnoreCase( "numOfX") ||
	    element.equalsIgnoreCase( "numOfY")  ||
	    element.equalsIgnoreCase( "numOfWavelengths") || 
	    element.equalsIgnoreCase( "areaChannelWidth") ||
	    element.equalsIgnoreCase( "areaDoubleWidth") ||
	    element.equalsIgnoreCase( "numOfSeqHist") ||
	    element.equalsIgnoreCase( "areaBinning") ||
	    element.equalsIgnoreCase( "microprocessor") ||
	    element.equalsIgnoreCase( "numOfOverflows") ||
	    element.equalsIgnoreCase( "numOfLockouts") ||
	    element.equalsIgnoreCase( "samplePos") ||
	    element.equalsIgnoreCase( "numOfHeadBlocks") ||
	    element.equalsIgnoreCase( "overflowSort") ) &&
		  (val <= 32767 && val >= -32768) ) {
	    set( element, (short)val );
	}
	else if ( (element.equalsIgnoreCase( "numOfBlocks") ||
	    element.equalsIgnoreCase( "histStartAddress") ||
	    element.equalsIgnoreCase( "offsetToFree") ||
	    element.equalsIgnoreCase( "versionNumber") ||
	    element.equalsIgnoreCase( "runNum") ||
	    element.equalsIgnoreCase( "nextRun") ||
	    element.equalsIgnoreCase( "presetMonitorCounts") ||
	    element.equalsIgnoreCase( "elapsedMonitorCounts") ||
	    element.equalsIgnoreCase( "runAfterFinished") ||
	    element.equalsIgnoreCase( "totalMonitorCounts") ||
	    element.equalsIgnoreCase( "detCalibFile") ||
	    element.equalsIgnoreCase( "moderatorCalibFile") ||
	    element.equalsIgnoreCase( "totalChannels") ||
	    element.equalsIgnoreCase( "numOfPulses") ||
	    element.equalsIgnoreCase( "sizeOfDataArea") ||
	    element.equalsIgnoreCase( "hardwareTMin") ||
	    element.equalsIgnoreCase( "hardwareTMax") ||
	    element.equalsIgnoreCase( "hardTimeDelay") ||
	    element.equalsIgnoreCase( "maxWavelength") ||
	    element.equalsIgnoreCase( "minWavelength") ||
	    element.equalsIgnoreCase( "addressOf1DData") ||
	    element.equalsIgnoreCase( "addressOf2DData") ||
	    element.equalsIgnoreCase( "endOfOverflow") ||
	    element.equalsIgnoreCase( "channels1D") ||
	    element.equalsIgnoreCase( "firstOverflow") ||
	    element.equalsIgnoreCase( "expNum") ||
	    element.equalsIgnoreCase( "firstRun") ||
	    element.equalsIgnoreCase( "lastRun") ||
	    element.equalsIgnoreCase( "defaultRun") ||
	    element.equalsIgnoreCase( "numOfElements") ||
	    element.equalsIgnoreCase( "instrumentType") ||
	    element.equalsIgnoreCase( "filterType") ||
	    element.equalsIgnoreCase( "sampleEnv") ||
	    element.equalsIgnoreCase( "detectorConfig") ||
	    element.equalsIgnoreCase( "runType") ||
	    element.equalsIgnoreCase( "MagicNumber") ) &&
		  (val >= -2147483648 && val <= 2147483647)) {
	    set (element, (int) val);
	}
	else{
	    System.out.println( "Cannot set " + element + " as a double" );
	    return errval = -1;
	}
	return errval;
    }

    protected int set( String element, float val  ) {
	int errval = 0;
	if (element.equalsIgnoreCase( "sourceToSample") ||
	    element.equalsIgnoreCase( "sourceToChopper") ||
	    element.equalsIgnoreCase( "dta") ||
	    element.equalsIgnoreCase( "dtd") ||
	    element.equalsIgnoreCase( "omega") ||
	    element.equalsIgnoreCase( "chi") ||
	    element.equalsIgnoreCase( "phi") ||
	    element.equalsIgnoreCase( "xLeft") ||
	    element.equalsIgnoreCase( "xRight") ||
	    element.equalsIgnoreCase( "yLower") ||
	    element.equalsIgnoreCase( "yUpper") ||
	    element.equalsIgnoreCase( "xDisplacement") ||
	    element.equalsIgnoreCase( "yDisplacement") ||
	    element.equalsIgnoreCase( "xLength") ||
	    element.equalsIgnoreCase( "clockPeriod") ||
	    element.equalsIgnoreCase( "energyIn") ||
	    element.equalsIgnoreCase( "energyOut") ||
	    element.equalsIgnoreCase( "protonCurrent") ||
	    element.equalsIgnoreCase( "standardClock") ||
	    element.equalsIgnoreCase( "lpsdClock")) {
	    set(element, (double)val);
	}
	else if ( ( element.equalsIgnoreCase( "nDet") ||
	    element.equalsIgnoreCase( "numOfCyclesPreset") ||
	    element.equalsIgnoreCase( "numOfCyclesCompleted") ||
	    element.equalsIgnoreCase( "groupToMonitor") ||
	    element.equalsIgnoreCase( "channelToMonitor") ||
	    element.equalsIgnoreCase( "numOfHistograms") ||
	    element.equalsIgnoreCase( "numOfTimeFields") ||
	    element.equalsIgnoreCase( "numOfControl") ||
	    element.equalsIgnoreCase( "controlFlag") ||
	    element.equalsIgnoreCase( "clockShift") ||
	    element.equalsIgnoreCase( "numOfX") ||
	    element.equalsIgnoreCase( "numOfY")  ||
	    element.equalsIgnoreCase( "numOfWavelengths") || 
	    element.equalsIgnoreCase( "areaChannelWidth") ||
	    element.equalsIgnoreCase( "areaDoubleWidth") ||
	    element.equalsIgnoreCase( "numOfSeqHist") ||
	    element.equalsIgnoreCase( "areaBinning") ||
	    element.equalsIgnoreCase( "microprocessor") ||
	    element.equalsIgnoreCase( "numOfOverflows") ||
	    element.equalsIgnoreCase( "numOfLockouts") ||
	    element.equalsIgnoreCase( "samplePos") ||
	    element.equalsIgnoreCase( "numOfHeadBlocks") ||
	    element.equalsIgnoreCase( "overflowSort") ) &&
		  (val <= 32767 && val >= -32768) ) {
	    set( element, (short)val );
	}
	else if ( (element.equalsIgnoreCase( "numOfBlocks") ||
	    element.equalsIgnoreCase( "histStartAddress") ||
	    element.equalsIgnoreCase( "offsetToFree") ||
	    element.equalsIgnoreCase( "versionNumber") ||
	    element.equalsIgnoreCase( "runNum") ||
	    element.equalsIgnoreCase( "nextRun") ||
	    element.equalsIgnoreCase( "presetMonitorCounts") ||
	    element.equalsIgnoreCase( "elapsedMonitorCounts") ||
	    element.equalsIgnoreCase( "runAfterFinished") ||
	    element.equalsIgnoreCase( "totalMonitorCounts") ||
	    element.equalsIgnoreCase( "detCalibFile") ||
	    element.equalsIgnoreCase( "moderatorCalibFile") ||
	    element.equalsIgnoreCase( "totalChannels") ||
	    element.equalsIgnoreCase( "numOfPulses") ||
	    element.equalsIgnoreCase( "sizeOfDataArea") ||
	    element.equalsIgnoreCase( "hardwareTMin") ||
	    element.equalsIgnoreCase( "hardwareTMax") ||
	    element.equalsIgnoreCase( "hardTimeDelay") ||
	    element.equalsIgnoreCase( "maxWavelength") ||
	    element.equalsIgnoreCase( "minWavelength") ||
	    element.equalsIgnoreCase( "addressOf1DData") ||
	    element.equalsIgnoreCase( "addressOf2DData") ||
	    element.equalsIgnoreCase( "endOfOverflow") ||
	    element.equalsIgnoreCase( "channels1D") ||
	    element.equalsIgnoreCase( "firstOverflow") ||
	    element.equalsIgnoreCase( "expNum") ||
	    element.equalsIgnoreCase( "firstRun") ||
	    element.equalsIgnoreCase( "lastRun") ||
	    element.equalsIgnoreCase( "defaultRun") ||
	    element.equalsIgnoreCase( "numOfElements") ||
	    element.equalsIgnoreCase( "instrumentType") ||
	    element.equalsIgnoreCase( "filterType") ||
	    element.equalsIgnoreCase( "sampleEnv") ||
	    element.equalsIgnoreCase( "detectorConfig") ||
	    element.equalsIgnoreCase( "runType") ||
	    element.equalsIgnoreCase( "MagicNumber")) &&
		  (val > -2147483648 && val < 2147483647)) {
	    set (element, (int) val);
	}
	else{
	    System.out.println( "Cannot set " + element + " as a float" );
	    return errval = -1;
	}
	return errval;
    }

    protected int set( String element, int val  ) {
	int errval = 0;
	if ( element.equalsIgnoreCase( "numOfBlocks") ) {
	    numOfBlocks= val;
	}
	else if( element.equalsIgnoreCase( "histStartAddress") ) {
	    histStartAddress = val;
	}
	else if( element.equalsIgnoreCase( "offsetToFree") ) {
	    offsetToFree = val;
	}
	else if( element.equalsIgnoreCase( "versionNumber") ) {
	    versionNumber = val;
	}
	else if( element.equalsIgnoreCase( "runNum") ) {
	    runNum = val;
	}
	else if( element.equalsIgnoreCase( "nextRun") ) {
	    nextRun = val;
	}
	else if( element.equalsIgnoreCase( "presetMonitorCounts") ) {
	    presetMonitorCounts = val;
	}
	else if( element.equalsIgnoreCase( "elapsedMonitorCounts") ) {
	    elapsedMonitorCounts = val;
	}
	else if( element.equalsIgnoreCase( "runAfterFinished") ) {
	    runAfterFinished = val;
	}
	else if( element.equalsIgnoreCase( "totalMonitorCounts") ) {
	    totalMonitorCounts = val;
	}
	else if( element.equalsIgnoreCase( "detCalibFile") ) {
	    detCalibFile = val;
	}
	else if( element.equalsIgnoreCase( "moderatorCalibFile") ) {
	    moderatorCalibFile = val;
	}
	else if( element.equalsIgnoreCase( "totalChannels") ) {
	    totalChannels = val;
	}
	else if( element.equalsIgnoreCase( "numOfPulses") ) {
	    numOfPulses = val;
	}
	else if( element.equalsIgnoreCase( "sizeOfDataArea") ) {
	    sizeOfDataArea = val;
	}
	else if( element.equalsIgnoreCase( "hardwareTMin") ) {
	    hardwareTMin = val;
	}
	else if( element.equalsIgnoreCase( "hardwareTMax") ) {
	    hardwareTMax = val;
	}
	else if( element.equalsIgnoreCase( "hardTimeDelay") ) {
	    hardTimeDelay = val;
	}
	else if( element.equalsIgnoreCase( "maxWavelength") ) {
	    maxWavelength = val;
	}
	else if( element.equalsIgnoreCase( "minWavelength") ) {
	    minWavelength = val;
	}
	else if( element.equalsIgnoreCase( "addressOf1DData") ) {
	    addressOf1DData = val;
	}
	else if( element.equalsIgnoreCase( "addressOf2DData") ) {
	    addressOf2DData = val;
	}
	else if( element.equalsIgnoreCase( "endOfOverflow") ) {
	    endOfOverflow = val;
	}
	else if( element.equalsIgnoreCase( "channels1D") ) {
	    channels1D = val;
	}
	else if( element.equalsIgnoreCase( "firstOverflow") ) {
	    firstOverflow = val;
	}
	else if( element.equalsIgnoreCase( "expNum") ) {
	    expNum = val;
	}
	else if( element.equalsIgnoreCase( "firstRun") ) {
	    firstRun = val;
	}
	else if( element.equalsIgnoreCase( "lastRun") ) {
	    lastRun = val;
	}
	else if( element.equalsIgnoreCase( "defaultRun") ) {
	    defaultRun = val;
	}
	else if( element.equalsIgnoreCase( "numOfElements") ) {
	    numOfElements = val;
	}
	else if( element.equalsIgnoreCase( "MagicNumber") ) {
	    MagicNumber = val;
	}
	else if( element.equalsIgnoreCase( "instrumentType") ) {
	    instrumentType = val;
	}
	else if( element.equalsIgnoreCase( "filterType") ) {
	    filterType = val;
	}
	else if( element.equalsIgnoreCase( "sampleEnv") ) {
	    sampleEnv = val;
	}
	else if( element.equalsIgnoreCase( "detectorConfig") ) {
	    detectorConfig = val;
	}
	else if( element.equalsIgnoreCase( "runType") ) {
	    runType = val;
	}

	else if (element.equalsIgnoreCase( "sourceToSample") ||
		     element.equalsIgnoreCase( "sourceToChopper") ||
		     element.equalsIgnoreCase( "dta") ||
		     element.equalsIgnoreCase( "dtd") ||
		     element.equalsIgnoreCase( "omega") ||
		     element.equalsIgnoreCase( "chi") ||
		     element.equalsIgnoreCase( "phi") ||
		     element.equalsIgnoreCase( "xLeft") ||
		     element.equalsIgnoreCase( "xRight") ||
		     element.equalsIgnoreCase( "yLower") ||
		     element.equalsIgnoreCase( "yUpper") ||
		     element.equalsIgnoreCase( "xDisplacement") ||
		     element.equalsIgnoreCase( "yDisplacement") ||
		     element.equalsIgnoreCase( "xLength") ||
		     element.equalsIgnoreCase( "clockPeriod") ||
		     element.equalsIgnoreCase( "energyIn") ||
		     element.equalsIgnoreCase( "energyOut") ||
		     element.equalsIgnoreCase( "protonCurrent") ||
		     element.equalsIgnoreCase( "standardClock") ||
		     element.equalsIgnoreCase( "lpsdClock") ) {
	    set( element, (double)val );
	}
	else if ( ( element.equalsIgnoreCase( "nDet") ||
		    element.equalsIgnoreCase( "numOfCyclesPreset") ||
		    element.equalsIgnoreCase( "numOfCyclesCompleted") ||
		    element.equalsIgnoreCase( "groupToMonitor") ||
		    element.equalsIgnoreCase( "channelToMonitor") ||
		    element.equalsIgnoreCase( "numOfHistograms") ||
		    element.equalsIgnoreCase( "numOfTimeFields") ||
		    element.equalsIgnoreCase( "numOfControl") ||
		    element.equalsIgnoreCase( "controlFlag") ||
		    element.equalsIgnoreCase( "clockShift") ||
		    element.equalsIgnoreCase( "numOfX") ||
		    element.equalsIgnoreCase( "numOfY")  ||
		    element.equalsIgnoreCase( "numOfWavelengths") || 
		    element.equalsIgnoreCase( "areaChannelWidth") ||
		    element.equalsIgnoreCase( "areaDoubleWidth") ||
		    element.equalsIgnoreCase( "numOfSeqHist") ||
		    element.equalsIgnoreCase( "areaBinning") ||
		    element.equalsIgnoreCase( "microprocessor") ||
		    element.equalsIgnoreCase( "numOfOverflows") ||
		    element.equalsIgnoreCase( "numOfLockouts") ||
		    element.equalsIgnoreCase( "samplePos") ||
		    element.equalsIgnoreCase( "numOfHeadBlocks") ||
		    element.equalsIgnoreCase( "overflowSort") ) &&
		  (val < 32767 && val > -32768) ) {
	    set( element, (short)val );
	}
	else{
	    System.out.println( "Cannot set " + element + " as an int" );
	    return errval = -1;
	}
	return errval;
    }

    protected int set( String element, short val  ) {
	int errval = 0;
	if (  element.equalsIgnoreCase( "nDet") ) {
	    nDet = val;
	}
	else if  ( element.equalsIgnoreCase( "numOfCyclesPreset") ) {
	    numOfCyclesPreset = val;
	}
	else if ( element.equalsIgnoreCase( "numOfCyclesCompleted") ){
	    numOfCyclesCompleted = val;
	}
	else if ( element.equalsIgnoreCase( "groupToMonitor") ) {
	    groupToMonitor= val;
	}
	else if ( element.equalsIgnoreCase( "channelToMonitor")) {
	    channelToMonitor = val;
	}
	else if (element.equalsIgnoreCase( "sourceToSample")) {
	    sourceToSample = val;
	}
	else if ( element.equalsIgnoreCase( "sourceToChopper")) {
	    sourceToChopper = val;
	}
	else if ( element.equalsIgnoreCase( "dta")) {
	    dta = val;
	}
	else if ( element.equalsIgnoreCase( "dtd")) {
	    dtd = val;
	}
	else if ( element.equalsIgnoreCase( "omega")) {
	    omega = val;
	}
	else if ( element.equalsIgnoreCase( "chi")) {
	    chi = val;
	}
	else if ( element.equalsIgnoreCase( "phi")) {
	    phi = val;
	}
	else if ( element.equalsIgnoreCase( "xLeft")) {
	    xLeft = val;
	}
	else if ( element.equalsIgnoreCase( "xRight")) {
	    xRight = val;
	}
	else if ( element.equalsIgnoreCase( "yLower")) {
	    yLower = val;
	}
	else if ( element.equalsIgnoreCase( "yUpper")) {
	    yUpper = val;
	}
	else if ( element.equalsIgnoreCase( "xDisplacement")) {
	    xDisplacement = val;
	}
	else if ( element.equalsIgnoreCase( "yDisplacement")) {
	    yDisplacement = val;
	}
	else if ( element.equalsIgnoreCase( "xLength")) {
	    xLength = val;
	}
	else if ( element.equalsIgnoreCase( "clockPeriod")) {
	    clockPeriod = val;
	}
	else if ( element.equalsIgnoreCase( "energyIn")) {
	    energyIn = val;
	}
	else if ( element.equalsIgnoreCase( "energyOut")) {
	    energyOut = val;
	}
	else if ( element.equalsIgnoreCase( "protonCurrent")) {
	    protonCurrent = val;
	}
	else if ( element.equalsIgnoreCase( "standardClock")) {
	    standardClock = val;
	}
	else if ( element.equalsIgnoreCase( "lpsdClock")) {
	    lpsdClock = val;
	}
	else if ( element.equalsIgnoreCase( "numOfHistograms")) {
	    numOfHistograms = val;
	}
	else if ( element.equalsIgnoreCase( "numOfTimeFields")) {
	    numOfTimeFields = val;
	}
	else if ( element.equalsIgnoreCase( "numOfControl")) {
	    numOfControl = val;
	}
	else if ( element.equalsIgnoreCase( "controlFlag")) {
	    controlFlag = val;
	}
	else if ( element.equalsIgnoreCase( "clockShift")) {
	    clockShift = val;
}
	else if ( element.equalsIgnoreCase( "numOfX")) {
	    numOfX = val;
	}
	else if ( element.equalsIgnoreCase( "numOfY") ) {
	    numOfY= val;
	}
	else if ( element.equalsIgnoreCase( "numOfWavelengths") ) {
	    numOfWavelengths = val;
	}
	else if ( element.equalsIgnoreCase( "areaChannelWidth")) {
	    areaChannelWidth = val;
	}
	else if ( element.equalsIgnoreCase( "areaDoubleInterval")) {
	    areaDoubleInterval = val;
}
	else if ( element.equalsIgnoreCase( "numOfSeqHist")) {
	    numOfSeqHist = val;
	}
	else if ( element.equalsIgnoreCase( "areaBinning")) {
	    areaBinning = val;
	}
	else if ( element.equalsIgnoreCase( "microprocessor")) {
	    microprocessor = val;
	}
	else if ( element.equalsIgnoreCase( "numOfOverflows")) {
	    numOfOverflows = val;
	}
	else if ( element.equalsIgnoreCase( "numOfLockouts")) {
	    numOfLockouts = val;
}
	else if ( element.equalsIgnoreCase( "samplePos")) {
	    samplePos = val;
	}
	else if ( element.equalsIgnoreCase( "numOfHeadBlocks")) {
	    numOfHeadBlocks = val;
	}
	else if ( element.equalsIgnoreCase( "overflowSort") ) {
	    overflowSort = val;
	}
	else if ( element.equalsIgnoreCase( "sourceToSample") ||
	    element.equalsIgnoreCase( "sourceToChopper") ||
	    element.equalsIgnoreCase( "dta") ||
	    element.equalsIgnoreCase( "dtd") ||
	    element.equalsIgnoreCase( "omega") ||
	    element.equalsIgnoreCase( "chi") ||
	    element.equalsIgnoreCase( "phi") ||
	    element.equalsIgnoreCase( "xLeft") ||
	    element.equalsIgnoreCase( "xRight") ||
	    element.equalsIgnoreCase( "yLower") ||
	    element.equalsIgnoreCase( "yUpper") ||
	    element.equalsIgnoreCase( "xDisplacement") ||
	    element.equalsIgnoreCase( "yDisplacement") ||
	    element.equalsIgnoreCase( "xLength") ||
	    element.equalsIgnoreCase( "clockPeriod") ||
	    element.equalsIgnoreCase( "energyIn") ||
	    element.equalsIgnoreCase( "energyOut") ||
	    element.equalsIgnoreCase( "protonCurrent") ||
	    element.equalsIgnoreCase( "standardClock") ||
	    element.equalsIgnoreCase( "lpsdClock") ) {
	    set( element, (double)val );
	}
	else if ( element.equalsIgnoreCase( "numOfBlocks") ||
	    element.equalsIgnoreCase( "histStartAddress") ||
	    element.equalsIgnoreCase( "offsetToFree") ||
	    element.equalsIgnoreCase( "versionNumber") ||
	    element.equalsIgnoreCase( "runNum") ||
	    element.equalsIgnoreCase( "nextRun") ||
	    element.equalsIgnoreCase( "presetMonitorCounts") ||
	    element.equalsIgnoreCase( "elapsedMonitorCounts") ||
	    element.equalsIgnoreCase( "runAfterFinished") ||
	    element.equalsIgnoreCase( "totalMonitorCounts") ||
	    element.equalsIgnoreCase( "detCalibFile") ||
	    element.equalsIgnoreCase( "moderatorCalibFile") ||
	    element.equalsIgnoreCase( "totalChannels") ||
	    element.equalsIgnoreCase( "numOfPulses") ||
	    element.equalsIgnoreCase( "sizeOfDataArea") ||
	    element.equalsIgnoreCase( "hardwareTMin") ||
	    element.equalsIgnoreCase( "hardwareTMax") ||
	    element.equalsIgnoreCase( "hardTimeDelay") ||
	    element.equalsIgnoreCase( "maxWavelength") ||
	    element.equalsIgnoreCase( "minWavelength") ||
	    element.equalsIgnoreCase( "addressOf1DData") ||
	    element.equalsIgnoreCase( "addressOf2DData") ||
	    element.equalsIgnoreCase( "endOfOverflow") ||
	    element.equalsIgnoreCase( "channels1D") ||
	    element.equalsIgnoreCase( "firstOverflow") ||
	    element.equalsIgnoreCase( "expNum") ||
	    element.equalsIgnoreCase( "firstRun") ||
	    element.equalsIgnoreCase( "lastRun") ||
	    element.equalsIgnoreCase( "defaultRun") ||
	    element.equalsIgnoreCase( "numOfElements") ||
	    element.equalsIgnoreCase( "instrumentType") ||
	    element.equalsIgnoreCase( "filterType") ||
	    element.equalsIgnoreCase( "sampleEnv") ||
	    element.equalsIgnoreCase( "detectorConfig") ||
	    element.equalsIgnoreCase( "runType") ||
	    element.equalsIgnoreCase( "MagicNumber") ) {
	    set (element, (int) val);
	}

	else{
	    System.out.println( "Cannot set " + element + " as a short" );
	    return errval = -1;
	}
	return errval;
    }

    protected int set( String element, String val  ) {
	int errval = 0;
	if ( element.equalsIgnoreCase( "userName") ) {
	    StringBuffer tempBuff = new StringBuffer( val );
	    tempBuff.setLength(20);
	    userName = new String(tempBuff);
	}
	else if ( element.equalsIgnoreCase( "runTitle") ) {
	    StringBuffer tempBuff = new StringBuffer( val );
	    tempBuff.setLength(80);
	    runTitle = new String(tempBuff);
	}
	else if ( element.equalsIgnoreCase( "startDate") ) {
	    startDate = val;
	}
	else if ( element.equalsIgnoreCase( "startTime") ) {
	    startTime = val;
	}
	else if ( element.equalsIgnoreCase( "endDate") ) {
	    endDate = val;
	}
	else if ( element.equalsIgnoreCase( "endTime") ) {
	    endTime = val;
	}
	else if ( element.equalsIgnoreCase( "iName") ) {
	    StringBuffer tempBuff = new StringBuffer( val );
	    tempBuff.setLength(4);
	    iName = new String(tempBuff);
	}
	else{
	    System.out.println( "Cannot set " + element + " as string" );
	    return errval = -1;
	}
	return errval;
    }

    public void Write( RandomAccessRunfile runfile ) {
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
		runfile.writeInt( numOfElements );
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
		runfile.writeInt( instrumentType );
		runfile.writeInt( filterType );
		runfile.writeInt( sampleEnv );
		runfile.writeInt( detectorConfig );
		runfile.writeInt( runType );
		runfile.seek(1532);
		runfile.writeInt( (int)0 );
		
		
	}
	catch (IOException ex) {
	    System.out.println( "Trouble writing runfile header" );
	    ex.printStackTrace();
  	}
    }

    public void reWrite( String runfileName) {
	try {
	RandomAccessRunfile runfile;
	runfile = new RandomAccessRunfile (runfileName, "rw" );
	Write( runfile );
	runfile.close();
	}
	catch ( IOException ex ) {
	    System.out.println( "Trouble rewriting runfile header" );
	    ex.printStackTrace();
	}
    }
    public Object clone() {
	try {
	    Header copy = (Header)super.clone();

	    return copy;

	} catch (CloneNotSupportedException ex ) { 
	    ex.printStackTrace();
	    throw new Error ( "Error Cloning Header Object" );
	}
    }

}
