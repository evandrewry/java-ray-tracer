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
 * Class for 3d points.
 *
 * @author arbree
 * Aug 18, 2005
 * Point3.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class Point3 extends Tuple3 {
	
	/**
	 * Default constructor.
	 */
	public Point3() {
		super(0, 0, 0);
	}
	
	/**
	 * Copy constructor.
	 * @param newPoint The point to copy.
	 */
	public Point3(Point3 newPoint) {
		super(newPoint.x, newPoint.y, newPoint.z);
	}
	
	/**
	 * The explicit constructor.
	 * @param newX The x coordinate of the new point.
	 * @param newY The y coordinate of the new point.
	 * @param newZ The z coordinate of the new point.
	 */
	public Point3(double newX, double newY, double newZ) {
		super(newX, newY, newZ);
	}
	
	/**
	 * Returns the squared distance from this Point3 to other
	 * @param other another point
	 * @return the squared distance from this point to the other point
	 */
	public double distanceSquared(Point3 other) {	
		double dx = (this.x - other.x);
		double dy = (this.y - other.y);
		double dz = (this.z - other.z);
		return dx*dx + dy*dy + dz*dz;
	}
	
	/**
	 * Returns the distance from this Point3 to other
	 * @param other another point
	 * @return the distance
	 */
	public double distance(Point3 other) {
		double dx = (this.x - other.x);
		double dy = (this.y - other.y);
		double dz = (this.z - other.z);
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	/**
	 * Add a Vector3 to this Point3
	 * @param vector the vector to add
	 */
	public void add(Tuple3 tup) {
		this.x += tup.x;
		this.y += tup.y;
		this.z += tup.z;
	}
	
	/**
	 * Add a Vector3 to a Point3 and store the result in this Point3
	 * @param point the input point
	 * @param vector the input vector
	 */
	public void add(Point3 point, Vector3 vector) {
		this.x = vector.x + point.x;
		this.y = vector.y + point.y;
		this.z = vector.z + point.z;
	}
	
	/**
	 * Subtract a Vector3 from this Point3
	 * @param vector the vector to substract
	 */
	public void sub(Vector3 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		this.z -= vector.z;
	}
	
	/**
	 * Subtract a Vector3 from a Point3 and store the result in this Point3
	 * @param point the input point
	 * @param vector the input vector
	 */
	public void sub(Point3 point, Vector3 vector) {
		this.x = point.x - vector.x;
		this.y = point.y - vector.y;
		this.z = point.z - vector.z;
	}
	
	/**
	 * Add a scaled multiple of a Vector3 to this Point3
	 * @param scale the input scale
	 * @param vector the input vector
	 */
	public void scaleAdd(double scale, Vector3 vector) {
		this.x += scale * vector.x;
		this.y += scale * vector.y;
		this.z += scale * vector.z;
	}
	
	public String toString() 
	{	
		return x + " " + y + " " + z;
	}
}
