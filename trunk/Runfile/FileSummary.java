package IPNS.Runfile;


import java.io.*;
import IPNS.Runfile.*;


/** This class is intended to provide a means for searching run files inside of
   a directory in order to find runs based on header info such as user name and
   run title

   @author John Hammonds
 */

/*
 *
 * $Log$
 * Revision 1.7  2003/02/27 19:52:08  pfpeterson
 * Changed default directory to look in to be '.' rather than '..'
 *
 * Revision 1.6  2002/12/13 14:23:45  hammonds
 * Took out println's.  Make necessary diagnostics go into the File Summary
 *
 * Revision 1.5  2002/12/13 02:59:16  hammonds
 * Added better error checking.  Used to catch an error and then bomb.  Now an error with a runfile is caught and reported and it goes on.
 *
 * Added support for using a file argument.
 *
 * If argument does not match a valid file or directory, this is also reported.
 *
 * Revision 1.4  2002/07/10 14:31:05  hammonds
 * Changed to get info from Header object directly.  Before info was pulled from Runfile object which gets more from file.
 *
 * Revision 1.3  2002/07/02 17:05:01  hammonds
 * Changed how much space was alloted for User names
 *
 * Revision 1.2  2002/07/02 16:57:35  hammonds
 * Changed Sort order
 *
 * Revision 1.1  2002/07/02 16:48:28  hammonds
 * Class added to print out username and title for runfiles in a given directory
 *
 */


public class FileSummary {
    StringBuffer summaryContents;

    /** Input a file directory and create a FileSummary for all run files in 
	that directory.  i.e. all files with .run or .RUN extensions. */

    
    public FileSummary( String dirName )throws IOException {
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
			    RandomAccessFile runFile = 
				new RandomAccessFile( dirList[ii].getPath(), 
						      "r" );
			    Header head = new Header(runFile);
			    
			    sb1.append( dirList[ii].getName() );
			    while ( sb1.length() < 18 )
				sb1.append(" ");
			    sb1.append( head.userName.trim() );
			    while ( sb1.length() < 42 )
				sb1.append(" ");
			    sb1.append( head.runTitle );
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
		RandomAccessFile runFile = new RandomAccessFile(dirName, "r");
		Header head = new Header(runFile);
		
		sb1.append( dirMain );
		while ( sb1.length() < 18 )
		    sb1.append(" ");
		sb1.append( head.userName.trim() );
		while ( sb1.length() < 42 )
		    sb1.append(" ");
		sb1.append( head.runTitle );
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
	FileSummary  fileSum = new FileSummary( args[0]);
	System.out.println( fileSum.getSummary());
    }

 

}
