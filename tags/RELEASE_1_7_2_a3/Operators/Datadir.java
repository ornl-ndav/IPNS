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
 *  Revision 1.6  2005/05/25 18:39:18  dennis
 *  Removed unused imports.
 *
 *  Revision 1.5  2003/09/17 14:42:25  hammonds
 *  Added bell and extra formatting to point out messages from caught exceptions
 *
 *  Revision 1.4  2003/08/02 14:46:05  hammonds
 *  Fix constructor with iName argument.  Fix main method
 *
 *  Revision 1.3  2003/03/12 02:30:26  hammonds
 *  Trim up imports.
 *
 *  Revision 1.2  2002/02/23 13:50:00  hammonds
 *  Added import statement for new location of GenericBatch and Generic Load Operators which are the base class for these operators.
 *
 *  Revision 1.1  2001/11/02 19:12:55  hammonds
 *  Added to allow scripts to get the data directory
 *
 *
 *
 */

package IPNS.Operators;

import  java.io.FileInputStream;
import  java.io.IOException;
import  java.io.Serializable;
import  java.util.Properties;
import  java.util.Vector;
import  DataSetTools.operator.Generic.Batch.GenericBatch;
import  DataSetTools.parameter.StringPG;
//import  IPNS.Runfile.*;

/**
 * This operator instantiates .
 * 
 */

public class Datadir extends  GenericBatch
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default Datadir operator to to allow scripts to get the
   * default data directory
   */
  String infileName;
  public Datadir( String iName  )
  {
	
    super( "Datadir" );
    getParameter(0).setValue(iName);
    //StringPG parameter= new StringPG("filename", iName);
    //addParameter( parameter );

  }

public Datadir()
  { super( "Datadir" );
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, RFBWrapper
   */
   public String getCommand()
   {
     return "Datadir";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
     StringPG parameter= new StringPG("iName", 
                                         new String(""));
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
      int runNum = 0;
      FileInputStream datFile;
      Properties iDat = new Properties();
      String instDir = new String();
      String dataDir = new String();
      String iName = (String) (getParameter(0).getValue());
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
		      System.out.println("/007/007/007/007");
		      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		      System.out.println(">>>");
		      System.out.println(">>>");
		      System.out.println(">>>");
		      System.out.println(">>>    Can't open file " + 
					datFileName);
		      System.out.println(">>>");
		      System.out.println(">>>");
		      System.out.println(">>>");
		      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		      System.exit(0);
		  }
	      }

	  return (dataDir);
	  
  }  
    

  public static void main(String[] arg)
  {
    Datadir op = new Datadir(arg[0]);
    
    System.out.println(op.getResult());
  }

}
