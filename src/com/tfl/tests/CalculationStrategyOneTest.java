package com.tfl.tests;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.*;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.hamcrest.core.IsNot;

public class CalculationStrategyOneTest {

    ClockTestDouble myClock= new ClockTestDouble();

    final OysterCard zlatan_Card = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    final OysterCard hazard_Card = new OysterCard("10000000-8cf0-11bd-b23e-10b96e4ef00d");

    OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
    OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
    OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);

    List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();

    @Test
    public void chargeASpecficCustomerForZeroTrips(){
        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));

        List<Journey> myJourneys = new ArrayList<>();

        BigDecimal customerTotal = new BigDecimal(0);
        customerTotal= customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

        CalculationStrategyOne testingStrategyOne = new CalculationStrategyOne();

        assertThat(testingStrategyOne.totalJourneysFor(zlatan_ibrahimovic, eventLog), is(customerTotal));
        assertThat(testingStrategyOne.getJourneysForCustomer(), is(myJourneys));


    }

//    @Test
//    public void chargeASpecficCustomerForOneTripStartingAtPeakAndEndingAtOffPeak() {
//
//        myClock.addTime(25200000l); //peak
//        myClock.addTime(75200000l); //off peak
//        myClock.addTime(25200000l); //peak
//        myClock.addTime(75200000l); //off peak
//
//        Customer zlatan_ibrahimovic = new Customer("Zlatan Ibrahimovic", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
//        Customer eden_hazard = new Customer("eden_hazard", new OysterCard("10000000-8cf0-11bd-b23e-10b96e4ef00d"));
//
//        List<JourneyEvent> EventLog = new ArrayList<JourneyEvent>();
//        List<Journey> zlatan_Journeys = new ArrayList<Journey>();
//        List<Journey> hazard_Journeys = new ArrayList<Journey>();
//
//
//
//        JourneyEvent zlatan_journeyStart= new JourneyStart(zlatan_Card.id(), paddingtonReader.id(), myClock);
//        JourneyEvent zlatan_journeyEnd= new JourneyEnd(zlatan_Card.id(), bakerStreetReader.id(), myClock);
//        Journey OneJourneyForZlatan= new Journey(zlatan_journeyStart, zlatan_journeyEnd);
//
//        EventLog.add(zlatan_journeyStart);
//        EventLog.add(zlatan_journeyEnd);
//        zlatan_Journeys.add(OneJourneyForZlatan);
//
//        JourneyEvent hazard_journeyStart= new JourneyStart(hazard_Card.id(), paddingtonReader.id(), myClock);
//        JourneyEvent hazard_journeyEnd= new JourneyEnd(hazard_Card.id(), bakerStreetReader.id(), myClock);
//        Journey OneJorneyForHazard= new Journey(hazard_journeyStart, hazard_journeyEnd);
//
//
//        EventLog.add(hazard_journeyStart);
//        EventLog.add(hazard_journeyEnd);
//        hazard_Journeys.add(OneJorneyForHazard);
//
//
//
//        BigDecimal customerTotal = new BigDecimal(3.20);
//        customerTotal= customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
//
//        CalculationStrategyOne testingStrategyOne = new CalculationStrategyOne(EventLog);
//
//        assertThat(testingStrategyOne.totalJourneysFor(zlatan_ibrahimovic), is(customerTotal));
//        assertThat(testingStrategyOne.getJourneysForCustomer(), equalTo(OneJourneyForZlatan));
//        assertThat(testingStrategyOne.getJourneysForCustomer(), is(not(OneJorneyForHazard)));
//
//
//    }
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