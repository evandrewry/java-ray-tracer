package ray.renderer;

import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;

/**
 * This class computes direct illumination at a surface by the simplest possible
 * approach: it estimates the integral of incident direct radiance using Monte
 * Carlo integration with a uniform sampling distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for
 * other methods, and it is a useful base class because it contains the
 * generally useful <incidentRadiance> function.
 * 
 * @author srm, Changxi Zheng(+)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {

	/* making these 'global' since all of this code is serial */
	private LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
	private IntersectionRecord lightIRec = new IntersectionRecord();
	private Color brdf = new Color();
	private Color irradiance = new Color();
	private Vector3 normal = new Vector3();
	private Ray sample = new Ray();

	public LuminaireSamplingRecord currentLuminaireSamplingRecord() {
		return lRec;
	}

	public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir,
			IntersectionRecord iRec, Point2 seed, Color outColor) {

		/* normal at intersection is w of the intersection record's frame */
		normal.set(iRec.frame.w);
		normal.normalize();

		/* unit square sample -> unit hemisphere sample */
		Geometry.squareToPSAHemisphere(seed, incDir);

		/* convert sample direction to world coords, normalize */
		iRec.frame.frameToCanonical(incDir);
		incDir.normalize();

		/*
		 * find reflection across normal at intersection point
		 * R = 2N * (L.N) - L
		 */
		outDir.set(normal);
		outDir.scale(2 * incDir.dot(normal));
		outDir.sub(incDir);
		outDir.normalize();

		/*
		 * cast a ray at the sample direction and see if it intersects an
		 * emitter
		 */
		sample.set(iRec.frame.o, incDir);
		sample.makeOffsetRay();
		if (scene.getFirstIntersection(lightIRec, sample)
				&& lightIRec.surface.getMaterial().isEmitter()) {
			/*
			 * if our surface is directly illuminated, calculate the rendering
			 * equation terms
			 */

			/* get BRDF */
			Material m = iRec.surface.getMaterial();
			m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);

			/* get incident illumination */
			lightIRec.surface.getMaterial().emittedRadiance(lRec, irradiance);
			irradiance.scale(iRec.frame.w.dot(incDir));

			/* compute direct illumination */
			outColor.set(1.0);
			outColor.scale(brdf);
			outColor.scale(irradiance);
			outColor.scale(Math.PI);

		} else {

			/* otherwise, there is no illumination from this sample direction */
			outColor.set(0);
		}
	}

}
