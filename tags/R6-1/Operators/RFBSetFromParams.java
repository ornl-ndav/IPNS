/*
 * File: RFBSetFromParams.java 
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
 *  Revision 1.4  2002/02/23 13:53:00  hammonds
 *  Added import statement for new location of GenericBatch and Generic Load Operators which are the base class for these operators.
 *
 *  Revision 1.3  2001/11/01 20:03:14  chatterjee
 *  Takes empty string for path of the Params fileand uses default file.
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

public class RFBSetFromParams extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBSetFromParams operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBSetFromParams( RunfileBuilder rfb, String attr, Object val)
  {
	
    super( "RFBSetFromParams" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("Runfilebuilder", rfb );
    addParameter( parameter );
    parameter= new Parameter("Filename", null );
    addParameter( parameter );

  }

public RFBSetFromParams()
  { super( "RFBSetFromParams" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBSetFromParams";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
    Parameter parameter= new Parameter("RunfileBuilder", new RunfileBuilder() );
    addParameter( parameter );
    parameter= new Parameter("File Name", null);
    addParameter( parameter );



  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    int rval = 0;
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    String val = (String)(getParameter(1).getValue());
    if(val.equalsIgnoreCase(""))
    	rval = rfb.headerSetFromParams();  
    else    
    	rval = rfb.headerSetFromParams(val);
    return new Integer(rval);         
  }  

}
