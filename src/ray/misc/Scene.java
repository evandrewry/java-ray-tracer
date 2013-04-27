package ray.misc;

import java.util.ArrayList;

import ray.accel.AccelerationStructure;
import ray.accel.BoundingVolume;
import ray.background.Background;
import ray.background.Uniform;
import ray.camera.Camera;
import ray.light.PointLight;
import ray.material.Material;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.renderer.DirectOnlyRenderer;
import ray.renderer.Renderer;
import ray.sampling.IndependentSampler;
import ray.sampling.SampleGenerator;
import ray.surface.Mesh;
import ray.surface.MeshTriangle;
import ray.surface.Surface;

/**
 * The scene is just a collection of objects that compose a scene. The camera,
 * the list of lights, and the list of surfaces.
 * 
 * @author ags
 */
public class Scene {

	/** The camera for this scene. */
	protected Camera camera;

	/** The list of surfaces in the scene. */
	protected ArrayList<Surface> surfaces = new ArrayList<Surface>();

	/** The list of surfaces in the scene that happen to be emitters. */
	protected ArrayList<Surface> luminaires = new ArrayList<Surface>();

	protected ArrayList<PointLight> pointLights = new ArrayList<PointLight>();

	/** The list of materials in the scene. */
	protected ArrayList<Material> materials = new ArrayList<Material>();

	/**
	 * The background, which gives the radiance for rays that don't hit
	 * anything.
	 */
	protected Background background = new Uniform();

	/**
	 * The renderer, which isn't really part of the scene but needs to be
	 * parsed.
	 */
	protected Renderer renderer = new DirectOnlyRenderer();

	/**
	 * The sampler, which isn't really part of the scene but needs to be parsed.
	 */
	protected SampleGenerator sampler = new IndependentSampler();

	/** Image to be produced by the renderer **/
	protected Image outputImage;

	/**
	 * Work space for an intersection record needed in the
	 * getFirstIntersctionMethod(). Avoids creating a new record each ray cast,
	 * but not thread safe.
	 */
	// private IntersectionRecord workRec = new IntersectionRecord();

	private AccelerationStructure accel;

	public ArrayList<Surface> getSurfaces() {
		return surfaces;
	}

	/**
	 * @return Returns the outputImage.
	 */
	public Image getImage() {
		return outputImage;
	}

	/**
	 * @param outputImage
	 *            The outputImage to set.
	 */
	public void setImage(Image outputImage) {
		this.outputImage = outputImage;
	}

	/**
	 * @return Returns the camera.
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * @param camera
	 *            The camera to set.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public SampleGenerator getSampler() {
		return sampler;
	}

	public void setSampler(SampleGenerator sampler) {
		this.sampler = sampler;
	}

	public int luminaireNum() {
		return luminaires.size();
	}

	/**
	 * Trace a ray to find direct radiance incident from a particular direction.
	 * 
	 * @param o
	 *            the point on surface, where the ray light will arrive
	 * @param dir
	 *            The direction from which to look for radiance (surface
	 *            coordinates)
	 * @param outRadiance
	 *            The radiance found
	 */
	public void incidentRadiance(Point3 o, Vector3 dir, Color outRadiance) {
		// Trace a ray to find incident (direct) radiance
		Ray ray = new Ray(o, dir);
		ray.makeOffsetRay();

		IntersectionRecord lIntRec = new IntersectionRecord();
		Material material = null;
		if (getFirstIntersection(lIntRec, ray)
				&& (material = lIntRec.surface.getMaterial()).isEmitter()) {
			// Hit something -- ask it what its emitted radiance is in our
			// direction
			LuminaireSamplingRecord lSampRec = new LuminaireSamplingRecord();
			lSampRec.set(lIntRec);
			lSampRec.emitDir.set(ray.direction);
			lSampRec.emitDir.scale(-1);
			material.emittedRadiance(lSampRec, outRadiance);

			return;
		}
		// Hit nothing -- background is not direct illumination so return zero
		outRadiance.set(0, 0, 0);
	}

	/**
	 * Adds a surface to the list of surfaces in the scene.
	 * 
	 * @param toAdd
	 *            The surface to add to the scene.
	 */
	public void addSurface(Surface toAdd) {

		// Add the individual triangles of a mesh
		if (toAdd instanceof Mesh) {
			MeshTriangle[] triangles = ((Mesh) toAdd).getTriangles();
			for (int i = 0; i < triangles.length; i++) {
				addSurface(triangles[i]);
			}
			System.err.println(triangles.length + " triangles added");
		} else {
			surfaces.add(toAdd);
			if (toAdd.getMaterial().isEmitter())
				luminaires.add(toAdd);
		}
	}

	public void addLight(PointLight plight) {
		pointLights.add(plight);
	}

	public ArrayList<PointLight> getPointLights() {
		return pointLights;
	}

	/**
	 * Adds a material to the list of materials in the scene.
	 * 
	 * @param toAdd
	 *            the material to add
	 */
	public void addMaterial(Material toAdd) {

		materials.add(toAdd);
	}

	/**
	 * Set outRecord to the first intersection of ray with the scene. Return
	 * true if there was an intersection and false otherwise. If no intersection
	 * was found outRecord is unchanged.
	 * 
	 * @param outRecord
	 *            the output IntersectionRecord
	 * @param ray
	 *            the ray to intesect
	 * @return true if and intersection is found.
	 */
	public boolean getFirstIntersection(IntersectionRecord outRecord, Ray ray) {

		if (accel == null)
			accel = new BoundingVolume(surfaces);

		return accel.getFirstIntersection(outRecord, ray);
	}

	/**
	 * Shadow ray calculations can be considerably accelerated by not bothering
	 * to find the first intersection. This record returns any intersection of
	 * the ray and the surfaces and returns true if one is found.
	 * 
	 * @param outRecord
	 *            the output record for the intersection
	 * @param ray
	 *            the ray to intersect
	 * @return true if any intersection is found
	 */
	public boolean getAnyIntersection(IntersectionRecord outRecord, Ray ray) {

		if (accel == null)
			accel = new BoundingVolume(surfaces);

		return accel.getAnyIntersection(outRecord, ray);
	}

	public Vector3 getBoundingBoxExtents() {

		return ((BoundingVolume) accel).getBoundingBox().getExtents();
	}

	public boolean chooseVisiblePointOnLuminaire(Point2 seed,
			IntersectionRecord iRec, LuminaireSamplingRecord lRec) {
		if (luminaires.size() == 0)
			return false;
		double d = seed.x * luminaires.size();
		int iLum = (int) d;
		seed.x = d - (double) iLum;
		lRec.surface = (Surface) luminaires.get(iLum);
		lRec.surface.chooseSamplePoint(iRec.frame.o, seed, lRec);
		// // check visibility
		// emitDir is from lRec.o to iRec.o
		if ((lRec.iCosine = lRec.emitDir.dot(iRec.frame.w)) > 0)
			return false;
		if ((lRec.lCosine = lRec.emitDir.dot(lRec.frame.w)) < 0)
			return false;

		lRec.shadowRay.set(iRec.frame.o, lRec.frame.o); // from surface to
														// luminaire
		lRec.shadowRay.makeOffsetSegment(1.0 - Ray.EPSILON);
		if (getAnyIntersection(new IntersectionRecord(), lRec.shadowRay))
			return false;
		lRec.pdf /= luminaires.size();
		return true;
		// }
		// return false;
	}

	public boolean chooseVisiblePointOnLuminaire(Point2 seed, Point3 p,
			LuminaireSamplingRecord lRec) {
		if (luminaires.size() == 0)
			return false;
		double d = seed.x * luminaires.size();
		int iLum = (int) d;
		seed.x = d - (double) iLum;
		lRec.surface = (Surface) luminaires.get(iLum);
		lRec.surface.chooseSamplePoint(p, seed, lRec);
		if ((lRec.lCosine = lRec.emitDir.dot(lRec.frame.w)) < 0)
			return false;
		lRec.shadowRay.set(p, lRec.frame.o);
		lRec.shadowRay.makeOffsetSegment(1.0 - Ray.EPSILON);
		if (getAnyIntersection(new IntersectionRecord(), lRec.shadowRay))
			return false;
		lRec.pdf /= luminaires.size();
		return true;
	}

	/**
	 * The probability density describing the behavior of
	 * <chooseVisiblePointOnLuminaire>. Used for volume points.
	 * 
	 * @param p
	 *            the interaction point.
	 * @param lRec
	 *            describes conditions of luminaire sampling.
	 * @return The probability density (with respect to surface area).
	 */
	public double pdfVisiblePointOnLuminaire(Point3 p,
			LuminaireSamplingRecord lRec) {
		return lRec.surface.pdfSamplePoint(p, lRec) / luminaires.size();
	}
}
