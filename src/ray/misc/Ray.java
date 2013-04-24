package ray.misc;

import ray.math.Point3;
import ray.math.Vector3;

/**
 * A ray is simply an origin point and a direction vector.
 *
 * @author ags
 */
public class Ray {

  /**
   * This quantity represents a "small amount" to handle comparisons in
   * floating point computations.  It is often useful to have one global
   * value across the ray tracer that stands for a "small amount".
   */
  public static final double EPSILON = 1e-6;

  /** The starting point of the ray. */
  public final Point3 origin = new Point3();

  /** The normalized direction in which the ray travels. */
  public final Vector3 direction = new Vector3();

  /**
  * It is convenient to have a ray have a start and end t values.
  * The start value lets the ray be offset slightly from surfaces
  * avoiding self intersection through numerical inaccuracies, and
  * the end value lets rays be cut off at certain points (such
  * as for shadow rays).
  */

  /** Starting t value of the ray **/
  public double start;

  /** Ending t value of the ray **/
  public double end;

  /**
   * Default constructor generates a trivial ray.
   */
  public Ray() {}

  /**
   * The explicit constructor.  This is the only constructor with any real
   * code in it.  Values should be set here, and any variables that need to
   * be calculated should be done here.
   * @param newOrigin The origin of the new ray.
   * @param newDirection The direction of the new ray.
   */
  public Ray(Point3 newOrigin, Vector3 newDirection) {

    origin.set(newOrigin);
    direction.set(newDirection);
  }
  
  public Ray(Ray ray) {
	  this.origin.set(ray.origin);
	  this.direction.set(ray.direction);
	  this.start = ray.start;
	  this.end = ray.end;
  }

  /**
   * Sets this ray with the given direction and origin.
   * @param newOrigin the new origin point
   * @param newDirection the new direction vector
   */
  public void set(Point3 newOrigin, Vector3 newDirection) {

    origin.set(newOrigin);
    direction.set(newDirection);
  }

  /**
   * Sets this ray with the given origin and destination.
   * @param origin the new origin point
   * @param destination the point the ray hits at t = 1
   */
  public void set(Point3 origin, Point3 destination) {

    this.origin.set(origin);
    direction.sub(destination, origin);
  }

  /**
   * Sets outPoint to the point on this ray t units from the origin.  Note that t can
   * be considered as distance along this ray only if the ray direction is normalized.
   * @param outPoint the output point
   * @param t The distance along the ray.
   */
  public void evaluate(Point3 outPoint, double t) {
    outPoint.set(origin);
    outPoint.scaleAdd(t, direction);
  }

  /**
   * Moves the origin of the ray EPISILON units along ray.  Avoids self intersection
   * when casting rays from surfaces.
   */
  public void makeOffsetRay() {

    start = EPSILON;
    end = Double.POSITIVE_INFINITY;

  }

  /**
   * Offsets the ray origin by EPSILON and sets the end point of the ray to t.
   * @param newEnd the endpoint of the ray.
   */
  public void makeOffsetSegment(double newEnd) {

    start = EPSILON;
    end = newEnd;


  }
}