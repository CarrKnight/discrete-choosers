package io.github.carrknight.heatmaps.regression;

import com.google.common.base.Preconditions;

/**
 * the least squares filter as implemented in :
 * http://www.cs.tut.fi/~tabus/course/ASP/LectureNew10.pdf
 * Basically a recursive least squares.
 */
public class RecursiveLeastSquaresRegression extends   WeightedNumericalRegression
{



    /**
     * this is usually the P matrix, telling us our uncertainty
     */
    private final  double[][] uncertainty;

    /**
     * these are our betas so far
     */
    private final double[] beta;

    /**
     * a number from 0 to 1 telling us how quickly we forget past information
     */
    private double exponentialForgetting;


    public RecursiveLeastSquaresRegression(double[][] uncertainty,
                                           double[] beta,
                                           double exponentialForgetting) {
        Preconditions.checkArgument(beta.length==uncertainty.length);
        this.uncertainty = uncertainty;
        this.beta = beta;
        this.exponentialForgetting = exponentialForgetting;
    }

    public RecursiveLeastSquaresRegression(double initialBeta,
                                           double initialUncertainty,
                                           int dimension,
                                           double exponentialForgetting) {

        this.uncertainty = new double[dimension][dimension];
        this.beta = new double[dimension];
        for(int i=0; i<dimension; i++) {
            this.beta[i] = initialBeta;
            this.uncertainty[i][i] = initialUncertainty;
        }
        this.exponentialForgetting = exponentialForgetting;
    }

    /**
     * learn from observation
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
     * predict, if possible, at position x
     *
     * @param x the vector describing the covariates X
     * @return y or NaN if prediction is impossible
     */
    @Override
    public double predict(double[] x) {
        if(!NumericalRegression.isValidInput(x))
            return Double.NaN;

        //prediction error
        double prediction = 0;
        for(int i=0; i<x.length; i++)
            prediction += x[i] * beta[i];
        assert (Double.isFinite(prediction));
        return prediction;
    }

    /**
     * @param x covariates
     * @param y to predict
     * @param weight the weight of the regression (basically 1/sigma^2)
     */
    public void observe(double[] x, double y, double weight){

        if(!NumericalRegression.isValidInput(x,y))
            return;

        int dimension = beta.length;
        assert x.length == dimension;


        //going through the least squares filter as described here:
        //http://www.cs.tut.fi/~tabus/course/ASP/LectureNew10.pdf
        double pi[] = new double[dimension];
        for(int column=0; column<dimension; column++)
            for(int row=0; row<dimension; row++)
            {
                pi[column] += x[row] * uncertainty[row][column];
                assert(Double.isFinite(pi[column]));

            }
        //gamma is basically dispersion
        double gamma = exponentialForgetting / weight;
        assert(gamma != 0);

        for(int row=0; row<dimension; row++)
            gamma+= x[row] *  pi[row];

        //if the dispersion is not invertible, do not add the observation
        if(gamma == 0)
        {
            System.out.println("Failed to invert matrix, observation ignored");
            increaseUncertainty();
            return;
        }


        //kalman gain
        double[] kalman = new double[dimension];
        for(int row=0; row<dimension; row++) {
            assert(Double.isFinite( pi[row]));
            assert(Double.isFinite( gamma));

            kalman[row] = pi[row] / gamma;

            assert(Double.isFinite( kalman[row]));


        }

        //prediction error
        double prediction = 0;
        for(int i=0; i<x.length; i++)
            prediction += x[i] * beta[i];
        double predictionError = y - prediction;
        assert (Double.isFinite(predictionError));

        //update beta
        for(int i=0; i<dimension; i++) {
            beta[i] += predictionError * kalman[i];
            assert  Double.isFinite(beta[i]);
        }
        //get P'
        final double[][] prime = new double[dimension][dimension];
        for(int row=0; row<dimension; row++)
            for(int column=0; column<dimension; column++) {
                prime[row][column] = kalman[row] * pi[column];
                assert(Double.isFinite(prime[row][column])) : "pi " + pi[column] + " , kalman: " + kalman[column] ;

            }
        //update uncertainty
        for(int row=0; row<dimension; row++)
            for(int column=0; column<dimension; column++)
            {
                assert(Double.isFinite(prime[row][column]));

                uncertainty[row][column]-=prime[row][column];
                uncertainty[row][column]/=exponentialForgetting;
                assert(Double.isFinite(uncertainty[row][column]));

            }


    }


    /**
     * if sigma^2 is infinite the kalman will be 0 which means that the only thing actually changing is P increasing.
     * This method just applies that part
     */
    public void increaseUncertainty()
    {
        for(int row=0; row<beta.length; row++)
            for(int column=0; column<beta.length; column++)
            {
                uncertainty[row][column]/=exponentialForgetting;
            }


    }

    /**
     * Getter for property 'beta'.
     *
     * @return Value for property 'beta'.
     */
    public double[] getBeta() {
        return beta;
    }

    /**
     * Getter for property 'exponentialForgetting'.
     *
     * @return Value for property 'exponentialForgetting'.
     */
    public double getExponentialForgetting() {
        return exponentialForgetting;
    }

    /**
     * Setter for property 'exponentialForgetting'.
     *
     * @param exponentialForgetting Value to set for property 'exponentialForgetting'.
     */
    public void setExponentialForgetting(double exponentialForgetting) {
        this.exponentialForgetting = exponentialForgetting;
    }
}
