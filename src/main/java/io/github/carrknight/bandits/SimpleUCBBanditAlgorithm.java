package io.github.carrknight.bandits;

import io.github.carrknight.utils.UtilityFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;

/**
 * facade/factory for the UCB algorithm
 */
public class SimpleUCBBanditAlgorithm extends UCBBanditAlgorithm<Integer, Double> {


    /**
     *

     * @param randomSeed       random seed
     * @param sigma the upper bound we want to target (0 makes it simple greedy, 1 is the default)
     * @param minimumRewardExpected the minimum reward that can be observed (will be mapped to 0)
     * @param maximumRewardExpected the maximum reward that can be observed (will be mapped to 1)
     */
    public SimpleUCBBanditAlgorithm(
            int numberOfOptions,
            long randomSeed, double sigma, double minimumRewardExpected,
            double maximumRewardExpected) {
        super(
                (optionTaken, experimentResult, contextObject) -> experimentResult,
                SimpleEpsilonGreedyBandit.buildOptionsArray(numberOfOptions),
                0,
                new SplittableRandom(randomSeed),
                minimumRewardExpected,
                maximumRewardExpected,
                sigma);
    }

    /**
     *
     * @param minimumRewardExpected the minimum reward that can be observed (will be mapped to 0)
     * @param maximumRewardExpected the maximum reward that can be observed (will be mapped to 1)
     */
    public SimpleUCBBanditAlgorithm(
            int numberOfOptions,
            long randomSeed,  double minimumRewardExpected,
            double maximumRewardExpected) {

        this(numberOfOptions,
             randomSeed,
             1d,
             minimumRewardExpected,
             maximumRewardExpected);
    }

}
