package IPNS.Runfile;


/**
   This class is an Exception class which will be thrown by Runfile methods
   that try to access data for Linear Position Sensitive Detectors.
*/

public class LpsdNotFound extends Exception {
    public LpsdNotFound() {
	super();
    }

    public LpsdNotFound( String desc ) {
	super( desc );
    }
}
