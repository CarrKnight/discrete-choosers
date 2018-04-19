package io.github.carrknight.bandits;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import io.github.carrknight.Observation;
import io.github.carrknight.utils.BoltzmannDistribution;
import io.github.carrknight.utils.RewardFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SplittableRandom;
import java.util.function.Function;

public class SoftmaxBanditAlgorithm<O,R> extends ContextUnawareAbstractBanditAlgorithm<O,R> {

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
            @NotNull RewardFunction<O, R,Object> rewardExtractor, @NotNull O[] optionsAvailable, double initialExpectedReward,
            SplittableRandom randomizer,
            double temperature,
            Function<Double, Double> temperatureUpdater) {
        super(rewardExtractor, optionsAvailable, initialExpectedReward, randomizer);
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
            BanditState state, @NotNull BiMap<O, Integer> optionsAvailable,
            @Nullable Observation<O, R, Object> lastObservation, O lastChoice) {

        assert state.getNumberOfOptions() == optionsAvailable.size();
        assert temperature>=1;
        //store memory of rewards into an array
        double[] rewards = new double[state.getNumberOfOptions()];
        for(int i=0; i<rewards.length; i++)
            rewards[i]=state.getAverageRewardObserved(i);
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
