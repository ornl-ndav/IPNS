package IPNS.Runfile;

import javax.swing.*;
import webproject.*;
import webproject.dataset.*;
import webproject.viewer.*;
import webproject.components.display.image.*;
import webproject.retriever.*;
import webproject.util.*;

import IPNS.Runfile.*;
import java.io.*;

/**

This class is intended to add a window which contains an ImageView object with
data from a specified runfile.
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 4.0beta1
*/

public class PlotPane extends JScrollPane {

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
          spectrum = new Data(x_scale, y_values, id);
	data_set.addData_entry ( spectrum );
  }
  ImageView image_view = new ImageView(data_set);
  setViewportView( image_view );
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
     //     for (int id = 1; id <= runFile.NumDet(); id++) {
     for (int id = runFile.MinSubgroupID(1); id <= runFile.MaxSubgroupID( 1 ); id++) {
       progressMonitor.setProgress(id);
       progressMonitor.setNote("Detector " + id );
       //       if ( runFile.IsBinned( id, 1 ) ) {
           float min = (float)runFile.MinBinned(id);

           float max = (float)runFile.MaxBinned(id);
	   x_values = runFile.TimeChannelBoundaries( id );
      	   x_scale = new UniformXScale(min,max,runFile.NumChannelsBinned( id ) );
//	   x_scale = new VariableXScale( x_values );

       	   y_values = runFile.Get1DSpectrum( id );


           spectrum = new Data(x_scale, y_values, id);
	   data_set.addData_entry ( spectrum );
	   //           }
       }
     runFile.Close();
     ImageView image_view = new ImageView(data_set);
     plotPane.setViewportView( image_view );
        }
   catch (IOException e) {
	}
   }

}

