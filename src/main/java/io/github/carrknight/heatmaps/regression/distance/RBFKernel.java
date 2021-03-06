/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2017  CoHESyS Lab cohesys.lab@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package io.github.carrknight.heatmaps.regression.distance;

/**
 * Takes a regression similarity and makes it into RBF kernel. Notice that in reality we probably want 1/this for similarity
 * since Kernels are a measure of similarity
 * Created by carrknight on 8/14/16.
 */
public class RBFKernel implements FeatureKernel
{



    private double bandwidth;

    public RBFKernel(double bandwidth) {
        this.bandwidth = bandwidth;
    }


    @Override
    public double similarity(double firstObservation, double secondObservation) {
        double distance = firstObservation - secondObservation;
        return Math.exp(- distance*distance/(bandwidth));
    }


    /**
     * utility method to use when you already have a difference and you know you will use RBF
     * @param difference
     * @return
     */
    public double transform(double difference){
        return  Math.exp(- difference*difference/(bandwidth));
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }
}
