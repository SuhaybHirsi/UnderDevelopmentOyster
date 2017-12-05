package com.tfl.tests;


import com.tfl.billing.*;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import com.oyster.OysterCard;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import static org.junit.Assert.*;
import com.oyster.*;




public class PaymentHandlerTest {


    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    GeneralPaymentsSystem mockPaymentSystem = context.mock(GeneralPaymentsSystem.class);
    PaymentStrategyInterface mockPaymentStrategy= context.mock(PaymentStrategyInterface.class);
    ClockTestDouble myClock= new ClockTestDouble();

    final OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");

    OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
    OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
    OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);

    @Test
    public void chargeASpecficCustomerForZeroTrips() {
        PaymentHandlerInterface paymentHandler = new PaymentHandler(mockPaymentStrategy, mockPaymentSystem);

        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));


        context.checking(new Expectations() {{
            List<Journey> journeys = new ArrayList<Journey>();

            BigDecimal customerTotal = new BigDecimal(0);
            customerTotal= customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

            exactly(1).of(mockPaymentStrategy).totalJourneysFor(zlatan_ibrahimovic); will(returnValue(customerTotal));
            exactly(1).of(mockPaymentStrategy).getJourneysForCustomer(); will(returnValue(journeys));

            exactly(1).of(mockPaymentSystem).charge(zlatan_ibrahimovic,journeys, customerTotal);


        }});

        paymentHandler.charge(zlatan_ibrahimovic);

    }

    @Test
    public void chargeASpecficCustomerForOneTripStartingAtPeakAndEndingAtOffPeak() {

        myClock.addTime(25200000l); //peak
        myClock.addTime(75200000l); //off peak


        PaymentHandlerInterface paymentHandler = new PaymentHandler(mockPaymentStrategy, mockPaymentSystem);

        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));


        context.checking(new Expectations() {{
            List<Journey> journeys = new ArrayList<Journey>();
            JourneyEvent journeyStart= new JourneyStart(myCard.id(), paddingtonReader.id(), myClock);
            JourneyEvent journeyEnd= new JourneyEnd(myCard.id(), bakerStreetReader.id(), myClock);
            Journey myJourney= new Journey(journeyStart, journeyEnd);
            journeys.add(myJourney);

            BigDecimal customerTotal = new BigDecimal(3.20);
            customerTotal= customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

            exactly(1).of(mockPaymentStrategy).totalJourneysFor(zlatan_ibrahimovic); will(returnValue(customerTotal));
            exactly(1).of(mockPaymentStrategy).getJourneysForCustomer(); will(returnValue(journeys));

            exactly(1).of(mockPaymentSystem).charge(zlatan_ibrahimovic,journeys, customerTotal);


        }});

        paymentHandler.charge(zlatan_ibrahimovic);

    }

    @Test
    public void chargeASpecficCustomerForOneTripStartingAtOffPeakAndEndingAtOffPeak() {

        myClock.addTime(75200000l); //peak
        myClock.addTime(95200000l); //off peak


        PaymentHandlerInterface paymentHandler = new PaymentHandler(mockPaymentStrategy, mockPaymentSystem);

        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));


        context.checking(new Expectations() {{
            List<Journey> journeys = new ArrayList<Journey>();
            JourneyEvent journeyStart= new JourneyStart(myCard.id(), paddingtonReader.id(), myClock);
            JourneyEvent journeyEnd= new JourneyEnd(myCard.id(), bakerStreetReader.id(), myClock);
            Journey myJourney= new Journey(journeyStart, journeyEnd);
            journeys.add(myJourney);

            BigDecimal customerTotal = new BigDecimal(2.40);
            customerTotal= customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

            exactly(1).of(mockPaymentStrategy).totalJourneysFor(zlatan_ibrahimovic); will(returnValue(customerTotal));
            exactly(1).of(mockPaymentStrategy).getJourneysForCustomer(); will(returnValue(journeys));

            exactly(1).of(mockPaymentSystem).charge(zlatan_ibrahimovic,journeys, customerTotal);


        }});

        paymentHandler.charge(zlatan_ibrahimovic);

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

}