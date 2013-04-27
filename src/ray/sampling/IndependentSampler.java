package ray.sampling;

import java.util.Random;

import ray.math.Point2;

/**
 * A trivial implementation of SampleGenerator in which all the samples are
 * independent and not related in any way to one another.
 * 
 * @author srm
 */
public class IndependentSampler implements SampleGenerator {

	int numSamples = 1;
	Random random = new Random(1);

	public IndependentSampler() {
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	public int getNumSamples() {
		return numSamples;
	}

	public void generate() {
	}

	public void sample(int row, int col, Point2 outPt) {
		outPt.set(random.nextDouble(), random.nextDouble());
	}

}
