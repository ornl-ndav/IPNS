/*
 * File: ModifyHeaderElement.java 
 *
 * Copyright (C) 2001, Alok Chatterjee,
 *                     Ruth Mikkelson, 
 *                     John Hammonds
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
 * Contact : Alok Chatterjee achatterjee@anl.gov>
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
 *  Revision 1.3  2006/07/10 16:26:04  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.2  2005/05/25 18:39:19  dennis
 *  Removed unused imports.
 *
 *  Revision 1.1  2005/04/21 02:13:10  hammonds
 *  New Class to modify elements of the runfile header.
 *
 *
 *
 */
package IPNS.Operators.Modify;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.operator.Parameter;
import  DataSetTools.operator.Generic.GenericOperator;
import gov.anl.ipns.Parameters.ChoiceListPG;
import gov.anl.ipns.Parameters.LoadFilePG;
import gov.anl.ipns.Util.SpecialStrings.*;

/**
 * This operator instantiates.
 * 
 */

public class ModifyHeaderElement extends  GenericOperator 
  implements Serializable
{
  
  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBModifyHeaderElement operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public ModifyHeaderElement( String filename, String attr,  Object val)
  {
    
    super( "ModifyHeaderElement" );
    //    System.out.println("Inside the constructor now");  
    getParameter(0).setValue(filename);
    getParameter(1).setValue(attr);
    getParameter(2).setValue(val);
  }
  
  public ModifyHeaderElement()
  { super( "ModifyHeaderElement" );
    //System.out.println("def constructor");
    setDefaultParameters();
  }
  
  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
  public String getCommand()
  {
    return "ModifyHeaderElement";
  }
  
  /* -------------------------- setDefaultParmeters ------------------------- */
  /**
   *  Set the default parameters.
   */
  public void setDefaultParameters()
  {
    parameters = new Vector();  // must do this to create empty list of 
    // parameters
    LoadFilePG filenamePG = new LoadFilePG("New RunfileBuilder", new String() );
    addParameter( filenamePG );
    String[] attrStr =new String[] {"runTitle", "userName","sourceToSample"};
    ChoiceListPG attrListPG = new ChoiceListPG("Attr name", attrStr );
    attrListPG.addItem(new String("runTitle"));
    attrListPG.addItem(new String("userName"));
    attrListPG.addItem(new String("sourceToSample"));
		       
    addParameter( attrListPG );
    Parameter parameter= new Parameter("Attr val", null);
    addParameter( parameter );
  }
  

  /* ---------------------------- getResult ------------------------------- */
  
  public Object getResult()
  {
    String filename = (String) (getParameter(0).getValue());
    String attr = (String)(getParameter(1).getValue());
    Object val = getParameter(2).getValue(); 
    int rval = 0;
    try {
      RandomAccessFile runFile = new RandomAccessFile(filename, "rw");
      if ( attr.equals("userName"))
	{
	  runFile.seek(122);
	  StringBuffer tempBuff = new StringBuffer( val.toString() );
	  tempBuff.setLength(20);
	  String userName = new String(tempBuff);
	  runFile.writeBytes(userName);
	} 
      else if ( attr.equals("runTitle") )
	{
	  runFile.seek(142);
	  StringBuffer tempBuff = new StringBuffer( val.toString() );
	  tempBuff.setLength(80);
	  String runTitle = new String(tempBuff);
	  runFile.writeBytes(runTitle);
	} 
      else if ( attr.equals("sourceToSample") )
	{
	  runFile.seek(292);
	  try{
	    float sourceToSample = new Float(val.toString()).floatValue();
	    runFile.writeFloat(sourceToSample);
	  }
	  catch (NumberFormatException nex) {
	    return new ErrorString("Write of sourceToSample failed");
	  }
	} 
      runFile.close();
    }
    catch (IOException ex) {
      return new ErrorString("Trouble working with " + filename + "\n" );
    }
    return new Integer(rval);
    
  }


  /**
   */
  public String[] getCategoryList() {
    String[] cat = {"Operator","DAS", "Modify" };
    return cat;
  }
}
