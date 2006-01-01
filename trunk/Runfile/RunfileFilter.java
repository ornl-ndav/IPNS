package IPNS.Runfile;
import java.io.File;
import javax.swing.filechooser.*;

public class RunfileFilter extends FileFilter {
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
       if ( extension.equals("run") ){
           return true;
           }
       else {
           return false;
           }
       }
     return false;
     }

public String getDescription() {
     return "IPNS Runfile (*.run)";
     }
}
