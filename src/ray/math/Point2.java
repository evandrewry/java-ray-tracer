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
 * Class for 2d points.
 *
 * @author arbree
 * Aug 18, 2005
 * Point2.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class Point2 extends Tuple2 {
	
	public Point2() {
		super(0, 0);
	}
	
	/**
	 * Copy constructor.
	 * @param newPoint The point to copy.
	 */
	public Point2(Point2 newPoint) {
		super(newPoint.x, newPoint.y);
	}
	
	/**
	 * The explicit constructor.
	 * @param newX The x coordinate of the new point
	 * @param newY The y coordinate of the new point
	 */
	public Point2(double newX, double newY) {
		super(newX, newY);
	}
	
	/**
	 * Returns the squared distance from this Point2 to other
	 * @param other another point
	 * @return the squared distance from this point to the other point
	 */
	public double distanceSquared(Point2 other) {
		double dx = (this.x - other.x);
		double dy = (this.y - other.y);
		return dx*dx + dy*dy;
	}
	
	/**
	 * Returns the distance from this Point2 to other
	 * @param other another point
	 * @return the distance
	 */
	public double distance(Point2 other) {
		double dx = (this.x - other.x);
		double dy = (this.y - other.y);
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Add a Vector2 to this Point2
	 * @param vector the vector to add
	 */
	public void add(Vector2 vector) {
		this.x += vector.x;
		this.y += vector.y;
	}
	
	/**
	 * Add a Vector2 to a Point2 and store the result in this Point2
	 * @param point the input point
	 * @param vector the input vector
	 */
	public void add(Point2 point, Vector2 vector) {
		this.x = vector.x + point.x;
		this.y = vector.y + point.y;
	}
	
	/**
	 * Subtract a Vector2 from this Point2
	 * @param vector the vector to substract
	 */
	public void sub(Vector2 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
	}
	
	/**
	 * Subtract a Vector2 from a Point2 and store the result in this Point2
	 * @param point the input point
	 * @param vector the input vector
	 */
	public void sub(Point2 point, Vector2 vector) {
		this.x = point.x - vector.x;
		this.y = point.y - vector.y;
	}
	
	/**
	 * Add a scaled multiple of a Vector2 to this Point2
	 * @param scale the input scale
	 * @param vector the input vector
	 */
	public void scaleAdd(double scale, Vector2 vector) {
		this.x += scale * vector.x;
		this.y += scale * vector.y;
	}
	
}
