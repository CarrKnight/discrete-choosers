package io.github.carrknight.utils.averager;

import java.util.function.Consumer;

/**
 * a class that eats numbers and spits out some form of average.
 * It is useful as a smoother
 */
public interface Averager {


    /**
     *
     * @param previousAverage previous average
     * @param numberOfObservations not including the new one
     * @param newObservation new observation
     * @return new average
     */
    public double computeNewAverage(double previousAverage,
                                    int numberOfObservations,
                                    double newObservation);
}
