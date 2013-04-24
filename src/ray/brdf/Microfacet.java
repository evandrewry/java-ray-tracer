package ray.brdf;

import java.util.Random;
import carbine.MathExt;
import ray.math.Frame3;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.Image;

public class Microfacet implements BRDF {    
    
    // The reflectance of the diffuse component of the BRDF
    private Color diffuseBrdf = new Color();
    
    // Fresnel term
    private Fresnel fresnel = new Fresnel();
    private BeckmannDistribution normalDistri = new BeckmannDistribution();
    private SmithShadowing shadowing = new SmithShadowing();
    
    /** The relative weight for sampling the specular component */
    private double specularSamplingWeight = 0.5;

    private double specularBrdfWeight = 1;
    private double diffuseBrdfWeight  = 1;
    
    /** beckmann roughness */
    private double roughness = 0.1;
    /** square of the beckmann roughness */
    private double roughnessSqr = 0.01;
    

    // For the benefit of the parser
    public Microfacet() {}
    
    public void setDiffuseReflectance(Color diffuseReflectance) { 
        this.diffuseBrdf = diffuseReflectance;
        this.diffuseBrdf.scale(diffuseBrdfWeight/Math.PI);
    }
    
    public void setAlpha_b(double alpha_b) {
        roughness = alpha_b;
        roughnessSqr = alpha_b * alpha_b;      
    }
    
    public void setN(double n) {
        fresnel.setN(n); 
    }
    
    public void setSpecularSamplingWeight(double specularSamplingWeight) { 
        this.specularSamplingWeight = specularSamplingWeight; 
    }

    private Random random = new Random();
    /**
     * Given the incident direction, reflection direction, compute the BRDF value.
     * The w component fo Frame3 aligns with the surface normal
     * 
     * @param incDir  incident direction in canonical coordinates
     * @param outDir outgoing direction also in canonical coordinates
     * @see ray.brdf.BRDF#evaluate(ray.math.Frame3, ray.math.Vector3,
     *      ray.math.Vector3, ray.misc.Color)
     */
    public void evaluate(Frame3 frame, Vector3 incDir, 
                         Vector3 outDir, Color outBRDFValue) {
        outBRDFValue.set(diffuseBrdf);
        
        Vector3 vecM = new Vector3(incDir);
        vecM.add(outDir);
        vecM.normalize();
        
        double dim = incDir.dot(vecM);
        double din = incDir.dot(frame.w);
        double don = outDir.dot(frame.w);
        double dmn = vecM.dot(frame.w);
        
        if ( don < 0 ) return;
        if ( dim < 0 || din < 0 || dmn < 0 )
        {
            System.err.println("DEBUG ERROR! evaluate");
        }
        double rs = fresnel.eval(dim) *
                    shadowing.eval(incDir, outDir, frame.w) *
                    normalDistri.eval(dmn) /
                    (4 * din * don);
        outBRDFValue.add(rs*specularBrdfWeight);        
    }
    
    /**    
     * f * |o.n| / Po = 4 * f * |o.n| * |i.m| / (D(m)*|m.n|)
     * 
     * @param seed is uniformly distributed in square [0...1]
     * @param fixedDir outgoing direction is specified
     * @param dir      incident direction is generated according to half angle direction which is
     *        generated from random seed
     */
    public void generate(Frame3 frame, Vector3 fixedDir, Vector3 dir, 
                         Point2 seed, Color outWeight) {
        Vector3 vecM;
        double dim;
        //// uniform samples for diffuse component
        if ( random.nextDouble() >= specularSamplingWeight ) {
            Geometry.squareToPSAHemisphere(seed, dir);
            frame.frameToCanonical(dir);            // transform to global coordinate

            vecM = new Vector3(fixedDir);
            vecM.add(dir);
            vecM.normalize();

            dim = dir.dot(vecM);
        } else {   //// importance smapling for specular component
            vecM = new Vector3();
            seed2Dir(seed, vecM);
            frame.frameToCanonical(vecM);               // transform to global coordinate
        
            dim = fixedDir.dot(vecM);
            dir.set(vecM);
            dir.scale(2. * dim);
            dir.sub(fixedDir);
        }

        double din = dir.dot(frame.w);
        double dmn = vecM.dot(frame.w);
        double don = fixedDir.dot(frame.w);
        
        if ( din < 0 ) {
            outWeight.set(0);
            return;
        }
        outWeight.set(diffuseBrdf);
        outWeight.scale(don);                           // brdf * cos(theta)
        double nd = normalDistri.eval(dmn);
        outWeight.add(specularBrdfWeight * fresnel.eval(dim) *
                      shadowing.eval(dir, fixedDir, frame.w) * nd /
                      (4. * din));
        outWeight.scale(1. / (
                    specularSamplingWeight*nd*dmn/(4.*dim) + 
                    (1. - specularSamplingWeight)*don/Math.PI));
    }

    /**
     * Evaluate the PDF used by generate(), with respect to the solid angle measure.
     * Given the <fixedDir>, what is the PDF value to select the direction at <dir>?
     * 
     * For uniform sample, the pdf is p(dir|uniform) = cos(theta)/PI
     * For importance sample, the pdf is p(dir|importance) = Po
     * So the overall p(dir) = p(dir, uniform)+p(dir, importance)
     *                       = cos(theta)/PI * (1-weight) + Po * weight
     * The value of Po = Pm * ||partial(half_angle)/partial(outDir)||
     * 
     * @param fixedDir The fixed argument of the BRDF
     * @param dir The variable argument
     * @return The pdf of generate() choosing <dir> if given <fixedDir>
     */
    public double pdf(Frame3 frame, Vector3 fixedDir, Vector3 dir) {
        Vector3 vecM = new Vector3(fixedDir);
        vecM.add(dir);
        vecM.normalize();
        
        double dmn = vecM.dot(frame.w);
        double dim = fixedDir.dot(vecM);
        return specularSamplingWeight*(normalDistri.eval(dmn)*dmn / (4*dim)) + 
                (1. - specularSamplingWeight)*dir.dot(frame.w)/Math.PI;
    }
    
    private void seed2Dir(Point2 seed, Vector3 dir) {
        double theta = Math.atan(Math.sqrt(-roughnessSqr*Math.log(1 - seed.x)));
        double phi = 2 * Math.PI * seed.y;
        
        dir.x = Math.sin(theta) * Math.cos(phi);
        dir.y = Math.sin(theta) * Math.sin(phi);
        dir.z = Math.cos(theta);
    }
    
    private class SmithShadowing {
        
        public SmithShadowing() { }
        
        public double eval(Vector3 vecIn, Vector3 vecOut, Vector3 vecN) {
            return monoDirShadowing(vecIn.dot(vecN))*monoDirShadowing(vecOut.dot(vecN));
        }
        
        private double monoDirShadowing(double co) {
            if ( co < MathExt.DOUBLE_EPS ) return 0;
            double ta = Math.sqrt(1. - co * co) / co;
            if ( ta < MathExt.DOUBLE_EPS ) return 1;
            double a = 1. / (roughness * ta);
            
            return 2. / (1. + MathExt.erf(a) + Math.exp(-a*a)*MathExt.M_1_SQRTPI);
        }
    }
    
    private class BeckmannDistribution {
        
        public BeckmannDistribution() {}
                
        /**
         * @param vecN  normal direction
         * @param vecM  half angle direction
         * @return
         */
        //public double eval(Vector3 vecN, Vector3 vecM) {
        //    return eval(vecN.dot(vecM));
        //}
        
        /**
         * @param cosine the cosine value of angle between normal direction and
         *               half angle direction
         * @return
         */
        public double eval(double cosine) {
            if ( cosine < MathExt.DOUBLE_EPS ) return 0.;
            double c2 = cosine * cosine;    // cosine square
            double t2 = (1. - c2) / c2;     // tangent square
            return Math.exp(-t2/roughnessSqr) / (Math.PI*roughnessSqr*c2*c2);
        }
    }
    
    /**
     * This class is for computing the fresneal term in macrofacet BRDF model
     */
    private class Fresnel {
        
        private double k = 1.5*1.5 - 1.;
        
        //public Fresnel(double n) {
        //    k = n*n - 1.;
        //}
        
        public Fresnel() { }
        
        public void setN(double n) {
            k = n*n - 1.;
        }
        
        /**
         * @param vecIn incident angle
         * @param vecMid Half angle
         * @return
         */
        //public double eval(Vector3 vecIn, Vector3 vecMid) {
        //    return eval(vecIn.dot(vecMid));
        //}
        
        /**
         * @param cosine The consine value of the angle between
         * @return
         */
        public double eval(double cosine) {
            double g = Math.sqrt(k + cosine*cosine);
            double gpc = g + cosine;
            double gmc = g - cosine;
            double vu  = cosine*gpc - 1.;
            double vd  = cosine*gmc + 1.;

            return 0.5*gmc*gmc*(1. + (vu*vu)/(vd*vd))/(gpc*gpc);
        }    
    }
    
    // handy testing functions
    public static void main(String argv[]) {
        
        int n = 200;
        
        Vector3 incDir = new Vector3(-2, 0, 1);
        incDir.normalize();
        
        Microfacet brdf = new Microfacet();
        brdf.setDiffuseReflectance(new Color(.3, .3, .3));
        brdf.setAlpha_b(0.08);
        brdf.setN(1.5);
        
        // Really ought to work for a Lambertian BRDF
        //Lambertian brdf = new Lambertian();
        //brdf.setReflectance(new Color(0.5, 0.5, 0.5));
        
        Frame3 frame = new Frame3();
        
        System.err.println("Testing eval");
        Vector3 outDir = new Vector3();
        Color brdfVal = new Color();
        Image img = new Image(n, n);
        for (int iy = 0; iy < n; iy++)
            for (int ix = 0; ix < n; ix++) {
                double u = (ix + 0.5) / (double) n;
                double v = (iy + 0.5) / (double) n;
                double x = 2*u - 1;
                double y = 2*v - 1;
                outDir.set(x, y, Math.sqrt(1 - x*x - y*y));
                brdf.evaluate(frame, incDir, outDir, brdfVal);
                img.setPixelColor(brdfVal, ix, iy);
            }
        writeLogImage(img, "1ufacet-test_eval.png");
        
        System.err.println("Testing pdf");
        for (int iy = 0; iy < n; iy++)
            for (int ix = 0; ix < n; ix++) {
                double u = (ix + 0.5) / (double) n;
                double v = (iy + 0.5) / (double) n;
                double x = 2*u - 1;
                double y = 2*v - 1;
                outDir.set(x, y, Math.sqrt(1 - x*x - y*y));
                double pdf = brdf.pdf(frame, incDir, outDir);
                img.setPixelRGB(0, pdf, 0, ix, iy);
            }
        writeLogImage(img, "2ufacet-test_pdf.png");
        
        System.err.println("Testing ratio");
        for (int iy = 0; iy < n; iy++)
            for (int ix = 0; ix < n; ix++) {
                double u = (ix + 0.5) / (double) n;
                double v = (iy + 0.5) / (double) n;
                double x = 2*u - 1;
                double y = 2*v - 1;
                outDir.set(x, y, Math.sqrt(1 - x*x - y*y));
                brdf.evaluate(frame, incDir, outDir, brdfVal);
                double pdf = brdf.pdf(frame, incDir, outDir);
                brdfVal.scale(1/pdf);
                img.setPixelColor(brdfVal, ix, iy);
            }
        writeLogImage(img, "3ufacet-test_eval-over-pdf.png");
        
        System.err.println("Checking PDF");
        img = new Image(n, n);
        Color wt = new Color();
        Color pixVal = new Color();
        Point2 seed = new Point2();
        double intPDF = 0;
        int m = 1000;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                seed.set((i + Math.random()) /  m, (j + Math.random()) / m);
                
                // Generate a random direction and contribute to a pixel in the test image
                brdf.generate(frame, incDir, outDir, seed, wt);
                double u = (outDir.x + 1) / 2;
                double v = (outDir.y + 1) / 2;
                int ix = (int) (n * u);
                int iy = (int) (n * v);
                img.getPixelColor(pixVal, ix, iy);
                double pixSA = (2.0 / n) * (2.0 / n) / outDir.z;
                pixVal.g += 1.0 / pixSA / (m*m);
                img.setPixelColor(pixVal, ix, iy);
                
                // Also compute the integral of the PDF
                Geometry.squareToHemisphere(seed, outDir);
                double ppdf = brdf.pdf(frame, incDir, outDir);
                if ( ppdf < 0 ) {
                    System.err.println("ERROR: ppdf");
                    System.exit(1);
                }
                intPDF = intPDF + ppdf;
            }
        }
        intPDF = intPDF * 2 * Math.PI / (m*m);
        writeLogImage(img, "4ufacet-test_hist.png");
        System.err.println("PDF integrates to " + intPDF);
    }

    private static void writeLogImage(Image img, String fname) {
        Color pixVal = new Color();
        double log10 = Math.log(10);
        for (int iy = 0; iy < img.getHeight(); iy++)
            for (int ix = 0; ix < img.getWidth(); ix++) {
                img.getPixelColor(pixVal, ix, iy);
                pixVal.r = (3 + Math.log(pixVal.r) / log10) / 6;
                pixVal.g = (3 + Math.log(pixVal.g) / log10) / 6;
                pixVal.b = (3 + Math.log(pixVal.b) / log10) / 6;
                img.setPixelColor(pixVal, ix, iy);
            }
        img.write(fname);
    }

}
