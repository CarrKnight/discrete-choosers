package io.github.carrknight.bandits;


import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import org.jetbrains.annotations.Nullable;

/**
 * basically the class deciding what should a bandit do with observed plays that weren't their own (say, a fisher
 * hearing from the grapevine somebody made a lot of money going to a specific position).
 */
public interface BanditImitationPolicy<O,R,C> {


    /**
     * given an observation that is not produced by the agent but somebody else, what should we do about it?
     * Anything you return will be memorized. You can do the memorization by yourself in this method and then return null
     * if you want to do something fancy (like weight it differently)
     * @param additionalInformation the observation produced elsewhere
     * @param state currentStateOfTheBandit
     * @return null means ignore, anything else that gets returned will be fed by the chooser to the banditState.
     */
    @Nullable
    public Observation<O,R,C> decideOnAdditionalInformation(
            Observation<O,R,C> additionalInformation,
            BeliefState<O,R,C> state
    );

}
