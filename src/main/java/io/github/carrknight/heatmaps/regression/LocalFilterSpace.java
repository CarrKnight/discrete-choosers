package io.github.carrknight.heatmaps.regression;

import com.google.common.base.Preconditions;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.heatmaps.regression.distance.Similarity;
import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * basically we build a 1D filter for each possible option but given a similarity function we can update the prediction
 * for one object given the observed reward for another
 */
public class LocalFilterSpace<O,R,C> implements BeliefState<O,R,C>
{


    @NotNull
    private Map<O,OneDimensionalFilter> filters = new HashMap<>();


    private final RewardFunction<O,R,C> utility;


    @Nullable
    private Similarity<O> optionSimilarity;

    public LocalFilterSpace(
            O[] optionsAvailable,
            OneDimensionalFilter[] givenFilters,
            RewardFunction<O, R, C> utility,
            @Nullable
                    Similarity<O> optionSimilarity)
    {
        Preconditions.checkArgument(givenFilters.length==optionsAvailable.length);
        Preconditions.checkArgument(givenFilters.length>0);
        this.utility = utility;

        this.optionSimilarity = optionSimilarity;
        for(int i=0; i<givenFilters.length;i++)
        {
            filters.put(optionsAvailable[i],
                        givenFilters[i]);
        }

    }




    public LocalFilterSpace(
            O[] optionsAvailable,
            Supplier<? extends OneDimensionalFilter> filterMaker,
            RewardFunction<O, R, C> utility,
            @Nullable
                    Similarity<O> optionSimilarity)
    {
        this.utility = utility;
        this.optionSimilarity = optionSimilarity;
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

        //if you have no similarity function, I assume you don't want to generalize observations
        // between choices
        if(optionSimilarity ==null)
        {

            filters.get(observation.getChoiceMade()).observe(
                    reward,
                    1d
            );
        }
        else {
            //for all options available
            for (Map.Entry<O, OneDimensionalFilter> filterEntry : filters.entrySet()) {

                double weight = optionSimilarity.similarity(
                        filterEntry.getKey(),
                        observation.getChoiceMade());

                filterEntry.getValue().observe(
                        reward,
                        weight
                );

            }
        }


    }


    public void resetFilter(
            Supplier<? extends OneDimensionalFilter> generator
    ){
        LinkedList<O> keys = new LinkedList<>(filters.keySet());
        filters.clear();
        for(O key : keys)
            filters.put(key,generator.get());
    }


    /**
     * Getter for property 'optionSimilarity'.
     *
     * @return Value for property 'optionSimilarity'.
     */
    @Nullable
    public Similarity<O> getOptionSimilarity() {
        return optionSimilarity;
    }

    /**
     * Setter for property 'optionSimilarity'.
     *
     * @param optionSimilarity Value to set for property 'optionSimilarity'.
     */
    public void setOptionSimilarity(@Nullable Similarity<O> optionSimilarity) {
        this.optionSimilarity = optionSimilarity;
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
