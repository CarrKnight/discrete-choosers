package io.github.carrknight.utils.rules;

import io.github.carrknight.Observation;
import io.github.carrknight.utils.RewardFunction;

import java.util.SplittableRandom;

/**
 * any object that given the last observation (which could be null) and the current best option returns
 * wheter or not to do something (usually whether to explore or not)
 * @param <O>
 * @param <R>
 * @param <C>
 */
public interface ObservationPredicate<O,R,C>
{

    /**
     *
     * @param lastObservation
     * @param currentChoice
     * @param rewardFunction utility function
     * @param random
     * @return true if the agent should "explore"
     */
    public boolean shouldExplore(
            Observation<O,R,C> lastObservation,
            O currentChoice,
            RewardFunction<O,R,C> rewardFunction,
            SplittableRandom random,
            Observation<O,R,C>... additionalObservations
    );
}
