import java.io.*;
import java.util.*;


public class Keep {

    public Keep(){}

    public static void main(String[] args) {
	String iName = new String();
	int keep;
	String instDir = new String();

	String homeDir = System.getProperty("user.home");
	System.out.println(homeDir);
	String ifileName = new String(homeDir + "/inst/iname.dat");
	try {
	    FileInputStream ifile = new FileInputStream(ifileName);
	    Properties ifileProp = new Properties();
	    ifileProp.load(ifile);
	    iName = ifileProp.getProperty("iName", new String(""));
	    if (iName.equals("")) {
		System.out.println("No instrument name was found");
		return;
	    }
	    ifile.close();
	    System.out.println(iName);
	}
	catch (IOException ex) {
	    System.out.println("No iName.dat found");
	    return;
	}

	String datfileName = new String(homeDir + "/inst/" + iName+ ".dat");
	try {
	    FileInputStream dfile = new FileInputStream(datfileName);
	    Properties dfileProp = new Properties();
	    dfileProp.load(dfile);
	    instDir = (dfileProp.getProperty("instDir", new String(""))).trim();
	    
	    if (instDir.equals("")) {
		System.out.println("instDir not found in" + datfileName);
		return;
	    }
	    dfile.close();
	    System.out.println(instDir);
	}
	catch (IOException ex) {
	    System.out.println("No " + iName + ".dat was found");
	    return;
	}

	String parfileName = new String(instDir + "/" + iName+ "__V5.par");
	Properties parProp = new Properties();
	try {
	    FileInputStream parfile = new FileInputStream(parfileName);
	    parProp.load(parfile);
	    String keepstr = parProp.getProperty("Keep", new String(""));
	    
	    if (keepstr.equals("")) {
		System.out.println("Keep not found in" + parfileName);
		return;
	    }
	    parfile.close();
	    keep = (new Integer(keepstr)).intValue();
	    System.out.println("Found Keep:" + keep);
	}
	catch (IOException ex) {
	    System.out.println("No " + parfileName + " was found");
	    return;
	}

	if ( args.length  > 0 ) {

	    try {
		int inkeep = (new Integer(args[0])).intValue();
	    }
	    catch (NumberFormatException ex) {
		System.out.println( "Input was expected to be an integer" );
		return;
	    }
	    try {
		parProp.put("Keep", args[0]);
		FileOutputStream parFile = new FileOutputStream(parfileName);
		String paramFileHeader = new
		    String(iName + " instrument parameter " + 
			   "file.  Saved By Keep.");
		parProp.store(parFile, paramFileHeader);
		System.out.println("Setting keep to: " + args[0]);
		parFile.close();
	    }
	    catch ( IOException ex ) {
		System.out.println("Trouble writing keep to file");
	    }
	}
	
    }

}
