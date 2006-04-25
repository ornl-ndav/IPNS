/*
 * File: S2DS.java 
 *
 * Copyright (C) 2003, John Hammonds
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
 * Contact : John Hammonds jphammonds@anl.gov>
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
 *  Revision 1.2  2005/12/22 03:26:28  hammonds
 *  Remove unused import.
 *
 *  Revision 1.1  2004/02/16 00:32:53  hammonds
 *  First In
 *
 *
 *
 */
package IPNS.Operators.Calib;

import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.IntListAttribute;
import DataSetTools.operator.Generic.GenericOperator;
import DataSetTools.parameter.DataSetPG;
import DataSetTools.parameter.SaveFilePG;
import IPNS.Calib.DS5;

/** 
This operator is used in conjuction with the SetDiscriminatorLevels Operator 
provided by ISAW.  First SetDiscriminatorLevels is used to set Discriminators
in a DataSet and then this operator is used to modify discriminator levels in
the discriminator files.
*/
public class DiscWrite extends GenericOperator implements Serializable {
  private static final String TITLE=new String("DiscWrite");
  /** Constructor */
  public DiscWrite( DataSet ds_in, String file_out) {
    super(TITLE);
    getParameter(0).setValue(ds_in);
    getParameter(1).setValue(file_out);
  }
 
  public DiscWrite() {
    super (TITLE);
    setDefaultParameters();
  }

  /** Provide the string to be used in scripts to invoke this operator */
  public String getCommand() {
    return (TITLE);
  }

  /** Provide a default set of parameter GUIs to be used for this Operator */
  public void setDefaultParameters(){
    parameters = new Vector();
    DataSetPG ds_in = new DataSetPG("Pulse Height DataSet",new DataSet());
    addParameter( ds_in );
    SaveFilePG file_out = new SaveFilePG("Discriminator File", new String());
    addParameter( file_out );
  }

  /** Perform functional part of this code */
  public Object getResult() {
    DataSet ds_in = (DataSet)getParameter(0).getValue();
    String file_out = (String)getParameter(1).getValue();
    
    System.out.println(file_out);
    DS5 discFile = new DS5();
    try {
      discFile = new DS5(file_out);
    }
    catch (IOException ex) {
      return (new String("ERROR Opening " + file_out ));
    }
    int numData = ds_in.getNum_entries();
    for (int id=0; id < numData; id++ ) {
      Data thisEntry = ds_in.getData_entry(id);
      int[] detID =
	((IntListAttribute)thisEntry.getAttribute("Detector IDs")).getIntegerValue();
      if (detID.length > 1) {
	return ("ERROR Each spectrum is allowed only one ID");
      }
      Integer lld = 
	(Integer)(thisEntry.getAttribute("Lower Level Discriminator").getValue());
      Integer uld = 
	(Integer)(thisEntry.getAttribute("Upper Level Discriminator").getValue());
      discFile.setDetDisc( detID[0], lld.intValue(), uld.intValue() );
    }
    discFile.Save(file_out);
    return file_out;
    
  }

  public String[] getCategoryList() {
    String[] cat = {"Operator","DAS", "Calib" };
    return cat;
    
    
  }

}
