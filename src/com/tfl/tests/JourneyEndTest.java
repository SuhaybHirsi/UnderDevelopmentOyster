package com.tfl.tests;

import com.tfl.billing.ClockInterface;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.SystemClock;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.is;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;

public class JourneyEndTest {
    UUID CARD_ID = UUID.randomUUID();
    UUID READER_ID = UUID.randomUUID();
    ClockInterface clock= new SystemClock();

    JourneyEvent end_journey = new JourneyEnd(CARD_ID, READER_ID, clock);

    @Test
    public void checkReturnOfTime() {
        long time = System.currentTimeMillis();
        assertThat((double) time, is(closeTo((double) end_journey.time(), 10)));
    }
    @Test
    public void checksReturnOfCardID()
    {
        assertThat(end_journey.cardId(), is(CARD_ID));

    }

    @Test
    public void checksReturnOfReaderID()
    {
        assertThat(end_journey.readerId(), is(READER_ID));

    }




}