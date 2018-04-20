package io.github.carrknight.bandits;

import io.github.carrknight.heatmaps.regression.LocalFilterSpace;
import io.github.carrknight.heatmaps.regression.OneDimensionalFilter;
import io.github.carrknight.heatmaps.regression.distance.Similarity;
import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 *  e-greedy algorithm where the options are just indices (that is 1,2,...,n) and you observe the rewards directly (no need to transform)
 */
public class SimpleEpsilonGreedyBandit extends EpsilonGreedyBandit<Integer, Double,Object> {
    /**
     * generates the standard epsilon greedy algorithm with iterative averages
     *
     * @param randomSeed       random seed
     * @param epsilon exploration rate
     */
    public SimpleEpsilonGreedyBandit(int numberOfOptions,
                                     long randomSeed, double epsilon) {
        super(
                (optionTaken, experimentResult, contextObject) ->
                        experimentResult,
                buildOptionsArray(numberOfOptions),
                randomSeed,
                epsilon);
    }

    /**
     * epsilon greedy constructor with flexible filter selection
     * @param numberOfOptions
     * @param randomSeed
     * @param epsilon
     * @param memoryMaker
     * @return
     */
    public static SimpleEpsilonGreedyBandit build(int numberOfOptions,
                                                  long randomSeed, double epsilon,
                                                  Supplier<? extends OneDimensionalFilter> memoryMaker
                                                  )
    {
        SimpleEpsilonGreedyBandit algo = new SimpleEpsilonGreedyBandit(numberOfOptions,
                                                                                            randomSeed,
                                                                                            epsilon);

        algo.setBanditState(new LocalFilterSpace<Integer, Double,Object>(
                buildOptionsArray(numberOfOptions),
                memoryMaker,
                (optionTaken, experimentResult, contextObject) ->
                        experimentResult,
                null

        ));

        return algo;

    }


    /**
     * epsilon greedy constructor with flexible filter  and similarity index for multiple updates
     * @param numberOfOptions
     * @param randomSeed
     * @param epsilon
     * @param memoryMaker
     * @return
     */
    public static EpsilonGreedyBandit build(int numberOfOptions,
                                            long randomSeed, double epsilon,
                                            Supplier<? extends OneDimensionalFilter> memoryMaker,
                                            Similarity<Integer> similarity
    )
    {
        SimpleEpsilonGreedyBandit algo = new SimpleEpsilonGreedyBandit(numberOfOptions,
                                                                       randomSeed,
                                                                       epsilon);

        algo.setBanditState(new LocalFilterSpace<Integer, Double,Object>(
                buildOptionsArray(numberOfOptions),
                memoryMaker,
                (optionTaken, experimentResult, contextObject) ->
                        experimentResult,
                similarity

        ));

        return algo;

    }


    public SimpleEpsilonGreedyBandit(int numberOfOptions,
                                     long randomSeed, double epsilon,
                                     double alpha) {
        super(
                (optionTaken, experimentResult, contextObject) ->
                        experimentResult,
                buildOptionsArray(numberOfOptions),
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
