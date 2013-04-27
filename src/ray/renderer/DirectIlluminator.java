package ray.renderer;

import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Scene;

public abstract class DirectIlluminator {

	private Vector3 _incDir = new Vector3();

	/**
	 * Computes radiance due to direct reflection and writes it to <outColor>.
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
	public void directIllumination(Scene scene, Vector3 outDir,
			IntersectionRecord iRec, Point2 seed, Color outColor) {
		directIllumination(scene, _incDir, outDir, iRec, seed, outColor);
	}

	public abstract void directIllumination(Scene scene, Vector3 incDir,
			Vector3 outDir, IntersectionRecord iRec, Point2 seed, Color outColor);
}
