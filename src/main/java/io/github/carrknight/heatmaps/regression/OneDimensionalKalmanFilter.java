package io.github.carrknight.heatmaps.regression;

import static io.github.carrknight.heatmaps.regression.GoodBadFilter.normalPDF;

/**
 * a small version of the Kalman filter that doesn't really fit a linear model but just keeps observing only
 * a one dimensional evidence and tracks a one-dimensional space
 */
public class OneDimensionalKalmanFilter implements OneDimensionalFilter {

    /**
     * the A of the model
     */
    private double transitionMultiplier;

    /**
     * the C of the model (that is x*c = evidence)
     */
    private double emissionMultiplier;

    /**
     * the P of the kalman filter
     */
    private double uncertainty;

    /**
     * hat x of the kalman filter
     */
    private double stateEstimate;

    /**
     * the sigma_m of the kalman filter (the gaussian shock each time step to add to uncertainty)
     */
    private double drift;


    public OneDimensionalKalmanFilter(
            double transitionMultiplier, double emissionMultiplier, double uncertainty, double stateEstimate,
            double drift) {
        this.transitionMultiplier = transitionMultiplier;
        this.emissionMultiplier = emissionMultiplier;
        this.uncertainty = uncertainty;
        this.stateEstimate = stateEstimate;
        this.drift = drift;
    }

    /**
     * when elapsing time we multiply the current estimate of the state by A and then we increase P by the Sigma_m
     */
    public void drift()
    {
        stateEstimate = stateEstimate * transitionMultiplier;
        uncertainty = uncertainty *(transitionMultiplier*transitionMultiplier)+drift;
    }

    /**
     * updates state estimate with new evidence
     * @param evidence the measurement
     * @param weight
     */
    public void observe(double evidence, double weight)
    {


        //weighs the importance of this new observation
        double kalmanGain =  uncertainty * emissionMultiplier /
                (uncertainty * emissionMultiplier * emissionMultiplier +1d/weight);
        //update estimate in proportion to how far off the mark the prediction is (weighted by the kalman gain)
        stateEstimate = stateEstimate + kalmanGain *(evidence - emissionMultiplier*stateEstimate);
        //reduces uncertainty depending on the quality of the observation
        uncertainty = uncertainty - uncertainty * kalmanGain  * emissionMultiplier;
    }

    /**
     * @return y or NaN if prediction is impossible
     */
    @Override
    public double predict() {
        return stateEstimate;
    }

    /**
     * Getter for property 'uncertainty'.
     *
     * @return Value for property 'uncertainty'.
     */
    public double getUncertainty() {
        return uncertainty;
    }

    /**
     * Getter for property 'stateEstimate'.
     *
     * @return Value for property 'stateEstimate'.
     */
    public double getStateEstimate() {
        return stateEstimate;
    }

    public double getStandardDeviation(){
        return Math.sqrt(uncertainty);
    }

    public double getProbabilityStateIsThis(double guess)
    {
        return normalPDF(stateEstimate,getStandardDeviation()).apply(guess);
    }

    /**
     * Getter for property 'transitionMultiplier'.
     *
     * @return Value for property 'transitionMultiplier'.
     */
    public double getTransitionMultiplier() {
        return transitionMultiplier;
    }

    /**
     * Getter for property 'emissionMultiplier'.
     *
     * @return Value for property 'emissionMultiplier'.
     */
    public double getEmissionMultiplier() {
        return emissionMultiplier;
    }

    /**
     * Setter for property 'emissionMultiplier'.
     *
     * @param emissionMultiplier Value to set for property 'emissionMultiplier'.
     */
    public void setEmissionMultiplier(double emissionMultiplier) {
        this.emissionMultiplier = emissionMultiplier;
    }

    /**
     * Setter for property 'uncertainty'.
     *
     * @param uncertainty Value to set for property 'uncertainty'.
     */
    public void setUncertainty(double uncertainty) {
        this.uncertainty = uncertainty;
    }

    /**
     * Setter for property 'stateEstimate'.
     *
     * @param stateEstimate Value to set for property 'stateEstimate'.
     */
    public void setStateEstimate(double stateEstimate) {
        this.stateEstimate = stateEstimate;
    }

    /**
     * Getter for property 'drift'.
     *
     * @return Value for property 'drift'.
     */
    public double getDrift() {
        return drift;
    }

    public void setDrift(double drift) {
        this.drift = drift;
    }

}
