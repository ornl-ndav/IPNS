package IPNS.Control;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ParamEditor extends JFrame implements ActionListener{
    JTabbedPane tabby = new JTabbedPane();
    int numRows;
    ParameterFile pFile; 
    ParamPane userPane = new ParamPane();
    ParamPane instPane = new ParamPane();

    public ParamEditor(){
	JMenu menu = new JMenu("File");
	menu.setMnemonic('F');
	menu.add( makeMenuItem("Open") );
	menu.add( makeMenuItem("Save") );
	menu.add( makeMenuItem("Save As") );
	menu.add( makeMenuItem("Close") );
	menu.add( makeMenuItem("Exit") );
	JMenuBar menuBar = new JMenuBar();
	menuBar.add( menu );
	setJMenuBar( menuBar );

	tabby.addTab("User Parameters", null, userPane, "UserParameters");
	tabby.addTab("Inst Parameters", null, instPane, "Inst Parameters");
	getContentPane().add(tabby);
	pack();
    }

    private JMenuItem makeMenuItem( String name ) {
	JMenuItem m = new JMenuItem( name );
	m.addActionListener( this );
	return m;
    }

    /**
     */
    private void OpenFile(){
	System.out.println("Open method");
	String dataFile;
	final JFileChooser fc = new JFileChooser("./");
	//        fc.addChoosableFileFilter(new RunfileFilter());
	int fcReturn = fc.showOpenDialog(this);
	if (fcReturn == JFileChooser.APPROVE_OPTION ) {
	  File file = fc.getSelectedFile();
	  dataFile = file.getPath();
	  }
	else
	  return;
	//	try {
	  System.out.println("Opening: " + dataFile);
	pFile = new ParameterFile(dataFile);
	if ( pFile.numUserParameters > pFile.numInstParameters ) {
	    numRows = pFile.numUserParameters;
	}
	else {
	    numRows = pFile.numInstParameters;
	}
	ParamPane userPane = new ParamPane( "User", pFile.getDeviceName(),
						pFile.getUserParameters(),
						numRows);
	ParamPane instPane = new ParamPane( "Inst", pFile.getDeviceName(),
						pFile.getInstParameters(), 
						numRows );
	userPane.showDbEntry(true);
	instPane.showDbEntry(true);
	tabby.setComponentAt(0, userPane);
	tabby.setComponentAt(1, instPane);
	setTitle( "Parameter Editor - " + dataFile);
	
    }

    /**
     */
    private void SaveFile(){
	System.out.println("Save method");
	try{
	    pFile.save(pFile.getFileName());
	}
	catch (IOException ex) {
	    System.out.println("trouble saving file");
	}
    }

    /**
     */
    private void SaveAsFile(){
	System.out.println("Save As method");
	String dataFile = null;
	final JFileChooser fc = new JFileChooser();
	//	fc.addChoosableFileFilter(new DS5Filter());
	fc.setSelectedFile(new File(pFile.getFileName()));
	int fcReturn = fc.showSaveDialog(this);
	if (fcReturn == JFileChooser.APPROVE_OPTION ) {
	    File file = fc.getSelectedFile();
	    dataFile = file.getPath();
	}
 	else
	    return;
	try {
	    pFile.save(dataFile);
	    setTitle( "Parameter Editor - " + dataFile);
	}
	catch ( Exception ex ) {
	    System.out.println("Trouble saving file");
	}
    }

    /**
     */
    private void CloseFile() {
	System.out.println("Close method");
    }

    /**
     */
    private void ExitFile() {
	System.out.println("Exit method");
	System.exit(0);
    }

    /**
     */
    public void actionPerformed ( ActionEvent e ) {
        String command = e.getActionCommand();
	if (command.equals("Open") ) {
	    OpenFile();
	}
	else if ( command.equals("Save") ) {
	    SaveFile();
	}
	else if ( command.equals("Save As") ) {
	    SaveAsFile();
	}
	else if ( command.equals("Close") ) {
	}
	else if ( command.equals("Exit") ) {
	    ExitFile();
	}

    }

    public static void main(String[] args) {
	ParamEditor mainFrame = new ParamEditor(); 
    }

}
