package IPNS.Runfile;

import java.io.*;
import java.text.*;

/**
 * This class is a utility class for the IPNS.Runfile package. It is
 * intended to reproduce the output of 'prun header <runnumber>' on
 * the old das vax system.
 */

class PrunHead{

    double tMin;
    double tMax;
    double tStep;
    int tDoubleLength;
    short numOfChannels;
    short timeFocusBit;
    short emissionDelayBit;
    short constantDelayBit;
    short energyBinBit;
    short wavelengthBinBit;
    short pulseHeightBit;
    short gBit;
    short hBit;
    boolean used;
    String iName;
    int versionNumber;
    /**
       This function provides a test method for this class' functionality.  It
       will provide a sampling of the information that is retrieved as a new 
       PrunHead Object is created.  It accepts a filename as the first command
       line argument.

       @param args - The first command line parameter is the runfile name.  
               This parameter should contain the file path unless the file is
	       in the current directory.

    */
    public static void main(String[] args) throws IOException {
	int i,j,k;
	int numTimeTableEntries;
        int numDetMapEntries;
        double temp;
        int itemp;

        RandomAccessFile runfile = new RandomAccessFile(
							args[0], "r");
        Runfile run = new Runfile( args[0] );
	int slashIndex = args[0]
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = args[0].substring( slashIndex+1, slashIndex + 5 );
	Header header = new Header(runfile, iName );

        // Set up time fields
        numTimeTableEntries = header.timeFieldTable.size / 16;
        TimeField[] timeField = new TimeField[numTimeTableEntries+1];
        for (i=1; i <= numTimeTableEntries; i++) {
            timeField[i]  = new TimeField(runfile, i, header);
        }

        // Set up detector map
        numDetMapEntries=header.detectorMapTable.size
            / DetectorMap.mapSize(header.versionNumber);
        DetectorMap[] detectorMap = new DetectorMap[numDetMapEntries+1];
        for( i=1 ; i<=numDetMapEntries ; i++ ){
            detectorMap[i]=new DetectorMap(runfile,i,header);
        }

        // Generic information
        System.out.println( header.iName + " RUN " + header.runNum
                            +": " + header.userName );
        System.out.println( header.runTitle );
        System.out.println( header.iName + " First started at " 
                            + header.startTime + " on " + header.startDate );
        System.out.println( "Total of Monitored Variable in file = " 
                            + header.totalMonitorCounts );
        // code for when a run has bee stopped is hidden somewhere in
        // the runfile that John needs to extract and interpret
        System.out.println( "??? last stopped at " + header.endTime + " on " 
                            + header.endDate );
        System.out.println( header.numOfCyclesCompleted + " of " 
                            + header.numOfCyclesPreset + " cycles completed" );
        System.out.println( header.numOfOverflows 
                            + " Channels have overflowed" );
        System.out.println("");

        // Conrolled Devices - NOT accessable (yet)
        /* System.out.println("Controlled Devices and Parameter Settings");
           System.out.println("<SKIPPING THIS SECTION>");
           System.out.println(""); */

        // Headers
        System.out.println("******************** TAKING DATA FROM FILE FOR "
                           + "RUN " + header.runNum + " ********************");
        System.out.println("");
        System.out.println("******************** LPSDs or Standard Detectors "
                           + "********************");
        System.out.println("");

        // distances and settings
        System.out.println("LI= " + header.sourceToSample + " LC= " 
                           + header.sourceToChopper +", Hardware time range= " 
                           + header.hardwareTMin + " - " 
                           + header.hardwareTMax + " microseconds");
        System.out.println("EIN = " + header.energyIn + " meV :   EOUT= " 
                           + header.energyOut + " meV :   NUMDET = " 
                           + header.nDet );
        // something funny is comming into the variables that are
        // printed here, John will fix them in Header
        System.out.println("Detector Calibration: " + header.detCalibFile 
                           + "???, Moderator Calibration: " 
                           + header.moderatorCalibFile + "???");

        // loop over the histograms
        for( i=0 ; i<header.numOfHistograms ; i++ ){
            System.out.println("");
            System.out.println("  Histogram "+ (i+1) );
            System.out.println("");
            System.out.println("  ID  ANGLE  LF(m)  Range:Time(microseconds)"
                               +"     CHW0:    CHAN   NCH     COUNTS");
            System.out.println("");
            int numIDs=0;
            int numChan=0;
            // loop over number of groups
            for( j=run.MinSubgroupID(i+1) ; j<=run.MaxSubgroupID(i+1) ; j++ ){
                int ids[]=run.IdsInSubgroup(j);
                Segment segs[]=run.SegsInSubgroup(j);
                numIDs+=ids.length;
                if(ids.length>1){
                    System.out.println("");
                    System.out.print("          The following detector IDs "
                                     + "are grouped together:");
                    for( k=0 ; k<ids.length ; k++ ){
                        if(k==0 || k%10==0){
                            System.out.println("");
                            System.out.print("                ");
                        }
                        space_hundred(ids[k]);
                        System.out.print( ids[k]+"  ");
                    }
                    System.out.println("");
                    // slightly different from vax version because of
                    // error in converting a vax float to an IEEE float
                    temp=run.DetectorAngle(segs[0],i+1);
                    System.out.print("     Reference Angle ");
                    space_hundred(temp);
                    System.out.print(format(temp,4)
                                     +"     Reference Total Length");
                    temp=run.FlightPath(segs[0],i+1)+run.SourceToSample();
                    space_hundred(temp);
                    System.out.println(format(temp,4));
                }
                space_hundred(ids[0]);
                System.out.print(ids[0]+" ");
                temp=run.RawDetectorAngle(ids[0]);
                space_hundred(temp);
                System.out.print(format(temp,2) + " ");
                temp=run.RawFlightPath(ids[0]);
                if(temp>=0.) System.out.print(" ");
                System.out.print(format(temp,2)+"    ");
                temp=run.MinBinned(j);
                System.out.print(format(temp,3)+" - ");
                temp=run.MaxBinned(j);
                System.out.print(format(temp,3)+"    ");
                itemp=run.TimeFieldType(segs[0],i+1);
                temp=timeField[itemp].tStep;
                space_thousand(temp);
                System.out.print(format(temp,3)+" ");
                itemp=detectorMap[ids[0]].address; // it is not the address
                System.out.print("    ??? ");
                itemp=run.TimeFieldType(segs[0],i+1);
                // this is two less than in the vax version b/c this
                // does not include the sum check bits
                temp=timeField[itemp].NumOfChannels();
                numChan+=temp;
                space_thousand(temp);
                if(temp<10000) System.out.print(" ");
                System.out.print((int)temp+" ");
                temp=run.Get1DSum(j);
                space_thousand(temp);
                if(temp<1000){
                    System.out.print("      ");
                }else if(temp<10000){
                    System.out.print("      ");
                }else if(temp<100000){
                    System.out.print("     ");
                }else if(temp<1000000){
                    System.out.print("    ");
                }else if(temp<10000000){
                    System.out.print("   ");
                }else if(temp<100000000){
                    System.out.print("  ");
                }else if(temp<1000000000){
                    System.out.print(" ");
                }
                System.out.println((int)temp);

                /*System.out.println("TimeField("+itemp+")="
                  +timeField[itemp].tStep+"  "
                  +timeField[itemp].NumOfChannels());*/
            }
            // end looping over groups
            System.out.println("There are " + header.numOfHistograms 
                               + " standard detector histograms using "
                               + header.totalChannels + " channels total");
            int numGrp=1+run.MaxSubgroupID(i+1)-run.MinSubgroupID(i+1);
            System.out.println("Histogram " + (i+1) + " bins " + numIDs 
                               + " IDs in " + numGrp
                               +" groups, using "+numChan+" of the channels");
        } // end looping over histograms

	runfile.close();
    }
    static private String format( double number, int sigfig){
        NumberFormat nf=NumberFormat.getInstance();
        nf.setMaximumFractionDigits(sigfig);
        nf.setGroupingUsed(false);

        String result=nf.format(number);
        int index=result.indexOf('.');

        if(index<0){
            result=result+".";
            for( int i=0 ; i<sigfig ; i++ ){
                result=result+"0";
            }
            index=result.indexOf('.');
        }

        while(result.length()-index<=sigfig){
            result=result+"0";
        }

        return result;
    }
    static private void space_hundred(double number){
        if(number>=0.){
            System.out.print(" ");
        }
        if(Math.abs(number)<10){
            System.out.print("  ");
        }else if(Math.abs(number)<100){
            System.out.print(" ");
        }
    }
    static private void space_thousand(double number){
        if(number<10){
            System.out.print("   ");
        }else if(number<100){
            System.out.print("  ");
        }else if(number<1000){
            System.out.print(" ");
        }
    }
}
