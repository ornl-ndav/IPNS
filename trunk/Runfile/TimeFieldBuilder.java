
package IPNS.Runfile;

import java.io.*;

/**
This class is inteded to construct the time fields to be written to a runfile.
*/
/*
 *
 *  $Log$
 *  Revision 5.2  2002/09/18 18:05:05  hammonds
 *  check mail log
 *
 *  Revision 5.1  2002/08/21 17:35:54  hammonds
 *  Added logBinBit to be used for dt/t binning
 *
 *  Revision 5.0  2000/01/07 22:45:30  hammonds
 *  Changed Version Number
 *
 *  Revision 1.1  2000/01/07 22:16:56  hammonds
 *  Added this file since it was missing.
 *
 *
 */

public class TimeFieldBuilder extends TimeField {
public static final int NORMAL_BIN = 0;
public static final int TIME_FOCUS = 2;
public static final int ENERGY_BIN = 4;
public static final int WAVELENGTH_BIN = 8;
public static final int LOG_BIN = 16;
 
    public TimeFieldBuilder() {
        tMin= 500.0;
	tMax = 30000.0;
	tStep = 10.0;
	tDoubleLength = 32768;
	emissionDelayBit = 0;
	constantDelayBit = 0;
	setBinMode(NORMAL_BIN);
    }

    public double TMin() {
	return tMin;
    }

    public double TMax() {
	return tMax;
    }

    public double TStep() {
	return tStep;
    }

    public int TDoubleLength() {
	return tDoubleLength;
    }

    /*    public short NumOfChannels() {
	return numOfChannels;
    }
    */

    public short EmissionDelayBit() {
	return emissionDelayBit;
    }

    public short ConstantDelayBit() {
	return constantDelayBit;
    }

    public int BinMode() {
	return 2 * timeFocusBit + 4 * energyBinBit + 8 * wavelengthBinBit * 
	    16 * logBinBit;
    }

    public void setTMin(double tMin) {
	this.tMin = tMin;
    }

    public void setTMax(double tMax) {
	this.tMax = tMax;
    }

    public void setTStep(double tStep) {
	this.tStep = tStep;
    }

    public void setTDoubleLength(int tDoubleLength) {
	this.tDoubleLength = tDoubleLength;
    }

    public void setEmissionDelayBit(short emissionDelayBit) {
	this.emissionDelayBit = emissionDelayBit;
    }

    public void setConstantDelayBit(short constantDelayBit) {
	this.constantDelayBit = constantDelayBit;
    }

    public void setBinMode(int mode) {
	switch (mode) {
	case (NORMAL_BIN): {
	    timeFocusBit = 0;
	    energyBinBit = 0;
	    wavelengthBinBit = 0;
	    logBinBit = 0;
	    break;
	}
	case (TIME_FOCUS): {
	    timeFocusBit = 1;
	    energyBinBit = 0;
	    wavelengthBinBit = 0;
	    logBinBit = 0;
	    break;
	}
	case (ENERGY_BIN): {
	    timeFocusBit = 0;
	    energyBinBit = 1;
	    wavelengthBinBit = 0;
	    logBinBit = 0;
	    break;
	}
	case (WAVELENGTH_BIN): {
	    timeFocusBit = 0;
	    energyBinBit = 0;
	    wavelengthBinBit = 0;
	    logBinBit = 0;
	    break;
	}
	case (LOG_BIN): {
	    timeFocusBit = 0;
	    energyBinBit = 0;
	    wavelengthBinBit = 0;
	    logBinBit = 1;
	    break;
	}
	    
	}
    }

 }
