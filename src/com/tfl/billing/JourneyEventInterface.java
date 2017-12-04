package com.tfl.billing;

import java.util.UUID;

public interface JourneyEventInterface {
    UUID cardId();

    UUID readerId();

    long time();
}
