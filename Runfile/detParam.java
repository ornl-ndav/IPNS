package IPNS.Runfile;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import IPNS.Runfile.*;
import java.lang.*;

/**

This class is intended to add a scrolling table which contains a listing of 
detector parameters stored in the Runfile.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 4.0
*/

public class detParam extends JScrollPane {
JTable detParamJTable;

/** A dummy method simply used to create a place holder until the runfile can
be specified
*/
public detParam() { 
   detParamJTable = new JTable(10,10);
   setViewportView(detParamJTable);

 
    }

/**
This constructor will create the table filled with the detector parameter
information.
@param runFile - A Runfile object whose information is dumped to the text area.
*/

public detParam(Runfile runFile){
    Object[][] detParamList = new Object[runFile.NumDet()][13];
   System.out.println(runFile.NumDet() );
   for (int i = 0; i < runFile.NumDet(); i++) {
//       System.out.println( "ID: " + i );
     detParamList[i][0] = new Integer(i +1);
     detParamList[i][1] = new Float((float)runFile.RawFlightPath( i + 1 ));
     detParamList[i][2] = new Float((float)runFile.FlightPath( i + 1, (int)1 ));
     detParamList[i][3] = new Float((float)runFile.RawDetectorAngle( i + 1 ));
     detParamList[i][4] = new Float((float)runFile.DetectorAngle( i + 1, (int)1 ));
     detParamList[i][5] = new Float((float)runFile.DetectorHeight( i + 1 ));
     detParamList[i][6] = new Integer((short)runFile.DetectorType( i + 1 ));
     detParamList[i][7] = new Integer((int)runFile.LowerLevelDisc(i + 1));
     detParamList[i][8] = new Integer((int)runFile.UpperLevelDisc(i + 1));
     detParamList[i][9] = new Integer((int)runFile.DataSource(i + 1));
     detParamList[i][10] = new Integer((int)runFile.NumSegs1(i + 1));
     detParamList[i][11] = new Integer((int)runFile.NumSegs2(i + 1));
     detParamList[i][12] = new Integer((int)runFile.MinID(i + 1));
     
     }

   String[] columnHeading = {"ID", "Raw Length", "Length", "Raw Angle",
				"Angle", "Height", "Type", "Lower Disc",
				"Upper Disc","Data Source", "numSegs1",
			        "numSegs2", "Min ID"};

   detParamJTable = new JTable(detParamList, columnHeading);
   setViewportView(detParamJTable);
	  
   }
   
    public static void main(String[] args ) throws IOException {
	Runfile runFile = new Runfile(args[0]);

	JFrame mainFrame = new JFrame();
	detParam dParam = new detParam( runFile );
	mainFrame.getContentPane().add(dParam);
	mainFrame.pack();
	mainFrame.show();
    }
}
