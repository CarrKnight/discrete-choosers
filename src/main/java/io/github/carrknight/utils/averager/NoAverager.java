package io.github.carrknight.utils.averager;

/**
 * makes no average, always returns the new observation
 */
public class NoAverager implements Averager {

    /**
     * @param previousAverage      previous average
     * @param numberOfObservations not including the new one
     * @param newObservation       new observation
     * @return new average
     */
    @Override
    public double computeNewAverage(double previousAverage, int numberOfObservations, double newObservation) {
        return newObservation;
    }
}
