/*
 * @(#)InstrumentType.java     0.1  99/07/08  Dennis Mikkelson
 *
 *
 *  $Log$
 *  Revision 1.3  2002/04/24 14:50:50  hammonds
 *  Fixed some instrument names for IPNS instruments
 *
 *  Revision 1.2  2001/03/05 19:42:36  hammonds
 *  Added test and dlab to instrument types
 *
 *  Revision 1.1  2001/02/27 21:08:17  hammonds
 *  add function to return the instrument type.
 *
 *  Revision 1.6  2001/02/16 21:53:49  dennis
 *  Added instrument types for triple axis spectrometer and four
 *  mono-chromatic instrument types. ( For compatibility with NeXus.)
 *
 *  Revision 1.5  2000/08/03 21:50:32  dennis
 *  Added methods to get the path and file name separately from the fully
 *  qualified file name.
 *
 *  Revision 1.4  2000/07/13 14:28:28  dennis
 *  Changed formIPNSFileName() method to NOT include the path
 *
 *  Revision 1.3  2000/07/12 18:33:29  dennis
 *  Added method formIPNSFileName() to construct a IPNS runfile
 *  name from the path, instrument name and run number.
 *
 *  Revision 1.2  2000/07/10 22:24:44  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.2  2000/05/11 16:42:51  dennis
 *  added RCS logging
 *
 */
package IPNS.Runfile;

import java.io.*;
//import DataSetTools.util.*;

/**
 *  This class defines constants for various instrument types and provides
 *  static methods for determining the instrument type from the file name
 *  for instruments at IPNS.
 */


public class InstrumentType implements Serializable
{
  public static final int  UNKNOWN                   = 0;
  public static final int  TOF_DIFFRACTOMETER        = 1;
  public static final int  TOF_SCD                   = 2;    
  public static final int  TOF_SAD                   = 3;    
  public static final int  TOF_REFLECTROMETER        = 4;
  public static final int  TOF_DG_SPECTROMETER       = 5;    // direct geometry
  public static final int  TOF_IDG_SPECTROMETER      = 6;   // inverse geometry

  public static final int  TRIPLE_AXIS_SPECTROMETER  = 7;
  public static final int  MONO_CHROM_DIFFRACTOMETER = 8;
  public static final int  MONO_CHROM_SCD            = 9;    
  public static final int  MONO_CHROM_SAD            = 10;    
  public static final int  MONO_CHROM_REFLECTROMETER = 11;




  /**
   *  Determine the type of an IPNS instrument based on the name of a
   *  runfile.
   *
   *  @param   file_name   The initial file name complete with the path.
   *                       The characters "/" or "\\" must be used to
   *                       describe the path.
   *
   *  @return              An integer code for the instrument type.  The
   *                       integer codes are defined above.
   */

 /**
  *  Determine the type of an IPNS instrument based on the instrument name
  *  alone.
  *
  *  @param  inst_name  The name of the instrument, such as "HRCS" or "GPPD"
  *
  *  @return            An integer code for the instrument type.  The
  *                     integer codes are defined above.
  */
 public static int getIPNSInstType( String  inst_name)
  {
     if ( inst_name.equalsIgnoreCase( "GPPD" )  ||
         inst_name.equalsIgnoreCase( "SEPD" )  ||
         inst_name.equalsIgnoreCase( "GLAD")    ) 
      return TOF_DIFFRACTOMETER;
    
    else if (inst_name.equalsIgnoreCase( "SCD0" ) )
      return TOF_SCD;

    else if ( inst_name.equalsIgnoreCase( "SAND" )  ||
              inst_name.equalsIgnoreCase( "SAD0" )  ||
              inst_name.equalsIgnoreCase( "SAD1" )     )
      return TOF_SAD;

    else if ( inst_name.equalsIgnoreCase( "POSY" )  ||   // ##### fix this
              inst_name.equalsIgnoreCase( "PNE0" )    )
      return TOF_REFLECTROMETER;

    else if ( inst_name.equalsIgnoreCase( "HRCS" )  ||
              inst_name.equalsIgnoreCase( "LRCS" )  ||
              inst_name.equalsIgnoreCase( "TEST" )  ||
              inst_name.equalsIgnoreCase( "DLAB" )    )
      return TOF_DG_SPECTROMETER;
 
    else if ( inst_name.equalsIgnoreCase( "QENS" ) ||
              inst_name.equalsIgnoreCase( "CHEX" )  )
      return TOF_IDG_SPECTROMETER;

    else
      return UNKNOWN;
  }

}
