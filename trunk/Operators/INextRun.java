/*
 * File: INextRun.java 
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
 * Contact : John Hammonds jhammonds@anl.gov>
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
 *  Revision 1.2  2001/11/26 15:18:00  hammonds
 *  Deleted println which clutter output.
 *
 *  Revision 1.1  2001/11/02 19:14:06  hammonds
 *  Retrieve the next run number as an Integer
 *
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

public class INextRun extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default INextRun operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  String infileName;
  public INextRun( String iName  )
  {
	
    super( "INextRun" );
    Parameter parameter= new Parameter("filename", new String());
    addParameter( parameter );

  }

public INextRun()
  { super( "INextRun" );
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, RFBWrapper
   */
   public String getCommand()
   {
     return "INextRun";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
     Parameter parameter= new Parameter("iName", 
                                         new String());
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
      int runNum = 0;
      FileInputStream datFile, paramFile;
      Properties iDat = new Properties();
      Properties params = new Properties();
      String paramFileName = new String();
      String instDir = new String();
      String dataDir = new String();
      String iName = (String) (getParameter(0).getValue());
      String sRunNum = new String("0000"); 
	  if (iName != null)
	      {
		  String home = System.getProperty("user.home");
		  String fileSep = System.getProperty("file.separator");
		  String datFileName = new String(home + fileSep + "inst" + 
						  fileSep + iName + ".dat");
		  
		  try {
		      datFile = new FileInputStream(datFileName);
		      iDat.load(datFile);
		      instDir = iDat.getProperty("instDir");
		      dataDir = iDat.getProperty("dataDir");
		  }
		  catch (IOException e) {
		      System.out.println("Can't open file " + datFileName);
		      System.exit(0);
		  }
		  paramFileName = new String(instDir + fileSep + iName + 
					     "__V5.par");
		  try {
		      paramFile = new FileInputStream(paramFileName);
		      params = new Properties();
		      params.load(paramFile);
		  }
		  catch (IOException e) {
		      System.out.println("Can't open file " + paramFileName);
		      System.exit(0);
		  }
		  try {
		      runNum = (
				new Integer(params.getProperty("LastRun"))).
			  intValue() + 1;
		  }
		  catch (NumberFormatException e) {
		      System.out.println("Improper LastRun number in parameter"
					 + " file");
		      System.exit(0);
		  }
	      }

	  return (new Integer(runNum));
	  
  }  
    

  public static void main(String[] arg)
  {

  //  try{ Class O = Class.forName( "Operators.RFBWrapper");
  //   }
  //  catch(Exception s)
  //    {System.out.println("Error = "+s);
  //    }
  }

}
