package io.github.carrknight.utils.averager;

public class IterativeAverager implements Averager {
    /**
     * @param previousAverage      previous average
     * @param numberOfObservations not including the new one
     * @param newObservation       new observation
     * @return new average
     */
    @Override
    public double computeNewAverage(double previousAverage, int numberOfObservations, double newObservation) {


        return previousAverage + (newObservation-previousAverage)/(double)(numberOfObservations+1);

    }
}
