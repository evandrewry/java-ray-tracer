package ray.surface;

import java.util.Comparator;

import ray.material.Material;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.math.Point2;
import ray.math.Point3;
import ray.accel.AxisAlignedBoundingBox;

/**
 * Abstract base class for all surfaces.  Provides access for material and
 * intersection uniformly for all surfaces.  Note that this class should not
 * contain a material pointer.  Material pointers are left under the control
 * of the implementation.  This allows very lightweight surfaces, a good
 * example are MeshTriangle which merely reference triangles in a TriangleMesh
 * surface, to share a material pointer.
 *
 * @author ags
 */
public abstract class Surface {
    
    /** Comparators for each direction */
    public static final SurfaceComparator X_COMPARE = new SurfaceComparator(SurfaceComparator.X_AXIS);
    public static final SurfaceComparator Y_COMPARE = new SurfaceComparator(SurfaceComparator.Y_AXIS);
    public static final SurfaceComparator Z_COMPARE = new SurfaceComparator(SurfaceComparator.Z_AXIS);
    
    /** total surface area */
    protected double area = 0;
    protected double oneOverArea = 0;
    
    public double getArea() { return area; }
    public double getOneOverArea() { return oneOverArea; }
    
    /** update the area of this surface */
    public abstract void updateArea();
        
    /**
     * @return Returns the material.
     */
    public abstract Material getMaterial();
    
    /**
     * @param material The material to set.
     */
    public abstract void setMaterial(Material material);
    
    /**
     * Tests this surface for intersection with ray. If an intersection is found
     * record is filled out with the information about the intersection and the
     * method returns true. It returns false otherwise and the information in
     * outRecord is not modified.
     *
     * @param outRecord the output IntersectionRecord
     * @param ray the ray to intersect
     * @return true if the surface intersects the ray
     */
    
    public abstract boolean intersect(IntersectionRecord outRecord, Ray ray);
    
    /**
     * Grow the bounding box to include this surface
     * @param inBox
     */
    public abstract void addToBoundingBox(AxisAlignedBoundingBox inBox);
    
    /**
     * Return an estimate of the center of this surface
     */
    public abstract void getCenter(Point3 outCenter);
    
    /**
     * Choose a random point to sample illumination from this surface as an emitter.
     */
    public abstract boolean chooseSamplePoint(Point3 p, Point2 seed, 
                                              LuminaireSamplingRecord lRec);

    /**
     * The pdf corresponding to chooseSamplePoint.  
     * LRec must describe a visible point on a luminaire.    
     */
    public double pdfSamplePoint(Point3 p, LuminaireSamplingRecord lRec)
    {
        return oneOverArea;
    }

    /**
     * Comparator for surfaces
     * @author arbree
     * Nov 18, 2005
     * Surface.java
     * Copyright 2005 Program of Computer Graphics, Cornell University
     */
    private static class SurfaceComparator implements Comparator<Surface> {
        
        //Constants defining the axis directions
        public static final int X_AXIS = 0;
        public static final int Y_AXIS = 1;
        public static final int Z_AXIS = 2;
        
        //The axis of this comparator
        protected int axis;
        
        //Stores the centers of the objects currently being compared
        private Point3 c1 = new Point3();
        private Point3 c2 = new Point3();
        
        /**
         * Constructs a comparator for a certain axis
         * @param inAxis
         */
        public SurfaceComparator(int inAxis) {
            
            axis = inAxis;
            
        }
        
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Surface s1, Surface s2) {
            
            s1.getCenter(c1);
            s2.getCenter(c2);
            
            //Get the direction to compare
            double d1 = 0;
            double d2 = 0;
            switch (axis) {
            case X_AXIS:
                d1 = c1.x;
                d2 = c2.x;
                break;
            case Y_AXIS:
                d1 = c1.y;
                d2 = c2.y;
                break;
            case Z_AXIS:
                d1 = c1.z;
                d2 = c2.z;
                break;
            }
            
            return Double.compare(d1, d2);
            
        }
    }

}
