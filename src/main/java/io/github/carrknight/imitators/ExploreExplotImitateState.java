package io.github.carrknight.imitators;

import io.github.carrknight.Observation;

/**
 * it may be best to consider the explore-exploit-imitate as a simple state machine
 */
public interface ExploreExplotImitateState<O,R,C> {

    public O getBestOption();

    public R getBestReward();

    public ExploreExploitImitatePair<O,R> resolve(
            Observation<O,R,C> observation
    );

}
