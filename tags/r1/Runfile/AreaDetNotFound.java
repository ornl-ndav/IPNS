package IPNS.Runfile;


/**
   This class is an Exception class which will be thrown by Runfile methods
   that try to access data for Area Detectors.
*/

class AreaNotFound extends Exception {
    public AreaNotFound() {
	super();
    }

    public AreaNotFound( String desc ) {
	super( desc );
    }
}
