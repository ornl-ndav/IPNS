/*
 * File: MakeCtlFile.java 
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
 * Contact : John Hammonds <JPHammonds@anl.gov>
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
 *  Revision 1.1  2005/06/08 11:45:19  hammonds
 *  Files added to automatically generate Ctl Files.
 *
 *  Revision 1.1  2005/04/21 02:13:10  hammonds
 *  New Class to modify elements of the runfile header.
 *
 *
 *
 */
package IPNS.Operators.Control;

import  java.io.*;
import  java.io.Serializable;
import  java.util.Vector;
import  DataSetTools.operator.Parameter;
import  DataSetTools.operator.Generic.GenericOperator;
import  DataSetTools.parameter.*;
import gov.anl.ipns.Util.SpecialStrings.*;
import IPNS.Operators.Instdir;
import IPNS.Control.*;
import java.lang.reflect.Array;
import gov.anl.ipns.Util.Sys.*;
/**
 * This operator instantiates.
 * 
 */

public class MakeCtlFile extends  GenericOperator 
  implements Serializable
{
  
  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default MakeCtlFile operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public MakeCtlFile( String filename, int numRuns, int numDevices,  Object val, String userName, String runTitle, int numCycles, int numPulses)
  {
    
    super( "MakeCtlFile" );
    //    System.out.println("Inside the constructor now");  
    getParameter(0).setValue(filename);
    getParameter(1).setValue(new Integer(numRuns));
    getParameter(2).setValue(new Integer(numDevices));
    getParameter(3).setValue(val);
    getParameter(4).setValue(userName);
    getParameter(5).setValue(runTitle);
    getParameter(6).setValue(new Integer(numCycles));
    getParameter(7).setValue(new Integer(numPulses));
  }
  
  public MakeCtlFile()
  { super( "MakeCtlFile" );
    //System.out.println("def constructor");
    setDefaultParameters();
  }
  
  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
  public String getCommand()
  {
    return "MakeCtlFile";
  }
  
  /* -------------------------- setDefaultParmeters ------------------------- */
  /**
   *  Set the default parameters.
   */
  public void setDefaultParameters()
  {
    parameters = new Vector();  // must do this to create empty list of 
    // parameters
    addParameter( new SaveFilePG("Ctl filename to save", new String() ) );
    addParameter(new IntegerPG("Number of runs", 1)  );
    addParameter( new IntegerPG("Number of devices/run", 1) );
    addParameter( new StringArrayPG("Device File Names", "[]" ) );
    addParameter( new StringPG("User Name", "user name") );
    addParameter( new StringPG("Run Title", "your title") );
    addParameter( new IntegerPG("Number of Cycles", 720) );
    addParameter( new IntegerPG("Number of Pulses", 108000) );
    
  }
  

  /* ---------------------------- getResult ------------------------------- */
  
  public Object getResult()
  {
    int rval = 0;
    String filename = (String) (getParameter(0).getValue());
    int numRuns = ((Integer)(getParameter(1).getValue())).intValue();
    int numDevices = ((Integer)(getParameter(2).getValue())).intValue();
    Vector pFiles = (Vector)getParameter(3).getValue(); 
    String userName = (String)(getParameter(4).getValue());
    String runTitle = (String)(getParameter(5).getValue());
    int numCycles = ((Integer)(getParameter(6).getValue())).intValue();
    int numPulses = ((Integer)(getParameter(7).getValue())).intValue();

    String[] pFileNames = new String[pFiles.size()];
    for (int ii = 0; ii < pFiles.size(); ii++) {
      pFileNames[ii] = (String)pFiles.elementAt(ii);
    }
    String instDir = (String)((new Instdir(System.getProperty("Default_Instrument"))).getResult());
    String ancDir = new String (instDir + "/anc");
    try {
      FileOutputStream outfile = new FileOutputStream( filename );
      PrintWriter outWriter = new PrintWriter(outfile, true);
      outWriter.print( numRuns );
      outWriter.println( "   RUNS");
      
      for (int ii=1; ii<=numRuns; ii++) {
	CtlSeq myCtls = new CtlSeq(ii, userName, runTitle, pFileNames, 
				   new String("transmission"), numCycles,
				   numPulses, (int)0, ancDir);
      
	myCtl.WriteCtl(outWriter);
	}
      }
    
    catch (FileNotFoundException fnfEx ) {
      System.out.println("trouble opening: " + filename + "for writing");
      return new ErrorString("MakeCtlFile: Troublem opening file");
    }
    catch (IOException ioEx) {
      System.out.println("Trouble Opening ParameterFile");
    }
    return new Integer(rval);
    
  }


  /**
   */
  public String[] getCategoryList() {
    String[] cat = {"Operator","DAS", "Control" };
    return cat;
  }
}
