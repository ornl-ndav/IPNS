package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBSetter extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBSetter operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBSetter( RunfileBuilder rfb, String attr,  Object val)
  {
	
    super( "RFBSetter" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("filename", rfb );
    addParameter( parameter );
    parameter= new Parameter("Attr name", attr );
    addParameter( parameter );
    parameter= new Parameter("Attr val", null );
    addParameter( parameter );

  }

public RFBSetter()
  { super( "RFBSetter" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBSetter";
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
    parameter= new Parameter("Attr name", "userName" );
    addParameter( parameter );
    parameter= new Parameter("Attr val", null);
    addParameter( parameter );



  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
    String attr = (String)(getParameter(1).getValue());
    Object val = getParameter(2).getValue(); 
    int rval = 0;
    if ( attr.equals("numOfHistograms"))
    {
       short val1 = ((Number)val).shortValue();
       rval = rfb.headerSet(attr, val1 );
    } 
    else if ( (val instanceof String) )
    {
      String val1 = (String)val;
      rval = rfb.headerSet(attr, val1 );
    } 

    else if ( (val instanceof Float) )
    {
      float val1 = ((Float)val).floatValue();
      rval = rfb.headerSet(attr, val1 );
    } 
 
    //else if ( (val instanceof Short) )
   // {
    //  short val1 = ((Short)val).shortValue();
   //   rval = rfb.headerSet(attr, val1 );
   // } 

    else if ( (val instanceof Integer) && !(attr.equals("numOfHistograms")) )
    {
      int val1 = ( (Integer)val).intValue();
      rval = rfb.headerSet(attr, val1 );
    } 

    else if ( (val instanceof Double) )
    {
      double val1 = ((Double)val).doubleValue();
      rval = rfb.headerSet(attr, val1 );
    } 

    else {System.out.println("Attribute Value type not supported");}
    
    return new Integer(rval);
         
  }  

}
