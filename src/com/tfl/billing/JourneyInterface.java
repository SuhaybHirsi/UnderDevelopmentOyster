package com.tfl.billing;

import java.util.Date;
import java.util.UUID;

public interface JourneyInterface {
    UUID originId();

    UUID destinationId();

    String formattedStartTime();

    String formattedEndTime();

    Date startTime();

    Date endTime();

    int durationSeconds();

    String durationMinutes();
}
