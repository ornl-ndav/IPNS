package IPNS.Runfile;

import java.io.*;
//import java.lang.*;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Arrays;
import IPNS.Control.*;
import IPNS.Calib.*;
import IPNS.Runfile.RandomAccessRunfile;
/**
This class is designed to provide an interface to IPNS run files for versions
V4 and earlier.  The Runfile constructor loads information from the run file
header and the tables that it describes.  Access metods are provided to give
access to this information and to the scattering data stored in the file.
Some of the methods for this class return arrays that have an empty zeroth
element.  As a rule, if the array is indexed to detector or subgroup ID it is
indexed starting at one.  If the array contains other types of data it is
indexed starting at zero.

@author  John P Hammonds (maintainer)
@author  Richard J. Goyette (wrote initial code)
@version 6.0
*/
/*
 *
 * $Log$
 * Revision 6.47  2004/04/19 15:33:50  hammonds
 * Make efficiency change suggested by Dennis for loading subroup/segment map table.
 * Change to reading older SAD runfiles to place beam monitor at 180 degrees.
 * This is similar to a change made to reading old SAND runfiles.
 *
 * Revision 6.46  2004/04/16 15:18:19  hammonds
 * Fix problem TimeField Type.  NEver got properly converted to Segments.
 *
 * Revision 6.45  2003/10/15 02:17:43  hammonds
 * Fix problems with Javadocs.
 *
 * Revision 6.44  2003/09/19 03:19:20  hammonds
 * Remove some messages printing when loading a runfile
 *
 * Revision 6.43  2003/09/16 02:07:12  hammonds
 * Fix FirstRun() and add LastRun()
 *
 * Revision 6.42  2003/08/27 02:24:09  hammonds
 * Fix problem identifying area detector as beam monitor in new SAND files.
 *
 * Revision 6.41  2003/08/12 04:38:03  hammonds
 * Fix problem with SAND beam monitor position for mon 1.  A fiz needs to go into RunfileRetriever to take out the temporary fix.
 *
 * Revision 6.40  2003/07/25 14:28:25  dennis
 * Commented out printing of Overflow information (lines 1513-1515).
 *
 * Revision 6.39  2003/07/25 04:25:20  hammonds
 * Add overflows for area detectors.  More for std detectors later.
 *
 * Revision 6.38  2003/07/07 20:11:40  hammonds
 * Fixed time field for area detectors.  Was dividing channel starting times by hardware clock period.  This should not affect SCD (clock period = 1 us) but will affect others SAND (clock period = .5 us)
 *
 * Revision 6.37  2003/04/22 21:40:31  hammonds
 * Fix problem Loading a new SCD file with no control parameters.
 *
 * Revision 6.36  2003/04/16 20:32:21  hammonds
 * Fixed Area Detector Size Problems.  There is still ~1 pixel discrepancy in absolute pixel location.
 *
 * Revision 6.35  2003/04/14 21:43:38  hammonds
 * Fixed problem with #of segments for SCD.  There are 2 extra channels of y for diagnostic information.
 *
 * Revision 6.34  2003/04/14 20:29:58  hammonds
 * Add new
 * Changed how GLAD <V3 files operate to coordinate better with ISAWs UniformDataGrid.  Since data is stored as individual segments, this is how data will be treated.  This causes some loss of info of actual SegmentInfo.
 *
 * This also seems to have an effect on how SCD data is displayed?
 *
 * Revision 6.33  2003/03/31 20:59:45  hammonds
 * Fix problem with SCD width.
 * Fix problem with GLAD length, width & depth.
 *
 * More changes to shift version dependancies.
 *
 * Revision 6.32  2003/03/31 15:24:23  hammonds
 * Fix some glad related errors in last check in.  Lack of sleep I guess.
 *
 * Revision 6.31  2003/03/30 04:24:24  hammonds
 * Change to use new RandomAccessRunfile and RunfileInputStream Classes.  These classes were introduced to simplify the code somewhat.  Some of this simplification has been made, more is to come.
 * Also, new methods DetectorLength, DetectorWidth and DetectorDepth have been introduced to get these values for a detector.
 *
 * Revision 6.29  2003/03/19 16:13:39  hammonds
 * Remove debug print.
 *
 * Revision 6.28  2003/03/12 22:38:17  hammonds
 * Fix trailing characters at the end of username.
 *
 * Revision 6.27  2003/03/12 03:43:44  hammonds
 * Remove references to LENGTH, WIDTH,... in this file and redirect to those in
 * IPNS.Calib.DC5.  Also removed these static variables
 *
 * Revision 6.26  2003/03/11 19:14:35  hammonds
 * Fix problems with detector Rotations in v4 and below files.
 *
 * Revision 6.25  2003/03/10 21:24:46  hammonds
 * Removed pesky debug messages.
 *
 * Revision 6.24  2003/03/10 20:54:52  hammonds
 * Clean up imports.
 * Add methods to get Rotation angles.
 *
 * Revision 6.23  2003/02/11 20:09:20  hammonds
 * Fixed Problem with Time focusing calculation on Chopper LPSDs.  A focused detector is only focused at one point.  The fix was made by changing the effective position of a pixel.
 *
 * Revision 6.22  2002/12/11 21:43:33  hammonds
 * Change how chi, phi & omega are defined.
 *
 * Revision 6.21  2002/12/09 15:54:20  hammonds
 * Flip detector x-axis.  This affects new runfiles only.  All V4 & earlier should remain the same.
 *
 * Revision 6.20  2002/10/28 22:58:50  hammonds
 * Speed/memory improvements.  This is from change of code structure.
 *
 * Revision 6.19  2002/09/16 14:43:17  hammonds
 * Changed detector position code for version 5 & up. This better reflects positions for lpsds and area detectors.
 * Added crate, slot, input info for old area detectors.
 *
 * Revision 6.18  2002/08/21 17:38:04  hammonds
 * Changed code to use new logBinBit instead of wavelength bit for dt/t bin binning.
 *
 * Revision 6.17  2002/08/20 20:15:45  hammonds
 * Added code to deal with dT/T binning.
 * Added reoutines IsTimeTypeConstStep to see if time fields have constant time step.
 *
 * Revision 6.16  2002/08/05 14:46:42  hammonds
 * Changed SCD detector angle from 90 to -90 in V3 or earlier files
 * Fixed problem loading POSY and PNE0 files
 *
 * Revision 6.15  2002/07/11 18:15:17  hammonds
 * Fixed problem reading area detector data in old files.
 *
 * Revision 6.14  2002/07/02 14:43:34  hammonds
 * Fixed problem where detector ID 1 must be binned.
 * Fixed problem with SAD/SAND transmission runs.
 * Removed diagnostic lines.
 *
 * Revision 6.13  2002/04/25 14:55:17  hammonds
 * remove one more comment.
 *
 * Revision 6.12  2002/04/25 14:33:14  hammonds
 * Removed debug message
 *
 * Revision 6.11  2002/04/25 14:21:29  hammonds
 * Corrected times for area detectors.
 *
 * Revision 6.10  2002/04/24 15:17:06  hammonds
 * Undepricated the RawDetectorAngle(detID), RawFlightPath(detID), RawDetectorHeight(detID).  A special need for these was determined.  For General use, Segment calls are prefered.
 *
 * Revision 6.9  2002/04/24 14:58:01  hammonds
 * Reading Control Parameters out of the old Runfiles.
 * For SCD Copy chi, phi and omega from control parameters to header.
 *
 * Revision 6.8  2002/04/23 13:52:06  hammonds
 * Changed bad mapping of LRMECS short detectors.
 * Changed bad mapping of Area detector positions.
 *
 * Revision 6.7  2002/04/22 18:07:52  hammonds
 * Changes to accomodate SAD w/ 128x128 or 64x64
 *
 * Revision 6.6  2002/03/04 20:57:57  hammonds
 * Updates to psd detector positions.  Getting better fplength, angle & height for segments.
 *
 * Revision 6.5  2002/02/14 21:05:53  hammonds
 * Added detector info for 1x8 LPSDs.
 *
 * Revision 6.4  2002/02/13 18:36:03  hammonds
 * Changed DetectorHeight gets to accomodate LPSDs in new runfiles.
 * Changed ordering of row & coloumn for lpsds in new files.
 * Switched how detector lengths, widths,... are retrieved for old files.  They are now stored in the DC5 class.
 *
 * Revision 6.3  2002/02/12 19:46:10  hammonds
 * Fixed offset for data reads for area detectors in Get1DSpectrum.
 * Changed number of y channels in SCD from 87 to 85.  This hides data on the electronics.
 *
 * Revision 6.2  2002/02/05 17:37:02  hammonds
 * Fixed spelling of deprecated
 * Fixed segID for area segments
 * Fixed sum for area segments
 * Added methods to get FilterType, RunType, SampleEnv, and DetectorConfig
 *
 * Revision 6.1  2002/01/03 20:04:49  hammonds
 * Instrument type has moved to the header.
 *
 * Revision 6.0  2002/01/02 21:22:40  hammonds
 * Change to version 6.0
 *
 * Revision 5.49  2002/01/02 19:47:55  hammonds
 * Changes to push a version number into the detector map so that version 6 can get a larger size to allow for a bigger data area.
 *
 * Revision 5.48  2001/12/20 22:04:50  hammonds
 * Made changes to how POSY1&2 detectors are set up.
 * Changed routines that check if a det/seg/group is a beam monitor.  Added a check to make sure that the detector is in the scattering plane and is at zero angle.
 *
 * Revision 5.47  2001/12/11 22:13:59  hammonds
 * Fixed problem with segments in old GLAD files.
 *
 * Revision 5.46  2001/12/11 18:41:30  hammonds
 * Changes from detectorID to segment mapping for LPSDs
 *
 * Revision 5.45  2001/11/26 15:21:14  hammonds
 * Added some code for POSY instruments.
 * Also added printStackTrace to caught exceptions.
 *
 * Revision 5.44  2001/11/16 17:55:30  hammonds
 * Cleaned up code that reads area data
 *
 * Revision 5.43  2001/10/31 22:05:08  hammonds
 * Changed how control parameters are read from a file to account for the fact that a place holder was added for a database record for the deviceName to be stored.
 *
 * Revision 5.42  2001/10/29 21:08:22  hammonds
 * Some changes in calculation of crate, slot, input.
 *
 * Revision 5.41  2001/10/29 17:53:22  hammonds
 * Runfiles can now be read from version 5 files.
 *
 * Revision 5.40  2001/10/10 15:27:37  hammonds
 * First level adding Control Parameters.
 *
 * Revision 5.39  2001/10/08 19:25:32  hammonds
 * Condense assignment of values for length, width, ... for old runfiles.
 *
 * Revision 5.38  2001/08/27 16:10:29  hammonds
 * Fix in reading the minID array from the runfile.  Still read the number of detectors instead of number of elements.
 *
 * Revision 5.37  2001/08/03 19:04:06  hammonds
 * Made Changes to add detector grouping via scripts.
 *
 * Revision 5.36  2001/07/23 20:37:35  hammonds
 * Fixed some problems with missing data in early HRMECS V5 files.
 *
 * Revision 5.35  2001/07/18 18:33:28  hammonds
 * Made changes to row column ordering.
 *
 * Revision 5.34  2001/07/13 20:35:59  hammonds
 * Fixed Problems with glad detectors all having positove angle.
 *
 * Revision 5.33  2001/07/12 21:31:48  hammonds
 * Now reading area detector data.
 *
 * Revision 5.32  2001/07/12 14:40:40  hammonds
 * Fixed positions for GLAD and for area detectors.
 *
 * Revision 5.31  2001/07/10 21:39:28  hammonds
 * Made changes to the offset angles for area detectors and made some changes to the flight path length for area detectors.
 *
 * Revision 5.30  2001/07/10 18:24:19  hammonds
 * Changed SAD/SAND detector sizes from meters to centimeters.
 *
 * Revision 5.29  2001/07/10 18:16:30  hammonds
 * Fixed problem with detector angle for area detector segments.
 *
 * Revision 5.28  2001/07/09 21:48:33  hammonds
 * Made fixes for area detector sizes and positions.
 *
 * Revision 5.27  2001/07/03 20:20:34  hammonds
 * Fixed array limit problem defining segmentMap for V5 files.
 *
 * Revision 5.26  2001/07/02 16:43:15  hammonds
 * Changed Code for area detector Locations.
 * Added some javadoc comments.
 *
 * Revision 5.25  2001/06/29 20:26:14  hammonds
 * Changed some of the segment based detector position information to give area information.
 *
 * Revision 5.24  2001/06/29 18:57:59  hammonds
 * Added segment code.
 * Started changes to allow area detectors to be read as spectra.
 *
 * Revision 5.23  2001/06/27 20:45:23  hammonds
 * Added code for adding segment information to expand the use of this package for use with area detectors.
 * Added a number of new detector types including area detectors used on SAD, SAND and SCD.
 *
 * Revision 5.22  2001/04/09 18:45:20  hammonds
 * Added functions for dataSource and minID.
 *
 * Revision 5.21  2001/04/03 20:46:09  hammonds
 * added detector dataSource and minID tables.
 *
 * Revision 5.20  2001/03/15 17:24:56  hammonds
 * Added stuff to handle new dcalib info ( det. size, rotations, crate info...).
 *
 * Revision 5.19  2001/02/27 21:07:28  hammonds
 * add fujnction to return the instrument type.
 *
 * Revision 5.18  2000/10/04 21:58:10  hammonds
 * Added routines IsPulseHeight to determine if a detector or subgroup has been binned pulse height data.
 *
 * Revision 5.17  2000/08/17 14:25:29  hammonds
 * Added routine AreaTimeSlice to read time slice data from an area detector.
 *
 * Revision 5.16  2000/07/11 03:32:44  hammonds
 * Changed DetectorAngle() to return average angle for time focused spectrometer runs.  Also added RawDetectorHeight() method to balance out the calls to locate a detector.
 *
 * Revision 5.15  2000/06/01 16:23:31  hammonds
 * Corrected Get1DSum for new runfiles (byte order was wrong).  Changed solidAngle() to SolidAngle().
 *
 * Revision 5.12  2000/03/11 03:06:19  hammonds
 * Fixed small problem with TimeScales for V<4 and with SourceToSampleTime( float)
 *
 * Revision 5.11  2000/03/10 04:10:37  hammonds
 * Changed code to use energy conversion factor from the new PhysicalConstants class.  Small changes
 *
 * Revision 5.9  2000/02/26 18:20:36  hammonds
 * Changed Get1DSpectum to not change byte order for Version >= 5
 *
 * Revision 5.8  2000/02/25 04:07:42  hammonds
 * Made changes to detector subgroups
 *
 * Revision 5.7  2000/02/23 23:33:27  hammonds
 * Fixed Some problems with subgroup maps in new files.
 *
 * Revision 5.6  2000/02/23 04:52:50  hammonds
 * Made changes to fix problems with setting up a run from a default run.  Subgroup Map was not being read and there was a problem with disc levels.
 *
 * Revision 5.5  2000/02/22 04:10:40  hammonds
 * Have deleted obsolete lpsd code.
 *
 * Revision 5.4  2000/02/18 03:41:05  hammonds
 * Removed lpsd member variables whose classes have been removed from the package.
 *
 * Revision 5.3  2000/02/17 21:20:41  hammonds
 * Minor bug fixes.
 *
 * Revision 5.2  2000/02/16 01:43:14  hammonds
 * Changed code to handle glad LPSDs.  Changes will soon result in a common interface for all detectors.  Most of the Lpsd<...> classes will be removed in favor of a unified interface references have been commented out for now.  References to Header member variable numOfLpsd have been changed to nDet or numOfElements as appropriate.  Lpsds will be treated as a multi-element detector.
 *
 * Revision 5.1  2000/02/11 23:16:45  hammonds
 * Added code to read/write discriminator levels in the new files.  Changed type for disc level methods to int.  Added many parameters to specify detector posistion in the runfile.
 *
 * Revision 5.0  2000/01/10 22:39:48  hammonds
 * Made update to allow this package to be used with the new newrun package.
 * These updates were generally made to allow for better operation when creating
 * a run via a default run do that parameters are fed out of Runfile properly and
 * into RunfileBuilder properly.
 * HeadText was modified to allow the displayed runfile to change without creating
 * a new instance of the Text object.
 *
 *
 */

public class Runfile implements Cloneable {
   public static int CURRENT_VERSION = 6;
   /** Flag to indicate if file is left open */ boolean leaveOpen;
    /** The file name associated with this Runfile object. */
    protected String runfileName;
    /** The FileStream containing the data. */   RandomAccessRunfile runfile;
    /** The runfile header */ 			protected Header header; 
    /** The detector mapping table */		DetectorMap[] detectorMap =
	new DetectorMap[1];
    /** The time field table */			TimeField[] timeField =
	new TimeField[1];
    /** Array of detector angles */		float[] detectorAngle;
    /** Array of flight path lengths */		float[] flightPath;
    /** Array of detector heights */		float[] detectorHeight;
    /** Array of detector types */		short[] detectorType;
    /** Array of time Scale parameters */        float[] timeScale;
    /** Array of time shift parameters */        float[] timeShift;
    /** Array of area detector start Times */    float[] areaStartTime;
    /** Array of time dela y parameters */        float[] timeDelay;
    /** Stored Message Region */                 String messageRegion;
    /** PSD Mapping Table */                     LpsdDetIdMap lpsdIDMap;
    /** Discriminator Levels */			DiscLevel[] discriminator;
    /** List of subgroup by ID */		int[] subgroupIDList;
    /** Maximum Subgroup ID in a histogram */    int[] maxSubgroupID;
    /** Minimum Subgroup ID in a histogram */    int[] minSubgroupID;
    /** Subgroup Map Table */			int[][] subgroupMap;
    /** Segment Map Table */                      Segment[][] segmentMap;
    /** Version Number */                       protected int versNum;
    /** run properties stored in message area */
                                               protected Properties properties;
    /** Array of lpsd angles */		        protected float[] lpsdAngle;
    /** Array of lpsd flight path lengths */	protected float[] 
	                                                       lpsdFlightPath;
    /** Array of detector heights */		protected float[] lpsdHeight;
    /** Array of detector types */		protected short[] lpsdType;
    /** Array of detector Segments */           protected Segment[] segments;
    float[] detectorLength = new float[0];
    float[] detectorWidth = new float[0];
    float[] detectorDepth = new float[0];
    short[] detCoordSys = new short[0];
    float[] detectorRot1 = new float[0];
    float[] detectorRot2 = new float[0];
    float[] detectorEfficiency = new float[0];
    int[] psdOrder = new int[0];
    int[] numSegs1 = new int[0];
    int[] numSegs2 = new int[0];
    int[] crateNum = new int[0];
    int[] slotNum = new int[0];
    int[] inputNum = new int[0];
    int[] dataSource = new int[0];
    int[] minID = new int[0];
  int[] overflows = new int[0];
    ParameterFile[] params = new ParameterFile[0];
    //-----------------------------------------------------------------
    static double MEV_FROM_VEL = 
	PhysicalConstants.meV_FROM_m_PER_microsec_CONST;

// --------------------------- readUnsignedInteger -------------------
/*
  protected int readUnsignedInteger(RandomAccessRunfile inFile,
   int length) throws IOException {

    byte b[] = new byte[length];
    int c[] = new int[length];
    int nBytesRead = inFile.read(b, 0, length);
    int num = 0;
    
    for (int i = 0; i < length; ++i) {
       if(b[i] < 0) {
        c[i] = b[i] + 256;
      }
      else {
        c[i] = b[i];
      }
       num += c[i] << (8*i);
    }
    return num;
  }
*/
    //-------------------- ReadIntegerArray ----------------------------------
    protected int[] ReadIntegerArray(RandomAccessRunfile inFile, int wordSize,
				     int numWords)
	throws IOException {
	int[] data = new int[numWords+1];
	byte[] bdata = new byte[numWords * wordSize];
	int[] cdata = new int[wordSize];

	int nbytes = inFile.read(bdata, 0, numWords * wordSize);
	int byteIndex;
	int i, j;
	int tInt;
	for (i=0; i<numWords; i++){
	    byteIndex = i*wordSize;
	    cdata[0] = bdata[byteIndex + 0];
	    cdata[1] = bdata[byteIndex + 1];
	    if ( cdata[0] < 0 ) cdata[0]+=256;
	    if ( cdata[1] < 0 ) cdata[1]+=256;
	    data[i+1] += (cdata[0] );
	    data[i+1] += (cdata[1] << 8);
	    if ( wordSize == 4 ) {
		cdata[2] = bdata[byteIndex + 2];
		cdata[3] = bdata[byteIndex + 3];
		if ( cdata[2] < 0 ) cdata[2]+=256;
		if ( cdata[3] < 0 ) cdata[3]+=256;
		data[i+1] += (cdata[2] << 16);
		data[i+1] += (cdata[3] << 24);
	    }

	}
	return data;
    }

    //-------------------- ReadVaxReal4Array ---------------------------------

    protected float[] ReadVAXReal4Array(RandomAccessRunfile inFile, int numWords)
	throws IOException {
	int[] data;
	float[] fdata = new float[numWords+1];
	data = ReadIntegerArray (inFile, 4, numWords);

	long hi_mant, low_mant, exp, sign;
	double f_val;
	long val;
	for (int iword = 1; iword < numWords + 1; iword++) {
	    val = (long)data[iword];
	    if (val < 0) {
		val = val + (long)4294967296L;
	    }
	    /* add 128 to put in the implied 1 */
	    hi_mant  = (val & 127) + 128;
	    val      = val >> 7;
	    /* exponent is "excess 128" */
	    exp      = ((int)(val & 255)) - 128;
	    val      = val >> 8;

	    sign     = val & 1;
	    low_mant = val >> 1;
	    /* This could also be a "reserved" operand of some sort?*/
	    if ( exp == -128 )
		f_val = 0;
	    else
		f_val = (hi_mant / 256.0 + low_mant / 16777216.0) *
		    (float)Math.pow(2.0, (double)exp );

	    if ( sign == 1 )
		f_val = -f_val;

	    fdata[iword] = (float)f_val;
	} 
	return fdata;
    }

    protected short[] ReadShortArray(RandomAccessRunfile inFile, int numWords)
	throws IOException {
	short[] sdata = new short[numWords + 1];
	int[] data;  
	data = ReadIntegerArray( inFile, 2, numWords );
	for (int i = 1; i <= numWords; i++) {
	    sdata[i] = (short)data[i];
	}
	return sdata;
    }

    /** Utility method for reading an integer table from a Version 5 Runfile
     */
    private int[] readV5IntTable( ) {
	int[] data = new int[1];

	return data;

    }



    /**
       This function provides a test method for this class' functionality.  It 
       will provide a sampling of the information that is retrieved as a new 
       Runfile Object is created.  It accepts a filename as the first command 
       line argument.

       @param args - The first command line parameter is the runfile name.  
       This parameter should contain the file path unless the file is in
       the current directory.

    */
    public static void main(String[] args) throws IOException {
	int i;
	int[] subgroups;
	System.setProperty("Runfile_Debug", "yes" );
	Runfile runFile = new Runfile(args[0]);
	
	System.out.println(runFile.UserName());
	/*	if (runFile.header.nDet > 0 ){

	  for (i=1; i <= runFile.header.nDet; i++){
	    System.out.println("id, theta, l, h, Type: " 
			       + i + "  " 
			       + (float)runFile.detectorAngle[i]
			       + " " + (float)runFile.flightPath[i] + " "
			       + (float)runFile.detectorHeight[i] + " "
			       + runFile.detectorType[i]);
	}
		System.out.println(  runFile.MinSubgroupID(1) + "  " +
				     runFile.MaxSubgroupID(runFile.NumOfHistograms() ));
	for (i = runFile.MinSubgroupID(1); i <= runFile.MaxSubgroupID(runFile.NumOfHistograms())
		 ; i++){
	    System.out.print( i + " - ");
	    int[] ids = runFile.IdsInSubgroup(i);
	    for ( int k = 0; k < ids.length; k++){
		System.out.print(" " + ids[k] );
	    }
	    System.out.println(runFile.IsSubgroupBeamMonitor(i));
	}
	}
	System.out.println( runFile.NumElements() );
	float data[];
	data = runFile.Get1DSpectrum(1, 1);
	float[]  energies = runFile.TimeChannelBoundaries( 3, 1);

	float[] time = runFile.TimeChannelBoundaries( 1, 1 );
	System.out.println( "Mon 1: " + time[0] + ", " + time[time.length-1]);
	  */	//	time = runFile.TimeChannelBoundaries( 2, 1 );
	//System.out.println( "Mon 2: " + time[0] + ", " + time[time.length-1]);
	//time = runFile.TimeChannelBoundaries( 4, 1 );
	//System.out.println( "Det 5: " + time[0] + ", " + time[time.length-1]);

	if ( runFile.NumOfControl() > 0 ) {
	    ParameterFile[] params = runFile.getControlParams();
	    for (int ii = 0; ii < runFile.NumOfControl(); ii++ ){
		params[ii].printDevice();
		params[ii].printUserParameters();
		params[ii].printInstParameters();
		}

	}
	for ( Enumeration propNames= runFile.propertyNames(); 
	      propNames.hasMoreElements();  ) {
	    String key = (String)propNames.nextElement();
	    System.out.println( key + ":" + runFile.getProperty( key ) );
	}
	
    }


    /**
       Default Constructor with empty Runfile.
    */
    public Runfile() {
	header = new Header();
    }

    /**
       This method is a constructor method for the class.  On creation the 
       method will load data from the Runfile's header.  Other access methods 
       are provided to retrieve the loaded data.

       @param infileName - This argument should contain the file path unless the 
       file is in the current directory.
    */
    public Runfile( String infileName ) throws IOException {
	int i;
	byte[] bArray = new byte[0];
	ByteArrayInputStream bArrayIS;
	RunfileInputStream dataStream;

	RandomAccessRunfile runfile = 
	  new RandomAccessRunfile ( infileName, "r");
	runfileName = new String(infileName);
	int slashIndex = runfileName
	    .lastIndexOf( System.getProperty( "file.separator"));
	String iName = runfileName.substring( slashIndex+1, slashIndex + 5 );
 
	header = new Header(runfile, iName  );
	timeField[0] = new TimeField();
	runfile.seek(68);  

	detectorMap = new DetectorMap[this.header.detectorMapTable.size/
				      DetectorMap.mapSize(header.versionNumber)
				     + 1];
	runfile.seek(  header.detectorMapTable.location );
	bArray = new byte[header.detectorMapTable.size];
	runfile.read(bArray);
	bArrayIS = new ByteArrayInputStream(bArray);
	dataStream = new RunfileInputStream(bArrayIS, header.versionNumber);
	for (i=1; 
	     i <= this.header.detectorMapTable.size
		 /DetectorMap.mapSize(header.versionNumber); 
	     i++){
	    detectorMap[i] = new DetectorMap(dataStream, i, header);
	}
	dataStream.close();
	bArrayIS.close();
	bArray = new byte[0];

	timeField = new TimeField[this.header.timeFieldTable.size/16 + 1];
	for (i=1; i <= this.header.timeFieldTable.size/16; i++){
	    timeField[i] = new TimeField(runfile, i, header);
	}
	

	runfile.seek(this.header.detectorAngle.location);
	detectorAngle= 
	  runfile.readRunFloatArray( header.detectorAngle.size/4 );
	runfile.seek(this.header.flightPath.location);
	flightPath = 
	  runfile.readRunFloatArray(header.flightPath.size/4 );
	runfile.seek(this.header.detectorHeight.location);
	detectorHeight = 
	  runfile.readRunFloatArray(header.detectorHeight.size/4);
	runfile.seek(this.header.detectorType.location);
	detectorType = 
	  runfile.readRunShortArray( header.detectorType.size/2);
	if ( header.versionNumber > 4 ) {
	    if ( System.getProperty("Runfile_Debug", "no" )
		 .equalsIgnoreCase("yes") ) {
		System.out.println( "Version Number > 4" );
		System.out.println( "Object creation passed to higher version" );
	    }
	    LoadV5( runfile );
	}
	else {
	    if ( System.getProperty("Runfile_Debug", "no" )
		 .equalsIgnoreCase("yes") ) {
		System.out.println( "Version Number < = 4" );
		System.out.println( "Object creation Handled by Runfile v4" );
	    }
	    LoadV4( runfile )
;
	}
	runfile.close();
    }

    void LoadV4( RandomAccessRunfile runfile ) throws IOException {
	int i;
	int[] gladbank = new int[0];
	int[] gladdetinbank = new int[0];
	byte[] bArray = new byte[0];
	ByteArrayInputStream bArrayIS;
	RunfileInputStream dataStream;

	
	
	
	
	detectorLength = new float[header.nDet + 1];
	detectorWidth = new float[header.nDet + 1];
	detectorDepth = new float[header.nDet + 1];
	detectorEfficiency = new float[header.nDet + 1];
	psdOrder = new int[header.nDet + 1];
	numSegs1 = new int[header.nDet + 1];
	numSegs2 = new int[header.nDet + 1];
	crateNum = new int[header.nDet + 1];
	slotNum = new int[header.nDet + 1];
	inputNum = new int[header.nDet + 1];
	dataSource = new int[header.nDet + 1];
	minID = new int[header.nDet + 1];
	detectorRot1 = new float[header.nDet + 1];
	detectorRot2 = new float[header.nDet + 1];
   
	if ( (this.header.iName).equalsIgnoreCase("scd0") ||
	     (this.header.iName).equalsIgnoreCase("sad0") ||
	     (this.header.iName).equalsIgnoreCase("sad1") ||
	     (this.header.iName).equalsIgnoreCase("sand") || 
	     (this.header.iName).equalsIgnoreCase("pne0") || 
	     (this.header.iName).equalsIgnoreCase("posy") ) {
	    float[] tDetectorAngle = new float[ detectorAngle.length + 1];
	    float[] tFlightPath = new float[ flightPath.length + 1];
	    float[] tDetectorHeight = new float[ detectorHeight.length+ 1];
	    short[] tDetectorType = new short[ detectorType.length + 1];
	    
	    DetectorMap[] tDetectorMap = new DetectorMap[detectorMap.length + 1];
	    
	    System.arraycopy(detectorAngle, 0 , tDetectorAngle, 0, 
			     detectorAngle.length );
	    System.arraycopy(flightPath, 0 , tFlightPath, 0, 
			     flightPath.length );
	    System.arraycopy(detectorHeight, 0 , tDetectorHeight, 0, 
			     detectorHeight.length );
	    System.arraycopy(detectorType, 0 , tDetectorType, 0,				 
			     detectorType.length );
	    System.arraycopy(detectorMap, 0 , tDetectorMap, 0, 
			     detectorMap.length );
	    detectorAngle = tDetectorAngle;
	    flightPath = tFlightPath;
	    detectorHeight = tDetectorHeight;
	    detectorType = tDetectorType;
	    detectorMap = tDetectorMap;
	    detectorAngle[detectorAngle.length - 1] = (float)header.dta -
	      (float)(Math.atan(header.xDisplacement/header.dtd) 
		      * 180/Math.PI);
	    flightPath[flightPath.length - 1] = (float)header.dtd/100.0f;
	    detectorHeight[detectorHeight.length - 1] = 
		(float)header.yDisplacement/100.0f;
	    detectorMap[detectorMap.length - 1] = 
		new DetectorMap(header.iName, header.versionNumber );
	    if ( (this.header.iName).equalsIgnoreCase("scd0") ){
		detectorType[ detectorType.length - 1 ] = 11;
	    }
	    else if ( (this.header.iName).equalsIgnoreCase("sad0") ||
		      (this.header.iName).equalsIgnoreCase("sad1")){
		if ((header.numOfX == 64) && (header.numOfY == 64)) {
		    detectorType[ detectorType.length - 1 ] = 12;
		}
		else if ((header.numOfX == 128) && (header.numOfY == 128)) {
		    detectorType[ detectorType.length - 1 ] = 13;
		}
		else if ((header.numOfX == 1) && (header.numOfY == 1)) {
		    detectorType[ detectorType.length - 1 ] = 13;
		}
		
	    }
	    else if ( (this.header.iName).equalsIgnoreCase("sand") ){
		detectorType[ detectorType.length - 1 ] = 13;
		
	    }
	    else if ( (this.header.iName).equalsIgnoreCase("pne0") ){
		detectorType[ detectorType.length - 1 ] = 15;
		
	    }
	    else if ( (this.header.iName).equalsIgnoreCase("posy") ){
		detectorType[ detectorType.length - 1 ] = 16;
		
	    }
	    
	}
	
	lpsdIDMap = new LpsdDetIdMap( runfile, header );
	segments = new Segment[header.numOfElements + 1];
	for ( int ii = 1; ii <= header.nDet; ii++ ) {
	    if ( detectorAngle[ii] == 0.0F &&
		 flightPath[ii] == 0.0F &&
		 detectorType[ii] == 0 ) {
	    }
	    else if ( header.iName.equalsIgnoreCase( "hrcs" ) || 
		      header.iName.equalsIgnoreCase( "lrcs" ) ) {
		if ( header.versionNumber < 4) {
		    switch (detectorType[ii]){
		    case 0: {
			if ( ii < 3 )
			    detectorType[ii] = 1;
			break;
		    }
		    case 1: {
			detectorType[ii] = 2;
			break;
		    }
		    case 2: {
			detectorType[ii] = 3;
			break;
		    }
		    case 5: {
			detectorType[ii] = 4;
			break;
		    }
		    }
		}
	    }
	    else if ( header.iName.equalsIgnoreCase( "gppd" ) ) {
		switch (detectorType[ii]){
		case 1: {
		    detectorType[ii] = 9;
		    break;
		}
		case 2: {
		    detectorType[ii] = 6;
		    break;
		}
		}
	    }
	    else if ( header.iName.equalsIgnoreCase( "sepd" ) ) {
		switch (detectorType[ii]){
		case 1: {
		    detectorType[ii] = 6;
		    break;
		}
		case 2: {
		    detectorType[ii] = 9;
		    break;
		}
		}
	    }
	    else if ( header.iName.equalsIgnoreCase( "qens" ) ) {
		switch (detectorType[ii]){
		case 0: {
		    detectorType[ii] = 8;
		    break;
		}
		case 1: {
		    detectorType[ii] = 10;
		    break;
		}
		}
	    }
	    else if ( header.iName.equalsIgnoreCase( "hipd" ) ) {
		switch (detectorType[ii]){
		case 0: {
		    detectorType[ii] = 9;
		    break;
		}
		case 1: {
		    detectorType[ii] = 6;
		    break;
		}
		}
	    }
	    else if ( header.iName.equalsIgnoreCase( "chex" ) ) {
		switch (detectorType[ii]){
		case 0: {
		    detectorType[ii] = 6;
		    break;
		}
		case 1: {
		    detectorType[ii] = 10;
		    break;
		}
		}
	    }
	    if (header.iName.equalsIgnoreCase( "hrcs" ) ||
		header.iName.equalsIgnoreCase( "lrcs" )||
		header.iName.equalsIgnoreCase( "gppd" )||
		header.iName.equalsIgnoreCase( "sepd" )||
		header.iName.equalsIgnoreCase( "qens" )||
		header.iName.equalsIgnoreCase( "hipd" )||
		header.iName.equalsIgnoreCase( "chex" ) ){
		psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
		numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
		numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		detectorLength[ii] = DC5.LENGTH[detectorType[ii]];
		detectorWidth[ii] = DC5.WIDTH[detectorType[ii]];
		detectorDepth[ii] = DC5.DEPTH[detectorType[ii]];
		detectorRot1[ii] = 0.0f;
		detectorRot2[ii] = 0.0f;
		minID[ii] = ii;
		segments[ii] = new Segment();
		segments[ii].detID = ii; 
		segments[ii].row = 1; 
		segments[ii].column = 1; 
		segments[ii].length = DC5.LENGTH[detectorType[ii]]; 
		segments[ii].width = DC5.WIDTH[detectorType[ii]]; 
		segments[ii].depth = DC5.DEPTH[detectorType[ii]]; 
		segments[ii].efficiency = 
		    DC5.EFFICIENCY[detectorType[ii]]; 
		segments[ii].segID = ii;
		if ( header.versionNumber < 4 ) {
		    crateNum[ii] = (ii-1)/160 + 1;
		    slotNum[ii] = (ii- (crateNum[ii] - 1) * 160 -1) / 8   
			+ 1;
		    inputNum[ii] = ii - (crateNum[ii] -1) * 160 - 
			(slotNum[ii] -1) * 8;
		}
		else {
		    crateNum[ii] = (ii-1)/176 + 1;
		    slotNum[ii] = (ii- (crateNum[ii] - 1) * 176 -1) / 16   
			+ 1;
		    inputNum[ii] = ii - (crateNum[ii] -1) * 176 - 
			(slotNum[ii] -1) * 16;
		}
	    }
	    if ( header.iName.equalsIgnoreCase( "scd0") ) {
		switch (detectorType[ii]){
		case 0:
		case 1:{
		    detectorType[ii] = 1;
		    crateNum[ii] = 1;
		    slotNum[ii] = 1;
		    inputNum[ii] = ii;
		    break;
		}
		case 11: {
		    detectorType[ii] = 11;
		    crateNum[ii] = 1;
		    slotNum[ii] = 19;
		    inputNum[ii] = 1;
		    psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
		    numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    break;
		}
		}
	    }
	    
	    
	    if ( header.iName.equalsIgnoreCase( "sad0") ||
		 header.iName.equalsIgnoreCase( "sad1") ) {
		switch (detectorType[ii]){
		case 1: {
		    detectorType[ii] = 9;
		    crateNum[ii] = 1;
		    slotNum[ii] = 1;
		    inputNum[ii] = ii;
		    if ((ii == 1) && (detectorAngle[ii] == 0.000f)) {
		      detectorAngle[ii] = 180.0f;
		    }
		    break;
		}
		case 2: {
		    detectorType[ii] = 9;
		    crateNum[ii] = 1;
		    slotNum[ii] = 1;
		    inputNum[ii] = ii;
		    break;
		}
		case 12: 
		case 13: {
		    crateNum[ii] = 1;
		    slotNum[ii] = 19;
		    inputNum[ii] = 1;
		    if ((header.numOfX == 64) && (header.numOfY == 64)) {
			detectorType[ii] = 12;
			psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    }
		    else if ((header.numOfX == 128) && (header.numOfY == 128)) {
			detectorType[ii] = 13;
			psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    }
		    else if ((header.numOfX == 1) && (header.numOfY == 1)) {
			detectorType[ii] = 12;
			psdOrder[ii] = 1;
			numSegs2[ii] = 1;
		    }
		    break;
		}
		}
	    }
	    if ( header.iName.equalsIgnoreCase( "sand") ){
		switch (detectorType[ii]){
		case 0: 
		case 1: {
		    detectorType[ii] = 9;
		    crateNum[ii] = 1;
		    slotNum[ii] = 1;
		    inputNum[ii] = ii;
		    if ((ii == 1) && (detectorAngle[ii] == 0.000f)) {
		      detectorAngle[ii] = 180.0f;
		    }
		    break;
		}
		case 13: {
		    detectorType[ii] = 13;
		    crateNum[ii] = 1;
		    slotNum[ii] = 19;
		    inputNum[ii] = 1;
		    if ((header.numOfX == 64) && (header.numOfY == 64)) {
			detectorType[ii] = 12;
			psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    }
		    else if ((header.numOfX == 128) && (header.numOfY == 128)) {
			detectorType[ii] = 13;
			psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
			numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
			numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    }
		    else if ((header.numOfX == 1) && (header.numOfY == 1)) {
			detectorType[ii] = 13;
			psdOrder[ii] = 2;
			numSegs1[ii] = 1;
			numSegs2[ii] = 1;
		    }
		    break;
		}
		}
	    }
	    if ( header.iName.equalsIgnoreCase( "pne0") ){
		switch (detectorType[ii]){
		case 0: 
		case 1: {
		    detectorType[ii] = 1;
		    break;
		}
		case 15: {
		    detectorType[ii] = 15;
		    crateNum[ii] = 1;
		    slotNum[ii] = 19;
		    inputNum[ii] = 1;
		    psdOrder[ii] = 2;
		    numSegs2[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs1[ii] = 2;
		    break;
		}
		}
	    }
	    if ( header.iName.equalsIgnoreCase( "posy") ){
		switch (detectorType[ii]){
		case 0: 
		case 1: {
		    detectorType[ii] = 1;
		    break;
		}
		case 16: {
		    detectorType[ii] = 16;
		    crateNum[ii] = 1;
		    slotNum[ii] = 19;
		    inputNum[ii] = 1;
		    psdOrder[ii] = 2;
		    numSegs2[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs1[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    break;
		}
		}
	    }
	    if (header.iName.equalsIgnoreCase( "scd0" ) ||
		header.iName.equalsIgnoreCase( "sad0" )||
		header.iName.equalsIgnoreCase( "sad1" )||
		header.iName.equalsIgnoreCase( "posy" )||
		header.iName.equalsIgnoreCase( "pne0" )||
		header.iName.equalsIgnoreCase( "sand" ) ){
		switch (detectorType[ii]) {
		case 0:
		case 1:
		case 9: {
		    psdOrder[ii] = DC5.PSD_DIMENSION[detectorType[ii]];
		    numSegs1[ii] = DC5.NUM_OF_SEGS_1[detectorType[ii]];
		    numSegs2[ii] = DC5.NUM_OF_SEGS_2[detectorType[ii]];
		    detectorLength[ii] = DC5.LENGTH[detectorType[ii]];
		    detectorWidth[ii] = DC5.WIDTH[detectorType[ii]];
		    detectorDepth[ii] = DC5.DEPTH[detectorType[ii]];
		    minID[ii] = ii;
		    segments[ii] = new Segment();
		    segments[ii].detID = ii; 
		    segments[ii].row = 1; 
		    segments[ii].column = 1; 
		    segments[ii].length = DC5.LENGTH[detectorType[ii]]; 
		    segments[ii].width = DC5.WIDTH[detectorType[ii]]; 
		    segments[ii].depth = DC5.DEPTH[detectorType[ii]]; 
		    segments[ii].efficiency = 
			DC5.EFFICIENCY[detectorType[ii]]; 
		    segments[ii].segID = ii;
		    break;
		}
		case 11:
		case 12:
		case 13:
		case 15:
		case 16: {
		  detectorLength[ii] = (float)(header.yUpper - header.yLower);
		  detectorWidth[ii] = (float)(header.xRight - header.xLeft );
		  detectorDepth[ii] = DC5.DEPTH[detectorType[ii]];
		  detectorHeight[ii] = (float)header.yDisplacement/100.0f;
		  minID[ii] = ii;
		  int index;
		  for ( int segY = 0; segY < numSegs1[ii]; segY++) {
		    for ( int segX = 0; segX < numSegs2[ii]; segX++) {
		      index = ii + segX + segY *( numSegs2[ii]);
		      segments[index] = new Segment();
		      segments[index].detID = ii; 
		      segments[index].row = segY + 1; 
		      segments[index].column = segX + 1; 
		      segments[index].length = detectorLength[ii]/numSegs1[ii];
		      segments[index].width = detectorWidth[ii]/numSegs2[ii]; 
		      segments[index].depth = DC5.DEPTH[detectorType[ii]]; 
		      segments[index].efficiency = 
			DC5.EFFICIENCY[detectorType[ii]]; 
		      segments[index].segID = index;
		    }
		  }
		  break;
		}
		}		
		
	    }
	}
	if ( header.iName.equalsIgnoreCase( "glad" ) || 
	     header.iName.equalsIgnoreCase( "lpsd" ) ) {
	    psdOrder = new int[header.numOfElements + 1];
	    numSegs1 = new int[header.numOfElements + 1];
	    numSegs2 = new int[header.numOfElements + 1];
	    crateNum = new int[header.numOfElements + 1];
	    slotNum = new int[header.numOfElements + 1];
	    inputNum = new int[header.numOfElements + 1];
	    dataSource = new int[header.numOfElements + 1];
	    minID = new int[header.numOfElements + 1];
	    detectorRot1 = new float[header.numOfElements +1];
	    detectorRot2 = new float[header.numOfElements +1];
	    gladbank = new int[header.numOfElements + 1];
	    gladdetinbank = new int[header.numOfElements + 1];
	    detectorLength = new float[header.numOfElements + 1];
	    detectorWidth = new float[header.numOfElements + 1];
	    detectorDepth = new float[header.numOfElements + 1];
	    int detNum = 0;
	    int[] dets =new int[0];
	    for ( int jj = 0; jj < lpsdIDMap.NumOfBanks(); jj++ ) {
		dets= lpsdIDMap.DetsInBank(jj);
		int tminID, tcrate, tslot, tinput;
		for ( int kk = 0; kk < dets.length;
		      kk++ ) {
		    detNum++;
		    tminID = lpsdIDMap.MinIdForDet(jj,dets[kk]);
		    tcrate = lpsdIDMap.CrateForDet(jj,dets[kk]);
		    tslot = lpsdIDMap.SlotForDet(jj,dets[kk]);	
		    tinput = lpsdIDMap.InputForDet(jj,dets[kk]);
		    if ( tminID != 0 && tcrate != 0 && tslot != 0 &&
			 tinput != 0 ) {
			if ( jj == 0 ) {
			    psdOrder[tminID] = 1;
			    numSegs1[tminID] = 1;
			    numSegs2[tminID] = 1;
			    crateNum[tminID] = tcrate;
			    slotNum[tminID] = tslot;
			    inputNum[tminID] = tinput;
			    detectorRot1[tminID] = 0.0f;
			    detectorRot2[tminID] = 0.0f;
			    segments[tminID] = new Segment();
			    segments[tminID].detID = tminID; 
			    segments[tminID].row = 1;
			    segments[tminID].column = 1; 
			    segments[tminID].length = DC5.LENGTH[1]; 
			    segments[tminID].width = DC5.WIDTH[1]; 
			    segments[tminID].depth = DC5.DEPTH[1]; 
			    segments[tminID].efficiency = DC5.EFFICIENCY[1]; 
			    segments[tminID].segID = tminID;
			    gladbank[tminID] = 0;
			    gladdetinbank[tminID] = kk;
			}
			else {
			    for ( int ll = 0; ll < 64; ll++ ) {
				psdOrder[tminID + ll] = 1;
				numSegs1[tminID + ll] = 1;
				numSegs2[tminID + ll] = 1;
				crateNum[tminID + ll] = tcrate;
				slotNum[tminID + ll] = tslot;
				inputNum[tminID + ll] = tinput;
				psdOrder[tminID + ll] = 1;
				numSegs1[tminID + ll] = 1;
				numSegs2[tminID + ll] = 1;
				detectorRot1[tminID + ll] = 0.0f;
				detectorRot2[tminID + ll] = 0.0f;
				detectorLength[tminID+ll] = DC5.LENGTH[7]/64; 
				detectorWidth[tminID+ll] = DC5.WIDTH[7]; 
				detectorDepth[tminID] = DC5.DEPTH[7]; 
				segments[tminID + ll] = new Segment();
				segments[tminID + ll].detID = tminID + ll; 
				segments[tminID + ll].row = 1; 
				segments[tminID + ll].column = 1; 
				segments[tminID + ll].length = 
				    DC5.LENGTH[7]/ 64; 
				segments[tminID + ll].width = 
				    DC5.WIDTH[7]; 
				segments[tminID + ll].depth = 
				    DC5.DEPTH[7]; 
				segments[tminID + ll].efficiency = 
				    DC5.EFFICIENCY[7]; 
				segments[tminID + ll].segID = tminID + ll;
				gladbank[tminID + ll] = jj;
				gladdetinbank[tminID + ll] = kk;
			    }
			}
		    }
		}
	    }
	}
	if (header.versionNumber > 3) {
	    runfile.seek(this.header.discSettings.location);
	    int size = header.nDet*2;
	    short[] tdisc = ReadShortArray(runfile, size );
	    discriminator = new DiscLevel[this.header.nDet + 1];
	    for (i = 1; i <= this.header.nDet; i++) {
		discriminator[i] = new DiscLevel();
		discriminator[i].lowerLevel = tdisc[(i-1)*2 + 1];
		discriminator[i].upperLevel = tdisc[(i-1)*2 + 2];
	    }
	}

	this.leaveOpen = false;
	subgroupIDList = 
	    new int[ header.numOfElements * header.numOfHistograms + 1 ];
	subgroupMap = new int[1][1];
	segmentMap = new Segment[1][1];
	for (i=0; i < subgroupIDList.length; i++ )
	    subgroupIDList[i] = -1;
	int group = 0;
	maxSubgroupID = new int[ header.numOfHistograms + 1 ];
	minSubgroupID = new int[ header.numOfHistograms + 1 ];
	for (int nHist = 1; nHist <= header.numOfHistograms; nHist++) {
	    minSubgroupID[nHist] = group + 1;
	    if((!(this.header.iName).equalsIgnoreCase("scd0")&&
		!(this.header.iName).equalsIgnoreCase("sad0")&&
		!(this.header.iName).equalsIgnoreCase("sad1")&&
		!(this.header.iName).equalsIgnoreCase("posy")&&
		!(this.header.iName).equalsIgnoreCase("pne0")&&
		!(this.header.iName).equalsIgnoreCase("sand")) ){
		int index, tfType;
		int index2, tfType2;
		int[][] tempMap = new int[0][0];
		int[] idList =new int[0];
		int[] tempList = new int[0];
		Segment[] tsegList = new Segment[0];
		Segment[] segList = new Segment[0];
		Segment[][] tsegMap = new Segment[0][0];
		for (int nDet = 1; nDet <= header.numOfElements; nDet++ ) {
		    index = ( (nHist -1) * header.numOfElements ) + nDet;
		    tfType = detectorMap[index].tfType;
		    if ((subgroupIDList[index] == -1) && (tfType != 0)) {
			group++;
			subgroupIDList[index] = group;
			tempMap = new int[group + 1][];
			System.arraycopy(subgroupMap, 0, tempMap, 0, 
					 subgroupMap.length);
			idList = new int[2];
			idList[1] = nDet;
			tsegMap = new Segment[group + 1][];
			System.arraycopy(segmentMap, 0 , tsegMap, 0,
					 segmentMap.length );
			segList = new Segment[1];
			segList[0] = segments[nDet];
			for (int k = nDet+1; k <= header.numOfElements
				 ; k++) {
				
			    index2 = 
				( (nHist -1) * header.numOfElements ) + k;

			    tfType2 = detectorMap[index2].tfType;
			    if ( ( detectorMap[index2].address == 
				   detectorMap[index].address) &&
				 (tfType2 != 0) ){
				subgroupIDList[index2] = group;
				tempList = new int[ idList.length + 1];
				System.arraycopy(idList, 0, tempList, 0, 
						 idList.length);
				idList = tempList;
				idList[idList.length - 1] = k;
				tsegList = 
				    new Segment[segList.length + 1];
				System.arraycopy( segList, 0, tsegList,
						  0, segList.length);
				segList = tsegList;
				segList[segList.length - 1] = 
				    segments[k];
				    
			    }
			}
			subgroupMap = tempMap;
			subgroupMap[group] = new int[idList.length - 1];
			System.arraycopy(idList, 1, subgroupMap[group], 0, 
					 idList.length-1);
			segmentMap = tsegMap;
			segmentMap[group] = segList;
		    }
		}
	    }
	    else {
		int index, tfType,aindex;
		int index2, tfType2;
		int[][] tempMap = new int[0][0];
		Segment[][] tsegMap = new Segment[0][0];
		int[] idList = new int[0];
		Segment[] segList = new Segment[0];
		int[] tempList = new int[0];
		Segment[] tsegList = new Segment[0];
		for (int nDet = 1; nDet <= header.nDet; nDet++ ) {
		    if (nDet < header.nDet ) {
			index = ( (nHist -1) * header.numOfElements ) + nDet;
			tfType = detectorMap[index].tfType;
			if ((subgroupIDList[index] == -1) && (tfType != 0)) {
			    group++;
			    subgroupIDList[index] = group;

			    tempMap = new int[group + 1][];
			    System.arraycopy(subgroupMap, 0, tempMap, 0, 
					     subgroupMap.length);

			    idList = new int[2];
			    idList[1] = nDet;
			    tsegMap = new Segment[group + 1][];
			    System.arraycopy(segmentMap, 0 , tsegMap, 0,
					     segmentMap.length );
			    segList = new Segment[1];
			    segList[0] = segments[nDet];
			    for (int k = nDet+1; k <= header.nDet; k++) {
				if ( k < header.nDet ) {
				    index2 = 
					((nHist -1)*header.numOfElements ) + k;
				    tfType2 = detectorMap[index2].tfType;
				    if ( ( detectorMap[index2].address == 
					   detectorMap[index].address) &&
					 (tfType2 != 0) ){
					subgroupIDList[index2] = group;
					tempList= new int[idList.length+1];
					System.arraycopy(idList, 0, tempList, 

							 0, idList.length);
					idList = tempList;
					idList[idList.length - 1] = k;
					tsegList = 
					    new Segment[segList.length + 1];
					System.arraycopy( segList, 0, tsegList,
							  0, segList.length);
					segList = tsegList;
					segList[segList.length - 1] = 
					    segments[k];
				    
				    }
				}
			    }
			    subgroupMap = tempMap;
			    subgroupMap[group] = 
				new int[idList.length - 1];
			    System.arraycopy(idList, 1, subgroupMap[group],
					     0, idList.length-1);
			    segmentMap = tsegMap;
			    segmentMap[group] = segList;
			}
		    }
		    else { 
			/*				System .out.println("Adding groups for Area "+
							"elements " + group + 
							" already." );*/
			int segX = numSegs1[nDet];
			int segY = numSegs2[nDet];
			tempMap = new int[group + (segX*segY) + 1][];
			tsegMap = 
			    new Segment[group + (segX*segY) + 1][1];
			System.arraycopy( subgroupMap, 0,
					  tempMap, 0,
					  subgroupMap.length );
			System.arraycopy( segmentMap, 0,
					  tsegMap, 0,
					  segmentMap.length );
			subgroupMap = tempMap;
			segmentMap = tsegMap;
			//	int[] idList = new int[0];
			for ( int ny = 0; ny < segY; ny++ ) {
			    for ( int nx=0; nx < segX; nx++ ) {
				aindex = nx + ny*(segX);
				//int[] idList = { nDet,nx, ny};
				subgroupMap[group + aindex +1] = 
				    new int[1]; 
				subgroupMap[group+aindex+1][0] = nDet;
				segmentMap[group+aindex+1][0] = 
				    segments[header.nDet + aindex];
			    }
			}
			group = group + segX * segY;
		    }
		}
	    }
	    maxSubgroupID[nHist] = group;
	}

	runfile.seek(this.header.timeScaleTable.location);
	short[] timeScaleTemp = new short[this.header.timeScaleTable.size/2 + 1];
	timeScale = new float[this.header.timeScaleTable.size/2 + 1];
	timeScaleTemp = ReadShortArray(runfile,
				       header.timeScaleTable.size/2);
	for (i = 1; i <= this.header.timeScaleTable.size/2; i++) {
	    timeScale[i] = (float)(1 + timeScaleTemp[i]/ Math.pow( 2, 15));
	}

	// Correct GLAD detector angles to be in plane angles
	if ( (this.header.iName).equalsIgnoreCase("glad")) {
	    double cvdr = Math.PI/ 180.0;
	    double cvrd = 180.0/Math.PI;
	    float fp, zdet,ang1;
	    double xdet, ydetz, ydet, dist;
	    for ( int ii = 0; ii <= header.numOfElements; ii++ ){
		if ( flightPath[ii] !=0.00f &&
		     detectorAngle[ii] != 0.00f && 
		     detectorHeight[ii] !=0.00f ) {
		    fp = flightPath[ii];
		    zdet = detectorHeight[ii];
		    ang1 = detectorAngle[ii];
		    xdet= 0.0;
		    ydetz= 0.0;
		    ydet = 0.0;
		    dist = 0.0;
		    xdet = Math.cos( ang1 * cvdr) * fp;
		    ydetz = Math.sin(ang1 * cvdr) * fp;
		    if ( Math.abs(ydetz * ydetz - zdet * zdet) < 0.001 && 
			 ydetz * ydetz< zdet*zdet){
			ydet = 0.0;
		    }
		    else {
			ydet = 
			    Math.sqrt( ydetz * ydetz - zdet * zdet );
		    }
		    dist = 
			Math.sqrt( fp * fp - zdet * zdet );
		    double ang2;
		    if ( xdet < ydet ) {
			ang2 =
			    (Math.acos(xdet/dist) * cvrd);
		    }
		    else {
			ang2 = 
			    (Math.asin(ydet/dist) * cvrd );
		    }
		    if ( ang2 == Double.NaN ) {
			System.out.println ( ii + "detAngle set to 0 ");
			detectorAngle[ii] = 0.0f;
		    }
		    else {
			detectorAngle[ii] = (float)ang2;
		    }
 		    if (gladbank[ii] > 6 ) {
			detectorAngle[ii] = -1.0f * detectorAngle[ii];
		    }
		    else if (gladbank[ii] == 6 && gladdetinbank[ii] > 14 ) {
			detectorAngle[ii] = -1.0f * detectorAngle[ii];
		    }
		}
	    }
	    
	}

	//End fixing Glad detector angles

	properties = new Properties();
	runfile.seek( this.header.messageRegion.location );
	byte[] messageBytes = new byte[this.header.messageRegion.size];

	runfile.read( messageBytes );
	//	String messageText = new String(messageBytes);
	int lastStart = 0;
	String line = new String();
	int colonIndex;
	String key =new String();
	String value = new String();
	//	System.out.println(new String(messageBytes));
	for ( int ii = 0; ii < messageBytes.length - 1; ii++ ) {
	    if ( messageBytes[ii] == 13 && messageBytes[ii+1] == 10 ) {
		line = new String( messageBytes, lastStart, 
					  ii - lastStart );
		colonIndex = line.indexOf( ":" );
		key = line.substring( 0, colonIndex ).trim();
		value = line.substring( colonIndex + 1, 
					       line.length() ).trim();
		properties.put( key, value );
		lastStart = ii + 2;
	    }
	}
 	// Read Control Parameters
	if (header.controlTable.size > 0 ) {
	    //	    System.out.println ( "Reading Control Parameters" );
	    int numOfControl = 15;
	    params = new ParameterFile[numOfControl];
	    runfile.seek( this.header.controlTable.location );
	    int numRead = 0;
	    int ii = 0;
 	    do {
		params[ii] = new ParameterFile();

		readParamFileFromRunfile(runfile, params[ii]);
		numRead += 16 + 4 + 4 * params[ii].getUserParameters().length;
		ii++;
	    }while ( numRead < header.controlTable.size );
	    header.numOfControl = (short)ii;
	    //	    System.out.println ( "Done Reading Control Parameters" );
	    if ( header.instrumentType == InstrumentType.TOF_SCD ) {
		Parameter[] upar = params[0].getUserParameters();
		header.chi = upar[0].Value();
		header.phi = upar[1].Value();
		header.omega = upar[2].Value();
	    }
	}
	// Read area detector start times
	if ( header.areaStartTable.size > 0 ) {
	    runfile.seek( this.header.areaStartTable.location );
	    int numStart = header.areaStartTable.size/2;
	    int[] iareaStartTime = ReadIntegerArray( runfile, 2, numStart );
	    areaStartTime = new float[numStart];
	    for (int ii = 0; ii < numStart; ii++) {
		areaStartTime[ii] = 
		    iareaStartTime[ii+1]*(float)header.clockPeriod;
	    }
	    
	}
	// Read Overflow Table
	if ( header.numOfOverflows > 0 ) {
	  int nchall =  header.channels1D;
	  if ( (header.numOfWavelengths > 0) &&
	       (header.totalChannels > 0 )) {
	    nchall = header.channels1D + header.totalChannels;
	  }
	  int nover = (header.endOfOverflow - nchall)/2;
	  
	  int offset = nchall*2 + header.histStartAddress;
	  runfile.seek( offset );
	  overflows = runfile.readRunIntArray(header.numOfOverflows);
	  for (int ii = 1; ii <= header.numOfOverflows; ii++ ) {
	    if (overflows[ii] > header.addressOf2DData && 
		header.numOfWavelengths>0) {
	      overflows[ii] = (overflows[ii]-header.addressOf2DData);
	    }
	    else if (overflows[ii] > header.addressOf1DData){
	      overflows[ii] = (overflows[ii]-header.addressOf1DData);
	      if ( header.numOfWavelengths > 0 ) {
		overflows[ii]= overflows[ii]+header.totalChannels * 2;
	      }
	    }

	  }
	}
	  Arrays.sort(overflows);
//	  for (int ii = 1; ii <= header.numOfOverflows; ii++ ) {
//	    System.out.println("Overflow " + ii + ": " + overflows[ii]);
//	  }
    }

    void LoadV5( RandomAccessRunfile runfile ) throws IOException {
	int i;
	byte[] bArray = new byte[0];
	ByteArrayInputStream bArrayIS;
	RunfileInputStream dataStream;

	runfile.seek(this.header.detectorLength.location);
	detectorLength = 
	  runfile.readRunFloatArray(header.detectorLength.size/4);

	runfile.seek(this.header.detectorWidth.location);
	detectorWidth = 
	  runfile.readRunFloatArray(header.detectorWidth.size/4);

	runfile.seek(this.header.detectorDepth.location);
	detectorDepth = 
	  runfile.readRunFloatArray(header.detectorDepth.size/4);

	runfile.seek(this.header.detCoordSys.location);
	detCoordSys =
	  runfile.readRunShortArray(header.detCoordSys.size/2);

	runfile.seek(this.header.detectorRot1.location);
	detectorRot1 = 
	  runfile.readRunFloatArray(header.detectorRot1.size/4);

	runfile.seek(this.header.detectorRot2.location);
	detectorRot2 = 
	  runfile.readRunFloatArray( header.detectorRot2.size/4);

	runfile.seek(this.header.detectorEfficiency.location);
	detectorEfficiency =
	  runfile.readRunFloatArray( header.detectorEfficiency.size/4);

	if (header.psdOrder.size != 0 ) {
	    runfile.seek(this.header.psdOrder.location);
	    psdOrder =
	      runfile.readRunIntArray( header.psdOrder.size/4);
	}
	else {
	    psdOrder = new int[header.nDet + 1];
	    for ( int ii = 1; ii <= header.nDet; ii++ ) {
		if ( detectorType[ii] != 0 ) psdOrder[ii] = 1;
	    }
	}
	
	if (header.numSegs1.size != 0 ) {
	    runfile.seek(this.header.numSegs1.location);
	    numSegs1 = 
	      runfile.readRunIntArray( header.numSegs1.size/4);
	    for ( i = 1; i <= header.numSegs1.size / 4; i++ ) {
		//correction for missing data in Early HRMECS files
		if ((numSegs1[i] == 0 ) && (detectorType[i] != 0)) { 
		    numSegs1[i] = 1;
		}
	    }
	}
	else {
	    numSegs1 = new int[header.nDet + 1];
	    for ( int ii = 1; ii <= header.nDet; ii++ ) {
		if ( detectorType[ii] != 0 ) numSegs1[ii] = 1;
	    }
	}

	if (header.numSegs2.size != 0 ) {
	    runfile.seek(this.header.numSegs2.location);
	    numSegs2 = 
	      runfile.readRunIntArray( header.numSegs2.size/4);
	}
	else {
	    numSegs2 = new int[header.nDet + 1];
	    for ( int ii = 1; ii <= header.nDet; ii++ ) {
		if ( detectorType[ii] != 0 ) numSegs2[ii] = 1;
	    } 
	}
	    
	runfile.seek(this.header.crateNum.location);
	if (header.crateNum.size > 0 ) {
	  crateNum = 
	    runfile.readRunIntArray( header.crateNum.size/4);
	}
	else {
	    crateNum = new int[header.nDet + 1];
	}

	runfile.seek(this.header.slotNum.location);
	if ( header.slotNum.size > 0 ) { 
	  slotNum =
	    runfile.readRunIntArray( header.slotNum.size/4);
	}
	else {
	    slotNum = new int[header.nDet + 1];
	}

	runfile.seek(this.header.inputNum.location);
	if ( header.inputNum.size > 0 ) {
	  inputNum = 
	    runfile.readRunIntArray( header.inputNum.size/4);
	}
	else {
	    inputNum = new int[header.nDet + 1];
	}

	runfile.seek(this.header.dataSource.location);
	if ( header.dataSource.size > 0 ) {
	  dataSource =
	    runfile.readRunIntArray( header.dataSource.size/4 );
	}
	else {
	    dataSource = new int[header.nDet + 1];
	    minID = new int[header.nDet + 1];
	    for (i = 1; i <= header.nDet; i++ ) {
		minID[i] = i;
	    }
	}

	runfile.seek(this.header.minID.location);
	if ( header.minID.size > 0 ) {
	  minID = runfile.readRunIntArray( header.minID.size/4 );
	  // make corrections to older minIDs
	  boolean ifLess = false;
	    
	    for ( i = 1; i <= header.minID.size / 4; i++ ) {
		if (minID[i] < i ) ifLess = true;
	    }
	    if ( ifLess ) setMinID();  // Fix for early, unused entries
	}
	else {
	    minID = new int[header.numOfElements + 1];
	    for ( i = 1; i <= header.numOfElements; i++ ) {
		minID[i] = i;
	    }
	}

	runfile.seek(this.header.discSettings.location);
	bArray = new byte[ this.header.discSettings.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new RunfileInputStream( bArrayIS, header.versionNumber );
	discriminator = new DiscLevel[this.header.discSettings.size/8 + 1];
	for (i = 1; i <= this.header.discSettings.size/8; i++) {
	    discriminator[i] = new DiscLevel();
	    discriminator[i].lowerLevel = dataStream.readInt();
	    discriminator[i].upperLevel = dataStream.readInt();
	    }
	bArrayIS.close();
	dataStream.close();

 	// Read Control Parameters
	if (header.numOfControl > 0 && header.controlTable.size > 0 ) {
	    //	    System.out.println ( "Reading Control Parameters" );
	    int numOfControl = header.numOfControl;
	    params = new ParameterFile[numOfControl];
	    runfile.seek( this.header.controlTable.location );
 	    for (int ii = 0; ii < numOfControl; ii++ ) {
		params[ii] = new ParameterFile();
		readParamFileFromRunfile(runfile, params[ii]);
	    }
	    //	    System.out.println ( "Done Reading Control Parameters" );
	    
	}
	if ( (header.instrumentType == InstrumentType.TOF_SCD) && (header.numOfControl > 0) ) {
	    Parameter[] upar = params[0].getUserParameters();
	    header.chi = upar[0].Value();
	    header.phi = upar[1].Value();
	    header.omega = 45.0f;
	}


	segments = new Segment[1];
	segments[0] = new Segment();
	for (int ii = 1; ii <= header.nDet; ii++ ) {
	    int segs = minID[ii] + numSegs1[ii] * numSegs2[ii];
	    if ( segments.length < ( segs + 1 ) ){
		Segment [] tsegments =new Segment[segs + 1];
		System.arraycopy( segments, 0 , tsegments, 0, 
				  segments.length);
		segments = tsegments;
	    }
	    for ( int segY = 0; segY < numSegs1[ii]; segY ++ ) {
		for ( int segX = 0; segX < numSegs2[ii]; segX ++ ) {
		    int segID = minID[ii] + segX + segY * numSegs2[ii];
		    segments[segID] = 
			new Segment( ii, segY +1 , segX + 1, 
				     detectorLength[ii]/numSegs1[ii],
				     detectorWidth[ii]/numSegs2[ii],
				     detectorDepth[ii],
				     detectorEfficiency[ii],
				     segID
				     );
		    
		}
	    }
	}
	runfile.seek(this.header.detectorSGMap.location );
	int [][] IDMap = 
	    new int[this.header.numOfHistograms][this.header.numOfElements];
	subgroupIDList = 
	    new int[this.header.numOfElements *this.header.numOfHistograms +1];
	
	minSubgroupID = new int[this.header.numOfHistograms + 1];
	maxSubgroupID = new int[this.header.numOfHistograms + 1];
	for ( i = 0; i < this.header.numOfHistograms; i++ ) {
	    for (int jj = 0; jj < this.header.numOfElements; jj++ ) {
		IDMap[i][jj] = runfile.readInt();
		//		System.out.println( "IDMap["+i+"][" +jj +"] = " + IDMap[i][jj]);
		subgroupIDList[(i)* this.header.numOfElements + jj]  = IDMap[i][jj];
		if ( IDMap[i][jj] > maxSubgroupID[i + 1]){
		    maxSubgroupID[i + 1] = IDMap[i][jj];
		}
		if ( (IDMap[i][jj] < minSubgroupID[i + 1] 
		      || minSubgroupID[i + 1] == 0)
		      && (IDMap[i][jj] > 0)  ) {
		    minSubgroupID[i+1] = IDMap[i][jj];
		}
	    }
	}
	subgroupMap = 
	    new int[maxSubgroupID[this.header.numOfHistograms]+ 1][];
	segmentMap =
	    new Segment[maxSubgroupID[this.header.numOfHistograms]+ 1][];
	//	Segment[] segList = j
/* 
	for ( i = 1 ; i <= maxSubgroupID[this.header.numOfHistograms]; i++ ) {
	    int[] idList = new int[0];
	    Segment[] segList = new Segment[0];
	    for ( int jj = 0; jj < this.header.numOfHistograms; jj++ ) {
		for ( int kk = 0; kk < this.header.numOfElements; kk++ ) {
		    //	if ( minID[kk + 1] >=0 ) {
		    if ( IDMap[jj][kk] == i ) {
			int[] temp = new int[ idList.length + 1];
			System.arraycopy( idList, 0, temp, 0, idList.length );
			temp[idList.length] = kk + 1;
 			idList = temp;
			Segment[] tseg = new Segment[ segList.length + 1];
			tseg[segList.length] = segments[ kk + 1];
			segList = tseg;
		    } 
		    //}
            }
	    }
	    subgroupMap[i] = idList;
	    segmentMap[i] = segList;
	}
*/
                                // first count how many times an id occurs in
                                // the IDMap
      int id_count[] = new int [maxSubgroupID[this.header.numOfHistograms]+1];

      for ( int id = 0; id < id_count.length; id++ )
          id_count[id] = 0;                   // zero out all of the counters

                                              // step across the IDMap once
                                              // incrementing the counters
      for ( int jj = 0; jj < this.header.numOfHistograms; jj++ )
        for ( int kk = 0; kk < this.header.numOfElements; kk++ )
        {
          i = IDMap[jj][kk];
          if ( i > 0 )
            id_count[i]++;
        }
                                            // now allocate proper size arrays
      for ( i = 1 ; i <= maxSubgroupID[this.header.numOfHistograms]; i++ )
      {
        subgroupMap[i] = new int[ id_count[i] ];
        segmentMap [i] = new Segment[ id_count[i] ];
      }
                                          // set up a list of next free index
                                          // for each id and start index at 0
      int next_index[] = new int [ id_count.length ];
      for ( int id = 0; id < next_index.length; id++ )
        next_index[id] = 0;
                                          // step across the array once more
                                          // adding the group_id and segment
                                          // to the right list
      for ( int jj = 0; jj < this.header.numOfHistograms; jj++ )
        for ( int kk = 0; kk < this.header.numOfElements; kk++ )
        {
           i = IDMap[jj][kk];
           if ( i > 0 )                     // save in next free spot in list
           {
             subgroupMap[i][ next_index[i] ] = kk + 1;
             segmentMap [i][ next_index[i] ] = segments[ kk + 1 ];
             next_index[i]++;
           }
        }


	runfile.seek(this.header.timeScaleTable.location);
	bArray = new byte[ this.header.timeScaleTable.size ];
	runfile.read( bArray );
	bArrayIS = new ByteArrayInputStream( bArray );
	dataStream = new RunfileInputStream( bArrayIS,  header.versionNumber );
	timeScale = new float[this.header.timeScaleTable.size/4 + 1];
	for (i = 1; i <= this.header.timeScaleTable.size/4; i++) {
	    timeScale[i] = dataStream.readFloat();
	}
	bArrayIS.close();
	dataStream.close();
	properties = new Properties();
	//header.numOfElements = header.nDet;

    }

    /**
       @return The version Number for this Runfile.
    */
    public int VersionNumber(){
	return this.header.versionNumber;
    }

    /**
       @return The number of standard detectors for this instrument
    */
    public int NumDet(){
	return this.header.nDet;
    }

    /**
       @return The number of detector elements for this instrument
    */
    public int NumElements(){
	return this.header.numOfElements;
    }

    /**
       @return The title stored for this run.
    */
    public String RunTitle(){
	return this.header.runTitle.trim();
    }

    /**
       @return The user name that was stored in the run file header.
    */ 
    public String UserName(){
	return this.header.userName.trim();
    }

    /**
       @return The run number for this file.
    */
    public int RunNumber(){
	return this.header.runNum;
    }

    /**
       @return the run number to be started at the end of this run.
    */
    public int NextRun(){
	return this.header.nextRun;
    }

    /**
       @return the date the run was first started.
    */
    public String StartDate(){
	return this.header.startDate;
    }

    /**
       @return The time the run was started.
    */
    public String StartTime(){
	return this.header.startTime;
    }

    /**
       @return The date that the run ended.
    */
    public String EndDate(){
	return this.header.endDate;
    }

    /**
       @return The time that the run ended.
    */
    public String EndTime(){
	return this.header.endTime;
    }

    /**
       @return The protection status for the file.
    */
    public char ProtectionStatus(){
	return this.header.protStatus;
    }

    /**
       @return The variable to be monitored for runtime sequencing.
    */
    public char VarToMonitor(){
	return this.header.varToMonitor;
    }

    /**
       @return The number of preset monitor counts used for runtime sequencing.
    */
    public int PresetMonitorCounts(){
	return this.header.presetMonitorCounts;
    }

    /**
       @return The number of elapsed monitor counts in the last cycle
    */
    public int ElapsedMonitorCounts(){
	return this.header.elapsedMonitorCounts;
    }

    /**
       @return The number of preset cycles used in runtime sequencing.
    */
    public short NumOfCyclesPreset(){
	return this.header.numOfCyclesPreset;
    }

    /**
       @return The number of cycles completed.
    */
    public short NumOfCyclesCompleted(){
	return this.header.numOfCyclesCompleted;
    }

    /**
       @return The Run to be started after the last cycle of this run.
    */
    public int RunAfterFinished(){
	return this.header.runAfterFinished;
    }

    /**
       @return The total number of monitor counts during acquisition.
    */
    public int TotalMonitorCounts(){
	return this.header.totalMonitorCounts;
    }

    /**
       @return The detector calibration file number used to configure this run.
    */
    public int DetectorCalibFile(){
	return this.header.detCalibFile;
    }

    /**
       @return The unit for referencing the detector location.
    */
    public char DetLocUnit(){
	return this.header.detLocUnit;
    }

    /**
       @return The Pseudo-time unit used to focus detectors.
    */
    public char PseudoTimeUnit(){
	return this.header.pseudoTimeUnit;
    }

    /**
       @return The source to sample distance
    */
    public double SourceToSample(){
	return this.header.sourceToSample;
    }

    /**
       Calculates source to sample time for the nominal energy of neutron.
       This routine is only meaningful for chopper instruments since the
       routine requires a nominal energy to be given in runfile.
       @return The source to sample distance
    */
    public double SourceToSampleTime(){
	double timeToSample = 
	    SourceToSample() / Math.sqrt(EnergyIn()/MEV_FROM_VEL);
	return timeToSample;
    }

    /**
       Calculates source to sample time for a given energy of neutron.
       @param energy - energy value for the calculation
       @return The source to sample distance
    */
    public double SourceToSampleTime(double energy){
	double timeToSample = 
	    SourceToSample() / Math.sqrt(energy/MEV_FROM_VEL);
	return timeToSample;
    }

    /**
       @return The source to chopper distance
    */
    public double SourceToChopper(){
	return this.header.sourceToChopper;
    }

    /**
       @return The moderator calibration file number used to set up this run
    */
    public int ModeratorCalibFile(){
	return this.header.moderatorCalibFile;
    }

    /**
       @return The group to monitor for runtime sequencing
    */
    public short GroupToMonitor(){
	return this.header.groupToMonitor;
    }

    /**
       @return The channel to monitor for runtime sequencing.
    */
    public short ChannelToMonitor(){
	return this.header.channelToMonitor;
    }

    /**
       @return The number of histograms in the run.
    */
    public short NumOfHistograms(){
	return this.header.numOfHistograms;
    }

    /**
       @return The number of time fields in the time field table.
    */
    public short NumOfTimeFields(){
	return this.header.numOfTimeFields;
    }

    /**
       @return The number of control parameters in the control table.
    */
    public short NumOfControl(){
	return this.header.numOfControl;
    }

    /**
       @return The control flag word.
    */
    public short ControlFlag(){
	return this.header.controlFlag;
    }

    /**
       @return The clock shift needed.
    */
    public short ClockShift(){
	return this.header.clockShift;
    }

    /**
       @return The total number of data channels in the runfile.
    */
    public int TotalChannels(){
	return this.header.totalChannels;
    }

    /**
       @return The number of accelerator pulses for this run.
    */
    public int NumOfPulses(){
	return this.header.numOfPulses;
    }

    /**
       @return The size of the data area.
    */
    public int SizeOfDataArea(){
	return this.header.sizeOfDataArea;
    }

    /**
       @return The minimum time for processing data in the hardware.
    */
    public int HardwareTMin(){
	return this.header.hardwareTMin;
    }

    /**
       @return The maximum time for processing data in the hardware.
    */
    public int HardwareTMax(){
	return this.header.hardwareTMax;
    }

    /**
       @return The hardware time delay.
    */
    public int HardTimeDelay(){
	return this.header.hardTimeDelay;
    }

    /**
       The number of y channels in and area detector.
       @return The number of x channels in and area detector.
    */
    public short NumOfX(){
	return this.header.numOfX;
    }

    /**
       The number of y channels in and area detector.
       @return The number of y channels in and area detector.
    */
    public short NumOfY(){
	return this.header.numOfY;
    }

    /**
       The number of wavelengths binned for an area detector.
       @return The number of wavelength channels for an area detector.
    */
    public short NumOfWavelengths(){
	return this.header.numOfWavelengths;
    }

    /**
       The minimum wavelength binned for an area detector.
       @return The minimum wavelength for an area detector.
    */
    public int MinWavelength(){
	return this.header.minWavelength;
    }

    /**
       The maximum wavelength binned for an area detector.
       @return The maximum wavelength for an area detector.
    */
    public int MaxWavelength(){
	return this.header.maxWavelength;
    }

    /**
       The angle for placement of an area detector.
       @return The angle.
    */
    public double Dta(){
	return this.header.dta;
    }

    /**
       Source to detector distance for an area detector
       @return The dtd.
    */
    public double Dtd(){
	return this.header.dtd;
    }

    /**
       @return The Omega.
    */
    public double Omega(){
	return this.header.omega;
    }

    /**
       @return The chi angle for SCD.
    */
    public double Chi(){
	return this.header.chi;
    }

    /**
       @return The phi angle for SCD.
    */
    public double Phi(){
	return this.header.phi;
    }

    /**
       @return The distance from detector center to the left of an area 
       detector.
    */
    public double XLeft(){
	return this.header.xLeft;
    }

    /**
       @return The distance from detector center to the right of an area 
       detector.
    */
    public double XRight(){
	return this.header.xRight;
    }

    /**
       @return The distance from detector center to the bottom of an area 
       detector.
    */
    public double YLower(){
	return this.header.yLower;
    }

    /**
       @return The distance from detector center to the top of an area 
       detector.
    */
    public double YUpper(){
	return this.header.yUpper;
    }

    /**
       @return The horizontal displacement of an area detector from beam 
       center.
    */
    public double XDisplacement(){
	return this.header.xDisplacement;
    }

    /**
       @return The vertical displacement of an area detector from beam center.
    */
    public double YDisplacement(){
	return this.header.yDisplacement;
    }

    /**
       @return The time channel width for area detectors.
    */
    public short AreaChannelWidth(){
	return this.header.areaChannelWidth;
    }

    /**
       @return The time channel doubling width for area detetors.
    */
    public short AreaDoubleInterval(){
	return this.header.areaDoubleInterval;
    }

    /**
       @return The clock period for area detector data acquisition system
    */
    public double AreaClock(){
	return this.header.clockPeriod;
    }

    /**
       @return The incident energy for a chopper spectrometer.
    */
    public double EnergyIn(){
	return this.header.energyIn;
    }

    /**
       @return The scatered energy for inverse geometry machine.
    */
    public double EnergyOut(){
	return this.header.energyOut;
    }

    /**
       @return The number of sequential histograms.
    */
    public short NumOfSeqHist(){
	return this.header.numOfSeqHist;
    }

    /**
       @return The proton current.
    */
    public double ProtonCurrent(){
	return this.header.protonCurrent;
    }

    /**
       @return The area binning type
    */
    public short AreaBinning(){
	return this.header.areaBinning;
    }

    /**
       @return The number of times the histogramming proccessor locked out.  
       This is and indication of possible data loss.
    */
    public short NumOfLockouts(){
	return this.header.numOfLockouts;
    }

    /**
       @return The experiment number for a multirun experiment.
    */
    public int ExpNum(){
	return this.header.expNum;
    }

    /**
       @return The first run in a multirun experiment
    */
    public int FirstRun(){
	return this.header.firstRun;
    }

    /**
       @return The first run in a multirun experiment
    */
    public int LastRun(){
	return this.header.lastRun;
    }

    /**
       @return The default run number.  Time fields and grouping info for 
       this run were copied from the default run.
    */
    public int DefaultRun(){
	return this.header.defaultRun;
    }

    /**
       @return The sample position for a multiple sample well instrument.
    */
    public short SamplePosition(){
	return this.header.samplePos;
    }

    /**
       @return The number of blocks in the runfile header.
    */
    public int NumOfHeadBlocks(){
	return this.header.numOfHeadBlocks;
    }

    /**
       @return The clock period for standard detectors.
    */
    public double StandardClock(){
	return this.header.standardClock;
    }

    /**
       @return The clock period for LPSD detectors.
    */
    public double LpsdClock(){
	return this.header.lpsdClock;
    }


    /**
       This method retrieves the effective scattering angle for a given 
       segment.  For a detector that is not time foused this routine 
       retrieves the actual angle.  For a time focuesed detector this 
       retrieves the average angle of the focused group of detectors.
       @param seg The detector segment.
       @return The scattering angle.
    */
    public double DetectorAngle(Segment seg, int hist ){
	int detID = seg.detID;
	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {

	    double dAngle;
	    double angle_sum= 0.0, angleSign;
	    int nDetsThisType = 0;
	    int index = (hist - 1) * header.nDet + detID;
	    int tfType = detectorMap[index].tfType;
	    
	    if (tfType == 0) return detectorAngle[detID];

	    if ((timeField[tfType].timeFocusBit > 0)) { 
		if (header.pseudoTimeUnit == 'D') { 
		    for ( int nid = 1; nid <= header.nDet; nid++ ) {
			int nindex = (hist - 1) * header.nDet + nid;
			int tempType = detectorMap[nindex].tfType;
			
			if ( tempType == tfType )
			    {
				angle_sum += Math.abs(detectorAngle[nid]);
				nDetsThisType++;
			    }
		    }
		}
		else if (header.pseudoTimeUnit == 'I') { 
		    for ( int nid = 1; nid <= header.nDet; nid++ ) {
			int nindex = (hist - 1) * header.nDet + nid;
			int tempType = detectorMap[nindex].tfType;
			
			if ( tempType == tfType && 
			     subgroupIDList[index] == 
			     subgroupIDList[nindex] )
			    {
				angle_sum += Math.abs(detectorAngle[nid]);
				nDetsThisType++;
			    }
		    }
		}
		angleSign = detectorAngle[detID]/
		    Math.abs(detectorAngle[detID]);
		dAngle = angle_sum / nDetsThisType * angleSign;
		
	    }
	    else {
		if ((numSegs1[detID] == 1) && (numSegs2[detID] == 1)) {
		    dAngle = this.detectorAngle[detID];
		}
		else{
		    dAngle = RawDetectorAngle(seg);
		}		    		    
		
	    }
	    return dAngle;
	}
	else {
	    return RawDetectorAngle(seg);
	}
    }

    /**
       This method retrieves the effective scattering angle for a given 
       detector.  For a detector that is not time foused this routine 
       retrieves the actual angle.  For a time focuesed detector this 
       retrieves the average angle of the focused group of detectors.
       @deprecated Should be using @link #DetectorAngle( Segment, int )
       @param detID The detector ID.
       @return The scattering angle.
    */
    public double DetectorAngle(int detID, int hist ){
	double dAngle;
	double angle_sum= 0.0, angleSign;
	int nDetsThisType = 0;
	int index = (hist - 1) * header.nDet + detID;
	int tfType = detectorMap[index].tfType;

	if (tfType == 0) return detectorAngle[detID];

	if ((timeField[tfType].timeFocusBit > 0)) { 
	    if (header.pseudoTimeUnit == 'D') { 
		for ( int nid = 1; nid <= header.nDet; nid++ ) {
		    int nindex = (hist - 1) * header.nDet + nid;
		    int tempType = detectorMap[nindex].tfType;
		    
		    if ( tempType == tfType )
			{
			    angle_sum += Math.abs(detectorAngle[nid]);
			    nDetsThisType++;
			}
		}
	    }
	    else if (header.pseudoTimeUnit == 'I') { 
		for ( int nid = 1; nid <= header.nDet; nid++ ) {
		    int nindex = (hist - 1) * header.nDet + nid;
		    int tempType = detectorMap[nindex].tfType;

		    //		    System.out.println( "Sizeof subgroupIDList = " + 
		    //		subgroupIDList.length );
		    if ( tempType == tfType && 
			 subgroupIDList[index] == 
			 subgroupIDList[nindex] )
			{
			    angle_sum += Math.abs(detectorAngle[nid]);
			    nDetsThisType++;
			}
		}
	    }
	    angleSign = detectorAngle[detID]/
		Math.abs(detectorAngle[detID]);
	    dAngle = angle_sum / nDetsThisType * angleSign;
	    
	}
	else {
	    dAngle = this.detectorAngle[detID];
	}
	return dAngle;
    }

    /**
       This method retrieves the actual scattering angle for a given detector. 
       @param detID The detector ID.
       @return The scattering angle.
    */
    public double RawDetectorAngle(int detID){
	return this.detectorAngle[detID];
    }

    /**
       This method retrieves the actual scattering angle for a given detector
       segment. 
       @param seg The detector segment.
       @return The scattering angle.
    */
    public double RawDetectorAngle(Segment seg){
	int detID = seg.detID;
	if ( !((psdOrder[detID]  == 2) && (header.versionNumber < 5 )) ) {
	    if ((numSegs1[detID] == 1) && (numSegs2[detID] == 1)) {
		return this.detectorAngle[seg.detID];
	    }
	    else{
		float relX = detectorWidth[detID]/200.0f -
		    ((float)(seg.column-0.5f )/numSegs2[detID]) * 
		    detectorWidth[detID]/100.0f;
		float relY = -detectorLength[detID]/200.0f +
		    ((float)(seg.row -0.5f)/numSegs1[detID]) * 
		    detectorLength[detID]/100.0f;
		     
		float rotCos2 = 
		    (float) Math.cos(Math.toRadians((double)detectorRot2[detID]));
		float rotSin2 = 
		    (float) Math.sin(Math.toRadians(detectorRot2[detID]));
		
		float rotX2 = relX * rotCos2 + relY * rotSin2;
		float rotY2 = relY * rotCos2 - relX * rotSin2;

		/*		float psdFromCenter = (float)(detectorHeight[detID] - 
		    (detectorLength[detID]/200.0f) * rotSin +
		    (((float)seg.row / (float) numSegs1[detID]) * 
		     detectorLength[detID]/100.0f) * rotSin);
		float psdFromCenter2 = psdFromCenter - detectorHeight[detID];

		float psdHeight = detectorHeight[detID] - 
		    (detectorLength[detID]/200.0f) * rotCos +
		    (((float)seg.row / (float)numSegs1[detID]) * 
		     detectorLength[detID]/100.0f) * rotCos;
		float psdHeight2 = psdHeight - detectorHeight[detID];

		*/
		float psdFlightPath = 
		    (float)Math.sqrt((flightPath[detID]*flightPath[detID])+ 
				     (rotX2 * rotX2));

		float psdAngle = 
		    (float)(Math.asin( (double)(rotX2)/
				       (double)psdFlightPath ) * 180.0/Math.PI+
			    detectorAngle[detID]);

		return psdAngle;
	    }		    		    
	}
	else {
	    double fromLeft = 
		((seg.column + .5) * ( -detectorWidth[detID])/100.0f ) 
		/ numSegs2[detID];
	    double fromCenter = fromLeft 
		 - ( header.xDisplacement + header.xLeft )/100.0f;
	    double offsetAngle = Math.atan(fromCenter / (header.dtd/100)) 
		* ( 180 / Math.PI );
	    return header.dta + offsetAngle;
	    
	}
    }

    /**
       This method retrieves the effective flight path length for a given 
       detector segment.  For a detector that is not time foused this routine 
       retrieves the actual length.  For a time focuesed detector this 
       retrieves the focused flight path length.
       @param seg The segment.
       @return The focused flight path length if pseudoUnit = I.
       The raw flight path length otherwise.
    */
    public double FlightPath(Segment seg, int hist){
	double fp;
	int detID = seg.detID;
	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
	    int index = (hist - 1) * header.nDet + detID;
	    int tfType = detectorMap[index].tfType;
	    
	    if (tfType == 0) return flightPath[detID];
	    
	    if ((timeField[tfType].timeFocusBit > 0) && 
		(header.pseudoTimeUnit == 'I')) { 
		if (flightPath[detID] > 3.0 ) {
		    fp = 4.0;
		    float twoDCorr = 1.00f;
		    if ( numSegs1[detID] > 1 ) {
			if (header.iName.equalsIgnoreCase("hrcs") && 
			    header.runNum < 3384 ){
			    twoDCorr = (float)
				RawFlightPath(seg)/(float)
				Math.sqrt(Math.pow(flightPath[detID], 2.0) +
					  Math.pow(detectorLength[detID]/200.0, 2.0));
			}
			else {

			    twoDCorr = (float)RawFlightPath(seg)/(float)flightPath[detID];
			}
		    }
		    fp = fp * twoDCorr;

		}
		else {
		    fp = 2.5;
		}
	    }
	    else {
		if ((numSegs1[detID] == 1) && (numSegs2[detID] == 1)) {
		    fp = flightPath[detID];
		}
		else {
		    fp = RawFlightPath(seg);
		}
	    }
	    return fp;
	}
	else {
	    return RawFlightPath(seg);
	}
    }

    /**
       This method retrieves the effective flight path length for a given 
       detector.  For a detector that is not time foused this routine 
       retrieves the actual length.  For a time focuesed detector this 
       retrieves the focused flight path length.
       @deprecated Should be using @link #FlightPath( Segment, int )
       @param detID The detector ID.
       @return The focused flight path length if pseudoUnit = I.
       The raw flight path length otherwise.
    */
    public double FlightPath(int detID, int hist){
	double fp;
	int index = (hist - 1) * header.nDet + detID;
	int tfType = detectorMap[index].tfType;

	if (tfType == 0) return flightPath[detID];

	if ((timeField[tfType].timeFocusBit > 0) && 
	    (header.pseudoTimeUnit == 'I')) { 
	    if (flightPath[detID] > 3.0 ) {
		fp = 4.0;
	    }
	    else {
		fp = 2.5;
	    }
	}
	else {
	    fp = flightPath[detID];	
	}
 
	    return fp;
    }

    /**
       This method retrieves the actual flight path length for a given 
       detector segment.  
       @param seg The segment.
       @return The flight path length.
    */
    public double RawFlightPath(Segment seg){
	int id = seg.detID;
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    if ((numSegs1[id] ==1) && (numSegs2[id] == 1)) {
		return this.flightPath[id];
	    }
	    else {
		float relX = -detectorWidth[id]/200.0f +
		    ((float)(seg.column - .5f)/numSegs2[id]) * detectorWidth[id]/100.0f;
		float relY = -detectorLength[id]/200.0f +
		    ((float)(seg.row - .5f)/numSegs1[id]) * detectorLength[id]/100.0f;
		
		float rotCos2 = 
		    (float) Math.cos(Math.toRadians((double)detectorRot2[id]));
		float rotSin2 = 
		    (float) Math.sin(Math.toRadians(detectorRot2[id]));
		
		float rotX2 = relX * rotCos2 + relY * rotSin2;
		float rotY2 = relY * rotCos2 - relX * rotSin2;
		float psdHeight2 = rotY2 + detectorHeight[id];
		float psdFlightPath = 
		    (float)Math.sqrt((float)(rotY2 * rotY2) + 
				     (rotX2 * rotX2) +
				     (flightPath[id]*flightPath[id]));
		float fp = psdFlightPath;
		return (fp);
	    }
	}
	else {
	    float fromLeft = (float)
		((seg.column+.5) * ( -detectorWidth[id])/100.0f ) 
		/ numSegs2[id];
	    float fromCenter = (float)(fromLeft 
		 - ( header.xDisplacement + header.xLeft )/100.0f);
	    
	    return Math.sqrt(flightPath[id] * flightPath[id]
			     + RawDetectorHeight(seg)*RawDetectorHeight (seg)
			     + fromCenter * fromCenter );
	} 
    }

    /**
       This method retrieves the actual flight path length for a given 
       detector.  
       @param detID The detector ID.
       @return The flight path length.
    */
    public double RawFlightPath(int detID){
	return this.flightPath[detID];
    }

    /**
       This method retrieves the height of the detector above the scattering 
       plane.  
       @deprecated Should be using @link #DetectorHeight( Segment, int )
       @param detID The detector ID.
       @return The height of the detector above the scattering plane.
    */
    public double DetectorHeight(int detID){
	return this.detectorHeight[detID];
    }

    /**
       This method retrieves the height of the segment above the scattering 
       plane.  
       @param seg The detector segment.
       @return The height of the detector above the scattering plane.
    */
    public double DetectorHeight(Segment seg, int hist){
	int id = seg.detID;
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    if ( (numSegs1[id] == 1) && (numSegs2[id] == 1)) {
		return this.detectorHeight[id];
	    }
	    else {
		return RawDetectorHeight(seg);
	    }
	}
	else {
	    return RawDetectorHeight(seg);
	}
    }

    /**
       This method retrieves the height of the detector above the scattering 
       plane.  
       @param detID The detector ID.
       @return The height of the detector above the scattering plane.
    */
    public double RawDetectorHeight(int detID){
	return this.detectorHeight[detID];
    }

    /**
       This method retrieves the height of the segment above the scattering 
       plane.  
       @param seg The detector segment.
       @return The height of the detector above the scattering plane.
    */
    public double RawDetectorHeight(Segment seg){
	int id = seg.detID;
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    if ( (numSegs1[id] == 1) && (numSegs2[id] == 1)) {
		return this.detectorHeight[seg.detID];
	    }
	    else {
		float relX = -detectorWidth[id]/200.0f +
		    ((float)(seg.column - .5f)/numSegs2[id]) * 
		    detectorWidth[id]/100.0f;
		float relY = -detectorLength[id]/200.0f +
		    ((float)(seg.row - .5f)/numSegs1[id]) * 
		    detectorLength[id]/100.0f;
		     
		float rotCos2 = 
		    (float) Math.cos(Math.toRadians((double)detectorRot2[id]));
		float rotSin2 = 
		    (float) Math.sin(Math.toRadians(detectorRot2[id]));
		
		float rotX2 = relX * rotCos2 + relY * rotSin2;
		float rotY2 = relY * rotCos2 - relX * rotSin2;
		float psdHeight2 = rotY2 + detectorHeight[id];
		return psdHeight2;
	    }
	}
	else {
	    double height = header.yDisplacement + header.yLower + 
		((seg.row+.5) * (detectorLength[id])) / numSegs1[id];
	    return height/100.0f;
	}
    }

  /**
     This method retrieves the detector length
     @param detID The detector ID
     @return The detector length
   */
  public float DetectorLength(int detID) {
    return detectorLength[detID];
  }

  /**
     This method retrieves the detector width
     @param detID The detector ID
     @return The detector width
   */
  public float DetectorWidth(int detID) {
    return detectorWidth[detID];
  }

  /**
     This method retrieves the detector depth
     @param detID The detector ID
     @return The detector depth
   */
  public float DetectorDepth(int detID) {
    return detectorDepth[detID];
  }

    /**
       This method retrieves the detector type.  
       @param detID The detector ID.
       @return The detector type.
    */
    public short DetectorType(int detID){
	return this.detectorType[detID];
    }

    /**
       This method retrieves the detector type.  
       @param seg The detector segment.
       @return The detector type.
    */
    public short DetectorType(Segment seg){
	return this.detectorType[seg.detID];
    }

    /**
       This method retrieves the detector data Source.  
       @param detID The detector ID.
       @return The detector dataSource.
     */
    public int DataSource( int detID ) {
	return this.dataSource[detID];
    }

    /**
       This method retrieves the detector data Source.  
       @param seg The detector segment.
       @return The detector dataSource.
     */
    public int DataSource( Segment seg ) {
	return this.dataSource[seg.detID];
    }

    /**
       This method retrieves the detector minimum ID.  
       @param detID The detector ID.
       @return The detector minimum ID.
     */
    public int MinID( int detID ) {
	return this.minID[detID];
    }

    /**
       This method retrieves the detector minimum ID.  
       @param seg The detector segment.
       @return The detector minimum ID.
     */
    public int MinID( Segment seg ) {
	return this.minID[seg.detID];
    }

    /**
       This method retrieves the PsdOrder for an ID.  
       @param detID The detector ID.
       @return The psd order.
     */
    public int PsdOrder( int detID ) {
	return this.psdOrder[detID];
    }

    /**
       This method retrieves the number of segments in the first dimension 
       for an ID.  
       @param detID The detector ID.
       @return The number of segments in the first dimension.
     */
    public int NumSegs1( int detID ) {
	return this.numSegs1[detID];
    }

    /**
       This method retrieves the number of segments in the first dimension 
       in the detector associated with seg.  
       @param seg The detector segment.
       @return The number of segments in the first dimension for this detector.
     */
    public int NumSegs1( Segment seg ) {
	return this.numSegs1[seg.detID];
    }

    /**
       This method retrieves the number of segments in the second dimension 
       for an ID.  
       @param detID The detector ID.
       @return The number of segments in the second dimension.
     */
    public int NumSegs2( int detID ) {
	return this.numSegs2[detID];
    }

    /**
       This method retrieves the number of segments in the second dimension 
       in the detector associated with seg.  
       @param seg The detector segment.
       @return The number of segments in the second dimension for this 
       detector.
     */
    public int NumSegs2( Segment seg ) {
	return this.numSegs2[seg.detID];
    }

    /**
       This method retrieves the rotation angle of the detector into the 
       scattering plane for an ID.  
       @param detID The detector ID.
       @return The described rotation for this detector.
     */
    public float DetRot1( int detID ) {
	return this.detectorRot1[detID];
    }

    /**
       This method retrieves the rotation angle of the detector into the 
       scattering plane for the detector associated with seg.  
       @param seg The detector segment.
       @return The described rotation for this detector.
     */
    public float DetRot1( Segment seg ) {
	return this.detectorRot1[seg.detID];
    }

    /**
       This method retrieves the rotation angle of the detector about its 
       center in a plane perpendicular to a vector pointing at the sample 
       for an ID.  
       @param detID The detector ID.
       @return The described rotation for this detector.
     */
    public float DetRot2( int detID ) {
	return this.detectorRot2[detID];
    }

    /**
       This method retrieves the rotation angle of the detector about its 
       center in a plane perpendicular to a vector pointing at the sample for 
       the detector associated with seg.  
       @param seg The detector segment.
       @return The described rotation for this detector.
     */
    public float DetRot2( Segment seg ) {
	return this.detectorRot2[seg.detID];
    }

    /**
       Retrieves the spectrum of a 1D detector.  This method opens and closes 
       the file on each call.
       @param subgroup Subgroup ID to be retrieved.
       @return The retrieved spectrum.
    */
    public float[] Get1DSpectrum(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return null;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	
	return Get1DSpectrum(seg, hist);
    }

    /**
       Retrieves the spectrum of a detector segment.  This method opens and 
       closes the file on each call.
       @param seg Segment to be retrieved.
       @param hist Histogram number for data to retrieved.
       @return The retrieved spectrum.
    */
    public float[] Get1DSpectrum(Segment seg, int hist) throws IOException {
	int numOfTimeChannels;
	int i;
	float[] data;
	byte[] bdata;
	int tfType;
	int index, offset;
	int wordLength;

	if ( leaveOpen == false){
	    System.out.println("GetSpectrum1D opening file");
	    runfile = new RandomAccessRunfile(runfileName, "r");
	}
	int detID = seg.detID;
	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
	    index = seg.segID + (hist-1) * this.header.nDet;
	    if (detectorMap[index].tfType == 0 ) {
		System.out.println( "invalid id in Get1DSpectrum(id,hist), " +
				    "returning null");
		return null;
	    }
	
	    tfType = this.detectorMap[index].tfType;
	    numOfTimeChannels = this.timeField[tfType].NumOfChannels();
	    data = new float[numOfTimeChannels];
	    if (header.numOfWavelengths > 0) {
		offset = this.detectorMap[index].address 
		    + this.header.totalChannels * 2 
		    + this.header.histStartAddress + 4;
	    }
	    else {
		offset = this.detectorMap[index].address + 
		    this.header.histStartAddress + 4;
	    }
	    
	    runfile.seek(offset);
	    if (numOfTimeChannels !=0){
		
		if (this.header.versionNumber < 4){
		    wordLength = 2;
		}
		else{
		    wordLength = 4;
		}
		if ( this.header.versionNumber < 5 ) {
		    bdata = new byte[numOfTimeChannels * wordLength];
		    int nbytes = runfile.read( bdata, 0, 
					       numOfTimeChannels * wordLength);
		for (i = 0; i < numOfTimeChannels; i++){
		    for (int j = 0; j < wordLength; j++) {
			int byteIndex = i * wordLength + j;
			if ( bdata[byteIndex] < 0 ) {
			    data[i] += (bdata[byteIndex] + 256) * 
				Math.pow(256.0, j);
			}
			else {
			    data[i] += bdata[byteIndex] * Math.pow(256.0, j);
			}
		    }
		}
		}
	    else {
		byte[] bArray = new byte[ 4 * numOfTimeChannels ];
		runfile.read( bArray );
		ByteArrayInputStream bArrayIS = 
		    new ByteArrayInputStream( bArray );
		DataInputStream dataStream = new DataInputStream( bArrayIS );
		for ( i = 0; i < numOfTimeChannels; i++ ) {
		    data[i] = dataStream.readInt();
		}
		bArrayIS.close();
		dataStream.close();
	    }
	}
	if (!leaveOpen ){
	    runfile.close();
	}
	return data; 
	}
	else {                        // Area detector data
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    int numWaves = header.numOfWavelengths;
	    float stepWave = (maxWave - minWave)/numWaves;
	    int areaStartAddress;
	    int sliceInterval;
	    int aindex;

	    areaStartAddress = header.histStartAddress;
	    sliceInterval = (header.totalChannels*2)/(numWaves);
	    aindex = seg.column  + (seg.row -1) * header.numOfX;

	    int ioffset = areaStartAddress  + 2 + aindex*2;
	    data = new float[numWaves];
	    bdata = new byte[2];
	    int nbytes;
	    int xx;
	    int minSearchChan=1;
	    int maxSearchChan=header.numOfOverflows;
	    for ( int ii = 0; ii < numWaves; ii++ ) {
    		runfile.seek( ioffset + ii * sliceInterval );
		int ovOffset = ioffset + ii * sliceInterval - header.histStartAddress;
		nbytes = runfile.read ( bdata, 0, 2 );
		if (bdata[0] < 0 ) {
		    data[ii] += 
			(bdata[0] + 256);
		    
		}
		else {
		    data[ii] += bdata[0];
		}
		if (bdata[1] < 0 ) {
		    data[ii] += 
       			(bdata[1] + 256) * 256;
		    
		}
		else {
		    data[ii] += bdata[1] * 256;
		}
		if (header.numOfOverflows > 0) {
		  boolean sdone = false;
		  for(int jj = minSearchChan; jj<=maxSearchChan&&!sdone; jj++) {
		    if ( ovOffset == overflows[jj] ) {
							       
		      data[ii] = data[ii] + 65536;
		    }
		    else if( ovOffset < overflows[jj] ) {
		      sdone = true;
		      minSearchChan = jj;
		    }
		  }
		}
	    }
	if (!leaveOpen ){
	    runfile.close();
	}
	    return data;
	}
    }
    

    /**
       Retrieves the spectrum of a 1D detector.  This method opens and closes 
       the file on each call.
       @deprecated Should be using @link #Get1DSpectrum( Segment, int )
       @param detID Detector ID to be retrieved.
       @param hist Histogram number for data to retrieved.
       @return The retrieved spectrum.
    */
    public float[] Get1DSpectrum(int detID, int hist) throws IOException{
	int numOfTimeChannels;
	int i;
	float[] data;
	byte[] bdata;
	int tfType;
	int index, offset;
	int wordLength;

	if ( leaveOpen == false){
	    System.out.println("GetSpectrum1D opening file");
	    runfile = new RandomAccessRunfile(runfileName, "r");
	}

	index = detID + (hist-1) * this.header.nDet;
	if (detectorMap[index].tfType == 0 ) {
	    System.out.println( "invalid id in Get1DSpectrum(id,hist), " +
				"returning null");
	    return null;
	}
	
	tfType = this.detectorMap[index].tfType;
	numOfTimeChannels = this.timeField[tfType].NumOfChannels();
	data = new float[numOfTimeChannels];
	if (header.numOfWavelengths > 0) {
	    offset = this.detectorMap[index].address 
		+ this.header.totalChannels * 2 
		+ this.header.histStartAddress + 4;
	}
	else {
	    offset = this.detectorMap[index].address + 
		this.header.histStartAddress + 4;
	}
	
	runfile.seek(offset);
	if (numOfTimeChannels !=0){
	    
	    if (this.header.versionNumber < 4){
		wordLength = 2;
	    }
	    else{
		wordLength = 4;
	    }
	    if ( this.header.versionNumber < 5 ) {
		bdata = new byte[numOfTimeChannels * wordLength];
		int nbytes = runfile.read( bdata, 0, 
					   numOfTimeChannels * wordLength);
		for (i = 0; i < numOfTimeChannels; i++){
		    for (int j = 0; j < wordLength; j++) {
			int byteIndex = i * wordLength + j;
			if ( bdata[byteIndex] < 0 ) {
			    data[i] += (bdata[byteIndex] + 256) * 
				Math.pow(256.0, j);
			}
			else {
			    data[i] += bdata[byteIndex] * Math.pow(256.0, j);
			}
		    }
		}
	    }
	    else {
		byte[] bArray = new byte[ 4 * numOfTimeChannels ];
		runfile.read( bArray );
		ByteArrayInputStream bArrayIS = 
		    new ByteArrayInputStream( bArray );
		DataInputStream dataStream = new DataInputStream( bArrayIS );
		for ( i = 0; i < numOfTimeChannels; i++ ) {
		    data[i] = dataStream.readInt();
		}
		bArrayIS.close();
		dataStream.close();
	    }
	}
	if (!leaveOpen ){
	    runfile.close();
	}
	return data; 
    }

    /**
       Retrieves the sum of counts in a 1D detector.  This method opens and 
       closes the file on each call.
       @param subgroup Subgroup ID to be retrieved.
       @return The retrieved spectrum.
    */
    public float Get1DSum(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return -1;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return Get1DSum(seg, hist);
    }

    /**
       Retrieves the sum of counts in a 1D detector.  This method opens and 
       closes the file on each call.
       @param seg segment to be retrieved.
       @param hist Histogram number for data to retrieved.
       @return The sum.
    */
    public float Get1DSum(Segment seg, int hist) throws IOException{
	int numOfTimeChannels;
	int i;
	float data;
	byte[] bdata;
	int tfType;
	int index, offset;
	int wordLength;

	if ( leaveOpen == false){
	    runfile = new RandomAccessRunfile(runfileName, "r");
	}
	int detID = seg.detID;
	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
	    index = seg.segID + (hist-1) * this.header.numOfElements;
	    tfType =  this.detectorMap[index].tfType;
	    numOfTimeChannels = this.timeField[tfType].NumOfChannels();
	    data = -1;
	    if (header.numOfWavelengths > 0) {
		offset = this.detectorMap[index].address 
		    + this.header.totalChannels * 2 
		    + this.header.histStartAddress;
	    }
	    else {
		offset = this.detectorMap[index].address + 
		    this.header.histStartAddress;
	    }
	    
	    runfile.seek(offset);
	    if (this.header.versionNumber <= 4) {
		if (numOfTimeChannels !=0){
		    data = 0;
		    wordLength = 4;
		    
		    bdata = new byte[wordLength];
		    int nbytes = runfile.read( bdata, 0, 
					       wordLength);
		    int byteIndex;
		    for (int j = 0; j < wordLength; j++) {
			byteIndex = j;
			if ( bdata[byteIndex] < 0 ) {
			    data  += (bdata[byteIndex] + 256) * 
				Math.pow(256.0, j);
			}
			else {
			    data += bdata[byteIndex] * Math.pow(256.0, j);
			}
		    }
		}
	    }
	    else {
		int idata = runfile.readInt();
		data = (float)idata;
	    }
	    if (!leaveOpen ){
		runfile.close();
	    }
	    return data; 
	}
	else {
	    float[] spect = Get1DSpectrum(seg, hist);
	    float specsum = 0;
	    for ( int jj = 0; jj < spect.length; jj++ ) {
		specsum += spect[jj];
	    }
	    return specsum;
	}
    }

    /**
       Retrieves the sum of counts in a 1D detector.  This method opens and 
       closes the file on each call.
       @deprecated Should be using @link #Get1DSum( Segment, int )
       @param detID Detector ID to be retrieved.
       @param hist Histogram number for data to retrieved.
       @return The sum.
    */
    public float Get1DSum(int detID, int hist) throws IOException{
	int numOfTimeChannels;
	int i;
	float data;
	byte[] bdata;
	int tfType;
	int index, offset;
	int wordLength;

	if ( leaveOpen == false){
	    runfile = new RandomAccessRunfile(runfileName, "r");
	}

	index = detID + (hist-1) * this.header.nDet;
	tfType =  this.detectorMap[index].tfType;
	numOfTimeChannels = this.timeField[tfType].NumOfChannels();
	data = -1;
	if (header.numOfWavelengths > 0) {
	    offset = this.detectorMap[index].address 
		+ this.header.totalChannels * 2 
		+ this.header.histStartAddress;
	}
	else {
	    offset = this.detectorMap[index].address + 
		this.header.histStartAddress;
	}
	
	runfile.seek(offset);
	if (this.header.versionNumber <= 4) {
	    if (numOfTimeChannels !=0){
		data = 0;
		wordLength = 4;
		
		bdata = new byte[wordLength];
		int nbytes = runfile.read( bdata, 0, 
					   wordLength);
		for (int j = 0; j < wordLength; j++) {
		    int byteIndex = j;
		    if ( bdata[byteIndex] < 0 ) {
			data  += (bdata[byteIndex] + 256) * 
			    Math.pow(256.0, j);
		    }
		    else {
			data += bdata[byteIndex] * Math.pow(256.0, j);
		    }
		}
	    }
	}
	else {
	    int idata = runfile.readInt();
	    data = (float)idata;
	}
	if (!leaveOpen ){
	    runfile.close();
	}
	return data; 
    }

    /**
       Check to see if a detector is binned in the specified histogram.
       @param id - Detector to be checked.
       @param hist - Histogram to be checked.
       @return true if binned, false if not binned.
    */

    public boolean IsBinned( int id, int hist ) {
	if ( id > header.nDet || hist > header.numOfHistograms ) return false;

	int index = (hist - 1) * header.nDet + id;
	if ( detectorMap[index].address > 0 ) return true;
	if ( detectorMap[index].address == 0 && detectorMap[index].tfType > 0 )
	    return true;

	return false;
    }

    /**
       Get number of data channels for a given detector.
       @param subgroup Subgroup ID to be retrieved.
       @return Number of channels binned.
    */
    public int NumChannelsBinned(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.numOfElements) 
	    return -1;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	
	return NumChannelsBinned(seg, hist);
    }

    /**
       Get number of data channels for a given detector.
       @deprecated Should be using @link #NumChannelsBinned( Segment, int )
       @param seg - segment to be retrieved.
       @param hist - Histogram number.
       @return Number of channels binned.
    */
    public int NumChannelsBinned( Segment seg, int hist )  {
	int id = seg.detID;
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.numOfElements + seg.segID;
	    int tfType = detectorMap[index].tfType;
	    int nch = (int)(timeField[tfType].NumOfChannels());
	    return nch;
	}
	else {
	    return header.numOfWavelengths;
	}

    }

    /**
       Get number of data channels for a given detector.
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Number of channels binned.
    */
    public int NumChannelsBinned( int id, int hist )  {
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.nDet + id;
	    int tfType = detectorMap[index].tfType;
	    int nch = (int)(timeField[tfType].NumOfChannels());
	    return nch;
	}
	else {
	    return header.numOfWavelengths;
	}

    }

    /**
       Minimum Binned time in time field table.
       @param subgroup Subgroup ID to be retrieved.
       @return Raw minumum time in the time field table.
    */
    public double MinBinned(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.numOfElements) 
	    return -9999;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return MinBinned(seg, hist);
    }

    /**
       Minimum Binned time in time field table.
       @param seg - Detector segment.
       @param hist - Histogram number.
       @return Raw minumum time in the time field table.
    */
    public double MinBinned( Segment seg, int hist ) {
	int id = seg.detID;
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.numOfElements + seg.segID;
	    int tfType = detectorMap[index].tfType;
	    return timeField[tfType].tMin;
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return minWave;
	}
    }
   
    /**
       Minimum Binned time in time field table.
       @deprecated Should be using @link #MinBinned( Segment, int )
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Raw minumum time in the time field table.
    */
    public double MinBinned( int id, int hist ) {
	int index = ( hist - 1 ) * header.nDet + id;
	int tfType = detectorMap[index].tfType;
	return timeField[tfType].tMin;
    }
   
    /**
       Maximum Binned time in time field table.
       @param subgroup Subgroup ID to be retrieved.
       @return Raw maxumum time in the time field table.
    */
    public double MaxBinned(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.numOfElements) 
	    return -9999.;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	
	return MaxBinned(seg, hist);
    }

    /**
       Maximum Binned time in time field table.
       @param seg - Detector segment.
       @param hist - Histogram number.
       @return Raw maxumum time in the time field table.
    */

    public double MaxBinned( Segment seg, int hist ) {
	int id = seg.detID;
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.numOfElements + seg.segID;
	    int tfType = detectorMap[index].tfType;
	    return timeField[tfType].tMax;
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return maxWave;
	}
    }
   
    /**
       Maximum Binned time in time field table.
       @deprecated Should be using @link #MaxBinned( Segment, int )
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Raw maxumum time in the time field table.
    */

    public double MaxBinned( int id, int hist ) {
	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int index = ( hist - 1 ) * header.nDet + id;
	    int tfType = detectorMap[index].tfType;
	    return timeField[tfType].tMax;
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    return maxWave;
	}
    }
   
    /**
       Trigger to leave the file open for subsequent reads.  This is not 
       required but will speed up file access if a large number of spectra 
       are retrieved.  Care must be used when using this routine.  It is not 
       used to initialize the object or retrieving header information.  It 
       leaves the file open to speed up getting spectra data.  After getting 
       data or between long pauses Close Method should be used.
    */
    public void LeaveOpen() {
	if (leaveOpen == true ) return; 
	try {
	    runfile = new RandomAccessRunfile ( runfileName, "r");
	}
	catch ( IOException e ) {
	    System.out.println("Problem Opening File: " + runfileName );
	    e.printStackTrace();
	}
	leaveOpen = true;
    }
      
    /** Closes files opened with LeaveOpen.
     */
    public void Close() {
	if (leaveOpen == false ) return; 
	try {
	    runfile.close();
	}
	catch ( IOException e ) {
	    System.out.println("Problem Closing File: " + runfileName );
	    e.printStackTrace();
	}
	leaveOpen = false;
    }
      
    /**
       Retrieves the time channel boundaries for a given spectrum.  Note that 
       since this is histogram data, there is one more boundary value than 
       there are elements in a spectrum.
       @param subgroup Subgroup ID to be retrieved.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] TimeChannelBoundaries(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return null;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];

	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return TimeChannelBoundaries(seg, hist);
    }

    /**
       Retrieves the time channel boundaries for a given spectrum.  Note that 
       since this is histogram data, there is one more boundary value than 
       there are elements in a spectrum.
       @param seg - Detector ID.
       @param hist - Histogram number.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] TimeChannelBoundaries(Segment seg, int hist){
	float us_correction;
	int id = seg.detID;
	if ( id > header.numOfElements || 
	     hist > header.numOfHistograms ) return null;
	int index = header.numOfElements * (hist - 1) + seg.segID;

	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    if (detectorMap[index].tfType == 0 ) {
		System.out.println( "invalid id in TimeChannelBoundaries" +
				    "(seg,hist), returning null");
		return null;
	    }
     
	    int tfType = detectorMap[index].tfType;
	    int numberOfChannels = timeField[tfType].NumOfChannels();
	    float[] channel = new float[numberOfChannels + 1];
	    
	    float min = (float)timeField[tfType].tMin;
	    float max = (float)timeField[tfType].tMax;
	    float step = (float)timeField[tfType].tStep;
	    
	    float timeToSample = (float)(header.sourceToSample / 
					 Math.sqrt( header.energyIn/MEV_FROM_VEL));
	    
	    switch (header.pseudoTimeUnit ) {
		
	    case ('I'): {
		if ( timeField[tfType].timeFocusBit == 0 ) {  //Det not focused
		    if ( header.versionNumber < 5 ) {
			us_correction = - (float)(header.hardTimeDelay * 
						  header.standardClock) + 
			    (float)(Math.floor(timeToSample/header.standardClock) *
				    header.standardClock);
		    }
		    else {
			us_correction = 0;
		    }
		}
		else {						//Det focused
		    us_correction = timeToSample;

		}
		min = min + us_correction;
		max = max + us_correction;
		break;
	    }
	    default: {
		min = min;
		max = max;
	    }
	    }
	    if (  timeField[tfType].logBinBit == 0 ) { //  check for const t
		                                       //  binning
		for (int chan = 0; chan <= numberOfChannels; chan++) {
		    channel[chan] = (float)( min + chan * step );
		}
	    }
	    else {       // assume dt/t binning
		channel[0] = min;
		for ( int chan = 1; chan <= numberOfChannels; chan++ ) {
		    float clock = (float)header.standardClock; 
		    channel[chan] = (float)channel[chan-1] * (1.0f + step);
		    float fjunk = channel[chan] / clock;
		    channel[chan] = Math.round( fjunk ) * clock;
		    
		}
	    }
	    return channel;
	}
	else {
	    float minWave = header.minWavelength;
	    float maxWave = header.maxWavelength;
	    int numWaves = header.numOfWavelengths;
	    float stepWave = (maxWave - minWave)/numWaves;

	    float[] channel = new float[header.numOfWavelengths + 1];
		for ( int ii = 0; ii <= header.numOfWavelengths; ii++ ) {
		    channel[ii] = minWave + ii* stepWave;
		}
		//	    return channel;
		return areaStartTime;
	}
    }

    /**
       Retrieves the time channel boundaries for a given spectrum.  Note that 
       since this is histogram data, there is one more boundary value than 
       there are elements in a spectrum.
       @deprecated Should be using @link #TimeChannelBoundaries( Segment, int )
       @param id - Detector ID.
       @param hist - Histogram number.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] TimeChannelBoundaries(int id, int hist){
	float us_correction;
	if ( id > header.numOfElements || hist > header.numOfHistograms ) return null;
	int index = header.nDet * (hist - 1) + id;

	if (detectorMap[index].tfType == 0 ) {
	    System.out.println( "invalid id in TimeChannelBoundaries" +
				"(id,hist), returning null");
	    return null;
	}
	
	int tfType = detectorMap[index].tfType;
	int numberOfChannels = timeField[tfType].NumOfChannels();
	float[] channel = new float[numberOfChannels + 1];
	
	float min = (float)timeField[tfType].tMin;
	float max = (float)timeField[tfType].tMax;
	float step = (float)timeField[tfType].tStep;
	
	float timeToSample = (float)(header.sourceToSample / 
				     Math.sqrt( header.energyIn/MEV_FROM_VEL));
	
	switch (header.pseudoTimeUnit ) {
	    
	case ('I'): {
	    if ( timeField[tfType].timeFocusBit == 0 ) {  //Det not focused
		if ( header.versionNumber < 5 ) {
		    us_correction = - (float)(header.hardTimeDelay * 
					      header.standardClock) + 
			(float)(Math.floor(timeToSample/header.standardClock) *
				header.standardClock);
		}
		else {
		    us_correction = 0;
		}
	    }
	    else {						//Det focused
		us_correction = timeToSample;
	    }
	    min = min + us_correction;
	    max = max + us_correction;
	    break;
	}
	default: {
	    min = min;
	    max = max;
	}
	}
	for (int chan = 0; chan <= numberOfChannels; chan++) {
	    channel[chan] = (float)( min + chan * step );
	}
	return channel;
    }


    /**
       Retrieves the energy channel boundaries for a given spectrum.  Note 
       that since this is histogram data, there is one more boundary value 
       than there are elements in a spectrum.
       @param subgroup Subgroup ID to be retrieved.
       @return Array of boundaries with 1 + number of channels values.
    */
    public float[] EnergyChannelBoundaries(int subgroup) throws IOException{
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return null;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return EnergyChannelBoundaries(seg, hist);
    }

    /**
       Retrieves the energy channel boundaries for a given segment.  Note 
       that since this is histogram data, there is one more boundary value 
       than there are elements in a spectrum.
       @param seg - Detector segment.
       @param hist - Histogram number.
       @return Array of boundary values with 1 + number of channels values.
    */
    public float[] EnergyChannelBoundaries( Segment seg, int hist ) {
	float[] timeBounds;
	timeBounds = TimeChannelBoundaries( seg, hist );
	float[] energyChannels = new float[timeBounds.length];
	float timeToSample = (float)(header.sourceToSample / 
				     Math.sqrt( header.energyIn/MEV_FROM_VEL));
   
	for (int i = 0; i < timeBounds.length; i++ ) {
	    energyChannels[i] = (float)(header.energyIn - MEV_FROM_VEL * 
					Math.pow(FlightPath(seg, hist) 
				     / (timeBounds[i] - timeToSample), 2.0)); 
	}
	return energyChannels;
    }
    /**
       Retrieves the energy channel boundaries for a given spectrum.  Note 
       that since this is histogram data, there is one more boundary value 
       than there are elements in a spectrum.
       @deprecated Should be using @link #EnergyChannelBoundaries( Segment, int )
       @param detID - Detector ID.
       @param hist - Histogram number.
       @return Array of boundary values with 1 + number of channels values.
    */
    public float[] EnergyChannelBoundaries( int detID, int hist ) {
	float[] timeBounds;

	timeBounds = TimeChannelBoundaries( detID, hist );
	float[] energyChannels = new float[timeBounds.length];
	float timeToSample = (float)(header.sourceToSample / 
				     Math.sqrt( header.energyIn/MEV_FROM_VEL));
   
	for (int i = 0; i < timeBounds.length; i++ ) {
	    energyChannels[i] = (float)(header.energyIn - MEV_FROM_VEL * 
					Math.pow(FlightPath(detID, hist) 
				     / (timeBounds[i] - timeToSample), 2.0)); 
	}
	return energyChannels;
    }

    /** 
	Retrieves the lower level discriminator for a detector.
	@param id - id of the detector
	@return Value of the lower level discriminator.
    */
     public int LowerLevelDisc(int id) {
	if (id < 1 || id > header.nDet ) return -1;
	if (header.versionNumber < 4 ) return (short)0;
	return discriminator[id].lowerLevel;
    }

    /** 
	Retrieves the upper level discriminator for a detector.
	@param id - id of the detector
	@return Value of the upper level discriminator.
    */
    public int UpperLevelDisc(int id) {
	if (id < 1 || id > header.nDet ) return -1;
	if (header.versionNumber < 4 ) return (short)0;
	return discriminator[id].upperLevel;
    }

    /** Retrieve a list that maps detectors to Subgroup IDs.  Note that this
	array starts at index 1 since it references information by detector 
	ID.  The list for all histograms is given here in a one dimentional
	array.  The index for a detector in a paricular detector histogram can
	be given by:
	index = (hist -1)*NumDet() + id;
	@return Array of mapped values
    */
    public int[] subgroupIDList() {
	return subgroupIDList;
    }

    /** Determines if detectors have been binned within a particular histogram.
	This is a rare occurance but anything is possible with users.
    */
    public boolean IsHistogramGrouped(int hist){
	if (minSubgroupID[hist] > maxSubgroupID[hist]) {
	   return false;
	   }
	else {
	   return true;
	   }
    }

    /** Retrieves the minimum subgroup ID in a histogram.  The subgroup IDs 
	are continuous accross the histogram boundaries.  i.e. The fist 
	subgroup ID in histogram 2 = 1 + the subgroup ID for histogram 1.
	@param hist - Requested histogram number.
	@return The ID of the maximum Subgroup ID for the histogram.
    */
    public int MinSubgroupID( int hist ) {
	return minSubgroupID[hist];
    }

    /** Retrieves the maximum subgroup ID in a histogram.  The subgroup IDs 
	are continuous accross the histogram boundaries.  i.e. The fist 
	subgroup ID in histogram 2 = 1 + the subgroup ID for histogram 1.
	@param hist - Requested histogram number.
	@return The ID of the maximum Subgroup ID for the histogram.
    */
    public int MaxSubgroupID( int hist ) {
	return maxSubgroupID[hist];
    }

    /**
       Retrieves a list of IDs mapped to a subgroup.  Note that this is a 0
       indexed array since there is no reference to a paticular group or ID for
       this array.
       @param  sg - subgroup number
       @return A list of detector IDs
    */
    public int[] IdsInSubgroup( int sg ) {
	System.out.println("subgroupMap.length : " + subgroupMap.length);
	System.out.println("sg: " + sg);
	return subgroupMap[sg];
    }

    /**
       Retrieves a list of Segments mapped to a subgroup.  Note that this is 
       a 0 indexed array since there is no reference to a paticular group or 
       ID for this array.
       @param sg - subgroup number
       @return A list of Segments
    */
    public Segment[] SegsInSubgroup( int sg ) {
	return segmentMap[sg];
    }

   /**
       Checks to see if an ID is a beam monitor.
       @deprecated Should be using @link #IsIDBeamMonitor( Segment )
       @param id - detector ID.
       @return boolean true if ID is a beam monitor.
   */
   public boolean IsIDBeamMonitor( int id ) {
	if ((detectorAngle[id] == 0.0 || detectorAngle[id] == 180.0 || 
	    detectorAngle[id] == -180.0) && (detectorHeight[id] == 0.0) ) { 
	    if ( header.iName.equalsIgnoreCase( "glad" )
		 && header.versionNumber < 4 ) {
		if ( crateNum[id] == 1 && slotNum[id] == 20 ) {
		    return true;
		}
		else return false;
	    }
	    else return true;
	}
	else 
	   return false;
	}
   
   /**
       Checks to see if an ID is a beam monitor.
       @param seg - detector segment.
       @return boolean true if ID is a beam monitor.
   */
   public boolean IsIDBeamMonitor( Segment seg ) {
       int id = seg.detID;
       if ((detectorAngle[id] == 0.0 || detectorAngle[id] == 180.0 || 
	   detectorAngle[id] == -180.0 ) && (detectorHeight[id] == 0.0)) {
	   if ( header.iName.equalsIgnoreCase( "glad" )
		&& header.versionNumber < 4 ) {
	       if ( crateNum[id] == 1 && slotNum[id] == 20 ) {
		   return true;
	       }
	       else return false;
	   }
	   else if ( detectorType[id]==12 || detectorType[id]==13||
		     detectorType[id]==14 || detectorType[id]==20 ) {
	     return false;
	   }
	   else return true;
       }
       else
	   return false;
   }
    
   /**
       Checks to see if a subgroup is a beam monitor.
       @param sg - detector subgroup.
       @return boolean true if subgroup is a beam monitor.
   */
   public boolean IsSubgroupBeamMonitor( int sg ) {

	if ( header.versionNumber < 5 && 
	     psdOrder[segmentMap[sg][0].detID] > 1 ) return false;
	for ( int ii = 0; ii < subgroupMap[sg].length; ii++ ) {
	    Segment seg = segmentMap[sg][ii];
	    if ( IsIDBeamMonitor(seg))  
	       return true;
            }
	    return false;
	}

   /**
       Checks to see if an ID is binned for Pulse height.
       @deprecated Should be using @link #IsPulseHeight( Segment, int )
       @param id - detector ID.
       @param hist - Histogram.
       @return boolean true if subgroup is a beam monitor.
   */
   public boolean IsPulseHeight( int id, int hist ) {
       if ( !(header.iName).equalsIgnoreCase("glad"))
       if ( header.versionNumber < 5 && psdOrder[id] > 1 ) return false;
	int index = header.numOfElements * (hist - 1) + id;
	if (detectorMap[index].tfType == 0 ) return false;
   	int tfType = detectorMap[index].tfType;
	if ( timeField[tfType].pulseHeightBit > 0 ) {
	    return true;
	}
       
	else
	   return false;
	}
   
   /**
       Checks to see if an ID is binned for Pulse height.
       @param seg - detector segment.
       @param hist - Histogram.
       @return boolean true if subgroup is a beam monitor.
   */
   public boolean IsPulseHeight( Segment seg, int hist ) {
       int id = seg.detID;
       if ( !(header.iName).equalsIgnoreCase("glad"))
       if ( header.versionNumber < 5 && psdOrder[id] > 1 ) return false;
	int index = header.nDet * (hist - 1) + id;
	if (detectorMap[index].tfType == 0 ) return false;
   	int tfType = detectorMap[index].tfType;
	if ( timeField[tfType].pulseHeightBit > 0 ) {
	    return true;
	}
       
	else
	   return false;
	}
   
   /**
       Checks to see if a subgroup is binned for Pulse height.
       @param sg - detector subgroup.
       @return boolean true if subgroup is a beam monitor.
   */
    public boolean IsPulseHeight( int sg ) {
       if ( !(header.iName).equalsIgnoreCase("glad"))
	if ( header.versionNumber < 5 && psdOrder[segmentMap[sg][0].detID] > 1 ) return false;
	int hist = 0;
	for ( int ii = 0; ii < subgroupMap[sg].length; ii++ ) {
	    int id = subgroupMap[sg][ii];
	    for ( int jj = 1; jj <= header.numOfHistograms; jj++ ) {
		if ( sg <= MaxSubgroupID(jj) ) hist = jj;
	    }
	    if ( IsPulseHeight( id, hist ))
		return true;
	}
	return false;
    }
    
    /** Returns Instrument Type
     */
    public int InstrumentType() {
	return ( header.instrumentType );
    }

    /** Returns Fileter Type
     */
    public int FilterType() {
	return ( header.filterType );
    }

    /** Returns SampleEnv
     */
    public int SampleEnv() {
	return ( header.sampleEnv );
    }

    /** Returns Instrument Type
     */
    public int RunType() {
	return ( header.runType );
    }

    /** Returns Detector Configuration
     */
    public int DetectorConfig() {
	return ( header.detectorConfig );
    }

    /**
       Compares this runfile to another Runfile object to see if they have 
       the same structure.  i.e. detector map and time field table are the 
       same.
       @param runFile - runfile to compare
       @return true if the same structure and false if the structure is 
       different.
    */
    public boolean isEqual( Runfile runFile ) {
	boolean mapEqual, tfTableEqual, answer;
	
	mapEqual = false;
	tfTableEqual = false;
	answer = false;
	
	if ( this.detectorMap.length == runFile.detectorMap.length ) {
	    int dMapLength = this.detectorMap.length;
	    for ( int ii = 1; ii < dMapLength; ii++ ) {
		if ( this.detectorMap[ii].isEqual(runFile.detectorMap[ii]) )
		    mapEqual = true;
	    }
	}
	if ( this.timeField.length == runFile.timeField.length ) {
	    int tfLength = this.timeField.length;
	    for ( int ii = 1; ii < tfLength; ii++ ) {
		if ( this.timeField[ii].isEqual(runFile.timeField[ii]) )
		    tfTableEqual = true;
	    }
	}

    answer = mapEqual && tfTableEqual;
    return answer;

    }
    /**
       Compares this runfile to a list of Runfile objects to see if they have 
       the same structure.  i.e. detector map and time field table are the 
       same.
       @param runFile - list of runfiles to compare
       @return true if the same structure and false if the structure is 
       different.
    */
    public boolean isEqual( Runfile[] runFile ) {

	boolean answer;
	
	answer = true;
	if ( runFile.length <= 0 ) return false;

	for ( int ii = 0; ii < runFile.length; ii++ ) {
	    if ( !this.isEqual(runFile[ii]) ) answer = false;
	}
	
	return answer;
    }


    /**
       Returns a list of property Names (keys) from the runfile
    */

    public Enumeration propertyNames() {
	return properties.propertyNames();
    }

    /**
       Returns the value of a property given a property name (key).
     */
    public String getProperty( String key ) {
	return properties.getProperty( key );
    }

    /**
       Returns the solid angle for a given detector or group of detectors in
       a subgroup
    */
    public float SolidAngle( int subgroup ) {
	return (float)segmentMap[subgroup].length;
    }

    /**
       Returns the solid angle for a given detector segment
    */
    public float SolidAngle( Segment seg, int hist ) {
	return 1.0f;
    }

    /**
       Returns the solid angle for a given detector ID
    */
    public float SolidAngle( int detID, int hist ) {
	return 1.0f;
    }

    /**
       Returns the time field type for a given detector or group of detectors 
       in a subgroup
    */
    public int TimeFieldType( int subgroup ) throws IOException
    {
	int hist=0;

	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet)
	    return -1;
	
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	int id = segsInSg[0].detID;
	if ( !( (psdOrder[id] == 2) && 
		(header.versionNumber < 5 ) ) ) {
	    for (int i = 1; i <= header.numOfHistograms; i++) {
		if ( subgroup <= MaxSubgroupID(i) && 
		     subgroup >= MinSubgroupID(i))
		    hist =  i;
	    }
	    
	    return TimeFieldType(segsInSg[0], hist);
	}
	else {
	    return -1;
	}
    }

    /**
       Returns the time field type for a given detector ID
    */
    public int TimeFieldType( Segment seg, int hist) throws IOException
    {
	int  index;
	int detID = seg.detID;
	if ( !((psdOrder[detID] == 2) && (header.versionNumber < 5 )) ) {
	
	    index  = seg.segID + (hist-1) * this.header.numOfElements;
	    return this.detectorMap[index].tfType;
	}
	else {
	    return -1;
	    }
    }

    /**
       Returns the time field type for a given detector ID
       @deprecated Should be using @link #TimeFieldType( Segment, int )
    */
    public int TimeFieldType( int detID, int hist) throws IOException
    {
	int  index;
	
	index  = detID + (hist-1) * this.header.nDet;
	return this.detectorMap[index].tfType;
    }

    /**
       Returns a 2D array that contains data from time slice sliceNum
     */
    public float[][] AreaTimeSlice( int sliceNum ) throws IOException {
	int numWavelength = header.numOfWavelengths;
	int numX = header.numOfX;
	int numY = header.numOfY;
	int areaStartAddress;
	int sliceInterval;

	if ( leaveOpen == false){
	    System.out.println("GetSpectrum1D opening file");
	    runfile = new RandomAccessRunfile(runfileName, "r");
	}
	
	float[][] slice = new float[numY][numX];

	if ( header.versionNumber <= 4 ) {
	    
	    areaStartAddress = header.histStartAddress;
	    sliceInterval = (header.totalChannels*2)/(header.numOfWavelengths);
	    runfile.seek( areaStartAddress + ( sliceNum - 1 ) * sliceInterval + 2);

	    int wordLength = 2;
	    byte[] bdata;
	    bdata = new byte[numX *numY * wordLength];
	    int nbytes = runfile.read( bdata, 0, 
				       numX * numY * wordLength);

	    for (int ix = 0; ix < numX; ix++){
		for (int iy = 0; iy < numY; iy++ ) {
		    for (int j = 0; j < wordLength; j++) {
			int byteIndex = (iy * numX + ix) * wordLength + j;
			if ( bdata[byteIndex] < 0 ) {
			    slice[iy][ix] += (bdata[byteIndex] + 256) * 
				Math.pow(256.0, j);
			}
			else {
			    slice[iy][ix] += bdata[byteIndex] * Math.pow(256.0, j);
			}
		    }
		}
	    }
	    
	}

	if (!leaveOpen ){
	    runfile.close();
	}
	return slice;
    }

   /**
       Returns the hardware crate number of the TOF module for this segment.
       @param seg - detector segment.
       @return int crate #.
   */
   public int CrateNum( Segment seg ) {
       int id = seg.detID;
       return crateNum[id];
   }
   /**
       Returns the hardware slot number ( in a crate) of the TOF module 
       for this segment.
       @param seg - detector segment.
       @return int slot #.
   */
   public int SlotNum( Segment seg ) {
       int id = seg.detID;
       return slotNum[id];
   }
   /**
       Returns the hardware input number (in a slot) of the TOF module for 
       this segment.
       @param seg - detector segment.
       @return int input  #.
   */
    public int InputNum( Segment seg ) {
       int id = seg.detID;
       return inputNum[id];
   }
 
    private void setMinID() {
	int lastRealID = 1;
	minID[1] = 1;
	if ( header.nDet > 1 ) {
	    for (int id = 2; id <= header.nDet; id++ ) {
		if ( detectorType[id] != 0 ) {
		    if ( detectorType[lastRealID] !=0 ) {
			minID[id] = minID[lastRealID] + 
			    numSegs1[lastRealID] * numSegs2[lastRealID];
		    }
		    else {
			minID[id] = minID[lastRealID] + 1;
		    }
		    lastRealID = id;
		}
		else { 
		    minID[id] = minID[lastRealID] + 1;
		    lastRealID = id;
		}
	    }
	}
    }
    
    void readParamFileFromRunfile( RandomAccessRunfile runfile, 
				   ParameterFile par ) throws IOException {
	if ( header.versionNumber >=5 ) {
	    try {
		byte[] temp = new byte[16];
		runfile.read( temp, 0, 16);
		par.setDeviceName(new String(temp));
		runfile.read( temp, 0, 16);
		par.setDeviceNameDbSignal(new String(temp));
		runfile.read( temp, 0, 16);
		par.setControllerName( new String(temp) );
		runfile.read( temp, 0, 16);
		par.setDbDevice( new String(temp) );
		runfile.read( temp, 0, 16);
		par.setVetoSignal( new String(temp) );
		int numUserParams = runfile.readShort();
		int numInstParams = runfile.readShort();
		temp = new byte[16];
		runfile.read( temp, 0, 1);
		par.setAncIoc( new String(temp) );
		Parameter[] userPars = new Parameter[numUserParams];
		for ( int ii = 0; ii < numUserParams; ii++ ) {
		    userPars[ii] = new Parameter();
		    runfile.read( temp, 0, 16 );
		    userPars[ii].setName( new String(temp) );
		    userPars[ii].setValue( runfile.readFloat() );
		    runfile.read( temp, 0, 16 );
		    userPars[ii].setDbSignal( new String(temp) );
		    
		    int numUserOpts = runfile.readShort(); 
		    //		    System.out.println("Reading UserParameter " + ii );
		    //		    System.out.println(" Reading " + numUserOpts + 
		    //				       " for parameter " + ii );
		    if (numUserOpts > 0 ) {
			//			System.out.println("Reading UserParameter " + ii );
			String[] topts = new String[numUserOpts];
			for (int jj = 0; jj < numUserOpts; jj++ ) {
			    runfile.read( temp, 0, 16 );
			    topts[jj] = new String(temp);
			}
			userPars[ii].setOptions(topts);
		    }
		}
		par.setUserParameters(userPars);
		
		Parameter[] instPars = new Parameter[numInstParams];
		for ( int ii = 0; ii < numInstParams; ii++ ) {
		    //		    System.out.println("Reading InstParameter " + ii );
		    instPars[ii] = new Parameter();
		    runfile.read( temp, 0, 16 );
		    instPars[ii].setName( new String(temp) );
		    instPars[ii].setValue( runfile.readFloat() );
		    runfile.read( temp, 0, 16 );
		    instPars[ii].setDbSignal( new String(temp) );
		    int numInstOpts = runfile.readShort(); 
		    if (numInstOpts > 0 ) {
			String[] topts = new String[numInstOpts];
			for (int jj = 0; jj < numInstOpts; jj++ ) {
			    runfile.read( temp, 0, 16 );
			    topts[jj] = new String(temp);
			}
			instPars[ii].setOptions(topts);
		    }
		}
		par.setInstParameters(instPars);
		
	    }
	    catch (IOException ex) {
		System.out.println("Problem reading control parameters from "+
				   "runfile");
		ex.printStackTrace();
		throw new IOException();
	    }
	}
	else {
	    try {
		byte[] temp = new byte[16];
		 runfile.read(temp, 0, 16);
		 par.setDeviceName(new String(temp));
		 //		 System.out.println( "Reading Parrameter:" + (new String(temp)));
		 int numUserParams = (int)runfile.readRunFloat();
		 Parameter[] userPars = new Parameter[numUserParams];
		 for (int ii = 0; ii < numUserParams; ii++) {
		     userPars[ii] = new Parameter();
		     userPars[ii].setName((new String ("uParam" + ii)));
		     float ftemp = (float)runfile.readRunFloat();
		     userPars[ii].setValue(ftemp);
		     userPars[ii].setDbSignal((new String("undefined")));
		     
		 }
		 par.setUserParameters(userPars);
		 }
	    catch ( IOException ex ) {
		System.out.println("Problem reading control parameters from "+
				   "runfile");
		ex.printStackTrace();
		throw new IOException();
	    }
	}
	return;
    }

    public ParameterFile[] getControlParams() {
	return params;
    }

    /** 
	returns instrument name from the header 
    */
    public String iName() {
	return header.iName;
    }

    /**
       Checks to see if a subgroup's time binning type has a constant time step
       @param sg - detector subgroup.
       @return boolean true if time step is constant in time.
     */
    boolean IsTimeTypeConstStep(int subgroup) {
	int hist=0;
	if (subgroup > MaxSubgroupID(header.numOfHistograms)* header.nDet) 
	    return false;
	Segment[] segsInSg = SegsInSubgroup(subgroup);
	Segment seg = segsInSg[0];
	
	for (int i = 1; i <= header.numOfHistograms; i++) {
	    if ( subgroup <= MaxSubgroupID(i) 
		 && subgroup >= MinSubgroupID(i)) hist =  i;
	}
	    
	return IsTimeTypeConstStep(seg, hist);
    }


    /**
       Checks to see if a segment's time binning type has a constant time step
       @param segID - segment ID.
       @param hist  - histogram #
       @return boolean true if time step is constant in time.
     */
    boolean IsTimeTypeConstStep(Segment segID, int hist) {
	int id = segID.detID;
	if ( id > header.numOfElements || 
	     hist > header.numOfHistograms ) return false;
	int index = header.numOfElements * (hist - 1) + segID.segID;

	if ( !((psdOrder[id] == 2) && (header.versionNumber < 5 )) ) {
	    int tftype = detectorMap[index].tfType;
	    if ( timeField[tftype].wavelengthBinBit != 0 ||
		 timeField[tftype].energyBinBit != 0 ||
		 timeField[tftype].logBinBit != 0 ) {
		return false;
	    }
	    else { 
		return true;
	    }
	}
	else {
	    if (header.areaBinning != 0) {
		return false;
	    }
	    else {
		return true;
	    }
	}
    }

    

}

   
