/*
 * File: SetupReader.java
 *
 * Copyright (C) 1999, Alok Chatterjee
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
 * For further information, see http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2002/10/15 16:46:12  chatterjee
 *  Reads an ascii file and sets up IPNS runs.
 *
 */

package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  DataSetTools.util.*;
import  DataSetTools.operator.Generic.Batch.*;
import  IPNS.Runfile.*;
/**
 *   This class supports reading of Strings, floats, ints etc. from an
 *   ordinary text file.  In addition to methods to read each of the 
 *   basic data types from the file, there is an "unread" method that 
 *   restores the last non-blank item that was read.  Error handling and
 *   end of file detection are done using exceptions.
 */

public class SetupReader extends GenericBatch implements Serializable
{    
  
	
  /* -------------------------- Constructor -------------------------- */
 /**
   *  Construct a SetupReader to read from the specified file.  The
   *  constructor will throw an exception if the file can't be opened.  The
   *  other methods of this class should not be used if the file can't be
   *  opened.
   *
   *  @param file_name  The fully qualified file name.
   *  @param num_head The number of header lines that should be
   *  skipped while reading in the file
   *  @param num_data The number of data lines to read in. If set to
   *  zero all lines until the end of file are read.

   */

   public SetupReader( String file_name,Integer num_head, Integer num_data )
   {  
     this();
     Vector parameters = new Vector();
     addParameter(new Parameter("Filename", null) );
     addParameter( new Parameter("# of Header lines", null) );
     addParameter( new Parameter("# of Data lines", null) );
     addParameter( new Parameter("# of Columns", null) );
   }


   public SetupReader(  )
   {
     super( "RunfileSetup");
     setDefaultParameters();
   } 




   /** 
     * Get the name of this operator, used in scripts
     * @return "SetupReader", the command used to invoke this operator
     * in Scripts
     */
    public String getCommand()
    {
        return "SetupReader";
    }
   /** 
     * Sets default values for the parameters. The parameters set must
     * match the data types of the parameters used in the constructor.
     */
    public void setDefaultParameters()
    {
        parameters = new Vector();
        addParameter( new Parameter("Filename", null));
        addParameter( new Parameter("# header lines",null) );
        addParameter( new Parameter("# data lines",null) );
	addParameter( new Parameter("# of cols",null) );
    }

  /** 
    *  Executes this operator using the current values of the
    *  parameters.
    */

   public Object getResult()
   {
   String  file_name = getParameter(0).getValue().toString();
   int     num_head = ((Integer)getParameter(1).getValue()).intValue();
   int     num_data = ((Integer)getParameter(2).getValue()).intValue();
   int     max_col = ((Integer)getParameter(3).getValue()).intValue();

   TextFileReader f = null;
  Vector a = new Vector(num_data);
  Vector b = new Vector(num_data);
  Vector c = new Vector(num_data);
  Vector d = new Vector(num_data);
  Vector ee = new Vector(num_data);
  Vector ff = new Vector(num_data);

   int run_num, def_run;
   String line, user_name, run_type, anci_equip;
   float float_val;
   int[] r_num, d_run;
   String[] l, u_name, r_type, a_equip;
   float[] f_val;

   r_num = new int[num_data];
   d_run = new int[num_data];

   u_name    = new String[num_data];
   r_type    = new String[num_data];
   a_equip    = new String[num_data];
   f_val = new float[num_data];

      try
      {
          f = new TextFileReader( file_name );
          while ( f != null && !f.eof() ){

       // skip the header lines
         for( int i=0 ; i<num_head ; i++ )
         {
              line=f.read_line();
          //  System.out.println("head"+i+": "+line);
         }


         for( int i=0 ; i<num_data ; i++ )
         {
          
	  run_num = f.read_int();
	  //System.out.println("run_num = "+run_num);
          a.add(new Integer(run_num) );

	  def_run = f.read_int();
	  //System.out.println("default run = "+def_run);
         b.add(new Integer(def_run) );

	  user_name = f.read_String();
	  //System.out.println("user_name = "+user_name);
            c.addElement(user_name); 
          //}  
	  run_type = f.read_String();
	  //System.out.println("run_type = "+run_type);
          d.addElement(run_type);
            r_type[i] = (String)run_type;

	  float_val = f.read_float();
	  //System.out.println("float_val = "+float_val);
          ee.add(new Float(float_val) );
           f_val[i] = (float)float_val;

	  anci_equip = f.read_String();
	  //System.out.println("anci_equip= "+anci_equip);
          ff.addElement(anci_equip);
          a_equip[i] = (String)anci_equip;

         // System.out.println("Array elements are :" +r_num[i] +" , " +d_run[i] +" , " +u_name[i] +" , " +r_type[i] +" , " +f_val[i] +" , " +a_equip[i]  );
          System.out.println("");

         }

         } //end of while
        } //end of try
      catch ( IOException e )
      {
        //throw e;
      }

Vector list = new Vector(max_col);
       list.addElement(a);
       list.addElement(b);
       list.addElement(c);
       list.addElement(d);
       list.addElement(ee);
       list.addElement(ff);

  	return list;
      //return "done";

    }

  }

