package io.github.carrknight.bandits;

import io.github.carrknight.utils.SimpleObservation;
import io.github.carrknight.utils.averager.ExponentialMovingAverager;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UCB1BanditAlgorithmTest {


    //10 options, the last is the best; can UCB1 find it?
    @Test
    public void tenOptions()
    {


        SimpleUCBBanditAlgorithm bandit =
                new SimpleUCBBanditAlgorithm(
                        10,
                        System.currentTimeMillis(),
                        0,10
                );


        Random random = new Random(System.currentTimeMillis());
        // should pick the best option
        for (int i = 0; i < 1000; i++) {
            int arm = bandit.getLastChoice();
            double reward = random.nextGaussian() / 2 + arm;
            bandit.updateAndChoose(
                    new SimpleObservation(arm, reward)
            );
        }


        System.out.println(bandit.getBanditState());
        for(int i=0; i<9; i++) {
            assertTrue(bandit.getBanditState().getAverageRewardObserved(9) >
                               bandit.getBanditState().getAverageRewardObserved(i));
            assertTrue(bandit.getBanditState().getNumberOfObservations(9) >
                               bandit.getBanditState().getNumberOfObservations(i));
        }

    }



    //exponential moving average should have little trouble adapting to changes in the reward
    @Test
    public void suddenChange()
    {

        SimpleUCBBanditAlgorithm bandit =
                new SimpleUCBBanditAlgorithm(
                        10,
                        System.currentTimeMillis(),
                        -200,200
                );

        bandit.setAverager(new ExponentialMovingAverager(.4));

        Random random = new Random(System.currentTimeMillis());
        // should pick the best option
        for (int i = 0; i < 1000; i++) {
            int arm = bandit.getLastChoice();
            double reward = random.nextGaussian() / 2 + 20 * arm;
            bandit.updateAndChoose(
                    new SimpleObservation(arm,reward)
            );
        }

        //now you should be playing best
        for(int i=0; i<9; i++) {
            assertTrue(bandit.getBanditState().getAverageRewardObserved(9) >
                               bandit.getBanditState().getAverageRewardObserved(i));
            assertTrue(bandit.getBanditState().getNumberOfObservations(9) >
                               bandit.getBanditState().getNumberOfObservations(i));
        }

        //but now reverse rewards!
        //NOTICE IT TAKES A LOT LONGER! UCB1 isn't that adaptive
        for (int i = 0; i < 1000; i++) {
            int arm = bandit.getLastChoice();
            double reward = random.nextGaussian() / 2 - 20 * arm;
            bandit.updateAndChoose(
                    new SimpleObservation(arm,reward)
            );
        }


        System.out.println(bandit.getBanditState());

        //should have switched!
        for(int i=1; i<10; i++) {
            assertTrue(bandit.getBanditState().getAverageRewardObserved(0) >
                               bandit.getBanditState().getAverageRewardObserved(i));
        }

    }


}
