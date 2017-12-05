package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.List;

public interface PaymentHandlerInterface {

    void charge(Customer customer, List<JourneyEvent> eventLog);
}
