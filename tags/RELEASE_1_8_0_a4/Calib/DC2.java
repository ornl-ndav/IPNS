package IPNS.Calib;

import java.io.*;

/**
This class is retrieves data from an IPNS DC2 detector calibration file.  This 
version of the detector calibration file is the version used on the Microvax
based version of the data acquisition system.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 5.0beta1
*/
/*
 *
 * $Log$
 * Revision 1.2  2005/12/22 03:05:53  hammonds
 * Update unused bits of code.
 *
 * Revision 1.1  2001/07/23 21:19:32  hammonds
 * Added to support newrun type scripts without iCame
 *
 * Revision 5.0  2000/01/07 05:33:19  hammonds
 * Added log messages to a comment section in each file.  Also have set the
 * version number to 5.0 which is the Runfile version number associated with
 * these programs at this time.
 *
 *
 */

public class DC2 {
    float[] angles, height, flightPath;
    int[] type;
    int nDet;
    String calibFileName;
    String iName;

    public static void main (String[] args) throws IOException{
	DC2 cfile = new DC2(args[0]);
	float[] angles = cfile.Angles();
	float[] height = cfile.Height();
	float[] flightPath = cfile.FlightPath();
	int[] type = cfile.Type();

	System.out.println("Number of detetors: " + cfile.NDet());
	for(int i = 0; i < cfile.NDet(); i++) {
	    System.out.println(angles[i] + "  " + height[i] + "  " + 
			       flightPath[i] + "  " + type[i]);
	}
    }

    // --------------------------- readUnsignedInteger -------------------

    int ReadInt(RandomAccessFile inFile,
		int length) throws IOException, EOFException {

	byte b[] = new byte[length];
	int c[] = new int[length];
	int nBytesRead = inFile.read(b, 0, length);
	if (nBytesRead != length ) {
		throw new EOFException("DC2 Not enough data for ReadInt");
	}
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


    public DC2(String filename) throws IOException {
	calibFileName = filename;
	RandomAccessFile calibFile = new RandomAccessFile(filename,"r");

	nDet = ReadInt ( calibFile, 4 );
	angles = new float[nDet];
	height = new float[nDet];
	flightPath = new float[nDet];
	type = new int[nDet];
	try {
	    for (int i=0; i < nDet; i++) {
		calibFile.seek((i+1)*16);
		angles[i] = ReadVaxFloat ( calibFile );
		height[i] = ReadVaxFloat ( calibFile );
		flightPath[i] = ReadVaxFloat ( calibFile );
		type[i] = ReadInt ( calibFile, 4 );

	    }
	    calibFile.close();
	} 
	catch (EOFException e){
	}
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

    public int[] Type() {
	return type;
    }

    public int NDet() {
	return nDet;
    }

    public String FileName() {
	return calibFileName;
    }

    public String IName() {
	return iName;
    }

    public void setIName(String iName) {
	this.iName = iName;
    }

}

