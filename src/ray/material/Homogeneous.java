package ray.material;

import ray.brdf.BRDF;
import ray.brdf.Lambertian;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

/**
 * A homogeneous reflecting material, which has the same BRDF at all locations.
 * 
 * @author srm
 */
public class Homogeneous implements Material {
	
	BRDF brdf = new Lambertian();
	
	public Homogeneous() { }

	public void setBRDF(BRDF brdf) { this.brdf = brdf; }

	public BRDF getBRDF(IntersectionRecord iRec) {
		return brdf;
	}

	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		outRadiance.set(0, 0, 0);
	}

	public boolean isEmitter() {
		return false;
	}

}
