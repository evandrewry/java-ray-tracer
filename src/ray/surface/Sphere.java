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
import carbine.MathExt;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {

    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;

    /** The center of the sphere. */
    protected final Point3 center = new Point3();

    /** The radius of the sphere. */
    protected double radius = 1.0;

    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {
    }

    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }

    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
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

    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {
        outPoint.set(center);
    }

    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {
        this.center.set(center);
    }

    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }

    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }

    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        double t = 0;
        Vector3 centerVect = new Vector3(this.center, ray.origin);
        double a = ray.direction.dot(ray.direction);
        double b = 2 * centerVect.dot(ray.direction);
        double c = centerVect.dot(centerVect) - this.radius * this.radius;

        double discriminant = b * b - 4.0 * a * c;
        if (discriminant < -MathExt.DOUBLE_EPS) {
            return false;
        } else if (discriminant <= MathExt.DOUBLE_EPS) {
            // find t value at intersection
            t = -b / 2 * a;
            if (t < ray.start || t > ray.end) {
                System.out.println("t not on ray");
                return false;
            }
        } else {
            /* find t values at both intersections */
            double t0 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
            double t1 = (-b + Math.sqrt(discriminant)) / (2.0 * a);

            /* check if we have a valid intersection on the ray */
            boolean t0valid = t0 >= ray.start && t0 <= ray.end;
            boolean t1valid = t1 >= ray.start && t1 <= ray.end;

            /* if both valid, choose the first, otherwise choose the valid one */
            if (t0valid && t1valid)
                t = t0 < t1 ? t0 : t1;
            else if (t0valid)
                t = t0;
            else if (t1valid)
                t = t1;
            else
                return false;
        }

        outRecord.t = t;
        outRecord.surface = this;
        ray.evaluate(outRecord.frame.o, t);
        outRecord.frame.w.sub(outRecord.frame.o, this.center);
        outRecord.frame.w.normalize();
        outRecord.frame.initFromW();

        return true;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }

    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);
    }


}
