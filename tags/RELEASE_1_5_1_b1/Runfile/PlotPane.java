package IPNS.Runfile;

import java.util.*;
import javax.swing.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;
import IPNS.Runfile.*;
import java.io.*;

/**

This class is intended to add a window which contains an ImageView object with
data from a specified runfile.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 6.0
*/

//public class PlotPane extends JScrollPane {
public class PlotPane extends JDesktopPane {

/** A dummy method simply used to create a place holder until the runfile can
be specified
*/

public PlotPane () {
  DataSet data_set = new DataSet("Sample DataSet", "sample log-info");

  data_set.setX_units ("Test X Units");
  data_set.setX_label ("Test X Label");
  data_set.setY_units ("Test Y Units");
  data_set.setY_label ("Test Y Label");

  Data spectrum;
  float[] y_values;
  UniformXScale x_scale;

  for (int id = 1; id < 10; id++) {
	x_scale = new UniformXScale(1,5,50);

	y_values = new float[50];

        for (int channel = 0; channel < 50; channel++){
	  y_values[channel] = (float) Math.sin( id * channel/10.0);
	  }
          spectrum = Data.getInstance(x_scale, y_values, id);
	data_set.addData_entry ( spectrum );
  }
  InternalViewManager image_view = new InternalViewManager(data_set, IViewManager.IMAGE);
  add( image_view );
}

/**
This constructor will create plot window filled with the detector data.
@param runFile - A Runfile object whose data is displayed.
*/
public PlotPane (Runfile runFile) throws IOException{
  ProgressMonitor progressMonitor = new ProgressMonitor(this, 
				"Loading Detecto Data", "Detector", 1,
				runFile.NumDet());
  
  PlotThread plotThread = new PlotThread( runFile, this, progressMonitor );
  plotThread.run();
}

}


class PlotThread extends Thread {
Runfile runFile;
PlotPane plotPane;
ProgressMonitor progressMonitor;

public PlotThread( Runfile runFile, PlotPane plotPane,
			ProgressMonitor progressMonitor ){
   this.runFile = runFile;
   this.progressMonitor = progressMonitor;
   this.plotPane = plotPane;
   }

public void run(){
   try{
     DataSet data_set = new DataSet(runFile.RunTitle(), "sample log-info");

     data_set.setX_units ("usec");
     data_set.setX_label ("Time");
     data_set.setY_units ("Detector ID");
     data_set.setY_label ("DetectorID");

     Data spectrum;
     float[] y_values;
     //VariableXScale x_scale;
     UniformXScale x_scale;
     float[] x_values;

     runFile.LeaveOpen();
     System.out.println( (new Date()).toString() );
     if ( runFile.header.numOfWavelengths == 0 ) {
     //     if (true){
	 System.out.println( "minSubgroup, maxSubgroup: " + runFile.MinSubgroupID(1) + ", " + 
			     runFile.MaxSubgroupID( 1 ) );
	 System.out.println( "  run has no area detectors" );
	 for (int id = runFile.MinSubgroupID(1); 
	      id <= runFile.MaxSubgroupID( 1 ); id++) {
	     progressMonitor.setProgress(id);
	     progressMonitor.setNote("Detector " + id );
	     float min = (float)runFile.MinBinned(id);
	     
	     float max = (float)runFile.MaxBinned(id);
	     x_values = runFile.TimeChannelBoundaries( id );
	     x_scale = new UniformXScale(min, max,
					 runFile.NumChannelsBinned( id ) );
	     
	     y_values = runFile.Get1DSpectrum( id );
	     
	     
	     spectrum = Data.getInstance(x_scale, y_values, id);
	     data_set.addData_entry ( spectrum );
	 }
     }
     else {
	 System.out.println( "  run has area detectors" );
	 int numX = runFile.header.numOfX;
	 int numY = runFile.header.numOfY;

	 float[][] slice = new float[1][1];
	 float[][] sliceSum = new float[numY][numX];

	 for ( int sliceNum = 1; sliceNum <= runFile.header.numOfWavelengths; 
	       sliceNum++ ) {
	     slice = runFile.AreaTimeSlice( sliceNum );
	     for ( int ix = 0; ix < numX; ix++ ) {
		 for ( int iy = 0; iy < numY; iy ++ ) {
		     sliceSum[iy][ix] += slice[iy][ix];
 		 }
	     }
	 }
	 x_scale = new UniformXScale( 1, numX, numX );
	 for ( int ii = 0; ii < numY; ii++ ) {
	     spectrum = Data.getInstance ( x_scale, sliceSum[ii], ii);
	     data_set.addData_entry ( spectrum );
	 }
     }
     System.out.println( (new Date()).toString() );
     runFile.Close();
     InternalViewManager image_view = new InternalViewManager(data_set, IViewManager.IMAGE);
     plotPane.add( image_view );
   }
   catch (IOException e) {
   }
}
    
}

