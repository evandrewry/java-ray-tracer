package ray.material;

import ray.brdf.BRDF;
import ray.brdf.Lambertian;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

/**
 * A Lambertian emitter, which emits the same radiance at all points and in all directions.
 * 
 * @author srm
 */
public class LambertianEmitter implements Material {
	
	Color radiance = new Color();
	BRDF brdf = new Lambertian(new Color(0, 0, 0));
	
	public LambertianEmitter() { }
	
	public void setBRDF(BRDF brdf) { this.brdf = brdf; }
	public void setRadiance(Color emittedRadiance) { this.radiance.set(emittedRadiance); }

	public BRDF getBRDF(IntersectionRecord iRec) {
		return brdf;
	}

	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		outRadiance.set(radiance);
	}

	public boolean isEmitter() {
		return true;
	}

}
