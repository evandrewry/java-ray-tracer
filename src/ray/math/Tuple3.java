/*
 * Created on Aug 18, 2005
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package ray.math;


/**********************************************************************************
 * GENERAL CONTRACT FOR ALL MATH CLASSES.
 *
 * -- The destination of every method is assumed to be the object the method was
 * called on.  For example:
 *
 *      c.add(a,b) means c = a + b.
 *
 * -- Whenever one operand of a binary operand is missing, it is assumed to be
 * the object the method was called upon.  For example:
 *
 *      c.add(a) means c = c + a.
 *
 *********************************************************************************
 *
 * Base class for 3d tuples (ie. points and vectors).
 * @author arbree
 * Aug 18, 2005
 * Tuple3.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class Tuple3 {

  /** The x coordinate of the Tuple3. */
  public double x;
  /** The y coordinate of the Tuple3. */
  public double y;
  /** The z coordinate of the Tuple3. */
  public double z;

  /**
   * Default constructor.
   */
  public Tuple3() {
      this(0, 0, 0);
  }

  /**
   * Copy constructor.
   * @param newTuple The tuple to copy.
   */
  public Tuple3(Tuple3 newTuple) {
      this(newTuple.x, newTuple.y, newTuple.z);
  }

  /**
   * The explicit constructor.
   * @param newX The x coordinate of the new tuple
   * @param newY The y coordinate of the new tuple
   * @param newZ The z coordinate of the new tuple
   */
  public Tuple3(double newX, double newY, double newZ) {
      x = newX;
      y = newY;
      z = newZ;
  }

  /**
   * Scale this Tuple3 by scale
   * @param scale the scale factor
   */
  public void scale(double scale) {
     this.x *= scale;
     this.y *= scale;
     this.z *= scale;
  }

  /**
   * Sets this tuple to a copy of another tuple.  Allows quick
   * conversion between points and vectors.
   * @param inTuple the input tuple
   */
  public void set(Tuple3 inTuple) {
    this.x = inTuple.x;
    this.y = inTuple.y;
    this.z = inTuple.z;
  }

  /**
   * Set the value of this Tuple3 explicitly
   * @param inX the new x coordinate
   * @param inY the new y coordinate
   * @param inZ the new z coordinate
   */
  public void set(double inX, double inY, double inZ) {
    this.x = inX;
    this.y = inY;
    this.z = inZ;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
      return "[" + x + "," + y + "," + z+"]";
  }
}
