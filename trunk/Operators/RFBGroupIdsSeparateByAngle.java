/*
 * File: RFBGroupIdsSeparateByAngle.java 
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
 *  Revision 1.2  2002/02/25 17:34:03  hammonds
 *  Added new import for change in Generic Batch location
 *
 *  Revision 1.1  2001/12/03 18:16:28  chatterjee
 *  Renamed file to include the word separate
 *
 *  Revision 1.2  2001/11/19 22:45:02  hammonds
 *  Take out debug prints.
 *
 *  Revision 1.1  2001/11/07 19:24:30  hammonds
 *  Added for scripts.
 *
 *  Revision 1.1  2001/11/01 20:37:38  chatterjee
 *  Added new method to allow grouping detectors separately
 *
 *  Revision 1.2  2001/08/01 20:57:32  chatter
 *  Added GPL license statement
 *
 *
 */

package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  DataSetTools.operator.Generic.Batch.*;
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBGroupIdsSeparateByAngle extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBGroupIdsBy Angle operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBGroupIdsSeparateByAngle( RunfileBuilder rfb, Integer val1, Integer val2, float[] low, float[] up)
  {
	
    super( "RFBGroupIdsSeparateByAngle" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("Runfilebuilder", rfb );
    addParameter( parameter );
    parameter= new Parameter("TF", null );
    addParameter( parameter );
    parameter= new Parameter("Hist", null );
    addParameter( parameter );
    parameter= new Parameter("low", null );
    addParameter( parameter );
    parameter= new Parameter("up", null );
    addParameter( parameter );
  }

public RFBGroupIdsSeparateByAngle()
  { super( "RFBGroupIdsSeparateByAngle" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBGroupIdsSeparateByAngle";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
    Parameter parameter= new Parameter("New RunfileBuilder", new RunfileBuilder() );
    addParameter( parameter );
    parameter= new Parameter("TF", null );
    addParameter( parameter );
    parameter= new Parameter("Hist", null );
    addParameter( parameter );
    parameter= new Parameter("low", null );
    addParameter( parameter );
    parameter= new Parameter("up", null );
    addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    int rval = 0;
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    int val1   = ( (Integer)(getParameter(1).getValue())).intValue();
    int val2   = ( (Integer)(getParameter(2).getValue())).intValue();



    Vector OIds = ((Vector)(getParameter(3).getValue()));
    int numIds = OIds.size();
    Float[] Ids = new Float[numIds];

    float[] ids = new float[numIds];
    for (int ii = 0; ii < Ids.length; ii++ ){
	
	Ids[ii] = (Float)((Vector)(getParameter(3).getValue())).elementAt(ii);
	ids[ii] = Ids[ii].floatValue();
		System.out.println(ids[ii]);
     }




    Vector OIds1 = ((Vector)(getParameter(4).getValue()));
    int numIds1 = OIds1.size();
    Float[] Ids1 = new Float[numIds1];

    float[] ids1 = new float[numIds1];
    for (int ii = 0; ii < Ids1.length; ii++ ){
	
	Ids1[ii] = (Float)((Vector)(getParameter(4).getValue())).elementAt(ii);
	ids1[ii] = Ids1[ii].floatValue();
		System.out.println(ids1[ii]);
   }
    rval = rfb.groupIdsSeparateByAngle(val1, val2, ids, ids1);    
    return new Integer(rval);
         
  }  

}
