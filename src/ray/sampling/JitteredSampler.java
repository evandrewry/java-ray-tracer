package ray.sampling;

import java.util.Random;

import ray.math.Point2;

public class JitteredSampler implements SampleGenerator {

	private static final int stratificationDepth = 30;

	int numSamplesU = 1, numSamplesV = 1;

	int[][] permutations;
	Random random = new Random();

	public JitteredSampler() {
	}

	public void setNumSamplesU(int numSamplesU) {
		this.numSamplesU = numSamplesU;
	}

	public void setNumSamplesV(int numSamplesV) {
		this.numSamplesV = numSamplesV;
	}

	public int getNumSamples() {
		return numSamplesU * numSamplesV;
	}

	public void generate() {
		if (permutations == null) {
			permutations = new int[stratificationDepth][];
			for (int i = 0; i < stratificationDepth; i++) {
				int n = getNumSamples();
				permutations[i] = new int[n];
				for (int j = 0; j < n; j++)
					permutations[i][j] = j;
				for (int j = n - 1; j > 0; j--) {
					int k = random.nextInt(j + 1);
					int temp = permutations[i][j];
					permutations[i][j] = permutations[i][k];
					permutations[i][k] = temp;
				}
			}
		}
	}

	public void sample(int row, int col, Point2 outPt) {
		if (row < stratificationDepth) {
			int i = permutations[row][col];
			int iu = i % numSamplesU;
			int iv = i / numSamplesU;
			outPt.set((iu + random.nextDouble()) / numSamplesU,
					(iv + random.nextDouble()) / numSamplesV);
			return;
		}

		// assert false;
		outPt.set(random.nextDouble(), random.nextDouble());
	}

}
