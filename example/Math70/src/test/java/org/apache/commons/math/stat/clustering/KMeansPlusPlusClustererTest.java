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

package org.apache.commons.math.stat.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class KMeansPlusPlusClustererTest {

    @Test
    public void dimension2() {
        KMeansPlusPlusClusterer<EuclideanIntegerPoint> transformer =
            new KMeansPlusPlusClusterer<EuclideanIntegerPoint>(new Random(1746432956321l));
        EuclideanIntegerPoint[] points = new EuclideanIntegerPoint[] {

                // first expected cluster
                new EuclideanIntegerPoint(new int[] { -15,  3 }),
                new EuclideanIntegerPoint(new int[] { -15,  4 }),
                new EuclideanIntegerPoint(new int[] { -15,  5 }),
                new EuclideanIntegerPoint(new int[] { -14,  3 }),
                new EuclideanIntegerPoint(new int[] { -14,  5 }),
                new EuclideanIntegerPoint(new int[] { -13,  3 }),
                new EuclideanIntegerPoint(new int[] { -13,  4 }),
                new EuclideanIntegerPoint(new int[] { -13,  5 }),

                // second expected cluster
                new EuclideanIntegerPoint(new int[] { -1,  0 }),
                new EuclideanIntegerPoint(new int[] { -1, -1 }),
                new EuclideanIntegerPoint(new int[] {  0, -1 }),
                new EuclideanIntegerPoint(new int[] {  1, -1 }),
                new EuclideanIntegerPoint(new int[] {  1, -2 }),

                // third expected cluster
                new EuclideanIntegerPoint(new int[] { 13,  3 }),
                new EuclideanIntegerPoint(new int[] { 13,  4 }),
                new EuclideanIntegerPoint(new int[] { 14,  4 }),
                new EuclideanIntegerPoint(new int[] { 14,  7 }),
                new EuclideanIntegerPoint(new int[] { 16,  5 }),
                new EuclideanIntegerPoint(new int[] { 16,  6 }),
                new EuclideanIntegerPoint(new int[] { 17,  4 }),
                new EuclideanIntegerPoint(new int[] { 17,  7 })

        };
        List<Cluster<EuclideanIntegerPoint>> clusters =
            transformer.cluster(Arrays.asList(points), 3, 10);

        assertEquals(3, clusters.size());
        boolean cluster1Found = false;
        boolean cluster2Found = false;
        boolean cluster3Found = false;
        for (Cluster<EuclideanIntegerPoint> cluster : clusters) {
            int[] center = cluster.getCenter().getPoint();
            if (center[0] < 0) {
                cluster1Found = true;
                assertEquals(8, cluster.getPoints().size());
                assertEquals(-14, center[0]);
                assertEquals( 4, center[1]);
            } else if (center[1] < 0) {
                cluster2Found = true;
                assertEquals(5, cluster.getPoints().size());
                assertEquals( 0, center[0]);
                assertEquals(-1, center[1]);
            } else {
                cluster3Found = true;
                assertEquals(8, cluster.getPoints().size());
                assertEquals(15, center[0]);
                assertEquals(5, center[1]);
            }
        }
        assertTrue(cluster1Found);
        assertTrue(cluster2Found);
        assertTrue(cluster3Found);

    }

    /**
     * JIRA: MATH-305
     *
     * Two points, one cluster, one iteration
     */
    @Test
    public void testPerformClusterAnalysisDegenerate() {
        KMeansPlusPlusClusterer<EuclideanIntegerPoint> transformer = new KMeansPlusPlusClusterer<EuclideanIntegerPoint>(
                new Random(1746432956321l));
        EuclideanIntegerPoint[] points = new EuclideanIntegerPoint[] {
                new EuclideanIntegerPoint(new int[] { 1959, 325100 }),
                new EuclideanIntegerPoint(new int[] { 1960, 373200 }), };
        List<Cluster<EuclideanIntegerPoint>> clusters = transformer.cluster(Arrays.asList(points), 1, 1);
        assertEquals(1, clusters.size());
        assertEquals(2, (clusters.get(0).getPoints().size()));
        EuclideanIntegerPoint pt1 = new EuclideanIntegerPoint(new int[] { 1959, 325100 });
        EuclideanIntegerPoint pt2 = new EuclideanIntegerPoint(new int[] { 1960, 373200 });
        assertTrue(clusters.get(0).getPoints().contains(pt1));
        assertTrue(clusters.get(0).getPoints().contains(pt2));

    }

}
