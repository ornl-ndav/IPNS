from java.io import FileInputStream
from java.io import FileOutputStream
from java.lang import Integer
from java.util import Properties
from DataSetTools.operator.Generic import GenericOperator
from java.io import File

class RmRuns(GenericOperator):
    def setDefaultParameters(self):
        self.super__clearParametersVector()
        defInst = System.getProperty("Default_Instrument")
        instChoice = ChoiceListPG("InstrumentName",defInst)
        instChoice.addItem("hrcs")
        instChoice.addItem("qens")
        instChoice.addItem("glad")
        instChoice.addItem("scd0")
        instChoice.addItem("sepd")
        instChoice.addItem("lrcs")
        instChoice.addItem("sand")
        instChoice.addItem("posy")
        instChoice.addItem("pne0")
        instChoice.addItem("sad1")
        instChoice.addItem("hipd")
        instChoice.addItem("gppd")
        instChoice.addItem("chex")
        instChoice.addItem("quip")
        self.addParameter(instChoice)
        self.addParameter(ArrayPG("EnterRunNumbers",None))
        self.addParameter(BooleanPG("ResetLastRun", "true"))
        
    def getResult(self):
        instName = self.getParameter(0).value
        runNums = self.getParameter(1).value
        resetLast = self.getParameter(2).value

        dataDir = System.getProperty("Data_Directory")
        userDir = System.getProperty("user.home")
        instDir = "%s/inst"%(userDir)
        print instDir
        parFileName = "%s/%s__V5.par"%(instDir,instName)
        print parFileName
        for x in runNums:
            sRun = ""
            if x < 1000:
                sRun = "%04d"%x
            else:
                sRun = "%d"%x
            fname = '%s/%s%s.run'%( dataDir, instName, sRun)
            rFile = File(fname)
            if rFile.delete():
                print "Removed: " + fname
            else:
                print "Problem removing: " + fname

        if resetLast:
            try:
                print parFileName
                parFile = FileInputStream(parFileName)
                pars = Properties()
                pars.load(parFile)
                parFile.close()
                
                newLast = min(runNums)-1    
                oldLast=pars.getProperty("LastRun")
                print 'oldLast' + oldLast
                print runNums
                print 'newLast' + newLast.toString()
                pars.setProperty("LastRun", Integer.toString(newLast) )
                oldLast=pars.getProperty("LastRun")
                print oldLast
                parOutFile = FileOutputStream(parFileName)
                pars.save(parOutFile,"#Saved by RmRuns")
                parOutFile.close()
            except IOException:
                print "IOException reading parFile in RmRuns"

# Override Category to dictate where this appears in the Macro menu
    def getCategoryList(self):
        cat = ["Operator","DAS","System_Maintenance"]
        return cat
    
    def __init__(self):
        Operator.__init__(self, "RmRuns")
