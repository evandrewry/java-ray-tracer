package ray.material;

import ray.brdf.BRDF;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

/**
 * A material is responsible for returning an emitted radiance and a BRDF
 * at any point on a surface.
 * 
 * @author ags +latest $Author: srm $
 */
public interface Material {
	
	/**
	 * The material given to all surfaces unless another is specified.
	 */
	public static Material DEFAULT_MATERIAL = new Homogeneous();
	
	/**
	 * Get the BRDF of this material at the intersection described in iRec.
	 * @param record The intersection record, which holds the location, normal, etc.
	 * @return The BRDF for this material at this point
	 */
	public BRDF getBRDF(IntersectionRecord iRec);
	
	/**
	 * Get the emitted radiance of this material at the location and direction described
	 * in lRec.
	 * @param lRec The luminaire sampling record.  Within this structure, frame and emitDir
	 *    are inputs to this method.
	 * @param outRadiance The radiance emitted in that direction
	 */
	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance);
	
	/**
	 * Identify this material as emitting or not (for purposes of direct lighting).
	 * @return 
	 */
	public boolean isEmitter();
	
}