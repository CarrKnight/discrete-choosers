package io.github.carrknight.bandits;

import io.github.carrknight.utils.averager.IterativeAverager;
import org.junit.Test;

import static org.junit.Assert.*;

public class BanditStateTest {


    @Test
    public void banditState() {

        BanditState state = new BanditState(3,1);
        assertEquals(state.getAverageRewardObserved(0),1,.0001d);
        assertEquals(state.getAverageRewardObserved(1),1,.0001d);
        assertEquals(state.getNumberOfObservations(0),0);
        assertEquals(state.getNumberOfObservations(1),0);

        IterativeAverager averager = new IterativeAverager();
        state.observeNewReward(1,0,averager); //this will actually replace the original 1
        state.observeNewReward(2,0,averager);
        state.observeNewReward(3,0,averager);
        state.observeNewReward(4,0,averager);


        assertEquals(state.getAverageRewardObserved(0),2.5,.0001d);
        assertEquals(state.getAverageRewardObserved(1),1,.0001d);
        assertEquals(state.getNumberOfObservations(0),4);
        assertEquals(state.getNumberOfObservations(1),0);

    }
}