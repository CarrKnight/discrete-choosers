package io.github.carrknight.bandits;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 *  e-greedy algorithm where the options are just indices (that is 1,2,...,n) and you observe the rewards directly (no need to transform)
 */
public class SimpleEpsilonGreedyBandit extends EpsilonGreedyBandit<Integer, Double> {
    /**
     * An array describing all the options available to the bandit algorithm
     *
     * @param rewardExtractor  transformer from R to double
     * @param optionsAvailable what kind of options are available
     * @param randomSeed       random seed
     * @param epsilon exploration rate
     */
    public SimpleEpsilonGreedyBandit(int numberOfOptions,
                                     long randomSeed, double epsilon) {
        super(
                aDouble -> aDouble,
                buildOptionsArray(numberOfOptions),
                randomSeed,
                epsilon);
    }




    private static Integer[] buildOptionsArray(int numberOfOptions){
        Integer[] options = new Integer[numberOfOptions];
        for(int i=0; i<options.length; i++)
            options[i]=i;

        return options;
    }
}
