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
import ray.sampling.SampleGenerator;

/**
 * A renderer that computes radiance due to emitted and directly reflected light only.
 *
 * @author srm
 */

public class DirectOnlyRenderer implements Renderer {

    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;

    /**
     * The default is to compute using uninformed sampling wrt. projected solid angle over the hemisphere.
     */
    public DirectOnlyRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }

    /**
     * This allows the rendering algorithm to be selected from the input file by substituting an instance of a different
     * class of DirectIlluminator.
     *
     * @param direct the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }

    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
        IntersectionRecord iRec = new IntersectionRecord();

        if (scene.getFirstIntersection(iRec, ray)) {

            /* --- compute emitted radiance --- */
            Color emittedRadiance = new Color();
            emittedRadiance(iRec, ray.direction, emittedRadiance);
            /* -------------------------------- */

            /* --- compute direct illumination --- */
            Point2 directSeed = new Point2();
            sampler.sample(1, sampleIndex, directSeed);     // this random variable is for incident direction

            // Generate a random incident direction
            Vector3 L = new Vector3();
            Geometry.squareToPSAHemisphere(directSeed, L);
            iRec.frame.frameToCanonical(L);

            Vector3 N = new Vector3(iRec.frame.w);
            N.normalize();

            //find reflection direction
            Vector3 R = new Vector3();
            R.set(N);
            R.scale(2 * L.dot(N));
            R.sub(L);
            R.normalize();

            Color directRadiance = new Color();
            direct.directIllumination(scene, L, R, iRec, directSeed, directRadiance);
            /* ------------------------------------ */

            outColor.set(emittedRadiance);
            outColor.add(directRadiance);
        } else {
            scene.getBackground().evaluate(ray.direction, outColor);
        }
    }

    /**
     * Compute the radiance emitted by a surface.
     *
     * @param iRec Information about the surface point being shaded
     * @param dir The exitant direction (surface coordinates)
     * @param outColor The emitted radiance is written to this color
     */
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
        Material m = iRec.surface.getMaterial();
        if (m.isEmitter()) {
            LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
            lRec.set(iRec);
            lRec.emitDir.set(dir);
            lRec.emitDir.scale(-1);
            iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
        }
    }
}
