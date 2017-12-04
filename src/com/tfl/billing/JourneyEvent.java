package com.tfl.billing;

import java.util.UUID;

public abstract class JourneyEvent implements JourneyEventInterface {

    private final UUID cardId;
    private final UUID readerId;
    private final long time;

    public JourneyEvent(UUID cardId, UUID readerId, ClockInterface clock) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = clock.getTime();
    }

    @Override
    public UUID cardId() {
        return cardId;
    }

    @Override
    public UUID readerId() {
        return readerId;
    }

    @Override
    public long time() {
        return time;
    }
}
