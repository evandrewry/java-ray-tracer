package ray.brdf;

import ray.math.Frame3;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

/**
 * A Lambertian (constant) BRDF, which has value R/pi where R is the
 * reflectance.
 * 
 * @author srm
 */
public class Lambertian implements BRDF {

	// The material's diffuse reflectance (the fraction of incident irradiance
	// reflected, for any incident distribution).
	Color reflectance = new Color(0.5, 0.5, 0.5);

	// For the benefit of the parser
	public Lambertian() {
	}

	public void setReflectance(Color reflectance) {
		this.reflectance.set(reflectance);
	}

	public Lambertian(Color reflectance) {
		this.reflectance.set(reflectance);
	}

	public void evaluate(Frame3 frame, Vector3 incDir, Vector3 reflDir,
			Color outBRDFValue) {
		outBRDFValue.set(reflectance);
		outBRDFValue.scale(1 / Math.PI);
	}

	public void generate(Frame3 frame, Vector3 fixedDir, Vector3 dir,
			Point2 seed, Color outWeight) {
		Geometry.squareToPSAHemisphere(seed, dir);
		frame.frameToCanonical(dir);
		outWeight.set(reflectance);
	}

	/**
	 * @param frame
	 *            frame comes from IntersectionRecord instance, where w
	 *            component of this frame align with the surface normal.
	 * @see ray.brdf.BRDF#pdf(ray.math.Frame3, ray.math.Vector3,
	 *      ray.math.Vector3)
	 */
	public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
		return fixedDir.dot(frame.w) / Math.PI;
	}

}
