
/**
 * RunFileNameFilter.java
 *
 *
 * Created: Mon Jul  1 16:45:58 2002
 *
 * @author John Hammonds
 * @version
 */
package IPNS.Runfile;

import java.io.*;
public class RunFileNameFilter implements FilenameFilter {

    public RunFileNameFilter(){
	};

 
   public boolean accept (File dir, String name) {
	return ((name.toLowerCase()).endsWith(".run"));
    }

}
