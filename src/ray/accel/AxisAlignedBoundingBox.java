/*
 * Created on Nov 10, 2005 Copyright 2005 Program of Computer Grpahics, Cornell
 * University
 */
package ray.accel;

import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.Ray;

/**
 * Basic bounding box structure used by BoundingVolumeHierarchy
 * 
 * @author arbree Nov 10, 2005 
 * AxisAlignedBoundingBox.java
 *  Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class AxisAlignedBoundingBox {

  /** Constants defining the axes */
  public static final int X = 0;
  public static final int Y = 1;
  public static final int Z = 2;
  
  /** The bounds of the box */
  protected double xMin, yMin, zMin;
  protected double xMax, yMax, zMax;

  /**
   * Basic constructor makes a box containing no space.
   */
  public AxisAlignedBoundingBox() {

    xMin = yMin = zMin = Double.POSITIVE_INFINITY;
    xMax = yMax = zMax = Double.NEGATIVE_INFINITY;
  }

  /**
   * Reset to a box contain nothing
   */
  public void clear() {

    xMin = yMin = zMin = Float.POSITIVE_INFINITY;
    xMax = yMax = zMax = Float.NEGATIVE_INFINITY;
  }

  /**
   * Increase the size of this box so that it entirely contains the input box.
   * @param box1
   */
  public void add(AxisAlignedBoundingBox inBox) {

    add(inBox.xMin, inBox.yMin, inBox.zMin);
    add(inBox.xMax, inBox.yMax, inBox.zMax);
    
  }

  /**
   * Grows the box to include the point p
   */
  public void add(Point3 p) {

    add(p.x, p.y, p.z);
    
  }
  
  /**
   * Grows the box to include the point (x, y, z)
   * @param x
   * @param y
   * @param z
   */
  public void add(double x, double y, double z) {
    
    xMin = (x < xMin ? x : xMin);
    yMin = (y < yMin ? y : yMin);
    zMin = (z < zMin ? z : zMin);
    xMax = (x > xMax ? x : xMax);
    yMax = (y > yMax ? y : yMax);
    zMax = (z > zMax ? z : zMax);
    
  }

  /**
   * Set this box to be the input box.
   * @param box
   */
  public void set(AxisAlignedBoundingBox box) {

    xMin = box.xMin;
    yMin = box.yMin;
    zMin = box.zMin;
    xMax = box.xMax;
    yMax = box.yMax;
    zMax = box.zMax;
  }

  /**
   * Return the ID of the longest axis of the box
   * @return
   */
  public int longestAxis() {

    double sx = xMax - xMin;
    double sy = yMax - yMin;
    double sz = zMax - zMin;
    if (sx >= sy && sx >= sz)
      return X;
    if (sy >= sx && sy >= sz)
      return Y;
    return Z;
  }

  /**
   * Return the volume of this box
   * @return
   */
  public double volume() {
    
    return (xMax - xMin)*(yMax - yMin)*(zMax - zMin);
    
  }
  
  public Vector3 getExtents() {
	  
	  return new Vector3(xMax-xMin, yMax-yMin, zMax-zMin);
	  
  }
  
  /**
   * Return true if this box intersects this input Ray.
   * @param ray
   * @return 
   */
  public boolean intersect(Ray ray) {

    Point3 o = ray.origin;
    Vector3 d = ray.direction;
    
    double ox = o.x;
    double oy = o.y;
    double oz = o.z;
    double dx = d.x;
    double dy = d.y;
    double dz = d.z;
    

    // a three-slab intersection test. We'll get in and out t values for
    // all three axes. For instance on the x axis:
    // o.x + t d.x = 1 => t = (1 - o.x) / d.x
    // o.x + t d.x = -1 => t = (-1 - o.x) / d.x
    // This code is straight from Shirley's section 10.9.1

    double tMin, tMax;
    if (dx >= 0) {
      tMin = (xMin - ox) / dx;
      tMax = (xMax - ox) / dx;
    }
    else {
      tMin = (xMax - ox) / dx;
      tMax = (xMin - ox) / dx;
    }

    double tyMin, tyMax;
    if (dy >= 0) {
      tyMin = (yMin - oy) / dy;
      tyMax = (yMax - oy) / dy;
    }
    else {
      tyMin = (yMax - oy) / dy;
      tyMax = (yMin - oy) / dy;
    }
    if (tMin > tyMax || tyMin > tMax)
      return false;
    if (tyMin > tMin)
      tMin = tyMin;
    if (tyMax < tMax)
      tMax = tyMax;

    double tzMin, tzMax;
    if (dz >= 0) {
      tzMin = (zMin - oz) / dz;
      tzMax = (zMax - oz) / dz;
    }
    else {
      tzMin = (zMax - oz) / dz;
      tzMax = (zMin - oz) / dz;
    }
    if (tMin > tzMax || tzMin > tMax)
      return false;
    if (tzMin > tMin)
      tMin = tzMin;
    if (tzMax < tMax)
      tMax = tzMax;

    return tMin < ray.end && tMax > ray.start;
  }

  /**
   * Returns true if this box intersects the other AABB
   * @param box
   * @return
   */
  public boolean intersects(AxisAlignedBoundingBox box) {

    return ((box.xMin < xMax && box.xMax > xMin) && (box.yMin < yMax && box.yMax > yMin) && (box.zMin < zMax && box.zMax > zMin));
    
  }

  
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {

    return "Box: ("+xMin+","+yMin+","+zMin+")x("+xMax+","+yMax+","+zMax+")";
    
  }
}
