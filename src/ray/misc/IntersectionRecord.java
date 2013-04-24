package ray.misc;

import ray.math.Frame3;
import ray.math.Point2;
import ray.surface.Surface;

/**
 * This class is really just a struct, holding necessary information about a
 * particular intersection point.
 *
 * @author ags
 */
public class IntersectionRecord {

  /** A frame at the intersection point, with w aligned with the surface normal. */
  public final Frame3 frame = new Frame3();

  /** The texture coordinates of the intersection point */
  public final Point2 texCoords = new Point2();
  
  /** A reference to the actual surface. */
  public Surface surface = null;

  /** The t value along the ray at which the intersection occurred. */
  public double t = 0;
  
  /**
   * Set this intersection record to the value of inRecord
   *
   * @param inRecord the input record
   */
  public void set(IntersectionRecord inRecord) {

    frame.set(inRecord.frame);
    texCoords.set(inRecord.texCoords);
    surface = inRecord.surface;
    t = inRecord.t;

  }

}