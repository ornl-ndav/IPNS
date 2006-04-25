package IPNS.Runfile;

import java.io.*;

/** This class is intended to provide a means for obtaining a short (80 character)
   file summary from a run file.

   @author John Hammonds and Tom Worlton
 */

/*
 *
 * $Log$
 * Revision 1.2  2006/01/01 04:04:38  hammonds
 * Remove unused imports.
 *
 * Revision 1.1  2003/05/07 16:58:40  hammonds
 * Tom version of file summary. FileSummary and RunFileSummary will be merged
 * into RunFileSummary.
 *
 * Revision 1.7  2003/02/19           Worlton
 * stripped out directory searching from "FileSummary.java" to make this class 
 * which only takes a single file and returns a summary.  I also changed the 
 * items chosen to be included in the summary.
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


public class RunFileSummary {
    StringBuffer summaryContents;

    /** Input a file directory and create a File Summary for all run files in 
	that directory.  i.e. all files with .run or .RUN extensions. */

    
    public RunFileSummary( String fileName )throws IOException {
	summaryContents = new StringBuffer();
// The next statement is only really useful when the file is a directory.
//	summaryContents.append("Summary of " + fileName +"\n");
	File file = new File (fileName);
	if ( file.isFile() ) {
	    try {
		StringBuffer sb1 = new StringBuffer(80);
		RandomAccessRunfile runFile = new RandomAccessRunfile(fileName, "r");
		Header head = new Header(runFile);
		
		sb1.append( file.getName() );
		while ( sb1.length() < 15 )
		    sb1.append(" ");
            sb1.append( head.startDate ); // Tom
		sb1.append( " " + head.userName.trim() );
		while ( sb1.length() < 43 )
		    sb1.append(" ");
//            sb1.append( head.numOfHistograms + " Histograms " );	//Tom
		sb1.append( head.runTitle.trim() );
            int lbuff = sb1.length();
            if(lbuff > 78)
              sb1.delete(78, lbuff);
//		sb1.append( "\n" );
		summaryContents.append( sb1.toString() );
		head = null;
		runFile.close();
		
	    }
	    catch (IOException ex) {
		summaryContents.append( "****ERROR: " + fileName + 
					" does not appear to be a valid "+
					"runfile\n" );
	    }
	}
	else {
	    summaryContents.append( fileName + " is not a valid file or "
				    + "directory");
	}
    }
    
    public String getSummary() {
	return summaryContents.toString();
    }


    public static void main (String [] args) throws IOException {
	if ( args.length == 0) args = new String[] {".."};
	RunFileSummary  fileSum = new RunFileSummary( args[0]);
	System.out.println( fileSum.getSummary());
    }
}
