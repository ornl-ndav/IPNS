package IPNS.Runfile;

//import java.io.*;
//import java.io.RandomAccessFile;
import java.io.IOException;

/**
   This class is a utility class for the IPNS.Runfile package.  This class 
   sets up and loads entries in the LPSD Detector ID Map Table.  Access is limited to members of this class.
*/

class LpsdDetIdMap {
MapElement[][] map = new MapElement[0][0];

    public static void main(String[] args) throws IOException {
	int i;
	int numTimeTableEntries;

        RandomAccessRunfile runfile = new RandomAccessRunfile(
							args[0], "r");
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
	Header header = new Header(runfile, iName );
	LpsdDetIdMap idMap = new LpsdDetIdMap( runfile, header );
	int[] dets = new int[0];
	for ( int ii = 0; ii < idMap.NumOfBanks(); ii++ ) {
	    dets = idMap.DetsInBank(ii);
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


    protected LpsdDetIdMap( RandomAccessRunfile runfile, Header header ) 
	throws IOException {
	

	long startingPosition = runfile.getFilePointer();
	runfile.seek( header.PSD_IDMap.location );
	int mapIndex = 0;
	int temp;
	int bankNo, numDets;
	int ii;
	MapElement[][] tempMap = new MapElement[0][0];
	while ( mapIndex < header.PSD_IDMap.size/2 ) {
	    bankNo = runfile.readRunShort();
	    numDets = runfile.readRunShort( );
	    for (ii = 0; ii < 5; ii++ ) {
		temp = runfile.readRunShort();
	    }
	    mapIndex += 7;
	    if ( bankNo > map.length - 1 ) {
		tempMap = new MapElement[bankNo + 1][];
		System.arraycopy(map, 0, tempMap, 0, map.length);
		map = tempMap;
	    }
	    map[bankNo] = new MapElement[numDets];
	    for ( ii = 0; ii < numDets; ii++ ) {
		map[bankNo][ii] = new MapElement();
		map[bankNo][ii].detID = runfile.readRunShort(  );
		map[bankNo][ii].crate = runfile.readRunShort( );
		map[bankNo][ii].slot = runfile.readRunShort();
		map[bankNo][ii].input = runfile.readRunShort();
		map[bankNo][ii].minID = runfile.readRunShort();
		temp = runfile.readRunShort();
		temp = runfile.readRunShort();
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
