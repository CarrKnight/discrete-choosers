package io.github.carrknight.heatmaps.regression.distance;

/**
 * given two objects of class O, return a number describing the distance between them
 * @param <O>
 */
public interface Distance<O> {


    public double distance(O first, O second);
}
