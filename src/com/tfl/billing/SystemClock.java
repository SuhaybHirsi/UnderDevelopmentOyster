package com.tfl.billing;

public class SystemClock implements ClockInterface {
    @Override
    public long getTime()
    {
        return System.currentTimeMillis();
    }
}
