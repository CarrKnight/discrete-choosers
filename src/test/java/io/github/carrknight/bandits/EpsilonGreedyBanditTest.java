package io.github.carrknight.bandits;

import io.github.carrknight.utils.SimpleObservation;
import io.github.carrknight.utils.averager.ExponentialMovingAverager;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class EpsilonGreedyBanditTest
{



    //10 options, the last is the best; can the epsilon greedy find it?
    @Test
    public void tenOptions()
    {


        SimpleEpsilonGreedyBandit bandit =
                new SimpleEpsilonGreedyBandit(
                        10,
                        System.currentTimeMillis(),
                        .2
                );


        Random random = new Random(System.currentTimeMillis());
        // should pick the best option
        for (int i = 0; i < 1000; i++) {
            int arm = bandit.getLastChoice();
            double reward = random.nextGaussian() / 2 + arm;
            bandit.updateAndChoose(
                    new SimpleObservation(arm,reward)
            );
        }

        //now you should be playing most
        bandit.setEpsilon(0);

        assertEquals(9, (int)bandit.updateAndChoose(null));

        System.out.println(bandit.getBanditState());
    }


    //exponential moving average should have little trouble adapting to changes in the reward
    @Test
    public void suddenChange()
    {


        SimpleEpsilonGreedyBandit bandit =
                new SimpleEpsilonGreedyBandit(
                        10,
                        System.currentTimeMillis(),
                        .8 //explore a lot
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
        bandit.setEpsilon(0);
        assertEquals(9, (int)bandit.updateAndChoose(null));
        System.out.println(bandit.getBanditState());

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
        assertEquals(0, (int)bandit.updateAndChoose(null));

    }


}