package IPNS.Runfile;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import IPNS.Runfile.*;
import java.lang.*;

/**

This class is intended to add a scrolling text area which contains a listing of information stored in the Runfile Header.  
@author John P. Hammonds, Intense Pulsed Neutron Source, Argonne National Lab
@version 4.0
*/
public class HeadText extends JScrollPane {
    JTextArea headTextArea;

    /** A dummy method simply used to create a place holder until the runfile 
	can be specified
    */
    public HeadText() { 
	headTextArea = new JTextArea();
	headTextArea.append("Empty");
	setViewportView( headTextArea );
	headTextArea.setEditable(false);
    }

    /**
       This constructor will create the text area filled with the run file 
       header information.
       @param runFile - A Runfile object whose information is dumped to the 
       text area.
    */
    public HeadText(Runfile runFile) {
	headTextArea = new JTextArea();
	setRunfile( runFile );
	
	headTextArea.setEditable(false);
    }

    public void setRunfile( Runfile runFile ) {
	Font font;
	headTextArea.setFont( font = new Font("Courier", Font.PLAIN, 12));
	headTextArea.setText( "" );
	headTextArea.append( "Version Number:          " 
			     + runFile.VersionNumber() + "\n" );
	headTextArea.append( "Number Of Detectors:     " 
			     + runFile.NumDet() + "\n" );
	headTextArea.append( "User Name:               " 
			     + runFile.UserName() + "\n");
	headTextArea.append( "Title:                   " + runFile.RunTitle()
			     + "\n");
	headTextArea.append( "Run Number:              " + runFile.RunNumber()
			     + "\n" );
	headTextArea.append( "Next Run:                " + runFile.NextRun()
			     + "\n" );
	headTextArea.append( "Start Date:              " + runFile.StartDate() 
			     + "\n" );
	headTextArea.append( "Start Time:              " + runFile.StartTime()
			     + "\n" );
	headTextArea.append( "End Date:                " + runFile.EndDate() 
			     + "\n" );
	headTextArea.append( "End Time:                " + runFile.EndTime() 
			     + "\n" );
	headTextArea.append( "Protection Status:       " 
			     + runFile.ProtectionStatus() + "\n" );
	headTextArea.append( "Variable To Monitor:    " 
			     + runFile.VarToMonitor() + "\n" );
	headTextArea.append( "Preset Monitor Counts:  " 
			     + runFile.PresetMonitorCounts() + "\n" );
	headTextArea.append( "Elapsed Monitor Counts: " 
			     + runFile.ElapsedMonitorCounts() + "\n" );
	headTextArea.append( "Preset Cycles:          " 
			     + runFile.NumOfCyclesPreset() + "\n" );
	headTextArea.append( "Elapsed Cycled:         " 
			     + runFile.NumOfCyclesCompleted() + "\n" );
	headTextArea.append( "Run After Finished:     " 
			     + runFile.RunAfterFinished() + "\n" );
	headTextArea.append( "Total Monitor Counts:   " 
			     + runFile.TotalMonitorCounts() + "\n" );
	headTextArea.append( "Detector Calib File:    " 
			     + runFile.DetectorCalibFile() + "\n" );
	headTextArea.append( "DetLocUnit:             " + runFile.DetLocUnit()
			     + "\n" );
	headTextArea.append( "Pseudo Time Unit:       " 
			     + runFile.PseudoTimeUnit() + "\n" );
	headTextArea.append( "Source to sample:       " 
			     + (float)runFile.SourceToSample() + "\n" );
	headTextArea.append( "Source to chopper:      " 
			     + (float)runFile.SourceToChopper() + "\n" );
	headTextArea.append( "Moderator Calib File:   " 
			     + runFile.ModeratorCalibFile() + "\n" );
	headTextArea.append( "Group to monitor:       " 
			     + runFile.GroupToMonitor() + "\n");
	headTextArea.append( "Channel to Monitor:     " 
			     + runFile.ChannelToMonitor() + "\n");
   headTextArea.append( "Number of Histograms:   " + runFile.NumOfHistograms()
			+ "\n");
   headTextArea.append( "Number of Time Fields:  " + runFile.NumOfTimeFields()
			+ "\n");
   headTextArea.append( "Number of Control:      " + runFile.NumOfControl()
			+"\n");
   headTextArea.append( "Control flag:           " + runFile.ControlFlag()
			+ "\n");
   headTextArea.append( "Clock Shift:            " + runFile.ClockShift()
			+ "\n");
   headTextArea.append( "Total channels:         " + runFile.TotalChannels()
			+ "\n");
   headTextArea.append( "Number of pulses:       " + runFile.NumOfPulses()
			+ "\n");
   headTextArea.append( "Size of data area:      " + runFile.SizeOfDataArea()
			+ "\n");
   headTextArea.append( "Hardware T Min:         " + runFile.HardwareTMin()
			+ "\n");
   headTextArea.append( "Hardware T Max:         " + runFile.HardwareTMax()
			+ "\n");
   headTextArea.append( "Hardware Time Delay:    " + runFile.HardTimeDelay()
			+ "\n");
   headTextArea.append( "Number of X channels:   " + runFile.NumOfX()
			+ "\n");
   headTextArea.append( "Number of Y channels:   " + runFile.NumOfY()
			+ "\n");
   headTextArea.append( "Number of wavelengths:  " + runFile.NumOfWavelengths()
			+ "\n");
   headTextArea.append( "Minimum wavelength:     " + runFile.MinWavelength()
			+ "\n");
   headTextArea.append( "Maximum wavelength:     " + runFile.MaxWavelength()
			+ "\n");
   headTextArea.append( "DTA:                    " + runFile.Dta()
			+ "\n");
   headTextArea.append( "DTD:                    " + runFile.Dtd()
			+ "\n");
   headTextArea.append( "Omega:                  " + runFile.Omega()
			+ "\n");
   headTextArea.append( "Chi:                    " + runFile.Chi()
			+ "\n");
   headTextArea.append( "Phi:                    " + runFile.Phi()
			+ "\n");
   headTextArea.append( "Dist to area left edge: " + runFile.XLeft()
			+ "\n");
   headTextArea.append( "Dist to right edge:     " + runFile.XRight()
			+ "\n");
   headTextArea.append( "Dist to top edge:       " + runFile.YUpper()
			+ "\n");
   headTextArea.append( "Dist to bottom edge:    " + runFile.YLower()
			+ "\n");
   headTextArea.append( "X displacement of area: " + runFile.XDisplacement()
			+ "\n");
   headTextArea.append( "Y displacement of area: " + runFile.YDisplacement()
			+ "\n");
   headTextArea.append( "Area channel width:     " + runFile.AreaChannelWidth()
			+ "\n");
   headTextArea.append( "Area chan double Width: " + runFile.AreaDoubleInterval()
			+ "\n");
   headTextArea.append( "Area clock period       " + runFile.AreaClock()
			+ "\n");
   headTextArea.append( "Incident Energy         " + runFile.EnergyIn()
			+ "\n");
   headTextArea.append( "Const Scattered Energy: " + runFile.EnergyOut()
			+ "\n");
   headTextArea.append( "Num. Sequential Hist:   " + runFile.NumOfSeqHist()
			+ "\n");
   headTextArea.append( "Proton Current:         " + runFile.ProtonCurrent()
			+ "\n");
   headTextArea.append( "Type of area binning:   " + runFile.AreaBinning()
			+ "\n");
   headTextArea.append( "Number of bus lockouts: " + runFile.NumOfLockouts()
			+ "\n");
   headTextArea.append( "Experiment number:      " + runFile.ExpNum()
			+ "\n");
   headTextArea.append( "First Run:              " + runFile.FirstRun()
			+ "\n");
   headTextArea.append( "Default run:            " + runFile.DefaultRun()
			+ "\n");
   headTextArea.append( "Sample position:        " + runFile.SamplePosition()
			+ "\n");
   headTextArea.append( "Num of header blocks:   " + runFile.NumOfHeadBlocks()
			+ "\n");
   headTextArea.append( "Std det clock period:   " + runFile.StandardClock()
			+ "\n");
   headTextArea.append( "LPSD clock period       " + runFile.LpsdClock()
			+ "\n");
   setViewportView( headTextArea );
   headTextArea.setCaretPosition(0);
    }
}

