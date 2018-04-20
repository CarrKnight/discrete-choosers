package io.github.carrknight.bandits;


import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.carrknight.Chooser;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.heatmaps.regression.LocalFilterSpace;
import io.github.carrknight.heatmaps.regression.OneDimensionalFilter;
import io.github.carrknight.heatmaps.regression.distance.Similarity;
import io.github.carrknight.utils.RewardFunction;
import io.github.carrknight.utils.averager.IterativeAverageFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;
import java.util.function.Supplier;

/**
 * complicated name, simple class: it's an abstract class tagging and implementing the basics
 * of bandit algorithms that do not use context (which at least initially is all the bandit algorithms I have)
 * @param <O> the class of the options available (say, FishingSpot if we are modelling a fisher deciding where to go next);
 * @param <R> the class describing the reward object (say, FishCaught if we are modelling the fisher judging the spot they have just been to)

 */
public abstract class AbstractBanditAlgorithm<O,R,C> implements Chooser<O, R, C> {



    /**
     * A bimap describing all the options available to the bandit algorithm;
     * is a bimap really necessary? wouldn't two arrays do it?
     */
    @NotNull
    private final BiMap<O,Integer> optionsAvailable;

    /**
     * object storing current knowledge
     */
    private BeliefState<O,R,C> banditState;

    /**
     * what to do about additional observations; default is to ignore
     */
    private BanditImitationPolicy<O,R,C>  imitationPolicy =
            new IgnoreBanditImitationPolicy<>();


    /**
     * last time we updated, this was our choice
     */
    private O lastChoice;

    final private int[] timesPlayed;

    private int numberOfObservations=0;

    /**
     * the randomizer to use
     */
    private final SplittableRandom randomizer;

    /**
     * An array describing all the options available to the bandit algorithm
     * @param rewardExtractor transformer from R to double
     * @param optionsAvailable what kind of options are available
     * @param randomSeed random seed
     */
    public AbstractBanditAlgorithm(
            @NotNull

                    RewardFunction<O,R,C > rewardExtractor,
            @NotNull

                    O[] optionsAvailable, long randomSeed) {

        this(rewardExtractor,
             optionsAvailable,
             0d,
             new SplittableRandom(randomSeed));


    }




    public AbstractBanditAlgorithm(
            @NotNull RewardFunction<O,R,C > rewardExtractor, @NotNull O[] optionsAvailable,
            double initialExpectedReward, SplittableRandom randomizer) {
        Preconditions.checkArgument(optionsAvailable.length>0,
                                    "Given no options!");

        //turn array into bimap
        ImmutableBiMap.Builder<O, Integer> builder = ImmutableBiMap.builder();
        for(int i=0; i<optionsAvailable.length; i++)
            builder.put(optionsAvailable[i],i);

        this.optionsAvailable = builder.build();

        this.banditState = new LocalFilterSpace<>(
                optionsAvailable,
                //by default use the standard average filter
                () -> new IterativeAverageFilter(initialExpectedReward),
                rewardExtractor,
                null
        );

        this.randomizer = randomizer;
        this.lastChoice = optionsAvailable[randomizer.nextInt(optionsAvailable.length)];
        timesPlayed = new int[optionsAvailable.length];

    }


    /**
     * the main method of the chooser. It does two things at once:
     * * Receives new information given a previous action (and possibly additional information from observing other
     * choosers)
     * * Picks a new O for next step and return it
     *
     * @param observation            the reward and action taken last
     * @param additionalObservations additional action-rewards observed (by imitation or whatever)
     * @return O chosen to play next
     */
    @SafeVarargs
    @Override
    public final O updateAndChoose(
            Observation<O, R, C> observation,
            Observation<O, R, C>... additionalObservations) {
        //learn from the last observation
        if(observation!=null)
            learnFromObservation(observation);
        //decide whether to learn from additional observations
        for (Observation<O, R, C> additional : additionalObservations) {
            Observation<O, R, C> filtered =
                    imitationPolicy.decideOnAdditionalInformation(additional,
                                                                  banditState);
            if(filtered!=null)
                learnFromObservation(filtered);

        }

        //now pick new option
        lastChoice = choose(banditState,
                            optionsAvailable,
                            observation,
                            lastChoice);
        return lastChoice;

    }



    private void learnFromObservation(Observation<O, R, C> observation) {
        timesPlayed[optionsAvailable.get(observation.getChoiceMade())]++;
        numberOfObservations++;
        banditState.observe(observation);
    }


    /**
     * to implement by subclasses; make a decision about what to play next AFTER learning has been done
     * @param state
     * @param lastObservation
     * @return
     */
    @NotNull
    abstract protected O choose(
            BeliefState<O,R,C> state,
            @NotNull
                    BiMap<O,Integer> optionsAvailable,
            @Nullable
                    Observation<O,R,C> lastObservation,
            O lastChoice
    ) ;

    /**
     * this is a simple getter that returns what the last choice made was. *this does not update choices*
     *
     * @return
     */
    @Override
    public O getLastChoice() {
        return lastChoice;
    }


    /**
     * Getter for property 'banditState'.
     *
     * @return Value for property 'banditState'.
     */
    public BeliefState<O, R, C> getBanditState() {
        return banditState;
    }

    /**
     * Setter for property 'banditState'.
     *
     * @param banditState Value to set for property 'banditState'.
     */
    public void setBanditState(BeliefState<O, R, C> banditState) {
        this.banditState = banditState;
    }

    /**
     * Getter for property 'imitationPolicy'.
     *
     * @return Value for property 'imitationPolicy'.
     */
    public BanditImitationPolicy<O, R, C> getImitationPolicy() {
        return imitationPolicy;
    }

    /**
     * Setter for property 'imitationPolicy'.
     *
     * @param imitationPolicy Value to set for property 'imitationPolicy'.
     */
    public void setImitationPolicy(BanditImitationPolicy<O, R, C> imitationPolicy) {
        this.imitationPolicy = imitationPolicy;
    }



    /**
     * Getter for property 'randomizer'.
     *
     * @return Value for property 'randomizer'.
     */
    public SplittableRandom getRandomizer() {
        return randomizer;
    }

    public int getNumberOfTimesPlayed(O choice){
        return timesPlayed[optionsAvailable.get(choice)];

    }

    /**
     * Getter for property 'numberOfObservations'.
     *
     * @return Value for property 'numberOfObservations'.
     */
    public int getNumberOfObservations() {
        return numberOfObservations;
    }


    /**
     * utility method to change quickly the BeliefState to another 1D filter method. Will only work
     * if the current BeliefState is also simple
     */
    public void resetStateUsingThisFilter(Supplier<? extends OneDimensionalFilter> filter)
    {
        Preconditions.checkArgument(banditState instanceof LocalFilterSpace,
                                    "you must have changed the bandit state away from LocalFilterSpace, now you can't call reset; just set the new state directly");
        assert banditState instanceof LocalFilterSpace;
        ((LocalFilterSpace) banditState).resetFilter(filter);
    }


    public void resetSimilarityIndex(Similarity<O> similarity)
    {
        Preconditions.checkArgument(banditState instanceof LocalFilterSpace,
                                    "you must have changed the bandit state away from LocalFilterSpace, now you can't call reset; just set the new state directly");
        assert banditState instanceof LocalFilterSpace;
        ((LocalFilterSpace) banditState).setOptionSimilarity(similarity);
    }

}
