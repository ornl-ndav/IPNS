package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBAddPulseHeightTF extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBAddPulseHeightTF operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBAddPulseHeightTF( RunfileBuilder rfb, Float val1, Float val2, Float val3, Integer val4)
  {
	
    super( "RFBAddPulseHeightTF" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("Runfilebuilder", rfb );
    addParameter( parameter );
    parameter= new Parameter("Min", null );
    addParameter( parameter );
    parameter= new Parameter("Max", null );
    addParameter( parameter );
    parameter= new Parameter("Step", null );
    addParameter( parameter );
    parameter= new Parameter("TFNum", null );
    addParameter( parameter );

  }

public RFBAddPulseHeightTF()
  { super( "RFBAddPulseHeightTF" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBAddPulseHeightTF";
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
    parameter= new Parameter("Min", null );
    addParameter( parameter );
    parameter= new Parameter("Max", null );
    addParameter( parameter );
    parameter= new Parameter("Step", null );
    addParameter( parameter );
    parameter= new Parameter("TFNum", null );
    addParameter( parameter );


  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    int rval = 0;
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    float val1 = ( (Float)(getParameter(1).getValue()) ).floatValue();
    float val2 = ( (Float)(getParameter(2).getValue()) ).floatValue();
    float val3 = ( (Float)(getParameter(3).getValue()) ).floatValue();
    int val4   = ( (Integer)(getParameter(4).getValue())).intValue();
    rval = rfb.addPulseHeightTimeField(val1, val2, val3, val4);    
    return new Integer(rval);
         
  }  

}
