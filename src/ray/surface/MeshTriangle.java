/*
 * Created on Aug 19, 2005 Copyright 2005 Program of Computer Grpahics, Cornell
 * University
 */
package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.math.Geometry;

/**
 * Represents an individual triangle in a mesh.
 * @author arbree Aug 19, 2005
 * MeshTriangle.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class MeshTriangle extends Surface {
    /** Pointer to the mesh containing this triangle * */
    protected Mesh mesh;
  
    /** The first triangle vertex * */
    public int v0;
  
    /** The second triangle vertex * */
    public int v1;
  
    /** The third triangle vertex * */
    public int v2;
  
    /**
     * Protected constructor, only TriangleMesh objects should create
     * MeshTriangles. Just sets the input parameters.
     *
     * @param parentMesh
     * @param inV0
     * @param inV1
     * @param inV2
     */
    protected MeshTriangle(Mesh parentMesh, int inV0, int inV1, int inV2) {
        mesh = parentMesh;
        v0 = inV0;
        v1 = inV1;
        v2 = inV2;

        updateArea();
    }
  
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return mesh.material;
    }
  
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        throw new Error("The material for a individual triangle in a Mesh cannot be set, set the material in the mesh object.");
    }
  
    public void updateArea() {
        Vector3 u = new Vector3(), v = new Vector3(), n = new Vector3();
        u.set(mesh.verts[3*v1]  -mesh.verts[3*v0], 
              mesh.verts[3*v1+1]-mesh.verts[3*v0+1],
              mesh.verts[3*v1+2]-mesh.verts[3*v0+2]);
        v.set(mesh.verts[3*v2]  -mesh.verts[3*v0],
              mesh.verts[3*v2+1]-mesh.verts[3*v0+1],
              mesh.verts[3*v2+2]-mesh.verts[3*v0+2]);
        n.cross(u, v);
        area = 0.5 * n.length();
        oneOverArea = 1. / area;
    }
    
    /**
     * This simply copies the triangle intersection method from Triangle, but
     * reads the vertex data from the mesh array and computes the normals from the
     * normal array (if present)
     *
     * The implemented method closely follows the Cramer's rule method
     * described on page 208 of Shirley.
     *
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
  
      // Rename the components of each vertex for convienience (and save many
      // field access computations)
      final double v0x = mesh.verts[3 * v0];
      final double v0y = mesh.verts[3 * v0 + 1];
      final double v0z = mesh.verts[3 * v0 + 2];
      final double v1x = mesh.verts[3 * v1];
      final double v1y = mesh.verts[3 * v1 + 1];
      final double v1z = mesh.verts[3 * v1 + 2];
      final double v2x = mesh.verts[3 * v2];
      final double v2y = mesh.verts[3 * v2 + 1];
      final double v2z = mesh.verts[3 * v2 + 2];
  
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
      if (beta < 0 || beta > 1 || Double.isNaN(beta))
        return false;
  
      // Compute the factors of the numerator of the gamma coordinate
      double AKJB = A * K - J * B;
      double JCAL = J * C - A * L;
      double BLKC = B * L - K * C;
  
      // Calculate the other barycentric coordinate
      double gamma = inv_denom * (I * AKJB + H * JCAL + G * BLKC);
  
      // If the intersection is out of this range it doesn't fall on the triangle
      if (gamma < 0 || beta + gamma > 1 || Double.isNaN(gamma))
        return false;
  
      // Calculate the ray t value
      double t = -inv_denom * (F * AKJB + E * JCAL + D * BLKC);
  
      // Check that we are on the corrent half line of the ray
      if (t < ray.start || t > ray.end || Double.isNaN(t))
        return false;
  
      // Fill out the record
      outRecord.t = t;
      outRecord.surface = this;
  
      // Faster to compute location using barycentric coordinates than
      // computations using Vector3
      double weight0 = 1 - beta - gamma; // Barycentric coordinates total 1
      outRecord.frame.o.set(weight0 * v0x + beta * v1x + gamma * v2x, weight0 * v0y + beta * v1y + gamma * v2y, weight0 * v0z + beta * v1z + gamma * v2z);
  
      // If the normals array is empty use the old calculation
      if (mesh.normals == null) {
  
        // Calculate the normal of the triangle. Again this could be precomputed,
        // but space usually ends up being more important. Plus this is done
        // only when the ray intersects, usually much less than the test is
        // performed.
        double normx = B * F - C * E;
        double normy = C * D - A * F;
        double normz = A * E - B * D;
        outRecord.frame.w.set(normx, normy, normz);
        outRecord.frame.initFromW();
  
      }
  
      // Else compute the normals using the normal list and the barycentric
      // coordinates
      else {
  
        // Read the normal coordinates
        double n0x = mesh.normals[3 * v0];
        double n0y = mesh.normals[3 * v0 + 1];
        double n0z = mesh.normals[3 * v0 + 2];
        double n1x = mesh.normals[3 * v1];
        double n1y = mesh.normals[3 * v1 + 1];
        double n1z = mesh.normals[3 * v1 + 2];
        double n2x = mesh.normals[3 * v2];
        double n2y = mesh.normals[3 * v2 + 1];
        double n2z = mesh.normals[3 * v2 + 2];
  
        // Compute normal
        outRecord.frame.w.set(weight0 * n0x + beta * n1x + gamma * n2x, weight0 * n0y + beta * n1y + gamma * n2y, weight0 * n0z + beta * n1z + gamma * n2z);
        outRecord.frame.initFromW();
  
      }
      
      //  If the texture coordinates array is not empty
      if (mesh.texcoords != null) {
  
        //Read the normal coordinates
        double t0x = mesh.texcoords[2 * v0];
        double t0y = mesh.texcoords[2 * v0 + 1];
        double t1x = mesh.texcoords[2 * v1];
        double t1y = mesh.texcoords[2 * v1 + 1];
        double t2x = mesh.texcoords[2 * v2];
        double t2y = mesh.texcoords[2 * v2 + 1];
  
        // Compute normal
        outRecord.texCoords.set(weight0 * t0x + beta * t1x + gamma * t2x, weight0 * t0y + beta * t1y + gamma * t2y);
  
      }
  
      return true;
    }
  
    /**
     * @see ray1.surface.Surface#getCenter(ray1.math.Point3)
     */
    public void getCenter(Point3 outCenter) {
  
      outCenter.set(mesh.verts[3*v0] + mesh.verts[3*v1] + mesh.verts[3*v1],
                    mesh.verts[3*v0+1] + mesh.verts[3*v1+1] + mesh.verts[3*v1+1],
                    mesh.verts[3*v0+2] + mesh.verts[3*v1+2] + mesh.verts[3*v1+2]);
      outCenter.scale(1/3.);
      
    }
  
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(mesh.verts[3*v0], mesh.verts[3*v0+1], mesh.verts[3*v0+2]);
        inBox.add(mesh.verts[3*v1], mesh.verts[3*v1+1], mesh.verts[3*v1+2]);
        inBox.add(mesh.verts[3*v2], mesh.verts[3*v2+1], mesh.verts[3*v2+2]);
    }
  
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Point2 baryPt = new Point2();
        Geometry.squareToTriangle(seed, baryPt);

        lRec.frame.o.set(mesh.verts[3*v0], mesh.verts[3*v0+1], mesh.verts[3*v0+2]);
        lRec.frame.u.set(mesh.verts[3*v1] - mesh.verts[3*v0],
        		mesh.verts[3*v1+1] - mesh.verts[3*v0+1],
        		mesh.verts[3*v1+2] - mesh.verts[3*v0+2]);
        lRec.frame.v.set(mesh.verts[3*v2] - mesh.verts[3*v0],
        		mesh.verts[3*v2+1] - mesh.verts[3*v0+1],
        		mesh.verts[3*v2+2] - mesh.verts[3*v0+2]);
        lRec.frame.w.cross(lRec.frame.u, lRec.frame.v);
        lRec.frame.o.scaleAdd(baryPt.x, lRec.frame.u);
        lRec.frame.o.scaleAdd(baryPt.y, lRec.frame.v);
        lRec.frame.initFromWU();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return (lRec.emitDir.dot(lRec.frame.w) > 0);
    }
  
    public double pdfSamplePoint(IntersectionRecord iRec, LuminaireSamplingRecord lRec) {
        return oneOverArea;
    }
  
}
