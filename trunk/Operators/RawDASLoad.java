/*
 * File:  ExampleOperator1.java 
 *
 * Copyright (C) 2001, John Hammonds
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
 * Contact : John Hammonds<jphammonds@anl.gov>
 *           Intense Pulsed Neutron Source
 *           Argonne National Laboratory
 *           Argonne, IL 60439
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.5  2003/03/12 03:29:52  hammonds
 * Cleanup imports.
 *
 * Revision 1.4  2002/07/08 14:35:57  hammonds
 * Use byte buffers to speed up file access.
 *
 * Revision 1.3  2002/03/15 00:38:45  hammonds
 * Made changes to reflect change of Data Object.  Now use Data.getInstance()
 *
 * Revision 1.2  2002/02/23 13:53:48  hammonds
 * Added import statement for new location of GenericBatch and Generic Load Operators which are the base class for these operators.
 *
 * Revision 1.1  2001/11/27 19:12:49  hammonds
 * First Checkin
 *
 *
 */
package IPNS.Operators;

import DataSetTools.operator.Operator;
import DataSetTools.operator.Parameter;
import DataSetTools.operator.Generic.Load.GenericLoad;
import DataSetTools.util.ErrorString;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.DataSetFactory;
import DataSetTools.dataset.VariableXScale;
import DataSetTools.viewer.ViewManager;
import DataSetTools.viewer.IViewManager;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.RandomAccessFile;
/** 
 *    This operator provides an example of an operator that reads data from
 *  from an ASCII text file and stores the data stored in a DataSet.  The data
 *  format that is read is basically just x, y pairs in columns with some
 *  preliminary information lines.
 *
 *    Specifically, the file format is as follows:
 *
 *  <p> 1. Five lines of text listing a title, units and labels in the 
 *         order shown.  Note:  In order for the data set created by this 
 *          operator to be merged with another data set, the units for the x 
 *          and y axes MUST BE THE SAME as for the other DataSet.
 *  <p>    Data Set Title
 *  <p>    Units for the x-axis
 *  <p>    Label for the x-axis
 *  <p>    Units for the y-axis
 *  <p>    Label for the y-axis
 *  <p> 2. One line containing the number of bins in this histogram
 *  <p> 3. The list of x y values.  There must be one more x value than
 *         y value.  To load data for a "tabulated function" instead of 
 *         for a histogram, change the size of the array of x values to be
 *         equal to the size of the array of y values, and remove the line
 *         that reads in the last bin boundary.
 *
 *  <p>  In order to be used from Isaw, this operator must be compiled and the
 *  resulting class file must be placed in one of the directories that Isaw
 *  looks at for operators, such as the ../Operators subdirectory of the 
 *  Isaw home directory.  For details on what directories are searched, see
 *  the Operator-HOWTO file, or the Isaw user manual.
 *
 *  <p>
 *  NOTE: This operator can also be run as a separate program, since it
 *  has a main program for testing purposes.  The main program merely uses 
 *  the operator to load a simple test file and pops up a view of the 
 *  data.
 */

public class RawDASLoad extends GenericLoad
{
  private static final String TITLE = "Load Raw DAS file";

 /* ------------------------- DefaultConstructor -------------------------- */
 /** 
  *   Default constructor that is used when the parameters will be
  *   set later
  */  
  public RawDASLoad()
  {
    super( TITLE );
    setDefaultParameters();
  }

 /* ----------------------------- Constructor ----------------------------- */
 /** 
  *  This form of the constructor specifies the parameters at construction 
  *  time.  The getResult method must be called to actually run the operator.
  *  
  *  @param  file_name   The fully qualified ASCII file name.
  */
  public RawDASLoad( String file_name )
  {
    super( TITLE );
    parameters = new Vector();
    addParameter( new Parameter("Filename", file_name) );
  }

 /* ------------------------------ getCommand ----------------------------- */
 /** 
  * Get the name of this operator to use in scripts
  * 
  * @return  "LoadASCII", the command used to invoke this operator in Scripts
  */
  public String getCommand()
  {
    return "LoadASCII";
  }

 /* ------------------------ setDefaultParameters ------------------------- */
 /** 
  * Sets default values for the parameters.  The parameters set must match the 
  * data types of the parameters used in the constructor.
  */
  public void setDefaultParameters()
  {
    parameters = new Vector();
    addParameter( new Parameter("Filename", "C:/") );
  }

 /* ------------------------------ getResult ------------------------------- */
 /** 
  *  Executes this operator using the current values of the parameters.
  *
  *  @return  If successful, this returns a new DataSet with the values that
  *           y-values that were read from the data file.
  */
  public Object getResult()
  {
    String file_name = (String)(getParameter(0).getValue());

    try
    {
      RandomAccessFile ff = new RandomAccessFile( file_name,"r" );
      
      byte[] bArray = new byte[(int)ff.length()];
      ff.read(bArray);
      ByteArrayInputStream bArrayIS = new ByteArrayInputStream(bArray);
      DataInputStream f = new DataInputStream(bArrayIS);

      String title   = new String(file_name);
      String x_units = new String("");
      String x_label = new String("Channel");
      String y_units = new String("");
      String y_label = new String("Counts");
      DataSetFactory ds_factory = 
	  new DataSetFactory( file_name, x_units, x_label, y_units, y_label );
      DataSet ds = ds_factory.getDataSet();

      int n_spec = f.readInt();

      for (int ii = 0; ii < n_spec; ii++ ) {
	  int n_bins = f.readInt();
	  float y[] = new float[ n_bins-1 ];
	  float x[] = new float[ n_bins ]; // histogram, so one extra bin boundary
	  int sum = f.readInt();
	  for ( int i = 0; i < n_bins-1; i++ )
	      {
		  x[i] = (float)(i + .5f);
		  y[i] = (float)f.readInt();
	      }
	  x[n_bins-1] = n_bins-1.0f+.5f;        // read the last bin boundary value

	  // Using a DataSetFactory to build the
	  // DataSet will give the DataSet a
	  // set of operators.
	  
	  Data    d  = Data.getInstance( new VariableXScale(x), y, ii+1 );
	  ds.addData_entry( d );

      }
	  return ds;
      }
      catch ( Exception E )
	  {
	      return new ErrorString( E.toString() );
	  } 
  }

 /* --------------------------------- clone -------------------------------- */
 /** 
  *  Creates a clone of this operator.  ( Operators need a clone method, so 
  *  that Isaw can make copies of them when needed. )
  */
  public Object clone()
  { 
    Operator op = new RawDASLoad();
    op.CopyParametersFrom( this );
    return op;
  }

 /* ------------------------------- main ----------------------------------- */
 /** 
  * Test program to verify that this will complile and run ok.  
  *
  */
  public static void main( String args[] )
  {
    System.out.println("Test of RawDASLoad starting...");

                                                 // make and run the operator
                                                 // to load the data
    Operator op  = new RawDASLoad("Example1.dat");
    Object   obj = op.getResult();
                                                 // display any message string
                                                 // that might be returned
    System.out.println("Operator returned: " + obj );

                                                 // if the operator produced a
                                                 // a DataSet, pop up a viewer
    if ( obj instanceof DataSet )
    {
      ViewManager vm = new ViewManager( (DataSet)obj, IViewManager.IMAGE );
    }
    
    System.out.println("Test of RawDASLoad done.");
  }
}
