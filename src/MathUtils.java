package src;

public class MathUtils {
    public static final double EPSILON = 1e-6;

    public static boolean nearlyEqual(double a, double b) {
        // Description: This method compares whether two floating-point numbers are close enough to be considered equal (necessary due to floating-point arithmetic errors).
        // Parameters: Two doubles
        // Return: Boolean representing whether they are nearly equal

        return Math.abs(a-b) < EPSILON;
    }

    public static boolean greater(double a, double b) {
        // Description: This method compares whether one floating-point number is greater than another (necessary due to floating-point arithmetic errors).
        // Parameters: Two doubles
        // Return: Boolean representing whether first is greater than second

        return a-b >= EPSILON;
    }

    public static boolean less(double a, double b) {
        // Description: This method compares whether one floating-point number is less than another (necessary due to floating-point arithmetic errors).
        // Parameters: 
        // Return: Boolean representing whether first is less than second

        return a-b <= -1 * EPSILON;
    }
}
