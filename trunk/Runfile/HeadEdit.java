package IPNS.Runfile;

import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import java.awt.GridLayout;
import java.awt.Dimension;


/**
   This class is intended to operate as a program for modifying certain fields
   in the header of an IPNS runfile.  The main method here is the launch
   point for this application.  The program here takes the file name of an
   IPNS runfile and provides a screen for editing the values.  A file menu 
   with a save and exit are provided.  At present Exit provides no check if
   things have changed.
 */
public class HeadEdit extends JScrollPane implements ActionListener {
  Header header;
  String fileName;
  FieldEdit uNameEnter;
  FieldEdit rTitleEnter;
  FieldEdit sampDistEnter;
  FieldEdit chopDistEnter;

  public HeadEdit() {
    header = new Header();
  }

  public HeadEdit(String headerFileName) throws IOException {
    setPreferredSize(new Dimension(850, 200));
    fileName = headerFileName;
    RandomAccessRunfile runFile = 
      new RandomAccessRunfile( headerFileName, "r" );
    header = new Header(runFile);
    uNameEnter = new FieldEdit("UserName", new String(header.userName.trim()));
    rTitleEnter = new FieldEdit("Run Title", new String(header.runTitle.trim()));
    sampDistEnter = 
      new FieldEdit("Source to Sample", new Float(header.sourceToSample));
    chopDistEnter = 
      new FieldEdit("Source to Chopper", new Float(header.sourceToChopper));
    GridLayout lo = new GridLayout(4,1);
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(lo);
    mainPanel.add(uNameEnter);
    mainPanel.add(rTitleEnter);
    mainPanel.add(sampDistEnter);
    mainPanel.add(chopDistEnter);
    setViewportView(mainPanel);
    runFile.close();
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("Save") ) {
      String uname = (String)uNameEnter.getFieldVal();
      String rtitle = (String)rTitleEnter.getFieldVal();
      Float sampdist = (Float)sampDistEnter.getFieldVal();
      Float chopdist = (Float)chopDistEnter.getFieldVal();
      
      StringBuffer tbuff = new StringBuffer(uname);
      tbuff.setLength(20);
      header.userName = new String(tbuff);
      tbuff = new StringBuffer(rtitle);
      tbuff.setLength(800);
      header.runTitle = new String(tbuff);
      header.sourceToSample = (double)sampdist.floatValue();
      header.sourceToChopper = (double)chopdist.floatValue();
      header.Print();
      try {
	RandomAccessRunfile runFile = new RandomAccessRunfile(fileName, "rw");
	header.Write(runFile);
      }
      catch (IOException ie) {
	System.out.println("Trouble opening file for write");
      }
    }
    else if (command.equals("Exit") ) {
      System.exit(0);
    }
  }

  public static void main(String[] args) {
    JFrame mainFrame = new JFrame("Header Editor");
    HeadEdit editor = new HeadEdit();
    try {
      editor = new HeadEdit( args[0] );
    }
    catch (IOException e ) {
      System.out.println("File not found");
    }
    mainFrame.getContentPane().add(editor);
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);
    fileMenu.add( editor.makeMenuItem("Save"));
    fileMenu.add( editor.makeMenuItem("Exit"));
    mainFrame.setJMenuBar(menuBar);
    mainFrame.pack();
    mainFrame.show();
  }

  public JMenuItem makeMenuItem( String name ) {
    JMenuItem m = new JMenuItem( name );
    m.addActionListener( this );
    return m;
  }

}
