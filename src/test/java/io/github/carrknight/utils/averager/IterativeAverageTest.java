package io.github.carrknight.utils.averager;

import io.github.carrknight.utils.averager.IterativeAverager;
import org.junit.Test;

import static org.junit.Assert.*;

public class IterativeAverageTest {


    @Test
    public void average() throws Exception {

        IterativeAverager averager = new IterativeAverager();

        double average = averager.computeNewAverage(0, 0, 1);
        assertEquals(average,1,.0001);

        average = averager.computeNewAverage(average,1,2d);
        average = averager.computeNewAverage(average,2,3d);
        average = averager.computeNewAverage(average,3,4d);
        assertEquals(2.5,average,.0001);
        }

}