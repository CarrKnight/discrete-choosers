package io.github.carrknight.heatmaps.regression;

/**
 * this is basically any object that is like a very basic regression that only ever observes Y (no X) and
 * wants to predict what Y will be next time
 */
public interface OneDimensionalFilter {


    /**
     * @return y or NaN if prediction is impossible
     */
    double predict();


    /**
     * learn from observation
     * @param evidence the exogenous variables observed
     * @param weight the strength of the observation we have seen (1/sigma^2 if we know the uncertainty)
     */
    void observe(double evidence, double weight);



}
