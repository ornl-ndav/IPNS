package IPNS.VaxAnc;

import java.io.*;
import IPNS.Control.*;
import javax.swing.*;

/**
   This class is used to create a file for holding ancillary equipment info.
   The created file is basically a cutdown version of a Runfile.  All that 
   will be stored here is control information.  
 */
/*
 * $Log$
 * Revision 1.1  2002/12/17 01:40:30  hammonds
 * initial Add
 *
 */
public class AncFile {
    int numOfControl = 0;
    ParameterFile[] paramFile = new ParameterFile[0];
    String filename = new String(); 
    
    /** Constructor that creates an empty AncFile
     */
    public AncFile() {
    }

    public AncFile( String fileName) {
	try {
	
	    RandomAccessFile ancFile = new RandomAccessFile(filename, "r");
	    numOfControl = ancFile.readInt();
	    paramFile = new ParameterFile[numOfControl];
	    for (int ii=0; ii < numOfControl; ii++ ) {
		paramFile[ii] = new ParameterFile(ancFile);
	    }
	}
	catch (IOException ex) {
	    System.out.println("Trouble loading ancfile, " + filename);
	}
    }
    /**
       This method adds ancillary control parameters from a device file.
     */
    public int addAncillaryEquipment(String inName) {
	int rval = 0;
	ParameterFile[] tparams = new ParameterFile[paramFile.length + 1];
	System.arraycopy(paramFile, 0, tparams, 0, paramFile.length );
	tparams[paramFile.length] = new ParameterFile(inName);
	paramFile = tparams;
	int thisParam = paramFile.length - 1;
	JOptionPane mainframe = 
	    new 
	    JOptionPane(new ParamPane(new String("User"), 
				      paramFile[thisParam].getDeviceName(), 
				      paramFile[thisParam].getUserParameters(),
				      paramFile[thisParam].getUserParameters().
				      length ) );
	String[] options = new String[1];
	options[0] = new String("OK");
	mainframe.setOptions(options);
	JDialog dialog = 
	    mainframe.createDialog(JOptionPane.getRootFrame(),
				   new String(paramFile[thisParam].getDeviceName()
					      + " User Parameters"));
	dialog.show();
	numOfControl += 1;   
	dialog.dispose();
	System.gc();
	return (rval);
   }

    /**
       Set filename for output.
     */
    public void setFilename( String  name) {
	filename = name;
    }

    /**
       Get filename for output.
     */
    public String getFilename() {
	return(filename);
    }

    /**
       Write output file for this device.
     */
    public void Write() throws IOException {
	RandomAccessFile ancFile = new RandomAccessFile(filename, "w");
	try {
	    
	    ancFile.writeInt(numOfControl);
	    for (int kk=0; kk < numOfControl; kk++ ) {
		paramFile[kk].Write(ancFile);
	    }
	    return;
	}
	catch ( IOException ex ) {
	    System.out.println("Error writing Ancillary File " + 
			       filename );
	    ex.printStackTrace();
	    throw new IOException();
	}
    }

    /**
       prints the contents of the class to stdout
    */
    
    public void printAnc() {
	System.out.println("numOfControl: " + numOfControl);
	for (int ii = 0; ii < numOfControl; ii++ ) {
	    paramFile[ii].printDevice();
	    paramFile[ii].printUserParameters();
	    paramFile[ii].printInstParameters();
	}
    }

    /**
     */
    public static void main (String[] args) {
	if ( args.length < 1 ) {
	    System.out.println("need to specify filename");
	}
	String ancFileName = args[0];
	AncFile ancFile = new AncFile(ancFileName);
	ancFile.printAnc();
    }

}
