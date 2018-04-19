package io.github.carrknight.heatmaps.regression.distance;

/**
 * basically the opposite of a feature distance: the higher the number the closer these two are.
 * Kernels should have other properties: (1) should be 0 for the kernel of itself, (2) should always be positive, (3) should be symmetric.
 *
 * In reality none of this is enforced
 */
public interface FeatureKernel {


    public double similarity(double firstObservation, double secondObservation);


}
