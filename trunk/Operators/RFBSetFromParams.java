package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBSetFromParams extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBSetFromParams operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBSetFromParams( RunfileBuilder rfb, String attr, Object val)
  {
	
    super( "RFBSetFromParams" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("Runfilebuilder", rfb );
    addParameter( parameter );
    parameter= new Parameter("Filename", null );
    addParameter( parameter );

  }

public RFBSetFromParams()
  { super( "RFBSetFromParams" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBSetFromParams";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
    Parameter parameter= new Parameter("New RunfileBuilder", new RunfileBuilder() );
    addParameter( parameter );
    parameter= new Parameter("File Name", null);
    addParameter( parameter );



  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    String val = (String)(getParameter(1).getValue());
    int rval = 0;
    rval = rfb.headerSetFromParams(val);    
    return new Integer(rval);
         
  }  

}
