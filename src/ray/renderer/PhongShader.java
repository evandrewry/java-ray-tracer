package ray.renderer;

import ray.light.PointLight;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class PhongShader implements Renderer {

	private double phongCoeff = 1.5;

	public PhongShader() {
	}

	public void setAlpha(double a) {
		phongCoeff = a;
	}

	@Override
	public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
			int sampleIndex, Color outColor) {
		Vector3 N, V, L, R;
		Color diffuse, specular;
		IntersectionRecord iRec = new IntersectionRecord();

		if (scene.getFirstIntersection(iRec, ray)) {
			outColor.set(0);

			N = new Vector3(iRec.frame.w);
			N.normalize();

			Ray view = new Ray();
			scene.getCamera().getRay(view, iRec.texCoords.x, iRec.texCoords.y);
			V = new Vector3(view.direction);
			V.normalize();

			R = new Vector3();
			L = new Vector3();

			diffuse = new Color();
			specular = new Color();

			for (PointLight pl : scene.getPointLights()) {

				L.sub(pl.location, iRec.frame.o);

				if (L.dot(N) > 0) {
					// find reflection direction
					R.set(N);
					R.scale(2 * L.dot(N));
					R.sub(L);
					R.normalize();

					// add diffuse
					iRec.surface.getMaterial().getBRDF(iRec)
							.evaluate(iRec.frame, L, R, diffuse);
					diffuse.scale(1 / Math.PI);
					diffuse.scale(L.dot(N));
					diffuse.scale(pl.diffuse);
					// diffuse.scale(0.1);
					outColor.add(diffuse);

					// add specular
					iRec.surface.getMaterial().getBRDF(iRec)
							.evaluate(iRec.frame, L, R, specular);
					specular.scale(1 / Math.PI);
					specular.scale(Math.pow(R.dot(V), phongCoeff));
					specular.scale(pl.specular);
					outColor.add(specular);
				}
			}
		} else {
			scene.getBackground().evaluate(ray.direction, outColor);
		}
	}

}
