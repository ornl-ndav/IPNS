package IPNS.Calib;

import java.io.*;

/**
This class is retrieves data from an IPNS DIS detector discriminator file.  This 
version of the detector discriminator file is the version used on the Microvax
based version of the data acquisition system.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 5.0beta1
*/
/*
 *
 * $ Log: DIS.java,v $
 *
 */

public class DIS {
int[] lld, uld;
int nDet;
String discFileName;

public static void main (String[] args) throws IOException{
  DIS cfile = new DIS(args[0]);
  int[] lld = cfile.Lld();
  int[] uld = cfile.Uld();

  System.out.println("Number of detetors: " + cfile.NDet());
  for(int i = 0; i < cfile.NDet(); i++) {
     System.out.println(lld[i] + "  " + uld[i] );
     }
  }

// --------------------------- readUnsignedInteger -------------------

  int ReadInt(RandomAccessFile inFile,
   int length) throws IOException, EOFException {

    byte b[] = new byte[length];
    int c[] = new int[length];
    int nBytesRead = inFile.read(b, 0, length);
    int num = 0;
    for (int i = 0; i < length; ++i) {
       if(b[i] < 0) {
        c[i] = b[i] + 256;
      }
      else {
        c[i] = b[i];
      }
      num += c[i] * (int)Math.pow(256.0, (double)i);
    }
    return num;
  }

// ---------------------------- ReadVAXReal4 ------------------------

   float ReadVaxFloat(RandomAccessFile inFile)
   throws IOException, EOFException {

        int length = 4;
        long hi_mant, low_mant, exp, sign;
        float f_val;
        long val = (long )ReadInt(inFile, length);
        if (val < 0) {
           val = val + (long)Math.pow(2.0, (double)32);
           }
/* add 128 to put in the implied 1 */
        hi_mant  = (val & 127) + 128;
        val      = val >> 7;
/* exponent is "excess 128" */
        exp      = ((int)(val & 255)) - 128;
        val      = val >> 8;

        sign     = val & 1;
        low_mant = val >> 1;
/* This could also be a "reserved" operand of some sort?*/
        if ( exp == -128 )
          f_val = 0;
        else
          f_val = (float)((hi_mant / 256.0 + low_mant / 16777216.0) *
            Math.pow(2.0, (double)exp ));

        if ( sign == 1 )
          f_val = -f_val;
        return f_val;
  }


public DIS(String filename) throws IOException {
  discFileName = filename;
  boolean dontStop = true;
    RandomAccessFile discFile = new RandomAccessFile(filename,"r");

    //
    nDet = (int)discFile.length()/8;
    //    nDet = ReadInt ( discFile, 4 );
    lld = new int[nDet];
    uld = new int[nDet];
  try {
    for (int i=0; i < nDet; i++) {
       discFile.seek((i)*4);
       lld[i] = ReadInt ( discFile ,2);
       uld[i] = ReadInt ( discFile ,2);

       }
       discFile.close();
      } 
     catch (EOFException e){
       }
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

}

