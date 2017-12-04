package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PaymentStrategy {


    BigDecimal totalJourneysFor(Customer customer);

    List<Journey> getJourneysForCustomer();

}