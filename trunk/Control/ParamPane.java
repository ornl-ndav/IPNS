package IPNS.Control;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class ParamPane extends JPanel implements ActionListener {
    Parameter[] params = new Parameter[0];
    ParameterEntry[] paramEntry = new ParameterEntry[0];

    public ParamPane() {
        setMinimumSize(new Dimension(400, 300));
        setSize( getMinimumSize());
        setPreferredSize( getMinimumSize());
	params = new Parameter[1];
	params[0] = new Parameter();
	paramEntry = new ParameterEntry[1];
	paramEntry[0] = new ParameterEntry(params[0]);
	add( paramEntry[0] );
    }

    public ParamPane(String type, String device, Parameter[] params, 
		       int rows){
	this.params = params;
	GridLayout lo = new GridLayout(rows,1);
	setLayout( lo );
	int height = params.length * 20;
        setMinimumSize(new Dimension(400, height));
        setMaximumSize(new Dimension(400, height));
        setSize( getMinimumSize());
        setPreferredSize( getMinimumSize());

	paramEntry = new ParameterEntry[params.length];
	for (int ii=0; ii < params.length; ii++) {
	    paramEntry[ii] = new ParameterEntry(params[ii]);
	    add( paramEntry[ii] );
	}	    
    }

    public void actionPerformed ( ActionEvent e ) {
        String command = e.getActionCommand();
    }

    public void showDbEntry( boolean on ) {
	for ( int ii = 0; ii < params.length; ii++ ) {
	    paramEntry[ii].showDbEntry(on);
	}
    }


    public static void main(String[] args) {
	ParameterFile pFile = new ParameterFile(args[0]);
	JFrame mainFrame = new JFrame("Parameter Panes");
	
	JTabbedPane tabby = new JTabbedPane();
	int numRows;
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
        tabby.addTab("User Parameters", null, userPane, "User Parameters");
        tabby.addTab("Inst Parameters", null, instPane, "Inst Parameters");

	mainFrame.pack();
	mainFrame.show();
    }

}
