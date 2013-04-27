package ray.renderer;

import ray.material.Material;
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

	/* making these 'global' for efficiency since all of this code is serial */
    private Point2 seed = new Point2();
    private Vector3 L = new Vector3();
    private Vector3 R = new Vector3();
    private Color emittedRadiance = new Color();
    private Color directRadiance = new Color();
    private IntersectionRecord iRec = new IntersectionRecord();
    private LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();

    
    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;

    /**
     * The default is to compute using uninformed sampling wrt. 
     * projected solid angle over the hemisphere.
     */
    public DirectOnlyRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }

    /**
     * This allows the rendering algorithm to be selected from the
     * input file by substituting an instance of a different
     * class of DirectIlluminator.
     *
     * @param direct the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }

    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    	/* --- cast ray and find first intersection --- */
        if (scene.getFirstIntersection(iRec, ray)) {
        	/* if the ray intersects an object in the scene: */

            /* --- compute emitted radiance --- */
            emittedRadiance(iRec, ray.direction, emittedRadiance);

            /* --- compute direct illumination --- */
            /* sample random seed on unit square */
            sampler.sample(1, sampleIndex, seed);
            direct.directIllumination(scene, L, R, iRec, seed, directRadiance);

            /* --- set outColor to sum of computed radiances --- */
            outColor.set(emittedRadiance);
            outColor.add(directRadiance);
            
        } else {
        	/* otherwise, just compute background color */
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
    	/* get the material of the intersected surface */
        Material m = iRec.surface.getMaterial();
        
        if (m.isEmitter()) {
        	/* get the emitted radiance if the material is an emitter */
            lRec.set(iRec);
            lRec.emitDir.set(dir);
            lRec.emitDir.scale(-1);
            iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
        } else {
        	/* emitted radiance is zero if the material is not an emitter */
        	outColor.set(0.0);
        }
    }
}
