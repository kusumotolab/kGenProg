package example;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GeometricMeanTest {
    @Test
    public void geometricMean_01() {
        Double[] array = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        List<Double> list = Arrays.asList(array);
        double expected = 4.528728688116765;
        double actual = GeometricMean.geometricMean(list);
        Assert.assertEquals(expected, actual, 0.0);
    }

    @Test
    public void geometricMean_02() {
        Double[] array = {15.0, 26.0, 30.0, 20.0, 12.0, 6.0, 8.0, 7.0, 14.0, 10.0};
        List<Double> list = Arrays.asList(array);
        double expected = 12.9445346287095;
        double actual = GeometricMean.geometricMean(list);
        Assert.assertEquals(expected, actual, 0.000001);
    }

    @Test
    public void geometricMean_03() {
        Double[] array = {8.0, 12.0, 5.0, 24.0, 26.0, 6.0, 22.0, 20.0, 20.0, 1.0};
        List<Double> list = Arrays.asList(array);
        double expected = 10.469018685709251;
        double actual = GeometricMean.geometricMean(list);
        Assert.assertEquals(expected, actual, 0.0);
    }

    @Test
    public void geometricMean_04() {
        List<Double> list = Collections.emptyList();
        double expected = Double.NaN;
        double actual = GeometricMean.geometricMean(list);
        Assert.assertEquals(expected, actual, 0.0);
    }
}
