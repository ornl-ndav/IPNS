package IPNS.Runfile;


/**
This package contains a series of Physical Constants as public static 
constants that can be used by any user.
These Constants were taken from:
"The NIST Reference on Cunstants, Units and Uncertainty
http://physics.nist.gov/cuu/Constants/index.html?/codata86.html

    neutron mass 
         Value                                 1.674 927 16 x 10-27 kg 
         Standard uncertainty                  0.000 000 13 x 10-27 kg 
         Relative standard uncertainty         7.9 x 10-8
         Concise form                          1.674 927 16(13) x 10-27 kg 
   joule-electron volt relationship 
         Value                                 6.241 509 74 x 10+18 eV 
         Standard uncertainty                  0.000 000 24 x 1018 eV 
         Relative standard uncertainty         3.9 x 10-8
         Concise form                          6.241 509 74(24) x 1018 eV
   electron volt-joule relationship 
         Value                                 1.602 176 462 x 10-19 J 
         Standard uncertainty                  0.000 000 063 x 10-19 J 
         Relative standard uncertainty         3.9 x 10-8
         Concise form                          1.602 176 462(63) x 10-19 J
   Avogadro constant 
         Value                                 6.022 141 99 x 1023 mol-1 
         Standard uncertainty                  0.000 000 47 x 1023 mol-1 
         Relative standard uncertainty         7.9 x 10-8
         Concise form                          6.022 141 99(47) x 1023 mol-1
   Bohr magneton in eV/T 
         Value                                 5.788 381 749 x 10-5 eV T-1 
         Standard uncertainty                  0.000 000 043 x 10-5 eV T-1 
         Relative standard uncertainty         7.3 x 10-9
         Concise form                          5.788 381 749(43) x 10-5 eV T-1
   nuclear magneton in eV/T 
         Value                                 3.152 451 238 x 10-8 eV T-1 
         Standard uncertainty                  0.000 000 024 x 10-8 eV T-1 
         Relative standard uncertainty         7.6 x 10-9
         Concise form                          3.152 451 238(24) x 10-8 eV T-1
   neutron magnetic moment to nuclear magneton ratio 
         Value                                -1.913 042 72 
         Standard uncertainty                  0.000 000 45 
         Relative standard uncertainty         2.4 x 10-7
         Concise form                         -1.913 042 72(45)    
   Planck constant 
         Value                                 6.626 068 76 x 10-34 J s 
         Standard uncertainty                  0.000 000 52 x 10-34 J s
         Relative standard uncertainty         7.8 x 10-8
         Concise form                          6.626 068 76(52) x 10-34 J s


@author  John P Hammonds
@version 5.0beta
*/
/* 
 *
 * $Log$
 * Revision 5.0  2000/03/10 04:13:40  hammonds
 * New Class to localize definitions of Physics constants.
 *
 *
 */

public class PhysicalConstants {

    public static final double 
	NEUTRON_MASS = 1.67492716E-27;
    public static final double 
	ELECTRON_VOLT_PER_JOULE = 6.24150974E18;
    public static final double 
	JOULE_PER_ELECTRON_VOLT = 1.602176462E-19;
    public static final double 
	AVOGADRO = 6.02214199E23;
    public static final double 
	BOHR_MAGNETON = 5.788381749E-5;
    public static final double 
	NUCLEAR_MAGNETON = 3.152451238E-8;
    public static final double 
	NEUTRON_MAG_MOMENT_BY_NUCLEAR_MAGNETON = -1.91304272;
    public static final double  
	NEUTRON_MAGNETIC_MOMENT = 
	NUCLEAR_MAGNETON * NEUTRON_MAG_MOMENT_BY_NUCLEAR_MAGNETON;
    public static final double
	eV_FROM_m_PER_sec_CONST = 
	NEUTRON_MASS / ( 2.0 * JOULE_PER_ELECTRON_VOLT );
    public static final double
	meV_FROM_m_PER_microsec_CONST = 
	eV_FROM_m_PER_sec_CONST  / ((1.0E-6)*(1.0E-6)* 1E-3) ;
    //    public static final double ;

    public static void main(String[] args) {
	System.out.println( "Neutron Mass: " + NEUTRON_MASS );
	System.out.println( "eV/J: " + ELECTRON_VOLT_PER_JOULE );
	System.out.println( "J/eV: " + JOULE_PER_ELECTRON_VOLT );
	System.out.println( "Avogadro: " + AVOGADRO  );
	System.out.println( "Bohr Magneton: " + BOHR_MAGNETON);
	System.out.println( "Nuclear Magneton: " + NUCLEAR_MAGNETON);
	System.out.println( "Neutron Magnetic Moment/Nuclear Magneton: " + 
			    NEUTRON_MAG_MOMENT_BY_NUCLEAR_MAGNETON);
	System.out.println( "Neutron Magnetic Moment: " + 
			    NEUTRON_MAGNETIC_MOMENT );
	System.out.println( "Energy from Velocity Const: " + 
			    eV_FROM_m_PER_sec_CONST );
	System.out.println( "Energy from Velocity Const: " + 
			    meV_FROM_m_PER_microsec_CONST );
	    //	System.out.println( ": " + );
    }

}
