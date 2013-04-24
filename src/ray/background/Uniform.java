package ray.background;

import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

/**
 * A uniform (constant) background that has the same radiance everywhere.
 * 
 * @author srm
 */
public class Uniform implements Background {
	
	Color radiance = new Color(0, 0, 0);
	
	public Uniform() { }
	
	public Color getRadiance() {
		return radiance;
	}
	
	public void setRadiance(Color radiance) {
		this.radiance.set(radiance);
	}

	public void evaluate(Vector3 direction, Color outColor) {
		outColor.set(radiance);
	}

	public void generate(Point2 seed, Vector3 outDirection) {
		Geometry.squareToSphere(seed, outDirection);
	}

	public double pdf(Vector3 direction) {
		return 1 / (4 * Math.PI);
	}

}
