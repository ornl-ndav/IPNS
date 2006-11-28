# File: Schedule.py
#
# Copyright 2003 John P. Hammonds 
# Intense Pulsed Neutron Source
# Argonne National Laboratory
# 9700 S. Cass Ave.
# Argonne IL, 60439
#
# $Log$
# Revision 1.4  2006/11/28 22:50:16  hammonds
# Fix problem with scroll bars for large # of runs.
#
# Revision 1.3  2004/01/29 17:55:29  hammonds
# Override default category
#
# Revision 1.2  2003/09/18 20:40:50  hammonds
# Fix problem with doubling parameters
#
#
# Script Schedule modifies the schedule parameters for a specified sequence of
#   runs.  Sets the sequence up so that the runs can be collected automatically
#   one after the other.
# User is first prompted for instrument name and a list of run numbers.
# The user is then prompted to input schedule parameters for each of the
# specified runs.
#
# Assumptions:
#    This script is intended for use by the instrument account.  It assumes
#    that the file ~user/inst/xxxx.dat (xxxx holds the instrument prefix)
#    contains a line that defines the directory where instruments run files
#    are.
# 
from IPNS.Operators import *
from javax.swing import JOptionPane
from javax.swing import JLabel
from javax.swing import JScrollPane
from javax.swing import JPanel
from javax.swing import JTextField
from java.awt import GridBagLayout
from java.awt import GridBagConstraints
from java.awt import Dimension
from java.awt.event import ActionEvent
from IPNS.Runfile import RunfileBuilder
from IPNS.Runfile import Runfile

class Schedule(GenericOperator):
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
        self.addParameter(ArrayPG("Enter Run Numbers: ", None))

    def getResult(self):
        inst = self.getParameter(0).value
        dd = Datadir(inst)

        dataDir = dd.getResult()
        runNums = self.getParameter(1).value
        print inst
        print dataDir
        print runNums

        scrollPane = JScrollPane()
        mainPanel =  JPanel()
#        mainPanel.setPreferredSize(Dimension(500,300))
#        mainPanel.setSize(Dimension(500,300))
        scrollPane.setPreferredSize(Dimension(500,300))
        scrollPane.setSize(Dimension(500,300))
        mainPanel.layout = GridBagLayout()
        mainWindow = JOptionPane(scrollPane)
        scrollPane.setViewportView(mainPanel)
        c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 0
        c.gridwidth = 1
        fName = []
        presetCycle = []
        presetMonitor = []
        numCycle = []
        numMonitor = []
        n = len(runNums)
        ii = range(n)
        for x in ii :
            sRun =""
            if x < 1000 :
                sRun = "%04d"%(runNums[x])
            else:
                sRun = "%d"%(runNums[x])
            fName.append( "%s/%s%s.run"%(dataDir,inst,sRun))
            runfile = Runfile(fName[x])
            presetCycle.append( runfile.NumOfCyclesPreset())
            presetMonitor.append(runfile.PresetMonitorCounts())
            fieldLabel = JLabel(fName[x])
            c.gridx = 0
            mainPanel.add(fieldLabel,c)
            numCycle.append(JTextField(presetCycle[x].toString(),10, actionPerformed=self.actionPerformed, focusLost=self.focusLost))
            numCycle[x].setActionCommand("numCyc%d"%(x))
            c.gridx = 1
            mainPanel.add(numCycle[x], c)
            numMonitor.append( JTextField(presetMonitor[x].toString(),10, actionPerformed=self.actionPerformed, focusLost=self.focusLost))
            numMonitor[x].setActionCommand("numMon%d"%(x))
            c.gridx = 2
            mainPanel.add(numMonitor[x],c)
            c.gridy = c.gridy + 1
        dialog = mainWindow.createDialog(mainWindow.getParent(),"Schedule")
        dialog.show()
        dialog.dispose()
        for x in ii:
            print fName[x]
            print "Num Cycles: ",presetCycle[x], "Num Monitored: ", presetMonitor[x]
            print "Num Cycles: ",numCycle[x].getText(), "Num Monitored: ", numMonitor[x].getText()
            rFile = RunfileBuilder(fName[x])
            rFile.ModifyHeaderElement("firstRun", runNums[0])
            rFile.ModifyHeaderElement("lastRun", runNums[n-1])
            if x < n-1 :
                rFile.ModifyHeaderElement("nextRun", runNums[x+1])
            else:
                rFile.ModifyHeaderElement("nextRun", runNums[x])
            rFile.ModifyHeaderElement("presetMonitorCounts",
                                      int(numMonitor[x].getText()) )
            rFile.ModifyHeaderElement("numOfCyclesPreset",
                                      int(numCycle[x].getText()) )
            
    def chkText(self,text):
        iNumOut=0
        try:
            iNumOut = int(text)
        except ValueError:
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Integer input Only")
            
#    def getDocumentation():
#        strOut = 
        
    def actionPerformed(self, e):
        numOut = e.getSource().getText()
        self.chkText(numOut)
        
    def focusLost(self, e):
        numOut = e.getSource().getText()
        self.chkText(numOut)
        
# Override Category to dictate where this appears in the Macro menu
    def getCategoryList(self):
        cat = ["Operator","DAS","Modify"]
        return cat
    
    def __init__(self):
        Operator.__init__(self,"Schedule")

