package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.Similarity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Each option has a little recursive least square filter attached to it.
 * @param <O>
 * @param <R>
 * @param <C>
 */
public class GeographicallyWeightedRegression<O,R,C> extends FeatureBasedRegression<O, R, C> {


    public static final double SIGNIFICANCE_THRESHOLD = .001;
    /**
     * tell how close two observations are
     */
    private final Similarity<O,C> similarity;


    private final Map<O,RecursiveLeastSquaresRegression> filters =
            new HashMap<>();


    public GeographicallyWeightedRegression(
            FeatureExtractor<O, C>[] extractors,
            Function<Observation<O, R, C>, Double> yExtractor,
            Similarity<O,C> similarity,
            O[] optionsAvailable,
            double forgettingFactor
            ) {
        super(extractors, yExtractor);

        this.similarity = similarity;
        for (O options : optionsAvailable) {
            filters.put(
                    options,
                    new RecursiveLeastSquaresRegression(
                            0,
                            10000,
                            extractors.length,
                            forgettingFactor
                    )
            );
        }







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
        return  predict(super.convertOptionToFeatures(whereToPredict,predictionContext));
    }

    @Override
    public double predict(double[] x) {
        throw new RuntimeException("should not be called!");
    }

    @Override
    public void observe(Double y, double[] x) {
        throw new RuntimeException("should not be called!");

    }

    /**
     * add a new observation to each regression and learn from it!
     *
     * @param observation
     */
    @Override
    public void observe(Observation<O, R, C> observation) {
        double[] x = super.convertOptionToFeatures(observation.getChoiceMade(),
                                                         observation.getContext());
        Double y = super.yExtractor.apply(observation);

        for (Map.Entry<O, RecursiveLeastSquaresRegression> filter : filters.entrySet()) {

            double weight = similarity.similarity(observation.getChoiceMade(),
                                                  filter.getKey(),
                                                  observation.getContext());

            if(weight> SIGNIFICANCE_THRESHOLD)
                filter.getValue().observe(x,y,weight);

        }


    }
}
