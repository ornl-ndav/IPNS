package IPNS.Runfile;



import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Dimension;

/** This Class is intended to provide a prompted input box.  The main 
constructor here takes a String which provides user prompt and an object to 
hold the value.  Valid input objects here are Sting, Float and Integer.  Some 
type checking is done on the Float an Integer inputs. */
public class FieldEdit extends JPanel implements ActionListener, FocusListener{
  Object field = new Object();
  JLabel fieldLabel = new JLabel();
  JTextField fieldValEntry = new JTextField();

  public FieldEdit() {

  }

  public FieldEdit(String fieldName, Object inField) {
    field = inField;
    GridBagLayout lo = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    setLayout( lo );

    fieldLabel = new JLabel(fieldName);
    fieldLabel.setPreferredSize( new Dimension(200, 20));
    c.gridx= 0;
    c.weightx =1;
    lo.setConstraints(fieldLabel, c);
    add(fieldLabel);
    fieldValEntry = new JTextField(field.toString());
    fieldValEntry.setPreferredSize( new Dimension(600, 20));
    fieldValEntry.addActionListener( this );
    fieldValEntry.addFocusListener( this );
    fieldValEntry.setActionCommand( "ValEntry" );
    c.gridx= 1;
    c.weightx =2;
    lo.setConstraints(fieldValEntry, c);
    add(fieldValEntry);
  }
 
  public void actionPerformed ( ActionEvent e ) {
    checkInput();
  }

  public void focusLost( FocusEvent e ) {
    checkInput();
  }

  public void checkInput() {
    if (field instanceof Integer ) {
      try{
      int tint = (new Integer(fieldValEntry.getText())).intValue();
      field = new Integer(tint);
      }
      catch (NumberFormatException ex) {
		JOptionPane.showMessageDialog(this, 
			    "This value must be entered as an integer");
		fieldValEntry.requestFocus();
      }
    }
    if (field instanceof Float ) {
      try{
	float tfloat = (new Float(fieldValEntry.getText())).floatValue();
	field = new Float(tfloat);
      }
      catch (NumberFormatException ex) {
		JOptionPane.showMessageDialog(this, 
			    "This value must be entered as a float");
		fieldValEntry.requestFocus();
      }
    }
    if (field instanceof String ) {
      field = fieldValEntry.getText();
    }
    
  }

  public void focusGained( FocusEvent e ) {
  }


  /**
     This method is used at appropriate times to pass back the value stored
     in the input box.
   */  
  public Object getFieldVal() {
    return field;
  }


  /**
     This is a tester method used to test the three types of inputs.
   */
  public static void main(String[] args) {
    JFrame mainFrame = new JFrame("FieldEdit Tester");
    FieldEdit intEdit = new FieldEdit("Integer Entry", new Integer(1));
    FieldEdit floatEdit = new FieldEdit("Float Entry", new Float(1.0));
    FieldEdit stringEdit = new FieldEdit("String Entry", new String("ONE"));
    GridLayout lo = new GridLayout(3,1);
    mainFrame.getContentPane().setLayout( lo );
    
    mainFrame.getContentPane().add(intEdit);
    mainFrame.getContentPane().add(floatEdit);
    mainFrame.getContentPane().add(stringEdit);
    mainFrame.pack();
    mainFrame.show();
  }
  
}
