package IPNS.Calib;
import IPNS.Calib.*;
import java.io.*;

public class DC5Merge {

    public DC5Merge(){}


    public static void main(String[] args) throws IOException{
	DC5 File1In = new DC5();
	DC5 File2In = new DC5();
	DC5 FileOut = new DC5();
	int nDet_1;
	String calibFileName_1;
	String iName_1;
	int[] psdOrder_1;
	int[] numSegs1_1;
	int[] numSegs2_1;
	int[] dataSource_1;
	int[] minID_1;
	float[] angles_1, height_1, flightPath_1, length_1, width_1, depth_1,
	    efficiency_1;
	float[] rot1_1, rot2_1;
	int[] type_1, coordSys_1;
	int[] crate_1, slot_1, input_1;

	int nDet_2;
	String calibFileName_2;
	String iName_2;
	int[] psdOrder_2;
	int[] numSegs1_2;
	int[] numSegs2_2;
	int[] dataSource_2;
	int[] minID_2;
	float[] angles_2, height_2, flightPath_2, length_2, width_2, depth_2, 
	    efficiency_2;
	float[] rot1_2, rot2_2;
	int[] type_2, coordSys_2;
	int[] crate_2, slot_2, input_2;

	int nDet_3;
	String calibFileName_3;
	String iName_3;
	int[] psdOrder_3;
	int[] numSegs1_3;
	int[] numSegs2_3;
	int[] dataSource_3;
	int[] minID_3;
	float[] angles_3 , height_3, flightPath_3, length_3, width_3, depth_3, 
	    efficiency_3;
	float[] rot1_3, rot2_3;
	int[] type_3, coordSys_3;
	int[] crate_3, slot_3, input_3;
	
	if (args.length != 3) {
	    System.out.println();
	    System.exit(0);
	}

	try{
	    File1In = new DC5(args[0]);
	}
	catch (IOException ex) {
	    System.out.println(" Trouble Opening File1");
	    System.exit(0);
	}
	try{
	    File2In = new DC5(args[1]);
	}
	catch (IOException ex) {
	    System.out.println(" Trouble Opening File2");
	    System.exit(0);
	}
	
	nDet_1 = File1In.NDet();
	angles_1 = File1In.Angles();
	height_1 = File1In.Height();
	flightPath_1 = File1In.FlightPath();
	rot1_1 = File1In.Rot1();
	rot2_1 = File1In.Rot2();
	type_1 = File1In.Type();
	length_1 = File1In.Length();
	width_1 = File1In.Width();
	depth_1 = File1In.Depth();
	efficiency_1 = File1In.Efficiency();
	coordSys_1 = File1In.CoordSys();
	crate_1 = File1In.Crate();
	slot_1 = File1In.Slot();
	input_1 = File1In.Input();
	psdOrder_1 = File1In.PsdOrder();
	numSegs1_1 = File1In.NumSegs1();
	numSegs2_1 = File1In.NumSegs2();
	dataSource_1 = File1In.DataSource();
	minID_1 = File1In.MinID();
	
       
	nDet_2 = File2In.NDet();
	angles_2 = File2In.Angles();
	height_2 = File2In.Height();
	flightPath_2 = File2In.FlightPath();
	rot1_2 = File2In.Rot1();
	rot2_2 = File2In.Rot2();
	type_2 = File2In.Type();
	length_2 = File2In.Length();
	width_2 = File2In.Width();
	depth_2 = File2In.Depth();
	efficiency_2 = File2In.Efficiency();
	coordSys_2 = File2In.CoordSys();
	crate_2 = File2In.Crate();
	slot_2 = File2In.Slot();
	input_2 = File2In.Input();
	psdOrder_2 = File2In.PsdOrder();
	numSegs1_2 = File2In.NumSegs1();
	numSegs2_2 = File2In.NumSegs2();
	dataSource_2 = File2In.DataSource();
	minID_2 = File2In.MinID();
	
	nDet_3 = nDet_1 + nDet_2;
	angles_3 = new float[nDet_3];
	height_3 = new float[nDet_3];
	flightPath_3 = new float[nDet_3];
	rot1_3 = new float[nDet_3];
	rot2_3 = new float[nDet_3];
	type_3 = new int[nDet_3];
	length_3 = new float[nDet_3];
	width_3 = new float[nDet_3];
	depth_3 = new float[nDet_3];
	efficiency_3 = new float[nDet_3];
	coordSys_3 = new int[nDet_3];
	crate_3 = new int[nDet_3];
	slot_3 = new int[nDet_3];
	input_3 = new int[nDet_3];
	psdOrder_3 = new int[nDet_3];
	numSegs1_3 = new int[nDet_3];
	numSegs2_3 = new int[nDet_3];
	dataSource_3 = new int[nDet_3];
	minID_3 = new int[nDet_3];
	
	System.arraycopy( angles_1, 0, angles_3, 0, nDet_1);
	System.arraycopy( angles_2, 0, angles_3, nDet_1, nDet_2);
	System.arraycopy( height_1, 0, height_3, 0, nDet_1);
	System.arraycopy( height_2, 0, height_3, nDet_1, nDet_2);
	System.arraycopy( flightPath_1, 0, flightPath_3, 0, nDet_1);
	System.arraycopy( flightPath_2, 0, flightPath_3, nDet_1, nDet_2);
	System.arraycopy( rot1_1, 0, rot1_3, 0, nDet_1);
	System.arraycopy( rot1_2, 0, rot1_3, nDet_1, nDet_2);
	System.arraycopy( rot2_1, 0, rot2_3, 0, nDet_1);
	System.arraycopy( rot2_2, 0, rot2_3, nDet_1, nDet_2);
	System.arraycopy( type_1, 0, type_3, 0, nDet_1);
	System.arraycopy( type_2, 0, type_3, nDet_1, nDet_2);
	System.arraycopy( length_1, 0, length_3, 0, nDet_1);
	System.arraycopy( length_2, 0, length_3, nDet_1, nDet_2);
	System.arraycopy( width_1, 0, width_3, 0, nDet_1);
	System.arraycopy( width_2, 0, width_3, nDet_1, nDet_2);
	System.arraycopy( depth_1, 0, depth_3, 0, nDet_1);
	System.arraycopy( depth_2, 0, depth_3, nDet_1, nDet_2);
	System.arraycopy( efficiency_1, 0, efficiency_3, 0, nDet_1);
	System.arraycopy( efficiency_2, 0, efficiency_3, nDet_1, nDet_2);
	System.arraycopy( coordSys_1, 0, coordSys_3, 0, nDet_1);
	System.arraycopy( coordSys_2, 0, coordSys_3, nDet_1, nDet_2);
	System.arraycopy( crate_1, 0, crate_3, 0, nDet_1);
	System.arraycopy( crate_2, 0, crate_3, nDet_1, nDet_2);
	System.arraycopy( slot_1, 0, slot_3, 0, nDet_1);
	System.arraycopy( slot_2, 0, slot_3, nDet_1, nDet_2);
	System.arraycopy( input_1, 0, input_3, 0, nDet_1);
	System.arraycopy( input_2, 0, input_3, nDet_1, nDet_2);
	System.arraycopy( psdOrder_1, 0, psdOrder_3, 0, nDet_1);
	System.arraycopy( psdOrder_2, 0, psdOrder_3, nDet_1, nDet_2);
	System.arraycopy( numSegs1_1, 0, numSegs1_3, 0, nDet_1);
	System.arraycopy( numSegs1_2, 0, numSegs1_3, nDet_1, nDet_2);
	System.arraycopy( numSegs2_1, 0, numSegs2_3, 0, nDet_1);
	System.arraycopy( numSegs2_2, 0, numSegs2_3, nDet_1, nDet_2);
	System.arraycopy( dataSource_1, 0, dataSource_3, 0, nDet_1);
	System.arraycopy( dataSource_2, 0, dataSource_3, nDet_1, nDet_2);

	FileOut.setNDet(nDet_3);
	FileOut.setAngles(angles_3);
	FileOut.setHeight(height_3);
	FileOut.setFlightPath(flightPath_3);
	FileOut.setRot1(rot1_3);
	FileOut.setRot2(rot2_3);
	FileOut.setType(type_3);
	FileOut.setLength(length_3);
	FileOut.setWidth(width_3);
	FileOut.setDepth(depth_3);
	FileOut.setEfficiency(efficiency_3);
	FileOut.setCoordSys(coordSys_3);
	FileOut.setCrate(crate_3);
	FileOut.setSlot(slot_3);
	FileOut.setInput(input_3);
	FileOut.setPsdOrder(psdOrder_3);
	FileOut.setNumSegs1(numSegs1_3);
	FileOut.setNumSegs2(numSegs2_3);
	FileOut.setDataSource(dataSource_3);
	FileOut.setMinID();

	FileOut.Save(args[2]);
    }
}
