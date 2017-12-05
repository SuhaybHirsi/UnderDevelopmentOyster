package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentStrategyInterface {

    BigDecimal totalJourneysFor(Customer customer, List<JourneyEvent> eventLog);

    List<Journey> getJourneysForCustomer();
}
