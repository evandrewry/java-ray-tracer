package ray.accel;

import ray.misc.IntersectionRecord;
import ray.misc.Ray;

public interface AccelerationStructure {

	/**
	 * Find the first intersection of ray with a surface in this structure.
	 *
	 * @param outRecord the output IntersectionRecord.  Unchanged if no intersection.
	 * @param ray the ray to intesect
	 * @return was an intersection found?
	 */
	public boolean getFirstIntersection(IntersectionRecord outRecord, Ray ray);

	/**
	 * Find an intersection of ray with a surface in this structure.
	 *
	 * @param outRecord the output IntersectionRecord.    Unchanged if no intersection.
	 * @param ray the ray to intesect
	 * @return was an intersection found?
	 */
	public boolean getAnyIntersection(IntersectionRecord outRecord, Ray ray);

}