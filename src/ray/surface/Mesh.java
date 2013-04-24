package ray.surface;

import java.io.BufferedReader;
import java.io.FileReader;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Frame3;
import ray.math.Point2;
import ray.math.Point3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;


/**
 * Basic packed triangle mesh. The triangle mesh is primarily data storage, all
 * geometric interaction is handled through MeshTriangle objects.
 *
 * @author arbree Aug 19, 2005 TriangleMesh.java Copyright 2005 Program of
 *         Computer Graphics, Cornell University
 */
public class Mesh extends Surface {
    /** The material for the mesh * */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The frame in which the input coordinates live */
    protected Frame3 frame = new Frame3();
    
    /** The number of vertices in the mesh * */
    protected int numVertices;
    
    /** The number of triangles in the mesh * */
    protected int numTriangles;
    
    /** The vertex array -- always present in each mesh * */
    protected float[] verts;
    
    /** The texture coordinate array -- may be null * */
    protected float[] texcoords;
    
    /** The normal coordinate array -- may be null * */
    protected float[] normals;
    
    /** Mesh triangle objects for each triangle. */
    protected MeshTriangle[] triangles;
    
    /**
     * Default constructor creates an empty mesh
     */
    public Mesh() { }
    
    /**
     * Basic constructor. Sets mesh data array into the mesh structure. IMPORTANT:
     * The data array are not copies so changes to the input data array will
     * affect the mesh structure. The number of vertices and the number of
     * triangles are inferred from the lengths of the verts and tris array. If
     * either is not a multiple of three, an error is thrown.
     *
     * @param verts the vertex data
     * @param tris the triangle data
     * @param normals the normal data
     * @param texcoords the texture coordinate data
     */
    public Mesh(float[] verts, int[] tris, float[] normals, 
                float[] texcoords, Material material) {
        
        // check the inputs
        if (verts.length % 3 != 0)
            throw new Error("Vertex array for a triangle mesh is not a multiple of 3.");
        if (tris.length % 3 != 0)
            throw new Error("Triangle array for a triangle mesh is not a multiple of 3.");
        
        // Set data
        this.material = material;
        setMeshData(verts, tris, normals, texcoords);        
    }
    
    public void updateArea() {    	
    }
    
    /**
     * Sets the mesh data and builds the triangle array.
     * @param verts the vertices
     * @param tris the triangles
     * @param normals the normals
     * @param texcoords the texture coordinates
     */
    private void setMeshData(float[] verts, int[] tris, float[] normals, float[] texcoords) {
        
        this.numVertices = verts.length / 3;
        this.numTriangles = tris.length / 3;
        this.verts = verts;
        this.normals = normals;
        this.texcoords = texcoords;
        
        // Transform the vertices
        for (int i = 0; i < numVertices; i++)
            frame.frameToCanonicalPoint(verts, 3*i);
        
        // Transform the normals -- only good for rigid motions!
        if (normals != null)
            for (int i = 0; i < numVertices; i++)
                frame.frameToCanonicalVector(normals, 3*i);
        
        // Build the mesh triangles
        triangles = new MeshTriangle[numTriangles];
        for (int i = 0; i < numTriangles; i++)
            triangles[i] = new MeshTriangle(this, tris[3 * i], tris[3 * i + 1], tris[3 * i + 2]);
        
    }
    
    /**
     * Returns the number of triangles
     *
     * @return the number of triangles
     */
    public int getNumTriangles() {
        
        return numTriangles;
        
    }
    
    /**
     * Returns the number of vertices
     *
     * @return the number of vertices
     */
    public int getNumVertices() {
        
        return numVertices;
        
    }
    
    /**
     * Note: returns the actual list of triangles changes will affect the mesh.
     *
     * @return Returns the triangles.
     */
    public MeshTriangle[] getTriangles() {
        
        return this.triangles;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        
        return material;
        
    }
    
    /**
     * @param material The material to set.
     */
    public void setMaterial(Material material) {
        
        this.material = material;
    }
    
    /**
     * Set the data in this mesh to the data in fileName
     * @param fileName the name of a .msh file
     */
    public void setData(String fileName) {
        
        //  Create a buffered reader for the mesh file
        try {
            BufferedReader fr = new BufferedReader(new FileReader(fileName));
            readMesh(this, fr);
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("ray1.surface.Mesh.setData(): Error reading mesh file.");
        }
    }
    
    public void setFrame(double[] m) {
        frame.set(m);
    }
    
    
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord, ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        
        throw new Error("Meshes cannot intersect rays.  Test for intersection against the individual mesh triangles.");
        
    }
    
    /**
     * @see ray1.surface.Surface#getCenter(ray1.math.Point3)
     */
    public void getCenter(Point3 outCenter) {
        throw new Error("ray1.surface.Mesh.getCenter(): Should never be called.");
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        throw new Error("ray1.surface.Mesh.addToBoundingBox(): Should never be called.");
    }
    
    /**
     * Reads a .msh file into outputMesh.
     *
     * @param outputMesh the mesh to store the read data
     * @param fileName the name of the mesh file to read
     * @return the TriangleMesh from the file
     */
    public static final void readMesh(Mesh outputMesh, BufferedReader fr) {
        
        float[] vertices;
        int[]   triangles;
        float[] normals;
        float[] texcoords;
        
        try {            
            // Read the size of the file
            int nPoints = Integer.parseInt(fr.readLine());
            int nPolys = Integer.parseInt(fr.readLine());
            
            // Create arrays for mesh data
            vertices = new float[nPoints*3];
            triangles = new int[nPolys*3];
            normals = null;
            texcoords = null;
            
            // read vertices
            if (!fr.readLine().equals("vertices")) throw new RuntimeException("Broken file - vertices expected");
            for (int i=0; i<vertices.length; ++i) {
                vertices[i] = Float.parseFloat(fr.readLine());
            }
            
            // read triangles
            String test = fr.readLine();
            if (!test.equals("triangles")) throw new RuntimeException("Broken file - triangles expected.");
            for (int i=0; i<triangles.length; ++i) {
                triangles[i] = Integer.parseInt(fr.readLine());
            }
            
            // read texcoords
            String line = fr.readLine();
            if (line != null && line.equals("texcoords")) {
                texcoords = new float[nPoints*2];
                line = null;
                for (int i=0; i<texcoords.length; ++i) {
                    texcoords[i] = Float.parseFloat(fr.readLine());
                }
            }
            
            // Make sure that if tex coords were missing, but normals were
            // still there, we can still read in the normals, rather than losing
            // the "normals" keyword
            if (line == null) {
                line = fr.readLine();
            }
            if (line != null && line.equals("normals")) {
                normals = new float[nPoints*3];
                for (int i=0; i<normals.length; ++i) {
                    normals[i] = Float.parseFloat(fr.readLine());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Error reading mesh.");
        }
        
        //Set the data in the output Mesh
        outputMesh.setMeshData(vertices, triangles, normals, texcoords);
        
    }
    
    public boolean chooseSamplePoint(Point3 iRec, Point2 seed, LuminaireSamplingRecord lRec) {
        // Auto-generated method stub
        throw new RuntimeException("Doesn't support");
    }

    public double pdfSamplePoint(IntersectionRecord iRec, LuminaireSamplingRecord lRec) {
        // Auto-generated method stub
    	throw new RuntimeException("Doesn't support");
    }
}
