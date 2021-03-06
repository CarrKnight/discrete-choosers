package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;

import java.util.function.Function;

/**
 * the most common approach to regression is to boil down context, choices and results into a set of
 * numerical features and focus on those
 * @param <O> kind of option
 * @param <R> kind of experiment result
 * @param <C> kind of context for choice and prediction
 */
public abstract class FeatureBasedRegression<O,R,C> implements BeliefState<O, R, C> {


    /**
     * sets of objects to transform O,C pair into X numerical columns
     */
    private final FeatureExtractor<O,C>[] extractors;

    /**
     * function transforming the tuple O,R,C (usually focusing on R) into a number representing the Y value of the regression
     */
    protected final Function<Observation<O,R,C>,Double> yExtractor;


    public FeatureBasedRegression(
            FeatureExtractor<O, C>[] extractors,
            Function<Observation<O, R, C>, Double> yExtractor) {
        this.extractors = extractors;
        this.yExtractor = yExtractor;
    }

    /**
     * ask the regresion to predict the value (or whatever is being modelled) at these coordinates
     *
     * @param whereToPredict    the option we want to predict the value of
     * @param predictionContext other information about what we are predicting
     * @return the numerical value we predict
     */
    @Override
    public double predict(O whereToPredict, C predictionContext) {
        double[] x = convertOptionToFeatures((O) whereToPredict, (C) predictionContext);

        //never predict if any feature is NaN
        for (double feature : x) {
            if(!Double.isFinite(feature))
                return Double.NaN;
        }

        return predict(x);
    }

    public abstract double predict(double[] x);

    /**
     * add a new observation to the regression and learn from it!
     *
     * @param observation
     */
    @Override
    public void observe(Observation<O, R, C> observation) {

        double[] x = convertOptionToFeatures(observation.getChoiceMade(), observation.getContext());
        Double y = yExtractor.apply(observation);


        //never bother if any feature is NaN
        if(!NumericalRegression.isValidInput(x, y))
            return;


        observe(y, x);
    }

    /**
     * convert options!
     * @param choiceMade
     * @param context
     * @return
     */
    protected double[] convertOptionToFeatures(O choiceMade, C context) {
        return FeatureExtractor.convertToFeatures(
                choiceMade,
                context,
                extractors
        );
    }



    public abstract void observe(Double y, double[] x);

}
