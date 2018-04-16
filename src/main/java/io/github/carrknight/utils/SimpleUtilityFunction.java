package io.github.carrknight.utils;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * simple utility function that ignores context and option picked and just focuses on the reward object
 * @param <R> the class of the reward object
 */
public abstract class SimpleUtilityFunction<R> implements UtilityFunction<Object, R,Object>,
        Function<R,Double> {


    @Override
    public double extractUtility(
            @NotNull Object optionTaken, @NotNull R experimentResult, @Nullable Object contextObject) {
        return apply(experimentResult);
    }
}
