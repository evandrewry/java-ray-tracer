package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
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
    private Color illumination = new Color();

    public LuminaireSamplingRecord currentLuminaireSamplingRecord() {
        return lRec;
    }

    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir,
            IntersectionRecord iRec, Point2 seed, Color outColor) {

        if ( scene.chooseVisiblePointOnLuminaire(seed, iRec, lRec) ) {
            incDir.set(lRec.emitDir); // from lRec to iRec
            incDir.normalize();
            incDir.scale(-100.0);


            /* get BRDF */
            Material m = iRec.surface.getMaterial();
            m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);

            /* get incident illumination */
            lRec.surface.getMaterial().emittedRadiance(lRec, irradiance);
            //irradiance.scale(lRec.surface.pdfSamplePoint(iRec.frame.o, lRec));
            irradiance.scale(iRec.frame.w.dot(incDir));
           // irradiance.scale(iRec.frame.w.dot(incDir));


            /* compute radiance */
            illumination.set(1.0);
            illumination.scale(brdf);
            illumination.scale(irradiance);
            //illumination.scale(irradiance);
            illumination.scale(lRec.pdf);

            //System.out.println(lRec.pdf);
            //System.out.println("brdf: " + brdf + ", irdnc: " + irradiance + ", g: " + illumination);
//            g.scale(Math.cos(theta));
        } else {
            /* get BRDF */
//            Material m = iRec.surface.getMaterial();
//            m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);
//
//            Vector3 w = new Vector3(incDir);
//            w.scale(-1.);
//            w.normalize();
//            outColor.set(0);

            /* compute radiance */
            illumination.set(0);
            //radiance.scale(incident);
            //g.scale(1 / Math.PI);
//            radiance.scale(w.dot(new Vector3(iRec.frame.o)));
        }
        outColor.set(illumination);

        // W4160 TODO (A)
    	// This method computes a Monte Carlo estimate of reflected radiance due to direct illumination.  It
        // generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
        //
        // The same code could be interpreted as an integration wrt. solid angle, as follows:
        //
        //    f = brdf * radiance * cos_theta
        //    p = cos_theta / pi
        //    g = f / p = brdf * radiance * pi
    	//
    	// As a hint, here are a few steps when I code this function
    	// 1. Generate a random incident direction according to proj solid angle
        //    pdf is constant 1/pi
    	// 2. Find incident radiance from that direction
    	// 3. Estimate reflected radiance using brdf * radiance / pdf = pi * brdf * radiance
    }

}
