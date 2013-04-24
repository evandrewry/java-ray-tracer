package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

/**
 * A triangle is defined simply by 3 points.
 *
 * @author ags
 */
public class Triangle extends Surface {
    
    /** Material for this triangle * */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The first vertex. */
    public final Point3 v0 = new Point3();
    
    /** The second vertex. */
    public final Point3 v1 = new Point3(1,0,0);
    
    /** The third vertex. */
    public final Point3 v2 = new Point3(0,1,0);
    
    /**
     * Default contructor
     */
    public Triangle() {}
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newV0 The first vertex of the new triangle.
     * @param newV1 The second vertex of the new triangle.
     * @param newV2 The third vertex of the new triangle.
     * @param newMaterial The material of the new triangle.
     */
    public Triangle(Point3 newV0, Point3 newV1, Point3 newV2, Material newMaterial) {        
        material = newMaterial;
        v0.set(newV0);
        v1.set(newV1);
        v2.set(newV2);
        updateArea();
    }
    
    /**
     * @param v0 The v0 to set.
     */
    public void setV0(Point3 v0) {        
        this.v0.set(v0);
        updateArea();
    }
    
    /**
     * @param v1 The v1 to set.
     */
    public void setV1(Point3 v1) {        
        this.v1.set(v1);
        updateArea();
    }
    
    /**
     * @param v2 The v2 to set.
     */
    public void setV2(Point3 v2) {        
        this.v2.set(v2);
        updateArea();
    }
    
    /**
     * Calculate area of the triangle
     */
    public void updateArea() {
        Vector3 u = new Vector3(), v = new Vector3(), n = new Vector3();
        u.sub(v1, v0);
        v.sub(v2, v0);
        n.cross(u, v);
        area = 0.5 * n.length();
        oneOverArea = 1. / area;        
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {        
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {        
        this.material = material;        
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, 
                                     LuminaireSamplingRecord lRec) {
        Point2 baryPt = new Point2();
        Geometry.squareToTriangle(seed, baryPt);
        lRec.frame.o.set(v0);
        lRec.frame.u.sub(v1, v0);
        lRec.frame.v.sub(v2, v0);
        lRec.frame.w.cross(lRec.frame.u, lRec.frame.v);
        
        lRec.frame.o.scaleAdd(baryPt.x, lRec.frame.u);
        lRec.frame.o.scaleAdd(baryPt.y, lRec.frame.v);
        lRec.frame.initFromWU();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        
        return true;	// always return true, since lRec.emitDir.dot(lRec.frame.w) will be checked later on in Scene class's method
        //return (lRec.emitDir.dot(lRec.frame.w) > 0);    // SHOULD we check if this ray can arrive the surface point, iRec.frame.o
    }

    /**
     * Triangle intersections should be FAST! Generally, most of the time in a ray
     * tracer is spent in this method (as much as 50%). In practice that means
     * that a triangle intersection method should not create any new objects, amd
     * should attempt to perform as little branching as necessary. Also note that
     * you can create as many primitive types in a method as you want. Allociation
     * of primitives is not from the heap and literally takes no time over the
     * cost of calling the method. This is why I can freely allocate so many
     * doubles.
     *
     * The implemented method closely follows the Cramer's rule method
     * described on page 208 of Shirley.
     *
     * @see Surface#intersect(IntersectionRecord, Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        
        // Rename the components of each vertex for convienience (and save many
        // field access computations)
        final double v0x = v0.x;
        final double v0y = v0.y;
        final double v0z = v0.z;
        final double v1x = v1.x;
        final double v1y = v1.y;
        final double v1z = v1.z;
        final double v2x = v2.x;
        final double v2y = v2.y;
        final double v2z = v2.z;
        
        // Compute elements of the triangle ray matrix
        double A = v0x - v1x;
        double B = v0y - v1y;
        double C = v0z - v1z;
        double D = v0x - v2x;
        double E = v0y - v2y;
        double F = v0z - v2z;
        
        // Rename ray directions for clarity and convenience
        double G = ray.direction.x;
        double H = ray.direction.y;
        double I = ray.direction.z;
        
        // Compute the factors in the numerator of the beta coordinate
        double EIHF = E * I - H * F;
        double GFDI = G * F - D * I;
        double DHEG = D * H - E * G;
        
        // Try and minimize divides (they are expensive). Compute inverse of the
        // denominator factor
        double inv_denom = 1.0 / (A * EIHF + B * GFDI + C * DHEG);
        
        // Compute the direction from the ray origin to the first vertex
        double J = v0x - ray.origin.x;
        double K = v0y - ray.origin.y;
        double L = v0z - ray.origin.z;
        
        // Compute the beta coordinate
        double beta = inv_denom * (J * EIHF + K * GFDI + L * DHEG);
        
        // If the intersection is out of this range it doesn't fall on the triangle
        if (beta < 0 || beta > 1)
            return false;
        
        // Compute the factors of the numerator of the gamma coordinate
        double AKJB = A * K - J * B;
        double JCAL = J * C - A * L;
        double BLKC = B * L - K * C;
        
        // Calculate the other barycentric coordinate
        double gamma = inv_denom * (I * AKJB + H * JCAL + G * BLKC);
        
        // If the intersection is out of this range it doesn't fall on the triangle
        if (gamma < 0 || beta + gamma > 1)
            return false;
        
        // Calculate the ray t value
        double t = -inv_denom * (F * AKJB + E * JCAL + D * BLKC);
        
        // Check that we are on the corrent half line of the ray
        if (t < ray.start || t > ray.end)
            return false;
        
        // Fill out the record
        outRecord.t = t;
        outRecord.surface = this;
        
        // Faster to compute location using barycentric coordinates than
        // computations using Vector3
        double weight0 = 1 - beta - gamma; // Barycentric coordinates total 1
        outRecord.frame.o.set(weight0 * v0x + beta * v1x + gamma * v2x, weight0 * v0y + beta * v1y + gamma * v2y, weight0 * v0z + beta * v1z + gamma * v2z);
        
        // Calculate the normal of the triangle. Again this could be precomputed,
        // but space usually ends up being more important. Plus this is done
        // only when the ray intersects, usually much less than the test is
        // performed.
        double normx = B * F - C * E;
        double normy = C * D - A * F;
        double normz = A * E - B * D;
        outRecord.frame.w.set(normx, normy, normz);
        outRecord.frame.initFromW();
        
        return true;
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        
        return "triangle " + v0 + " " + v1 + " " + v2 + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#getCenter(ray1.math.Point3)
     */
    public void getCenter(Point3 outCenter) {
        
        outCenter.set(v0.x + v1.x + v2.x, v0.y + v1.y + v2.y, v0.z + v1.z + v2.z);
        outCenter.scale(0.33333);
        
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {        
        inBox.add(v0);
        inBox.add(v1);
        inBox.add(v2);        
    }
}
