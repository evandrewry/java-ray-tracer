package ray.misc;

import ray.math.Frame3;
import ray.math.Vector3;
import ray.surface.Surface;

public class LuminaireSamplingRecord {
	
	/** A reference to the surface the point came from */
	public Surface surface = null;
	
	/** The surface frame on the luminaire at the chosen point */
	public Frame3 frame = new Frame3();
	
	/** 
	 * The direction toward the shading point, in canonical coordinates
	 * from lRec.o to iRec.o
	 */
	public Vector3 emitDir = new Vector3();
	
	/** The pdf with which the point was chosen (wrt. surface area on luminaire) */
	public double pdf;
	
	public double iCosine;
	public double lCosine;
	
	/** The ray between the shading point and the luminaire */
	public Ray shadowRay = new Ray();
	
	/** Copy another LuminaireSamplingRecord. */
	public void set(LuminaireSamplingRecord lRec) {
		frame.set(lRec.frame);
		emitDir.set(lRec.emitDir);
		pdf = lRec.pdf;
		iCosine = lRec.iCosine;
		lCosine = lRec.lCosine;
	}
	
	/** Set this record up from an intersection -- could copy
	 * texture coordinates or other needed information */
	public void set(IntersectionRecord iRec) {
		surface = iRec.surface;
		frame.set(iRec.frame);
	}
}
