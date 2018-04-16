package io.github.carrknight.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * any function that maps a (option,reward object, context) tuple to a double
 * @param <O> class of options available
 * @param <R> reward object (the output of an experiment; usually)
 * @param <C> context object
 */
public interface UtilityFunction<O,R,C> {



    public double extractUtility(
            @NotNull
                    O optionTaken,
            @NotNull
                    R experimentResult,
            @Nullable
                    C contextObject);

}
