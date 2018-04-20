package io.github.carrknight.bandits;

import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BelieveAllBanditImitationPolicyTest {

    @Test
    public void believeAll() {

        BelieveAllBanditImitationPolicy a = new BelieveAllBanditImitationPolicy();

        Observation input = mock(Observation.class);
        Observation output = a.decideOnAdditionalInformation(
                input,
                mock(BeliefState.class)
        );

        assertEquals(input,output);

    }
}