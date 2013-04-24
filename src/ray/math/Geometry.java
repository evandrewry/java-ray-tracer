package ray.math;

/**
 * Class to contain static methods for various useful geometric computations.
 * 
 * @author srm
 */
public class Geometry {
	
	/**
	 * @param seed A point randomly distributed in the unit square
	 * @param outDir A vector randomly distributed over the hemisphere wrt. proj. solid angle
	 */
	static public void squareToPSAHemisphere(Point2 seed, Vector3 outDir) {
		double r = Math.sqrt(seed.x);
		double phi = 2 * Math.PI * seed.y;
		outDir.x = r * Math.cos(phi);
		outDir.y = r * Math.sin(phi);
		outDir.z = Math.sqrt(1 - r*r);
	}
	
	/**
	 * @param seed A point randomly distributed in the unit square
	 * @param outDir A vector randomly distributed over the hemisphere wrt. solid angle (area)
	 */
	static public void squareToHemisphere(Point2 seed, Vector3 outDir) {
		outDir.z = seed.x;
		double phi = 2 * Math.PI * seed.y;
		double xyRad = Math.sqrt(1 - outDir.z*outDir.z);
		outDir.x = xyRad * Math.cos(phi);
		outDir.y = xyRad * Math.sin(phi);
	}

	/**
	 * @param seed A point randomly distributed in the unit square
	 * @param outDir A vector randomly distributed over the sphere wrt. solid angle (area)
	 */
	static public void squareToSphere(Point2 seed, Vector3 outDir) {
		outDir.z = 2 * seed.x - 1;
		double phi = 2 * Math.PI * seed.y;
		double xyRad = Math.sqrt(1 - outDir.z*outDir.z);
		outDir.x = xyRad * Math.cos(phi);
		outDir.y = xyRad * Math.sin(phi);
	}

	/**
	 * Here we use a better way to sample in the triangle, because
	 * it doesn't use squre root which is computationally expensive.
	 * 
	 * @param seed A point randomly distributed in the unit square
	 * @param baryPt A 2D point randomly distributed over the triangle 
	 *   (0,0), (1,0), (0,1).  Its coordinates are the beta and gamma
	 *   barycentric coordinates for an arbitrary triangle.
	 */
	public static void squareToTriangle(Point2 seed, Point2 baryPt) {
		if ( seed.x + seed.y <= 1. ) {
			baryPt.set(seed);
			return;
		}
		baryPt.x = 1. - seed.x;
		baryPt.y = 1. - seed.y;
	}
	
	/**
	 * Map from square to disc using Shirley's concentric mapping.
	 * @param seed A point randomly distributed in the unit square
	 * @param polarPt A 2D point randomly distributed over the unit disc, 
	 *   in polar coordinates (polarPt.x is radius, polarPt.y is angle).
	 */
	public static void squareToPolarDisc(Point2 seed, Point2 polarPt) {
		double x = 2 * seed.x - 1,  y = 2 * seed.y - 1;
		double r, th;
		if (Math.abs(x) > Math.abs(y)) {
			r = x;
			th = Math.PI/4 * y / x;
		} else if (y != 0) {
			r = y;
			th = -Math.PI/4 * x / y + Math.PI/2;
		} else {
			r = th = 0;
		}
		if (r < 0) {
			r = -r;
			th += Math.PI;
		}
		polarPt.set(r, th);
	}
	
	public static void main(String[] args) {
		final int N = 10;
		Point2 seed = new Point2();
		Point2 polarPt = new Point2();
		for (int i = 0; i <= N; i++)
			for (int j = 0; j <= N; j++) {
				seed.set(i / (float) N, j / (float) N);
				squareToPolarDisc(seed, polarPt);
				System.out.println(polarPt.x + " " + polarPt.y);
			}
	}
	
}
