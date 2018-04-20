package io.github.carrknight.heatmaps.regression;

import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import io.github.carrknight.heatmaps.regression.distance.FeatureDistance;

/**
 * nearest neighbour regression implemented by KD-Tree. Simple and scalable although with discontinuities everywhere!
 */
public class NearestNeighborNumericalRegression implements NumericalRegression {


    /**
     * KdTree doing all the work
     */
    private final KdTree<Double> nearestNeighborTree;

    /**
     * divide each feature similarity by this to reweight them.
     * Strictly speaking these are copies of the feature based distances, however we store them here
     * because it makes the whole regression object easier to tune!
     */
    private final double[] bandwidths;


    /**
     * similarity function between dimensions
     */
    private final FeatureDistance transformer;


    /**
     * how many neighbours to use to make a prediction
     */
    private int maxNeighbours;


    /**
     * how do we judge the similarity between two nodes (this object basically just adapts our similarity function
     * \to the similarity function of the KD tree)
     */
    private DistanceFunction treeDistance;


    public NearestNeighborNumericalRegression(
            double[] bandwidths,
            FeatureDistance distance, int maxNeighbours) {
        this.bandwidths = bandwidths;
        this.transformer = distance;
        this.maxNeighbours = maxNeighbours;
        this.nearestNeighborTree = new KdTree<>(bandwidths.length);
        rebuildDistanceFunction(bandwidths);
    }

    /**
     * gets the number of maxNeighbours required and return their average value
     * @param x the features
     * @return average value of nearest neighbours
     */
    @Override
    public double predict(double[] x) {
        //never bother if any feature is NaN
        if(!NumericalRegression.isValidInput(x))
            return Double.NaN;

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

    /**
     * Add observation to KD tree
     * @param x the exogenous variables observed
     * @param y the endogenous variable observed
     */
    @Override
    public void observe(double[] x, Double y) {

        //never bother if any feature is NaN
        if(!NumericalRegression.isValidInput(x,y))
            return;

        nearestNeighborTree.addPoint(
                x,
                y
        );

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
     * Returns a defensive copy of the bandwidth array
     *
     * @return an array containing all the parameters of the model
     */
    public double[] getBandwidths() {
        double[] parameters = new double[bandwidths.length+1];
        System.arraycopy(bandwidths,0,parameters,0,bandwidths.length);
        parameters[parameters.length-1] = maxNeighbours;
        return
                parameters;
    }

    /**
     * set new bandwidths
     *
     * @param parameterArray the new parameters for this regresssion
     */
    public void setBandwidths(double[] parameterArray) {
        assert parameterArray.length == this.bandwidths.length;
        for(int i=0; i<bandwidths.length; i++)
            this.bandwidths[i] = parameterArray[i];
        rebuildDistanceFunction(bandwidths);
    }

}
