package IPNS.Calib;

import java.io.*;
import java.lang.*;
/**
This class is manages data from an IPNS DC5 detector calibration file.  This 
version of the detector calibration file is the first version used on the 
VXI based version of the data acquisition system.  This version of the run file
stores data in big endian IEEE floating point format.  It also adds detector
geometry information length, width and depth to the DC2 format.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 5.0beta1
*/
/*
 *
 * $Log$
 * Revision 1.2  2001/08/20 20:58:03  hammonds
 * Changed starting minID from 1 to 0 in setMinID
 *
 * Revision 1.1  2001/07/23 21:19:35  hammonds
 * Added to support newrun type scripts without iCame
 *
 * Revision 5.5  2001/06/18 18:35:15  hammonds
 * Added new detectors for SCD, SAD & SAND
 *
 * Revision 5.4  2001/04/24 20:14:28  hammonds
 * Add comments to take out some forced paramers in the system.
 *
 * Revision 5.3  2001/03/30 17:19:41  hammonds
 * Added elements Data Sources and minID.
 * Fixed number of Y segments for Linear detectors to be 1
 *
 * Revision 5.2  2001/02/09 17:48:29  hammonds
 * Added test instrument.
 *
 * Revision 5.1  2000/02/11 23:04:07  hammonds
 * Have added code to put many parameters about detector location and type into the calibration files.  Standard detector types have been used.  Code to convert old types into new types has been added to the dc2 reader.
 *
 * Revision 5.0  2000/01/07 05:33:19  hammonds
 * Added log messages to a comment section in each file.  Also have set the
 * version number to 5.0 which is the Runfile version number associated with
 * these programs at this time.
 *
 *
 */

public class DC5 {
    float[] angles, height, flightPath, length, width, depth, efficiency;
    float[] rot1, rot2;
    int[] type, coordSys;
    int[] crate, slot, input;
    int nDet;
    String calibFileName;
    String iName;
    int[] psdOrder;
    int[] numSegs1;
    int[] numSegs2;
    int[] dataSource;
    int[] minID;

    public static final float[] 
	LENGTH = {0.0F, 7.62F, 45.72F, 22.86F, 11.43F, 91.44F, 38.1F, 38.1F,
		  12.7F, 3.81F, 12.7F, 0.30F, 0.20F, 0.40F, 0.40F};
    public static final float[] 
	WIDTH = {0.0F, 7.62F, 2.377F, 2.377F, 2.377F, 2.377F, 1.074F, 1.074F, 
		 0.493F, 3.81F, 3.81F, 0.30F, 0.20F, 0.40F, 0.40F };
    public static final float[] 
	DEPTH = {0.0F, 3.81F, 2.377F, 2.377F, 2.377F, 2.377F, 1.074F, 1.074F,
		 0.493F, 2.54F, 2,54F, 2.54F, 2.54F, 2.54F, 2.54F};
    public static final float[] 
	EFFICIENCY = {0.0F, 0.001F, 1.00F, 1.00F, 1.00F, 1.00F, 1.00F, 1.00F,
		 1.00F, 0.001F, 0.001F, 1.00F, 1.00F, 1.00F, 1.00F};
    public static final int[]
	PSD_DIMENSION = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 };
    public static final int[]
	NUM_OF_SEGS_1 = { 0, 1, 1, 1, 1, 8, 1, 32, 1, 1, 1, 85, 64, 128, 128 };
    public static final int[]
	NUM_OF_SEGS_2 = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 85, 64, 128, 128 };
    public static final int[] SEGMENT_SELECT = {
	1, 2, 4, 8, 16, 32, 64, 128, 256 };
    public static final String[] TYPE_DESCRIPTION ={"Not a detector",
						    "3\" pancake monitor",
						    "1\" x 18\"",
		 				    "1\" x 9\"",
						    "1\" x 4.5\"",
						    "1\" x 36\" LPSD",
						    "0.5\" x 15\"",
						    "0.5\" x 15\" LPSD",
						    "0.25\" x 5\"",
						    "1.5\" pancake monitor",
						    "OrdellaBeam Monitor",
						    "SCD Anger Camera",
						    "Ordella 2210 SAD 20cm",
						    "Ordella 2400 SAND 40cm",
						    "Ordella 2410 SAND 40cm"
    };
    public static final String[] DATA_SOURCE_TYPES = {"Detector",
	                                              "Delay 1",
	                                              "Delay 2",
                                                      "External 1",
	                                              "External 2",
	                                              "External 3",
						      "Low" };

public static void main (String[] args) throws IOException{ 
  DC5 cfile = new DC5(args[0]);
  float[] angles = cfile.Angles();
  float[] height = cfile.Height();
  float[] flightPath = cfile.FlightPath();
  float[] efficiency = cfile.Efficiency();
  int[] type = cfile.Type();
  float[] rot1 = cfile.Rot1();
  float[] rot2 = cfile.Rot2();
  int [] crate = cfile.Crate();
  int[] slot = cfile.Slot();
  int[] input = cfile.Input();
  int[] psdOrder = cfile.PsdOrder();
  int[] numSegs1 = cfile.NumSegs1();
  int[] numSegs2 = cfile.NumSegs2();

  System.out.println("Number of detetors: " + cfile.NDet());
  for(int i = 0; i < cfile.NDet(); i++) {
     System.out.println(angles[i] + "  " + height[i] + "  " + flightPath[i] +
               "  " + type[i] + "  " + efficiency[i] );
     }
  }

public DC5() {
  nDet = 1; 
  coordSys = new int[1];
  angles = new float[1];
  height = new float[1];
  flightPath = new float[1];
  rot1 = new float[1];
  rot2 = new float[1];
  type = new int[1];
  length = new float[1];
  width = new float[1];
  depth = new float[1];
  efficiency = new float[1];
  crate = new int[1];
  slot = new int[1];
  input = new int[1];
  psdOrder = new int[1];
  numSegs1 = new int[1];
  numSegs2 = new int[1];
  dataSource = new int[1];
  minID = new int[1];
  calibFileName = "new.dc5";
  }

public DC5(String filename) throws IOException {
  calibFileName = filename;
  boolean dontStop = true;
  try {
    RandomAccessFile calibFile = new RandomAccessFile(filename,"r");

    nDet = calibFile.readInt ();
    System.out.println ( "Opening file with " + nDet + " detectors." );
    coordSys = new int[nDet];
    angles = new float[nDet];
    height = new float[nDet];
    flightPath = new float[nDet];
    rot1 = new float[nDet];
    rot2 = new float[nDet];
    type = new int[nDet];
    length= new float[nDet];
    width = new float[nDet];
    depth = new float[nDet];
    efficiency = new float[nDet];
    crate = new int[nDet];
    slot = new int[nDet];
    input = new int[nDet];
    psdOrder = new int[nDet];
    numSegs1 = new int[nDet];
    numSegs2 = new int[nDet];
    dataSource = new int[nDet];
    minID = new int[nDet];

    for (int i=0; i < nDet; i++) {
 	coordSys[i] = calibFile.readInt ( );
	angles[i] = calibFile.readFloat ( );
	height[i] = calibFile.readFloat ( );
	flightPath[i] = calibFile.readFloat ( );
	rot1[i] = calibFile.readFloat( );
	rot2[i] = calibFile.readFloat( );
	type[i] = calibFile.readInt ( );
	length[i] = calibFile.readFloat ( );
	width[i] = calibFile.readFloat ( );
	depth[i] = calibFile.readFloat ( );
	efficiency[i] = calibFile.readFloat();
	crate[i] = calibFile.readInt();
	slot[i] = calibFile.readInt();
	input[i] = calibFile.readInt();
	psdOrder[i] = calibFile.readInt();
	numSegs1[i] = calibFile.readInt();
	numSegs2[i] = calibFile.readInt();
	dataSource[i] = calibFile.readInt();
	minID[i] = calibFile.readInt();
    }
    setMinID();
    calibFile.close();
  } 
  catch (EOFException e){
  }
}

public DC5( DC2 oldcalib ) {
  String oldfilename = oldcalib.FileName();
  int index =oldfilename.lastIndexOf('.');
  calibFileName = oldfilename.substring(0, index) + ".dc5";

  iName = oldcalib.IName();
  nDet = oldcalib.NDet();
  
  angles = oldcalib.Angles();
  height = oldcalib.Height();
  flightPath = oldcalib.FlightPath();
  type = oldcalib.Type();
  coordSys= new int[nDet];
  rot1 = new float[nDet];
  rot2 = new float[nDet];
  length = new float[nDet];
  width = new float[nDet];
  depth = new float[nDet];
  efficiency = new float[nDet];
  crate = new int[nDet];
  slot = new int[nDet];
  input = new int[nDet];
  psdOrder = new int[nDet];
  numSegs1 = new int[nDet];
  numSegs2 = new int[nDet];
  dataSource = new int[nDet];
  minID = new int[nDet];

  for ( int i=0; i < nDet; i++ ) {
      if ( height[i] == 0.0F && flightPath[i] == 0.0F && type[i] == 0 ) {
      }
      else if ( iName.equals("hrcs") || iName.equals("lrcs") ||
	iName.equals("test") ) {
	  coordSys[i] = 0;
	  switch (type[i]) {  
	  case 0 : {
	      type[i] = 1;
	      length[i] = DC5.LENGTH[1];
	      width[i] = DC5.WIDTH[1];
	      depth[i] = DC5.DEPTH[1];
	      efficiency[i] = DC5.EFFICIENCY[1];
	      psdOrder[i] = DC5.PSD_DIMENSION[1];
	      numSegs1[i] = DC5.NUM_OF_SEGS_1[1];
	      numSegs2[i] = DC5.NUM_OF_SEGS_2[1];
	      dataSource[i] = 0;
 	      break;
	  }
	  case 1 : {
	      length[i] = DC5.LENGTH[2];
	      width[i] = DC5.WIDTH[2];
	      depth[i] = DC5.DEPTH[2];
	      efficiency[i] = DC5.EFFICIENCY[2];
	      type[i] = 2;
	      rot1[i] = (float)(Math.atan( (double)(height[i]/flightPath[i] )) 
				* 180.0 / Math.PI);
	      psdOrder[i] = DC5.PSD_DIMENSION[2];
	      numSegs1[i] = DC5.NUM_OF_SEGS_1[2];
	      numSegs2[i] = DC5.NUM_OF_SEGS_2[2];
	      dataSource[i] = 0;
	      break;
	  }
	  case 2 : {
	      type[i] = 3;
	      length[i] = DC5.LENGTH[3];
	      width[i] = DC5.WIDTH[3];
	      depth[i] = DC5.DEPTH[3];
	      efficiency[i] = DC5.EFFICIENCY[3];
	      rot1[i] = (float)(Math.atan( (double)(height[i]/flightPath[i] )) 
				* 180.0 / Math.PI );
	      psdOrder[i] = DC5.PSD_DIMENSION[3];
	      numSegs1[i] = DC5.NUM_OF_SEGS_1[3];
	      numSegs2[i] = DC5.NUM_OF_SEGS_2[3];
	      dataSource[i] = 0;
	      break;
	  }
	  case 3 : {
	      type[i] = 4;
	      length[i] = DC5.LENGTH[4];
	      width[i] = DC5.WIDTH[4];
	      depth[i] = DC5.DEPTH[4];
	      efficiency[i] = DC5.EFFICIENCY[4];
	      psdOrder[i] = DC5.PSD_DIMENSION[4];
	      numSegs1[i] = DC5.NUM_OF_SEGS_1[4];
	      numSegs2[i] = DC5.NUM_OF_SEGS_2[4];
	      dataSource[i] = 0;
	      break;
	  }
	  }
      }
      if ( iName.equalsIgnoreCase("glad") || iName.equalsIgnoreCase("lpsd") ) {
      }
      else { 
	  crate[i] = i /176 + 1;
	  slot[i] = (i - (crate[i] - 1) * 176 ) / 16 + 1;
	  input[i] = i - (crate[i] - 1) * 176  - (slot[i] - 1) * 16 + 1;
      }
  }
  setMinID();
}

public float[] Angles() {
  return angles;
  }

public float[] Height() {
  return height;
  }

public float[] FlightPath() {
  return flightPath;
  }

public float[] Rot1() {
  return rot1;
  }

public float[] Rot2() {
  return rot2;
  }

public int[] Type() {
  return type;
  }

public float[] Length() {
  return length;
  }

public float[] Width() {
  return width;
  }

public float[] Depth() {
  return depth;
  }

public float[] Efficiency() {
  return efficiency;
  }

public int[] CoordSys() {
  return coordSys;
  }

public int[] Crate() {
    return crate;
}

public int[] Slot() {
    return slot;
}

public int[] Input() {
    return input;
}

public int[] PsdOrder() {
    return psdOrder;
}

public int[] NumSegs1() {
    return numSegs1;
}

public int[] NumSegs2() {
    return numSegs2;
}

public int[] DataSource() {
    return dataSource;
}

public int[] MinID() {
    return minID; 
	}
public int NDet() {
  return nDet;
  }

public String FileName() {
  return calibFileName;
  }

public void setAngles( float[] angles ) {
  this.angles = angles;
  }

public void setHeight( float[] height ) {
  this.height = height;
  }

public void setFlightPath( float[] flightPath ) {
  this.flightPath = flightPath;
  }

public void setRot1( float[] rot1 ) {
  this.rot1 = rot1;
  }

public void setRot2( float[] rot2 ) {
  this.rot2 = rot2;
  }

public void setType( int[] type ) {
  this.type = type;
  }

public void setLength( float[] length ) {
  this.length = length;
  }

public void setWidth( float[] width ) {
  this.width = width;
  }

public void setDepth( float[] depth ) {
  this.depth = depth;
  }

public void setEfficiency( float[] efficiency ) {
  this.efficiency = efficiency;
  }

public void setCoordSys( int[] coordSys ) {
  this.coordSys = coordSys;
  }

public void setCrate( int[] crate ) {
    this.crate = crate;
}

public void setSlot( int[] slot ) {
    this.slot = slot;
}

public void setInput( int[] input ) {
    this.input = input;
}

public void setPsdOrder( int[] psdOrder ) {
    this.psdOrder = psdOrder;
}

public void setNumSegs1( int[] numSegs1 ) {
    this.numSegs1 = numSegs1;
}

public void setNumSegs2( int[] numSegs2 ) {
    this.numSegs2 = numSegs2;
}

public void setDataSource( int[] dataSource ) {
    this.dataSource = dataSource;
}

public void setMinID() {
    int lastRealID = 0;
    minID[0] = 1;
    if ( nDet > 1 ) {
    for (int id = 1; id < nDet; id++ ) {
	if ( type[id] != 0 ) {
	    minID[id] = minID[lastRealID] + 
		numSegs1[lastRealID] * numSegs2[lastRealID];
	   // System.out.println(id + "  " + minID[id]);
	    lastRealID = id;
	}
	else { 
	    minID[id] = -1;
	}
    }
    }
}
public void setNDet(int nDet) {
  this.nDet =  nDet;
  minID = new int[nDet];
  }

public void setFileName(String calibFileName) {
  this.calibFileName = calibFileName;
  }

public void Save(String filename) {
  calibFileName = filename;
  try {
    int index = calibFileName.lastIndexOf('.');
    String extension = calibFileName.substring(index +1).toLowerCase();
    if ( !(extension.equals("dc5")) ) {
       calibFileName += ".dc5";
       }
    RandomAccessFile calibFile = new RandomAccessFile(calibFileName,"rw");

    calibFile.writeInt ( nDet );

    for (int i=0; i < nDet; i++) {
	calibFile.writeInt ( coordSys[i] );
	calibFile.writeFloat ( angles[i] );
	calibFile.writeFloat ( height[i] );
	calibFile.writeFloat ( flightPath[i] );
	calibFile.writeFloat ( rot1[i] );
	calibFile.writeFloat ( rot2[i] );
	calibFile.writeInt ( type[i] );
	calibFile.writeFloat ( length[i] );
	calibFile.writeFloat ( width[i] );
	calibFile.writeFloat ( depth[i] );
	calibFile.writeFloat ( efficiency[i] );
	calibFile.writeInt ( crate[i] );
	calibFile.writeInt ( slot[i] );
	calibFile.writeInt ( input[i] );
	calibFile.writeInt ( psdOrder[i] );
	calibFile.writeInt ( numSegs1[i] );
//	if ( type[i] != 0 ) {
//	    numSegs2[i] = 1;
//	}
	calibFile.writeInt ( numSegs2[i] );
//	if ( type[i] != 0 ) {
//	    dataSource[i] = 6;
//	}
	calibFile.writeInt ( dataSource[i] );
	calibFile.writeInt ( minID[i] );
       }

    calibFile.close();
    }
  catch (IOException e) {
    }
  }

public void InsertDetectors(int start, int num) {
  int oldNDet = nDet;
  nDet += num;
  int[] tcoordSys = new int [nDet];
  float[] tangles = new float[nDet];
  float[] theight = new float[nDet];
  float[] tflightPath = new float[nDet];
  float[] trot1 = new float[nDet];
  float[] trot2 = new float[nDet];
  int[] ttype = new int[nDet];
  float[] tlength = new float[nDet];
  float[] twidth = new float[nDet];
  float[] tdepth = new float[nDet];
  float[] tefficiency = new float[nDet];
  int[] tcrate = new int[nDet];
  int[] tslot = new int[nDet];
  int[] tinput = new int[nDet];
  int[] tpsdOrder = new int[nDet];
  int[] tnumSegs1 = new int[nDet];
  int[] tnumSegs2 = new int[nDet];
  int[] tdataSource = new int[nDet];
  int[] tminID = new int[nDet];

  if ( start > 0) {
     System.arraycopy( coordSys, 0, tcoordSys, 0, start);
     System.arraycopy( angles, 0, tangles, 0, start);
     System.arraycopy( height, 0, theight, 0, start);
     System.arraycopy( flightPath, 0, tflightPath, 0, start);
     System.arraycopy( rot1, 0, trot1, 0, start);
     System.arraycopy( rot2, 0, trot2, 0, start);
     System.arraycopy( type, 0, ttype, 0, start);
     System.arraycopy( length, 0, tlength, 0, start);
     System.arraycopy( width, 0, twidth, 0, start);
     System.arraycopy( depth, 0, tdepth, 0, start);
     System.arraycopy( efficiency, 0, tefficiency, 0, start);
     System.arraycopy( crate, 0, tcrate, 0, start);
     System.arraycopy( slot, 0, tslot, 0, start);
     System.arraycopy( input, 0, tinput, 0, start);
     System.arraycopy( psdOrder, 0, tpsdOrder, 0, start);
     System.arraycopy( numSegs1, 0, tnumSegs1, 0, start);
     System.arraycopy( numSegs2, 0, tnumSegs2, 0, start);
     System.arraycopy( dataSource, 0, tdataSource, 0, start);
     System.arraycopy( minID, 0, tminID, 0, start);
     }

  System.arraycopy( coordSys, start, tcoordSys, start+num, oldNDet-start);
  System.arraycopy( angles, start, tangles, start+num, oldNDet-start);
  System.arraycopy( height, start, theight, start+num, oldNDet-start);
  System.arraycopy( flightPath, start, tflightPath, start+num, oldNDet-start);
  System.arraycopy( rot1, start, trot1, start+num, oldNDet-start);
  System.arraycopy( rot2, start, trot2, start+num, oldNDet-start);
  System.arraycopy( type, start, ttype, start+num, oldNDet-start);
  System.arraycopy( length, start, tlength, start+num, oldNDet-start);
  System.arraycopy( width, start, twidth, start+num, oldNDet-start);
  System.arraycopy( depth, start, tdepth, start+num, oldNDet-start);
  System.arraycopy( efficiency, start, tefficiency, start+num, oldNDet-start);
  System.arraycopy( crate, start, tcrate, start+num, oldNDet-start);
  System.arraycopy( slot, start, tslot, start+num, oldNDet-start);
  System.arraycopy( input, start, tinput, start+num, oldNDet-start);
  System.arraycopy( psdOrder, start, tpsdOrder, start+num, oldNDet-start);
  System.arraycopy( numSegs1, start, tnumSegs1, start+num, oldNDet-start);
  System.arraycopy( numSegs2, start, tnumSegs2, start+num, oldNDet-start);
  System.arraycopy( dataSource, start, tdataSource, start+num, oldNDet-start);
  System.arraycopy( minID, start, tminID, start+num, oldNDet-start);
  
  coordSys = tcoordSys;
  angles = tangles;
  height = theight;
  flightPath = tflightPath;
  rot1 = trot1;
  rot2 = trot2;
  type = ttype;
  length = tlength;
  width = twidth;
  depth = tdepth;
  efficiency = tefficiency;
  crate = tcrate;
  slot = tslot;
  input = tinput;
  psdOrder = tpsdOrder;
  numSegs1 = tnumSegs1;
  numSegs2 = tnumSegs2;
  dataSource = tdataSource;
  minID = tminID;
  }

public void AppendDetectors( int rowsToAdd ) {
  int oldNDet = nDet;
  nDet += rowsToAdd;
  int[] tcoordSys = new int[nDet];
  float[] tangles = new float[nDet];
  float[] theight = new float[nDet];
  float[] tflightPath = new float[nDet];
  float[] trot1 = new float[nDet];
  float[] trot2 = new float[nDet];
  int[] ttype = new int[nDet];
  float[] tlength = new float[nDet];
  float[] twidth = new float[nDet];
  float[] tdepth = new float[nDet];
  float[] tefficiency = new float[nDet];
  int[] tcrate = new int[nDet];
  int[] tslot = new int[nDet];
  int[] tinput = new int[nDet];
  int[] tpsdOrder = new int[nDet];
  int[] tnumSegs1 = new int[nDet];
  int[] tnumSegs2 = new int[nDet];
  int[] tdataSource = new int[nDet];
  int[] tminID = new int[nDet];

  System.arraycopy( coordSys, 0, tcoordSys, 0, oldNDet);
  System.arraycopy( angles, 0, tangles, 0, oldNDet);
  System.arraycopy( height, 0, theight, 0, oldNDet);
  System.arraycopy( flightPath, 0, tflightPath, 0, oldNDet);
  System.arraycopy( rot1, 0, trot1, 0, oldNDet);
  System.arraycopy( rot2, 0, trot2, 0, oldNDet);
  System.arraycopy( type, 0, ttype, 0, oldNDet);
  System.arraycopy( length, 0, tlength, 0, oldNDet);
  System.arraycopy( width, 0, twidth, 0, oldNDet);
  System.arraycopy( depth, 0, tdepth, 0, oldNDet);
  System.arraycopy( efficiency, 0, tefficiency, 0, oldNDet);
  System.arraycopy( crate, 0, tcrate, 0, oldNDet);
  System.arraycopy( slot, 0, tslot, 0, oldNDet);
  System.arraycopy( input, 0, tinput, 0, oldNDet);
  System.arraycopy( psdOrder, 0, tpsdOrder, 0, oldNDet);
  System.arraycopy( numSegs1, 0, tnumSegs1, 0, oldNDet);
  System.arraycopy( numSegs2, 0, tnumSegs2, 0, oldNDet);
  System.arraycopy( dataSource, 0, tdataSource, 0, oldNDet);
  System.arraycopy( minID, 0, tminID, 0, oldNDet);

  coordSys = tcoordSys;
  angles = tangles;
  height = theight;
  flightPath = tflightPath;
  rot1 = trot1;
  rot2 = trot2;
  type = ttype;
  length = tlength;
  width = twidth;
  depth = tdepth;
  efficiency = tefficiency;
  crate = tcrate;
  slot = tslot;
  input = tinput;
  psdOrder = tpsdOrder;
  numSegs1 = tnumSegs1;
  numSegs2 = tnumSegs2;
  dataSource = tdataSource;
  minID = tminID;
  
  }

  
public void DeleteDetectors(int start, int num) {
  int oldNDet = nDet;
  nDet -= num;
  int[] tcoordSys = new int[nDet];
  float[] tangles = new float[nDet];
  float[] theight = new float[nDet];
  float[] tflightPath = new float[nDet];
  float[] trot1 = new float[nDet];
  float[] trot2 = new float[nDet];
  int[] ttype = new int[nDet];
  float[] tlength = new float[nDet];
  float[] twidth = new float[nDet];
  float[] tdepth = new float[nDet];
  float[] tefficiency = new float[nDet];
  int[] tcrate = new int[nDet];
  int[] tslot = new int[nDet];
  int[] tinput = new int[nDet];
  int[] tpsdOrder = new int[nDet];
  int[] tnumSegs1 = new int[nDet];
  int[] tnumSegs2 = new int[nDet];
  int[] tdataSource = new int[nDet];
  int[] tminID = new int[nDet];

  if ( start > 0) {
     System.arraycopy( coordSys, 0, tcoordSys, 0, start);
     System.arraycopy( angles, 0, tangles, 0, start);
     System.arraycopy( height, 0, theight, 0, start);
     System.arraycopy( flightPath, 0, tflightPath, 0, start);
     System.arraycopy( rot1, 0, trot1, 0, start);
     System.arraycopy( rot2, 0, trot2, 0, start);
     System.arraycopy( type, 0, ttype, 0, start);
     System.arraycopy( length, 0, tlength, 0, start);
     System.arraycopy( width, 0, twidth, 0, start);
     System.arraycopy( depth, 0, tdepth, 0, start);
     System.arraycopy( efficiency, 0, tefficiency, 0, start);
     System.arraycopy( crate, 0, tcrate, 0, start);
     System.arraycopy( slot, 0, tslot, 0, start);
     System.arraycopy( input, 0, tinput, 0, start);
     System.arraycopy( psdOrder, 0, tpsdOrder, 0, start);
     System.arraycopy( numSegs1, 0, tnumSegs1, 0, start);
     System.arraycopy( numSegs2, 0, tnumSegs2, 0, start);
     System.arraycopy( dataSource, 0, tdataSource, 0, start);
     System.arraycopy( minID, 0, tminID, 0, start);
     }

  if ( start+num < nDet-1 ) {
  System.arraycopy( coordSys, start+num, tcoordSys, start, oldNDet-start-num);
  System.arraycopy( angles, start+num, tangles, start, oldNDet-start-num);
  System.arraycopy( height, start+num, theight, start, oldNDet-start-num);
  System.arraycopy( flightPath, start+num, tflightPath, start, 
						oldNDet-start-num);
  System.arraycopy( rot1, start+num, trot1, start, oldNDet-start-num);
  System.arraycopy( rot2, start+num, trot2, start, oldNDet-start-num);
  System.arraycopy( type, start+num, ttype, start, oldNDet-start-num);
  System.arraycopy( length, start+num, tlength, start, oldNDet-start-num);
  System.arraycopy( width, start+num, twidth, start, oldNDet-start-num);
  System.arraycopy( depth, start+num, tdepth, start, oldNDet-start-num);
  System.arraycopy( efficiency, start+num, tefficiency, start, 
		    oldNDet-start-num);
  System.arraycopy( crate, start+num, tcrate, start, oldNDet-start-num);
  System.arraycopy( slot, start+num, tslot, start, oldNDet-start-num);
  System.arraycopy( input, start+num, tinput, start, oldNDet-start-num);
  System.arraycopy( psdOrder, start+num, tpsdOrder, start, oldNDet-start-num);
  System.arraycopy( numSegs1, start+num, tnumSegs1, start, oldNDet-start-num);
  System.arraycopy( numSegs2, start+num, tnumSegs2, start, oldNDet-start-num);
  System.arraycopy( dataSource, start+num, tdataSource, start, 
	oldNDet-start-num);
  System.arraycopy( minID, start+num, tminID, start, 
	oldNDet-start-num);
  } 
  coordSys = tcoordSys;
  angles = tangles;
  height = theight;
  flightPath = tflightPath;
  rot1 = trot1;
  rot2 = trot2;
  type = ttype;
  length = tlength;
  width = twidth;
  depth = tdepth;
  efficiency = tefficiency;
  crate = tcrate;
  slot = tslot;
  input = tinput;
  psdOrder = tpsdOrder;
  numSegs1 = tnumSegs1;
  numSegs2 = tnumSegs2;
  dataSource = tdataSource;
  minID = tminID;
  }

}
