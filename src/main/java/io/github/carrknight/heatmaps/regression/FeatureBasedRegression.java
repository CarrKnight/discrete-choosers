package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.OnlineRegression;

import java.util.function.Function;

/**
 * the most common approach to regression is to boil down context, choices and results into a set of
 * numerical features and focus on those
 * @param <O> kind of option
 * @param <R> kind of experiment result
 * @param <C> kind of context for choice and prediction
 */
public abstract class FeatureBasedRegression<O,R,C> implements OnlineRegression<O, R, C> {


    /**
     * sets of objects to transform O,C pair into X numerical columns
     */
    private final FeatureExtractor<O,C>[] extractors;

    /**
     * function transforming the tuple O,R,C (usually focusing on R) into a number representing the Y value of the regression
     */
    private final Function<Observation<O,R,C>,Double> yExtractor;


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
        double[] x = FeatureExtractor.convertToFeatures(
                whereToPredict,
                predictionContext,
                extractors
        );

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

        double[] x = FeatureExtractor.convertToFeatures(
                observation.getChoiceMade(),
                observation.getContext(),
                extractors
        );
        Double y = yExtractor.apply(observation);


        //never bother if any feature is NaN
        for (double feature : x) {
            if(!Double.isFinite(feature))
                return;
        }
        //don't bother if Y is NaN either
        if(!Double.isFinite(y))
            return;

        observe(y, x);
    }


    public abstract void observe(Double y, double[] x);

}
