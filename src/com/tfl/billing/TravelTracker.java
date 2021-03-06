package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;

import java.util.*;

public class TravelTracker implements ScanListener {
    private final List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();
    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();
    private Database customerDatabase;
    private PaymentHandlerInterface payment_handler;
    private final ClockInterface clock;


    public TravelTracker() {
        this.customerDatabase = CustomerDatabaseAdapter.getInstance();
        this.payment_handler = new PaymentHandler(new CalculationStrategyOne());
        this.clock=new SystemClock();

    }

    public TravelTracker(Database customer_database, PaymentHandlerInterface payment_handler, ClockInterface clock) {


        this.customerDatabase = customer_database;
        this.payment_handler = payment_handler;
        this.clock=clock;
    }


    public void chargeAccounts() {


        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            payment_handler.charge(customer, eventLog);
        }
    }


    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

//    public void connect(CardReaderInterface... cardReaders) {
//        for (CardReaderInterface cardReader : cardReaders) {
//            cardReader.register(this);
//        }
//    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId, clock));
            currentlyTravelling.remove(cardId);
        } else {
            if (customerDatabase.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId, clock));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

    public List<JourneyEvent> getEventLog()
    {
        return eventLog;
    }

}
