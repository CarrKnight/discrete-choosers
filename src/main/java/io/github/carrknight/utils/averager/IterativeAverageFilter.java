package io.github.carrknight.utils.averager;

import io.github.carrknight.heatmaps.regression.OneDimensionalFilter;


/**
 * very basic way to keep an iterative average (with weight). Good example is here:
 * https://stackoverflow.com/a/9916195/975904
 */
public class IterativeAverageFilter implements OneDimensionalFilter {

    /**
     * keep tracks of SUM(w*y) we have seen
     */
    private double numerator = 0;

     private double totalWeight = 0;

    /**
     * when we don't have any information, we return this
     */
    private final double initialGuess;


    public IterativeAverageFilter() {
        this(Double.NaN);
    }

    public IterativeAverageFilter(double initialGuess) {
        this.initialGuess = initialGuess;
    }

    /**
     * learn from observation
     *
     * @param evidence the exogenous variables observed
     * @param weight   the strength of the observation we have seen (1/sigma^2 if we know the uncertainty)
     */
    @Override
    public void observe(double evidence, double weight) {

        numerator+= evidence*weight;
        totalWeight+=weight;

    }

    /**
     * @return y or NaN if prediction is impossible
     */
    @Override
    public double predict() {

        return totalWeight == 0 ? initialGuess : numerator/totalWeight;
    }
}
