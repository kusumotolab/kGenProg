/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math3.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.random.RandomDataImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link ArithmeticUtils} class.
 *
 * @version $Id$
 */
public class ArithmeticUtilsTest {

    /** cached binomial coefficients */
    private static final List<Map<Integer, Long>> binomialCache = new ArrayList<Map<Integer, Long>>();

    /** Verify that b(0,0) = 1 */
    @Test
    public void test0Choose0() {
        Assert.assertEquals(ArithmeticUtils.binomialCoefficientDouble(0, 0), 1d, 0);
        Assert.assertEquals(ArithmeticUtils.binomialCoefficientLog(0, 0), 0d, 0);
        Assert.assertEquals(ArithmeticUtils.binomialCoefficient(0, 0), 1);
    }

    @Test
    public void testAddAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticUtils.addAndCheck(big, 0));
        try {
            ArithmeticUtils.addAndCheck(big, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticUtils.addAndCheck(bigNeg, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testAddAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticUtils.addAndCheck(max, 0L));
        Assert.assertEquals(min, ArithmeticUtils.addAndCheck(min, 0L));
        Assert.assertEquals(max, ArithmeticUtils.addAndCheck(0L, max));
        Assert.assertEquals(min, ArithmeticUtils.addAndCheck(0L, min));
        Assert.assertEquals(1, ArithmeticUtils.addAndCheck(-1L, 2L));
        Assert.assertEquals(1, ArithmeticUtils.addAndCheck(2L, -1L));
        Assert.assertEquals(-3, ArithmeticUtils.addAndCheck(-2L, -1L));
        Assert.assertEquals(min, ArithmeticUtils.addAndCheck(min + 1, -1L));
        testAddAndCheckLongFailure(max, 1L);
        testAddAndCheckLongFailure(min, -1L);
        testAddAndCheckLongFailure(1L, max);
        testAddAndCheckLongFailure(-1L, min);
    }


    @Test
    public void testBinomialCoefficient() {
        long[] bcoef5 = {
            1,
            5,
            10,
            10,
            5,
            1 };
        long[] bcoef6 = {
            1,
            6,
            15,
            20,
            15,
            6,
            1 };
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals("5 choose " + i, bcoef5[i], ArithmeticUtils.binomialCoefficient(5, i));
        }
        for (int i = 0; i < 7; i++) {
            Assert.assertEquals("6 choose " + i, bcoef6[i], ArithmeticUtils.binomialCoefficient(6, i));
        }

        for (int n = 1; n < 10; n++) {
            for (int k = 0; k <= n; k++) {
                Assert.assertEquals(n + " choose " + k, binomialCoefficient(n, k), ArithmeticUtils.binomialCoefficient(n, k));
                Assert.assertEquals(n + " choose " + k, binomialCoefficient(n, k), ArithmeticUtils.binomialCoefficientDouble(n, k), Double.MIN_VALUE);
                Assert.assertEquals(n + " choose " + k, FastMath.log(binomialCoefficient(n, k)), ArithmeticUtils.binomialCoefficientLog(n, k), 10E-12);
            }
        }

        int[] n = { 34, 66, 100, 1500, 1500 };
        int[] k = { 17, 33, 10, 1500 - 4, 4 };
        for (int i = 0; i < n.length; i++) {
            long expected = binomialCoefficient(n[i], k[i]);
            Assert.assertEquals(n[i] + " choose " + k[i], expected,
                ArithmeticUtils.binomialCoefficient(n[i], k[i]));
            Assert.assertEquals(n[i] + " choose " + k[i], expected,
                ArithmeticUtils.binomialCoefficientDouble(n[i], k[i]), 0.0);
            Assert.assertEquals("log(" + n[i] + " choose " + k[i] + ")", FastMath.log(expected),
                ArithmeticUtils.binomialCoefficientLog(n[i], k[i]), 0.0);
        }
    }

    @Test
    public void testBinomialCoefficientFail() {
        try {
            ArithmeticUtils.binomialCoefficient(4, 5);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            ArithmeticUtils.binomialCoefficientDouble(4, 5);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            ArithmeticUtils.binomialCoefficientLog(4, 5);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            ArithmeticUtils.binomialCoefficient(-1, -2);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticUtils.binomialCoefficientDouble(-1, -2);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticUtils.binomialCoefficientLog(-1, -2);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            ArithmeticUtils.binomialCoefficient(67, 30);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        try {
            ArithmeticUtils.binomialCoefficient(67, 34);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        double x = ArithmeticUtils.binomialCoefficientDouble(1030, 515);
        Assert.assertTrue("expecting infinite binomial coefficient", Double
            .isInfinite(x));
    }

    /**
     * Tests correctness for large n and sharpness of upper bound in API doc
     * JIRA: MATH-241
     */
    @Test
    public void testBinomialCoefficientLarge() throws Exception {
        // This tests all legal and illegal values for n <= 200.
        for (int n = 0; n <= 200; n++) {
            for (int k = 0; k <= n; k++) {
                long ourResult = -1;
                long exactResult = -1;
                boolean shouldThrow = false;
                boolean didThrow = false;
                try {
                    ourResult = ArithmeticUtils.binomialCoefficient(n, k);
                } catch (MathArithmeticException ex) {
                    didThrow = true;
                }
                try {
                    exactResult = binomialCoefficient(n, k);
                } catch (MathArithmeticException ex) {
                    shouldThrow = true;
                }
                Assert.assertEquals(n + " choose " + k, exactResult, ourResult);
                Assert.assertEquals(n + " choose " + k, shouldThrow, didThrow);
                Assert.assertTrue(n + " choose " + k, (n > 66 || !didThrow));

                if (!shouldThrow && exactResult > 1) {
                    Assert.assertEquals(n + " choose " + k, 1.,
                        ArithmeticUtils.binomialCoefficientDouble(n, k) / exactResult, 1e-10);
                    Assert.assertEquals(n + " choose " + k, 1,
                        ArithmeticUtils.binomialCoefficientLog(n, k) / FastMath.log(exactResult), 1e-10);
                }
            }
        }

        long ourResult = ArithmeticUtils.binomialCoefficient(300, 3);
        long exactResult = binomialCoefficient(300, 3);
        Assert.assertEquals(exactResult, ourResult);

        ourResult = ArithmeticUtils.binomialCoefficient(700, 697);
        exactResult = binomialCoefficient(700, 697);
        Assert.assertEquals(exactResult, ourResult);

        // This one should throw
        try {
            ArithmeticUtils.binomialCoefficient(700, 300);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // Expected
        }

        int n = 10000;
        ourResult = ArithmeticUtils.binomialCoefficient(n, 3);
        exactResult = binomialCoefficient(n, 3);
        Assert.assertEquals(exactResult, ourResult);
        Assert.assertEquals(1, ArithmeticUtils.binomialCoefficientDouble(n, 3) / exactResult, 1e-10);
        Assert.assertEquals(1, ArithmeticUtils.binomialCoefficientLog(n, 3) / FastMath.log(exactResult), 1e-10);

    }

    @Test
    public void testFactorial() {
        for (int i = 1; i < 21; i++) {
            Assert.assertEquals(i + "! ", factorial(i), ArithmeticUtils.factorial(i));
            Assert.assertEquals(i + "! ", factorial(i), ArithmeticUtils.factorialDouble(i), Double.MIN_VALUE);
            Assert.assertEquals(i + "! ", FastMath.log(factorial(i)), ArithmeticUtils.factorialLog(i), 10E-12);
        }

        Assert.assertEquals("0", 1, ArithmeticUtils.factorial(0));
        Assert.assertEquals("0", 1.0d, ArithmeticUtils.factorialDouble(0), 1E-14);
        Assert.assertEquals("0", 0.0d, ArithmeticUtils.factorialLog(0), 1E-14);
    }

    @Test
    public void testFactorialFail() {
        try {
            ArithmeticUtils.factorial(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticUtils.factorialDouble(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticUtils.factorialLog(-1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
        try {
            ArithmeticUtils.factorial(21);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // ignored
        }
        Assert.assertTrue("expecting infinite factorial value", Double.isInfinite(ArithmeticUtils.factorialDouble(171)));
    }

    @Test
    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, ArithmeticUtils.gcd(0, 0));

        Assert.assertEquals(b, ArithmeticUtils.gcd(0, b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.gcd(0, -b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(-a, 0));

        Assert.assertEquals(10, ArithmeticUtils.gcd(a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(a, -b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, -b));

        Assert.assertEquals(1, ArithmeticUtils.gcd(a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(a, -c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, -c));

        Assert.assertEquals(3 * (1<<15), ArithmeticUtils.gcd(3 * (1<<20), 9 * (1<<15)));

        Assert.assertEquals(Integer.MAX_VALUE, ArithmeticUtils.gcd(Integer.MAX_VALUE, 0));
        Assert.assertEquals(Integer.MAX_VALUE, ArithmeticUtils.gcd(-Integer.MAX_VALUE, 0));
        Assert.assertEquals(1<<30, ArithmeticUtils.gcd(1<<30, -Integer.MIN_VALUE));
        try {
            // gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(Integer.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(0, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testGcdConsistency() {
        int[] primeList = {19, 23, 53, 67, 73, 79, 101, 103, 111, 131};
        ArrayList<Integer> primes = new ArrayList<Integer>();
        for (int i = 0; i < primeList.length; i++) {
            primes.add(Integer.valueOf(primeList[i]));
        }
        RandomDataImpl randomData = new RandomDataImpl();
        for (int i = 0; i < 20; i++) {
            Object[] sample = randomData.nextSample(primes, 4);
            int p1 = ((Integer) sample[0]).intValue();
            int p2 = ((Integer) sample[1]).intValue();
            int p3 = ((Integer) sample[2]).intValue();
            int p4 = ((Integer) sample[3]).intValue();
            int i1 = p1 * p2 * p3;
            int i2 = p1 * p2 * p4;
            int gcd = p1 * p2;
            Assert.assertEquals(gcd, ArithmeticUtils.gcd(i1, i2));
            long l1 = i1;
            long l2 = i2;
            Assert.assertEquals(gcd, ArithmeticUtils.gcd(l1, l2));
        }
    }

    @Test
    public void  testGcdLong(){
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, ArithmeticUtils.gcd(0L, 0));

        Assert.assertEquals(b, ArithmeticUtils.gcd(0, b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.gcd(0, -b));
        Assert.assertEquals(a, ArithmeticUtils.gcd(-a, 0));

        Assert.assertEquals(10, ArithmeticUtils.gcd(a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(a, -b));
        Assert.assertEquals(10, ArithmeticUtils.gcd(-a, -b));

        Assert.assertEquals(1, ArithmeticUtils.gcd(a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(a, -c));
        Assert.assertEquals(1, ArithmeticUtils.gcd(-a, -c));

        Assert.assertEquals(3L * (1L<<45), ArithmeticUtils.gcd(3L * (1L<<50), 9L * (1L<<45)));

        Assert.assertEquals(1L<<45, ArithmeticUtils.gcd(1L<<45, Long.MIN_VALUE));

        Assert.assertEquals(Long.MAX_VALUE, ArithmeticUtils.gcd(Long.MAX_VALUE, 0L));
        Assert.assertEquals(Long.MAX_VALUE, ArithmeticUtils.gcd(-Long.MAX_VALUE, 0L));
        Assert.assertEquals(1, ArithmeticUtils.gcd(60247241209L, 153092023L));
        try {
            // gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
            ArithmeticUtils.gcd(Long.MIN_VALUE, 0);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticUtils.gcd(0, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticUtils.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }


    @Test
    public void testLcm() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assert.assertEquals(0, ArithmeticUtils.lcm(0, b));
        Assert.assertEquals(0, ArithmeticUtils.lcm(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.lcm(1, b));
        Assert.assertEquals(a, ArithmeticUtils.lcm(a, 1));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, -b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, -b));
        Assert.assertEquals(2310, ArithmeticUtils.lcm(a, c));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        Assert.assertEquals((1<<20)*15, ArithmeticUtils.lcm((1<<20)*3, (1<<20)*5));

        // Special case
        Assert.assertEquals(0, ArithmeticUtils.lcm(0, 0));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Integer.MIN_VALUE, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Integer.MIN_VALUE, 1<<20);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            ArithmeticUtils.lcm(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testLcmLong() {
        long a = 30;
        long b = 50;
        long c = 77;

        Assert.assertEquals(0, ArithmeticUtils.lcm(0, b));
        Assert.assertEquals(0, ArithmeticUtils.lcm(a, 0));
        Assert.assertEquals(b, ArithmeticUtils.lcm(1, b));
        Assert.assertEquals(a, ArithmeticUtils.lcm(a, 1));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(a, -b));
        Assert.assertEquals(150, ArithmeticUtils.lcm(-a, -b));
        Assert.assertEquals(2310, ArithmeticUtils.lcm(a, c));

        Assert.assertEquals(Long.MAX_VALUE, ArithmeticUtils.lcm(60247241209L, 153092023L));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        Assert.assertEquals((1L<<50)*15, ArithmeticUtils.lcm((1L<<45)*3, (1L<<50)*5));

        // Special case
        Assert.assertEquals(0L, ArithmeticUtils.lcm(0L, 0L));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Long.MIN_VALUE, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            ArithmeticUtils.lcm(Long.MIN_VALUE, 1<<20);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }

        Assert.assertEquals((long) Integer.MAX_VALUE * (Integer.MAX_VALUE - 1),
            ArithmeticUtils.lcm((long)Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
        try {
            ArithmeticUtils.lcm(Long.MAX_VALUE, Long.MAX_VALUE - 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testMulAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticUtils.mulAndCheck(big, 1));
        try {
            ArithmeticUtils.mulAndCheck(big, 2);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticUtils.mulAndCheck(bigNeg, 2);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testMulAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticUtils.mulAndCheck(max, 1L));
        Assert.assertEquals(min, ArithmeticUtils.mulAndCheck(min, 1L));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(max, 0L));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(min, 0L));
        Assert.assertEquals(max, ArithmeticUtils.mulAndCheck(1L, max));
        Assert.assertEquals(min, ArithmeticUtils.mulAndCheck(1L, min));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(0L, max));
        Assert.assertEquals(0L, ArithmeticUtils.mulAndCheck(0L, min));
        Assert.assertEquals(1L, ArithmeticUtils.mulAndCheck(-1L, -1L));
        Assert.assertEquals(min, ArithmeticUtils.mulAndCheck(min / 2, 2));
        testMulAndCheckLongFailure(max, 2L);
        testMulAndCheckLongFailure(2L, max);
        testMulAndCheckLongFailure(min, 2L);
        testMulAndCheckLongFailure(2L, min);
        testMulAndCheckLongFailure(min, -1L);
        testMulAndCheckLongFailure(-1L, min);
    }

    @Test
    public void testSubAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        Assert.assertEquals(big, ArithmeticUtils.subAndCheck(big, 0));
        Assert.assertEquals(bigNeg + 1, ArithmeticUtils.subAndCheck(bigNeg, -1));
        Assert.assertEquals(-1, ArithmeticUtils.subAndCheck(bigNeg, -big));
        try {
            ArithmeticUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
        try {
            ArithmeticUtils.subAndCheck(bigNeg, 1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
        }
    }

    @Test
    public void testSubAndCheckErrorMessage() {
        int big = Integer.MAX_VALUE;
        try {
            ArithmeticUtils.subAndCheck(big, -1);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            Assert.assertTrue(ex.getMessage().length() > 1);
        }
    }

    @Test
    public void testSubAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        Assert.assertEquals(max, ArithmeticUtils.subAndCheck(max, 0));
        Assert.assertEquals(min, ArithmeticUtils.subAndCheck(min, 0));
        Assert.assertEquals(-max, ArithmeticUtils.subAndCheck(0, max));
        Assert.assertEquals(min + 1, ArithmeticUtils.subAndCheck(min, -1));
        // min == -1-max
        Assert.assertEquals(-1, ArithmeticUtils.subAndCheck(-max - 1, -max));
        Assert.assertEquals(max, ArithmeticUtils.subAndCheck(-1, -1 - max));
        testSubAndCheckLongFailure(0L, min);
        testSubAndCheckLongFailure(max, -1L);
        testSubAndCheckLongFailure(min, 1L);
    }

    @Test
    public void testPow() {

        Assert.assertEquals(1801088541, ArithmeticUtils.pow(21, 7));
        Assert.assertEquals(1, ArithmeticUtils.pow(21, 0));
        try {
            ArithmeticUtils.pow(21, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541, ArithmeticUtils.pow(21, 7l));
        Assert.assertEquals(1, ArithmeticUtils.pow(21, 0l));
        try {
            ArithmeticUtils.pow(21, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541l, ArithmeticUtils.pow(21l, 7));
        Assert.assertEquals(1l, ArithmeticUtils.pow(21l, 0));
        try {
            ArithmeticUtils.pow(21l, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(1801088541l, ArithmeticUtils.pow(21l, 7l));
        Assert.assertEquals(1l, ArithmeticUtils.pow(21l, 0l));
        try {
            ArithmeticUtils.pow(21l, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        BigInteger twentyOne = BigInteger.valueOf(21l);
        Assert.assertEquals(BigInteger.valueOf(1801088541l), ArithmeticUtils.pow(twentyOne, 7));
        Assert.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, 0));
        try {
            ArithmeticUtils.pow(twentyOne, -7);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(BigInteger.valueOf(1801088541l), ArithmeticUtils.pow(twentyOne, 7l));
        Assert.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, 0l));
        try {
            ArithmeticUtils.pow(twentyOne, -7l);
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        Assert.assertEquals(BigInteger.valueOf(1801088541l), ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(7l)));
        Assert.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, BigInteger.ZERO));
        try {
            ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(-7l));
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException e) {
            // expected behavior
        }

        BigInteger bigOne =
            new BigInteger("1543786922199448028351389769265814882661837148" +
                           "4763915343722775611762713982220306372888519211" +
                           "560905579993523402015636025177602059044911261");
        Assert.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, 103));
        Assert.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, 103l));
        Assert.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(103l)));

    }

    @Test
    public void testIsPowerOfTwo() {
        final int n = 1025;
        final boolean[] expected = new boolean[n];
        Arrays.fill(expected, false);
        for (int i = 1; i < expected.length; i *= 2) {
            expected[i] = true;
        }
        for (int i = 0; i < expected.length; i++) {
            final boolean actual = ArithmeticUtils.isPowerOfTwo(i);
            Assert.assertTrue(Integer.toString(i), actual == expected[i]);
        }
    }

    @Test
    public void testStirlingS2() {

        Assert.assertEquals(1, ArithmeticUtils.stirlingS2(0, 0));

        for (int n = 1; n < 30; ++n) {
            Assert.assertEquals(0, ArithmeticUtils.stirlingS2(n, 0));
            Assert.assertEquals(1, ArithmeticUtils.stirlingS2(n, 1));
            if (n > 2) {
                Assert.assertEquals((1l << (n - 1)) - 1l, ArithmeticUtils.stirlingS2(n, 2));
                Assert.assertEquals(ArithmeticUtils.binomialCoefficient(n, 2),
                                    ArithmeticUtils.stirlingS2(n, n - 1));
            }
            Assert.assertEquals(1, ArithmeticUtils.stirlingS2(n, n));
        }
        Assert.assertEquals(536870911l, ArithmeticUtils.stirlingS2(30, 2));
        Assert.assertEquals(576460752303423487l, ArithmeticUtils.stirlingS2(60, 2));

        Assert.assertEquals(   25, ArithmeticUtils.stirlingS2( 5, 3));
        Assert.assertEquals(   90, ArithmeticUtils.stirlingS2( 6, 3));
        Assert.assertEquals(   65, ArithmeticUtils.stirlingS2( 6, 4));
        Assert.assertEquals(  301, ArithmeticUtils.stirlingS2( 7, 3));
        Assert.assertEquals(  350, ArithmeticUtils.stirlingS2( 7, 4));
        Assert.assertEquals(  140, ArithmeticUtils.stirlingS2( 7, 5));
        Assert.assertEquals(  966, ArithmeticUtils.stirlingS2( 8, 3));
        Assert.assertEquals( 1701, ArithmeticUtils.stirlingS2( 8, 4));
        Assert.assertEquals( 1050, ArithmeticUtils.stirlingS2( 8, 5));
        Assert.assertEquals(  266, ArithmeticUtils.stirlingS2( 8, 6));
        Assert.assertEquals( 3025, ArithmeticUtils.stirlingS2( 9, 3));
        Assert.assertEquals( 7770, ArithmeticUtils.stirlingS2( 9, 4));
        Assert.assertEquals( 6951, ArithmeticUtils.stirlingS2( 9, 5));
        Assert.assertEquals( 2646, ArithmeticUtils.stirlingS2( 9, 6));
        Assert.assertEquals(  462, ArithmeticUtils.stirlingS2( 9, 7));
        Assert.assertEquals( 9330, ArithmeticUtils.stirlingS2(10, 3));
        Assert.assertEquals(34105, ArithmeticUtils.stirlingS2(10, 4));
        Assert.assertEquals(42525, ArithmeticUtils.stirlingS2(10, 5));
        Assert.assertEquals(22827, ArithmeticUtils.stirlingS2(10, 6));
        Assert.assertEquals( 5880, ArithmeticUtils.stirlingS2(10, 7));
        Assert.assertEquals(  750, ArithmeticUtils.stirlingS2(10, 8));

    }

    @Test(expected=NotPositiveException.class)
    public void testStirlingS2NegativeN() {
        ArithmeticUtils.stirlingS2(3, -1);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testStirlingS2LargeK() {
        ArithmeticUtils.stirlingS2(3, 4);
    }

    @Test(expected=MathArithmeticException.class)
    public void testStirlingS2Overflow() {
        ArithmeticUtils.stirlingS2(26, 9);
    }

    /**
     * Exact (caching) recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) throws MathArithmeticException {
        if (binomialCache.size() > n) {
            Long cachedResult = binomialCache.get(n).get(Integer.valueOf(k));
            if (cachedResult != null) {
                return cachedResult.longValue();
            }
        }
        long result = -1;
        if ((n == k) || (k == 0)) {
            result = 1;
        } else if ((k == 1) || (k == n - 1)) {
            result = n;
        } else {
            // Reduce stack depth for larger values of n
            if (k < n - 100) {
                binomialCoefficient(n - 100, k);
            }
            if (k > 100) {
                binomialCoefficient(n - 100, k - 100);
            }
            result = ArithmeticUtils.addAndCheck(binomialCoefficient(n - 1, k - 1),
                binomialCoefficient(n - 1, k));
        }
        if (result == -1) {
            throw new MathArithmeticException();
        }
        for (int i = binomialCache.size(); i < n + 1; i++) {
            binomialCache.add(new HashMap<Integer, Long>());
        }
        binomialCache.get(n).put(Integer.valueOf(k), Long.valueOf(result));
        return result;
    }

    /**
     * Exact direct multiplication implementation to test against
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private void testAddAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticUtils.addAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    private void testMulAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticUtils.mulAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }

    private void testSubAndCheckLongFailure(long a, long b) {
        try {
            ArithmeticUtils.subAndCheck(a, b);
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // success
        }
    }
}
