package fuzs.visualworkbench.util;

public class MathHelper {

    public static float easeOutQuad(double t, float b, float c, double d) {
        float z = (float) t / (float) d;
        return -c * z * (z - 2.0F) + b;
    }
}
