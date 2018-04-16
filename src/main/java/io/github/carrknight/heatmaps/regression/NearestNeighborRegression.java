package io.github.carrknight.heatmaps.regression;

import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import com.google.common.base.Preconditions;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.regression.distance.FeatureDistance;

import java.util.function.Function;

/**
 * classic nearest neighbour.
 * @param <O>
 * @param <R>
 * @param <C>
 */
public class NearestNeighborRegression<O,R,C> extends FeatureBasedRegression<O, R, C> {

    /**
     * KdTree doing all the work
     */
    private final KdTree<Double> nearestNeighborTree;

    /**
     * divide each feature distance by this to reweight them.
     * Strictly speaking these are copies of the feature based distances, however we store them here
     * because it makes the whole regression object easier to tune!
     */
    private double[] bandwidths;


    private final FeatureDistance transformer;

    /**
     * how many maxNeighbours to use
     */
    private int maxNeighbours;


    /**
     * how do we judge the distance between two nodes (this object basically just adapts our distance function
     * \to the distance function of the KD tree)
     */
    private DistanceFunction treeDistance;


    public NearestNeighborRegression(
            FeatureExtractor<O, C>[] extractors,
            Function<Observation<O, R, C>, Double> yExtractor,
            double[] bandwidths,
            FeatureDistance transformer, int maxNeighbours) {
        super(extractors, yExtractor);
        Preconditions.checkArgument(bandwidths.length==extractors.length,
                                    "The number of bandwidths should match number of extractors!");
        this.bandwidths = bandwidths;
        this.transformer = transformer;
        this.maxNeighbours = maxNeighbours;
        rebuildDistanceFunction(bandwidths);
        this.nearestNeighborTree = new KdTree<>(bandwidths.length);
    }

    public void rebuildDistanceFunction(final double[] bandwidths) {
        this.treeDistance =  new DistanceFunction() {
            @Override
            public double distance(double[] obs1, double[] obs2) {

                double distance = 0;
                for(int i = 0; i < obs1.length; i++)
                {
                    transformer.setBandwidth(bandwidths[i]);
                    distance += transformer.distance(obs1[i],obs2[i]);
                }
                return distance;

            }

            @Override
            public double distanceToRect(double[] observation,
                                         double[] min,
                                         double[] max) {
                double distance = 0;
                for(int i = 0; i < observation.length; i++)
                {
                    transformer.setBandwidth(bandwidths[i]);
                    double diff = 0;
                    if (observation[i] > max[i]) {
                        diff = transformer.distance(observation[i],max[i]);
                    }
                    else if (observation[i] < min[i]) {
                        diff = transformer.distance(observation[i],min[i]);
                    }
                    distance += diff;
                }
                return distance;
            }
        };
    }


    /**
     * gets the number of maxNeighbours required and return their average value
     * @param x the features
     * @return average value of nearest neighbours
     */
    @Override
    public double predict(double[] x) {
        if(nearestNeighborTree.size()<1)
            return Double.NaN;

        MaxHeap<Double> neighbors = nearestNeighborTree.findNearestNeighbors(x,
                                                                             this.maxNeighbours,
                                                                             treeDistance);

        double prediction = 0;
        double size = neighbors.size();
        while(neighbors.size()>0) {
            prediction += neighbors.getMax();
            neighbors.removeMax();
        }
        if(size>0)
            prediction= prediction/size;
        return  prediction;


    }

    @Override
    public void observe(Double y, double[] x) {

        nearestNeighborTree.addPoint(
                x,
                y
        );

    }


    /**
     * Transforms the parameters used (and that can be changed) into a double[] array so that it can be inspected
     * from the outside without knowing the inner workings of the regression
     *
     * @return an array containing all the parameters of the model
     */
    public double[] getParametersAsArray() {
        double[] parameters = new double[bandwidths.length+1];
        System.arraycopy(bandwidths,0,parameters,0,bandwidths.length);
        parameters[parameters.length-1] = maxNeighbours;
        return
                parameters;
    }

    /**
     * given an array of parameters (of size equal to what you'd get if you called the getter) the regression is supposed
     * to transition to these parameters
     *
     * @param parameterArray the new parameters for this regresssion
     */
    public void setParameters(double[] parameterArray) {
        assert parameterArray.length == this.bandwidths.length+1;
        for(int i=0; i<bandwidths.length; i++)
            this.bandwidths[i] = parameterArray[i];
        maxNeighbours = Math.max(1, (int) parameterArray[parameterArray.length-1]);
        rebuildDistanceFunction(bandwidths);
    }


    /**
     * Getter for property 'maxNeighbours'.
     *
     * @return Value for property 'maxNeighbours'.
     */
    public int getMaxNeighbours() {
        return maxNeighbours;
    }

    /**
     * Setter for property 'maxNeighbours'.
     *
     * @param maxNeighbours Value to set for property 'maxNeighbours'.
     */
    public void setMaxNeighbours(int maxNeighbours) {
        this.maxNeighbours = maxNeighbours;
    }
}
