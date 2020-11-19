package com.example.familymap.jsmall3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymap.jsmall3.FontAwesome.FontAwesome;
import com.example.familymap.jsmall3.R;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.model.Event;
import com.example.familymap.jsmall3.model.Person;
import com.example.familymap.jsmall3.model.Settings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;

    public static String ARG_EVENT_ID = "event-id";

    public static boolean firstTime = true;

    private ImageView iconView;
    private TextView personNameTextView;
    private TextView eventDetailsTextView;
//
    private Event selectedEvent = null;
    private DataCache dataCache;
//    private Map<Marker, Event> markersToEvents;
    private List<Marker> markers = new ArrayList<>();
    private List<Polyline> lines = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() == null) {
            setHasOptionsMenu(true);
        }
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        iconView = (ImageView)view.findViewById(R.id.mapIcon);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventInformationClicked();
            }
        });

        setGenderIcon("general");

        personNameTextView = (TextView)view.findViewById(R.id.PersonName);
        personNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventInformationClicked();
            }
        });

        eventDetailsTextView = (TextView)view.findViewById(R.id.EventInformation);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventInformationClicked();
            }
        });

//        markersToEvents = new HashMap<>();
//        selectedEvent = null;
//        lines = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        if(map != null) {
//            this.map = DataCache.map;
//            this.markers = DataCache.markers;
//            this.lines = DataCache.lines;
            addAllEventLines(map);

            addAllEventMarkers(map);
        }
        firstTime = false;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        new MenuInflater(getActivity()).inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_search:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eventInformationClicked() {
        if(selectedEvent != null) {

//            String tempMessage = "You clicked Events";
//            Toast toast = Toast.makeText(getActivity(), tempMessage,
//                    Toast.LENGTH_LONG);
//
//            toast.show();

            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra(PersonActivity.PERSON, selectedEvent.getPersonID());
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        DataCache.map = this.map;
        map.setOnMapLoadedCallback(this);
        map.setOnMarkerClickListener(markerClickListener);

//        Get DataCache and add all event markers
        LatLng startingPos = addAllEventMarkers(map);


        if(getArguments() != null) {
            selectedEvent = dataCache.getAssociatedEvent(getArguments().getString(ARG_EVENT_ID));
            startingPos = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            populateInformation();
        }
        // Add a marker in Sydney and move the camera
        if(getArguments() != null) {
            addAllEventLines(map);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPos, 5F));
        }
        else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPos, 2F));
        }
    }

    private void addAllEventLines(GoogleMap map) {
        if(selectedEvent != null) {
            Settings settings = dataCache.getSettings();
            clearPolylines();

//        Spouse Lines
            if (settings.isSpouseLines()) {
                addSpouseLine(map);
            }

            if (settings.isFamilyTreeLines()) {
                addFamilyTreeLines(map);
            }

            if (settings.isLifeStoryLines()) {
                addLifeStoryLines(map);
            }
        }
    }

    private void addLifeStoryLines(GoogleMap map) {
        Person eventPerson = dataCache.getAssociatedPerson(selectedEvent.getPersonID());
        List<Event> selectedPersonLifeEvents = dataCache.getPersonEventsList(eventPerson.getPersonID());
//        if(!selectedPersonLifeEvents.isEmpty()) {
            for(int i = 0; i < selectedPersonLifeEvents.size() - 1; ++i) {
                Event currEvent = selectedPersonLifeEvents.get(i);
                Event nextEvent = selectedPersonLifeEvents.get(i + 1);
                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                new LatLng(nextEvent.getLatitude(), nextEvent.getLongitude()))
                                .width(5).color(Color.BLUE);
                Polyline newLine = map.addPolyline(line);
                lines.add(newLine);
            }
//        }
    }

    private void addFamilyTreeLines(GoogleMap map) {
        Person eventPerson = dataCache.getAssociatedPerson(selectedEvent.getPersonID());

//        Set Father's side
        if(eventPerson.getFatherID() != null) {
            Person father = dataCache.getAssociatedPerson(eventPerson.getFatherID());
            List<Event> fatherLifeEvents = dataCache.getPersonEventsList(father.getPersonID());
            if(!fatherLifeEvents.isEmpty()) {
                Event earliestFatherEvent = fatherLifeEvents.get(0);
                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()),
                                new LatLng(earliestFatherEvent.getLatitude(), earliestFatherEvent.getLongitude()))
                                .width(5).color(Color.RED);
                Polyline newLine = map.addPolyline(line);
                lines.add(newLine);
                addFamilyTreeLinesHelper(map, earliestFatherEvent, 4);
            }
        }

//        Set Mothers's side
        if(eventPerson.getMotherID() != null) {
            Person mother = dataCache.getAssociatedPerson(eventPerson.getMotherID());
            List<Event> motherLifeEvents = dataCache.getPersonEventsList(mother.getPersonID());
            if(!motherLifeEvents.isEmpty()) {
                Event earliestMotherEvent = motherLifeEvents.get(0);
                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()),
                                new LatLng(earliestMotherEvent.getLatitude(), earliestMotherEvent.getLongitude()))
                                .width(10).color(Color.RED);
                Polyline newLine = map.addPolyline(line);
                lines.add(newLine);
                addFamilyTreeLinesHelper(map, earliestMotherEvent, 8);
            }
        }
    }

    private void addFamilyTreeLinesHelper(GoogleMap map, Event currEvent, int width) {

        Person eventPerson = dataCache.getAssociatedPerson(currEvent.getPersonID());
        //        Set Father's side
        if(eventPerson.getFatherID() != null) {
            Person father = dataCache.getAssociatedPerson(eventPerson.getFatherID());
            List<Event> fatherLifeEvents = dataCache.getPersonEventsList(father.getPersonID());
            if(!fatherLifeEvents.isEmpty()) {
                Event earliestFatherEvent = fatherLifeEvents.get(0);
                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                new LatLng(earliestFatherEvent.getLatitude(), earliestFatherEvent.getLongitude()))
                                .width(width).color(Color.RED);
                Polyline newLine = map.addPolyline(line);
                lines.add(newLine);
                if(width <= 0) {
                    addFamilyTreeLinesHelper(map, earliestFatherEvent, 0);
                }
                else {
                    addFamilyTreeLinesHelper(map, earliestFatherEvent, (width - 2));
                }
            }
        }

//        Set Mothers's side
        if(eventPerson.getMotherID() != null) {
            Person mother = dataCache.getAssociatedPerson(eventPerson.getMotherID());
            List<Event> motherLifeEvents = dataCache.getPersonEventsList(mother.getPersonID());
            if(!motherLifeEvents.isEmpty()) {
                Event earliestMotherEvent = motherLifeEvents.get(0);
                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()),
                                new LatLng(earliestMotherEvent.getLatitude(), earliestMotherEvent.getLongitude()))
                                .width(width).color(Color.RED);
                Polyline newLine = map.addPolyline(line);
                lines.add(newLine);

                if(width <= 0) {
                    addFamilyTreeLinesHelper(map, earliestMotherEvent, 0);
                }
                else {
                    addFamilyTreeLinesHelper(map, earliestMotherEvent, (width - 2));
                }
            }
        }
    }

    private void clearPolylines() {
        for(Polyline line : lines) {
            line.remove();
        }
        lines.clear();
    }

    private void addSpouseLine(GoogleMap map) {

        Person eventPerson = dataCache.getAssociatedPerson(selectedEvent.getPersonID());
        if(eventPerson.getSpouseID() != null) {
            Person spouse = dataCache.getAssociatedPerson(eventPerson.getSpouseID());
            List<Event> spouseLifeEvents = dataCache.getPersonEventsList(spouse.getPersonID());
            if(!spouseLifeEvents.isEmpty()) {
                Event earliestSpouseEvent = spouseLifeEvents.get(0);
                PolylineOptions line =
                        new PolylineOptions().add(new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()),
                                new LatLng(earliestSpouseEvent.getLatitude(), earliestSpouseEvent.getLongitude()))
                                .width(5).color(Color.MAGENTA);
                Polyline newLine = map.addPolyline(line);
                lines.add(newLine);
            }
        }
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }


    private LatLng addAllEventMarkers(GoogleMap map) {
        clearMarkers();

        dataCache = DataCache.getInstance();
        Map<String, Integer> colorMap = dataCache.getEventTypeColors();
        int currColor;
        LatLng pos = null;

        List<Event> allFilteredEvents = dataCache.getAllEvents();

        for (Event currEvent : allFilteredEvents) {
            pos = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());

            currColor = colorMap.get(currEvent.getEventType());

            Marker currMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(currColor)).position(pos));
            currMarker.setTag(currEvent);
            markers.add(currMarker);
//            May need this one with a title
//            map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(currColor)).position(pos).title());
        }
        return pos;


//        pos


    }

    private void clearMarkers() {
        for(Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            selectedEvent = (Event) marker.getTag();
            LatLng currEventPos = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(currEventPos));


            populateInformation();

            addAllEventLines(map);

            addAllEventMarkers(map);

//            populateMap(false);
            return true;
        }
    };

    private void populateInformation() {

        dataCache = DataCache.getInstance();
        Person associatedPerson = dataCache.getAssociatedPerson(selectedEvent.getPersonID());

        String personName = associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
        personNameTextView.setText(personName);

        String eventInfo = createEventInformation();

        eventDetailsTextView.setText(eventInfo);

        if(associatedPerson.getGender().equals("m")) {
            setGenderIcon("male");
        }
        else{
            setGenderIcon("female");
        }
    }

    private String createEventInformation() {
        String eventInfo = selectedEvent.getEventType().toUpperCase() + ": " +
                selectedEvent.getCity() + ", " + selectedEvent.getCountry() + " (" + selectedEvent.getYear() + ")";
        return eventInfo;
    }

//    private View.OnClickListener eventInfoClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//        }
//    }

    private void setGenderIcon(String iconType) {
    Drawable icon;

    if(iconType.equals("general")) {
        icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_user).
                colorRes(R.color.android_icon).sizeDp(30);
    }
    else if(iconType.equals("male")) {
        icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                colorRes(R.color.male_icon).sizeDp(30);
    }
    else if(iconType.equals("female")) {
        icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                colorRes(R.color.female_icon).sizeDp(30);
    }
    else {
        icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_apple).
                colorRes(R.color.male_icon).sizeDp(30);
    }

    iconView.setImageDrawable(icon);
}




}
