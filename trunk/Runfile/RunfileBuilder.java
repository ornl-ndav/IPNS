package IPNS.Runfile;

import java.io.*;

public class RunfileBuilder extends Runfile implements Cloneable{

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
	timeScale = new float[1];
	timeShift = new float[1];
	areaStartTime = new float[1];
	timeDelay = new float[1];
	timeField = new TimeField[1];
	detectorMap = new DetectorMap[1];
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
	detectorMap = new DetectorMap[numOfHistograms * header.nDet + 1];
	for ( int i = 0; i < detectorMap.length; i++ ) {
	    detectorMap[i] = new DetectorMap();
	}
	timeField = new TimeField[1];
	timeField[0] = new TimeField();
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

    public void Write () {
	RandomAccessFile runfile;
	 int offsetToFree = header.numOfHeadBlocks * 512;

	try {
	runfile = new RandomAccessFile (runfileName, "rw" );
	header.Write( runfile );

	// Write Detector Angles
	if (detectorAngle.length > 0 ){
	    runfile.seek( 72 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( detectorAngle.length - 1 ) * 4 );
	    runfile.seek( offsetToFree );
	    for ( int ii = 1; ii < detectorAngle.length; ii++ ) {
		runfile.writeFloat( detectorAngle[ii] );
	    }
	    offsetToFree = offsetToFree + (detectorAngle.length - 1) * 4;
	}
	//Write Flight Path Lengths
	if ( flightPath.length > 0 ) {
	    runfile.seek( 80 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( flightPath.length - 1 )* 4 );
	    runfile.seek( offsetToFree );
	    for ( int ii = 1; ii < flightPath.length; ii++ ) {
		runfile.writeFloat( flightPath[ii] );
	    }
	    offsetToFree = offsetToFree + ( flightPath.length - 1 ) * 4;
	}
	//Write detector heights
	if (detectorHeight.length > 0 ) {
	    runfile.seek( 88 );
 	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( detectorHeight.length - 1 ) * 4 );
	    runfile.seek( offsetToFree );
	    for ( int ii = 1; ii < detectorHeight.length; ii++ ) {
		runfile.writeFloat( detectorHeight[ii] );
	    }
	    offsetToFree = offsetToFree + ( detectorHeight.length - 1 ) * 4;
	}
	//Write detector type
	if ( detectorType.length > 0 ) {
	    runfile.seek( 96 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( detectorType.length - 1 )* 2 );
	    runfile.seek( offsetToFree );
	    for ( int ii = 1; ii < detectorType.length; ii++ ) {
		runfile.writeShort( detectorType[ii] );
	    }
	    offsetToFree = offsetToFree + ( detectorType.length - 1 )* 2;
	}
	//Write detector length
	if( detectorLength.length > 0 ) { 
	    runfile.seek( 700 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( detectorLength.length - 1 ) * 4 );
	    runfile.seek( offsetToFree );
	    for( int ii = 1; ii < detectorLength.length; ii++ ) {
		runfile.writeFloat( detectorLength[ii] );
	    }
	    offsetToFree = offsetToFree + ( detectorLength.length -  1 ) * 4;
	}
	// Write detector widths
	if( detectorWidth.length > 0 ) {
	    runfile.seek( 708 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( detectorWidth.length - 1 ) * 4 );
	    runfile.seek( offsetToFree );
	    for( int ii = 1; ii < detectorWidth.length; ii++ ) {
		runfile.writeFloat( detectorWidth[ii] );
	    }
	    offsetToFree = offsetToFree + ( detectorWidth.length - 1 ) * 4;
	}
	//  Write detector Depths
	if( detectorDepth.length > 0 ) {
	    runfile.seek( 716 );
	    runfile.writeInt( offsetToFree );
	    runfile.writeInt( ( detectorDepth.length - 1 ) * 4 );
	    runfile.seek( offsetToFree );
	    for( int ii = 1; ii < detectorDepth.length; ii++ ) {
		runfile.writeFloat( detectorDepth[ii] );
	    }
	    offsetToFree = offsetToFree + ( detectorDepth.length - 1 ) * 4;
	}
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

    public void addSubgroup( int[] ids, float minVal, float maxVal, 
			     float stepVal, int tDoubleLength, short nChanns, 
			     short timeFocus, short emissionDelay, 
			     short constDelay, short energyBin,
			     short wavelengthBin, short pulseHeightBin,
			     int hist ) {
	float maxDiff = 0.0000001f;
	int matchingTimeField;
	matchingTimeField = 0;
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

	    timeField = tempFields;
				
	}
	header.numOfTimeFields = (short)(timeField.length -1);
	
	for ( int jj = 0; jj < ids.length; jj++ ) {
	    int index = ( hist ) * header.nDet + ids[jj];
	    detectorMap[index].tfType = matchingTimeField;
	    detectorMap[index].address = header.channels1D * 4;
	    header.channels1D = header.channels1D + nChanns + 1;
	}
    }


    public Object clone() {
	try {
	    RunfileBuilder copy = (RunfileBuilder)super.clone();

	    return copy;

	} catch (CloneNotSupportedException ex ) { 
	    throw new Error ( "Error Cloning RunfileBuilder Object" );
	}
    }

}
