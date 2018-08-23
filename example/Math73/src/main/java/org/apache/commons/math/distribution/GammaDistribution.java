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
package org.apache.commons.math.distribution;

/**
 * The Gamma Distribution.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/GammaDistribution.html">
 * Gamma Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision$ $Date$
 */
public interface GammaDistribution extends ContinuousDistribution, HasDensity<Double> {
    /**
     * Modify the shape parameter, alpha.
     * @param alpha the new shape parameter.
     */
    void setAlpha(double alpha);

    /**
     * Access the shape parameter, alpha
     * @return alpha.
     */
    double getAlpha();

    /**
     * Modify the scale parameter, beta.
     * @param beta the new scale parameter.
     */
    void setBeta(double beta);

    /**
     * Access the scale parameter, beta
     * @return beta.
     */
    double getBeta();

    /**
     * Return the probability density for a particular point.
     * @param x  The point at which the density should be computed.
     * @return  The pdf at point x.
     */
    double density(Double x);
}
