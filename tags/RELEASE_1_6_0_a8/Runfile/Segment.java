package IPNS.Runfile;


import java.io.*;


/** This class is designed to store/feedback information on a segment of a
multi-segment detector.  This concept will be used for standard (single 
element tubes, LPSDs and Area detectors.
@author  John P Hammonds
*/
/*
 *
 * $Log$
 * Revision 1.2  2001/08/03 19:02:53  hammonds
 * Added a segment ID to uniquely identify each element.
 *
 * Revision 1.1  2001/06/27 22:19:32  hammonds
 * Added to help implement area & lpsd data.
 *
 *
 */

public class Segment {
    int detID;
    int row;
    int column;
    int segID;
    float length;
    float width;
    float depth;
    float efficiency;

    
    public Segment() {
    }

    public Segment( int detID, int row, int column, float length, float width,
		   float depth, float Efficiency, int segID ) {
	this.detID = detID;
	this.row = row;
	this.column = column;
	this.length = length;
	this.width = width;
	this.depth = depth;
	this.efficiency = efficiency;
	this.segID = segID;
    }
    /**
       This method returns the detector ID for this segment
       @return detID
     */
    public int DetID(){
	return detID;
    }
    
    /**
       This method returns the row number for locating this segment of the
       detector
       @return row
     */
    public int Row(){
	return row;
    }
    
    /**
       This method returns the column number for locating this segment of the
       detector
       @return column
     */
    public int Column(){
	return column;
    }
    
    /**
       This method returns the length (vertical dimension) of this segment of 
       the detector
       @return length
     */
    public float Length() {
	return length;
    }

    /**
       This method returns the width (horizontal dimension) of this segment of
       the detector
       @return width
     */
    public float Width() {
	return width;
    }

    /**
       This method returns the depth (in plane dimension) of this segment of
       the detector
       @return depth
     */
    public float Depth() {
	return depth;
    }

    /**
       This method returns the efficiency of this segment of the detector
       @return efficiency
     */
    public float Efficiency() {
	return efficiency;
    }

    /**
       This method returns the segment ID of this segment of the detector
       @return efficiency
     */
    public int SegID() {
	return segID;
    }

    


}
