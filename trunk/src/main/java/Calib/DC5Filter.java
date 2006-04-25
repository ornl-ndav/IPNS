package IPNS.Calib;

import java.io.File;
import javax.swing.filechooser.*;

/*
 *
 * $Log$
 * Revision 1.2  2005/12/22 03:15:09  hammonds
 * Remove unused import.
 *
 * Revision 1.1  2001/07/23 21:37:57  hammonds
 * Moved from iCame.dcalib.
 *
 * Revision 5.0  2000/01/07 05:33:19  hammonds
 * Added log messages to a comment section in each file.  Also have set the
 * version number to 5.0 which is the Runfile version number associated with
 * these programs at this time.
 *
 *
 */

public class DC5Filter extends FileFilter {
   public boolean accept(File f){
     if (f.isDirectory() ) {
        return true;
        }
     String s = f.getName();
     int index = s.lastIndexOf('.');
     String extension = null;
     if ( index > 0 && index < s.length() - 1 ) {
        extension = s.substring(index +1).toLowerCase();
        }
     if ( extension != null ) {
       if ( extension.equals("dc5") ){
           return true;
           }
       else {
           return false;
           }
       }
     return false;
     }

public String getDescription() {
     return "Detector Calibration (*.dc5)";
     }
}
