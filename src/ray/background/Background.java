package ray.background;

import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

/**
 * A background is responsible for assigning radiances to rays that don't hit any geometry.
 * This interface has the standard form for a function that can be importance sampled.
 * It could be extended to allow for stratification or to give the sampling code access to 
 * some information about the shading point to enable better sampling methods.
 * 
 * @author srm
 */
public interface Background {
	
	/**
	 * Find the background radiance.
	 * @param direction The direction in which we are looking
	 * @param outRadiance The radiance seen in that direction
	 */
	public void evaluate(Vector3 direction, Color outRadiance);
	
	/**
	 * Generate a sample from a distribution suitable for importance sampling 
	 * this background.
	 * @param seed A random point in the unit square
	 * @param outDirection The chosen direction direction
	 */
	public void generate(Point2 seed, Vector3 outDirection);
	
	/**
	 * Evaluate the pdf used by generate().
	 * @param direction The direction in question
	 * @return The pdf of generate() choosing <direction>
	 */
	public double pdf(Vector3 direction);

}
