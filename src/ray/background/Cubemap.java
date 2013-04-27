package ray.background;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;

public class Cubemap implements Background {

	// Parameters
	String filename;
	double scaleFactor = 1.0;

	int width, height, blockSz;
	int mapBits; // 2^(mapBits-1) < width*height <= 2^mapBits
	float[] imageData;

	public Cubemap() {
	}

	public void setFilename(String filename) {
		this.filename = filename;

		PNMHeaderInfo hdr = new PNMHeaderInfo();
		imageData = readPFM(new File(filename), hdr);

		width = hdr.width;
		height = hdr.height;
		blockSz = width / 3;
		for (mapBits = 0; (1 << mapBits) < width * height; mapBits++)
			;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	Point2 faceUV = new Point2();

	public void evaluate(Vector3 dir, Color outRadiance) {
		// W4160 TODO (bonus)
		// This function evaluates the color values specified by the given
		// direction
	}

	public void generate(Point2 seed, Vector3 outDirection) {
		// W4160 TODO (bonus)
		// generate a sampled ray direction
	}

	public double pdf(Vector3 dir) {
		// W4160 TODO (bonus)
		// PDF of the sampled ray along the direction given by dir

		// I leave the code from my impelementation as a hint
		// int iFace = dirToFace(dir, faceUV);
		// double u = faceUV.x;
		// double v = faceUV.y;

		// quantize to pixel, look up
		// int k = faceToIndex(iFace, faceUV);
		// double pixelProb = cumProb[k+1] - cumProb[k];

		// pdf is uniform wrt area on cube and is equal to probability / pixel
		// area
		// this is probability / (2/blockSz)^2
		// pdf on sphere is that over cos^3 alpha. cos alpha = 1 / sqrt(1 + u^2
		// + v^2).
		// return 0.25 * blockSz*blockSz * pixelProb * Math.pow(1 + u*u + v*v,
		// 1.5);
		return 0.;
	}

	public static class PNMHeaderInfo {
		int width, height, bands;
		float maxval;
	}

	public float[] readPFM(File pfmFile, PNMHeaderInfo hdr) {

		try {

			FileInputStream inf = new FileInputStream(pfmFile);
			DataInputStream inSt = new DataInputStream(inf);
			FileChannel inCh = inf.getChannel();

			int imageSize = readPPMHeader(inSt, hdr);
			if (imageSize == -1)
				return null;

			System.err.println("reading FP image: " + hdr.width + "x"
					+ hdr.height + "x" + hdr.bands);

			ByteBuffer imageBuffer = ByteBuffer.allocate(imageSize * 4);
			imageBuffer.order(ByteOrder.LITTLE_ENDIAN);

			imageBuffer.clear();
			inCh.read(imageBuffer);
			float[] imageData = new float[imageSize];
			imageBuffer.flip();
			imageBuffer.asFloatBuffer().get(imageData);
			return imageData;

		} catch (FileNotFoundException e) {
			System.err.println("readPFM: file not found: " + pfmFile.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int readPPMHeader(DataInputStream in, PNMHeaderInfo info)
			throws IOException {

		// Read PNM header of the form 'P[F]\n<width> <height>\n<maxval>\n'
		if (in.readByte() != 'P') {
			System.err.println("readPFM: not a PNM file");
			return -1;
		}

		byte magic = in.readByte();
		int bands;
		if (magic == 'F')
			bands = 3;
		else {
			System.err.println("readPFM: Unsupported PNM variant 'P" + magic
					+ "'");
			return -1;
		}

		int width = Integer.parseInt(readWord(in));
		int height = Integer.parseInt(readWord(in));
		int imageSize = width * height * bands;

		float maxval = Float.parseFloat(readWord(in));
		if (info != null) {
			info.width = width;
			info.height = height;
			info.bands = bands;
			info.maxval = maxval;
		}
		return imageSize;
	}

	static String readWord(DataInputStream in) throws IOException {
		char c;
		String s = "";
		while (Character.isWhitespace(c = (char) in.readByte()))
			;
		s += c;
		while (!Character.isWhitespace(c = (char) in.readByte()))
			s += c;
		return s;
	}

}
