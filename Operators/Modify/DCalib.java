/*
 * File: DCalib.java 
 *
 * Copyright (C) 2005, John Hammonds
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : John Hammonds jphammonds@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 S. Cass Avenue, Bldg 360
 *           Argonne, IL 60440
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2006/07/10 16:26:04  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.1  2005/04/21 02:10:54  hammonds
 *  New Class to update angles from DCalib file.
 *
 *
 *
 *
 */
package IPNS.Operators.Modify;

import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.IntListAttribute;
import DataSetTools.operator.Parameter;
import DataSetTools.operator.Generic.GenericOperator;
import IPNS.Calib.DC5;
import java.io.RandomAccessFile;

import gov.anl.ipns.Parameters.LoadFilePG;
import gov.anl.ipns.Util.SpecialStrings.*;
/** 
*/
public class DCalib extends GenericOperator implements Serializable {
  private static final String TITLE=new String("Modify DCalib in runfile");
  /** Constructor */
  public DCalib( String runfile_in, String dcalib_in) {
    super(TITLE);
    getParameter(0).setValue(runfile_in);
    getParameter(1).setValue(dcalib_in);
  }
 
  public DCalib() {
    super (TITLE);
    setDefaultParameters();
  }

  /** Provide the string to be used in scripts to invoke this operator */
  public String getCommand() {
    return ("ModDCalib");
  }

  /** Provide a default set of parameter GUIs to be used for this Operator */
  public void setDefaultParameters(){
    parameters = new Vector();
    LoadFilePG runfile_inPG = new LoadFilePG("Runfile to modify",new String());
    addParameter( runfile_inPG );
    LoadFilePG dcalib_inPG = new LoadFilePG("New DCalib File", new String());
    addParameter( dcalib_inPG );
  }

  /** Perform functional part of this code */
  public Object getResult() {
    String runfileName = (String)getParameter(0).getValue();
    String dcalibName = (String)getParameter(1).getValue();
    String ErrorStatus = "OK";
    DC5 dcalib = new DC5();
    int numDC5Dets = 0;
    int numRunDets = 0;
    try{
      dcalib = new DC5(dcalibName);
      numDC5Dets = dcalib.NDet();
      System.out.println(dcalibName + " has " + numDC5Dets + " detectors\n");
    }
    catch (IOException ex) {
      return new ErrorString("Trouble loading Calibration");
    }
    try {
      RandomAccessFile runFile = 
	new RandomAccessFile( runfileName, "rw");
      //seek location and size of angle table
      runFile.seek(72);
      int angLoc = runFile.readInt(); 
      System.out.println("Location of Angle Table "+ angLoc);
      int angSize = runFile.readInt();
      numRunDets = angSize/4;
      System.out.println( runfileName +" has " + numRunDets + " detectors\n");
      if ( numDC5Dets != numRunDets ) {
	return new ErrorString("Num of dets in run and dcalib must match");
      }
      runFile.seek(angLoc);
      float[] newAngle = dcalib.Angles();
      for (int ii=0; ii<numRunDets; ii++){
	System.out.println( ii + ":" + newAngle[ii]);
	runFile.writeFloat(newAngle[ii]);
      }

     runFile.close();
     return "OK";
    }
    catch (IOException ex) {
      return new ErrorString("Trouble opening runfile");
    }


    //    return ErrorStatus;
    
  }

  public String[] getCategoryList() {
    String[] cat = {"Operator","DAS", "Modify" };
    return cat;
    
    
  }

}
