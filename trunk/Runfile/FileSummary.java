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
	try {
	    File dirMain = new File (dirName);

  
	File[] dirList = dirMain.listFiles( new RunFileNameFilter() );
	dirList = sortFiles(dirList);
	if ( dirList != null ) {
	    for (int ii = 0; ii < dirList.length; ii++ ){
		//     		System.out.println( dirList[ii].getPath()  );
		if ( dirList[ii].isFile() ){
		    StringBuffer sb1 = new StringBuffer();
		    Runfile rfile = new Runfile ( dirList[ii].getPath() );
		    sb1.append( dirList[ii].getName() );
		    while ( sb1.length() < 18 )
			sb1.append(" ");
		    sb1.append( rfile.UserName().trim() );
		    while ( sb1.length() < 62 )
		        sb1.append(" ");
		    sb1.append( rfile.RunTitle() );
		    sb1.append( "\n" );
		    summaryContents.append( sb1.toString() );
		}
		else if (dirList[ii].isDirectory() ){ }
		
	    }
	}
	else {
	    System.out.println( "No run files found");
	}
	}
	
	catch ( IOException ex ) {	    System.out.println( "error with directory " + dirName );
	    return;
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
		     > 0) {
		    tempFile = inFiles[jj];
		    inFiles[jj] = inFiles[jj+1];
		    inFiles[jj+1] = tempFile;
		}
	    }
	}
	return inFiles;
    }


    public static void main (String [] args) throws IOException {
	if ( args.length == 0) args = new String[] {".."};
	FileSummary  fileSum = new FileSummary( args[0]);
	System.out.println( fileSum.getSummary());
    }

 

}
