/*
 * File: S2DS.java 
 *
 * Copyright (C) 2003, John Hammonds
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
 *  Revision 1.2  2004/03/28 22:28:53  hammonds
 *  Changes for operators moved to gov from ISAW
 *
 *  Revision 1.1  2003/09/06 04:24:05  hammonds
 *  First in.  Diagnostic for SAND.
 *
 *
 */
package IPNS.Operators.Print;

import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import DataSetTools.operator.Parameter;
import DataSetTools.operator.Generic.Batch.GenericBatch;
import DataSetTools.parameter.IntegerPG;
import DataSetTools.parameter.LoadFilePG;
import gov.anl.ipns.Util.SpecialStrings.LoadFileString;
import IPNS.Runfile.Runfile;


/**
    This Operator modifies the Runfile header fields which control Run 
    scheduling 
*/

public class S2DS extends GenericBatch implements Serializable{

  /**
     Constructor
  */
  public S2DS(String inName) {
    super("S2DS");
    getParameter(0).setValue(inName);
  }
  /**
     Dummy Constructor
  */
  public S2DS() {
    super("S2DS");
    setDefaultParameters();
  }

  /** 
   * Get the name of this operator, used in scripts
   * @return "Schedule", the command used to invoke this operator
   * in Scripts
   */
  public String getCommand()
  {
    return "S2DS";
  }
  /** 
   * Sets default values for the parameters. The parameters set must
   * match the data types of the parameters used in the constructor.
   */
  public void setDefaultParameters()
  {
    parameters = new Vector();
    LoadFilePG inFile=new LoadFilePG("Input File", new LoadFileString(""));
    addParameter( inFile );
  }


  /**
     Execute the functional part of this code.  Set the scheduling parameters
     in the header of each file specified
  */
  public Object getResult() {
    StringBuffer strBuff = new StringBuffer();
    String inName = (String)(getParameter(0).getValue());
    Runfile runFile = new Runfile();
    try {
      runFile = new Runfile(inName);
    }
    catch (IOException ex) {
      strBuff.append("ERROR Opening file " + inName);
      return strBuff;
    }
    String userName = runFile.UserName();
    String runTitle = runFile.RunTitle();
    int runNum = runFile.RunNumber();
    String iName = runFile.iName();

    strBuff.append( "\n" + iName + " RUN: " + runNum + ":  " + userName + "\n" );
    strBuff.append( runTitle + "\n");

    float mon1tot, mon2tot, mon3tot, pottot;
    try {
      runFile.LeaveOpen();
      mon1tot = runFile.Get1DSum(1);
      mon2tot = runFile.Get1DSum(2);
      mon3tot = runFile.Get1DSum(3);
      pottot = runFile.Get1DSum(4);
      runFile.Close();
    }
    catch (IOException ex) {
      strBuff.append("ERROR reading sums for " + inName);
      return strBuff;
    }
    strBuff.append( "\nBEAM MONITOR TOTAL COUNTS = \n");
    strBuff.append("        " + mon1tot + "    " + mon2tot + "    " +
		       mon3tot + "    " + pottot + "\n");
    strBuff.append("M/POT:  " + mon1tot/pottot + "    " + mon2tot/pottot +
		   "    " + mon3tot/pottot + "    " + pottot/pottot + "\n");
    int numPulses = runFile.NumOfPulses();
    strBuff.append("\nTOTAL T0 PULSES=     " + numPulses + "\n");
    return strBuff.toString();

  }

  /**
     return a string with operator documentation
  */
  public String getDocumentation() {
    StringBuffer s = new StringBuffer("");
    
    return s.toString();
  }

  public static void main( String args[] ) {
    S2DS op = new S2DS(args[0]);
    String out = (String)op.getResult();
    System.out.println(out);
    System.exit(0);
  }

}

