package io.github.carrknight.utils.averager;

import com.google.common.base.Preconditions;

/**
 * returns (1-alpha)*old + alpha*new; if there are no previous observations, returns the newObservation as average
 */
public class ExponentialMovingAverager implements Averager
{


    private final double alpha;


    public ExponentialMovingAverager(double alpha) {
        Preconditions.checkArgument(alpha>=0,"alpha must be >=0");
        Preconditions.checkArgument(alpha<=1,"alpha must be <=1");
        this.alpha = alpha;
    }


    /**
     * @param previousAverage      previous average
     * @param numberOfObservations not including the new one
     * @param newObservation       new observation
     * @return new average
     */
    @Override
    public double computeNewAverage(double previousAverage, int numberOfObservations, double newObservation) {
        if(numberOfObservations>0)
            return previousAverage*(1d-alpha)+alpha*newObservation;
        else
            return newObservation;
    }
}
