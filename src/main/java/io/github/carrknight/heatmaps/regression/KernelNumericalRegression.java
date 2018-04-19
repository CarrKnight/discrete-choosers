package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.FeatureDistance;
import io.github.carrknight.heatmaps.regression.distance.FeatureKernel;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;

public class KernelNumericalRegression implements NumericalRegression {


    /**
     * delete observations after you have more than this
     */
    private int maximumNumberOfObservationsToKeep;

    /**
     * past observations (notice that this double[] is [x_1,x_2,\dots,x_n,y] )
     */
    private final Queue<double[]> observations = new LinkedList<>();



    private final FeatureKernel[] kernels;


    public KernelNumericalRegression(
            FeatureKernel[] kernels,
            int maximumNumberOfObservationsToKeep) {
        this.kernels = kernels;
        this.maximumNumberOfObservationsToKeep = maximumNumberOfObservationsToKeep;
    }

    @Override
    public void observe(Double y, double[] x) {

        if(!NumericalRegression.isValidInput(x,y))
            return;

        double[] newObservation = new double[x.length+1];
        for(int i=0; i<x.length; i++)
            newObservation[i]=x[i];
        newObservation[x.length] = y;

        observations.add(newObservation);
        if(observations.size()>maximumNumberOfObservationsToKeep)
        {
            assert observations.size()==maximumNumberOfObservationsToKeep+1;
            observations.poll();
        }

    }


    @Override
    public double predict(double[] x) {

        if(!NumericalRegression.isValidInput(x))
            return Double.NaN;

        assert x.length == kernels.length;

        double kernelSum = 0;
        double numerator = 0;
        //basically a fancy weighted regression
        for(double[] observation : observations)
        {
            assert observation.length == x.length+1;
            double currentKernel = 1;
            for(int i=0; i<x.length; i++) {



                currentKernel *= kernels[i].similarity(
                        x[i],
                        observation[i]
                );
                //don't bother if it's a 0
                if((currentKernel )<.00001)
                    break;
            }

            if((currentKernel )>.00001) {
                kernelSum += currentKernel;
                //the last item of the memorized observation is actually the Y
                numerator += currentKernel * observation[x.length];
            }
        }

        if(kernelSum <.00001)
            return Double.NaN;

        return numerator/kernelSum;

    }

    /**
     * Getter for property 'maximumNumberOfObservationsToKeep'.
     *
     * @return Value for property 'maximumNumberOfObservationsToKeep'.
     */
    public int getMaximumNumberOfObservationsToKeep() {
        return maximumNumberOfObservationsToKeep;
    }

    /**
     * Setter for property 'maximumNumberOfObservationsToKeep'.
     *
     * @param maximumNumberOfObservationsToKeep Value to set for property 'maximumNumberOfObservationsToKeep'.
     */
    public void setMaximumNumberOfObservationsToKeep(int maximumNumberOfObservationsToKeep) {
        this.maximumNumberOfObservationsToKeep = maximumNumberOfObservationsToKeep;
    }

    /**
     * Getter for property 'observations'.
     *
     * @return Value for property 'observations'.
     */
    public Queue<double[]> getObservations() {
        return observations;
    }


}
