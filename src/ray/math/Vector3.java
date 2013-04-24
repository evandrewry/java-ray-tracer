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
 * The Vector3 class represents a 3 dimension vector of doubles.
 * @author ags
 */
public class Vector3 extends Tuple3 {
	
	/**
	 * Default constructor creates a zero vector.
	 */
	public Vector3() {
		super(0, 0, 0);
	}
	
	/**
	 * Copy constructor.
	 * @param newTuple The vector to copy.
	 */
	public Vector3(Tuple3 newTuple) {
		super(newTuple.x, newTuple.y, newTuple.z);
	}
	
	/**
	 * Construct vector with given coordinates.
	 * @param newX 
	 * @param newY
	 * @param newZ 
	 */
	public Vector3(double newX, double newY, double newZ) {
		super(newX, newY, newZ);
	}
	
	public void setSpherical(double theta, double phi) {
		double s = Math.sin(theta);
		x = s * Math.cos(phi);
		y = s * Math.sin(phi);
		z = Math.cos(theta);
	}
	
	/**
	 * Sets this vector to the cross product of op1 and op2
	 * @param op1
	 * @param op2
	 */
	public void cross(Vector3 op1, Vector3 op2) {
		this.x = op1.y * op2.z - op1.z * op2.y;
		this.y = op1.z * op2.x - op1.x * op2.z;
		this.z = op1.x * op2.y - op1.y * op2.x;
	}
	
	/**
	 * Compute the dot product of this vector with another vector.
	 * @param rhs The right hand operand.
	 * @return The dot product of this with rhs.
	 */
	public double dot(Vector3 rhs) {
		return x * rhs.x + y * rhs.y + z * rhs.z;
	}
	
	/**
	 * Compute the length of this vector.
	 * @return The length.
	 */
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	/**
	 * Compute the squared length of this vector.
	 * @return The squared length
	 */
	public double squaredLength() {	
		return x * x + y * y + z * z;
	}
	
	/**
	 * Normalize this vector so that its length is 1.0.
	 * If the length is 0, no action is taken.
	 */
	public void normalize() {
		double dist = Math.sqrt(x * x + y * y + z * z);
		if (dist != 0) {
			x /= dist;
			y /= dist;
			z /= dist;
		}
	}
	
	/**
	 * Add a Vector3 to this Vector3
	 * @param vector the Vector3 to add
	 */
	public void add(Vector3 vector) {
		this.x += vector.x;
		this.y += vector.y;
		this.z += vector.z;
	}
	
	/**
	 * Add the values of Vector3 v1 and Vector3 v2 and store the sum in this Vector3.
	 * @param v1 the first operand
	 * @param v2 the second operand
	 */
	public void add(Vector3 v1, Vector3 v2) {
		this.x = v1.x + v2.x;
		this.y = v1.y + v2.y;
		this.z = v1.z + v2.z;
	}
	
	/**
	 * Substract a Vector3 from this Vector3, storing the result in this Vector3.
	 * @param vector the Tuple3 to subtract
	 */
	public void sub(Vector3 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		this.z -= vector.z;
	}
	
	/**
	 * Subtract one Point3 from another Point3, storing the result in this Vector2.
	 * @param p1 the first operand
	 * @param p2 the second operand
	 */
	public void sub(Point3 p1, Point3 p2) {
		this.x = p1.x - p2.x;
		this.y = p1.y - p2.y;
		this.z = p1.z - p2.z;
	}
	
	/**
	 * Add a scalar multiple of a Vector3 to this Vector3
	 * @param scale the scale factor
	 * @param vector the vector to scale and add
	 */
	public void scaleAdd(double scale, Tuple3 vector) {
		this.x += scale * vector.x;
		this.y += scale * vector.y;
		this.z += scale * vector.z;
	}
	
	/**
	 * Construct a vector that is perpendicular to v.  This is done
	 * stably by finding a far-from-parallel vector and crossing it with v.
	 * @param v
	 */
	public void perpendicular(Vector3 v) {
		double vx = Math.abs(v.x), vy = Math.abs(v.y), vz = Math.abs(v.z);
		if (vx < vy) {
			if (vx < vz) 
				set(0, -v.z, v.y); // (-1, 0, 0)
			else 
				set(-v.y, v.x, 0); // (0, 0, -1)
		} else {
			if (vy < vz) 
				set(v.z, 0, -v.x); // (0, -1, 0)
			else 
				set(-v.y, v.x, 0); // (0, 0, -1)
		}
	}
	
}