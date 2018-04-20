package io.github.carrknight.utils.averager;

import com.google.common.base.Preconditions;
import io.github.carrknight.heatmaps.regression.OneDimensionalFilter;

/**
 * this is the standard Exponential moving average filter. There really isn't a good way to do weights with it (the exponential part ought to do it)
 * but what I'll do is simply turn the usual EMA:
 * old*(1-alpha) + new (alpha)
 * into
 * old * (1-w*alpha) + new * (w*alpha)
 * kind of like an eligibility trace. Of course it only makes sense for 0<=weight<=1
 */
public class ExponentialMovingAverageFilter implements OneDimensionalFilter {




    private double average;


    private double alpha;

    public ExponentialMovingAverageFilter(double initialAverage, double alpha) {
        this.average = initialAverage;
        this.alpha = alpha;
    }

    public ExponentialMovingAverageFilter(double alpha) {
        this.average = Double.NaN;
        this.alpha = alpha;
    }

    /**
     * learn from observation
     *
     * @param evidence the exogenous variables observed
     * @param weight   the strength of the observation we have seen (1/sigma^2 if we know the uncertainty)
     */
    @Override
    public void observe(double evidence, double weight) {
        Preconditions.checkArgument(weight<=1);
        Preconditions.checkArgument(weight>=0);
        //first observation with non-initialized EMA: just copy
        if(Double.isNaN(average))
        {
            if(weight>0)
                average = evidence;
            return;
        }
        else
        {
            average =  (1-alpha* weight) * average + (alpha * weight) * evidence;

        }


    }

    /**
     * @return y or NaN if prediction is impossible
     */
    @Override
    public double predict() {
        return average;
    }
}
