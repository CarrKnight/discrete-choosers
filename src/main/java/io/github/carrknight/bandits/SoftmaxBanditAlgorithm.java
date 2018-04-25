package io.github.carrknight.bandits;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.heatmaps.regression.LocalFilterSpace;
import io.github.carrknight.utils.BoltzmannDistribution;
import io.github.carrknight.utils.RewardFunction;
import io.github.carrknight.utils.averager.IterativeAverageFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;
import java.util.function.Function;

public class SoftmaxBanditAlgorithm<O,R,C> extends AbstractBanditAlgorithm<O,R,C> {

    /**
     * the higher the more it will randomize rather than going for the top
     */
    private double temperature;

    /**
     * called each time a decision is made, will update the temperature; This is useful if you want high exploration initially
     * but you want it to go down after a while
     */
    private Function<Double,Double> temperatureUpdater;


    public SoftmaxBanditAlgorithm(
            @NotNull RewardFunction<O, R,C> rewardExtractor, @NotNull O[] optionsAvailable, double initialExpectedReward,
            SplittableRandom randomizer,
            double temperature,
            Function<Double, Double> temperatureUpdater) {
        super(optionsAvailable, randomizer, new LocalFilterSpace<>(
                optionsAvailable,
                //by default use the standard average filter
                () -> new IterativeAverageFilter(initialExpectedReward),
                rewardExtractor,
                null
        ));
        setTemperature(temperature);
        this.temperatureUpdater = temperatureUpdater;
    }

    /**
     * to implement by subclasses; make a decision about what to play next AFTER learning has been done
     *
     * @param state the memory of the agent
     * @param optionsAvailable the options available
     * @param lastObservation the last observation made
     * @param lastChoice the last choice made
     * @return the next choice
     */
    @NotNull
    @Override
    protected O choose(
            BeliefState<O, R, C> state, @NotNull BiMap<O, Integer> optionsAvailable,
            @Nullable Observation<O, R, C> lastObservation, O lastChoice) {

        assert temperature>=1;
        //store memory of rewards into an array
        double[] rewards = new double[optionsAvailable.size()];
        for(int i=0; i<rewards.length; i++)
            rewards[i]=state.predict(optionsAvailable.inverse().get(i),
                                     lastObservation == null ? null : lastObservation.getContext()
                                     );
        //now pick by softmax
        BoltzmannDistribution distribution = new BoltzmannDistribution(rewards, temperature);

        int newChoice = distribution.sample(getRandomizer().nextDouble());

        //before returning update temperature
        setTemperature(temperatureUpdater.apply(temperature));

        return optionsAvailable.inverse().get(newChoice);

    }


    /**
     * Getter for property 'temperature'.
     *
     * @return Value for property 'temperature'.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Setter for property 'temperature'.
     *
     * @param temperature Value to set for property 'temperature'.
     */
    public void setTemperature(double temperature) {

        Preconditions.checkArgument(temperature>=1, "temperature shouldn't go below 1");

        this.temperature = temperature;
    }

    /**
     * Getter for property 'temperatureUpdater'.
     *
     * @return Value for property 'temperatureUpdater'.
     */
    public Function<Double, Double> getTemperatureUpdater() {
        return temperatureUpdater;
    }

    /**
     * Setter for property 'temperatureUpdater'.
     *
     * @param temperatureUpdater Value to set for property 'temperatureUpdater'.
     */
    public void setTemperatureUpdater(Function<Double, Double> temperatureUpdater) {
        this.temperatureUpdater = temperatureUpdater;
    }
}
