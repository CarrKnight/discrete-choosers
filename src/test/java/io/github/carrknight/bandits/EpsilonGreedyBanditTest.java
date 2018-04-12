package io.github.carrknight.bandits;

import io.github.carrknight.utils.SimpleObservation;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class EpsilonGreedyBanditTest
{



    //10 options, the last is the best; can the epsilon greedy find it?
    @Test
    public void tenOptions() throws Exception
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

        assertEquals(9, (int)bandit.skipRoundAndChoose());

        System.out.println(bandit.getBanditState());
    }



}