package ray.renderer;

import ray.misc.Color;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class PhongShader implements Renderer {
	
	private double phongCoeff = 1.5;
	
	public PhongShader() { }
	
	public void setAlpha(double a) {
		phongCoeff = a;
	}
	
	@Override
	public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
			int sampleIndex, Color outColor) {
		// W4160 TODO (A)
		// Here you need to implement the basic phong reflection model to calculate
		// the color value (radiance) along the given ray. The output color value 
		// is stored in outColor. 
		// 
		// For such a simple rendering algorithm, you might not need Monte Carlo integration
		// In this case, you can ignore the input variable, sampler and sampleIndex.
	}
}
