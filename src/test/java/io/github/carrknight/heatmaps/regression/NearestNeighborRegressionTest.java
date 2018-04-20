package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.AbsoluteFeatureDistance;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class NearestNeighborRegressionTest {


    @Test
    public void correctNeighbor() throws Exception
    {

        //2 dimensional regressions where you are trying to predict over X_position,Y_position
        FeatureBasedRegression<Point2D,
                        Double,
                        Double> regression =
                new NearestNeighborRegression<Point2D, Double, Double>(
                        new FeatureExtractor[]{
                                (FeatureExtractor<Point2D, Double>) (option, context) -> option.getX(),
                                (FeatureExtractor<Point2D, Double>) (option, context) -> option.getY()
                        },
                        new Function<Observation<Point2D, Double, Double>, Double>() {
                            @Override
                            public Double apply(
                                    Observation<Point2D, Double, Double> observation) {
                                return  observation.getResultObserved();
                            }
                        },
                        new double[]{1,1},
                        new AbsoluteFeatureDistance(0), //this bandwitdh gets ignored
                        1


                );

        //observe a 100 at X={10,10}
        regression.observe(new Observation<Point2D, Double, Double>(
                new Point2D.Double(10,10),
                100d,
                null
        ));

        //observe a 1 at X{0,0}
        regression.observe(new Observation<Point2D, Double, Double>(
                new Point2D.Double(0,0),
                1d,
                null
        ));


        assertEquals(regression.predict(new double[]{0,0}),1,.001);
        assertEquals(regression.predict(new double[]{1,0}),1,.001);
        assertEquals(regression.predict(new double[]{0,1}),1,.001);
        assertEquals(regression.predict(new double[]{3,3}),1,.001);
        assertEquals(regression.predict(new double[]{6,6}),100,.001);
        assertEquals(regression.predict(new double[]{30,30}),100,.001);
        assertEquals(regression.predict(new double[]{3,10}),100,.001);





        //change bandwidths: stop caring about second dimension
        ((NearestNeighborRegression<Point2D, Double, Double>) regression).setBandwidths(
                new double[]{1,1000000}
        );
        assertEquals(regression.predict(new double[]{0,0}),1,.001);
        assertEquals(regression.predict(new double[]{3,10}),1,.001); //this used to be 1

    }

}