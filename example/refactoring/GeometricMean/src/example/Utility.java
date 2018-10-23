package example;

import java.util.List;

public class Utility {

    public static double product(List<Double> numbers) {
        return numbers.stream().reduce(1.0, (acc, succ) -> acc * succ);
    }
}
