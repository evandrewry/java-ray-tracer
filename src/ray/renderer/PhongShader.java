package ray.renderer;

import ray.brdf.Lambertian;
import ray.light.PointLight;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Color;
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
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
        IntersectionRecord iRec = new IntersectionRecord();

        if (scene.getFirstIntersection(iRec, ray)) {
            outColor.set(0);
            Point3 vertex = new Point3();
            ray.evaluate(vertex, iRec.t);

            Vector3 N = new Vector3(iRec.frame.w);
            N.normalize();
            //N.scale(-1);

            Vector3 V = new Vector3(ray.direction);
            Ray cameraRay = new Ray();
            scene.getCamera().getRay(cameraRay, iRec.texCoords.x, iRec.texCoords.y);
            V.set(cameraRay.direction);
            V.normalize();
            // V.scale(-1.);

            for (PointLight pl : scene.getPointLights()) {
                Vector3 L = new Vector3(pl.location);
                L.sub(new Vector3(vertex));
                L.normalize();
                //L.scale(-1);



                if (L.dot(N) > 0) {
                    //find reflection direction
                    Vector3 R = new Vector3(N);
                    R.scale(2 * L.dot(N));
                    R.sub(L);
                    R.normalize();

                    //add diffuse
                    Color diffuse = new Color(/*pl.diffuse*/);
                    iRec.surface.getMaterial().getBRDF(iRec).evaluate(iRec.frame, L, R, diffuse);
                    diffuse.scale(1 / Math.PI);
                    diffuse.scale(L.dot(N));
                    diffuse.scale(pl.diffuse);
                    //diffuse.clamp(0, 1);
                    outColor.add(diffuse);




                    Color specular = new Color(/*pl.specular*/);
                    //iRec.surface.getMaterial().getBRDF(iRec).evaluate(iRec.frame, L, R, specular);
                    //specular.scale(1 / Math.PI);
                    specular.set(pl.specular);

                    specular.scale(Math.pow(R.dot(V), phongCoeff));
//                    specular.scale(pl.specular);
                //    specular.scale(0.2);
                    //specular.clamp(0, 1);

                    //outColor.add(specular);
                    //outColor.clamp(0, 1);
                }
            }


            return;
        }

        scene.getBackground().evaluate(ray.direction, outColor);

        // W4160 TODO (A)
        // Here you need to implement the basic phong reflection model to calculate
        // the color value (radiance) along the given ray. The output color value
        // is stored in outColor.
        //
        // For such a simple rendering algorithm, you might not need Monte Carlo integration
        // In this case, you can ignore the input variable, sampler and sampleIndex.
    }

}
