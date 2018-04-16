package io.github.carrknight.heatmaps;

import io.github.carrknight.Observation;

/**
 * any method that can be fed observations and make predictions based on them. You could see it like this:
 * any regression really is f(Option,Context)---> a numerical value;
 * For example if this was a linear regression Option and Context would form the X columns.
 *
 * @param <C> object describing the context in which observations are made
 */
public interface OnlineRegression<O,R,C> {


    /**
     * ask the regresion to predict the value (or whatever is being modelled) at these coordinates
     * @param whereToPredict the option we want to predict the value of
     * @param predictionContext other information about what we are predicting
     * @return the numerical value we predict
     */
    public double predict(O whereToPredict,
                          C predictionContext);


    /**
     * add a new observation to the regression and learn from it!
     * @param observation
     */
    public void observe(Observation<O,R,C> observation);


}
