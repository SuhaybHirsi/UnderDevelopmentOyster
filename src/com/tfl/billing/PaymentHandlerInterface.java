package com.tfl.billing;

import com.tfl.external.Customer;

public interface PaymentHandlerInterface {

    void charge(Customer customer);
}
