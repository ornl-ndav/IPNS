package IPNS.Runfile;

import java.io.*;

/**
This class is a utility class for the IPNS.Runfile package.  This class sets
up and loads entries in the Detector Mapping table stored in IPNS Runfiles.
Access to members is limited to members of the package.

*/

class DetectorMap{

    int address;
    int tfType;
    int moreHistBit;

    String iName;
    int versionNumber;
    /**
       This function provides a test method for this class' functionality.  It
       will provide a sampling of the information that is retrieved as a new 
       TimeField Object is created.  It accepts a filename as the first command
       line argument.

       @param args - The first command line parameter is the runfile name.  
              This parameter should contain the file path unless the file is in
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
	Header header = new Header(runfile, iName );
        numEntries = header.detectorMapTable.size / DetectorMap.mapSize(header.versionNumber);
        System.out.println("Number of Time Field entries in the table: " +
			   numEntries);
        DetectorMap[] detectorMap = new DetectorMap[numEntries+1];
        for (i=1; i <= numEntries; i++) {
	    detectorMap[i]  = new DetectorMap(runfile, i, header);
	    System.out.println( detectorMap[i].address + " " +
				detectorMap[i].tfType + " " + 
				detectorMap[i].moreHistBit);
	}
        runfile.close();
    }

    protected  DetectorMap(RandomAccessFile runfile, int id, 
			   Header header ) throws IOException{
	long startingPosition;
	int temp = 0;
	iName = header.iName;
	versionNumber= header.versionNumber;

	startingPosition = runfile.getFilePointer();
	runfile.seek( header.detectorMapTable.location + (id - 1) 
		      * DetectorMap.mapSize(versionNumber));
	
	if ( header.versionNumber <= 4 ) {
	    temp = header.readUnsignedInteger(runfile, 4);
	}
	else if ( header.versionNumber < 6 ){
	    temp = runfile.readInt();
	}
	else {
	    address = runfile.readInt();
	    tfType = runfile.readShort();
	    moreHistBit = runfile.readShort();
	}
	
	if ( header.versionNumber < 6 ) {
	    iName = new String( header.iName );
	    versionNumber = header.versionNumber;
	    if ( (!iName.equalsIgnoreCase("glad") &&
		  !iName.equalsIgnoreCase("lpsd")) || versionNumber >=5 ) {
		
		address = temp & 0x7FFFFF;
		tfType = (temp >> 24) & 0xFF;
		moreHistBit = (temp >> 23) & 0x1;
	    }
	    else {
		
		address = temp & 0xFFFF;
		address = address << 8;
		tfType = (temp >> 24) & 0xFF;
		moreHistBit = (temp >> 23) & 0x1;
	    }	    
	}
	runfile.seek(startingPosition);
    }

    protected DetectorMap() {
    }

    protected DetectorMap(String iName) {
	this.iName = new String( iName );
    }

    protected DetectorMap(String iName, int versionNumber) {
	this.iName = new String( iName );
	this.versionNumber = versionNumber;
    }

    protected void Write ( RandomAccessFile runfile ) throws IOException {
	int tfEntry;
	if ( versionNumber < 6) {
	    if ( (!iName.equalsIgnoreCase("glad") &&
		  !iName.equalsIgnoreCase("lpsd")) 
		 || versionNumber >= 5 ) {
		
		tfEntry = ( address & 0x7FFFFF ) 
		    | ( (tfType << 24) & 0xFF000000 )
		    | ( (moreHistBit << 23)  &  0x800000 );
	    }
	    else {
		tfEntry = ( address & 0x7FFFFF ) 
		    | ( (tfType << 24) & 0xFF000000 )
		    | ( (moreHistBit << 23)  &  0x800000 );
	    }
	    runfile.writeInt( tfEntry );
	}
	else {
	    runfile.writeInt( address );
	    runfile.writeShort( tfType );
	    runfile.writeShort( moreHistBit );
	}
    }

    protected boolean isEqual( DetectorMap mapToCompare ) {
	boolean answer;

	answer = false;
	if ( this.address == mapToCompare.address &&
	     this.tfType == mapToCompare.tfType &&
	     this.moreHistBit == mapToCompare.moreHistBit )
	    answer = true;
	return answer;
    }

    protected static int mapSize( int versionNumber ) {
	if (versionNumber <= 5 ) {
	    return (4);
	}
	else {
	    return (8);
	}
    }
}
 
