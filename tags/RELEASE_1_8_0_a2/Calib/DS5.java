package IPNS.Calib;

import java.io.*;

/**
This class is manages data from an IPNS DS5 detector discration file.  This 
version of the detector discration file is the first version used on the 
VXI based version of the data acquisition system.  This version of the run file
stores data in big endian IEEE floating point format.  It also adds detector
geometry information length, width and depth to the DIS format.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 5.0beta1
*/
/*
 *
 * $ Log: DS5.java,v $
 *
 */

public class DS5 {
int[] lld, uld;
int nDet;
String discFileName;

public static void main (String[] args) throws IOException{
  DS5 cfile = new DS5(args[0]);
  int[] lld = cfile.Lld();
  int[] uld = cfile.Uld();

  System.out.println("Number of detetors: " + cfile.NDet());
  for(int i = 0; i < cfile.NDet(); i++) {
     System.out.println(lld[i] + "  " + uld[i] );
     }
  }

public DS5() {
  nDet = 1;
  lld = new int[1];
  uld = new int[1];
  discFileName = "new.ds5";
  }

public DS5(String filename) throws IOException {
  discFileName = filename;
  try {
    RandomAccessFile discFile = new RandomAccessFile(filename,"r");

    nDet = discFile.readInt ();
    lld = new int[nDet];
    uld = new int[nDet];
    for (int i=0; i < nDet; i++) {
       lld[i] = discFile.readInt ( );
       uld[i] = discFile.readInt ( );

       }
       discFile.close();
    } 
  catch (EOFException e){
    }
  }

public DS5( DIS olddisc ) {
  String oldfilename = olddisc.FileName();
  int index =oldfilename.lastIndexOf('.');
  discFileName = oldfilename.substring(0, index) + ".ds5";
   nDet = olddisc.NDet();
  lld = olddisc.Lld();
  uld = olddisc.Uld();
  }


public int[] Lld() {
  return lld;
  }

public int[] Uld() {
  return uld;
  }

public int NDet() {
  return nDet;
  }

public String FileName() {
  return discFileName;
  }

public void setLld( int[] lld ) {
  this.lld = lld;
  }

public void setUld( int[] uld ) {
  this.uld = uld;
  }

  public void setDetDisc( int id, int lld, int uld) {
    this.lld[id-1] = lld;
    this.uld[id-1] = uld;
  }

public void setNDet(int nDet) {
  this.nDet =  nDet;
  }

public void setFileName(String discFileName) {
  this.discFileName = discFileName;
  }

public void Save(String filename) {
  discFileName = filename;
  try {
    int index = discFileName.lastIndexOf('.');
    String extension = discFileName.substring(index +1).toLowerCase();
    if ( !(extension.equals("ds5")) ) {
       discFileName += ".ds5";
       }
    RandomAccessFile discFile = new RandomAccessFile(discFileName,"rw");

    discFile.writeInt ( nDet );

    for (int i=0; i < nDet; i++) {
       discFile.writeInt ( lld[i] );
       discFile.writeInt ( uld[i] );
       }

    discFile.close();
    }
  catch (IOException e) {
    }
  }

public void InsertDetectors(int start, int num) {
  int oldNDet = nDet;
  nDet += num;
  int[] tlld = new int[nDet];
  int[] tuld = new int[nDet];
  if ( start > 0) {
     System.arraycopy( lld, 0, tlld, 0, start);
     System.arraycopy( uld, 0, tuld, 0, start);
     }

  System.arraycopy( lld, start, tlld, start+num, oldNDet-start);
  System.arraycopy( uld, start, tuld, start+num, oldNDet-start);
  
  lld = tlld;
  uld = tuld;
  }

public void AppendDetectors( int rowsToAdd ) {
  int oldNDet = nDet;
  nDet += rowsToAdd;
  int[] tlld = new int[nDet];
  int[] tuld = new int[nDet];
  System.arraycopy( lld, 0, tlld, 0, oldNDet);
  System.arraycopy( uld, 0, tuld, 0, oldNDet);

  lld = tlld;
  uld = tuld;
  }

  
public void DeleteDetectors(int start, int num) {
  int oldNDet = nDet;
  nDet -= num;
  int[] tlld = new int[nDet];
  int[] tuld = new int[nDet];
  if ( start > 0) {
     System.arraycopy( lld, 0, tlld, 0, start);
     System.arraycopy( uld, 0, tuld, 0, start);
     }

  if ( start+num < nDet-1 ) {
  System.arraycopy( lld, start+num, tlld, start, oldNDet-start-num);
  System.arraycopy( uld, start+num, tuld, start, oldNDet-start-num);
  } 
  lld = tlld;
  uld = tuld;
  }

}

