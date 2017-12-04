package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentsSystemAdapter implements GeneralPaymentsSystem{

    private final Customer customer;
    private PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();
    private final PaymentStrategy strategy;



    public PaymentsSystemAdapter(PaymentStrategy strategy, Customer customer){
        this.strategy =  strategy;
        this.customer=customer;
    }

    @Override
    public void charge() {
        BigDecimal  totalBill = strategy.totalJourneysFor(customer);
        List<Journey> journeys = strategy.getJourneysForCustomer();
        paymentsSystem.charge(customer, journeys,totalBill);
    }
}
