/*
 * File: RFBGroupIdsSeparate.java 
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
 *  Revision 1.3  2001/12/03 17:32:31  chatterjee
 *  Uses two arrays of low and high angles to do the detector groupings
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
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBGroupIdsSeparate extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBGroupIdsSeparate operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBGroupIdsSeparate( RunfileBuilder rfb, Integer val1, Integer val2, Integer [] Ids)
  {
	
    super( "RFBGroupIdsSeparate" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("Runfilebuilder", rfb );
    addParameter( parameter );
    parameter= new Parameter("TF", null );
    addParameter( parameter );
    parameter= new Parameter("Hist", null );
    addParameter( parameter );
    parameter= new Parameter("Ids", null );
    addParameter( parameter );
  }

public RFBGroupIdsSeparate()
  { super( "RFBGroupIdsSeparate" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBGroupIdsSeparate";
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
    parameter= new Parameter("Ids", null );
    addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    int rval = 0;
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    int val1   = ( (Integer)(getParameter(1).getValue())).intValue();
    int val2   = ( (Integer)(getParameter(2).getValue())).intValue();
    //    int numIds = ( (Integer)(getParameter(3).getValue())).lent

    Vector OIds = ((Vector)(getParameter(3).getValue()));
    int numIds = OIds.size();
    Integer[] Ids = new Integer[numIds];

    int[] ids = new int[numIds];
    for (int ii = 0; ii < Ids.length; ii++ ){
	
	Ids[ii] = (Integer)((Vector)(getParameter(3).getValue())).elementAt(ii);
	ids[ii] = Ids[ii].intValue();
	//	System.out.println(ids[ii]);

   }

    rval = rfb.groupIdsSeparate(val1, val2, ids);    
    return new Integer(rval);
         
  }  

}
