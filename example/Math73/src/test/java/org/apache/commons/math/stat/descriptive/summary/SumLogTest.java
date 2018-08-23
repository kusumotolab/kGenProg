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
package org.apache.commons.math.stat.descriptive.summary;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision$ $Date$
 */
public class SumLogTest extends StorelessUnivariateStatisticAbstractTest{

    protected SumOfLogs stat;

    /**
     * @param name
     */
    public SumLogTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new SumOfLogs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.sumLog;
    }

    public void testSpecialValues() {
        SumOfLogs sum = new SumOfLogs();
        // empty
        assertTrue(Double.isNaN(sum.getResult()));

        // finite data
        sum.increment(1d);
        assertFalse(Double.isNaN(sum.getResult()));

        // add negative infinity
        sum.increment(0d);
        assertEquals(Double.NEGATIVE_INFINITY, sum.getResult(), 0);

        // add positive infinity -- should make NaN
        sum.increment(Double.POSITIVE_INFINITY);
        assertTrue(Double.isNaN(sum.getResult()));

        // clear
        sum.clear();
        assertTrue(Double.isNaN(sum.getResult()));

        // positive infinity by itself
        sum.increment(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, sum.getResult(), 0);

        // negative value -- should make NaN
        sum.increment(-2d);
        assertTrue(Double.isNaN(sum.getResult()));
    }

}
