package ray.camera;

import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.Ray;

/**
 * Represents a simple camera. The camera has a position in space, location, and
 * a frame, described by back, up, and right. The frame determines how the
 * camera is oriented relative to space. The view of the camera is described by
 * fields of view in each dimension (the size of the angle the camera can see).
 * Typical values are 45-60 degrees, Typically, regardless of the shape of the
 * image, points on the camera plane are described at lying in the square
 * [0,1]x[0,1]. The user generally maps points in the image to camera plane
 * coordinates before making queries of the camera.
 * 
 * @author: Changxi Zheng
 */
public class Camera {

  // Fields to represent the camera frame of reference

  /** The location of the camera in the scene. */
  protected final Point3 location = new Point3();

  /** The direction opposite the direction that the camera is looking. */
  protected final Vector3 back = new Vector3(1, 0, 0);

  /**
   * The right vector of the camera, it points toward the right side of the
   * image.
   */
  protected final Vector3 right = new Vector3(0, 1, 0);

  /** The up vector of the camera, it points toward the top of the image. */
  protected final Vector3 up = new Vector3(0, 0, 1);

  // Camera view descriptors

  /** The full horizontal field of view in degrees. */
  protected double xFOV = 45.0;

  /** The full vertical field of view in degrees. */
  protected double yFOV = 45.0;

  /** Only tangent of 1/2 FOV needed in most calculations, useful to cache it */
  private double tanHalfXFOV;

  /** Only tangent of 1/2 FOV needed in most calculations, useful to cache it */
  private double tanHalfYFOV;

  /**
   * Default constructor, assumes camera uses the default values.
   */
  public Camera() {

    tanHalfXFOV = Math.tan(Math.toRadians(xFOV) / 2);
    tanHalfYFOV = Math.tan(Math.toRadians(yFOV) / 2);

  }

  /**
   * Camera constructor. It is difficult to input the 4 camera position and
   * orientation components in their raw form. What is much more common is to
   * enter two points and a vector, generally called the eye, target and up
   * respectively. The eye is the position of the camera. The target is the
   * point the camera will "look" at. Finally, the up vector defines the
   * vertical direction in the image. An orthonormal basis is contructed from
   * these vectors to describe the camera orientation.
   * 
   * @param eye the camera eye point
   * @param target the camera target point
   * @param up the camera up vector
   * @param inXFOV the new vertical field of view in radians
   * @param inYFOV the new horizontal field of view in radians
   */
  public Camera(Point3 eye, Point3 target, Vector3 up, double inXFOV, double inYFOV) {

    // Set the position and orientation
    lookAt(eye, target, up);

    // Setup the camera image
    xFOV = inXFOV;
    yFOV = inYFOV;
    tanHalfXFOV = Math.tan(Math.toRadians(xFOV) / 2);
    tanHalfYFOV = Math.tan(Math.toRadians(yFOV) / 2);

  }

  /**
   * Sets the FOVs given an aspect ratio and a new YFOV.
   *
   * @param inYFOV the new YFOV
   * @param inAspectRatio the new aspect ratio
   */
  public void setAspectRatioAndYFOV(double inYFOV, double inAspectRatio) {

    yFOV = inYFOV;
    xFOV = 2 * Math.toDegrees(Math.atan(Math.tan(Math.toRadians(yFOV) / 2) * inAspectRatio));
    tanHalfYFOV = Math.tan(Math.toRadians(yFOV) / 2);
    tanHalfXFOV = tanHalfYFOV * inAspectRatio;
  }

  /**
   * Same as above but reuses the current YFOV
   *
   * @param inAspectRatio the new aspect ratio
   */
  public void setAspectRatioKeepYFOV(double inAspectRatio) {

    xFOV = 2 * Math.toDegrees(Math.atan(Math.tan(Math.toRadians(yFOV) / 2) * inAspectRatio));
    tanHalfXFOV = tanHalfYFOV * inAspectRatio;

  }

  /**
   * @return Returns the xFOV.
   */
  public double getXFOV() {

    return this.xFOV;
  }

  /**
   * @param xfov The xFOV to set.
   */
  public void setXFOV(double xfov) {

    this.xFOV = xfov;
    this.tanHalfXFOV = Math.tan(Math.toRadians(xFOV) / 2);

  }

  /**
   * @return Returns the yFOV.
   */
  public double getYFOV() {

    return this.yFOV;
  }

  /**
   * @param yfov The yFOV to set.
   */
  public void setYFOV(double yfov) {

    this.yFOV = yfov;
    this.tanHalfYFOV = Math.tan(Math.toRadians(yFOV) / 2);
  }

  /**
   * Sets the camera to look at target from eye and have an up direction up.
   *
   * @param inEye the new location of the camera
   * @param inTarget the new target of the camera
   * @param inUp the new up direction of the camera
   */
  public void lookAt(Point3 inEye, Point3 inTarget, Vector3 inUp) {

    // Infer the camera parameters from eye, target and up
    location.set(inEye);            // The location is the eye
    back.sub(inEye, inTarget);      // The back vector points from the target to the eye
    back.normalize();               // and is normalized
    up.set(inUp);                   // up is up
    up.normalize();                 // and is normalized
    right.cross(back, up);          // right is the cross of forward and up
    right.normalize();              // and is normalized
    up.cross(back, right);          // but we need to set up to the cross of foward
                                    // and right in case the forward and right vectors
                                    // were not originally perpendicular

  }

  /**
   * Set outRay to be a ray from the camera through the point in the image
   * (xPixel, yPixel). The direction of outRay is normalized after this call.
   *
   * @param outRay The output ray
   * @param inU The u coord of the image point
   * @param inV The v coord of the image point
   */
  public void getRay(Ray outRay, double inU, double inV) {

    // Remap the UV coordinates
    inU = inU * 2 - 1;
    inV = inV * 2 - 1;

    // Set the output ray
    outRay.origin.set(location);
    outRay.direction.set(back);
    outRay.direction.scale(-1);
    outRay.direction.scaleAdd(-inU * tanHalfXFOV, right); // Move the direction
    // along the right/left axis
    outRay.direction.scaleAdd(-inV * tanHalfYFOV, up); // Move the direction along
    // the up/down axis
    outRay.direction.normalize(); // Normalize
    outRay.makeOffsetRay();

  }

  // //////////////////////////////////////////////////////////////////////////////////////////////////
  // DO NOT USE ANY OF THESE METHODS IN YOUR IMPLEMENATION!!!!!!
  // USING LOOKAT() CAN GIVE THE SAME EFFECTS!!!!!!
  // As a concession to the parser, which needs to be able to set each
  // value, eye, target and up, separately as they are read out of the file, I
  // have  created this cache space and these methods to be used to store the
  // values while the parser reads in the values from the file. They are not
  // strictly needed in the real implemenation of a camera, but my effort to
  // keep the parser simple requires them.
  // //////////////////////////////////////////////////////////////////////////////////////////////////

  /** Cache of last eye value */
  private final Point3 cacheEye = new Point3(0, 0, 0);

  /** Cache of last target value */
  private final Point3 cacheTarget = new Point3(1, 0, 0);

  /** Cache of last up value */
  private final Vector3 cacheUp = new Vector3(0, 0, 1);

  /**
   * Uses the current cached values of eye, target and inputUp to setup the
   * camera orientation. The implementation is identical to lookAt.
   */
  private void renormalizeFromCacheValues() {

    lookAt(cacheEye, cacheTarget, cacheUp);

  }

  /**
   * Used internally by the parser to set the original value of the camera. Use
   * lookAt instead.
   *
   * @deprecated
   * @param cacheEye The cacheEye to set.
   */
  public void setEye(Point3 cacheEye) {

    this.cacheEye.set(cacheEye);
    renormalizeFromCacheValues();

  }

  /**
   * Used internally by the parser to set the original value of the camera. Use
   * lookAt instead.
   *
   * @deprecated
   * @param cacheTarget The cacheTarget to set.
   */
  public void setTarget(Point3 cacheTarget) {

    this.cacheTarget.set(cacheTarget);
    renormalizeFromCacheValues();

  }

  /**
   * Used internally by the parser to set the original value of the camera. Use
   * lookAt instead.
   *
   * @deprecated
   * @param cacheUp The cacheUp to set.
   */
  public void setUp(Vector3 cacheUp) {

    this.cacheUp.set(cacheUp);
    renormalizeFromCacheValues();

  }

}