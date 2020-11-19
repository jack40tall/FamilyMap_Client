package com.example.familymap.jsmall3.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymap.jsmall3.R;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.model.Event;
import com.example.familymap.jsmall3.model.Person;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonActivity extends AppCompatActivity {

    public static final String PERSON = "selectedPerson";

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView genderTextView;

    private Person currPerson;

    private ExpandableListView expandableListView;

    DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        String currPersonID = myIntent.getStringExtra(PERSON);

//        Initialize Widget Pointers
        firstNameTextView = (TextView)findViewById(R.id.person_firstName);
        lastNameTextView = (TextView)findViewById(R.id.person_lastName);
        genderTextView = (TextView)findViewById(R.id.person_gender);
        expandableListView = findViewById(R.id.person_expandableListView);


        populatePersonData(currPersonID);

        List<Event> lifeEvents = dataCache.getPersonEventsList(currPerson.getPersonID());
        List<Person> currPersonFamily = dataCache.getFamily(currPerson);

        expandableListView.setAdapter(new ExpandableListAdapter(lifeEvents, currPersonFamily));
    }

    private void populatePersonData(String currPersonID) {
        currPerson = dataCache.getAssociatedPerson(currPersonID);

        firstNameTextView.setText(currPerson.getFirstName());
        lastNameTextView.setText(currPerson.getLastName());
        if(currPerson.getGender().equals("m")) {
            genderTextView.setText("Male");
        }
        else {
            genderTextView.setText("Female");
        }
    }


    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> lifeEvents;
        private final List<Person> currPersonFamily;

        ExpandableListAdapter(List<Event> lifeEvents, List<Person> currPersonFamily) {
            this.lifeEvents = lifeEvents;
            this.currPersonFamily = currPersonFamily;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    if(lifeEvents == null) {
                        return 0;
                    }
                    else {
                        return lifeEvents.size();
                    }
                case FAMILY_GROUP_POSITION:
                    if(currPersonFamily == null) {
                        return 0;
                    }
                    else {
                        return currPersonFamily.size();
                    }
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return getString(R.string.group1);
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.group2);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return lifeEvents.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return currPersonFamily.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.list_parent);

            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.group1);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.group2);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_list_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventItemView, final int childPosition) {

            ImageView eventIconImageView = eventItemView.findViewById(R.id.person_eventMarkerIcon);

            Drawable icon;
            icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.color_0).sizeDp(10);
            eventIconImageView.setImageDrawable(icon);


            TextView eventInfoTextView = eventItemView.findViewById(R.id.person_eventInformation);
            String eventInformationText = createEventInformation(lifeEvents.get(childPosition));
            eventInfoTextView.setText(eventInformationText);

            TextView eventPersonNameTextView = eventItemView.findViewById(R.id.person_eventPersonName);
            String name = currPerson.getFirstName() + " " + currPerson.getLastName();
            eventPersonNameTextView.setText(name);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(PersonActivity.this, getString(R.string.good_job, lifeEvents.get(childPosition).getEventType()), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    intent.putExtra(EventActivity.EVENT, lifeEvents.get(childPosition).getEventID());
                    startActivity(intent);
                }
            });
        }

        private void initializePersonView(View personItemView, final int childPosition) {

            ImageView genderIconImageView = personItemView.findViewById(R.id.person_genderMarkerIcon);
            TextView relativeNameTextView = personItemView.findViewById(R.id.person_relativeName);
            TextView relativeTitleTextView = personItemView.findViewById(R.id.person_relativeTitle);
            Drawable icon;

            Person tempPerson = currPersonFamily.get(childPosition);

            if(tempPerson.getGender().equals("m")) {
                icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(10);
            }
            else {
                icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(10);
            }


            genderIconImageView.setImageDrawable(icon);

            String name = tempPerson.getFirstName() + " " + tempPerson.getLastName();
            relativeNameTextView.setText(name);

            relativeTitleTextView.setText(tempPerson.getRelationship());

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON, currPersonFamily.get(childPosition).getPersonID());
                    startActivity(intent);
//                    Toast.makeText(PersonActivity.this, getString(R.string.good_person, currPersonFamily.get(childPosition).getFirstName()), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private String createEventInformation(Event currEvent) {
            String eventInfo = currEvent.getEventType().toUpperCase() + ": " +
                    currEvent.getCity() + ", " + currEvent.getCountry() + " (" + currEvent.getYear() + ")";
            return eventInfo;
        }
    }
}
