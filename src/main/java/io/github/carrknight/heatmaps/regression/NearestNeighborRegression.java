package io.github.carrknight.heatmaps.regression;

import com.google.common.base.Preconditions;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.FeatureDistance;

import java.util.function.Function;

/**
 * adaptor between feature based regression and the standard numerical nearest neighbour regression
 * @param <O> the type of options available
 * @param <R> class describing experiment results
 * @param <C> class describing experiment context
 */
public class NearestNeighborRegression<O,R,C> extends FeatureBasedRegression<O, R, C> {

    /**
     * KdTree doing all the work
     */
    private final NearestNeighborNumericalRegression delegate;


    public NearestNeighborRegression(
            FeatureExtractor<O, C>[] extractors,
            Function<Observation<O, R, C>, Double> yExtractor,
            double[] bandwidths,
            FeatureDistance transformer, int maxNeighbours) {
        super(extractors, yExtractor);
        Preconditions.checkArgument(bandwidths.length==extractors.length,
                                    "The number of bandwidths should match number of extractors!");
        delegate = new NearestNeighborNumericalRegression(
                bandwidths,
                transformer,
                maxNeighbours
        );
    }

    /**
     * gets the number of maxNeighbours required and return their average value
     * @param x the features
     * @return average value of nearest neighbours
     */
    @Override
    public double predict(double[] x) {
        return delegate.predict(x);
    }

    /**
     * Add observation to KD tree
     * @param y the endogenous variable observed
     * @param x the exogenous variables observed
     */
    @Override
    public void observe(Double y, double[] x) {
        delegate.observe(x, y);
    }

    /**
     * Returns a defensive copy of the bandwidth array
     *
     * @return an array containing all the parameters of the model
     */
    public double[] getBandwidths() {
        return delegate.getBandwidths();
    }

    /**
     * set new bandwidths
     *
     * @param parameterArray the new parameters for this regresssion
     */
    public void setBandwidths(double[] parameterArray) {
        delegate.setBandwidths(parameterArray);
    }
}
