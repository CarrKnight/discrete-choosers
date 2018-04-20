package io.github.carrknight.heatmaps.regression.distance;

/**
 * given two objects of class O, return a number describing the similarity between them
 * @param <O> type of option
 * @param <C> context object
 */
public interface Similarity<O,C> {


    public double similarity(O first, O second, C context);
}
