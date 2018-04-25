package io.github.carrknight.utils;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.SplittableRandom;
import java.util.function.Function;

public class DiscreteChoosersUtilities {

    private DiscreteChoosersUtilities()
    {

    }


    /**
     * assign a fitness to all options, return the one with maximum fitness (randomizes draws)
     * @param possibleOptions options available
     * @param fitnessOfOption utility function for each option
     * @param randomizer randomizer
     * @param minimumMaximum the fitness threshold below which we don't care
     * @param <O> type of options
     * @return one of the top options or null if they are all below the minimum threshold
     */
     static  public <O> Pair<O,Double> getBestOption(
            Iterable<O> possibleOptions,
            Function<O,Double> fitnessOfOption,
            SplittableRandom randomizer,
            double minimumMaximum
    )
    {


        ArrayList<O> candidates = new ArrayList<>();
        double currentMax=minimumMaximum;
        for (O possibleOption : possibleOptions)
        {
            double reward =  fitnessOfOption.apply(possibleOption);
            if(Double.isFinite(reward) && reward>currentMax)
            {
                candidates.clear();
                candidates.add(possibleOption);
                currentMax=reward;
            }
            else if(reward==currentMax)
                candidates.add(possibleOption);

        }
        if(candidates.size()==0)
        {
            assert currentMax==minimumMaximum;
            return null;
        }
        else {

            return
                    new Pair<>(candidates.get(randomizer.nextInt(candidates.size())),
                               currentMax);

        }
    }



}
