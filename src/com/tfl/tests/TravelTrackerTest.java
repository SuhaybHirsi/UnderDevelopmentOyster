package com.tfl.tests;

import com.tfl.billing.*;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.junit.Test;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;


import java.lang.reflect.Field;
import java.util.UUID;

import com.oyster.*;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.*;

import com.oyster.OysterCard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;

import static java.util.Arrays.asList;

public class TravelTrackerTest {

    private class PaymentHandlerDouble implements PaymentHandlerInterface
    {
        private final PaymentStrategyInterface strategy;
        private List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();
        private GeneralPaymentsSystem payment_instance;
        private List<Customer> customers = new ArrayList<Customer>();

        public PaymentHandlerDouble(PaymentStrategyInterface strategy, GeneralPaymentsSystem payment_instance) {
            this.strategy=strategy;
            this.payment_instance = payment_instance;
        }

        @Override
        public void charge(Customer customer,List<JourneyEvent> eventLog) {
            this.eventLog=eventLog;
            customers.add(customer);
            BigDecimal totalBill = strategy.totalJourneysFor(customer, eventLog);
            List<Journey> journeys = strategy.getJourneysForCustomer();
            payment_instance.charge(customer, journeys,totalBill);
        }

        public List<JourneyEvent> getEventLog()
        {
            return eventLog;
        }

        public List<Customer> getCustomers()
        {
            return customers;
        }
    }

    private class ClockTestDouble implements ClockInterface
    {
        ArrayList<Long> myTimes = new ArrayList<>();
        @Override
        public long getTime()
        {
            long temp = myTimes.get(0);
            myTimes.remove(0);

            return  temp;
        }
        public void addTime(long time)
        {
            myTimes.add(time);
        }

    }

    private class CustomerDatabaseTestDouble implements Database
    {
        private List<Customer> customers = new ArrayList<Customer>() {
            {
                this.add(new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d")));
//                this.add(new Customer("Shelly Cooper", new OysterCard("3f1b3b55-f266-4426-ba1b-bcc506541866")));
//                this.add(new Customer("Oliver Morrell", new OysterCard("07b0bcb1-87df-447f-bf5c-d9961ab9d01e")));
//                this.add(new Customer("Jesse Schmitz", new OysterCard("3b5a03cb-2be6-4ed3-b83e-94858b43e407")));
            }

            public boolean add(Customer customer) {
                return super.add(customer);
            }
        };


        @Override
        public List<Customer> getCustomers() {
            return customers;
        }

        @Override
        public boolean isRegisteredId(UUID cardId) {
            return true;
        }
    }

    private class PaymentSystemTestDouble implements GeneralPaymentsSystem
    {

        @Override
        public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
            System.out.println("\n\n*****************\n\n");
            System.out.println("Customer: " + customer.fullName() + " - " + customer.cardId());
            System.out.println("Journey Summary:");
            Iterator i$ = journeys.iterator();

            while(i$.hasNext()) {
                Journey journey = (Journey)i$.next();
                System.out.println(journey.formattedStartTime() + "\t" + this.stationWithReader(journey.originId()) + "\t" + " -- " + journey.formattedEndTime() + "\t" + this.stationWithReader(journey.destinationId()));
            }

            System.out.println("Total charge £: " + totalBill);
        }

        private String stationWithReader(UUID originId) {
            return OysterReaderLocator.lookup(originId).name();
        }
    }

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    PaymentHandlerInterface mockPaymentHandler = context.mock(PaymentHandlerInterface.class);
    Database mockCustomerDB = context.mock(Database.class);

    ClockTestDouble myClock= new ClockTestDouble();
    ClockTestDouble tempClock= new ClockTestDouble();
    final Database myCustomerDB = new CustomerDatabaseTestDouble();
    final GeneralPaymentsSystem myPS= new PaymentSystemTestDouble();
    PaymentHandlerDouble myPaymentHandler = new PaymentHandlerDouble(new CalculationStrategyOne(), PaymentsSystemAdapter.getInstance());

    final OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");

    OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
    OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
    OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);



    @Test
    public void chargeAccountsForTwoCustomersWithZeroTrips() {

        TravelTracker travelTracker = new TravelTracker(mockCustomerDB, myPaymentHandler, myClock);
        List<JourneyEvent> eventLog = new ArrayList<>();
        List<Customer> myCustomers= new ArrayList<Customer>();

        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        Customer eden_Hazard = new Customer("Eden Hazard", new OysterCard("00400000-8cf0-11bd-b23e-10b96e4ef00d"));

        myCustomers.add(zlatan_ibrahimovic);
        myCustomers.add(eden_Hazard);
        context.checking(new Expectations() {{

            exactly(1).of(mockCustomerDB).getCustomers(); will(returnValue(myCustomers));

//            exactly(1).of(mockPaymentHandler).charge(with(equal(zlatan_ibrahimovic)), with(aNonNull(ArrayList.class)) );
//            exactly(1).of(mockPaymentHandler).charge(with(equal(eden_Hazard)), with(aNonNull(ArrayList.class)));

        }});

        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);

        travelTracker.chargeAccounts();

        assertEquals(myPaymentHandler.getEventLog(), eventLog);
        assertEquals(myPaymentHandler.getCustomers().get(0), zlatan_ibrahimovic);
        assertEquals(myPaymentHandler.getCustomers().get(1), eden_Hazard);

    }

    @Test
    public void chargeAccountsForOneTripStartingAtPeakAndEndingAtOffPeak() {


        myClock.addTime(25200000l); //peak
        myClock.addTime(75200000l); //off peak

        tempClock.addTime(25200000l); //
        tempClock.addTime(75200000l); //peak

        TravelTracker travelTracker = new TravelTracker(mockCustomerDB, myPaymentHandler, myClock);

        JourneyEvent journeyStart= new JourneyStart(myCard.id(), paddingtonReader.id(), tempClock);
        JourneyEvent journeyEnd= new JourneyEnd(myCard.id(), bakerStreetReader.id(), tempClock);
        List<JourneyEvent> eventLog = new ArrayList<>();
        eventLog.add(journeyStart);
        eventLog.add(journeyEnd);
        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
        List<Customer> myCustomers= new ArrayList<Customer>();
        myCustomers.add(zlatan_ibrahimovic);
        context.checking(new Expectations() {{
            exactly(1).of(mockCustomerDB).isRegisteredId(myCard.id()); will(returnValue(true));

//            List<Journey> journeys = new ArrayList<Journey>();

//            Journey myJourney= new Journey(journeyStart, journeyEnd);
//            journeys.add(myJourney);
            BigDecimal customerTotal = new BigDecimal(3.20);

            exactly(1).of(mockCustomerDB).getCustomers(); will(returnValue(myCustomers));
            customerTotal= customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

//
//            exactly(1).of(mockPaymentHandler).charge(with(equal(zlatan_ibrahimovic)), with(aNonNull(ArrayList.class)));

        }});


        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);
//        travelTracker.cardScanned(myCard.id(), paddingtonReader.id());
        paddingtonReader.touch(myCard);

        bakerStreetReader.touch(myCard);





        travelTracker.chargeAccounts();

//        assertEquals(myPaymentHandler.getEventLog(), eventLog);
//        assertTrue(eventLog.equals(myPaymentHandler.getEventLog()));
        if (eventLog.size()!=travelTracker.getEventLog().size())
        {
            Assert.fail();
        }
        int index=0;
        for (JourneyEvent myEvent : eventLog)
        {

            assertEquals(myEvent.cardId(), myPaymentHandler.getEventLog().get(index).cardId());
            assertEquals(myEvent.readerId(), myPaymentHandler.getEventLog().get(index).readerId());
            index++;
        }
        assertEquals(myPaymentHandler.getCustomers().get(0), zlatan_ibrahimovic);




//        UUID CARD_ID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
//        UUID START_READER_ID = UUID.randomUUID();
//        UUID END_READER_ID = UUID.randomUUID();

    }



    @Test
    public void connect() {

//        TravelTracker travelTracker = new TravelTracker();
//        travelTracker.chargeAccounts();

    }

    @Test
    public void cardScanned() {


    }

}


