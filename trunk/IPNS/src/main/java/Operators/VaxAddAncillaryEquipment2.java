/*
 * File: VaxAddAncillaryEquipment.java 
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
 *  Revision 1.3  2003/03/12 03:38:11  hammonds
 *  Cleanup Imports
 *
 *  Revision 1.2  2003/03/04 15:56:52  hammonds
 *  *** empty log message ***
 *
 *  Revision 1.1  2003/02/13 17:05:01  hammonds
 *  Initial In
 *
 *
 *
 */

package IPNS.Operators;

import  java.io.Serializable;
import  java.util.Vector;
import  DataSetTools.operator.Parameter;
import  DataSetTools.operator.Generic.Batch.GenericBatch;
import  IPNS.VaxAnc.AncFile;

/**
 * This operator instantiates IPNS.VaxAnc.AncFile.
 * 
 */

public class VaxAddAncillaryEquipment2 extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default VaxAddAncillaryEquipment operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public VaxAddAncillaryEquipment2( AncFile vaf, String attr, Vector vals)
  {
	
    super( "VaxAddAncillaryEquipment" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("AncFile", vaf );
    addParameter( parameter );
    parameter= new Parameter("File Name", null );
    addParameter( parameter );
    parameter = new Parameter("ParVals", vals);
    addParameter(parameter);
  }

public VaxAddAncillaryEquipment2()
  { super( "VaxAddAncillaryEquipment" );
	//System.out.println("def constructor");
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "VaxAddAncillaryEquipment";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
    Parameter parameter= new Parameter("AncFile", new AncFile() );
    addParameter( parameter );
    parameter= new Parameter("File Name", null);
    addParameter( parameter );
    Float[] temp = new Float[1];
    temp[0] = new Float(0.0f);
    Vector vVal = new Vector();
    vVal.add(temp);
    parameter=new Parameter("ParVals", vVal  );
    addParameter (parameter);
  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    int rval = 0;
    AncFile vaf = (AncFile) (getParameter(0).getValue());
    String name = (String)(getParameter(1).getValue());
    Vector vVals = (Vector)(getParameter(2).getValue());
    Float [] parvals = new Float[vVals.size()];
    float [] fparvals = new float[vVals.size()];
    for (int ii=0; ii < parvals.length; ii++){
	parvals[ii] = (Float)vVals.get(ii);
	fparvals[ii] = parvals[ii].floatValue();
	System.out.println(parvals[ii].floatValue());
    }
    rval = vaf.addAncillaryEquipment(name, fparvals);    
    return new Integer(rval);
         
  }  

}
