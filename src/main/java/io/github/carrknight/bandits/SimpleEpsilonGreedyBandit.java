package io.github.carrknight.bandits;

import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *  e-greedy algorithm where the options are just indices (that is 1,2,...,n) and you observe the rewards directly (no need to transform)
 */
public class SimpleEpsilonGreedyBandit extends EpsilonGreedyBandit<Integer, Double> {
    /**
     * An array describing all the options available to the bandit algorithm
     *
     * @param randomSeed       random seed
     * @param epsilon exploration rate
     */
    public SimpleEpsilonGreedyBandit(int numberOfOptions,
                                     long randomSeed, double epsilon) {
        super(
                new RewardFunction<Integer, Double, Object>() {
                    @Override
                    public double extractUtility(
                            @NotNull Integer optionTaken, @NotNull Double experimentResult,
                            @Nullable Object contextObject) {
                        return experimentResult;
                    }
                },                buildOptionsArray(numberOfOptions),
                randomSeed,
                epsilon);
    }




    static public Integer[] buildOptionsArray(int numberOfOptions){
        Integer[] options = new Integer[numberOfOptions];
        for(int i=0; i<options.length; i++)
            options[i]=i;

        return options;
    }
}
