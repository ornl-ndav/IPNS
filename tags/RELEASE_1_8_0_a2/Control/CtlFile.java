package IPNS.Control;

import java.io.*;
import IPNS.Operators.Instdir;
import IPNS.Control.CtlSeq;


public class CtlFile {
  CtlSeq[] ctlSeqs = new CtlSeq[0];
  int numRuns = 0;

  public CtlFile() {
  }


  /**
   *   Construct object with most of the data filled out
   */
  public CtlFile(  int numRuns,
		  String userName, String runTitle, String[] userParamNames, 
		  String type, int numCycles, int numPulses, int schedType ) {

    this.numRuns = numRuns;
    ctlSeqs = new CtlSeq[numRuns];
    for (int ii = 0; ii < numRuns; ii++ ) {
      ctlSeqs[ii] = new CtlSeq( ii+1, userName, runTitle, userParamNames, 
				    new String("transmission"), numCycles, 
				  numPulses, 0);
    }
  }

  /**
   *   Construct object using mostly dummy data
   */
  public CtlFile(  int numRuns, String[] userParamNames ) {

    String userName = new String("Joe User");
    String runTitle = new String("Joe's Run Title");
    String type = new String("transmission");
    int numCycles = 720;
    int numPulses = 108000;
    int schedType = 0;

    this.numRuns = numRuns;
    String instDir = (String)((new Instdir(System.getProperty("Default_Instrument"))).getResult());
    String ancDir = new String (instDir + "/anc");
    ctlSeqs = new CtlSeq[numRuns];
    for (int ii = 0; ii < numRuns; ii++ ) {
      ctlSeqs[ii] = new CtlSeq( ii+1, userName, runTitle, userParamNames, 
				    new String("transmission"), numCycles, 
				  numPulses, 0);
    }
  }

  

  public void WriteCtl( String ctlFileName ){
    try {
      FileOutputStream outfile = new FileOutputStream( ctlFileName );

      PrintWriter outWriter = new PrintWriter(outfile, true);
      outWriter.print( numRuns );
      outWriter.println( "   RUNS");
      for (int ii=1; ii<numRuns; ii++) {
	ctlSeqs[ii].WriteCtl(outWriter);
      }

    }
   catch (FileNotFoundException fnfEx ) {
      System.out.println("trouble opening: " + ctlFileName + "for writing");
    }
  }


  /**
   * Gets the value of ctlSeqs
   *
   * @return the value of ctlSeqs
   */
  public CtlSeq[] getCtlSeqs()  {
    return this.ctlSeqs;
      }
  
  /**
   * Sets the value of ctlSeqs
   *
   * @param argCtlSeqs Value to assign to this.ctlSeqs
       */
  public void setCtlSeqs(CtlSeq[] argCtlSeqs) {
    this.ctlSeqs = argCtlSeqs;
  }
  
  /**
   * Gets the value of numRuns
   *
   * @return the value of numRuns
   */
  public int getNumRuns()  {
    return this.numRuns;
  }

  /**
   * Sets the value of numRuns
   *
   * @param argNumRuns Value to assign to this.numRuns
   */
  public void setNumRuns(int argNumRuns) {
    this.numRuns = argNumRuns;
  }
}
