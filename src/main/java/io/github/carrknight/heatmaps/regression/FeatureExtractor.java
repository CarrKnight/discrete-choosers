package io.github.carrknight.heatmaps.regression;


/**
 * basic object that observes a pair of option,context and returns a number which represents one of its features.
 * For example, imagine the option being a cell in a grid, one feature extractor could return its X value
 * @param <O> option class
 * @param <C> context class
 */
public interface FeatureExtractor<O,C> {


    /**
     * extract a single numerical feature from the option,context pair
     * @param option
     * @param context
     * @return
     */
    public double extract(O option, C context);


    /**
     * takes a series of extractors and returns it as an array of numerical features
     */
    static <O,C> double[] convertToFeatures(
            O option, C context, FeatureExtractor<O,C>[] extractors)
    {
        double[] observation = new double[extractors.length];
        for(int i=0; i<observation.length; i++)
            observation[i] = extractors[i].extract(option,context);
        return observation;
    }

}
