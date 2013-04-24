package ray.renderer;

import ray.misc.Color;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

/**
 * A Renderer is an object that can compute the radiance of a ray.
 * All details about how this is accomplished are hidden.
 * 
 * @author srm
 *
 */

public interface Renderer {

	void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor);
}
