package io.github.carrknight.heatmaps.regression;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.FeatureKernel;
import io.github.carrknight.heatmaps.regression.distance.RBFKernel;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.function.Function;

public class KernelRegressionTest {

    @Test
    public void smoothPrediction() {
        //2 dimensional regressions where you are trying to predict over X_position,Y_position
        FeatureBasedRegression<Point2D,
                Double,
                Double> regression =
                new KernelRegression<Point2D, Double, Double>(
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
                        new FeatureKernel[]{
                                new RBFKernel(100),
                                new RBFKernel(100)
                        },
                        2



                );

        //observe a 1 at X{0,0}
        regression.observe(new Observation<Point2D, Double, Double>(
                new Point2D.Double(0,0),
                1d,
                null
        ));

        //observe a 100 at X={10,10}

        regression.observe(new Observation<Point2D, Double, Double>(
                new Point2D.Double(10,10),
                100d,
                null
        ));


        System.out.println(regression.predict(new double[]{0,0}));
        System.out.println(regression.predict(new double[]{1,0}));
        System.out.println(regression.predict(new double[]{5,5}));
        System.out.println(regression.predict(new double[]{10,10}));
        Assert.assertTrue(regression.predict(new double[]{0,0})<
                                  regression.predict(new double[]{1,0}));
        Assert.assertTrue(regression.predict(new double[]{1,0})<
                                  regression.predict(new double[]{1,1}));

        Assert.assertTrue(regression.predict(new double[]{8,8})<
                                  regression.predict(new double[]{9,9}));

        Assert.assertTrue(Double.isNaN(regression.predict(new double[]{80,80})));


        //it will pop out the oldest observation when we add this one in
        regression.observe(new Observation<Point2D, Double, Double>(
                new Point2D.Double(0,0),
                100d,
                null));

        Assert.assertTrue(((KernelRegression<Point2D, Double, Double>) regression).getObservations().size()==2);
        Assert.assertEquals(regression.predict(new double[]{0,0}),100,.0001);
        Assert.assertEquals(regression.predict(new double[]{1,0}),100,.0001);
        Assert.assertEquals(regression.predict(new double[]{5,5}),100,.0001);
        Assert.assertEquals(regression.predict(new double[]{10,10}),100,.0001);

    }




}