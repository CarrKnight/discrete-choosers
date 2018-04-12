package io.github.carrknight;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a pairing optionTaken ---> rewardAchieved
 * @param <O> the class of the options available (say, FishingSpot if we are modelling a fisher deciding where to go next);
 * @param <R> the class describing the reward object (say, FishCaught if we are modelling the fisher judging the spot they have just been to)
 * @param <C> the class describing the context in which the decision or reward was observed (say, Weather for fisher)

 */
public class Observation<O,R,C> {


    @NotNull
    private final O choiceMade;

    @NotNull
    private final R rewardObserved;

    @Nullable
    private final C context;


    public Observation(@NotNull O choiceMade, @NotNull R rewardObserved) {
        this.choiceMade = choiceMade;
        this.rewardObserved = rewardObserved;
        this.context=null;
    }

    public Observation(@NotNull O choiceMade, @NotNull R rewardObserved, @Nullable C context) {
        this.choiceMade = choiceMade;
        this.rewardObserved = rewardObserved;
        this.context = context;
    }


    /**
     * Getter for property 'choiceMade'.
     *
     * @return Value for property 'choiceMade'.
     */
    @NotNull
    public O getChoiceMade() {
        return choiceMade;
    }

    /**
     * Getter for property 'rewardObserved'.
     *
     * @return Value for property 'rewardObserved'.
     */
    @NotNull
    public R getRewardObserved() {
        return rewardObserved;
    }

    /**
     * Getter for property 'context'.
     *
     * @return Value for property 'context'.
     */
    @Nullable
    public C getContext() {
        return context;
    }
}
