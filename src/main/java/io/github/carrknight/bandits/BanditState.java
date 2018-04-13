package io.github.carrknight.bandits;

import com.google.common.base.Objects;
import io.github.carrknight.utils.averager.Averager;

import java.util.Arrays;

/**
 * represent the memory of the bandit: how many observations for each slot machine you have and the current estimated expectations
 */
public class BanditState {


    /**
     * contains the current expected reward of each option; itself just an average of what we have observed so far
     */
    private final double[] averageReward;
    /**
     * contains the number of observations made for each reward
     */
    private final int[] observationsMade;

    /**
     * the number of observations made (this ought to be just the sum of each individual item in the array)
     */
    private int numberOfObservations = 0;


    public BanditState(int numberOfOptions, double initialAverageReward)
    {
        averageReward = new double[numberOfOptions];
        Arrays.fill(averageReward,initialAverageReward);
        observationsMade=new int[numberOfOptions];

    }

    public BanditState(int numberOfOptions)
    {
        this(numberOfOptions,0d);

    }


    public BanditState(double[] averageReward, int[] observationsMade)
    {
        this.averageReward = averageReward;
        this.observationsMade = observationsMade;
    }

    public double getAverageRewardObserved(int optionIndex){
        return averageReward[optionIndex];
    }

    public int getNumberOfObservations(int optionIndex)
    {
        return observationsMade[optionIndex];
    }


    /**
     * main method to update the bandit state: call this whenever we want to memorize a reward we observed
     * @param rewardObserved utility gained from playing an option
     * @param index the index representing the option taken
     * @param averager the object we use to average out our current knowledge of the reward and the new observation
     */
    public void observeNewReward(double rewardObserved, int index, Averager averager)
    {

        averageReward[index] =
                averager.computeNewAverage(averageReward[index],
                                           observationsMade[index],
                                           rewardObserved);

        observationsMade[index]++;
        numberOfObservations++;
        assert numberOfObservations == Arrays.stream(observationsMade).sum();


    }



    public int getNumberOfOptions(){
        return observationsMade.length;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BanditState{");
        sb.append("averageReward=");
        if (averageReward == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < averageReward.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(averageReward[i]);
            sb.append(']');
        }
        sb.append(", observationsMade=");
        if (observationsMade == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < observationsMade.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(observationsMade[i]);
            sb.append(']');
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Getter for property 'numberOfObservations'.
     *
     * @return Value for property 'numberOfObservations'.
     */
    public int getNumberOfObservations() {
        return numberOfObservations;
    }
}
