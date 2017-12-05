package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

public class PaymentsSystemAdapter implements GeneralPaymentsSystem{

    private static PaymentsSystemAdapter instance = new PaymentsSystemAdapter();
    private PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();

    public static PaymentsSystemAdapter getInstance() {
        return instance;
    }

    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
        paymentsSystem.charge(customer, journeys, totalBill);
    }
}
