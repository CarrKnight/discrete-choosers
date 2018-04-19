package io.github.carrknight.heatmaps.regression;

import com.google.common.io.Resources;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RecursiveLeastSquaresRegressionTest {

    @Test
    public void regression() throws Exception {

        //replicate regression I have done in R first

        List<String> data = Files.readAllLines(
                Paths.get(
                Resources.getResource("regression.csv").toURI()
                ));
        assertEquals(data.size(),100);

        //no forgetting, big initial uncertainty (to avoid settling too soon)
        //uncomittal 0 beta
        RecursiveLeastSquaresRegression tile = new RecursiveLeastSquaresRegression(0,
                                                                                   //as small as 10 would also work, of course
                                                                                   1000d,
                                                                                   2,
                                                                                   1d);

        for(String line : data)
        {
            String[] split = line.split(",");
            assertEquals(split.length,2);
            double x =  Double.parseDouble(split[0]);
            double y =  Double.parseDouble(split[1]);
            tile.observe(new double[]{1,x}, y,1d);
        }
        System.out.println(Arrays.toString(tile.getBeta()));
        assertEquals(1.97711,tile.getBeta()[0],.01);
        assertEquals(4.85097,tile.getBeta()[1],.01);
    }


}