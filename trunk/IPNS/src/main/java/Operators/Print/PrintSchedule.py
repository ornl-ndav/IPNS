from IPNS.Operators import *
from IPNS.Runfile import Runfile
from DataSetTools.util import SharedData

class PrintSchedule(GenericOperator):

    def setDefaultParameters(self):
        self.super__clearParametersVector()
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
        if firstRun != 0:
            curRun = firstRun
        else:
            curRun = runNum
            SharedData.addmsg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" )
            SharedData.addmsg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" )
            SharedData.addmsg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"  )
            SharedData.addmsg( ">>>>                                                   >>>")
            SharedData.addmsg(">>>>                                                   >>>" )
            SharedData.addmsg( ">>>>    Not enough info to find what came before this  >>>" )
            SharedData.addmsg(">>>>                                                   >>>" )
            SharedData.addmsg( ">>>>                                                   >>>")
            SharedData.addmsg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" )
            SharedData.addmsg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"  )
            SharedData.addmsg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" )
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
            SharedData.addmsg( "********************************************")
            temp = "Instrument: %s  Run #: %d"%(inst, curRun)
            SharedData.addmsg(temp)
            SharedData.addmsg("User: " + userName )
            SharedData.addmsg("RunTitle:")
            SharedData.addmsg( runTitle )
            temp = "Presets: %d Cycles of %d"%(numCyclesPreset,numMonitorPreset)
            SharedData.addmsg(temp )
            SharedData.addmsg("%d cycles completed"%(numCyclesElapsed)  )
            SharedData.addmsg( "%d  monitored completed"%(numMonitorElapsed))
            SharedData.addmsg( "Total Pulses Completed: %d"%(numPulses) )
            nextRun = runfile.NextRun()
            lastRun = runfile.LastRun()
            if lastRun == 0 and  nextRun == curRun:
                lastRun = curRun
            SharedData.addmsg( "********************************************")
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

# Override Category to dictate where this appears in the Macro menu
    def getCategoryList(self):
        cat = ["Operator","DAS","Print"]
        return cat
    
    def getDocumentration(self):
        docs = StringBuffer()
        docs.append("@overview This Operator is intended for use by the ")
        docs.append("instrument account.  This operator assumes that there ")
        docs.append("is a file xxxx.dat (xxxx is the instrument prefix) ")
        docs.append("which contains a line such as dataDir=/home/xxxx/data")
        docs.append(" that gives the location of runfiles.  This operator ")
        docs.append("looks at the firstRun element in the header of the ")
        docs.append("specified run file.  It then starts at firstRun and ")
        docs.append("steps through each run in the sequence (looking at each ")
        docs.append("nextRun) and prints out the schedule paramenters for ")
        docs.append("run.")


    def __init__(self):
        Operator.__init__(self,"Print Schedule")
