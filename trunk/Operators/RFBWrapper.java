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
 *  Revision 1.5  2002/01/02 21:28:28  hammonds
 *  Added Parameter for version number.
 *
 *  Revision 1.4  2001/11/01 20:36:39  chatterjee
 *  Changed the runfile extension to .run instead of .RUN
 *
 *  Revision 1.3  2001/11/01 20:06:39  chatterjee
 *  Changed comments
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

public class RFBWrapper extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBWrapper operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  String infileName;
  public RFBWrapper( String infileName, Integer versionNumber  )
  {
	
    super( "RFBWrapper" );
    System.out.println("Inside the constructor now");  

    Parameter parameter1= new Parameter("filename", new String());
    addParameter( parameter1 );
    Parameter parameter2= new Parameter("versionNumber", new Integer(-1));
    addParameter( parameter2 );

  }

   public RFBWrapper( String infileName  )
  {
	
      super( "RFBWrapper" );
      System.out.println("Inside the constructor now");  
      Parameter parameter= new Parameter("filename", new String());
      addParameter( parameter );
      Parameter parameter2= new Parameter("versionNumber", new Integer(-1));
      addParameter( parameter2 );

  }
   
public RFBWrapper()
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
     parameter= new Parameter("versionNumber", 
                                         new Integer(-1) );
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
      int vers = 0;
      int numParams = getNum_parameters();
      String S = (String) (getParameter(0).getValue());
      if ( ((Integer)getParameter(1).getValue()).intValue() <= 0 ) {
	  vers = ((Integer)(getParameter(1).getValue())).intValue();
      }
      else {
	  vers = 5;
      }
      RunfileBuilder rfb = new RunfileBuilder();
      
      if (S != null)
	  {
	      rfb.setFileName( S + ".run");
	      rfb.headerSet("versionNumber",vers );
	  }
      return rfb;
      
  }  


  public static void main(String[] arg)
  {
    RFBWrapper rfb = new RFBWrapper("hello", new Integer(5));

  //  try{ Class O = Class.forName( "Operators.RFBWrapper");
  //   }
  //  catch(Exception s)
  //    {System.out.println("Error = "+s);
  //    }
  }

}
