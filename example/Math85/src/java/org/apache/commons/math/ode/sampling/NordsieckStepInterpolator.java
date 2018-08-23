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

package org.apache.commons.math.ode.sampling;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.ode.DerivativeException;

/**
 * This class implements an interpolator for integrators using Nordsieck representation.
 *
 * <p>This interpolator computes dense output around the current point.
 * The interpolation equation is based on Taylor series formulas.
 *
 * @see org.apache.commons.math.ode.nonstiff.AdamsBashforthIntegrator
 * @see org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */

public class NordsieckStepInterpolator extends AbstractStepInterpolator {

    /** Serializable version identifier */
    private static final long serialVersionUID = -7179861704951334960L;

    /** Step size used in the first scaled derivative and Nordsieck vector. */
    private double scalingH;

    /** Reference time for all arrays.
     * <p>Sometimes, the reference time is the same as previousTime,
     * sometimes it is the same as currentTime, so we use a separate
     * field to avoid any confusion.
     * </p>
     */
    private double referenceTime;

    /** First scaled derivative. */
    private double[] scaled;

    /** Nordsieck vector. */
    private Array2DRowRealMatrix nordsieck;

    /** State variation. */
    protected double[] stateVariation;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link AbstractStepInterpolator#reinitialize} method should be called
     * before using the instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases.
     */
    public NordsieckStepInterpolator() {
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    public NordsieckStepInterpolator(final NordsieckStepInterpolator interpolator) {
        super(interpolator);
        scalingH      = interpolator.scalingH;
        referenceTime = interpolator.referenceTime;
        if (interpolator.scaled != null) {
            scaled = interpolator.scaled.clone();
        }
        if (interpolator.nordsieck != null) {
            nordsieck = new Array2DRowRealMatrix(interpolator.nordsieck.getDataRef(), true);
        }
        if (interpolator.stateVariation != null) {
            stateVariation = interpolator.stateVariation.clone();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected StepInterpolator doCopy() {
        return new NordsieckStepInterpolator(this);
    }

    /** Reinitialize the instance.
     * <p>Beware that all arrays <em>must</em> be references to integrator
     * arrays, in order to ensure proper update without copy.</p>
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param forward integration direction indicator
     */
    @Override
    public void reinitialize(final double[] y, final boolean forward) {
        super.reinitialize(y, forward);
        stateVariation = new double[y.length];
    }

    /** Reinitialize the instance.
     * <p>Beware that all arrays <em>must</em> be references to integrator
     * arrays, in order to ensure proper update without copy.</p>
     * @param referenceTime time at which all arrays are defined
     * @param scalingH step size used in the scaled and nordsieck arrays
     * @param scaled reference to the integrator array holding the first
     * scaled derivative
     * @param nordsieck reference to the integrator matrix holding the
     * nordsieck vector
     */
    public void reinitialize(final double referenceTime, final double scalingH,
                             final double[] scaled, final Array2DRowRealMatrix nordsieck) {
        this.referenceTime = referenceTime;
        this.scalingH      = scalingH;
        this.scaled        = scaled;
        this.nordsieck     = nordsieck;

        // make sure the state and derivatives will depend on the new arrays
        setInterpolatedTime(getInterpolatedTime());

    }

    /** Rescale the instance.
     * <p>Since the scaled and Nordiseck arrays are shared with the caller,
     * this method has the side effect of rescaling this arrays in the caller too.</p>
     * @param scalingH new step size to use in the scaled and nordsieck arrays
     */
    public void rescale(final double scalingH) {

        final double ratio = scalingH / this.scalingH;
        for (int i = 0; i < scaled.length; ++i) {
            scaled[i] *= ratio;
        }

        final double[][] nData = nordsieck.getDataRef();
        double power = ratio;
        for (int i = 0; i < nData.length; ++i) {
            power *= ratio;
            final double[] nDataI = nData[i];
            for (int j = 0; j < nDataI.length; ++j) {
                nDataI[j] *= power;
            }
        }

        this.scalingH = scalingH;

    }

    /**
     * Get the state vector variation from current to interpolated state.
     * <p>This method is aimed at computing y(t<sub>interpolation</sub>)
     * -y(t<sub>current</sub>) accurately by avoiding the cancellation errors
     * that would occur if the subtraction were performed explicitly.</p>
     * <p>The returned vector is a reference to a reused array, so
     * it should not be modified and it should be copied if it needs
     * to be preserved across several calls.</p>
     * @return state vector at time {@link #getInterpolatedTime}
     * @see #getInterpolatedDerivatives()
     * @throws DerivativeException if this call induces an automatic
     * step finalization that throws one
     */
    public double[] getInterpolatedStateVariation()
        throws DerivativeException {
        // compute and ignore interpolated state
        // to make sure state variation is computed as a side effect
        getInterpolatedState();
        return stateVariation;
    }

    /** {@inheritDoc} */
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {

        final double x = interpolatedTime - referenceTime;
        final double normalizedAbscissa = x / scalingH;

        Arrays.fill(stateVariation, 0.0);
        Arrays.fill(interpolatedDerivatives, 0.0);

        // apply Taylor formula from high order to low order,
        // for the sake of numerical accuracy
        final double[][] nData = nordsieck.getDataRef();
        for (int i = nData.length - 1; i >= 0; --i) {
            final int order = i + 2;
            final double[] nDataI = nData[i];
            final double power = Math.pow(normalizedAbscissa, order);
            for (int j = 0; j < nDataI.length; ++j) {
                final double d = nDataI[j] * power;
                stateVariation[j]          += d;
                interpolatedDerivatives[j] += order * d;
            }
        }

        for (int j = 0; j < currentState.length; ++j) {
            stateVariation[j] += scaled[j] * normalizedAbscissa;
            interpolatedState[j] = currentState[j] + stateVariation[j];
            interpolatedDerivatives[j] =
                (interpolatedDerivatives[j] + scaled[j] * normalizedAbscissa) / x;
        }

    }

    /** {@inheritDoc} */
    @Override
    public void writeExternal(final ObjectOutput out)
        throws IOException {

        // save the state of the base class
        writeBaseExternal(out);

        // save the local attributes
        out.writeDouble(scalingH);
        out.writeDouble(referenceTime);

        final int n = (currentState == null) ? -1 : currentState.length;
        if (scaled == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            for (int j = 0; j < n; ++j) {
                out.writeDouble(scaled[j]);
            }
        }

        if (nordsieck == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(nordsieck);
        }

        // we don't save state variation, it will be recomputed

    }

    /** {@inheritDoc} */
    @Override
    public void readExternal(final ObjectInput in)
        throws IOException, ClassNotFoundException {

        // read the base class 
        final double t = readBaseExternal(in);

        // read the local attributes
        scalingH      = in.readDouble();
        referenceTime = in.readDouble();

        final int n = (currentState == null) ? -1 : currentState.length;
        final boolean hasScaled = in.readBoolean();
        if (hasScaled) {
            scaled = new double[n];
            for (int j = 0; j < n; ++j) {
                scaled[j] = in.readDouble();
            }
        } else {
            scaled = null;
        }

        final boolean hasNordsieck = in.readBoolean();
        if (hasNordsieck) {
            nordsieck = (Array2DRowRealMatrix) in.readObject();
        } else {
            nordsieck = null;
        }

        if (hasScaled && hasNordsieck) {
            // we can now set the interpolated time and state
            stateVariation = new double[n];
            setInterpolatedTime(t);
        } else {
            stateVariation = null;
        }

    }

}
