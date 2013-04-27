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
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 *
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 *
 * @author srm, Changxi Zheng(+)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {
    private LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
    private Color brdf = new Color();
    private Color irradiance = new Color();

    public LuminaireSamplingRecord currentLuminaireSamplingRecord() {
        return lRec;
    }

    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir,
            IntersectionRecord iRec, Point2 seed, Color outColor) {
    	
        Geometry.squareToPSAHemisphere(seed, incDir);
        iRec.frame.frameToCanonical(incDir);
        incDir.normalize();
        
        Vector3 N = new Vector3(iRec.frame.w);
        N.normalize();
        outDir.set(N);
        outDir.scale(2 * incDir.dot(N));
        outDir.sub(incDir);
        outDir.normalize();
        
        Ray reflection = new Ray(iRec.frame.o, incDir);
        reflection.makeOffsetRay();
        IntersectionRecord lightIRec = new IntersectionRecord();
        if (scene.getFirstIntersection(lightIRec, reflection) && lightIRec.surface.getMaterial().isEmitter()) {
            /* get BRDF */
            Material m = iRec.surface.getMaterial();
            m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);

            /* get incident illumination */
            lightIRec.surface.getMaterial().emittedRadiance(lRec, irradiance);
            irradiance.scale(iRec.frame.w.dot(incDir));


            /* compute radiance */
            outColor.set(1.0);
            outColor.scale(brdf);
            outColor.scale(irradiance);
            outColor.scale(Math.PI);

            //System.out.println("brdf: " + brdf + ", irdnc: " + irradiance + ", g: " + illumination);
        } else {
        	outColor.set(0);
        }
    }

}
