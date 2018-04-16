package io.github.carrknight.imitators;

import com.sun.javafx.UnmodifiableArrayList;

import java.util.SplittableRandom;

/**
 * simplest possible exploration rule: pick one option at random!
 * @param <O>
 * @param <R>
 * @param <C>
 */
public class ExploreAtRandom<O,R,C> implements ExplorationStep<O, R, C> {
    @Override
    public O explore(
            UnmodifiableArrayList<O> optionsAvailable, O lastChoiceMade, SplittableRandom random) {
        return optionsAvailable.get(
                random.nextInt(optionsAvailable.size())
        );
    }
}
