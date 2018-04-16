package io.github.carrknight.utils.rules;

import io.github.carrknight.Observation;
import io.github.carrknight.utils.UtilityFunction;

import java.util.SplittableRandom;

/**
 * explores with fixed probability
 * @param <O>
 * @param <R>
 * @param <C>
 */
public class StochasticObservationPredicate<O,R,C> implements ObservationPredicate<O, R, C> {

    private final double explorationProbability;


    public StochasticObservationPredicate(double explorationProbability) {
        this.explorationProbability = explorationProbability;
    }


    /**
     * @param lastObservation
     * @param currentChoice
     * @param utilityFunction utility function
     * @param random
     * @return true if the agent should "explore"
     */
    @Override
    public boolean shouldExplore(
            Observation<O, R, C> lastObservation, O currentChoice, UtilityFunction<O, R, C> utilityFunction,
            SplittableRandom random,
            Observation<O,R,C>... additionalObservations
    ) {
        return random.nextDouble()<explorationProbability;
    }
}
