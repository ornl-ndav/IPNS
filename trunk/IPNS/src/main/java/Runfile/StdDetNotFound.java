package IPNS.Runfile;


/**
   This class is an Exception class which will be thrown by Runfile methods
   that try to access data for Standard 1D Detectors.
*/

class StdDetNotFound extends Exception {
    public StdDetNotFound() {
	super();
    }

    public StdDetNotFound( String desc ) {
	super( desc );
    }
}
