package IPNS.Calib;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/*
 *
 * $ Log: DISFilter.java,v $
 *
 */

public class DISFilter extends FileFilter {
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
       if ( extension.equals("dis") ){
           return true;
           }
       else {
           return false;
           }
       }
     return false;
     }

public String getDescription() {
     return "Vax Discriminator Settings (*.dis)";
     }
}
