package io.github.carrknight.heatmaps.regression;

import com.google.common.base.Preconditions;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.OnlineRegression;
import io.github.carrknight.heatmaps.regression.distance.Distance;
import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * basically we build a 1D filter for each possible option but given a distance function we can update the prediction
 * for one object given the observed reward for another
 */
public class LocalFilterRegression<O,R,C> implements OnlineRegression<O,R,C>
{


    @NotNull
    private Map<O,OneDimensionalFilter> filters = new HashMap<>();


    private final RewardFunction<O,R,C> utility;


    @Nullable
    private final Distance<O> optionDistance;

    public LocalFilterRegression(
            O[] optionsAvailable,
            OneDimensionalFilter[] givenFilters,
            RewardFunction<O, R, C> utility,
            @Nullable
                    Distance<O> optionDistance)
    {
        Preconditions.checkArgument(givenFilters.length==optionsAvailable.length);
        Preconditions.checkArgument(givenFilters.length>0);
        this.utility = utility;

        this.optionDistance = optionDistance;
        for(int i=0; i<givenFilters.length;i++)
        {
            filters.put(optionsAvailable[i],
                        givenFilters[i]);
        }

    }


    public LocalFilterRegression(
            O[] optionsAvailable,
            Supplier<? extends OneDimensionalFilter> filterMaker,
            RewardFunction<O, R, C> utility,
            @Nullable
                    Distance<O> optionDistance)
    {
        this.utility = utility;
        this.optionDistance = optionDistance;
        for(O option : optionsAvailable)
            filters.put(
                    option,
                    filterMaker.get()
            );
    }


    /**
     * add a new observation to the regression and learn from it!
     *
     * @param observation
     */
    @Override
    public void observe(Observation<O, R, C> observation) {

        double reward = utility.extractUtility(observation.getChoiceMade(),
                                                 observation.getResultObserved(),
                                                 observation.getContext());

        //if you have no distance function, I assume you don't want to generalize observations
        // between choices
        if(optionDistance==null)
        {

            filters.get(observation.getChoiceMade()).observe(
                    reward,
                    1d
            );
        }
        else {
            //for all options available
            for (Map.Entry<O, OneDimensionalFilter> filterEntry : filters.entrySet()) {

                double weight = optionDistance.distance(
                        filterEntry.getKey(),
                        observation.getChoiceMade());

                filterEntry.getValue().observe(
                        reward,
                        weight
                );

            }
        }


    }

    /**
     * ask the regression to predict the value (or whatever is being modelled) at these coordinates
     *
     * @param whereToPredict    the option we want to predict the value of
     * @param predictionContext IGNORED
     * @return the numerical value we predict
     */
    @Override
    public double predict(O whereToPredict, C predictionContext) {
        return filters.get(whereToPredict).predict();
    }
}
