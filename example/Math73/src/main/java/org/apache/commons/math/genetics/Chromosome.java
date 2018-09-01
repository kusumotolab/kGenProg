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
package org.apache.commons.math.genetics;

/**
 * Individual in a population. Chromosomes are compared based on their fitness.
 *
 * The chromosomes are IMMUTABLE, and so their fitness is also immutable and
 * therefore it can be cached.
 *
 * @since 2.0
 * @version $Revision$ $Date$
 */
public abstract class Chromosome implements Comparable<Chromosome>,Fitness {

    /**
     * Cached value of the fitness of this chromosome.
     */
    private double fitness = Double.MIN_VALUE;

    /**
     * Access the fitness of this chromosome. The bigger the fitness, the better
     * the chromosome.
     *
     * Computation of fitness is usually very time-consuming task, therefore the
     * fitness is cached.
     *
     * @return the fitness.
     */
    public double getFitness() {
        if (this.fitness == Double.MIN_VALUE) {
            // no cache - compute the fitness
            this.fitness = fitness();
        }
        return this.fitness;
    }

    /**
     * Compares two chromosomes based on their fitness. The bigger the fitness,
     * the better the chromosome.
     *
     * @param another another chromosome to compare
     * @return
     * <ul>
     *     <li>-1 if <code>another</code> is better than <code>this</code></li>
     *     <li>1 if <code>another</code> is worse than <code>this</code></li>
     *     <li>0 if the two chromosomes have the same fitness</li>
     * </ul>
     */
    public int compareTo(Chromosome another) {
        return ((Double)this.getFitness()).compareTo(another.getFitness());
    }

    /**
     * Returns <code>true<code> iff <code>another</code> has the same
     * representation and therefore the same fitness. By default, it returns
     * false -- override it in your implementation if you need it.
     * @param another chromosome to compare
     * @return true if <code>another</code> is equivalent to this chromosome
     */
    protected boolean isSame(Chromosome another) {
        return false;
    }

    /**
     * Searches the <code>population</code> for another chromosome with the same
     * representation. If such chromosome is found, it is returned, if no such
     * chromosome exists, returns <code>null</code>.
     *
     * @param population
     *            Population to search
     * @return Chromosome with the same representation, or <code>null</code> if
     *         no such chromosome exists.
     */
    protected Chromosome findSameChromosome(Population population) {
        for (Chromosome anotherChr : population) {
            if (this.isSame(anotherChr))
                return anotherChr;
        }
        return null;
    }

    /**
     * Searches the population for a chromosome representing the same solution,
     * and if it finds one, updates the fitness to its value.
     *
     * @param population
     *            Population to search
     */
    public void searchForFitnessUpdate(Population population) {
        Chromosome sameChromosome = findSameChromosome(population);
        if (sameChromosome != null) {
            fitness = sameChromosome.getFitness();
        }
    }

}
