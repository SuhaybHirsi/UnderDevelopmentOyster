package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.ScanListener;

import java.util.UUID;

public interface CardReaderInterface {
    void register(ScanListener scanListener);

    void touch(OysterCard card);

    UUID id();
}
