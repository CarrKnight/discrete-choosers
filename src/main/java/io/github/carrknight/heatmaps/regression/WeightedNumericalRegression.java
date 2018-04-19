package io.github.carrknight.heatmaps.regression;

public abstract class WeightedNumericalRegression implements NumericalRegression {


    /**
     * learn from observation assuming weight = 1
     *
     * @param x the exogenous variables observed
     * @param y the endogenous variable observed
     */
    @Override
    public void observe(double[] x, Double y) {
        //simply do not weigh your observation
        observe(x, y, 1d);
    }


    /**
     * Prediction weighted by strength of observation
     * @param x covariates
     * @param y to predict
     * @param weight the weight of the regression (basically 1/sigma^2)
     */
    public abstract  void observe(double[] x, double y, double weight);


}
