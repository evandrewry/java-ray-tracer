/*
 * MathExt.java:  Mar 28, 2007
 * Changxi Zheng (cxzheng@cs.cornell.edu)
 */
package carbine;

/**
 * Some extensions about the Java.lang.Math class
 * 
 * @author Changxi Zheng
 */
public class MathExt {
    
    public static final double DOUBLE_EPS = 1e-12;
    // ======= some constants from math.h ========
    public static final double M_2_SQRTPI = 1.12837916709551257390;		// 2 / sqrt(pi)
    public static final double M_1_SQRTPI = M_2_SQRTPI * 0.5;
    
    /**
     * @param v
     * @return 1 if v is positive, -1 if v is negative, 0 for zero
     */
    public static int sign(double v) {
        if ( v > DOUBLE_EPS ) return 1;
        if ( v < -DOUBLE_EPS ) return -1;
        return 0;
    }
    
    /**
     * fractional error in math formula less than 1.2 * 10 ^ -7.
     * although subject to catastrophic cancellation when z in very close to 0
     */
    public static double erf2(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 + 
                                            t * ( 0.09678418 + 
                                            t * (-0.18628806 + 
                                            t * ( 0.27886807 + 
                                            t * (-1.13520398 + 
                                            t * ( 1.48851587 + 
                                            t * (-0.82215223 + 
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    } 
    
    /**
     * A slower version of computing erf function
     * 
     * @param x
     * @return
     */
    public static double erf(double x) {
    	double scale = 1.;
    	if ( x < 0 ) {
    		scale = -1;
    		x = -x;
    	}
    	
    	if ( x >= 5. ) return 1;
    	double s = 1, a = x, t = x, c = -x*x, ret = 0;
    	int n = 0;
    	while ( Math.abs(a) > DOUBLE_EPS ) {
    		ret += a;
    		++ n;
    		s *= n;
    		t *= c;
    		a = t / (s * (2.*n + 1));
    	}
    	return scale*ret*M_2_SQRTPI;
    }
    
    static public void main(String[] args) {    	
    	for(double a = 0;a < 3;a += 0.01) {
    		System.out.println(MathExt.erf(a)+"    "+MathExt.erf2(a)+" >>>> "+(Math.abs(MathExt.erf(a)-MathExt.erf2(a))));
    	}
    }
}
