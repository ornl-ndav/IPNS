/*
 * File: RFBGroupIDsBySegmentMap.java 
 *
 * Copyright (C) 2001, Alok Chatterjee,
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
 *  Revision 1.3  2003/03/12 03:29:52  hammonds
 *  Cleanup imports.
 *
 *  Revision 1.2  2002/02/25 17:33:44  hammonds
 *  Added new import for change in Generic Batch location
 *
 *  Revision 1.1  2002/02/06 16:22:26  chatterjee
 *  Takes a list of detector IDs and imposes a segment map for LPSD's
 *
 *
 *
 */

package IPNS.Operators;

import  java.io.Serializable;
import  java.util.Vector;
import  DataSetTools.operator.Parameter;
import  DataSetTools.operator.Generic.Batch.GenericBatch;
import  IPNS.Runfile.RunfileBuilder;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBGroupIDsBySegmentMap extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBGroupIdsBy Angle operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBGroupIDsBySegmentMap( RunfileBuilder rfb, Integer val1, Integer val2, int[] list, int[] segList)
  {
	
    super( "RFBGroupIDsBySegmentMap" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("Runfilebuilder", rfb );
    addParameter( parameter );
    parameter= new Parameter("TF", null );
    addParameter( parameter );
    parameter= new Parameter("Hist", null );
    addParameter( parameter );
    parameter= new Parameter("list", null );
    addParameter( parameter );
    parameter= new Parameter("segList", null );
    addParameter( parameter );
  }

public RFBGroupIDsBySegmentMap()
  { super( "RFBGroupIDsBySegmentMap" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBGroupIDsBySegmentMap";
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
    parameter= new Parameter("list", null );
    addParameter( parameter );
    parameter= new Parameter("segList", null );
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
    Integer[] Ids = new Integer[numIds];

    int[] ids = new int[numIds];
    for (int ii = 0; ii < Ids.length; ii++ ){
	
	Ids[ii] = (Integer)((Vector)(getParameter(3).getValue())).elementAt(ii);
	ids[ii] = Ids[ii].intValue();
     }

    Vector OIds1 = ((Vector)(getParameter(4).getValue()));
    int numIds1 = OIds1.size();
    Integer[] Ids1 = new Integer[numIds1];

    int[] ids1 = new int[numIds1];
    for (int ii = 0; ii < Ids1.length; ii++ ){
	
	Ids1[ii] = (Integer)((Vector)(getParameter(4).getValue())).elementAt(ii);
	ids1[ii] = Ids1[ii].intValue();
   }
    rval = rfb.groupIdsBySegmentMap(val1, val2, ids, ids1); 
    return new Integer(rval);
         
  }  

}