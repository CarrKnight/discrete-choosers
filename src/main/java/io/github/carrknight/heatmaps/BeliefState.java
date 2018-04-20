package io.github.carrknight.heatmaps;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.LocalFilterSpace;
import io.github.carrknight.heatmaps.regression.OneDimensionalFilter;
import io.github.carrknight.utils.averager.ExponentialMovingAverageFilter;

import java.util.function.Supplier;

/**
 * any object that can be fed observations and make predictions based on them. You could see it like this:
 * any mapping from f(Option,Context)---> a numerical value (usually rewards);
 * This could be a online regression, just a map of state--->average reward seen or something different still
 *
 * @param <C> object describing the context in which observations are made
 */
public interface BeliefState<O,R,C> {


    /**
     * ask the beliefState to predict the value (or whatever is being modelled) at these coordinates
     * @param whereToPredict the option we want to predict the value of
     * @param predictionContext other information about what we are predicting
     * @return the numerical value we predict
     */
    public double predict(O whereToPredict,
                          C predictionContext);


    /**
     * add a new observation to the beliefState and learn from it!
     * @param observation
     */
    public void observe(Observation<O,R,C> observation);


}
