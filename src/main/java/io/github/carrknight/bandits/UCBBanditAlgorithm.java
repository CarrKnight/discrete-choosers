package io.github.carrknight.bandits;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import io.github.carrknight.Observation;
import io.github.carrknight.utils.DiscreteChoosersUtilities;
import io.github.carrknight.utils.UtilityFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.SplittableRandom;
import java.util.function.Function;

/**
 * the UCB1 algorithm; it requires bounded rewards between 1 and 0
 * @param <O>
 * @param <R>
 */
public class UCBBanditAlgorithm<O,R> extends ContextUnawareAbstractBanditAlgorithm<O, R> {

    private double sigma;


    public UCBBanditAlgorithm(
            @NotNull UtilityFunction<O,R,Object > rewardExtractor,
            @NotNull O[] optionsAvailable,
            double initialExpectedReward,
            SplittableRandom randomizer,
            double minimumRewardExpected,
            double maximumRewardExpected, double sigma) {
        super(
                new UtilityFunction<O, R, Object>() {
                    @Override
                    public double extractUtility(
                            @NotNull O optionTaken, @NotNull R experimentResult, @Nullable Object contextObject) {
                        //rescales rewards between 0 and 1!
                        double reward = rewardExtractor.extractUtility(optionTaken,
                                                                       experimentResult,
                                                                       contextObject);
                        reward = Math.min(Math.max(reward, minimumRewardExpected), maximumRewardExpected);
                        return (reward - minimumRewardExpected) / (maximumRewardExpected - minimumRewardExpected);

                    }
                }

                ,

                optionsAvailable, initialExpectedReward, randomizer);
        this.sigma = sigma;
    }

    /**
     * this is basically the bound generated by the Chernoff-Hoeffding inequality. The best explanation for it
     * is probably here: http://jeremykun.com/2013/10/28/optimism-in-the-face-of-uncertainty-the-ucb1-algorithm/
     *
     * @return
     */
    private double upperConfidenceBound(double average,
                                        int numberOfObservationsOnThisArm,
                                        int totalNumberOfObservations) {
        Preconditions.checkArgument(numberOfObservationsOnThisArm > 0);
        Preconditions.checkArgument(numberOfObservationsOnThisArm <= totalNumberOfObservations);
        return average + sigma *
                Math.sqrt(2 * Math.log(totalNumberOfObservations) / numberOfObservationsOnThisArm);
    }

    /**
     * chooses as UCB
     *
     * @param state
     * @param optionsAvailable
     * @param lastObservation
     * @param lastChoice
     * @return
     */
    @NotNull
    @Override
    protected O choose(
            BanditState state, @NotNull BiMap<O, Integer> optionsAvailable,
            @Nullable Observation<O, R, Object> lastObservation, O lastChoice) {

        int numberOfOptions = optionsAvailable.size();
        //if there is an option without a single played game, play that first
        if (numberOfOptions > state.getNumberOfObservations()) {
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int slotMachine = 0; slotMachine < numberOfOptions; slotMachine++) {

                if (state.getNumberOfObservations(slotMachine) == 0)
                    candidates.add(slotMachine);


            }

            assert candidates.size() >= 1;
            return
                    optionsAvailable.inverse().get(
                            candidates.get(getRandomizer().nextInt(candidates.size()))
                    );
        }
        //everything has been played at least once, proceed with standard UCB1 exploitation
        else {


            Integer bestIndex = DiscreteChoosersUtilities.getBestOption(
                    optionsAvailable.values(),
                    slotMachine -> upperConfidenceBound(
                            state.getAverageRewardObserved(slotMachine),
                            state.getNumberOfObservations(slotMachine),
                            state.getNumberOfObservations()
                    ),
                    getRandomizer(),
                    Double.NEGATIVE_INFINITY
            );
            assert bestIndex != null;
            return
                    optionsAvailable.inverse().get(bestIndex);

        }

    }


    /**
     * Getter for property 'sigma'.
     *
     * @return Value for property 'sigma'.
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * Setter for property 'sigma'.
     *
     * @param sigma Value to set for property 'sigma'.
     */
    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
}
