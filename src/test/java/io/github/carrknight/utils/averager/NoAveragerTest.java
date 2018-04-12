package io.github.carrknight.utils.averager;

import org.junit.Test;

import static org.junit.Assert.*;

public class NoAveragerTest {


    @Test
    public void noAverages() {

        NoAverager averager = new NoAverager();

        //by default with no previous observations the average is just the new observation
        double average = averager.computeNewAverage(0, 0, 1);
        assertEquals(average,1,.0001);

        average = averager.computeNewAverage(average,1,2d);
        average = averager.computeNewAverage(average,2,3d);
        average = averager.computeNewAverage(average,3,4d);
        assertEquals(4,average,.0001);
    }
}