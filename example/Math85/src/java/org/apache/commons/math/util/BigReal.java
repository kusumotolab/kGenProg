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
package org.apache.commons.math.util;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;

/**
 * Arbitrary precision decimal number.
 * <p>
 * This class is a simple wrapper around the standard <code>BigDecimal</code>
 * in order to implement the {@link FieldElement} interface.
 * </p>
 * @since 2.0
 * @version $Revision$ $Date$
 */
public class BigReal implements FieldElement<BigReal>, Comparable<BigReal>, Serializable {
    // TODO: Add Serializable documentation
    // TODO: Check Serializable implementation

    /** Serializable version identifier. */
    private static final long serialVersionUID = 7887631840434052850L;

    /** A big real representing 0. */
    public static final BigReal ZERO = new BigReal(BigDecimal.ZERO);

    /** A big real representing 1. */
    public static final BigReal ONE = new BigReal(BigDecimal.ONE);

    /** Underlying BigDecimal. */
    private final BigDecimal d;

    /** Build an instance from a BigDecimal.
     * @param val value of the instance
     */
    public BigReal(BigDecimal val) {
        d =  val;
    }

    /** Build an instance from a BigInteger.
     * @param val value of the instance
     */
    public BigReal(BigInteger val) {
        d = new BigDecimal(val);
    }

    /** Build an instance from an unscaled BigInteger.
     * @param unscaledVal unscaled value
     * @param scale scale to use
     */
    public BigReal(BigInteger unscaledVal, int scale) {
        d = new BigDecimal(unscaledVal, scale);
    }

    /** Build an instance from an unscaled BigInteger.
     * @param unscaledVal unscaled value
     * @param scale scale to use
     * @param mc to used
     */
    public BigReal(BigInteger unscaledVal, int scale, MathContext mc) {
        d = new BigDecimal(unscaledVal, scale, mc);
    }

    /** Build an instance from a BigInteger.
     * @param val value of the instance
     * @param mc context to use
     */
    public BigReal(BigInteger val, MathContext mc) {
        d = new BigDecimal(val, mc);
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     */
    public BigReal(char[] in) {
        d = new BigDecimal(in);
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param offset offset of the first character to analyze
     * @param len length of the array slice to analyze
     */
    public BigReal(char[] in, int offset, int len) {
        d = new BigDecimal(in, offset, len);
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param offset offset of the first character to analyze
     * @param len length of the array slice to analyze
     * @param mc context to use
     */
    public BigReal(char[] in, int offset, int len, MathContext mc) {
        d = new BigDecimal(in, offset, len, mc);
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param mc context to use
     */
    public BigReal(char[] in, MathContext mc) {
        d = new BigDecimal(in, mc);
    }

    /** Build an instance from a double.
     * @param val value of the instance
     */
    public BigReal(double val) {
        d = new BigDecimal(val);
    }

    /** Build an instance from a double.
     * @param val value of the instance
     * @param mc context to use
     */
    public BigReal(double val, MathContext mc) {
        d = new BigDecimal(val, mc);
    }

    /** Build an instance from an int.
     * @param val value of the instance
     */
    public BigReal(int val) {
        d = new BigDecimal(val);
    }

    /** Build an instance from an int.
     * @param val value of the instance
     * @param mc context to use
     */
    public BigReal(int val, MathContext mc) {
        d = new BigDecimal(val, mc);
    }

    /** Build an instance from a long.
     * @param val value of the instance
     */
    public BigReal(long val) {
        d = new BigDecimal(val);
    }

    /** Build an instance from a long.
     * @param val value of the instance
     * @param mc context to use
     */
    public BigReal(long val, MathContext mc) {
        d = new BigDecimal(val, mc);
    }

    /** Build an instance from a String representation.
     * @param val character representation of the value
     */
    public BigReal(String val) {
        d = new BigDecimal(val);
    }

    /** Build an instance from a String representation.
     * @param val character representation of the value
     * @param mc context to use
     */
    public BigReal(String val, MathContext mc)  {
        d = new BigDecimal(val, mc);
    }

    /** {@inheritDoc} */
    public BigReal add(BigReal a) {
        return new BigReal(d.add(a.d));
    }

    /** {@inheritDoc} */
    public BigReal subtract(BigReal a) {
        return new BigReal(d.subtract(a.d));
    }

    /** {@inheritDoc} */
    public BigReal divide(BigReal a) throws ArithmeticException {
        return new BigReal(d.divide(a.d));
    }

    /** {@inheritDoc} */
    public BigReal multiply(BigReal a) {
        return new BigReal(d.multiply(a.d));
    }

    /** {@inheritDoc} */
    public int compareTo(BigReal a) {
        return d.compareTo(a.d);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        try {
            if (other == null) {
                return false;
            }
            return d.equals(((BigReal) other).d);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return d.hashCode();
    }

    /** {@inheritDoc} */
    public Field<BigReal> getField() {
        return BigRealField.getInstance();
    }

}
