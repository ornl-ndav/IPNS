package IPNS.Control;
import java.util.*;
import java.io.*;

public class Parameter {

    String type;
    int index;
    float value;
    String name = new String();
    String dbsignal = new String();
    String[] options = new String[0];
    boolean optionsAvailable;
    public Parameter(){
    type = new String("Not defined");
    name = new String("Not defined");
    dbsignal = new String("Not defined");
    }

    public Parameter( Properties input, String type, int num ) {
		  this.type = type;
		  index = num;
		  name = 
				input.getProperty((new String(type + "ParameterName" + num)), 
										"NotDefined" );
		  dbsignal = 
				input.getProperty((new String(type + "ParameterDbSignal" + num)), 
										"NotDefined" );
		  value = 
				(new Float(
							  input.getProperty(
													  (new String(type +"Parameter" + num)), "0")
							  )).floatValue();
		  optionsAvailable=false;
		  String tOptions =
				input.getProperty((new String(type + 
														"ParameterOptions" + num)),
										"undefined") ;
		  if( !tOptions.equalsIgnoreCase("undefined") ) {
				options = getCommaList(tOptions);
				if ( options.length > 1 ) {
					 optionsAvailable = true;
				}
				else {
					 System.out.println( "Options are not valid.  "+
												"Must contain more than one option.");
				}
		  }
		  
    }
	 /**
	 */
	 public Parameter( RandomAccessFile ancFile ) throws IOException {
		  byte[] temp = new byte[16];
		  ancFile.read( temp, 0, 16 );
		  name = new String(temp);
		  value = ancFile.readFloat();
		  ancFile.read( temp, 0, 16 );
		  dbsignal = new String(temp);
		  
		  int numUserOpts = ancFile.readShort(); 
		  if (numUserOpts > 0 ) {
				optionsAvailable = true;
				String[] topts = new String[numUserOpts];
				for (int jj = 0; jj < numUserOpts; jj++ ) {
					 ancFile.read( temp, 0, 16 );
					 topts[jj] = new String(temp);
				}
				options = topts;
		  }
 
	 }

    /**
       Saves the parameters to a file 
    */
    public void save(PrintWriter pwout) throws IOException {

		  pwout.println( type + "ParameterName" + index + "=" + name );
		  pwout.println( type + "Parameter" + index + "=" + value );
		  pwout.println( type + "ParameterDbSignal" + index + "=" + dbsignal );
		  if ( optionsAvailable  ) {
				pwout.print(type + "ParameterOptions" + index + "=");
				for (int jj=0; jj<(options.length-1); jj++){
					 pwout.print(options[jj] + ",");
				}
				pwout.println(options[options.length-1]);
		  }
		  
    }

    /**
     */
    public static void printParameters( Parameter[] params) {
		  for ( int ii=0; ii < params.length; ii++) {
				System.out.println( "  |-" + params[ii].type + "Parameter " + (ii+1) );
				System.out.println( "  | |-" + "Name: " + params[ii].name);
				System.out.println( "  | |-" + "Current Value: " + 
										  params[ii].value);
				System.out.println( "  | |-" + "DB signal name: " + 
										  params[ii].dbsignal);
				if ( params[ii].optionsAvailable ) {
					 System.out.println( "  | |-Options" );
					 for (int jj=0; jj<(params[ii].options).length; jj++){
						  System.out.println( "       | | |-" + 
													 params[ii].options[jj] + "= " +
													 jj);
					 }
				}
				else {
				}
		  }
    }

    /**
       Get the Parameter name
     */
    public String Name() {
		  return name;
    }

    /** 
	Set the parameter's name
    */
    public void setName( String pname ) {
		  name = pname;
    }

    /**
       Get the associated database signal name
     */
    public String DbSignal() {
		  return dbsignal;
    }

    /** 
	Set the associated database signal name
    */
    public void setDbSignal( String pname ) {
		  dbsignal = pname;
    }

    /**
       Get the value
     */
    public float Value() {
		  return value;
    }

    /**
     */
    public void setValue( float val ) {
		  value = val;
    }

    /** 
	Set the signals value
    */
    public void setDbSignal( float pval ) {
		  value = pval;
    }

    /**
       Return options for the record
     */
    public String[] Options() {
		  return options;
    }

    /** 
	Set the optionStrings for value translations
    */
    public void setOptions( String[] popts ) {
		  options = popts;
		  if ( popts.length > 0) optionsAvailable = true;
    }
    /**
       Return true if options are available
     */
    public boolean OptionsAvailable() {
		  return optionsAvailable;
    }

    /**
       
     */
    private String[] getCommaList(String options) {
		  String[] list = new String[0];
		  int index1 = 0;
		  int index2 = 0;
		  options = options.trim();
		  index2 = options.indexOf(",");
		  while ( index2 != -1 ) {
				String[] tlist = new String[list.length + 1];
				System.arraycopy( list, 0, tlist, 0, list.length);
				tlist[list.length] = options.substring(index1, index2);
				list=tlist;
				index1 = index2 + 1;
				index2 = options.indexOf(",", index1);
				
		  }
		  String[] tlist = new String[list.length + 1];
		  System.arraycopy( list, 0, tlist, 0, list.length);
		  tlist[list.length] = options.substring(index1, options.length());
		  list=tlist;
		  return list;
    }

    /**
     */
    public int NumOptions(){
		  return options.length;
    }

	 /**
		 Write contents of the class to a binary file such as a Runfile
	 */
	 public void Write(RandomAccessFile ancFile) throws IOException{
		  ancFile.writeBytes( ParameterFile.fixLength( name, 16));
		  ancFile.writeFloat( value );
		  ancFile.writeBytes( ParameterFile.fixLength( dbsignal, 16));
		  if ( optionsAvailable ) {
				ancFile.writeShort( options.length );
				for ( int jj = 0; jj < options.length; jj++ ) {
					 ancFile.writeBytes( ParameterFile.fixLength( options[jj], 16));
				}
		  }
		  else {
				ancFile.writeShort( 0);
		  }
		  
	 }
	 
}
