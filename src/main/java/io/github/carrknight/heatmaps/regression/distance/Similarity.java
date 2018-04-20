package io.github.carrknight.heatmaps.regression.distance;

/**
 * given two objects of class O, return a number describing the similarity between them
 * @param <O>
 */
public interface Similarity<O> {


    public double similarity(O first, O second);
}
