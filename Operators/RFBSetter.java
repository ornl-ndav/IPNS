/*
 * File: RFBSetter.java 
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
 *  Revision 1.3  2002/02/23 13:53:16  hammonds
 *  Added import statement for new location of GenericBatch and Generic Load Operators which are the base class for these operators.
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

public class RFBSetter extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBSetter operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBSetter( RunfileBuilder rfb, String attr,  Object val)
  {
	
    super( "RFBSetter" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("filename", rfb );
    addParameter( parameter );
    parameter= new Parameter("Attr name", attr );
    addParameter( parameter );
    parameter= new Parameter("Attr val", null );
    addParameter( parameter );

  }

public RFBSetter()
  { super( "RFBSetter" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBSetter";
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
    parameter= new Parameter("Attr name", "userName" );
    addParameter( parameter );
    parameter= new Parameter("Attr val", null);
    addParameter( parameter );



  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    String attr = (String)(getParameter(1).getValue());
    Object val = getParameter(2).getValue(); 
    int rval = 0;
    if ( attr.equals("numOfHistograms"))
    {
       short val1 = ((Number)val).shortValue();
       rval = rfb.headerSet(attr, val1 );
    } 
    else if ( (val instanceof String) )
    {
      String val1 = (String)val;
      rval = rfb.headerSet(attr, val1 );
    } 

    else if ( (val instanceof Float) )
    {
      float val1 = ((Float)val).floatValue();
      rval = rfb.headerSet(attr, val1 );
    } 
 
    //else if ( (val instanceof Short) )
   // {
    //  short val1 = ((Short)val).shortValue();
   //   rval = rfb.headerSet(attr, val1 );
   // } 

    else if ( (val instanceof Integer) && !(attr.equals("numOfHistograms")) )
    {
      int val1 = ( (Integer)val).intValue();
      rval = rfb.headerSet(attr, val1 );
    } 

    else if ( (val instanceof Double) )
    {
      double val1 = ((Double)val).doubleValue();
      rval = rfb.headerSet(attr, val1 );
    } 

    else {System.out.println("Attribute Value type not supported");}
    
    return new Integer(rval);
         
  }  

}
