/*
 * File: RFBWrapper.java 
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
 *  Revision 1.4  2003/03/12 03:29:52  hammonds
 *  Cleanup imports.
 *
 *  Revision 1.3  2002/02/23 13:53:32  hammonds
 *  Added import statement for new location of GenericBatch and Generic Load Operators which are the base class for these operators.
 *
 *  Revision 1.2  2002/01/08 20:15:36  hammonds
 *  Fixed problem setting iName
 *
 *  Revision 1.1  2002/01/08 19:32:58  hammonds
 *  Added this class to overload the RFBWrapper routine.  This method will also set the instrument name, versionNumber, and type.
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

public class RFBWrapper2 extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBWrapper2 operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  String infileName;
  public RFBWrapper2( String infileName, String iName, int versionNumber, 
		      int instrumentType  )
  {
	
    super( "RFBWrapper" );
    System.out.println("Inside the constructor now");  

    Parameter parameter= new Parameter("filename", new String(infileName));
    addParameter( parameter );
    parameter= new Parameter("iName", new String(infileName));
    addParameter( parameter );
    parameter= new Parameter("versionNumber", new Integer(versionNumber));
    addParameter( parameter );
    parameter= new Parameter("instrumentType", new Integer(instrumentType));
    addParameter( parameter );

  }

public RFBWrapper2()
  { super( "RFBWrapper" );
	//System.out.println("def constructor");
        setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, RFBWrapper
   */
   public String getCommand()
   {
     return "RFBWrapper";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
     Parameter parameter= new Parameter("Runfilename", 
                                         new String());
     addParameter( parameter );
     parameter= new Parameter("iName", 
                                         new String());
     addParameter( parameter );
     parameter= new Parameter("versionNumber", 
                                         new Integer(-1) );
     addParameter( parameter );
     parameter= new Parameter("instrumentType", 
                                         new Integer(-1) );
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
      int vers = 0;
      int type = 0;
      int numParams = getNum_parameters();
      String S = (String) (getParameter(0).getValue());
      String iName = (String) (getParameter(1).getValue());
      vers = ((Integer)(getParameter(2).getValue())).intValue();
      type = ((Integer)(getParameter(3).getValue())).intValue();

      RunfileBuilder rfb = new RunfileBuilder();
      
      if (S != null)
	  {
	      rfb.setFileName( S + ".run");
	      rfb.headerSet("iName",iName );
	      rfb.headerSet("versionNumber",vers );
	      rfb.headerSet("instrumentType",type );
	  }
      return rfb;
      
  }  


  public static void main(String[] arg)
  {
    RFBWrapper2 rfb = new RFBWrapper2("hello", "hrcs", 5, 0);

  //  try{ Class O = Class.forName( "Operators.RFBWrapper");
  //   }
  //  catch(Exception s)
  //    {System.out.println("Error = "+s);
  //    }
  }

}
