package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.FeatureKernel;

import java.util.Queue;
import java.util.function.Function;

public class KernelRegression<O,R,C> extends FeatureBasedRegression<O, R, C> {


    private final KernelNumericalRegression regression;


    public KernelRegression(
            FeatureExtractor<O, C>[] extractors,
            Function<Observation<O, R, C>, Double> yExtractor,
            FeatureKernel[] kernels,
            int maximumNumberOfObservationsToKeep) {
        super(extractors, yExtractor);
        this.regression = new KernelNumericalRegression(
                kernels,
                maximumNumberOfObservationsToKeep
        );
    }


    @Override
    public void observe(Double y, double[] x) {
        regression.observe(x, y);
    }

    @Override
    public double predict(double[] x) {
        return regression.predict(x);
    }

    /**
     * Getter for property 'maximumNumberOfObservationsToKeep'.
     *
     * @return Value for property 'maximumNumberOfObservationsToKeep'.
     */
    public int getMaximumNumberOfObservationsToKeep() {
        return regression.getMaximumNumberOfObservationsToKeep();
    }

    /**
     * Setter for property 'maximumNumberOfObservationsToKeep'.
     *
     * @param maximumNumberOfObservationsToKeep Value to set for property 'maximumNumberOfObservationsToKeep'.
     */
    public void setMaximumNumberOfObservationsToKeep(int maximumNumberOfObservationsToKeep) {
        regression.setMaximumNumberOfObservationsToKeep(maximumNumberOfObservationsToKeep);
    }

    /**
     * Getter for property 'observations'.
     *
     * @return Value for property 'observations'.
     */
    public Queue<double[]> getObservations() {
        return regression.getObservations();
    }
}
