package IPNS.Control;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

public class ParameterFile {
    String deviceName = new String("Not Defined");
    String deviceNameDbSignal = new String("Not Defined");
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

	 /**
	  */
    public ParameterFile( Properties input ) {
	readPropsFile(input);
    }

	 /**
	  */
	 public ParameterFile(RandomAccessFile ancFile) throws IOException {
		byte[] temp = new byte[16];
		ancFile.read( temp, 0, 16);
		deviceName = new String(temp);
		ancFile.read( temp, 0, 16);
		deviceNameDbSignal = new String(temp);
		ancFile.read( temp, 0, 16);
		controllerName = new String(temp) ;
		ancFile.read( temp, 0, 16);
		dbDevice = new String(temp);
		ancFile.read( temp, 0, 16);
		vetoSignal = new String(temp);
		int numUserParams = ancFile.readShort();
		int numInstParams = ancFile.readShort();
		temp = new byte[16];
		ancFile.read( temp, 0, 1);
		ancIoc = new String(temp);
		Parameter[] userPars = new Parameter[numUserParams];
		for ( int ii = 0; ii < numUserParams; ii++ ) {
		    userPars[ii] = new Parameter(ancFile);
		}
		userParameter = userPars;
		
		Parameter[] instPars = new Parameter[numInstParams];
		for ( int ii = 0; ii < numInstParams; ii++ ) {
		    instPars[ii] = new Parameter(ancFile);
		}
		instParameter = instPars;
	 }

    /**
       Utility method for the constructors
     */
    private void readPropsFile(Properties input){
		  deviceName = input.getProperty("deviceName", "Not_Defined");
		  deviceNameDbSignal = input.getProperty("deviceNameDbSignal", 
															  "Not_Defined");
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
		  System.out.println( deviceNameDbSignal );
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
		  pwout.println("deviceNameDbSignal=" + deviceNameDbSignal );
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
       Set the device name
    */
    public void setDeviceName(String name){
		  deviceName = name;
		  return;
    }

    /**
       Returns the database signal that holds the the device Name
    */
    public String getDeviceNameDbSignal() {
		  return deviceNameDbSignal;
    }

    /**
       Set the device name
    */
    public void setDeviceNameDbSignal(String name){
		  deviceNameDbSignal = name;
		  return;
    }

    /**
       Returns the name of the controller used for this device
     */
    public String getControllerName() {
		  return controllerName;
    }

    /**
       Set the controller name
    */
    public void setControllerName(String name){
		  controllerName = name;
		  return;
    }

    /**
       Returns the device portion of the EPICS record name for parameters in
       this file
     */
    public String getDbDevice() {
		  return dbDevice;
    }

    /**
       Set the database device name
    */
    public void setDbDevice(String name){
		  dbDevice = name;
		  return;
    }

    /**
       Returns the veto signal for connection to the DAS veto.
     */
    public String getVetoSignal() {
		  return vetoSignal;
    }

    /**
       Set the veto signal name
    */
    public void setVetoSignal(String name){
		  vetoSignal = name;
		  return;
    }

    /**
       Returns an array of User level parameters
     */
    public Parameter[] getUserParameters() {
		  return userParameter;
    }

    /** 
	sets the user parameters
    */
    public void setUserParameters(Parameter[] params) {
		  userParameter = params;
		  numUserParameters = params.length;
		  for (int ii =0; ii < numUserParameters; ii++ ) {
				params[ii].type = new String("User");
		  }
		  return;
    }
	 
    /**
       Returns an array of Instrument level parameters
     */
    public Parameter[] getInstParameters() {
		  return instParameter;
    }

    /** 
	sets the user parameters
    */
    public void setInstParameters(Parameter[] params) {
		  instParameter = params;
		  numInstParameters = params.length;
		  return;
    }

    /**
       Returns the crate label for this parameters crate
    */
    public String getAncIoc() {
		  return ancIoc;
    }

    /**
       Set the ancillary Ioc name
    */
    public void setAncIoc(String name){
		  ancIoc = name;
    }

    /**
       Returns file name for the ParameterFile if one is available.
     */
    public String getFileName() {
		  return fileName;
    }

	 /**
		 Write contents of the class to a binary file such as a Runfile
	 */
	 public void Write(RandomAccessFile ancFile) throws IOException {
		  ancFile.writeBytes( ParameterFile.fixLength( deviceName, 16));
		  ancFile.writeBytes( ParameterFile.fixLength( deviceNameDbSignal, 16));
		  ancFile.writeBytes( ParameterFile.fixLength( controllerName, 16));
		  ancFile.writeBytes( ParameterFile.fixLength( dbDevice, 16));
		  ancFile.writeBytes( ParameterFile.fixLength( vetoSignal, 16));
		  ancFile.writeShort( numUserParameters );
		  ancFile.writeShort( numInstParameters );
		  ancFile.writeBytes( ParameterFile.fixLength( ancIoc, 1));
		  
		  for ( int ii = 0; ii < numUserParameters; ii++ ) {
			 userParameter[ii].Write(ancFile);
		  }
		  for ( int ii = 0; ii < numInstParameters; ii++ ) {
				instParameter[ii].Write(ancFile);
		  }
	 }

    /**
     */
    public static String fixLength(String inString, int length) {
		  StringBuffer tempBuff = new StringBuffer( inString );
		  tempBuff.setLength(length);
		  return( new String(tempBuff));
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
