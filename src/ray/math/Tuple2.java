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
 * Base class for 2d tuples (ie. points and vectors).
 * @author arbree
 * Aug 18, 2005
 * Tuple2.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class Tuple2 {
	
	/** The x coordinate of the Tuple2. */
	public double x;
	/** The y coordinate of the Tuple2. */
	public double y;
	
	/**
	 * Default constructor.
	 */
	public Tuple2() {
		this(0, 0);
	}
	
	/**
	 * Copy constructor.
	 * @param newTuple The tuple to copy.
	 */
	public Tuple2(Tuple2 newTuple) {
		this(newTuple.x, newTuple.y);
	}
	
	/**
	 * The explicit constructor.
	 * @param newX The x coordinate of the new tuple.
	 * @param newY The y coordinate of the new tuple.
	 */
	public Tuple2(double newX, double newY) {
		x = newX;
		y = newY;
	}
	
	/**
	 * Scale this Tuple2 by scale
	 * @param scale the scale factor
	 */
	public void scale(double scale) {
		this.x *= scale;
		this.y *= scale;
	}
	
	/**
	 * Sets this tuple to a copy of another tuple.  Allows quick
	 * conversion between points and vectors.
	 * @param inTuple the input tuple
	 */
	public void set(Tuple2 inTuple) {
		this.x = inTuple.x;
		this.y = inTuple.y;
	}
	
	/**
	 * Set the value of this Tuple2 explicitly
	 * @param inX the new x coordinate
	 * @param inY the new y coordinate
	 */
	public void set(double inX, double inY) {
		this.x = inX;
		this.y = inY;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "[" + x + "," + y +"]";
	}
}
