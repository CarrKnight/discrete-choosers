package io.github.carrknight.bandits;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.utils.DiscreteChoosersUtilities;
import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EpsilonGreedyBandit<O,R,C> extends AbstractBanditAlgorithm<O, R,C> {


    /**
     * exploratory probability
     */
    private double epsilon;

    /**
     * An array describing all the options available to the bandit algorithm
     *  @param rewardExtractor  transformer from R to double
     * @param optionsAvailable what kind of options are available
     * @param randomSeed       random seed
     * @param epsilon
     */
    public EpsilonGreedyBandit(
            @NotNull RewardFunction<O,R,C> rewardExtractor,
            @NotNull O[] optionsAvailable, long randomSeed, double epsilon) {
        super(rewardExtractor, optionsAvailable, randomSeed);
        Preconditions.checkArgument(epsilon>=0, "espilon cannot be lower than 0");
        Preconditions.checkArgument(epsilon<=1, "epsilon cannot be higher than 1");
        this.epsilon = epsilon;
    }




    /**
     * with probability epsilon, choose an option at random;
     * otherwise exploit greedily (split at random if multiple arms have equal expected rewards)
     *
     * @param state current memory of the bandit algorithm
     * @param optionsAvailable options available
     * @param lastObservation the last observation made
     * @param lastChoice the last choice made
     * @return new choice
     */
    @NotNull
    @Override
    protected O choose(
            BeliefState<O, R, C> state, @NotNull BiMap<O, Integer> optionsAvailable,
            @Nullable Observation<O, R, C> lastObservation, O lastChoice) {

        int numberOfOptions=optionsAvailable.size();

        //explore:
        if(getRandomizer().nextDouble() < epsilon)
        {
            int nextChoice = getRandomizer().nextInt(numberOfOptions);
            assert optionsAvailable.inverse().containsKey(nextChoice);
            return optionsAvailable.inverse().get(nextChoice);
        }
        else
        {

            O bestOption = DiscreteChoosersUtilities.getBestOption(
                    optionsAvailable.keySet(),
                    o -> state.predict(o,null),
                    getRandomizer(),
                    Double.NEGATIVE_INFINITY
            );
            assert bestOption != null;
            return
                    bestOption;

        }


    }

    /**
     * Getter for property 'epsilon'.
     *
     * @return Value for property 'epsilon'.
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * Setter for property 'epsilon'.
     *
     * @param epsilon Value to set for property 'epsilon'.
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
