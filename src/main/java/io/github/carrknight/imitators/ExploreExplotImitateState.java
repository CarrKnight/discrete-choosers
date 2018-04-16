package io.github.carrknight.imitators;

import io.github.carrknight.Observation;
import io.github.carrknight.utils.UtilityFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * it may be best to consider the explore-exploit-imitate as a simple state machine.
 * Each state waits for information and returns the new favourite option
 */
public class ExploreExplotImitateState<O,R,C> {



    @NotNull
    private final O favoriteOption;


    @Nullable
    private final R favoriteResult;


    public ExploreExplotImitateState(O favoriteOption, R favoriteResult) {
        this.favoriteOption = favoriteOption;
        this.favoriteResult = favoriteResult;
    }

    /**
     * Getter for property 'favoriteOption'.
     *
     * @return Value for property 'favoriteOption'.
     */
    public O getFavoriteOption() {
        return favoriteOption;
    }

    /**
     * Getter for property 'favoriteResult'.
     *
     * @return Value for property 'favoriteResult'.
     */
    public R getFavoriteResult() {
        return favoriteResult;
    }

    /**
     * choose the new favorite in lieu of this new piece of information (uses new context to judge old favorite)
     * @param observation the observation made
     * @param utilityFunction the utility function
     * @return the new favorite
     */
    public ExploreExplotImitateState<O,R,C> resolve(
            @NotNull Observation<O,R,C> observation,
            @NotNull UtilityFunction<O,R,C> utilityFunction
    ){
        //if the previous option has no result (meaning that we must have just initialized, return the new observation)
        if(favoriteResult == null)
        {
            assert observation.getResultObserved() != null;
            return new ExploreExplotImitateState<>(observation.getChoiceMade(),
                                                   observation.getResultObserved());
        }

        //if we just played the same option (exploiting) then might just as well return now
        if(observation.getChoiceMade()==favoriteOption)
            return new ExploreExplotImitateState<>(favoriteOption,
                                                   observation.getResultObserved());

        //get new reward
        double newReward = utilityFunction.extractUtility(observation.getChoiceMade(),
                                                          observation.getResultObserved(),
                                                          observation.getContext());



        //get old reward
        double currentReward = utilityFunction.extractUtility(favoriteOption,
                                                              favoriteResult,
                                                              observation.getContext());

        //pick option with best reward
        if(newReward > currentReward)
            return new ExploreExplotImitateState<>(
                    observation.getChoiceMade(),
                    observation.getResultObserved()
            );
        else
            return new ExploreExplotImitateState<>(
                    favoriteOption,
                    favoriteResult
            );
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ExploreExplotImitateState{");
        sb.append("favoriteOption=").append(favoriteOption);
        sb.append(", favoriteResult=").append(favoriteResult);
        sb.append('}');
        return sb.toString();
    }
}
