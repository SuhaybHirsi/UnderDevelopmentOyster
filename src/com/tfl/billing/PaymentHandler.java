package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public class PaymentHandler implements PaymentHandlerInterface{

    private final PaymentStrategyInterface strategy;

    private GeneralPaymentsSystem payment_adapter;

    public PaymentHandler(PaymentStrategyInterface strategy) {
        this.strategy=strategy;
        this.payment_adapter = PaymentsSystemAdapter.getInstance();
    }

    public PaymentHandler(PaymentStrategyInterface strategy, GeneralPaymentsSystem payment_adapter) {
        this.strategy=strategy;
        this.payment_adapter = payment_adapter;
    }

    @Override
    public void charge(Customer customer,List<JourneyEvent> eventLog) {
        BigDecimal totalBill = strategy.totalJourneysFor(customer, eventLog);
        List<Journey> journeys = strategy.getJourneysForCustomer();
        payment_adapter.charge(customer, journeys,totalBill);
    }


}
