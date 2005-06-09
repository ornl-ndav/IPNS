package IPNS.Control;

import java.io.*;
import IPNS.Control.*;

public class CtlSeq {
  int runNum = 0;
  String userName = new String ("Joe User");
  String runTitle = new String ("My Title");
  ParameterFile[] userParam = new ParameterFile[0];
  String type = new String("transmission");
  int numCyclesSched = 720;
  int numPulsesSched = 108000;
  int schedType = 0;
  String[] schedTypeString = {"P"};
  public CtlSeq() {
  }

  public CtlSeq(int runNum, String userName, String runTitle, 
		String[] userParamNames, String type, int numCycles, 
		int numPulses, int schedType  ) {
    this.runNum = runNum;
    this.userName = new String(userName);
    this.runTitle = new String(runTitle);
    int numParams = userParamNames.length;
    userParam = new ParameterFile[numParams];
    for (int ii = 0; ii < numParams; ii++ ) {
      userParam[ii] = new ParameterFile(userParamNames[ii]);
    }
    this.type = new String(type);
    this.numCyclesSched = numCycles;
    this.numPulsesSched = numPulses;
    this.schedType = schedType;
  }

  public void WriteCtl( PrintWriter outWriter ) {
    outWriter.print("RUN     ");
    outWriter.print(this.runNum);
    outWriter.print("    ");
    outWriter.print(userParam.length);
    outWriter.println("     DEVICES");
	
    outWriter.print("  User    \"");
    outWriter.print(userName);
    outWriter.println("\"");
    outWriter.print("  Title   \"");
    outWriter.print(runTitle);
    outWriter.println("\"");
    outWriter.print("  type   ");
    outWriter.println(type);
	
    outWriter.print("  Schedule   ");
    outWriter.print(numCyclesSched);
    outWriter.print("  ");
    outWriter.print(numPulsesSched);
    outWriter.print("  ");
    outWriter.println( schedTypeString[schedType]);
	
    for (int ii=0; ii<userParam.length; ii++ ) {
      Parameter[] pars = userParam[ii].getUserParameters();
      outWriter.print("  ");
      outWriter.print(userParam[ii].getShortFileName());
      outWriter.print("     ");
      outWriter.print(pars.length);
      outWriter.println("    PARAM");
      outWriter.print("   ");
      for (int valNum = 0; valNum < pars.length; valNum++) {
	outWriter.print(pars[valNum].Value());
	outWriter.print("     ");
      }
      outWriter.println();
    }
    
  }


  /**
   * Gets the value of runNum
   *
   * @return the value of runNum
   */
  public int getRunNum()  {
    return this.runNum;
  }

  /**
   * Sets the value of runNum
   *
   * @param argRunNum Value to assign to this.runNum
   */
  public void setRunNum(int argRunNum) {
    this.runNum = argRunNum;
  }

  /**
   * Gets the value of userName
   *
   * @return the value of userName
   */
  public String getUserName()  {
    return this.userName;
  }

  /**
   * Sets the value of userName
   *
   * @param argUserName Value to assign to this.userName
   */
  public void setUserName(String argUserName) {
    this.userName = argUserName;
  }

  /**
   * Gets the value of runTitle
   *
   * @return the value of runTitle
   */
  public String getRunTitle()  {
    return this.runTitle;
  }

  /**
   * Sets the value of runTitle
   *
   * @param argRunTitle Value to assign to this.runTitle
   */
  public void setRunTitle(String argRunTitle) {
    this.runTitle = argRunTitle;
  }

  /**
   * Gets the value of userParam
   *
   * @return the value of userParam
   */
  public ParameterFile[] getUserParam()  {
    return this.userParam;
  }

  /**
   * Sets the value of userParam
   *
   * @param argUserParam Value to assign to this.userParam
   */
  public void setUserParam(ParameterFile[] argUserParam) {
    this.userParam = argUserParam;
  }

  /**
   * Gets the value of type
   *
   * @return the value of type
   */
  public String getType()  {
    return this.type;
  }

  /**
   * Sets the value of type
   *
   * @param argType Value to assign to this.type
   */
  public void setType(String argType) {
    this.type = argType;
  }

  /**
   * Gets the value of numCyclesSched
   *
   * @return the value of numCyclesSched
   */
  public int getNumCyclesSched()  {
    return this.numCyclesSched;
  }

  /**
   * Sets the value of numCyclesSched
   *
   * @param argNumCyclesSched Value to assign to this.numCyclesSched
   */
  public void setNumCyclesSched(int argNumCyclesSched) {
    this.numCyclesSched = argNumCyclesSched;
  }

  /**
   * Gets the value of numPulsesSched
   *
   * @return the value of numPulsesSched
   */
  public int getNumPulsesSched()  {
    return this.numPulsesSched;
  }

  /**
   * Sets the value of numPulsesSched
   *
   * @param argNumPulsesSched Value to assign to this.numPulsesSched
   */
  public void setNumPulsesSched(int argNumPulsesSched) {
    this.numPulsesSched = argNumPulsesSched;
  }

  /**
   * Gets the value of schedType
   *
   * @return the value of schedType
   */
  public int getSchedType()  {
    return this.schedType;
  }

  /**
   * Sets the value of schedType
   *
   * @param argSchedType Value to assign to this.schedType
   */
  public void setSchedType(int argSchedType) {
    this.schedType = argSchedType;
  }


}
