package example;

import java.util.List;

public class GeometricMean {
    public static double geometricMean(List<Double> numbers) {
        double product = 1.0;

        for (double num : numbers) {
            product *= num;
        }

        return Math.pow(product, 1.0 / numbers.size());
    }

    public static void reuseMe(double product, List<Double> numbers) {
        product = Utility.product(numbers);
    }
}
