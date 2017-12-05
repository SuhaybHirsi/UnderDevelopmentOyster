package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CalculationStrategyOne implements PaymentStrategyInterface {

    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    private final List<Journey> journeysForCustomer = new ArrayList<Journey>();
    private final List<JourneyEvent> eventLog;

    public CalculationStrategyOne(List<JourneyEvent> eventLog ){
        this.eventLog = eventLog;
    }

    @Override
    public BigDecimal totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeysForCustomer.add(new Journey(start, event));
                start = null;
            }
        }

        BigDecimal customerTotal = new BigDecimal(0);
        for (Journey journey : journeysForCustomer) {
            BigDecimal journeyPrice = OFF_PEAK_JOURNEY_PRICE;
            if (peak(journey)) {
                journeyPrice = PEAK_JOURNEY_PRICE;
            }
            customerTotal = customerTotal.add(journeyPrice);
        }

        return roundToNearestPenny(customerTotal);
    }

    @Override
    public List<Journey> getJourneysForCustomer() {
        return this.journeysForCustomer;
    }


    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }


    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }


}