package IPNS.Runfile;

import java.io.*;

/**
This class is a utility class for the IPNS.Runfile package.  This class sets
up and loads entries in the Time Field table stored in IPNS Runfiles.
Access to members is limited to members of the package.

*/

class TimeField{

    double tMin;
    double tMax;
    double tStep;
    int tDoubleLength;
    short numOfChannels;
    short timeFocusBit;
    short emissionDelayBit;
    short constantDelayBit;
    short energyBinBit;
    short wavelengthBinBit;
    short pulseHeightBit;
    short gBit;
    short hBit;

    String iName;
    /**
       This function provides a test method for this class' functionality.  It
       will provide a sampling of the information that is retrieved as a new 
       TimeField Object is created.  It accepts a filename as the first command
       line argument.

       @param args - The first command line parameter is the runfile name.  
               This parameter should contain the file path unless the file is
	       in the current directory.

    */
    public static void main(String[] args) throws IOException {
	int i;
	int numTimeTableEntries;

        RandomAccessFile runfile = new RandomAccessFile(
							args[0], "r");
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
	Header header = new Header(runfile, iName );
	numTimeTableEntries = header.timeFieldTable.size / 16;
	System.out.println("Number of Time Field entries in the table: " +
			   numTimeTableEntries);
	TimeField[] timeField = new TimeField[numTimeTableEntries+1];
	for (i=1; i <= numTimeTableEntries; i++) {
	    timeField[i]  = new TimeField(runfile, i, header);
	    System.out.println( "Time Field " + i + " " 
				+ timeField[i].tMin + " "
				+ timeField[i].tMax + " " 
				+ timeField[i].tStep + " " 
				+ timeField[i].numOfChannels + " "
				+ timeField[i].tDoubleLength + " "
				+ timeField[i].NumOfChannels() + " " 
				+ timeField[i].timeFocusBit + " "
				+ timeField[i].emissionDelayBit + " " 
				+ timeField[i].constantDelayBit + " "
				+ timeField[i].energyBinBit + " " 
				+ timeField[i].wavelengthBinBit + " "
				+ timeField[i].pulseHeightBit );
	}
	runfile.close();
    }

    protected  TimeField(RandomAccessFile runfile, int iType,
			 Header header ) throws IOException{
	long startingPosition;
        int maskChanWord, minWord, rangeWord, widthWord;

	startingPosition = runfile.getFilePointer();
	runfile.seek(header.timeFieldTable.location + 
		     (iType - 1) * 16);
	iName = new String( header.iName );
	

	if ( header.versionNumber <= 4 ) {
	    maskChanWord = header.readUnsignedInteger( runfile, 4);
	    minWord = header.readUnsignedInteger( runfile, 4);
	    rangeWord = header.readUnsignedInteger( runfile, 4);
	    widthWord = header.readUnsignedInteger( runfile, 4);
	    if ( iName.equalsIgnoreCase( "glad" ) ||
		 iName.equalsIgnoreCase( "lpsd" ) ) {
		this.tMin = (double) minWord*header.lpsdClock;
		this.tMax = (double) rangeWord*header.lpsdClock;
		this.tStep =(double) (widthWord & 0xffff)*header.lpsdClock;
	    }
	    else {
		this.tMin = (double) minWord*header.standardClock;
		this.tMax = (double) rangeWord*header.standardClock;
		this.tStep =(double) (widthWord & 0xffff)*header.standardClock;
	    }
	    this.tDoubleLength =  ((widthWord & 0xffff0000) >>16);
	    this.numOfChannels = (short)(maskChanWord & 0xffff);
	    this.timeFocusBit = (short)((maskChanWord >> 31 ) &1);
	    this.emissionDelayBit = (short)((maskChanWord >> 30 ) &1);
	    this.constantDelayBit = (short)((maskChanWord >> 29 ) &1);
	}
	else {
	    maskChanWord = runfile.readInt();
	    this.tMin = (double)runfile.readFloat();
	    this.tMax = (double)runfile.readFloat();
	    this.tStep = (double)runfile.readFloat();
	    this.numOfChannels = (short)(maskChanWord & 0xffff);
	    this.timeFocusBit = (short)((maskChanWord >> 31 ) &1);
	    this.emissionDelayBit = (short)((maskChanWord >> 30 ) &1);
	    this.constantDelayBit = (short)((maskChanWord >> 29 ) &1);
	    this.energyBinBit = (short)((maskChanWord >> 28 ) &1);
	    this.wavelengthBinBit = (short)((maskChanWord >> 27 ) &1);
	    this.pulseHeightBit = (short)((maskChanWord >> 26 ) &1);
	    
	}

	runfile.seek(startingPosition);
    }

    protected TimeField() {
	tMin= 500.0;
	tMax = 30000.0;
	tStep = 10.0;
	tDoubleLength = 32768;
	emissionDelayBit = 0;
	constantDelayBit = 0;

    }
    protected int NumOfChannels(){
	return (int)((tMax-tMin)/tStep -1);
    }

    protected void Write ( RandomAccessFile runfile ) throws IOException {

	int flagword;
		
	int chwword;
		
	flagword = ( (timeFocusBit << 31) & 0x80000000 ) |
	    ( (emissionDelayBit << 30)    & 0x40000000 ) |
	    ( (constantDelayBit << 29)    & 0x20000000 ) |
	    ( (energyBinBit << 28)        & 0x10000000 ) |
	    ( (wavelengthBinBit << 27)    & 0x08000000 ) |
	    ( (pulseHeightBit << 26)      & 0x04000000 ) |
	    ( numOfChannels               & 0x0000ffff );

	runfile.writeInt( flagword );
	runfile.writeFloat( (float)tMin );
	runfile.writeFloat( (float)tMax );
	runfile.writeFloat( (float)tStep );

    }

    protected boolean isEqual( TimeField FieldToCompare ) {
	boolean answer;

	answer = false;
	if ( this.timeFocusBit == FieldToCompare.timeFocusBit &&
	     this.emissionDelayBit == FieldToCompare.emissionDelayBit &&
	     this.constantDelayBit == FieldToCompare.constantDelayBit &&
	     this.energyBinBit == FieldToCompare.energyBinBit &&
	     this.wavelengthBinBit == FieldToCompare.wavelengthBinBit &&
	     this.pulseHeightBit == FieldToCompare.pulseHeightBit &&
	     this.numOfChannels == FieldToCompare.numOfChannels &&
	     this.tMin == FieldToCompare.tMin &&
	     this.tMax == FieldToCompare.tMax &&
	     this.tStep == FieldToCompare.tStep )
	    answer = true;
	return answer;
    }


}
