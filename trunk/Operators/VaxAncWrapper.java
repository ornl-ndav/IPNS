/*
 * File: VaxAncWrapper.java 
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
 *  Revision 1.1  2003/02/13 17:05:02  hammonds
 *  Initial In
 *
 *
 *
 */

package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  DataSetTools.operator.Generic.Batch.*;
import  IPNS.VaxAnc.*;

/**
 * This operator instantiates IPNS.VaxAnc.VaxAnc.AncFile.
 * 
 */

public class VaxAncWrapper extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default VaxAncWrapper operator to to allow scripts to call IPNS.VaxAnc.AncFile.
   */
  String infileName;
  public VaxAncWrapper( String infileName  )
  {
	
    super( "VaxAncWrapper" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("filename", new String());
    addParameter( parameter );

  }

public VaxAncWrapper()
  { super( "VaxAncWrapper" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, VaxAncWrapper
   */
   public String getCommand()
   {
     return "VaxAncWrapper";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
     Parameter parameter= new Parameter("filename", 
                                         new String());
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    String S = (String) (getParameter(0).getValue());
    AncFile vaf = new AncFile();
    if (S != null)
    {
       vaf.setFileName( S );
    }
    return vaf;

  }  


  public static void main(String[] arg)
  {
    VaxAncWrapper rfb = new VaxAncWrapper("hello");

  //  try{ Class O = Class.forName( "Operators.VaxAncWrapper");
  //   }
  //  catch(Exception s)
  //    {System.out.println("Error = "+s);
  //    }
  }

}
