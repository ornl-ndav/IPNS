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
    static String eol=System.getProperty("line.separator");

     /**
     * This method does all of the work to create the fixed width
     * format header, copying the format in the VAX version.
     *
     * @param filename The name of the file to be parsed
     */
    public static String getHeader(String filename) throws IOException{
        String rs="";

	int i,j,k;
	int numTimeTableEntries;
        int numDetMapEntries;
        double temp;
        int itemp;

        RandomAccessFile runfile = new RandomAccessFile(filename, "r");
        Runfile run = new Runfile( filename );
	int slashIndex = filename
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = filename.substring( slashIndex+1, slashIndex + 5 );
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
        rs+=header.iName + " RUN " + header.runNum+": " + header.userName +eol;
        rs+=header.runTitle+eol;
        rs+=header.iName + " First started at " 
            + header.startTime + " on " + header.startDate +eol;
        rs+="Total of Monitored Variable in file = " 
                            + header.totalMonitorCounts +eol;
        // code for when a run has bee stopped is hidden somewhere in
        // the runfile that John needs to extract and interpret
        rs+=header.iName + " ??? last stopped at " + header.endTime + " on " 
            + header.endDate +eol;
        rs+=header.numOfCyclesCompleted + " of " + header.numOfCyclesPreset 
            + " cycles completed" +eol;
        if(header.numOfOverflows>0){
            rs+=header.numOfOverflows + " Channels have overflowed" +eol;
        }
        rs+=eol;

        // Conrolled Devices - NOT accessable (yet)
        /* rs+="Controlled Devices and Parameter Settings"+eol;
           rs+="<SKIPPING THIS SECTION>"+eol;
           rs+=eol; */

        // Headers
        rs+="******************** TAKING DATA FROM FILE FOR RUN "
            + header.runNum + " ********************" +eol;
        rs+=eol;
        rs+="******************** LPSDs or Standard Detectors "
            + "********************" +eol;
        rs+=eol;

        // distances and settings
        rs+="LI= " + format(header.sourceToSample,2) + " LC= " 
            + format(header.sourceToChopper,2) +", Hardware time range= " 
            + header.hardwareTMin + " - " + header.hardwareTMax 
            + " microseconds" +eol;
        rs+="EIN = " + format(header.energyIn,4) + " meV :   EOUT= " 
            + format(header.energyOut,4) + " meV :   NUMDET = " 
            + header.nDet +eol;
        // stuff just for choppers
        if(run.InstrumentType()==InstrumentType.TOF_DG_SPECTROMETER){
            rs+="Hardware Time delay:  "+header.hardTimeDelay
                +",  LOF,LA1D =   ???   ???"+eol;
        }
        // something funny is comming into the variables that are
        // printed here, John will fix them in Header
        rs+="Detector Calibration: " + header.detCalibFile 
            + "???, Moderator Calibration: " + header.moderatorCalibFile 
            + "???" +eol;

        // loop over the histograms
        for( i=0 ; i<header.numOfHistograms ; i++ ){
            rs+=eol;
            rs+="  Histogram "+ (i+1) +eol;
            rs+=eol;
            rs+="  ID  ANGLE  LF(m)  Range:Time(microseconds)     CHW0:    "
                +"CHAN   NCH     COUNTS"+eol;
            rs+=eol;
            int numIDs=0;
            int numChan=0;
            // loop over number of groups
            for( j=run.MinSubgroupID(i+1) ; j<=run.MaxSubgroupID(i+1) ; j++ ){
                int ids[]=run.IdsInSubgroup(j);
                Segment segs[]=run.SegsInSubgroup(j);
                numIDs+=ids.length;
                if(ids.length>1){
                    rs+=eol;
                    rs+="          The following detector IDs are grouped "
                        + "together:";
                    for( k=0 ; k<ids.length ; k++ ){
                        if(k==0 || k%10==0){
                            rs+=eol;
                            rs+="                ";
                        }
                        rs+=space_hundred(ids[k]) + ids[k] + "  ";
                    }
                    rs+=eol;
                    // slightly different from vax version because of
                    // error in converting a vax float to an IEEE float
                    temp=run.DetectorAngle(segs[0],i+1);
                    rs+="     Reference Angle ";
                    rs+=space_hundred(temp) + format(temp,4) 
                        + "     Reference Total Length";
                    temp=run.FlightPath(segs[0],i+1)+run.SourceToSample();
                    rs+=space_hundred(temp) + format(temp,4) +eol;
                }
                rs+=space_hundred(ids[0]) + ids[0]+" ";
                temp=run.RawDetectorAngle(ids[0]);
                rs+=space_hundred(temp) + format(temp,2) + " ";
                temp=run.RawFlightPath(ids[0]);
                if(temp>=0.) rs+=" ";
                rs+=format(temp,2)+"    ";
                temp=run.MinBinned(j);
                rs+=space_thousand(temp)+format(temp,3)+" - ";
                temp=run.MaxBinned(j);
                rs+=space_thousand(temp)+format(temp,3)+"    ";
                itemp=run.TimeFieldType(segs[0],i+1);
                temp=timeField[itemp].tStep;
                rs+=space_thousand(temp) + format(temp,3)+" ";
                itemp=detectorMap[ids[0]].address; // it is not the address
                rs+="    ??? ";
                itemp=run.TimeFieldType(segs[0],i+1);
                // This is two less than in the vax version b/c this
                // does not include the sum check bits. 
                temp=timeField[itemp].NumOfChannels()+2;
                numChan+=temp;
                rs+=space_thousand(temp);
                if(temp<10000) rs+=" ";
                rs+=(int)temp+" ";
                temp=run.Get1DSum(j);
                rs+=space_thousand(temp);
                if(temp<1000){
                    rs+="      ";
                }else if(temp<10000){
                    rs+="      ";
                }else if(temp<100000){
                    rs+="     ";
                }else if(temp<1000000){
                    rs+="    ";
                }else if(temp<10000000){
                    rs+="   ";
                }else if(temp<100000000){
                    rs+="  ";
                }else if(temp<1000000000){
                    rs+=" ";
                }
                rs+=(int)temp+eol;

                /*rs+="TimeField("+itemp+")="+timeField[itemp].tStep+"  "
                  +timeField[itemp].NumOfChannels();*/
            }
            // end looping over groups
            rs+="There are " + header.numOfHistograms 
                + " standard detector histograms using "
                + header.totalChannels + " channels total" +eol;
            int numGrp=1+run.MaxSubgroupID(i+1)-run.MinSubgroupID(i+1);
            rs+="Histogram " + (i+1) + " bins " + numIDs 
                + " IDs in " + numGrp
                +" groups, using "+numChan+" of the channels" +eol;
        } // end looping over histograms

	runfile.close();

        return rs;
    }

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
        System.out.print("*****\n"+getHeader(args[0])+"*****\n");
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
    static private String space_hundred(double number){
        String rs="";

        if(number>=0.){
            rs+=" ";
        }
        if(Math.abs(number)<10){
            rs+="  ";
        }else if(Math.abs(number)<100){
            rs+=" ";
        }

        return rs;
    }
    static private String space_thousand(double number){
        String rs="";

        if(number<10){
            rs+="   ";
        }else if(number<100){
            rs+="  ";
        }else if(number<1000){
            rs+=" ";
        }

        return rs;
    }
}
