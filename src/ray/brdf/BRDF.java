package ray.brdf;

import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

/**
 * A BRDF gives information about the directional scattering properties of an opaque
 * surface.  It is a function of two directions, and it is generally used by integrating
 * over the hemisphere wrt. one direction while keeping the other direction fixed.
 * For that usage, this interface has the standard form for a function that will be used
 * in importance sampling -- the samples that are chosen are values of one direction for
 * a fixed value of the other direction.  Because of reciprocity it does not matter which
 * direction is incident and which is reflected.
 * 
 * @author srm
 *
 */
public interface BRDF {
    
    /**
     * Evaluate the BRDF.  Must be reciprocal (invariant to swapping arguments).
     * @param frame The surface frame
     * @param incDir The incident direction
     * @param reflDir The reflected direction
     * @param outBRDFValue The value of the BRDF
     */
    public void evaluate(Frame3 frame, Vector3 incDir, Vector3 reflDir, Color outBRDFValue);
    
    /**
     * Generate a sample on the hemisphere drawn from a distribution suitable for 
     * importance sampling this BRDF.  The probability density is reported with respect
     * to the solid angle measure.
     * @param frame The surface frame
     * @param fixedDir The fixed argument of the BRDF
     * @param outDir The random sample for the variable argument
     * @param outWeight The weight for an unbiased estimate (value / pdf)
     */
    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 outDir, 
                         Point2 seed, Color outWeight);
    
   /**
     * Evaluate the PDF used by generate(), with respect to the solid angle measure.
     * @param frame The surface frame
     * @param fixedDir The fixed argument of the BRDF
     * @param dir The variable argument
     * @return The pdf of generate() choosing <dir> if given <fixedDir>
     */
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir);

}
