package ray.sampling;

import ray.math.Point2;

/**
 * A sample generator is responsible for generating random (or quasi-random)
 * samples on a multidimensional domain. It exists to keep track of stratified
 * sampling patterns.
 * 
 * The idea is that a sample pattern is a set of points in a multidimensional
 * space, which are in some way "nicely distributed" over some dimensions. In
 * principle, the number of dimensions needs to be unlimited, because rendering
 * often includes recursive processes that don't terminate deterministically. We
 * can think of the samples as being like a 2D table: each row is a sample, and
 * each column is a dimension in the problem. For example, the columns might
 * correspond to subpixel position, shadow ray direction, reflected ray
 * direction, etc. To get the benefits of stratified patterns, all the values in
 * a particular column need to be related to one another. The caller is free to
 * assign any desired meaning to the columns, and there's not even any
 * requirement that they be used in order.
 * 
 * A sample generator internally maintains whatever state is required to be able
 * to generate the entries in this table. It generally won't need to generate
 * any samples in a particular column until the first time one is called for. A
 * pattern is assumed to have a fixed number of samples.
 * 
 * @author srm
 */
public interface SampleGenerator {

	/**
	 * @return The number of samples in this generator's pattern
	 */
	int getNumSamples();

	/**
	 * Forget any state and generate a whole new set of samples.
	 */
	void generate();

	/**
	 * Draw a sample from the sampling pattern. The caller explicitly decides
	 * which sample is being retrieved and which dimensions of the domain are
	 * required.
	 * 
	 * @param row
	 *            Which sample of the pattern is being used; 0 <= row <
	 *            getNumSamples(). Two samples drawn using the same index are
	 *            not uniformly distributed (for example, they might always be
	 *            the same point).
	 * @param col
	 *            Which dimensions are being used. Each distinct depth value
	 *            corresponds to its own two dimensions.
	 * @param outPt
	 *            The 2D point, distributed uniformly on the unit square.
	 */
	void sample(int row, int col, Point2 outPt);

}
