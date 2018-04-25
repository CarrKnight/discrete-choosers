package io.github.carrknight.imitators;

import com.google.common.collect.ObjectArrays;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.heatmaps.regression.FeatureExtractor;
import io.github.carrknight.heatmaps.regression.KernelRegression;
import io.github.carrknight.heatmaps.regression.distance.FeatureKernel;
import io.github.carrknight.heatmaps.regression.distance.RBFKernel;
import io.github.carrknight.utils.RewardFunction;
import io.github.carrknight.utils.SimpleObservation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ParticleSwarmTest {


    @Test
    public void inertiaWorks() {


        ParticleSwarm<Point2D, Double, Object> swarm = setup();


        // reward is best at 25,25
        for (int i = 0; i < 1000; i++) {
            Point2D arm = swarm.getLastChoice();
            double reward = -(Math.abs(arm.getX()-25)+Math.abs(arm.getY() - 25));
            //with no friends!
            swarm.updateAndChoose(
                    new Observation<>(arm, reward)
            );
            System.out.println(Arrays.toString(swarm.getCurrentPosition()));
        }

        //no good information, eventually you should stop!

        Assert.assertArrayEquals(new double[]{0,0},
                                 swarm.getCurrentVelocity(),
                                 .001d);






    }

    @Test
    public void kernel() {


        ParticleSwarm<Point2D, Double, Object> swarm = setup2();

        //you should be able to find your own way if you use a regression underneath your model


        //do not start around the best
        swarm.getCurrentPosition()[0] = (new SplittableRandom()).nextDouble(0,10);
        swarm.getCurrentPosition()[1] = (new SplittableRandom()).nextDouble(40,50);

        // reward is best at 25,25
        for (int i = 0; i < 50; i++) {
            Point2D arm = swarm.getLastChoice();
            double reward = -(Math.abs(arm.getX()-25)+Math.abs(arm.getY() - 25));
            //with no friends!
            swarm.updateAndChoose(
                    new Observation<>(arm, reward)
            );
            System.out.println(Arrays.toString(swarm.getCurrentPosition()));
            System.out.println(Arrays.toString(swarm.getCurrentVelocity()));
            System.out.println("===============================================");
        }

        //kind of optimal

        Assert.assertTrue(Math.abs(swarm.getCurrentPosition()[0]-25)<3 &&
                                   Math.abs(swarm.getCurrentPosition()[1]-25)<3);







    }

    @Test
    public void lead() {


        ParticleSwarm<Point2D, Double, Object> swarm = setup();


        //constant good friend
        Observation<Point2D,Double,Object> repellant = new Observation<>(
                new Point2D.Double(25,25),
                10000000d,
                null
        );

        //do not start around the best
        swarm.getCurrentPosition()[0] = (new SplittableRandom()).nextDouble(0,10);
        swarm.getCurrentPosition()[1] = (new SplittableRandom()).nextDouble(40,50);

        // reward is best at 25,25
        for (int i = 0; i < 1000; i++) {
            Point2D arm = swarm.getLastChoice();
            double reward = -(Math.abs(arm.getX()-25)+Math.abs(arm.getY() - 25));
            //with no friends!
            swarm.updateAndChoose(
                    new Observation<>(arm, reward),
                    repellant
            );
            System.out.println(Arrays.toString(swarm.getCurrentPosition()));
            System.out.println(Arrays.toString(swarm.getCurrentVelocity()));
            System.out.println("===============================================");
        }

        //the "friend" is feeding you good info, you should get to the top

        Assert.assertTrue(Math.abs(swarm.getCurrentPosition()[0]-25)<.001 &&
                                   Math.abs(swarm.getCurrentPosition()[0]-25)<.001);
        Assert.assertArrayEquals(new double[]{0,0},
                                 swarm.getCurrentVelocity(),
                                 .001d);






    }

    /**
     * creates a 50x50 map and a particle swarm object that needs to climb it!
     * @return
     */
    @NotNull
    private ParticleSwarm<Point2D, Double, Object> setup() {
        //50x50 map
        Point2D[][] grid = new Point2D[50][50];
        for(int x=0;x<50;x++) {
            for (int y = 0; y < 50; y++) {
                grid[x][y]=new Point2D.Double(x, y);
            }
        }

        Point2D[] oneDArray = new Point2D[grid.length*grid.length];
        //Flatten 2D array to 1D array...
        int s = 0;
        for(int i = 0; i < grid.length; i ++)
            for(int j = 0; j < grid.length; j ++){
                oneDArray[s] = grid[i][j];
                s++;
            }

        FeatureExtractor<Point2D,Object> extractors[] =
                new FeatureExtractor[]{
                        new FeatureExtractor<Point2D,Object>() {
                            @Override
                            public double extract(Point2D option, Object context) {
                                return option.getX();
                            }
                        },
                        new FeatureExtractor<Point2D,Object>() {
                            @Override
                            public double extract(Point2D option, Object context) {
                                return option.getY();
                            }
                        },
                };

        return new ParticleSwarm<Point2D, Double, Object>(
                extractors,
                position -> grid[
                        (int)Math.min(Math.max(0,position[0]),49d)]
                        [(int)Math.min(Math.max(0,position[1]),49d)],
                1d,
                1d,
                .7,
                5,
                (RewardFunction<Point2D, Double, Object>) (optionTaken, experimentResult, contextObject) ->
                        experimentResult,
                oneDArray,
                System.currentTimeMillis()
        );
    }

    /**
     * creates a 50x50 map and a particle swarm object USING KERNEL!
     * @return
     */
    @NotNull
    private ParticleSwarm<Point2D, Double, Object> setup2() {
        //50x50 map
        Point2D[][] grid = new Point2D[50][50];
        for(int x=0;x<50;x++) {
            for (int y = 0; y < 50; y++) {
                grid[x][y]=new Point2D.Double(x, y);
            }
        }

        Point2D[] oneDArray = new Point2D[grid.length*grid.length];
        //Flatten 2D array to 1D array...
        int s = 0;
        for(int i = 0; i < grid.length; i ++)
            for(int j = 0; j < grid.length; j ++){
                oneDArray[s] = grid[i][j];
                s++;
            }

        FeatureExtractor<Point2D,Object> extractors[] =
                new FeatureExtractor[]{
                        new FeatureExtractor<Point2D,Object>() {
                            @Override
                            public double extract(Point2D option, Object context) {
                                return option.getX();
                            }
                        },
                        new FeatureExtractor<Point2D,Object>() {
                            @Override
                            public double extract(Point2D option, Object context) {
                                return option.getY();
                            }
                        },
                };

        BeliefState<Point2D,Double,Object> regression =
                new KernelRegression<>(extractors,
                                       new Function<Observation<Point2D, Double, Object>, Double>() {
                                           @Override
                                           public Double apply(
                                                   Observation<Point2D, Double, Object>
                                                           obs) {
                                               return obs.getResultObserved();
                                           }
                                       },
                                       new FeatureKernel[]{
                                               new RBFKernel(5),
                                               new RBFKernel(5)
                                       },
                                       50);

        return new ParticleSwarm<Point2D, Double, Object>(
                extractors,
                position -> grid[
                        (int)Math.min(Math.max(0,position[0]),49d)]
                        [(int)Math.min(Math.max(0,position[1]),49d)],
                1d,
                .2d,
                .2,
                5,
                regression,
                (RewardFunction<Point2D, Double, Object>) (optionTaken, experimentResult, contextObject) ->
                        experimentResult,
                oneDArray,
                new SplittableRandom(System.currentTimeMillis()),
                null

        );
    }
}