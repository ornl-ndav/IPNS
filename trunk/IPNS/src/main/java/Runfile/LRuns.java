package IPNS.Runfile;


import java.io.*;
//import IPNS.Runfile.*;


/** This class is intended to provide a means for searching run files inside of
   a directory in order to find runs based on header info such as user name and
   run title

   @author John Hammonds
 */

/*
 *
 * $Log$
 * Revision 1.2  2004/10/11 14:45:30  hammonds
 * Add start date & end date
 *
 * Revision 1.1  2004/07/09 16:25:48  hammonds
 * A copy of File Summary to support the needs of GPPD.
 *
 *
 */


public class LRuns {
    StringBuffer summaryContents;

    /** Input a file directory and create a LRuns for all run files in 
	that directory.  i.e. all files with .run or .RUN extensions. */

    
    public LRuns( String dirName )throws IOException {
	summaryContents = new StringBuffer();
	summaryContents.append("Summary of " + dirName +"\n");
	File dirMain = new File("");

	dirMain = new File (dirName);
	if ( dirMain.isDirectory()) {
	    File[] dirList = dirMain.listFiles( new RunFileNameFilter() );
	    if ( dirList != null && dirList.length != 0) {
		dirList = sortFiles(dirList);
		for (int ii = 0; ii < dirList.length; ii++ ){
		    try {
			if ( dirList[ii].isFile() ){
			    StringBuffer sb1 = new StringBuffer();
			    RandomAccessRunfile runFile = 
				new RandomAccessRunfile( dirList[ii].getPath(), 
						      "r" );
			    Header head = new Header(runFile);
			    
			    sb1.append( dirList[ii].getName() );
			    while ( sb1.length() < 18 )
				sb1.append(" ");
			    sb1.append( head.userName.trim() );
			    while ( sb1.length() < 42 )
				sb1.append(" ");
			    sb1.append( head.runTitle.trim() );
		            while (sb1.length() < 122 ) 
		                sb1.append(" ");
		            sb1.append( head.numOfPulses) ;
		            while (sb1.length() < 132 ) 
		                sb1.append(" ");
			    sb1.append( head.startDate );
		            while (sb1.length() < 142 ) 
		                sb1.append(" ");
			    sb1.append( head.endDate );
			    sb1.append( "\n" );
			    summaryContents.append( sb1.toString() );
			    head = null;
			    runFile.close();
			}
			else if (dirList[ii].isDirectory() ){ }
			
		    }
		    catch (IOException ex) {
		    summaryContents.append
			    ( new String( "*******ERROR " +
					  "with file " +
					  dirList[ii].getPath() + "\n")
			      );
		    }
		}
	    }
	    else {
		summaryContents.append("No run files found.\n");
	    }
	}
	else if ( dirMain.isFile() ) {
	    try {
		StringBuffer sb1 = new StringBuffer();
		RandomAccessRunfile runFile = new RandomAccessRunfile(dirName, "r");
		Header head = new Header(runFile);
		
		sb1.append( dirMain );
		while ( sb1.length() < 18 )
		    sb1.append(" ");
		sb1.append( head.userName.trim() );
		while ( sb1.length() < 42 )
		    sb1.append(" ");
		sb1.append( head.runTitle );
		while (sb1.length() < 122 ) 
		    sb1.append(" ");
		sb1.append( head.numOfPulses) ;
		while (sb1.length() < 132 ) 
		  sb1.append(" ");
		sb1.append( head.startDate );
		while (sb1.length() < 142 ) 
		  sb1.append(" ");
		sb1.append( head.endDate );
		sb1.append( "\n" );
		summaryContents.append( sb1.toString() );
		head = null;
		runFile.close();
		
	    }
	    catch (IOException ex) {
		summaryContents.append( "****ERROR: " + dirName + 
					" does not appear to be a valid "+
					"runfile\n" );
	    }
	}
	else {
	    summaryContents.append( dirName + " is not a valid file or "
				    + "directory");
	}
    }
    
    public String getSummary() {
	return summaryContents.toString();
    }

    private File[] sortFiles(File[] inFiles ){
	File tempFile;

	for ( int ii=0; ii< inFiles.length; ii++) {
	    for ( int jj = 0; jj<( inFiles.length - 1 -ii); jj++) {
		if ( inFiles[jj+1].getName().compareTo(inFiles[jj].getName())
		     < 0) {
		    tempFile = inFiles[jj];
		    inFiles[jj] = inFiles[jj+1];
		    inFiles[jj+1] = tempFile;
		}
	    }
	}
	return inFiles;
    }


    public static void main (String [] args) throws IOException {
	if ( args.length == 0) args = new String[] {"."};
	LRuns  fileSum = new LRuns( args[0]);
	System.out.println( fileSum.getSummary());
    }

 

}
