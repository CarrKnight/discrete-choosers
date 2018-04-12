package io.github.carrknight.bandits;

import io.github.carrknight.Observation;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class IgnoreBanditImitationPolicyTest {

    @Test
    public void believeNoOne() {

        IgnoreBanditImitationPolicy a = new IgnoreBanditImitationPolicy();

        Observation input = mock(Observation.class);
        Observation output = a.decideOnAdditionalInformation(
                input,
                mock(BanditState.class)
        );

        assertNull(output);

    }

}