package com.example.familymap.jsmall3;

import com.example.familymap.jsmall3.model.AllEvents;
import com.example.familymap.jsmall3.model.AllPeople;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.model.Event;
import com.example.familymap.jsmall3.model.Person;
import com.example.familymap.jsmall3.model.Search;
import com.example.familymap.jsmall3.model.Settings;
import com.example.familymap.jsmall3.net.ServerProxy;
import com.example.familymap.jsmall3.net.requests.LoginOrRegisterRequest;
import com.example.familymap.jsmall3.net.results.LoginOrRegisterResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ModelTest {

    ServerProxy serverProxy;
    DataCache dataCache;
    LoginOrRegisterRequest loginRequest;
    LoginOrRegisterResult loginResult;
    Settings settings;
    Search search;

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    @Before
    public void setUp() throws Exception {
        dataCache = DataCache.getInstance();
        dataCache.clear();
        serverProxy = new ServerProxy();
        serverProxy.serverHostName = "localhost";
        serverProxy.serverPortNumber = 8081;
        loginRequest = new LoginOrRegisterRequest("sheila", "parker");
        loginResult = serverProxy.login(loginRequest);
        AllPeople allPeople = serverProxy.getAllPeople();
        AllEvents allEvents = serverProxy.getAllEvents();
        dataCache.setUserID(loginResult.getPersonID());
        dataCache.addFamilyTree(allPeople);
        dataCache.addAllEvents(allEvents);
        settings = dataCache.getSettings();
        settings.reset();
        search = new Search();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getFamilyRelationshipPass() {

        List<Person> family =  dataCache.getFamily(dataCache.getUser());
        Person father = family.get(0);
        Person mother = family.get(1);
        Person spouse = family.get(2);

        assertEquals(father.getRelationship(), "Father");
        assertEquals(mother.getRelationship(), "Mother");
        assertEquals(spouse.getRelationship(), "Spouse");

    }

    @Test
    public void getFamilyRelationshipPassChildren() {

//        Test if father's children and family show up
        Person userFather = dataCache.getAssociatedPerson(dataCache.getUser().getFatherID());
        List<Person> family =  dataCache.getFamily(userFather);
        Person father = family.get(0);
        Person mother = family.get(1);
        Person spouse = family.get(2);
        Person Sheila = family.get(3);

        assertEquals(father.getRelationship(), "Father");
        assertEquals(mother.getRelationship(), "Mother");
        assertEquals(spouse.getRelationship(), "Spouse");
        assertEquals(Sheila.getRelationship(), "Child");

    }

    @Test
    public void filterMalePass() {
        List<Event> allFilteredEvents = dataCache.getAllEvents();
        int maleEventsPresent = 0;

        for(Event nextEvent : allFilteredEvents) {
            if(dataCache.getAssociatedPerson(nextEvent.getPersonID()).getGender().equals("m")) {
                maleEventsPresent++;
            }
        }
        assertTrue(maleEventsPresent > 0);

//        Test male events filter turned off
        settings.setMaleEventsFilter(false);
        allFilteredEvents = dataCache.getAllEvents();
        maleEventsPresent = 0;

        for(Event nextEvent : allFilteredEvents) {
            if(dataCache.getAssociatedPerson(nextEvent.getPersonID()).getGender().equals("m")) {
                maleEventsPresent++;
            }
        }
        assertTrue(maleEventsPresent == 0);
    }

    @Test
    public void filterFemalePass() {
        List<Event> allFilteredEvents = dataCache.getAllEvents();
        int femaleEventsPresent = 0;

        for(Event nextEvent : allFilteredEvents) {
            if(dataCache.getAssociatedPerson(nextEvent.getPersonID()).getGender().equals("f")) {
                femaleEventsPresent++;
            }
        }
        assertTrue(femaleEventsPresent > 0);

//        Test female events filter turned off
        settings.setFemaleEventsFilter(false);

        allFilteredEvents = dataCache.getAllEvents();
        femaleEventsPresent = 0;

        for(Event nextEvent : allFilteredEvents) {
            if(dataCache.getAssociatedPerson(nextEvent.getPersonID()).getGender().equals("f")) {
                femaleEventsPresent++;
            }
        }
        assertTrue(femaleEventsPresent == 0);
    }

//    @Test
//    public void filterSideOfFamilyPass() {
//        List<Event> allFilteredEvents = dataCache.getAllEvents();
//        int fathersSideEventsPresent = 0;
//        int mothersSideEventsPresent = 0;
//
//        for(Event nextEvent : allFilteredEvents) {
//            Person eventPerson = dataCache.getAssociatedPerson(nextEvent.getPersonID());
//
//            if(dataCache.getPaternalAncestors().contains(eventPerson)) {
//                fathersSideEventsPresent++;
//            }
//            if(dataCache.getMaternalAncestors().contains(eventPerson)) {
//                mothersSideEventsPresent++;
//            }
//        }
//        assertTrue(fathersSideEventsPresent > 0);
//        assertTrue(mothersSideEventsPresent > 0);
//
////        Test mothers side events filter turned off
//        settings.setMothersSideFilter(false);
//        allFilteredEvents = dataCache.getAllEvents();
//        fathersSideEventsPresent = 0;
//        mothersSideEventsPresent = 0;
//
//        for(Event nextEvent : allFilteredEvents) {
//            Person eventPerson = dataCache.getAssociatedPerson(nextEvent.getPersonID());
//            if(dataCache.getPaternalAncestors().contains(eventPerson)) {
//                fathersSideEventsPresent++;
//            }
//            if(dataCache.getMaternalAncestors().contains(eventPerson)) {
//                mothersSideEventsPresent++;
//            }
//        }
//        assertTrue(mothersSideEventsPresent == 0);
//        assertTrue(fathersSideEventsPresent > 0);
//
////        Test fathers side events filter turned off
//        settings.setMothersSideFilter(true);
//        settings.setFathersSideFilter(false);
//
//        allFilteredEvents = dataCache.getAllEvents();
//        fathersSideEventsPresent = 0;
//        mothersSideEventsPresent = 0;
//
//        for(Event nextEvent : allFilteredEvents) {
//            Person eventPerson = dataCache.getAssociatedPerson(nextEvent.getPersonID());
//            if(dataCache.getPaternalAncestors().contains(eventPerson)) {
//                fathersSideEventsPresent++;
//            }
//            if(dataCache.getMaternalAncestors().contains(eventPerson)) {
//                mothersSideEventsPresent++;
//            }
//        }
//        assertTrue(fathersSideEventsPresent == 0);
//        assertTrue(mothersSideEventsPresent > 0);
//
//    }

    @Test
    public void sortEventsPass() {
        Event earliestEvent = new Event("sheila", "earliest1", "Sheila_Parker", 80.0000000000000f, -70.0000000000000f, "Denmark", "China", "Earliest Event", 1975);
        Event middleEvent = new Event("sheila", "middle1", "Sheila_Parker", 80.0000000000000f, -70.0000000000000f, "Denmark", "China", "Middle Event", 2000);
        Event latestEvent = new Event("sheila", "latest1", "Sheila_Parker", 80.0000000000000f, -70.0000000000000f, "Denmark", "China", "Latest Event", 2006);

        dataCache.addEvent(middleEvent);
        dataCache.matchEventToPerson(middleEvent);

        dataCache.addEvent(earliestEvent);
        dataCache.matchEventToPerson(earliestEvent);

        dataCache.addEvent(latestEvent);
        dataCache.matchEventToPerson(latestEvent);

        dataCache.sortAllEventsByYear();

        List<Event> personEvents = dataCache.getPersonEventsList("Sheila_Parker");

        assertEquals(personEvents.get(0).getEventType(), "birth");              //1970
        assertEquals(personEvents.get(1).getEventType(), "Earliest Event");     //1975
        assertEquals(personEvents.get(2).getEventType(), "Middle Event");       //2000
        assertEquals(personEvents.get(3).getEventType(), "Latest Event");       //2006
        assertEquals(personEvents.get(4).getEventType(), "marriage");           //2012
        assertEquals(personEvents.get(5).getEventType(), "completed asteroids");//2014
        assertEquals(personEvents.get(6).getEventType(), "COMPLETED ASTEROIDS");//2014
        assertEquals(personEvents.get(7).getEventType(), "death");              //2015

    }

    @Test
    public void sortAllEventsTest() {
        Map<String, List<Event>> allEvents = dataCache.getPersonEvents();

        boolean inChronologicalOrder = true;

        for (Map.Entry<String,List<Event>> entry : allEvents.entrySet()) {
            List<Event> personEvents = entry.getValue();

            for(int i = 0; i < personEvents.size() - 1; ++i) {
                Event currEvent = personEvents.get(i);
                Event nextEvent = personEvents.get(i + 1);

                if(currEvent.getYear() > nextEvent.getYear()) {
                    inChronologicalOrder = false;
                }
            }
        }

        assertTrue(inChronologicalOrder);
    }

    @Test
    public void searchPass() {
//        Returned People
        List<Person> peopleResults = search.searchForPeople("Sheila");
        assertEquals(peopleResults.get(0).getPersonID(), "Sheila_Parker");

//        Returned Events
        List<Event> eventResults = search.searchForEvents("United States");
        assertEquals(eventResults.get(0).getEventType(), "marriage");
        assertEquals(eventResults.get(1).getEventType(), "Graduated from BYU");



    }

    @Test
    public void searchInvalidQueryPass() {
        //        Returned People
        List<Person> peopleResults = search.searchForPeople("xyz");
        assertTrue(peopleResults.isEmpty());

//        Returned Events
        List<Event> eventResults = search.searchForEvents("xyz");
        assertTrue(eventResults.isEmpty());
    }

}
