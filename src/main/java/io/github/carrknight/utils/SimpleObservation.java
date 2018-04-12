package io.github.carrknight.utils;

import io.github.carrknight.Observation;
import org.jetbrains.annotations.NotNull;

/**
 * numerical observation with no context; this is just syntactic sugar
 */
public class SimpleObservation extends Observation<Integer,Double,Object> {


    public SimpleObservation(
            @NotNull Integer choiceMade,
            @NotNull Double rewardObserved) {
        super(choiceMade, rewardObserved);
    }
}
