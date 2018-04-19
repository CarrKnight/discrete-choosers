package io.github.carrknight.heatmaps.regression;

import java.util.function.Function;

/**
 * you have a prior on what a "good" reward distribution looks like and what a "bad" reward distribution looks like.
 * This filter tries to guess if what we keep filter is a good or a bad distribution.
 * Good and bad priors are normal distributions
 */
public class GoodBadFilter implements OneDimensionalFilter {

    /**
     * gives us the mean for the bad prior
     */
    private final double badAverage;

    /**
     * gives us the mean for the good prior
     */
    private final double goodAverage;


    private final double standardDeviation;

    /**
     * daily drift of probabilities towards the middle
     */
    private final double drift;


    private double probabilityBeingAGoodSpot;


    public GoodBadFilter(double badAverage, double goodAverage, double standardDeviation, double drift) {
        this.badAverage = badAverage;
        this.goodAverage = goodAverage;
        this.standardDeviation = standardDeviation;
        this.drift = drift;
        probabilityBeingAGoodSpot=.5;
    }

    /**
     * @return y or NaN if prediction is impossible
     */
    @Override
    public double predict() {
        return probabilityBeingAGoodSpot * goodAverage + (1-probabilityBeingAGoodSpot) * badAverage;

    }




    /**
     * learn from observation
     *
     * @param evidence the exogenous variables observed
     * @param weight   the strength of the observation we have seen (1/sigma^2 if we know the uncertainty)
     */
    @Override
    public void observe(double evidence, double weight) {

        if(!Double.isFinite(evidence) || !Double.isFinite(weight) )
            return;


        double goodLikelihood = normalPDF(
                goodAverage,standardDeviation*weight).apply(evidence);
        double goodPosterior =  probabilityBeingAGoodSpot *goodLikelihood;
        assert  Double.isFinite(goodPosterior);
        assert  goodPosterior >=0;


        double badPrior = 1d-probabilityBeingAGoodSpot;
        double badLikelihood = normalPDF(
                badAverage,standardDeviation*weight).apply(evidence);
        double badPosterior = badPrior*badLikelihood;
        assert  badPosterior >=0;
        assert  Double.isFinite(badPosterior);

        if(badPosterior + goodPosterior == 0) {
            //if the evidence is weird, the problem is probably with the standard deviation
            if (evidence > goodAverage)
                probabilityBeingAGoodSpot=1d;
            else if (evidence < badAverage)
                probabilityBeingAGoodSpot=0d;
            else
                probabilityBeingAGoodSpot=.5d; //if you are here that's some very poor averages/std you got
        }
        else
            probabilityBeingAGoodSpot = (goodPosterior/(badPosterior+goodPosterior));
    }




    public static Function<Double,Double> normalPDF(double mean, double standardDeviation)
    {
        return new Function<Double,Double>(){


            @Override
            public Double apply(Double x) {
                return Math.exp(-Math.pow(x - mean, 2) / (2 * standardDeviation * standardDeviation)) /
                        Math.sqrt(2 * standardDeviation * standardDeviation * Math.PI);

            }
        };
    }

    public void drift(){
        double good = probabilityBeingAGoodSpot;
        double bad = 1d-good;
        probabilityBeingAGoodSpot = (good + drift) / (good + drift + bad + drift);
    }


    /**
     * Getter for property 'probabilityBeingAGoodSpot'.
     *
     * @return Value for property 'probabilityBeingAGoodSpot'.
     */
    public double getProbabilityBeingAGoodSpot() {
        return probabilityBeingAGoodSpot;
    }
}
