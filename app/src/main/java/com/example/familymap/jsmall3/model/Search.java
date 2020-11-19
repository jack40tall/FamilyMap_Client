package com.example.familymap.jsmall3.model;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private DataCache dataCache = DataCache.getInstance();

    List<Person> peopleResults;
    List<Event> eventResults;

    public List<Person> searchForPeople(String q) {
        String query = q.toLowerCase();
        peopleResults = new ArrayList<>();
        List<Person> allPeople = dataCache.getAllPeople();

        for (Person temp : allPeople) {
            if(temp.getFirstName().toLowerCase().contains(query) ||
                temp.getLastName().toLowerCase().contains(query)) {

                peopleResults.add(temp);
            }
        }
        return peopleResults;
    }

    public List<Event> searchForEvents(String q) {
        String query = q.toLowerCase();
        eventResults = new ArrayList<>();
        List<Event> allEvents = dataCache.getAllEvents();

        for (Event temp : allEvents) {
            if(temp.getEventType().toLowerCase().contains(query) ||
                    Integer.toString(temp.getYear()).contains(query) ||
                    temp.getCountry().toLowerCase().contains(query) ||
                    temp.getCity().toLowerCase().contains(query)) {

                eventResults.add(temp);
            }
        }
        return eventResults;
    }
}
