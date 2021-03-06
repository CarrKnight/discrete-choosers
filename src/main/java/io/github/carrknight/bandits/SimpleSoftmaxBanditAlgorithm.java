package io.github.carrknight.bandits;

import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;

/**
 * nothing more than a facade/factory for the softmax bandit when it deals with indexed choices and observes readily its rewards
 */
public class SimpleSoftmaxBanditAlgorithm extends SoftmaxBanditAlgorithm<Integer, Double,Object>
{



    /**
     * creates a softmax bandit with temperature 1 that never decays
     *
     * @param randomSeed       random seed
     */
    public SimpleSoftmaxBanditAlgorithm(int numberOfOptions,
                                     long randomSeed) {
        super(
                new RewardFunction<Integer, Double, Object>() {
                    @Override
                    public double extractUtility(
                            @NotNull Integer optionTaken, @NotNull Double experimentResult,
                            @Nullable Object contextObject) {
                        return experimentResult;
                    }
                },
                SimpleEpsilonGreedyBandit.buildOptionsArray(numberOfOptions),
                0,
                new SplittableRandom(randomSeed),
                1,
                temperature -> temperature);
    }


    /**
     * creates a softmax bandit with temperature 1 that never decays but with a starting initial expected reward to
     * simulate initial optimism/pessimism
     *
     * @param randomSeed       random seed
     */
    public SimpleSoftmaxBanditAlgorithm(int numberOfOptions,
                                        long randomSeed,
                                        double initialExpectedReward) {
        super(
                new RewardFunction<Integer, Double, Object>() {
                    @Override
                    public double extractUtility(
                            @NotNull Integer optionTaken, @NotNull Double experimentResult,
                            @Nullable Object contextObject) {
                        return experimentResult;
                    }
                },                SimpleEpsilonGreedyBandit.buildOptionsArray(numberOfOptions),
                initialExpectedReward,
                new SplittableRandom(randomSeed),
                1,
                temperature -> temperature);
    }


    /**
     * reates a softmax bandit
     * @param numberOfOptions number of options
     * @param randomSeed random seed for the randomizer
     * @param initialExpectedReward initial expected reward to simulate optimism or pessimism
     * @param initialTemperature temperature>=1, the higher the more random it gets
     * @param temperatureDecay function called each time a decision is made in order to change temperature (usually to lower it!)
     */
    public SimpleSoftmaxBanditAlgorithm(int numberOfOptions,
                                        long randomSeed,
                                        double initialExpectedReward,
                                        double initialTemperature,
                                        double temperatureDecay) {
        super(
                new RewardFunction<Integer, Double, Object>() {
                    @Override
                    public double extractUtility(
                            @NotNull Integer optionTaken, @NotNull Double experimentResult,
                            @Nullable Object contextObject) {
                        return experimentResult;
                    }
                },
                SimpleEpsilonGreedyBandit.buildOptionsArray(numberOfOptions),
                initialExpectedReward,
                new SplittableRandom(randomSeed),
                initialTemperature,
                temperature -> Math.max(temperatureDecay*temperature,1));
    }




}
