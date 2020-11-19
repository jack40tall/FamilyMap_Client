package com.example.familymap.jsmall3.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.lang.reflect.Array;
import java.util.*;

public class DataCache {
    private static DataCache _instance = new DataCache();
    public static boolean loginSuccess = false;
    public static DataCache getInstance() {
        return _instance;
    }

    public static GoogleMap map;
    public static List<Marker> markers;
    public static List<Polyline> lines;


    //    Key = ID, value = person/event
    private String userID;
    private Map<String, Person> people = new HashMap<>();
    private List<Event> allEvents = new ArrayList<>();
    private List<Person> allPeople = new ArrayList<>();
    private Map<String, Event> events = new HashMap<>();
    private Map<String, List<Event>> personEvents = new HashMap<>();
    private Settings settings = new Settings();
    private Set<String> eventTypes = new TreeSet<>();
    private Map<String, Integer> eventTypeColors = new HashMap<>();
    private Person user;
    private Set<String> paternalAncestors = new HashSet<>();
    private Set<String> maternalAncestors = new HashSet<>();
    private Map<String, List<Person>> personChildren = new HashMap<>();

    private DataCache() { }

    public void clear() {
        people.clear();
        events.clear();
        personEvents.clear();
        eventTypes.clear();
        settings.reset();
        eventTypeColors.clear();
        allEvents.clear();
        allPeople.clear();
        paternalAncestors.clear();
        maternalAncestors.clear();
        personChildren.clear();
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public List<Event> getAllEvents() {
        List<Event> filteredEvents = new ArrayList<>();
        for(Event temp : allEvents) {
//            User and spouse filter
            if((user.getPersonID().equals(temp.getPersonID())) &&
                    ((getAssociatedPerson(temp.getPersonID()).getGender().equals("m") && settings.isMaleEventsFilter()) ||
                    (getAssociatedPerson(temp.getPersonID()).getGender().equals("f") && settings.isFemaleEventsFilter()))) {

                filteredEvents.add(temp);

            }
            if(user.getSpouseID() != null) {
                if ((user.getSpouseID().equals(temp.getPersonID())) &&
                        ((getAssociatedPerson(temp.getPersonID()).getGender().equals("m") && settings.isMaleEventsFilter()) ||
                                (getAssociatedPerson(temp.getPersonID()).getGender().equals("f") && settings.isFemaleEventsFilter()))) {

                    filteredEvents.add(temp);
                }
            }
//            gender filter and side filter
            if(((getAssociatedPerson(temp.getPersonID()).getGender().equals("m") && settings.isMaleEventsFilter()) ||
                    (getAssociatedPerson(temp.getPersonID()).getGender().equals("f") && settings.isFemaleEventsFilter())) &&
                    ((paternalAncestors.contains(temp.getPersonID()) && settings.isFathersSideFilter()) ||
                            (maternalAncestors.contains(temp.getPersonID()) && settings.isMothersSideFilter()))) {


                filteredEvents.add(temp);
            }
        }



        return filteredEvents;
    }

    public List<Person> getAllPeople() {
        return allPeople;
    }

    /**
     * Adds person to PersonMap
     */
    private void addPerson(Person person) {
        people.put(person.getPersonID(), person);
        allPeople.add(person);
    }

    /**
     * Adds event to eventMap, and adds event to correct personEvents list
     */
    public void addEvent(Event currEvent) {
        events.put(currEvent.getEventID(), currEvent);
        eventTypes.add(currEvent.getEventType());
        allEvents.add(currEvent);

    }

    public void addAllEvents(AllEvents allEvents) {
        for (int i = 0; i < allEvents.getData().length; ++i) {
            Event currEvent = allEvents.getData()[i];
            addEvent(currEvent);
            matchEventToPerson(currEvent);
        }
        determineEventColors();
        sortAllEventsByYear();
    }

    /**
     * Adds the received events to eventTypes
     */
    public void matchEventToPerson(Event currEvent) {
        if(personEvents.containsKey(currEvent.getPersonID())) {
            List<Event> tempList = personEvents.get(currEvent.getPersonID());
            tempList.add(currEvent);
            personEvents.remove(currEvent.getPersonID());
            personEvents.put(currEvent.getPersonID(), tempList);
        }
        else {
            List<Event> tempList = new ArrayList<>();
            tempList.add(currEvent);
            personEvents.put(currEvent.getPersonID(), tempList);
        }
    }

    public void sortAllEventsByYear() {
        for (Map.Entry<String,List<Event>> entry : personEvents.entrySet()) {
            List<Event> events = entry.getValue();
            Collections.sort(events, new SortByDate());
        }
    }

    public void addFamilyTree(AllPeople userFam) {
        for (int i = 0; i < userFam.getData().length; ++i) {
            addPerson(userFam.getData()[i]);
        }
        addUser();
        determinePaternalAndMaternalAncestors();
    }

    private void determinePaternalAndMaternalAncestors() {
        if(user.getFatherID() != null) {
//            children
            determineChildren(user.getFatherID(), user);

            Person firstPaternalRelative = getAssociatedPerson(user.getFatherID());
            paternalAncestors.add(firstPaternalRelative.getPersonID());
            determinePaternalAncestors(getAssociatedPerson(user.getFatherID()));
        }
        if(user.getMotherID() != null) {
            determineChildren(user.getMotherID(), user);

            Person firstMaternalRelative = getAssociatedPerson(user.getMotherID());
            maternalAncestors.add(firstMaternalRelative.getPersonID());
            determineMaternalAncestors(getAssociatedPerson(user.getMotherID()));
        }
    }

    private void determineChildren(String parentID, Person currPerson) {
        if(personChildren.containsKey(parentID)) {
            List<Person> children = personChildren.get(parentID);
            children.add(currPerson);
        }
        else{
            List<Person> children = new ArrayList<>();
            children.add(currPerson);
            personChildren.put(parentID, children);
        }
    }

    private void determinePaternalAncestors(Person currPerson) {
        if(currPerson.getFatherID() != null) {
            determineChildren(currPerson.getFatherID(), currPerson);

            Person nextPaternalRelative = getAssociatedPerson(currPerson.getFatherID());
            paternalAncestors.add(nextPaternalRelative.getPersonID());
            determinePaternalAncestors(nextPaternalRelative);
        }
        if(currPerson.getMotherID() != null) {
            determineChildren(currPerson.getMotherID(), currPerson);

            Person nextPaternalRelative = getAssociatedPerson(currPerson.getMotherID());
            paternalAncestors.add(nextPaternalRelative.getPersonID());
            determinePaternalAncestors(nextPaternalRelative);
        }
    }

    private void determineMaternalAncestors(Person currPerson) {
        if(currPerson.getFatherID() != null) {
            determineChildren(currPerson.getFatherID(), currPerson);

            Person nextMaternalRelative = getAssociatedPerson(currPerson.getFatherID());
            maternalAncestors.add(nextMaternalRelative.getPersonID());
            determineMaternalAncestors(nextMaternalRelative);
        }
        if(currPerson.getMotherID() != null) {
            determineChildren(currPerson.getMotherID(), currPerson);

            Person nextMaternalRelative = getAssociatedPerson(currPerson.getMotherID());
            maternalAncestors.add(nextMaternalRelative.getPersonID());
            determineMaternalAncestors(nextMaternalRelative);
        }
    }

    public void setUserID(String personID){
        userID = personID;
    }

    private void addUser() {
        user = people.get(userID);
    }

    public Person getUser() {
        return user;
    }

    public Map<String, List<Event>> getPersonEvents() {
        return personEvents;
    }

    public void determineEventColors() {
        int i = 0;
        for (String temp : eventTypes) {
            int colorReference = i * 60;
            eventTypeColors.put(temp, colorReference);
            ++i;
            if(i >= 6) {
                i = 0;
            }
        }
    }

    public Map<String, Event> getEvents() {
        return events;
    }


    public Map<String, Integer> getEventTypeColors() {
        return eventTypeColors;
    }

    public Person getAssociatedPerson(String personID) {
        Person foundPerson = people.get(personID);
        return foundPerson;
    }

    public Event getAssociatedEvent(String eventID) {
        Event foundEvent = events.get(eventID);
        return foundEvent;
    }

    public List<Event> getPersonEventsList(String personID) {
        List<Event> personEventsList = personEvents.get(personID);
        List<Event> eventResults = new ArrayList<>();

        for(Event temp : personEventsList) {

            //            User and spouse filter
            if((user.getPersonID().equals(temp.getPersonID())) &&
                    ((getAssociatedPerson(temp.getPersonID()).getGender().equals("m") && settings.isMaleEventsFilter()) ||
                            (getAssociatedPerson(temp.getPersonID()).getGender().equals("f") && settings.isFemaleEventsFilter()))) {

                eventResults.add(temp);

            }
            if(user.getSpouseID() != null) {
                if ((user.getSpouseID().equals(temp.getPersonID())) &&
                        ((getAssociatedPerson(temp.getPersonID()).getGender().equals("m") && settings.isMaleEventsFilter()) ||
                                (getAssociatedPerson(temp.getPersonID()).getGender().equals("f") && settings.isFemaleEventsFilter()))) {

                    eventResults.add(temp);
                }
            }
//            gender filter and side filter
            if(((getAssociatedPerson(temp.getPersonID()).getGender().equals("m") && settings.isMaleEventsFilter()) ||
                (getAssociatedPerson(temp.getPersonID()).getGender().equals("f") && settings.isFemaleEventsFilter())) &&
                    ((paternalAncestors.contains(temp.getPersonID()) && settings.isFathersSideFilter()) ||
                (maternalAncestors.contains(temp.getPersonID()) && settings.isMothersSideFilter()))) {


                eventResults.add(temp);
            }
        }


        return  eventResults;
    }

    public List<Person> getFamily(Person currPerson) {
        List<Person> family = new ArrayList<>();

        if(currPerson.getFatherID() != null) {
            Person father = getAssociatedPerson(currPerson.getFatherID());
            if(father != null) {
                father.setRelationship("Father");
                family.add(father);
            }
        }
        if(currPerson.getMotherID() != null) {
            Person mother = getAssociatedPerson(currPerson.getMotherID());
            if(mother != null) {
                mother.setRelationship("Mother");
                family.add(mother);
            }
        }
        if(currPerson.getSpouseID() != null) {
            Person spouse = getAssociatedPerson(currPerson.getSpouseID());
            if(spouse != null) {
                spouse.setRelationship("Spouse");
                family.add(spouse);
            }
        }
        if(personChildren.get(currPerson.getPersonID()) != null) {
            List<Person> children = personChildren.get(currPerson.getPersonID());

            for (Person temp : children) {
                temp.setRelationship("Child");
                family.add(temp);
            }
        }
        return family;
    }

    public Settings getSettings() {
        return settings;
    }

    class SortByDate implements Comparator<Event> {

        @Override
        public int compare(Event o1, Event o2) {
            return o1.getYear() - o2.getYear();
        }
    }
}
