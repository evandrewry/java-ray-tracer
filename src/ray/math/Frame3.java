package ray.math;

/**
 * An orthonormal coordinate frame in 3D, consisting of an origin and an orthonormal 
 * basis.  Supports transformations between frame (local) and canonical (global)
 * coordinates, and includes functions for setting up the basis from on the values 
 * of one or two vectors.
 * 
 * The vectors u, v, and w are supposed to be maintained in an orthonormal state.  
 * Whenever they are written,  Depends on the orthonormality of (u,v,w).coordinatecoordinate
 * 
 * @author srm
 */
public class Frame3 {
	
	public Point3 o = new Point3(0, 0, 0);
	public Vector3 u = new Vector3(1, 0, 0);
	public Vector3 v = new Vector3(0, 1, 0);
	public Vector3 w = new Vector3(0, 0, 1);
	
	public Frame3() {}
	public Frame3(Frame3 f) { set(f); }
	
	public void set(Frame3 f) {
		set(f.o, f.u, f.v, f.w);
	}
	
	public void set(Point3 o, Vector3 u, Vector3 v, Vector3 w) {
		this.o.set(o);
		this.u.set(u);
		this.v.set(v);
		this.w.set(w);
	}
	
	public void set(double[] m) {
		this.u.set(m[0], m[4], m[8]);
		this.v.set(m[1], m[5], m[9]);
		this.w.set(m[2], m[6], m[10]);
		this.o.set(m[3], m[7], m[11]);
	}
	
	/**
	 * Set up the basis (u,v,w) so that it is orthonormal, presesrving only
	 * the direction of w and the plane spanned by w and u.
	 */
	public void initFromWU() {
		w.normalize();
		u.normalize();
		v.cross(w, u);
		u.cross(v, w);
	}
	
	/**
	 * Set up the basis (u,v,w) so that it is orthonormal, preserving only
	 * the direction of w.
	 */
	public void initFromW() {
		w.normalize();
		u.perpendicular(w);
		u.normalize();
		v.cross(w, u);
	}
	
	/**
	 * Transform a vector represented in frame coordinates to represent the same vector
	 * in canonical coordinates.
	 * @param vec The vector to be transformed (overwritten)
	 */
	public void frameToCanonical(Vector3 vec) {
		double x = vec.x, y = vec.y, z = vec.z;
		vec.x = x * u.x + y * v.x + z * w.x;
		vec.y = x * u.y + y * v.y + z * w.y;
		vec.z = x * u.z + y * v.z + z * w.z;
	}
	
	/**
	 * Transform a point represented in frame coordinates to represent the same point
	 * in canonical coordinates.
	 * @param data An array in which the coordinates are stored (overwritten)
	 * @param offset The offset into the array at which the coordinates are found
	 */
	public void frameToCanonicalVector(float[] data, int offset) {
		double x = data[offset+0], y = data[offset+1], z = data[offset+2];
		data[offset+0] = (float) (x * u.x + y * v.x + z * w.x);
		data[offset+1] = (float) (x * u.y + y * v.y + z * w.y);
		data[offset+2] = (float) (x * u.z + y * v.z + z * w.z);
	}

	/**
	 * Transform a point represented in frame coordinates to represent the same point
	 * in canonical coordinates.
	 * @param pt The point to be transformed (overwritten)
	 */
	public void frameToCanonical(Point3 pt) {
		double x = pt.x, y = pt.y, z = pt.z;
		pt.x = o.x + x * u.x + y * v.x + z * w.x;
		pt.y = o.y + x * u.y + y * v.y + z * w.y;
		pt.z = o.z + x * u.z + y * v.z + z * w.z;
	}
	
	/**
	 * Transform a point represented in frame coordinates to represent the same point
	 * in canonical coordinates.
	 * @param data An array in which the coordinates are stored (overwritten)
	 * @param offset The offset into the array at which the coordinates are found
	 */
	public void frameToCanonicalPoint(float[] data, int offset) {
		double x = data[offset+0], y = data[offset+1], z = data[offset+2];
		data[offset+0] = (float) (o.x + x * u.x + y * v.x + z * w.x);
		data[offset+1] = (float) (o.y + x * u.y + y * v.y + z * w.y);
		data[offset+2] = (float) (o.z + x * u.z + y * v.z + z * w.z);
	}

	/**
	 * Transform a vector represented in canonical coordinates to represent the same vector
	 * in frame coordinates.  Depends on the orthonormality of (u,v,w).
	 * @param vec The vector to be transformed (overwritten)
	 */
	public void canonicalToFrame(Vector3 vec) {
		double x = vec.x, y = vec.y, z = vec.z;
		vec.x = x * u.x + y * u.y + z * u.z;
		vec.y = x * v.x + y * v.y + z * v.z;
		vec.z = x * w.x + y * w.y + z * w.z;
	}
	
	/**
	 * Transform a point represented in canonical coordinates to represent the same point
	 * in frame coordinates.  Depends on the orthonormality of (u,v,w).
	 * @param pt The point to be transformed (overwritten)
	 */
	public void canonicalToFrame(Point3 pt) {
		double x = pt.x - o.x, y = pt.y - o.y, z = pt.z - o.z;
		pt.x = x * u.x + y * u.y + z * u.z;
		pt.y = x * v.x + y * v.y + z * v.z;
		pt.z = x * w.x + y * w.y + z * w.z;
	}
	
}
