package io.github.carrknight.imitators;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.utils.RewardFunction;

/**
 * Has no belief except for the best observation so far
 * @param <O> choice type
 * @param <R> experiment result type
 * @param <C> context type
 */
public class MonoBelief<O,R,C> implements BeliefState<O,R,C>
{

    private ExploreExplotImitateState<O,R,C> delegate;

    private final RewardFunction<O,R,C> extractor;


    public MonoBelief(RewardFunction<O, R, C> extractor) {
        this.extractor = extractor;
    }

    /**
     * ask the beliefState to predict the value (or whatever is being modelled) at these coordinates
     *
     * @param whereToPredict    the option we want to predict the value of
     * @param predictionContext other information about what we are predicting
     * @return the numerical value we predict
     */
    @Override
    public double predict(O whereToPredict, C predictionContext) {

        //if no memory, return 0
        if(delegate==null || delegate.getFavoriteResult() == null)
            return 0;
        else
            //do not try to predict away from the only memory you keep
            if(delegate.getFavoriteOption() == whereToPredict)
                return extractor.extractUtility(delegate.getFavoriteOption(),
                                                delegate.getFavoriteResult(),
                                                predictionContext);
            else
                return Double.NaN;

    }

    /**
     * add a new observation to the beliefState and learn from it!
     *
     * @param observation
     */
    @Override
    public void observe(Observation<O, R, C> observation) {

        if(delegate== null)
            delegate = new ExploreExplotImitateState<>(observation.getChoiceMade(),
                                                       observation.getResultObserved());

        delegate = delegate.resolve(observation,
                         extractor);
    }
}
