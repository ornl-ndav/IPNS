package IPNS.Control;
import java.io.*;
import java.util.*;

class ParameterFile {
    String deviceName = new String("Not Defined");
    String controllerName = new String("Not Defined");
    String fileName = new String("noname.anc");
    String dbDevice = new String("Not Defined");
    String ancIoc = new String("Not Defined");
    String vetoSignal = new String("Not Defined");

    int numUserParameters;
    int numInstParameters;
    Parameter[] userParameter = new Parameter[0];
    Parameter[] instParameter = new Parameter[0];

    public ParameterFile() {
    }

    public ParameterFile( String inName ) {
	FileInputStream propsFile;
	Properties props = new Properties();
	try {
	    propsFile = new FileInputStream(inName);
	    props.load(propsFile);
	    propsFile.close();
	}
	catch (IOException ex) {
	    System.out.println("File not found");
	}
	fileName = inName;
      	readPropsFile(props);
	
    }

    public ParameterFile( Properties input ) {
	readPropsFile(input);
    }

    /**
       Utility method for the constructors
     */
    private void readPropsFile(Properties input){
	deviceName = input.getProperty("deviceName", "Not_Defined");
	controllerName = input.getProperty("controllerName", "Not_Defined");
	dbDevice = input.getProperty("dbDevice", "Not_Defined");
	ancIoc = input.getProperty("ancIoc", "Not_Defined");
	vetoSignal = input.getProperty("vetoSignal", "Not_Defined");

	numUserParameters = 
	    Integer.parseInt(input.getProperty("numUserParameters", "0"));
	userParameter = new Parameter[numUserParameters];
	for (int ii=0; ii<numUserParameters; ii++){
	    userParameter[ii] = new Parameter(input, "User", ii+1);
	}
	numInstParameters = 
	    Integer.parseInt(input.getProperty("numInstParameters", "0"));
	instParameter = new Parameter[numInstParameters];
	for (int ii=0; ii<numInstParameters; ii++){
	    instParameter[ii] = new Parameter(input, "Inst", ii+1);
	}
    }

    /**
       Prints information on the device and its controller
     */
    public void printDevice() {
	System.out.println( deviceName );
	System.out.println( controllerName );
	System.out.println( dbDevice );
	System.out.println( ancIoc );
	System.out.println( vetoSignal );
    }

    /**
       Saves the parameters to a file 
    */
    public void save(String outname) throws IOException {
	FileOutputStream  out = new FileOutputStream(outname);
	PrintWriter pwout =new PrintWriter(out);
	pwout.println("deviceName=" + deviceName);
	pwout.println("controllerName=" + controllerName);
	pwout.println("dbDevice=" + dbDevice );
	pwout.println("ancIoc=" + ancIoc );
	pwout.println("vetoSignal=" + vetoSignal );
	

	pwout.println("#User Parameters");
	pwout.println("numUserParameters=" + numUserParameters);
	for (int ii=0; ii<numUserParameters; ii++) {
	    userParameter[ii].save(pwout);
	}
	pwout.println("#Inst Parameters");
	pwout.println("numInstParameters=" + numInstParameters);
	for (int ii=0; ii<numInstParameters; ii++) {
	    instParameter[ii].save(pwout);
	}

	pwout.close();
	out.close();
    }

    /**
     */
    public void printUserParameters() {
	System.out.println("User Parameters");
	    Parameter.printParameters( userParameter );
    }

    /**
     */
    public void printInstParameters() {
	System.out.println("Inst Parameters");
	Parameter.printParameters( instParameter );
    }

    /**
       Returns the device Name for the parameters
    */
    public String getDeviceName() {
	return deviceName;
    }

    /**
       Returns the name of the controller used for this device
     */
    public String getControllerName() {
	return controllerName;
    }

    /**
       Returns the device portion of the EPICS record name for parameters in
       this file
     */
    public String getDbDevice() {
	return dbDevice;
    }

    /**
       Returns the veto signal for connection to the DAS veto.
     */
    public String getVetoSignal() {
	return vetoSignal;
    }

    /**
       Returns an array of User level parameters
     */
    public Parameter[] getUserParameters() {
	return userParameter;
    }

    /**
       Returns an array of Instrument level parameters
     */
    public Parameter[] getInstParameters() {
	return instParameter;
    }

    
    /**
       Returns file name for the ParameterFile if one is available.
     */
    public String getFileName() {
	return fileName;
    }

    public static void main( String[] args )throws IOException {
	FileInputStream propsFile;
	Properties props = new Properties();
	try {
	    propsFile = new FileInputStream(args[0]);
	    props.load(propsFile);
	    propsFile.close();
	}
	catch (IOException ex) {
	    System.out.println("File not found");
	}
      	ParameterFile par = new ParameterFile(props);
	par.printDevice();
	par.printUserParameters();
	par.printInstParameters();
	par.save("temp.anc");
    }


}
