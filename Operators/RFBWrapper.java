package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBWrapper extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBWrapper operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  String infileName;
  public RFBWrapper( String infileName  )
  {
	
    super( "RFBWrapper" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("filename", new String());
    addParameter( parameter );

  }

public RFBWrapper()
  { super( "RFBWrapper" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBWrapper";
   }

 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the default parameters.
  */
  public void setDefaultParameters()
  {
     parameters = new Vector();  // must do this to create empty list of 
                                 // parameters
     Parameter parameter= new Parameter("Runfilename", 
                                         new String());
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    String S = (String) (getParameter(0).getValue());
    RunfileBuilder rfb = new RunfileBuilder();
    if (S != null)
    {
       rfb.setFileName( S + ".RUN");
    }
    return rfb;

  }  


  public static void main(String[] arg)
  {
    RFBWrapper rfb = new RFBWrapper("hello");

  //  try{ Class O = Class.forName( "Operators.RFBWrapper");
  //   }
  //  catch(Exception s)
  //    {System.out.println("Error = "+s);
  //    }
  }

}
