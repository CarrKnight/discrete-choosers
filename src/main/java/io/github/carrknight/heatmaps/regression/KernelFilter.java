package io.github.carrknight.heatmaps.regression;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * If we assume the weight we get is a Kernel between here and wherever the observation comes from, this is basically
 * an iterative moving average with some forgetting
 */
public class KernelFilter implements OneDimensionalFilter {

    private final double forgettingFactor;

    private double currentMean = 0;

    private double denominator = 0;


    public KernelFilter(double forgettingFactor) {
        this.forgettingFactor = forgettingFactor;
    }

    public KernelFilter(double forgettingFactor, double initialMean) {
        this.forgettingFactor = forgettingFactor;
        this.currentMean = initialMean;
    }

    /**
     * @return y or NaN if prediction is impossible
     */
    @Override
    public double predict() {
        return currentMean;
    }

    /**
     * learn from observation
     *
     * @param evidence the exogenous variables observed
     * @param weight   the kernel (the higher the more important the observation is)
     */
    @Override
    public void observe(double evidence, double weight) {

        Preconditions.checkArgument(Double.isFinite(evidence));
        Preconditions.checkArgument(weight>=0);
        Preconditions.checkArgument(Double.isFinite(weight));

        denominator = denominator * forgettingFactor + weight;
        Preconditions.checkArgument(Double.isFinite(denominator), denominator + " , " +
                forgettingFactor + " , " + weight + " , ");
        //update predictor
        if (denominator > 0)
            currentMean += (evidence - currentMean) * weight / denominator;
    }
}
