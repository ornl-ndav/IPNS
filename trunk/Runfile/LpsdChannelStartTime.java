package IPNS.Runfile;


import java.io.*;

/**
   This class is a utility class for the IPNS.Runfile package.  This class sets
   up and loads entries in the channel starting time table for LPSDs stored in 
   IPNS Runfiles.  Access to members is limited to members of the package.

*/

class LpsdChannelStartTime {

    int [][] table;


    /** 
	The main routine provides a test method.
    */

    public static void main ( String[] args ) throws IOException {
	
	RandomAccessFile runfile = new RandomAccessFile(
							args[0], "r");
	
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
        Header header = new Header(runfile, iName);

	try {
	    LpsdChannelStartTime cst = new LpsdChannelStartTime( runfile, 
								 header );
	}
	catch ( LpsdNotFound ex ) {
	    System.out.println( ex );
	}
	runfile.close();
    }


    /** 
	Constructor Class
    */

    protected LpsdChannelStartTime( RandomAccessFile runfile, Header header )
	throws IOException, LpsdNotFound {
	
	long startingPosition;

	startingPosition = runfile.getFilePointer();

	if ( header.numOfLpsds <= 0 ) {
	    throw new LpsdNotFound( "LpsdChannelStartTime: LpsdNotFound" );
	}
	
	if ( header.versionNumber <= 4 ) {
	    runfile.seek( header.lpsdStartTable.location );
	    table = new int[header.numOfTimeFields][];
	    for ( int ii = 0; ii < header.numOfTimeFields; ii++ ) {
		LpsdTimeField tf = new LpsdTimeField(runfile, ii + 1, header);
		table[ii] = new int[tf.numOfChannels - 1 ];
		for ( int jj = 0; jj < (tf.numOfChannels -1); jj++ ) {
		    table[ii][jj] = (int)(header.readUnsignedInteger( runfile,
								      2 ) *
			header.lpsdClock);
		}
		int junk = (int)(header.readUnsignedInteger( runfile, 2 ));
	    }
	}

	runfile.seek( startingPosition );
    }
}
