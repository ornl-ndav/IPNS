package IPNS.Runfile;

import java.io.*;

/**
This class is a utility class for the IPNS.Runfile package.  This class sets
up and loads entries in the Detector Mapping table for LPSDs stored in IPNS 
Runfiles.  Access to members is limited to members of the package.

*/

class LpsdMap{

int address;
int tfType;
int moreHistBit;


/**
This function provides a test method for this class' functionality.  It will
provide a sampling of the information that is retrieved as a new TimeField
Object is created.  It accepts a filename as the first command line argument.

@param args - The first command line parameter is the runfile name.  This
              parameter should contain the file path unless the file is in
              the current directory.

*/
public static void main(String[] args) throws IOException {
  int i;
  int numEntries;

        RandomAccessFile runfile = new RandomAccessFile(
                args[0], "r");

	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
        Header header = new Header(runfile, iName);
        numEntries = header.lpsdMapTable.size / 4;
        System.out.println("Number Map Table entries in the table: " +
                        numEntries);
	try {
	    LpsdMap[] detectorMap = new LpsdMap[numEntries+1];
	    for (i=1; i <= numEntries; i++) {
		detectorMap[i]  = new LpsdMap(runfile, i, header);
		System.out.println( detectorMap[i].address + " " +
				    detectorMap[i].tfType + " " + 
				    detectorMap[i].moreHistBit);
	    }
	}
	catch ( LpsdNotFound ex ) {
	    System.out.println( ex );
	}
	
        runfile.close();
}

protected  LpsdMap(RandomAccessFile runfile, int id, 
				 Header header ) 
    throws IOException, LpsdNotFound {
	long startingPosition;
	int temp;

	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound( "LpsdMap: LpsdNotFound" );
	}

	startingPosition = runfile.getFilePointer();
	runfile.seek( header.lpsdMapTable.location + (id - 1) * 4);

	if ( header.versionNumber <= 4 ) {
	    temp = header.readUnsignedInteger(runfile, 4);
	}
	else {
	    temp = runfile.readInt();
	}

	address = temp & 0xFFFF;
	address = address << 8;
	tfType = (temp >> 24) & 0xFF;
        moreHistBit = (temp >> 23) & 0x1;
	runfile.seek(startingPosition);
	}

    protected LpsdMap() {
    }

    protected void Write ( RandomAccessFile runfile ) throws IOException {
	int tfEntry = ( address & 0x7FFFFF ) | ( (tfType << 24) & 0xFF000000 )
	    | ( (moreHistBit << 23)  &  0x800000 );
	runfile.writeInt( tfEntry );
	
    }

    protected boolean isEqual( LpsdMap mapToCompare ) {
	boolean answer;

	answer = false;
	if ( this.address == mapToCompare.address &&
	     this.tfType == mapToCompare.tfType &&
	     this.moreHistBit == mapToCompare.moreHistBit )
	    answer = true;
	return answer;
    }
}
 
