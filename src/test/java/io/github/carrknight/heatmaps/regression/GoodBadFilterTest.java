package io.github.carrknight.heatmaps.regression;

import org.junit.Test;

import static org.junit.Assert.*;

public class GoodBadFilterTest {


    @Test
    public void twoSteps() {


        GoodBadFilter filter = new GoodBadFilter(
                0,100,50,
                0
        );

        filter.observe(100,1);
        assertEquals(0.8807971,filter.getProbabilityBeingAGoodSpot(),.0001);
        filter.observe(100,1);

        assertEquals(0.9820138,filter.getProbabilityBeingAGoodSpot(),.0001);

    }
}