package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Scene;

/**
 * This class computes direct illumination by integrating over the area of the
 * luminaires. It calls the scene to generate a random point on a luminaire, and
 * it uses the pdf (wrt. area) returned by the luminaire sampling code together
 * with the area integration formulation of local illumination.
 * 
 * @author cxzheng
 */
public class LuminairesIlluminator extends DirectIlluminator {

	private LuminaireSamplingRecord lumRec = new LuminaireSamplingRecord();
	private Color brdfVal = new Color();

	public LuminaireSamplingRecord currentLuminaireSamplingRecord() {
		return lumRec;
	}

	/**
	 * Computes radiance due to direct reflection and writes it to <outColor>.
	 * 1. Get Illuminaires in the current scene 2. Randomly select a
	 * Illuminaires according to their areas 3. Randomly select a point from
	 * that Illuminaire
	 * 
	 * @param scene
	 * @param outDir
	 *            The exitant direction (canonical coordinates)
	 * @param iRec
	 *            Information about the shading point
	 * @param seed
	 *            Stratified random point
	 * @param outColor
	 *            Reflected radiance is written to this color
	 */
	public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir,
			IntersectionRecord iRec, Point2 seed, Color outColor) {
		double dist;
		if (scene.chooseVisiblePointOnLuminaire(seed, iRec, lumRec)) {
			Material material = iRec.surface.getMaterial();
			BRDF brdf = material.getBRDF(iRec);
			if (brdf != null) {
				// compute BRDF value
				incDir.set(lumRec.emitDir); // from lRec to iRec
				incDir.scale(-1);
				incDir.normalize();
				brdf.evaluate(iRec.frame, incDir, outDir, brdfVal);

				// compute incident radiance
				scene.incidentRadiance(iRec.frame.o, incDir, outColor);
				// lumRec.surface.getMaterial().emittedRadiance(lumRec,
				// outColor);

				dist = lumRec.emitDir.squaredLength();
				outColor.scale(brdfVal);
				outColor.scale(-lumRec.iCosine * lumRec.lCosine
						/ (dist * lumRec.pdf)); // one extra "dist" to normalize
												// consine values

				return;
			}
		}
		outColor.set(0, 0, 0);
	}

}
