package IPNS.Operators;

import  java.io.*;
import  java.util.*;
import  DataSetTools.operator.*;
import  IPNS.Runfile.*;

/**
 * This operator instantiates IPNS.RunfileBuilder.
 * 
 */

public class RFBWriter extends  GenericBatch 
                            implements Serializable
{

  /* ------------------------ FULL CONSTRUCTOR -------------------------- */
  /**
   * Construct a default RFBWriter operator to to allow scripts to call IPNS.RunfileBuilder.
   */
  public RFBWriter( RunfileBuilder rfb)
  {
	
    super( "RFBWriter" );
    System.out.println("Inside the constructor now");  
    Parameter parameter= new Parameter("filename", rfb );
    addParameter( parameter );

  }

public RFBWriter()
  { super( "RFBWriter" );
	//System.out.println("def constructor");
     setDefaultParameters();
  }

  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case, Pause
   */
   public String getCommand()
   {
     return "RFBWriter";
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
                                         new RunfileBuilder());
     addParameter( parameter );

  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
    RunfileBuilder rfb = (RunfileBuilder) (getParameter(0).getValue());
   
    
    {
      
      rfb.Write();
      return "RFBWriter finished writing";
    }
         
  }  


  public static void main(String[] arg)
  {
   // RFBWriter rfb = new RFBWriter("hello");

  //  try{ Class O = Class.forName( "Operators.RFBWriter");
  //   }
  //  catch(Exception s)
  //    {System.out.println("Error = "+s);
  //    }
  }

}
