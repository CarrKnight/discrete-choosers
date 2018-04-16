package io.github.carrknight.imitators;

import com.sun.javafx.UnmodifiableArrayList;

import java.util.SplittableRandom;

/**
 * any function that tells me how to "explore", that is how to pick a new spot at random
 */
public interface ExplorationStep<O,R,C> {

    O explore(
            UnmodifiableArrayList<O> optionsAvailable,
            O lastChoiceMade,
            SplittableRandom random);


}
