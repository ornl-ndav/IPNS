package IPNS.Runfile;

import java.io.*;

/**
   This class is a utility class for the IPNS.Runfile package.  This class 
   sets up and loads entries in the LPSD Detector ID Map Table.  Access is limited to members of this class.
*/

class LpsdDetIdMap {
MapElement[][] map = new MapElement[0][0];

    public static void main(String[] args) throws IOException {
	int i;
	int numTimeTableEntries;

        RandomAccessFile runfile = new RandomAccessFile(
							args[0], "r");
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
	Header header = new Header(runfile, iName );
	LpsdDetIdMap idMap = new LpsdDetIdMap( runfile, header );
	for ( int ii = 0; ii < idMap.NumOfBanks(); ii++ ) {
	    int[] dets = idMap.DetsInBank(ii);
	    System.out.println( "Bank " + ii + " has " );
	    for ( int jj = 0; jj < dets.length; jj++ ) {
		int crate = idMap.CrateForDet( ii, dets[jj] );
		int slot = idMap.SlotForDet( ii, dets[jj] );
		int input = idMap.InputForDet( ii, dets[jj] );
		int minid = idMap.MinIdForDet( ii, dets[jj] );
		System.out.println( dets[jj] + ":" + crate + ":" + slot + 
				    ":" + input + ":" + minid );
	    }
	}

    }


    protected LpsdDetIdMap( RandomAccessFile runfile, Header header ) 
	throws IOException {
	

	long startingPosition = runfile.getFilePointer();
	runfile.seek( header.PSD_IDMap.location );
	int mapIndex = 0;
	while ( mapIndex < header.PSD_IDMap.size/2 ) {
	    int bankNo = header.readUnsignedInteger( runfile, 2 );
	    int numDets = header.readUnsignedInteger( runfile, 2 );
	    for (int ii = 0; ii < 5; ii++ ) {
		int temp = header.readUnsignedInteger( runfile, 2);
	    }
	    mapIndex += 7;
	    if ( bankNo > map.length - 1 ) {
		MapElement[][] tempMap = new MapElement[bankNo + 1][];
		System.arraycopy(map, 0, tempMap, 0, map.length);
		map = tempMap;
	    }
	    map[bankNo] = new MapElement[numDets];
	    for ( int ii = 0; ii < numDets; ii++ ) {
		map[bankNo][ii] = new MapElement();
		map[bankNo][ii].detID = header.readUnsignedInteger( runfile,
								    2 );
		map[bankNo][ii].crate = header.readUnsignedInteger( runfile,
								    2 );
		map[bankNo][ii].slot = header.readUnsignedInteger( runfile,
								    2 );
		map[bankNo][ii].input = header.readUnsignedInteger( runfile,
								    2 );
		map[bankNo][ii].minID = header.readUnsignedInteger( runfile,
								    2 );
		int temp = header.readUnsignedInteger( runfile, 2);
		temp = header.readUnsignedInteger( runfile, 2);
		mapIndex += 7;
	    }
	}

      	runfile.seek(startingPosition);
    }


    public int NumOfBanks() {
	return map.length;
    }

    public int[] DetsInBank( int bank ) {
	int nDets = map[bank].length;
	int[] dets = new int[nDets];
	for (  int i = 0; i < nDets; i++ ) {
	    dets[i] = map[bank][i].detID;
	}
	return dets;
    }

    public int CrateForDet( int bank, int detId ) {
	return map[bank][detId - 1].crate;
    }

    public int SlotForDet( int bank, int detId ) {
	return map[bank][detId - 1].slot;
    }

    public int InputForDet( int bank, int detId ) {
	return map[bank][detId -1 ].input;
    }

    public int MinIdForDet( int bank, int detId ) {
	return map[bank][detId - 1 ].minID;
    }

    class MapElement {
	int detID;
	int crate;
	int slot;
	int input;
	int minID;
    }
}
