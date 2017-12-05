package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public class PaymentHandler implements PaymentHandlerInterface{

    private final PaymentStrategyInterface strategy;

    private GeneralPaymentsSystem payment_instance;

    public PaymentHandler(PaymentStrategyInterface strategy) {
        this.strategy=strategy;
        this.payment_instance = PaymentsSystemAdapter.getInstance();
    }

    public PaymentHandler(PaymentStrategyInterface strategy, GeneralPaymentsSystem payment_instance) {
        this.strategy=strategy;
        this.payment_instance = payment_instance;
    }

    @Override
    public void charge(Customer customer) {
        BigDecimal totalBill = strategy.totalJourneysFor(customer);
        List<Journey> journeys = strategy.getJourneysForCustomer();
        payment_instance.charge(customer, journeys,totalBill);
    }


}
