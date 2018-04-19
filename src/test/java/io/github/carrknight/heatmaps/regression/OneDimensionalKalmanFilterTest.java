package io.github.carrknight.heatmaps.regression;

import org.junit.Test;

import java.util.SplittableRandom;

import static org.junit.Assert.*;

public class OneDimensionalKalmanFilterTest {


    @Test
    public void kalman() {
        OneDimensionalKalmanFilter kalmanFilter = new OneDimensionalKalmanFilter(
                1,1,50*50,0,1
        );
        //we start at N(0,50^2)
        assertEquals(kalmanFilter.getStandardDeviation(),50,.0001);
        assertEquals(kalmanFilter.getStateEstimate(),0,.0001);

        //if I keep observing 30 then my state estimate should move to there and my standard deviation drop
        for(int i=0; i<10; i++) {
            kalmanFilter.observe(30,1d/5d);
            System.out.println(kalmanFilter.getStateEstimate() +
                                       " ==== " +
                                       kalmanFilter.getStandardDeviation());
        }

        assertTrue(kalmanFilter.getStandardDeviation()<1);
        assertEquals(kalmanFilter.getStateEstimate(),30,1);

        System.out.println("==============================================");
        //if I keep elapsing time the mean stays constant but the uncertainty grows
        for(int i=0; i<100; i++)
        {
            kalmanFilter.drift();
            System.out.println(kalmanFilter.getStateEstimate() +
                                       " ==== " +
                                       kalmanFilter.getStandardDeviation());
        }
        assertTrue(kalmanFilter.getStandardDeviation()>5);
        assertEquals(kalmanFilter.getStateEstimate(),30,1);

    }
}