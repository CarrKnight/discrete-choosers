package io.github.carrknight.bandits;

import io.github.carrknight.utils.SimpleObservation;
import io.github.carrknight.utils.averager.ExponentialMovingAverager;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class SoftmaxBanditAlgorithmTest {





    //10 options, the last is the best; can the SOFTMAX find it?
    @Test
    public void tenOptions() {


        //you need to add a high initial expectation if you don't want to get stuck!
        SimpleSoftmaxBanditAlgorithm bandit =
                new SimpleSoftmaxBanditAlgorithm(
                        10,
                        System.currentTimeMillis(),
                        100
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

        //you should assume the bandit is the best
        //and you played the best the most
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


        SimpleSoftmaxBanditAlgorithm bandit =
                new SimpleSoftmaxBanditAlgorithm(
                        10,
                        System.currentTimeMillis(),
                        0,
                        23163.6, //number that will drop to 1 after 1000 steps
                        .99

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
        System.out.println(bandit.getBanditState());
        for(int i=0; i<9; i++) {
            assertTrue(bandit.getBanditState().getAverageRewardObserved(9) >
                               bandit.getBanditState().getAverageRewardObserved(i));
            assertTrue(bandit.getBanditState().getNumberOfObservations(9) >
                               bandit.getBanditState().getNumberOfObservations(i));
        }

        //but now reverse rewards!
        for (int i = 0; i < 100; i++) {
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