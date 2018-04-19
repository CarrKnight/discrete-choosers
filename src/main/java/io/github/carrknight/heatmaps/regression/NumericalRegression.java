package io.github.carrknight.heatmaps.regression;

/**
 * Any recursive regression that takes numerical X and predicts numerical, scalar Y
 */
public interface NumericalRegression {

    /**
     * predict, if possible, at position x
     * @param x the vector describing the covariates X
     * @return y or NaN if prediction is impossible
     */
    double predict(double[] x);


    /**
     * learn from observation
     * @param y the endogenous variable observed
     * @param x the exogenous variables observed
     */
    void observe(Double y, double[] x);


    /**
     * checks a double[] array to see if all the numbers included are finite
     * @param x an array
     * @param additional additional doubles to check
     * @return false if at least one element is not a finite number
     */
    public static boolean isValidInput(double[] x, double... additional){
        for(double element: x)
            if(!Double.isFinite(element))
                return false;
        for(double element: additional)
            if(!Double.isFinite(element))
                return false;
        return true;

    }

}
