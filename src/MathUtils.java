package src;

public class MathUtils {
    public static final double EPSILON = 1e-6;

    public static boolean nearlyEqual(double a, double b) {
        return Math.abs(a-b) < EPSILON;
    }

    public static boolean greater(double a, double b) {
        return a-b >= EPSILON;
    }

    public static boolean less(double a, double b) {
        return a-b <= -1 * EPSILON;
    }
}
