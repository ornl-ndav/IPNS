from IPNS.Operators import *
from IPNS.Runfile import Runfile

class PrintSchedule(GenericOperator):

    def setDefaultParameters(self):
        defInst = System.getProperty("Default_Instrument")
        instChoice = ChoiceListPG("Instrument Name",defInst)
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
        instChoice.addItem("sasi")
        instChoice.addItem("hipd")
        instChoice.addItem("gppd")
        instChoice.addItem("chex")
        instChoice.addItem("quip")
        self.addParameter(instChoice)
        self.addParameter(IntegerPG("Enter Run Number: ", 0))

    def getResult(self):
        inst = self.getParameter(0).value
        dd = Datadir(inst)

        dataDir = dd.getResult()
        runNum = self.getParameter(1).value
        fName =""
        sRun = ""
        if runNum < 1000:
            sRun = "%04d"%(runNum)
        else:
            sRun = "%d"%(runNum)
        fName = "%s/%s%s.run"%(dataDir,inst,sRun)
        runfile = Runfile(fName)
        firstRun = runfile.FirstRun()
        curRun = firstRun
        if curRun < 1000:
            sRun = "%04d"%(curRun)
        else:
            sRun = "%d"%(curRun)
        fName = "%s/%s%s.run"%(dataDir,inst,sRun)
        runfile = Runfile(fName)
        done = 0
        while done != 1:
            numCyclesElapsed = runfile.NumOfCyclesCompleted()
            numCyclesPreset = runfile.NumOfCyclesPreset()
            numMonitorElapsed = runfile.ElapsedMonitorCounts()
            numMonitorPreset = runfile.PresetMonitorCounts()
            userName = runfile.UserName()
            numPulses = runfile.NumOfPulses()
            runTitle = runfile.RunTitle()
            print "********************************************"
            print "Instrument: ", inst, "  Run #: ", curRun
            print "User: ", userName
            print "RunTitle:"
            print runTitle
            print "Presets: ", numCyclesPreset, " Cycles of ", numMonitorPreset
            print numCyclesElapsed, " cycles completed"
            print numMonitorElapsed, " monitored completed"
            print "\nTotal Pulses Completed: :", numPulses
            nextRun = runfile.NextRun()
            lastRun = runfile.LastRun()
            print "********************************************"
            if curRun == lastRun:
                done = 1
            else:
                curRun = nextRun
                if curRun < 1000:
                    sRun = "%04d"%(curRun)
                else:
                    sRun = "%d"%(curRun)
                    fName = "%s/%s%s.run"%(dataDir,inst,sRun)
                    runfile = Runfile(fName)

    def __init__(self):
        Operator.__init__(self,"PrintSchedule")

