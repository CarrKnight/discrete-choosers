package io.github.carrknight.bandits;


import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.carrknight.Chooser;
import io.github.carrknight.Observation;
import io.github.carrknight.utils.RewardFunction;
import io.github.carrknight.utils.averager.Averager;
import io.github.carrknight.utils.averager.IterativeAverager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;

/**
 * complicated name, simple class: it's an abstract class tagging and implementing the basics
 * of bandit algorithms that do not use context (which at least initially is all the bandit algorithms I have)
 * @param <O> the class of the options available (say, FishingSpot if we are modelling a fisher deciding where to go next);
 * @param <R> the class describing the reward object (say, FishCaught if we are modelling the fisher judging the spot they have just been to)

 */
public abstract class ContextUnawareAbstractBanditAlgorithm<O,R> implements Chooser<O, R, Object> {


    /**
     * In the end these bandits want a reward that is numeric. This takes the R observed and returns a single number
     * describing how good it is. The higher the better
     */
    @NotNull
    private final RewardFunction<O,R,Object > rewardExtractor;

    /**
     * A bimap describing all the options available to the bandit algorithm;
     * is a bimap really necessary? wouldn't two arrays do it?
     */
    @NotNull
    private final BiMap<O,Integer> optionsAvailable;

    /**
     * object storing current knowledge
     */
    private BanditState banditState;

    /**
     * what to do about additional observations; default is to ignore
     */
    private BanditImitationPolicy<O,R,Object>  imitationPolicy =
            new IgnoreBanditImitationPolicy<>();

    /**
     * how to smooth current knowledge with new observations? Currently just averages stuff out
     */
    private Averager averager = new IterativeAverager();

    /**
     * last time we updated, this was our choice
     */
    private O lastChoice;


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
    public ContextUnawareAbstractBanditAlgorithm(
            @NotNull

                    RewardFunction<O,R,Object > rewardExtractor,
            @NotNull

                    O[] optionsAvailable, long randomSeed) {

        this(rewardExtractor,
             optionsAvailable,
             0d,
             new SplittableRandom(randomSeed));


    }








    public ContextUnawareAbstractBanditAlgorithm(
            @NotNull RewardFunction<O,R,Object > rewardExtractor, @NotNull O[] optionsAvailable,
            double initialExpectedReward, SplittableRandom randomizer) {
        Preconditions.checkArgument(optionsAvailable.length>0,
                                    "Given no options!");

        this.rewardExtractor = rewardExtractor;

        //turn array into bimap
        ImmutableBiMap.Builder<O, Integer> builder = ImmutableBiMap.builder();
        for(int i=0; i<optionsAvailable.length; i++)
            builder.put(optionsAvailable[i],i);

        this.optionsAvailable = builder.build();

        this.banditState = new BanditState(
                optionsAvailable.length,
                initialExpectedReward
        );

        this.randomizer = randomizer;
        this.lastChoice = optionsAvailable[randomizer.nextInt(optionsAvailable.length)];

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
            Observation<O, R, Object> observation,
            Observation<O, R, Object>... additionalObservations) {
        //learn from the last observation
        if(observation!=null)
            learnFromObservation(observation);
        //decide whether to learn from additional observations
        for (Observation<O, R, Object> additional : additionalObservations) {
            Observation<O, R, Object> filtered =
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



    private void learnFromObservation(Observation<O, R, Object> observation) {
        int observationIndex = optionsAvailable.get(observation.getChoiceMade());
        double rewardObtained = rewardExtractor.extractUtility(
                observation.getChoiceMade(),
                observation.getResultObserved(),
                null);
        banditState.observeNewReward(rewardObtained,observationIndex,averager);
    }


    /**
     * to implement by subclasses; make a decision about what to play next AFTER learning has been done
     * @param state
     * @param lastObservation
     * @return
     */
    @NotNull
    abstract protected O choose(
            BanditState state,
            @NotNull
                    BiMap<O,Integer> optionsAvailable,
            @Nullable
                    Observation<O,R,Object> lastObservation,
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
    public BanditState getBanditState() {
        return banditState;
    }

    /**
     * Setter for property 'banditState'.
     *
     * @param banditState Value to set for property 'banditState'.
     */
    public void setBanditState(BanditState banditState) {
        this.banditState = banditState;
    }

    /**
     * Getter for property 'imitationPolicy'.
     *
     * @return Value for property 'imitationPolicy'.
     */
    public BanditImitationPolicy<O, R, Object> getImitationPolicy() {
        return imitationPolicy;
    }

    /**
     * Setter for property 'imitationPolicy'.
     *
     * @param imitationPolicy Value to set for property 'imitationPolicy'.
     */
    public void setImitationPolicy(BanditImitationPolicy<O, R, Object> imitationPolicy) {
        this.imitationPolicy = imitationPolicy;
    }

    /**
     * Getter for property 'averager'.
     *
     * @return Value for property 'averager'.
     */
    public Averager getAverager() {
        return averager;
    }

    /**
     * Setter for property 'averager'.
     *
     * @param averager Value to set for property 'averager'.
     */
    public void setAverager(Averager averager) {
        this.averager = averager;
    }

    /**
     * Getter for property 'randomizer'.
     *
     * @return Value for property 'randomizer'.
     */
    public SplittableRandom getRandomizer() {
        return randomizer;
    }
}
