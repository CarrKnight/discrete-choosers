package io.github.carrknight.bandits;

import io.github.carrknight.Observation;
import org.jetbrains.annotations.Nullable;

public class BelieveAllBanditImitationPolicy<O,R,C> implements BanditImitationPolicy<O, R, C> {
    /**
     * treats all observations as believable and made by the chooser itself
     *
     * @param additionalInformation the observation produced elsewhere
     * @param state                 currentStateOfTheBandit
     * @return always returns the additionalInformation as
     */
    @Nullable
    @Override
    public Observation<O, R, C> decideOnAdditionalInformation(
            Observation<O, R, C> additionalInformation, BanditState state) {
        return additionalInformation;
    }
}
