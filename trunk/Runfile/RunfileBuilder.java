package IPNS.Runfile;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import IPNS.Calib.*;
import IPNS.Control.*;
/*
 *
 * $Log$
 * Revision 5.23  2001/10/11 15:16:40  hammonds
 * Fixed problems with groupAllSeperate routine.  It was having problems when detectors were not valid detectors.
 * Also some work on Ancillary Equipment.
 *
 * Revision 5.22  2001/10/10 15:33:52  hammonds
 * Modifications to start/endDateAndTimeToCurrent to correct some formatting problems.
 * Add getDefDiscFileName, getDefDCalibFileName, getDefParamFileName
 * Add headerSetFromParams, headerSetFromDCalib, headerSetFromDiscFile with no arguments which take values from the system default files.
 * First level add of control parameters.
 *
 * Revision 5.21  2001/10/08 19:30:32  hammonds
 * Change how segments are specified since the segment IDs have been changed.  minIDs are now assigned even if the detector type is 0.
 *
 * Revision 5.20  2001/08/27 16:12:12  hammonds
 * Fixed problem in the calculation of the numberOfElements.  The calculated number was always one higher than were actually there.
 *
 * Revision 5.19  2001/08/03 19:06:01  hammonds
 * Changes to allow grouping detectors via a script.
 *
 * Revision 5.18  2001/07/24 16:56:26  hammonds
 * Updated addxxxxxTimeField methods.
 *
 * Revision 5.17  2001/07/24 15:05:31  hammonds
 * Took RunfileBuilder argument out of many calls related to building a runfile from a script.
 * Added timesetting methods.
 *
 * Revision 5.16  2001/07/23 21:18:38  hammonds
 * Added to support newrun type scripts without iCame
 *
 * Revision 5.15  2001/07/23 16:02:21  hammonds
 * Added routine to add information from the instrument paramenter file to the runfile.
 * Also added method to set the Current Date and time to the runfile.
 *
 * Revision 5.14  2001/07/20 21:41:43  hammonds
 * added setHeader methods with double and short arguments
 *
 * Revision 5.13  2001/07/20 20:04:38  hammonds
 * Added methods for setting the values in the Header from the RunfileBuilder.  Also made some small changes that will allow a runfile with a header to be constructed from the main method of RunfileBuilder.
 *
 * Revision 5.12  2001/07/18 18:28:41  hammonds
 * Condensed writes of tables to use writeFloatTable, writeIntTable, writeShortTable.
 *
 * Revision 5.11  2001/04/24 20:06:49  hammonds
 * Changed addSubgroup so that the step size that comes in for Pulse Height subgroups is the number of steps.  This is used to calculate a step size that will produce that many steps so that the data area is properly sized.
 *
 * Revision 5.10  2001/04/09 18:36:14  hammonds
 * Added functions for dataSource and minID.
 *
 * Revision 5.8  2001/03/15 17:24:58  hammonds
 * Added stuff to handle new dcalib info ( det. size, rotations, crate info...).
 *
 * Revision 5.7  2000/03/11 03:07:43  hammonds
 * Fixed small problem with TimeScales.
 *
 * Revision 5.6  2000/02/29 01:37:48  hammonds
 * Fixed problem with writing time scale.
 *
 * Revision 5.5  2000/02/25 04:08:34  hammonds
 * Made changes to detector subgroups
 *
 * Revision 5.4  2000/02/22 06:13:35  hammonds
 * Fix to change time scale problem.
 *
 * Revision 5.3  2000/02/18 03:34:27  hammonds
 * Added Log header to track changes
 *
 *
 */

public class RunfileBuilder extends Runfile implements Cloneable{

    public static void main (String[] args) throws IOException {
	String filename = args[0];
	RunfileBuilder newRF = new RunfileBuilder();
        newRF.setFileName(filename);
	newRF.headerSet( "iName", "hrcs");
	newRF.headerSet( "userName", "hammonds");
	newRF.headerSet( "runTitle", "RunfileBuilder test from main");
	newRF.headerSet( "versionNumber", 6);
	newRF.headerSet( "totalChannels", 70000);
	newRF.headerSet( "energyIn", 5.0);
 	newRF.startDateAndTimeToCurrent();
	newRF.headerSetFromParams();
	newRF.headerSetFromDCalib( );
	newRF.headerSetFromDiscFile();

	newRF.headerSet( "clockPeriod", 5.0);
	newRF.headerSet( "numOfHistograms", (short)1);
	newRF.addNormalTimeField( 1000.0f, 10000.0f, 10.0f, 1);
	newRF.addFocusedTimeField( 1000.0f, 25000.0f, 7.0f, 3);
	newRF.addPulseHeightTimeField( 1000.0f, 10000.0f, 10.0f, 3);
	newRF.addPulseHeightTimeField( 1000.0f, 10000.0f, 255.0f, 2);
	newRF.addEnergyTimeField( -100.0f, 100.0f, 5.0f, 4);
	newRF.addWavelengthTimeField( 2.0f, 5.0f, 10.0f, 7);
	newRF.addWavelengthTimeField( 1.0f, 6.0f, 10.0f, 5);
       	newRF.groupAllSeparate(1, 1);
	newRF.endDateAndTimeToCurrent();
	newRF.addAncillaryEquipment("/home/hammonds/inst/anc/Lakeshore330.dat");
	System.out.println("here");
	newRF.Write();
	System.exit(0);
    }
    public RunfileBuilder() {
	super( );
	header = new Header();
	header.versionNumber = 5;
	header.numOfBlocks = (short)3;
	header.numOfHeadBlocks = (short)3;
	header.offsetToFree = 1536  ;
	header.presetMonitorCounts = 108000;
	header.numOfCyclesPreset = (short)720;
	header.varToMonitor = 'p';
	detectorAngle = new float[1];
	flightPath = new float[1];
	detectorHeight = new float[1];
	detectorType = new short[1];
	detCoordSys = new short[1];
	detectorRot1 = new float[1];
	detectorRot2 = new float[1];
	detectorEfficiency = new float[1];
	timeScale = new float[1];
	timeShift = new float[1];
	areaStartTime = new float[1];
	timeDelay = new float[1];
	timeField = new TimeField[1];
	detectorMap = new DetectorMap[1];
	subgroupMap = new int[1][];
	minSubgroupID = new int[0];
	maxSubgroupID = new int[0];
	discriminator = new DiscLevel[0];
	params = new ParameterFile[0];
    }
    
    public RunfileBuilder( String infileName ) throws IOException {
	super( infileName );
    }

    public void setFileName( String infileName ) {
	runfileName =  new String( infileName );
    }
	
    public void setHistStartAddress( int histStartAddress ) {
	header.histStartAddress = histStartAddress;
    }

    public void setNumOfBlocks ( int numOfBlocks ) {
	header.numOfBlocks =  numOfBlocks;
    }

    public void setOffsetToFree ( int offsetToFree ) {
	header.offsetToFree = offsetToFree ;
    }

    public void setNDet ( short nDet ) {
	header.nDet = nDet;
    }

    public void setUserName ( String userName ) {
	StringBuffer tempBuff = new StringBuffer( userName );
	tempBuff.setLength(20);
	header.userName = new String(tempBuff);
    }

    public void setRunTitle ( String runTitle ) {
	StringBuffer tempBuff = new StringBuffer( runTitle );
	tempBuff.setLength(80);
	header.runTitle = new String(tempBuff);
    }

    public void setRunNum ( int runNum ) {
	header.runNum = runNum;
    }

    public void setNextRun ( int nextRun ) {
	header.nextRun = nextRun;
    }

    public void setStartDate ( String startDate ) {
	StringBuffer tempBuff = new StringBuffer( startDate );
	tempBuff.setLength(9);
	header.startDate = new String( tempBuff );
    }

    public void setStartTime ( String startTime ) {
	StringBuffer tempBuff = new StringBuffer( startTime );
	tempBuff.setLength(8);
	header.startTime = new String( tempBuff );
    }
    public void setEndDate ( String endDate ) {
	StringBuffer tempBuff = new StringBuffer( endDate );
	tempBuff.setLength(9);
	header.endDate = new String( tempBuff );
    }

    public void setEndTime ( String endTime ) {
	StringBuffer tempBuff = new StringBuffer( endTime );
	tempBuff.setLength(8);
	header.endTime = new String( tempBuff );
    }

    public void setProtStatus ( char protStatus ) {
	header.protStatus = protStatus;
    }

    public void setVarToMonitor ( char varToMonitor ) {
	header.varToMonitor = varToMonitor;
    }

    public void setPresetMonitorCounts ( int presetMonitorCounts ) {
	header.presetMonitorCounts = presetMonitorCounts;
    }

    public void setElapsedMonitorCounts ( int elapsedMonitorCounts ) {
	header.elapsedMonitorCounts = elapsedMonitorCounts;
    }

    public void setNumOfCyclesPreset ( short numOfCyclesPreset ) {
	header.numOfCyclesPreset = numOfCyclesPreset;
    }

    public void setNumOfCyclesCompleted ( short numOfCyclesCompleted ) {
	header.numOfCyclesCompleted = numOfCyclesCompleted;
    }
    
    public void setRunAfterFinished ( int runAfterFinished ) {
	header.runAfterFinished = runAfterFinished;
    }
    
    public void setTotalMonitorCounts ( int totalMonitorCounts ) {
	header.totalMonitorCounts = totalMonitorCounts;
    }
    
    public void setDetCalibFile ( int detCalibFile ) {
	header.detCalibFile = detCalibFile;
    }
    
    public void setDetLocUnit ( char detLocUnit ) {
	header.detLocUnit = detLocUnit;
    }
    
    public void setPseudoTimeUnit ( char pseudoTimeUnit ) {
	header.pseudoTimeUnit = pseudoTimeUnit;
    }

    public void setSourceToSample ( double sourceToSample ) {
	header.sourceToSample = sourceToSample;
    }

    public void setSourceToChopper ( double sourceToChopper ) {
	header.sourceToChopper = sourceToChopper;
    }

    public void setModeratorCalibFile ( int moderatorCalibFile ) {
	header.moderatorCalibFile = moderatorCalibFile;
    }

    public void setGroupToMonitor ( short groupToMonitor ) {
	header.groupToMonitor = groupToMonitor;
    }

    public void setChannelToMonitor ( short channelToMonitor ) {
	header.channelToMonitor = channelToMonitor;
    }

    public void setNumberOfHistograms ( short numOfHistograms ) {
	header.numOfHistograms = numOfHistograms;
	if ( header.numOfElements > 0){
	    System.out.println( "numElements != 0: "  + header.numOfElements);
	    detectorMap = 
		new DetectorMap[numOfHistograms * (header.numOfElements) + 1];
	}
	else {
	    System.out.println( "numElements == 0: "  + header.numOfElements);
	    detectorMap = 
		new DetectorMap[numOfHistograms * (header.numOfElements + 1)];
	}
	for ( int i = 0; i < detectorMap.length; i++ ) {
	    detectorMap[i] = new DetectorMap( header.iName );
	}
	timeField = new TimeField[1];
	timeField[0] = new TimeField();
	minSubgroupID = new int[numOfHistograms + 1];
	maxSubgroupID = new int[numOfHistograms + 1];
	subgroupMap = new int[1][];
    }

    public void setNumOfTimeFields ( short numOfTimeFields ) {
	header.numOfTimeFields = numOfTimeFields;
    }

    public void setNumOfControl ( short numOfControl ) {
	header.numOfControl = numOfControl;
    }

    public void setControlFlag ( short controlFlag ) {
	header.controlFlag = controlFlag;
    }

    public void setClockShift ( short clockShift ) {
	header.clockShift = clockShift;
    }

    public void setTotalChannels ( int totalChannels ) {
	header.totalChannels = totalChannels;
    }

    public void setNumOfPulses ( int numOfPulses ) {
	header.numOfPulses = numOfPulses;
    }

    public void setSizeOfDataArea ( int sizeOfDataArea ) {
	header.sizeOfDataArea = sizeOfDataArea;
    }

    public void setHardwareTMin ( int hardwareTMin ) {
	header.hardwareTMin = hardwareTMin;
    }

    public void setHardwareTMax ( int hardwareTMax ) {
	header.hardwareTMax = hardwareTMax;
    }

    public void setHardTimeDelay ( int hardTimeDelay ) {
	header.hardTimeDelay = hardTimeDelay;
    }

    public void setNumOfX ( short numOfX ) {
	header.numOfX = numOfX;
    }

    public void setNumOfY ( short numOfY ) {
	header.numOfY = numOfY;
    }

    public void setNumOfWavelengths ( short numOfWavelengths ) {
	header.numOfWavelengths = numOfWavelengths;
    }

    public void setMaxWavelength ( int maxWavelength ) {
	header.maxWavelength = maxWavelength;
    }
    
    public void setMinWavelength ( int minWavelength ) {
	header.minWavelength = minWavelength;
    }

    public void setDta ( double dta ) {
	header.dta = dta;
    }

    public void setDtd ( double dtd ) {
	header.dtd = dtd;
    }

    public void setOmega (double omega ) {
	header.omega = omega;
    }

    public void setChi ( double chi ) {
	header.chi = chi;
    }

    public void setPhi ( double phi ) {
	header.phi = phi;
    }

    public void setXLeft ( double xLeft ) {
	header.xLeft = xLeft;
    }

    public void setXRight ( double xRight ) {
	header.xRight = xRight;
    }

    public void setYLower ( double yLower ) {
	header.yLower = yLower;
    }

    public void setYUpper ( double yUpper ) {
	header.yUpper = yUpper;
    }

    public void setXDisplacement ( double xDisplacement ) {
	header.xDisplacement = xDisplacement;
    }

    public void setYDisplacement ( double yDisplacement ) {
	header.yDisplacement = yDisplacement;
    }

    public void setXLength ( double xLength ) {
	header.xLength = xLength;
    }

    public void setAreaChannelWidth ( short areaChannelWidth ) {
	header.areaChannelWidth = areaChannelWidth;
    }

    public void setAreaDoubleInterval ( short areaDoubleInterval ) {
	header.areaDoubleInterval = areaDoubleInterval;
    }

    public void setAddressOf1DData ( int addressOf1DData ) {
	header.addressOf1DData = addressOf1DData;
    }

    public void setAddressOf2DData ( int addressOf2DData ) {
	header.addressOf2DData = addressOf2DData;
    }

    public void setEndOfOverflow ( int endOfOverflow ) {
	header.endOfOverflow = endOfOverflow;
    }

    public void setChannels1D ( int channels1D ) {
	header.channels1D = channels1D;
    }

    public void setNumOfOverflows ( short numOfOverflows ) {
	header.numOfOverflows = numOfOverflows;
    }

    public void setClockPeriod ( double clockPeriod ) {
	header.clockPeriod = clockPeriod;
    }

    public void setEnergyIn ( double energyIn ) {
	header.energyIn = energyIn;
    }

    public void setEnergyOut ( double energyOut ) {
	header.energyOut  = energyOut;
    }

    public void setNumOfSeqHist ( short numOfSeqHist ) {
	header.numOfSeqHist = numOfSeqHist;
    }

    public void setProtonCurrent ( double protonCurrent ) {
	header.protonCurrent = protonCurrent;
    }

    public void setAreaBinning ( short areaBinning ) {
	header.areaBinning = areaBinning;
    }

    public void setMicroprocessor ( short microprocessor ) {
	header.microprocessor = microprocessor;
    }

    public void setNumOfLockouts ( short numOfLockouts ) {
	header.numOfLockouts = numOfLockouts;
    }

    public void setFirstOverflow ( int firstOverflow ) {
	header.firstOverflow = firstOverflow;
    }

    public void setExpNum ( int expNum ) {
	header.expNum = expNum;
    }
    
    public void setFirstRun ( int firstRun ) {
	header.firstRun = firstRun;
    }
    
    public void setLastRun ( int lastRun ) {
	header.lastRun = lastRun;
    }

    public void setDefaultRun ( int defaultRun ) {
	header.defaultRun = defaultRun;
    }
    
    public void setSamplePos ( short samplePos ) {
	header.samplePos = samplePos;
    }

    public void setNumOfHeadBlocks ( short numOfHeadBlocks ) {
	header.numOfHeadBlocks = numOfHeadBlocks;
    }

    public void setOverflowSort ( short overflowSort ) {
	header.overflowSort = overflowSort;
    }

    public void setStandardClock ( double standardClock ) {
	header.standardClock = standardClock;
    }

    public void setLpsdClock ( double lpsdClock ) {
	header.lpsdClock = lpsdClock;
    }

    public void setIName ( String iName ) {
	header.iName = iName;
    }

    public void setNumOfElements ( int numOfElements ) {
	header.numOfElements = numOfElements;
    }

    public void addDetectorCoordSys( int[] detCoordSys ) {
	this.detCoordSys = new short[ detCoordSys.length +1 ];
	for ( int ii = 1; ii <= detCoordSys.length; ii++ ) {
	    this.detCoordSys[ii] = (short)detCoordSys[ii - 1];
	}
    }

    public void addDetectorCoordSys( short[] detCoordSys ) {
	this.detCoordSys = new short[ detCoordSys.length +1 ];
	System.arraycopy(detCoordSys, 0, this.detCoordSys, 1, 
			 detCoordSys.length);
    }

    public void addDetectorAngle( double[] detectorAngle ){
	this.detectorAngle = new float[detectorAngle.length + 1 ];
	for ( int ii = 1; ii <= detectorAngle.length; ii++ ) {
	    this.detectorAngle[ii] = (float)detectorAngle[ii - 1];
	}
    }

    public void addDetectorAngle( float[] detectorAngle ){
	this.detectorAngle = new float[detectorAngle.length + 1 ];
	System.arraycopy(detectorAngle, 0, this.detectorAngle, 1, 
			 detectorAngle.length);
    }

    public void addFlightPath( double[] flightPath ) {
	this.flightPath = new float[ flightPath.length + 1 ];
	for ( int ii = 1; ii <= flightPath.length; ii++ ) {
	    this.flightPath[ii] = (float)flightPath[ii - 1];
	}
    }

    public void addFlightPath( float[] flightPath ) {
	this.flightPath = new float[ flightPath.length + 1 ];
	System.arraycopy(flightPath, 0, this.flightPath, 1, 
			 flightPath.length);
    }

    public void addDetectorHeight( double[] detectorHeight ) {
	this.detectorHeight = new float[ detectorHeight.length + 1 ];
	for ( int ii = 1; ii <= detectorHeight.length; ii++ ) {
	    this.detectorHeight[ii] = (float)detectorHeight[ii - 1];
	}
    }

    public void addDetectorHeight( float[] detectorHeight ) {
	this.detectorHeight = new float[ detectorHeight.length + 1 ];
	System.arraycopy(detectorHeight, 0, this.detectorHeight, 1, 
			 detectorHeight.length);
    }

    public void addDetectorRot1( double[] detectorRot1 ){
	this.detectorRot1 = new float[detectorRot1.length + 1 ];
	for ( int ii = 1; ii <= detectorRot1.length; ii++ ) {
	    this.detectorRot1[ii] = (float)detectorRot1[ii - 1];
	}
    }

    public void addDetectorRot1( float[] detectorRot1 ){
	this.detectorRot1 = new float[detectorRot1.length + 1 ];
	System.arraycopy(detectorRot1, 0, this.detectorRot1, 1, 
			 detectorRot1.length);
    }

    public void addDetectorRot2( double[] detectorRot2 ){
	this.detectorRot2 = new float[detectorRot2.length + 1 ];
	for ( int ii = 1; ii <= detectorRot2.length; ii++ ) {
	    this.detectorRot2[ii] = (float)detectorRot2[ii - 1];
	}
    }

    public void addDetectorRot2( float[] detectorRot2 ){
	this.detectorRot2 = new float[detectorRot2.length + 1 ];
	System.arraycopy(detectorRot2, 0, this.detectorRot2, 1, 
			 detectorRot2.length);
    }

    public void addDetectorType( int[] detectorType ) {
	this.detectorType = new short[ detectorType.length +1 ];
	for ( int ii = 1; ii <= detectorType.length; ii++ ) {
	    this.detectorType[ii] = (short)detectorType[ii - 1];
	}
    }

    public void addDetectorType( short[] detectorType ) {
	this.detectorType = new short[ detectorType.length +1 ];
	System.arraycopy(detectorType, 0, this.detectorType, 1, 
			 detectorType.length);
    }

    public void addDetectorLength( double[] detectorLength ) {
	this.detectorLength = new float[ detectorLength.length + 1 ];
	for ( int ii = 1; ii <= detectorLength.length; ii++ ) {
	    this.detectorLength[ii] = (float)detectorLength[ii - 1];
	}
    }
    
    public void addDetectorLength( float[] detectorLength ) {
	this.detectorLength = new float[ detectorLength.length + 1 ];
	System.arraycopy(detectorLength, 0, this.detectorLength, 1, 
			 detectorLength.length);
    }
    
    public void addDetectorWidth( double[] detectorWidth ) {
	this.detectorWidth = new float[ detectorWidth.length + 1 ];
	for ( int ii = 1; ii <= detectorWidth.length; ii++ ) {
	    this.detectorWidth[ii] = (float)detectorWidth[ii - 1];
	}
    }

    public void addDetectorWidth( float[] detectorWidth ) {
	this.detectorWidth = new float[ detectorWidth.length + 1 ];
	System.arraycopy(detectorWidth, 0, this.detectorWidth, 1, 
			 detectorWidth.length);
    }

    public void addDetectorDepth( double[] detectorDepth ) {
	this.detectorDepth = new float[ detectorDepth.length + 1 ];
	for ( int ii = 1; ii <= detectorDepth.length; ii++ ) {
	    this.detectorDepth[ii] = (float)detectorDepth[ii - 1];
	}
    }

    public void addDetectorDepth( float[] detectorDepth ) {
	this.detectorDepth = new float[ detectorDepth.length + 1 ];
	System.arraycopy(detectorDepth, 0, this.detectorDepth, 1, 
			 detectorDepth.length);
    }

    public void addDetectorEfficiency( double[] detectorEfficiency ) {
	this.detectorEfficiency = new float[ detectorEfficiency.length + 1 ];
	for ( int ii = 1; ii <= detectorEfficiency.length; ii++ ) {
	    this.detectorEfficiency[ii] = (float)detectorEfficiency[ii - 1];
	}
    }

    public void addDetectorEfficiency( float[] detectorEfficiency ) {
	this.detectorEfficiency = new float[ detectorEfficiency.length + 1 ];
	System.arraycopy(detectorEfficiency, 0, this.detectorEfficiency, 1, 
			 detectorEfficiency.length);
    }

    public void addDetectorPsdOrder( int[] psdOrder ) {
	this.psdOrder = new int[ psdOrder.length +1 ];
	System.arraycopy(psdOrder, 0, this.psdOrder, 1, 
			 psdOrder.length);
    }

    public void addDetectorPsdOrder( short[] psdOrder ) {
	this.psdOrder = new int[ psdOrder.length +1 ];
	for ( int ii = 1; ii <= psdOrder.length; ii++ ) {
	    this.psdOrder[ii] = (int)psdOrder[ii - 1];
	}
    }

    public void addDetectorNumSegs1( int[] numSegs1 ) {
	this.numSegs1 = new int[ numSegs1.length +1 ];
	System.arraycopy(numSegs1, 0, this.numSegs1, 1, 
			 numSegs1.length);
    }

    public void addDetectorNumSegs1( short[] numSegs1 ) {
	this.numSegs1 = new int[ numSegs1.length +1 ];
	for ( int ii = 1; ii <= numSegs1.length; ii++ ) {
	    this.numSegs1[ii] = (int)numSegs1[ii - 1];
	}
    }

    public void addDetectorNumSegs2( int[] numSegs2 ) {
	this.numSegs2 = new int[ numSegs2.length +1 ];
	System.arraycopy(numSegs2, 0, this.numSegs2, 1, 
			 numSegs2.length);
    }

    public void addDetectorNumSegs2( short[] numSegs2 ) {
	this.numSegs2 = new int[ numSegs2.length +1 ];
	for ( int ii = 1; ii <= numSegs2.length; ii++ ) {
	    this.numSegs2[ii] = (int)numSegs2[ii - 1];
	}
    }

    public void addDetectorCrateNum( int[] crateNum ) {
	this.crateNum = new int[ crateNum.length +1 ];
	System.arraycopy(crateNum, 0, this.crateNum, 1, 
			 crateNum.length);
    }

    public void addDetectorSlotNum( int[] slotNum ) {
	this.slotNum = new int[ slotNum.length +1 ];
	System.arraycopy(slotNum, 0, this.slotNum, 1, 
			 slotNum.length);
    }

    public void addDetectorInputNum( int[] inputNum ) {
	this.inputNum = new int[ inputNum.length +1 ];
	System.arraycopy(inputNum, 0, this.inputNum, 1, 
			 inputNum.length);
    }

    public void addDetectorDataSource( int[] dataSource ) {
	this.dataSource = new int[ dataSource.length +1 ];
	System.arraycopy(dataSource, 0, this.dataSource, 1, 
			 dataSource.length);
    }

    public void addDetectorMinID( int[] minID ) {
	this.minID = new int[ minID.length +1 ];
	System.arraycopy(minID, 0, this.minID, 1, 
			 minID.length);
    }

    public void addDiscriminatorLevels( int[] lld, int[] uld ) {
	this.discriminator = new DiscLevel[ lld.length + 1 ];
	for (int ii = 1; ii <= lld.length; ii++ ) {
	    discriminator[ii] = new DiscLevel();
	    discriminator[ii].lowerLevel = lld[ii - 1];
	    discriminator[ii].upperLevel = uld[ii - 1];
	}
    }
 
    public void Write () {
	RandomAccessFile runfile;
	 int offsetToFree = header.numOfHeadBlocks * 512;
	try {
	runfile = new RandomAccessFile (runfileName, "rw" );
	//      	header.numOfElements = 0;

	header.Write( runfile );

	// Write Detector Angles
	offsetToFree = writeFloatTable( runfile, detectorAngle, 
					offsetToFree, 72);
	//Write Flight Path Lengths
	offsetToFree = writeFloatTable( runfile, flightPath, offsetToFree, 80);
	//Write detector heights
	offsetToFree = writeFloatTable( runfile, detectorHeight, 
					offsetToFree, 88);
	//Write detector type
	offsetToFree = writeShortTable( runfile, detectorType, offsetToFree, 
					96);
     	//Write detector length
	offsetToFree = writeFloatTable( runfile, detectorLength, offsetToFree, 
					700 );
	// Write detector widths
	offsetToFree = writeFloatTable( runfile, detectorWidth, offsetToFree, 
					708 );
	//  Write detector Depths
	offsetToFree = writeFloatTable( runfile, detectorDepth, offsetToFree, 
					716 );
	//  Write detector Maps
	if( detectorMap.length > 0 ) {
	    runfile.seek( 8 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt(  ( detectorMap.length - 1 ) * 4 );
	    runfile.seek( offsetToFree );
	    for( int ii = 1; ii < detectorMap.length; ii++ ) {
		detectorMap[ii].Write( runfile );
 	    }
	    offsetToFree = offsetToFree + ( detectorMap.length - 1 ) * 4;
	}

	//  Write time fields
	if( timeField.length > 0 ) {
	    runfile.seek( 16 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( timeField.length - 1) * 16 );
	    runfile.seek( offsetToFree );
	    for( int ii = 1; ii < timeField.length; ii++ ) {
		timeField[ii].Write( runfile );
	    }
	    offsetToFree = offsetToFree + ( timeField.length - 1 ) * 16;
	}

	//  Write detector subgroup map
	if( header.numOfHistograms * header.numOfElements > 0 ) {
	    runfile.seek( 728 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( header.numOfHistograms * header.numOfElements * 4 );
	    runfile.seek( offsetToFree );
	    int[][] IDList = new int[header.numOfHistograms][header.numOfElements];
	    for( int ii = 0; ii < header.numOfHistograms; ii++ ) {
		for ( int jj = 0; jj < header.numOfElements; jj++ ) {
		    IDList[ii][jj] = -1;
		}
	    }
	    for ( int kk = 0; kk < header.numOfHistograms; kk++ ){
		for ( int ii = minSubgroupID[kk+ 1]; 
		      ii <= maxSubgroupID[kk + 1]; ii++ ) {
		    if (subgroupMap[ii] != null ) {
			for (int jj = 0; jj < subgroupMap[ii].length; jj++ ){
			    IDList[kk][subgroupMap[ii][jj] -1] = ii;
			}
		    }
		}
	    }
		
	    for( int ii = 0; ii < header.numOfHistograms; ii++ ) {
		for ( int jj = 0; jj < header.numOfElements; jj++ ) {
		    runfile.writeInt( IDList[ii][jj] );
		}
	    }
	    offsetToFree += header.numOfHistograms * header.nDet * 4;
	}
	//Write detector coordinate system
	offsetToFree = writeShortTable( runfile, detCoordSys, offsetToFree, 
					736 );
	//  Write detector rotation angle 1
	offsetToFree = writeFloatTable( runfile, detectorRot1, offsetToFree, 
					744 );
	//  Write detector rotation angle 2
	offsetToFree = writeFloatTable( runfile, detectorRot2, offsetToFree, 
					752 );
	//  Write detector Efficiencies
	offsetToFree = writeFloatTable( runfile, detectorEfficiency, 
					offsetToFree, 760 );
	//  Write detector psd dimensionality 1 for lpsd, 2 for area
	offsetToFree = writeIntTable( runfile, psdOrder, offsetToFree, 768);

	//  Write # psd segments in 1st dimension
	offsetToFree = writeIntTable( runfile, numSegs1, offsetToFree, 776);

	//  Write # psd segments in 2nd dimension
	offsetToFree = writeIntTable( runfile, numSegs2, offsetToFree, 784);

	//  Write crate # for this detector
	offsetToFree = writeIntTable( runfile, crateNum, offsetToFree, 792);

	//  Write slot in crate # for this detector
	offsetToFree = writeIntTable( runfile, slotNum, offsetToFree, 800);

	//  Write input on slot # for this detector
	offsetToFree = writeIntTable( runfile, inputNum, offsetToFree, 808);

	//  Write data Source for this detector
	offsetToFree = writeIntTable( runfile, dataSource, offsetToFree, 816);

	//  Write input on slot # for this detector
	offsetToFree = writeIntTable( runfile, minID, offsetToFree, 824);

	//  Write detector Time Scaling factors 
	offsetToFree = writeFloatTable( runfile, timeScale, 
					offsetToFree, 24 );

	//  Write detector discriminator levels 
	if( discriminator.length > 1 ) {
	    runfile.seek( 520 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( discriminator.length - 1 ) * 8 );
	    runfile.seek( offsetToFree );
	    for( int ii = 1; ii < discriminator.length; ii++ ) {
		runfile.writeInt( discriminator[ii].lowerLevel );
		runfile.writeInt( discriminator[ii].upperLevel );
	    }
	    offsetToFree = offsetToFree + 
		( discriminator.length - 1 ) * 8;
	}


	header.totalChannels = header.channels1D;
	header.histStartAddress = offsetToFree;
	header.offsetToFree = header.histStartAddress + 
	    header.totalChannels * 4;
	header.sizeOfDataArea = header.totalChannels;

	runfile.seek( 56 );

	runfile.writeInt( header.histStartAddress );
	runfile.writeInt( header.offsetToFree/ 512 );
	runfile.writeInt( header.offsetToFree );
	
	runfile.seek ( 318 );
	runfile.writeInt( header.totalChannels );

	runfile.seek ( 326 );
	runfile.writeInt( header.sizeOfDataArea );

	runfile.seek( header.offsetToFree - 4 );
	runfile.writeInt( (int)0 );

	runfile.seek( 264 );
	runfile.writeByte( 1 );

	runfile.close();
	}
	catch ( IOException ex ) {
	    System.out.println("Error writ ing file " + runfileName );
	}
    }

    private int writeIntTable( RandomAccessFile runfile, int[] intList, 
			       int offsetToFree, int headLoc ) 
    throws IOException {
	try {
	    if( intList.length > 0 ) {
		runfile.seek( headLoc );
		runfile.writeInt( offsetToFree );
		runfile.writeInt( ( intList.length - 1 ) * 4 );
		runfile.seek( offsetToFree );
		for( int ii = 1; ii < intList.length; ii++ ) {
		    runfile.writeInt( intList[ii] );
		}
		offsetToFree = offsetToFree + 
		    ( intList.length - 1 ) * 4;
	    }
	}
	catch ( IOException ex ) {
	    System.out.println("Error writing IntTable: " + runfileName );
	    throw new IOException();
	}
	return offsetToFree;
 
    }

    private int writeShortTable( RandomAccessFile runfile, short[] intList, 
			       int offsetToFree, int headLoc ) 
    throws IOException {
	try {
	    if( intList.length > 0 ) {
		runfile.seek( headLoc );
		runfile.writeInt( offsetToFree );
		runfile.writeInt( ( intList.length - 1 ) * 4 );
		runfile.seek( offsetToFree );
		for( int ii = 1; ii < intList.length; ii++ ) {
		    runfile.writeShort( intList[ii] );
		}
		offsetToFree = offsetToFree + 
		    ( intList.length - 1 ) * 4;
	    }
	}
	catch ( IOException ex ) {
	    System.out.println("Error writing ShortTable: " + runfileName );
	    throw new IOException();
	}
	return offsetToFree;
 
    }

    private int writeFloatTable( RandomAccessFile runfile, float[] floatList, 
			       int offsetToFree, int headLoc ) 
	throws IOException{
	try {
	    if( floatList.length > 1 ) {
		runfile.seek( headLoc );
		runfile.writeInt( offsetToFree );
		runfile.writeInt( ( floatList.length - 1 ) * 4 );
		runfile.seek( offsetToFree );
		for( int ii = 1; ii < floatList.length; ii++ ) {
		    runfile.writeFloat( floatList[ii] );
		}
		offsetToFree = offsetToFree + 
		    ( floatList.length - 1 ) * 4;
	    }
	}
	catch ( IOException ex ) {
	    System.out.println("Error writing FloatTable: " + runfileName );
	    throw new IOException();
	}
	return offsetToFree;
    }
    public void addSubgroup( int[] ids, float minVal, float maxVal, 
			     float stepVal, int tDoubleLength, short nChanns, 
			     short timeFocus, short emissionDelay, 
			     short constDelay, short energyBin,
			     short wavelengthBin, short pulseHeightBin,
			     int hist, int sgNum ) {
	if ( sgNum > maxSubgroupID[hist + 1]) maxSubgroupID[hist + 1] = sgNum;
	if ( sgNum < minSubgroupID[hist + 1] || minSubgroupID[hist + 1] == 0 ) 
	    minSubgroupID[hist +1] = sgNum;
	float maxDiff = 0.0000001f;
 	int matchingTimeField;
	matchingTimeField = 0;
	if ( pulseHeightBin == (short)1 ) {
	    float tempStep=(float)(int)((maxVal-minVal)/stepVal);
	    short tempNchans= (short)stepVal;	    
	    stepVal = tempStep;
	    nChanns = tempNchans;
	}    
	if ( timeField.length > 1 ) {
	    for ( int ii = 1; ii < timeField.length; ii++ ) {
		if ( minVal == timeField[ii].tMin &&
		     maxVal == timeField[ii].tMax  &&
		     stepVal == timeField[ii].tStep &&
		     tDoubleLength == timeField[ii].tDoubleLength &&
		     nChanns == timeField[ii].numOfChannels &&
		     timeFocus == timeField[ii].timeFocusBit &&
		     emissionDelay == timeField[ii].emissionDelayBit &&
		     constDelay == timeField[ii].constantDelayBit &&
		     energyBin == timeField[ii].energyBinBit &&
		     wavelengthBin == timeField[ii].wavelengthBinBit &&
		     pulseHeightBin == timeField[ii].pulseHeightBit
			 ) { 
		    matchingTimeField = ii;
		}
	    }
	}
	if ( matchingTimeField == 0 ) {
	    // Add a time Field
	    TimeField[] tempFields = new TimeField[ timeField.length + 1 ];
 	    System.arraycopy ( timeField, 0, tempFields, 0, 
			       timeField.length );
	    matchingTimeField = timeField.length;
	    tempFields[matchingTimeField] = new TimeField();
	    tempFields[matchingTimeField].tMin = minVal;
	    tempFields[matchingTimeField].tMax = maxVal;
	    tempFields[matchingTimeField].tStep = stepVal;
	    tempFields[matchingTimeField].tDoubleLength = tDoubleLength;
	    tempFields[matchingTimeField].numOfChannels = nChanns;
	    tempFields[matchingTimeField].timeFocusBit = timeFocus;
	    tempFields[matchingTimeField].emissionDelayBit = emissionDelay;
	    tempFields[matchingTimeField].constantDelayBit = constDelay;
	    tempFields[matchingTimeField].energyBinBit = energyBin;
	    tempFields[matchingTimeField].wavelengthBinBit = wavelengthBin;
	    tempFields[matchingTimeField].pulseHeightBit = pulseHeightBin;

	    if ( timeFocus != 0 ) {
		header.pseudoTimeUnit = 'I';
	    }
	    timeField = tempFields;
				
	}
	header.numOfTimeFields = (short)(timeField.length -1);
	
	int[][] tempMap = new int[subgroupMap.length + 1][];
	System.arraycopy( subgroupMap, 0, tempMap, 0, subgroupMap.length );
	int[] segIDs = new int[ ids.length];
    for (int kk = 0;  kk < ids.length; kk++ ){
        	segIDs[kk] = minID[ids[kk]];
    }
	tempMap[subgroupMap.length] = segIDs;
	subgroupMap = tempMap;
	for ( int jj = 0; jj < ids.length; jj++ ) {
	    int index = ( hist ) * header.numOfElements + minID[ids[jj]];
	    detectorMap[index].tfType = matchingTimeField;
	    detectorMap[index].address = header.channels1D * 4;
	    header.channels1D = header.channels1D + nChanns + 1;
	    //	    subgroupMap[ hist ][ids[jj] - 1] = sgNum;
	    switch ( header.pseudoTimeUnit ) {

	    case ('I'): {
		if ( timeScale.length != (header.nDet + 1)  ) 
		    timeScale = new float[header.nDet +1 ];
		float refLength = 0.0f;
		if ( header.iName.equalsIgnoreCase("hrcs")) {
		    refLength = 4.0f;
		}
		else if (header.iName.equals("lrcs")) {
		    refLength = 2.5f;
		}
		    timeScale[ids[jj]] = (float)(refLength/
			Math.sqrt(
				  Math.pow(flightPath[ids[jj]], 2.0) 
				 + Math.pow( detectorLength[ids[jj]]/200, 2.0 ))
						 );
		break;
	    }
	    default: {
		break;
	    }
	    }
	}
     }


    /**
       This method sets a value in the runfile header. 
       @param element The String code for the element to be modified
       @param val The value to be placed in the field.
       @return A return error code
     */
    public int headerSet( String element, double val ) {
	int rval = this.header.set(element,(double) val);
	return rval;
    }

    /**
       This method sets a value in the runfile header. 
       @param element The String code for the element to be modified
       @param val The value to be placed in the field.
       @return A return error code
     */
    public int headerSet( String element, float val ) {
	int rval = this.header.set(element, (float)val);
	return rval;
    }

    /**
       This method sets a value in the runfile header. 
       @param element The String code for the element to be modified
       @param val The value to be placed in the field.
       @return A return error code
     */
    public int headerSet( String element, int val ) {
	int rval = this.header.set(element, (int)val);
	return rval;
    }

    /**
       This method sets a value in the runfile header. 
       @param element The String code for the element to be modified
       @param val The value to be placed in the field.
       @return A return error code
     */
    public int headerSet( String element, short val ) {
	if (element.equalsIgnoreCase("numOfHistograms")) {
	    setNumberOfHistograms(val);
	}
	int rval = this.header.set(element, (short)val);
	return rval;
    }

    /**
       This method sets a value in the runfile header. 
       @param element The String code for the element to be modified
       @param val The value to be placed in the field.
       @return A return error code
     */
    public int headerSet( String element, String val ) {
	int rval = this.header.set(element, (String)val);
	return rval;
    }

    /**
       This method will set values in the runfile header that are normally 
       from the instrument parameter file.
       Calibration file is the default
    */
    public int headerSetFromParams(){
	int rval = headerSetFromParams(getDefParamFileName());
	return (rval);
    }

    /**
       This method will set values in the runfile header that are normally 
       from the instrument parameter file.
       @param paramfilename = filename from which parameters should be taken
       @return a return error code
    */
    public int headerSetFromParams( String filename){
	int rval = 0;
	Properties params = new Properties();
	FileInputStream paramFile;
	
        try {
            paramFile = new FileInputStream(filename);
            params = new Properties();
            params.load(paramFile);
        }
        catch (IOException e) {
            System.out.println("Can't open file " + filename);
            return(-1);
        }
	this.header.sourceToSample = 
            (new Double(params.getProperty("SourceToSample"))).doubleValue();
        this.header.sourceToChopper =
            (new Double(params.getProperty("SourceToChopper"))).doubleValue();
        this.header.standardClock = 
            (new Double(params.getProperty("StdClock"))).doubleValue();
        this.header.lpsdClock =
            (new Double(params.getProperty("LpsdClock"))).doubleValue();
        this.header.clockPeriod =
            (new Double(params.getProperty("AreaClock"))).doubleValue();
        this.header.dta = 
            (new Double(params.getProperty("DetA"))).doubleValue();
        this.header.dtd =
            (new Double(params.getProperty("DetD"))).doubleValue();
        this.header.chi =
            (new Double(params.getProperty("Chi"))).doubleValue();
        this.header.phi =
            (new Double(params.getProperty("Phi"))).doubleValue();
        this.header.omega =
            (new Double(params.getProperty("Omega"))).doubleValue();
        this.header.xLeft =
            (new Double(params.getProperty("XLeft"))).doubleValue();
        this.header.xRight =
            (new Double(params.getProperty("XRight"))).doubleValue();
        this.header.yUpper =
            (new Double(params.getProperty("YUpper"))).doubleValue();
        this.header.yLower =
            (new Double(params.getProperty("YLow"))).doubleValue();
        this.header.xDisplacement =
            (new Double(params.getProperty("XDisplacement"))).doubleValue();
        this.header.yDisplacement =
            (new Double(params.getProperty("YDisplacement"))).doubleValue();
        this.header.xLength =
            (new Double(params.getProperty("XLength"))).doubleValue();
        this.header.detCalibFile = 
	    Integer.parseInt(params.getProperty("DetectorCal"));
        this.header.moderatorCalibFile =
	    Integer.parseInt(params.getProperty("ModeratorCal"));
	return (0);
    }

    

    /**
       This method will set values in the runfile header that are normally 
       from the detector calibration table.
       Calibration file name is the instrument default.
       @return a return error code
    */
    public int headerSetFromDCalib(){
	int rval = headerSetFromDCalib(getDefDCalibFileName());
	return (rval);
    }
    
    /**
       This method will set values in the runfile header that are normally 
       from the detector calibration table.
       @param paramfilename = filename from which calibration data should be 
       taken.
       @return a return error code
    */
    public int headerSetFromDCalib( String filename){
	int rval = 0;
	DC5 dCalib = new DC5();
        try {
            dCalib = new DC5(filename);
        }
        catch (IOException ex) {
            System.out.println(" Trouble opening detector calibration file");
        }
        this.addDetectorCoordSys(dCalib.CoordSys());
        this.addDetectorAngle(dCalib.Angles());
        this.addFlightPath(dCalib.FlightPath());
        this.addDetectorHeight(dCalib.Height());
        this.addDetectorRot1(dCalib.Rot1());
        this.addDetectorRot2(dCalib.Rot1());
        this.addDetectorType(dCalib.Type());
        this.addDetectorLength(dCalib.Length());
        this.addDetectorWidth(dCalib.Width());
        this.addDetectorDepth(dCalib.Depth());
        this.addDetectorEfficiency(dCalib.Efficiency());
        this.addDetectorPsdOrder(dCalib.PsdOrder());
        this.addDetectorNumSegs1(dCalib.NumSegs1());
        this.addDetectorNumSegs2(dCalib.NumSegs2());
        this.addDetectorCrateNum(dCalib.Crate());
        this.addDetectorSlotNum(dCalib.Slot());
        this.addDetectorInputNum(dCalib.Input());
        this.addDetectorDataSource(dCalib.DataSource());
        this.addDetectorMinID(dCalib.MinID());
	this.header.nDet = (short)dCalib.NDet();
	segments = new Segment[1];
	segments[0] = new Segment();
	for (int ii = 1; ii <= header.nDet; ii++ ) {
	    if (detectorType[ii] != 0 ) {
		int segs = minID[ii] + numSegs1[ii] * numSegs2[ii] - 1;
		if ( segments.length < ( segs + 1 ) ){
		    Segment [] tsegments =new Segment[segs + 1];
		    System.arraycopy( segments, 0 , tsegments, 0, 
				      segments.length);
		    segments = tsegments;
		}
		for ( int segY = 0; segY < numSegs1[ii]; segY ++ ) {
		    for ( int segX = 0; segX < numSegs2[ii]; segX ++ ) {
			int segID = minID[ii] + segX + segY * numSegs2[ii];
			segments[segID] = 
			    new Segment( ii, segX +1 , segY + 1, 
					 detectorLength[ii]/numSegs1[ii],
					 detectorWidth[ii]/numSegs2[ii],
					 detectorDepth[ii],
					 detectorEfficiency[ii],
					 segID
					 );
			
		    }
		}
	    }
	}
	header.numOfElements = segments.length - 1;
	System.out.println("numOfSegment: " + header.numOfElements);
	return rval;
    }

    /**
     This method sets discriminator values int the Runfile.
     The file is assumed to be the default file.
     */
    public int headerSetFromDiscFile(){
	int rval = headerSetFromDiscFile(getDefDiscFileName());
	return rval;
    }

    /**
     This method sets discriminator values int the Runfile 
     */
    public int headerSetFromDiscFile(String filename){
	DS5 disc = new DS5();
	int rval = 0;
        try {
            disc = 
		new DS5(filename);
        }
        catch (IOException ex) {
            System.out.println(" Trouble opening discriminator file");
	    rval = 1;
	    return (rval);
        }
        addDiscriminatorLevels(disc.Lld(), disc.Uld());
	return (rval);
    }

    /**
       This method will set start date and time  in the runfile header to the 
       current time and date.
       @return a return error code
    */
    public int startDateAndTimeToCurrent( ) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, 
							   Locale.FRENCH);
        String date = (dateFormat.format(
            new Date())).toUpperCase();
        String time = timeFormat.format(
            new Date());
	String fixedDate = new String();
	if (date.length() == 12 ) {
	    fixedDate = new String(date.substring(4,6) + "-" 
				   + date.substring(0,3)
				   + "-" + date.substring(10,12) );
	}
	else {
	    fixedDate = new String("0" +date.substring(4,5) + "-" 
				   + date.substring(0,3)
				   + "-" + date.substring(9,11) );
	}
      	System.out.println( "Date: " + date + "   " + fixedDate );
      	System.out.println( "Time: " + time );
	header.startDate = fixedDate;
	header.startTime = time;
	return (0);
    }

    /**
       This method will set end date and time  in the runfile header to the 
       current time and date.
       @return a return error code
    */
    public int endDateAndTimeToCurrent( ) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, 
							   Locale.FRENCH);
        String date = (dateFormat.format(
            new Date())).toUpperCase();
        String time = timeFormat.format(
            new Date());
	String fixedDate = new String(date.substring(4,6) + "-" 
				      + date.substring(0,3)
				      + "-" + date.substring(9,11) );
	header.endDate = fixedDate;
	header.endTime = time;
	return (0);
    }

    /**
       Adds a new TimeField to a Runfile
       @param min Minimum time for the field
       @param max Maximum time for the field
       @param TFNum Time Field Number
       @param step Step size for the field
       @param TFNum Time Field Number
     */
    public int addNormalTimeField(float min, float max, float step, 
				  int TFNum) {
				  
	int rval = 0;
	if ( (min >= max) && (step >= (max-min)) ) {
	    rval = -98;
	    System.out.println( "Invalid parameters in addNormalTimeFields" +
				"(min, max, step): (" + min + ", " + max +
				", " + step + ")");
	    return (rval);
	}
	TimeField[] tempFields = new TimeField[0]; 
	if ( TFNum + 1 > timeField.length ) {
	    tempFields = new TimeField[ TFNum + 1 ];
	    System.arraycopy ( timeField, 0, tempFields, 0, 
			       timeField.length );
	    for (int ii= timeField.length; ii <= TFNum; ii++){
		tempFields[ii] = new TimeField();
	    }
	}
	else {
	    if ( timeField[TFNum].isUsed() ) {
		System.out.println( "TimeField[" + TFNum 
				    + "] is already used");
		return -99;
	    }
	    else {
		tempFields = timeField;
	    }
	}
	int matchingTimeField = TFNum;
	tempFields[matchingTimeField] = new TimeField();
	tempFields[matchingTimeField].tMin = min;
	tempFields[matchingTimeField].tMax = max;
	tempFields[matchingTimeField].tStep = step;
	tempFields[matchingTimeField].tDoubleLength = 32768;
	tempFields[matchingTimeField].numOfChannels = (short)((max-min)/step);
	tempFields[matchingTimeField].timeFocusBit = 0;
	tempFields[matchingTimeField].emissionDelayBit = 0;
	tempFields[matchingTimeField].constantDelayBit = 0;
	tempFields[matchingTimeField].energyBinBit = 0;
	tempFields[matchingTimeField].wavelengthBinBit = 0;
	tempFields[matchingTimeField].pulseHeightBit = 0;
	tempFields[matchingTimeField].used = true;
	timeField = tempFields;
	header.numOfTimeFields = (short)(timeField.length - 1);
	return(rval);

    }

    /**
       Adds a new TimeField to a Runfile
       @param min Minimum time for the field
       @param max Maximum time for the field
       @param step Step size for the field
       @param TFNum Time Field Number
     */
    public int addFocusedTimeField(float min, float max, float step, 
				  int TFNum ) {
				  
	int rval = 0;
	if ( (min >= max) && (step >= (max-min)) ) {
	    rval = -98;
	    System.out.println( "Invalid parameters in addFocusedTimeFields" +
				"(min, max, step): (" + min + ", " + max +
				", " + step + ")");
	    return (rval);
	}
	TimeField[] tempFields = new TimeField[0]; 
	if ( TFNum + 1 > timeField.length ) {
	    tempFields = new TimeField[ TFNum + 1 ];
	    System.arraycopy ( timeField, 0, tempFields, 0, 
			       timeField.length );
	    for (int ii= timeField.length; ii <= TFNum; ii++){
		tempFields[ii] = new TimeField();
	    }
	}
	else {
	    if ( timeField[TFNum].isUsed() ) {
		System.out.println( "TimeField[" + TFNum 
				    + "] is already used");
		return -99;
	    }
	    else {
		tempFields = timeField;
	    }
	}
	int matchingTimeField = TFNum;
	tempFields[matchingTimeField] = new TimeField();
	tempFields[matchingTimeField].tMin = min;
	tempFields[matchingTimeField].tMax = max;
	tempFields[matchingTimeField].tStep = step;
	tempFields[matchingTimeField].tDoubleLength = 32768;
	tempFields[matchingTimeField].numOfChannels = (short)((max-min)/step);
	tempFields[matchingTimeField].timeFocusBit = 1;
	tempFields[matchingTimeField].emissionDelayBit = 0;
	tempFields[matchingTimeField].constantDelayBit = 0;
	tempFields[matchingTimeField].energyBinBit = 0;
	tempFields[matchingTimeField].wavelengthBinBit = 0;
	tempFields[matchingTimeField].pulseHeightBit = 0;
	tempFields[matchingTimeField].used = true;
	timeField = tempFields;
	header.numOfTimeFields = (short)(timeField.length - 1);

	return(rval);

    }

    /**
       Adds a new TimeField to a Runfile
       @param min Minimum time for the field
       @param max Maximum time for the field
       @param step number of steps for the field
       @param TFNum Time Field Number
     */
    public int addPulseHeightTimeField(float min, float max, float step, 
				  int TFNum ) {
				  
	int rval = 0;
	if ( (min >= max) && (step >= (max-min)) ) {
	    rval = -98;
	    System.out.println( "Invalid parameters in addPuseHeight" + 
				"TimeFields" +
				"(min, max, step): (" + min + ", " + max +
				", " + step + ")");
	    return (rval);
	}
	TimeField[] tempFields = new TimeField[0]; 
	if ( TFNum + 1 > timeField.length ) {
	    tempFields = new TimeField[ TFNum + 1 ];
	    System.arraycopy ( timeField, 0, tempFields, 0, 
			       timeField.length );
	    for (int ii= timeField.length; ii <= TFNum; ii++){
		tempFields[ii] = new TimeField();
	    }
	}
	else {
	    if ( timeField[TFNum].isUsed() ) {
		System.out.println( "TimeField[" + TFNum 
				    + "] is already used");
		return -99;
	    }
	    else {
		tempFields = timeField;
	    }
	}
	int matchingTimeField = TFNum;
	tempFields[matchingTimeField] = new TimeField();
	tempFields[matchingTimeField].tMin = min;
	tempFields[matchingTimeField].tMax = max;
	tempFields[matchingTimeField].tStep = (max-min)/step;
	tempFields[matchingTimeField].tDoubleLength = 32768;
	tempFields[matchingTimeField].numOfChannels = (short)step;
	tempFields[matchingTimeField].timeFocusBit = 0;
	tempFields[matchingTimeField].emissionDelayBit = 0;
	tempFields[matchingTimeField].constantDelayBit = 0;
	tempFields[matchingTimeField].energyBinBit = 0;
	tempFields[matchingTimeField].wavelengthBinBit = 0;
	tempFields[matchingTimeField].pulseHeightBit = 1;
	tempFields[matchingTimeField].used = true;
	timeField = tempFields;
	header.numOfTimeFields = (short)(timeField.length - 1);

	return(rval);

    }

    /**
       Adds a new TimeField to a Runfile
       @param min Minimum energy for the field
       @param max Maximum energy for the field
       @param step Step size for the field
       @param TFNum Time Field Number
     */
    public int addEnergyTimeField(float min, float max, float step, 
				  int TFNum ) {
				  
	int rval = 0;
	if ( (min >= max) && (step >= (max-min)) ) {
	    rval = -98;
	    System.out.println( "Invalid parameters in addEnergyTimeFields" +
				"(min, max, step): (" + min + ", " + max +
				", " + step + ")");
	    return (rval);
	}
	TimeField[] tempFields = new TimeField[0]; 
	if ( TFNum + 1 > timeField.length ) {
	    tempFields = new TimeField[ TFNum + 1 ];
	    System.arraycopy ( timeField, 0, tempFields, 0, 
			       timeField.length );
	    for (int ii= timeField.length; ii <= TFNum; ii++){
		tempFields[ii] = new TimeField();
	    }
	}
	else {
	    if ( timeField[TFNum].isUsed() ) {
		System.out.println( "TimeField[" + TFNum 
				    + "] is already used");
		return -99;
	    }
	    else {
		tempFields = timeField;
	    }
	}
	int matchingTimeField = TFNum;
	tempFields[matchingTimeField] = new TimeField();
	tempFields[matchingTimeField].tMin = min;
	tempFields[matchingTimeField].tMax = max;
	tempFields[matchingTimeField].tStep = step;
	tempFields[matchingTimeField].tDoubleLength = 32768;
	tempFields[matchingTimeField].numOfChannels = (short)((max-min)/step);
	tempFields[matchingTimeField].timeFocusBit = 0;
	tempFields[matchingTimeField].emissionDelayBit = 0;
	tempFields[matchingTimeField].constantDelayBit = 0;
	tempFields[matchingTimeField].energyBinBit = 1;
	tempFields[matchingTimeField].wavelengthBinBit = 0;
	tempFields[matchingTimeField].pulseHeightBit = 0;
	tempFields[matchingTimeField].used = true;
	timeField = tempFields;
	header.numOfTimeFields = (short)(timeField.length - 1);

	return(rval);

    }

    /**
       Adds a new TimeField to a Runfile
       @param min Minimum energy for the field
       @param max Maximum energy for the field
       @param step Step size for the field
       @param TFNum Time Field Number
     */
    public int addWavelengthTimeField(float min, float max, float step, 
				  int TFNum ) {
				  
	int rval = 0;
	if ( (min >= max) && (step >= (max-min)) ) {
	    rval = -98;
	    System.out.println( "Invalid parameters in addWavelengthTimeFields" +
				"(min, max, step): (" + min + ", " + max +
				", " + step + ")");
	    return (rval);
	}
	TimeField[] tempFields = new TimeField[0]; 
	if ( TFNum + 1 > timeField.length ) {
	    tempFields = new TimeField[ TFNum + 1 ];
	    System.arraycopy ( timeField, 0, tempFields, 0, 
			       timeField.length );
	    for (int ii= timeField.length; ii <= TFNum; ii++){
		tempFields[ii] = new TimeField();
	    }
	}
	else {
	    if ( timeField[TFNum].isUsed() ) {
		System.out.println( "TimeField[" + TFNum 
				    + "] is already used");
		return -99;
	    }
	    else {
		tempFields = timeField;
	    }
	}
	int matchingTimeField = TFNum;
	tempFields[matchingTimeField] = new TimeField();
	tempFields[matchingTimeField].tMin = min;
	tempFields[matchingTimeField].tMax = max;
	tempFields[matchingTimeField].tStep = step;
	tempFields[matchingTimeField].tDoubleLength = 32768;
	tempFields[matchingTimeField].numOfChannels = (short)((max-min)/step);
	tempFields[matchingTimeField].timeFocusBit = 0;
	tempFields[matchingTimeField].emissionDelayBit = 0;
	tempFields[matchingTimeField].constantDelayBit = 0;
	tempFields[matchingTimeField].energyBinBit = 0;
	tempFields[matchingTimeField].wavelengthBinBit = 1;
	tempFields[matchingTimeField].pulseHeightBit = 0;
	tempFields[matchingTimeField].used = true;
	timeField = tempFields;
	header.numOfTimeFields = (short)(timeField.length - 1);

	return(rval);

    }

    /**
       Makes a separate subgroup for all detector Elements
       @param tf The time Field to associate with all detectors.
       @return an error code.
     */
    public int groupAllSeparate(int tf, int hist) {
	int rval = 0;
	int numBogusSegs = 0;
	if ( !timeField[tf].isUsed() ) {
	    System.out.println( "Error in groupAllSeparate: Time Field " + 
				tf + " is not valid.");
	}
	for (int ii = 1; ii <= header.numOfElements; ii++ ) {
	    int index = (hist - 1) * (header.numOfElements + 1 ) + ii;
	    if (segments[index] == null ){
		numBogusSegs++;
	    }
	}
       	int[][] tempMap = 
	    new int[subgroupMap.length + header.numOfElements- numBogusSegs][];
       	System.arraycopy( subgroupMap, 0, tempMap, 0, subgroupMap.length);

	numBogusSegs = 0;
	for (int ii = 1; ii <= header.numOfElements; ii++ ) {
	    int index = (hist - 1) * (header.numOfElements + 1 ) + ii;
	    if (segments[index] != null ){
		int segID = segments[index].segID;
		if (minSubgroupID[hist ] > segID || minSubgroupID[hist] == 0 ) 
		    minSubgroupID[hist] = segID-numBogusSegs;
		if (maxSubgroupID[hist] < ii ) maxSubgroupID[hist] = segID - numBogusSegs;
		tempMap[subgroupMap.length +ii -1 - numBogusSegs] = new int[1];
		tempMap[subgroupMap.length +ii -1 - numBogusSegs][0] = segID;
		
		detectorMap[index].tfType= tf;
		detectorMap[index].address = header.channels1D;
		header.channels1D += timeField[tf].numOfChannels;
 		
	    }
	    else {
		numBogusSegs++;
		//		tempMap[subgroupMap.length +ii -1] = new int[0];
		//		tempMap[subgroupMap.length +ii -1][0] = segID;
		
	    }
	}
       	subgroupMap = tempMap;
	System.out.println("minSubgroupID[" + hist + "], maxSubgroupID[" +
			   hist + "] = " +minSubgroupID[hist] + ", " +
			   maxSubgroupID[hist] );
	return (rval);
    }
    public Object clone() {
	try {
	    RunfileBuilder copy = (RunfileBuilder)super.clone();

	    return copy;

	} catch (CloneNotSupportedException ex ) { 
	    throw new Error ( "Error Cloning RunfileBuilder Object" );
	}
    }

    /**
       This method returns the instrument default discriminator file 
       name.
    */
    public String getDefDiscFileName() {
	Properties iDat = new Properties();
	String instDir = new String();
	String filename = new String();
        String home = System.getProperty("user.home");
        String fileSep = System.getProperty("file.separator");
        String datFileName = new String(home + fileSep + "inst" + fileSep + 
					header.iName + ".dat");
        iDat = new Properties();
        try {
            FileInputStream datFile = new FileInputStream(datFileName);
            iDat.load(datFile);
            instDir = iDat.getProperty("instDir");
        }
        catch (IOException e) {
            System.out.println("Can't open file " + datFileName);
            System.exit(0);
        }
	filename = new String (iDat.getProperty("instDir") + 
			       System.getProperty("file.separator") +
			       iDat.getProperty("iName") + ".ds5");
	return ( filename );
    }

    /**
       This method returns the instrument default discriminator file 
       name.
    */
    public String getDefParamFileName() {
	Properties iDat = new Properties();
	String instDir = new String();
	String filename = new String();
        String home = System.getProperty("user.home");
        String fileSep = System.getProperty("file.separator");
        String datFileName = new String(home + fileSep + "inst" + fileSep + 
					header.iName + ".dat");
        iDat = new Properties();
        try {
            FileInputStream datFile = new FileInputStream(datFileName);
            iDat.load(datFile);
            instDir = iDat.getProperty("instDir");
        }
        catch (IOException e) {
            System.out.println("Can't open file " + datFileName);
            System.exit(0);
        }
	filename = new String (instDir + fileSep + header.iName + 
			       "__V5.par");
	return ( filename );
    }

    /**
       This method returns the instrument default detector calibration file 
       name.
    */
    public String getDefDCalibFileName() {
	Properties iDat = new Properties();
	Properties params = new Properties();
	String instDir = new String();
	String filename = new String();
        String home = System.getProperty("user.home");
        String fileSep = System.getProperty("file.separator");
        String datFileName = new String(home + fileSep + "inst" + fileSep + 
					header.iName + ".dat");
        iDat = new Properties();
        try {
            FileInputStream datFile = new FileInputStream(datFileName);
            iDat.load(datFile);
            instDir = iDat.getProperty("instDir");
        }
        catch (IOException e) {
            System.out.println("Can't open file " + datFileName);
            System.exit(0);
        }
        String paramFileName = new String(instDir + fileSep + header.iName + 
					  "__V5.par");
        try {
            FileInputStream paramFile = new FileInputStream(paramFileName);
            params = new Properties();
            params.load(paramFile);
        }
        catch (IOException e) {
            System.out.println("Can't open file " + paramFileName);
            System.exit(0);
        }
	int CalNum = Integer.parseInt(params.getProperty("DetectorCal"));
	filename = new String ((iDat.getProperty("instDir") + 
				    System.getProperty("file.separator") + 
				    iDat.getProperty("iName") +
		              new DecimalFormat("0000").format((long)CalNum) + 
				    ".dc5"));
	return ( filename );
    }

    public int addAncillaryEquipment(String inName) {
	int rval = 0;
	ParameterFile[] tparams = new ParameterFile[params.length + 1];
	System.arraycopy(params, 0, tparams, 0, params.length );
	tparams[params.length] = new ParameterFile(inName);
	params = tparams;
	int thisParam = params.length - 1;
	JOptionPane mainframe = new JOptionPane(new ParamPane(new String("User"), 
					 params[thisParam].getDeviceName(), 
					 params[thisParam].getUserParameters(),
					 params[thisParam].getUserParameters().
					 length ) );
	String[] options = new String[1];
	options[0] = new String("OK");
	mainframe.setOptions(options);
	/*mainframe.getContentPane().add( this, );*/
	//	mainframe.pack();
	//mainframe.show();
	JDialog dialog = mainframe.createDialog(JOptionPane.getRootFrame(),new String("Enter User Parameters"));
	dialog.show();
	//	Object value = mainframe.getValue();
	//	if ( value == null ) {}
	header.numOfControl += 1;   
	//	mainframe = null;
	dialog.dispose();
	//	value = null;
	System.gc();
	return (rval);
   }
}
