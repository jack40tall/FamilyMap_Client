package com.example.familymap.jsmall3.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymap.jsmall3.R;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.model.Event;
import com.example.familymap.jsmall3.model.Person;
import com.example.familymap.jsmall3.model.Search;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        MenuItem ourSearchItem = menu.findItem(R.id.menu_item_search);
//
//        SearchView sv = (SearchView) ourSearchItem.getActionView();

        searchView = (SearchView)findViewById(R.id.search_searchText);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                Search search = new Search();
                List<Person> peopleResults = search.searchForPeople(query);
                List<Event> eventResults = search.searchForEvents(query);

                RecyclerView recyclerView = findViewById(R.id.search_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

                SearchAdapter adapter = new SearchAdapter(peopleResults, eventResults);
                recyclerView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });




    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> peopleResults;
        private final List<Event> eventResults;

        SearchAdapter(List<Person> peopleResults, List<Event> eventResults) {
            this.peopleResults = peopleResults;
            this.eventResults = eventResults;
        }

        @Override
        public int getItemViewType(int position) {
            return position < peopleResults.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.search_person, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < peopleResults.size()) {
                holder.bind(peopleResults.get(position));
            } else {
                holder.bind(eventResults.get(position - peopleResults.size()));
            }
        }

        @Override
        public int getItemCount() {
            return peopleResults.size() + eventResults.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView genderIconImageView;
        private final TextView personNameTextView;

        private final ImageView eventIconImageView;
        private final TextView eventInformationTextView;
        private final TextView eventPersonTextView;

        private DataCache dataCache = DataCache.getInstance();

        private final int viewType;
        private Person currPerson;
        private Event currEvent;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                genderIconImageView = itemView.findViewById(R.id.search_genderMarkerIcon);
                personNameTextView = itemView.findViewById(R.id.search_name);
                eventIconImageView = null;
                eventInformationTextView = null;
                eventPersonTextView = null;
            } else {
                genderIconImageView = null;
                personNameTextView = null;
                eventIconImageView = itemView.findViewById(R.id.person_eventMarkerIcon);
                eventInformationTextView = itemView.findViewById(R.id.person_eventInformation);
                eventPersonTextView = itemView.findViewById(R.id.person_eventPersonName);
            }
        }

        private void bind(Person currPerson) {
            this.currPerson = currPerson;

            Drawable icon;
            if(currPerson.getGender().equals("m")) {
                icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(10);
            }
            else {
                icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(10);
            }
            genderIconImageView.setImageDrawable(icon);

            String name = currPerson.getFirstName() + " " + currPerson.getLastName();
            personNameTextView.setText(name);
        }

        private void bind(Event currEvent) {
            this.currEvent = currEvent;

            Drawable icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.color_0).sizeDp(10);
            eventIconImageView.setImageDrawable(icon);

            String eventInfo = createEventInformation(currEvent);
            eventInformationTextView.setText(eventInfo);

            Person eventPerson = dataCache.getAssociatedPerson(currEvent.getPersonID());
            String eventPersonName = eventPerson.getFirstName() + " " + eventPerson.getLastName();
            eventPersonTextView.setText(eventPersonName);
        }

        private String createEventInformation(Event currEvent) {
            String eventInfo = currEvent.getEventType().toUpperCase() + ": " +
                    currEvent.getCity() + ", " + currEvent.getCountry() + " (" + currEvent.getYear() + ")";
            return eventInfo;
        }


        @Override
        public void onClick(View view) {
            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON, currPerson.getPersonID());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.putExtra(EventActivity.EVENT, currEvent.getEventID());
                startActivity(intent);
            }
        }
//        private void initializeEventView(View eventItemView, final int childPosition) {
//
//            ImageView eventIconImageView = eventItemView.findViewById(R.id.person_eventMarkerIcon);
//
//            Drawable icon;
//            icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
//                    colorRes(R.color.color_0).sizeDp(10);
//            eventIconImageView.setImageDrawable(icon);
//
//
//            TextView eventInfoTextView = eventItemView.findViewById(R.id.person_eventInformation);
//            String eventInformationText = createEventInformation(lifeEvents.get(childPosition));
//            eventInfoTextView.setText(eventInformationText);
//
//            TextView eventPersonNameTextView = eventItemView.findViewById(R.id.person_eventPersonName);
//            String name = currPerson.getFirstName() + " " + currPerson.getLastName();
//            eventPersonNameTextView.setText(name);
//
//            eventItemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Toast.makeText(PersonActivity.this, getString(R.string.good_job, lifeEvents.get(childPosition).getEventType()), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
//                    intent.putExtra(EventActivity.EVENT, lifeEvents.get(childPosition).getEventID());
//                    startActivity(intent);
//                }
//            });
//        }
//
//        private void initializePersonView(View personItemView, final int childPosition) {
//
//            ImageView genderIconImageView = personItemView.findViewById(R.id.person_genderMarkerIcon);
//            TextView relativeNameTextView = personItemView.findViewById(R.id.person_relativeName);
//            TextView relativeTitleTextView = personItemView.findViewById(R.id.person_relativeTitle);
//            Drawable icon;
//
//            Person tempPerson = currPersonFamily.get(childPosition);
//
//            if(tempPerson.getGender().equals("m")) {
//                icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).
//                        colorRes(R.color.male_icon).sizeDp(10);
//            }
//            else {
//                icon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).
//                        colorRes(R.color.female_icon).sizeDp(10);
//            }
//
//
//            genderIconImageView.setImageDrawable(icon);
//
//            String name = tempPerson.getFirstName() + " " + tempPerson.getLastName();
//            relativeNameTextView.setText(name);
//
//            relativeTitleTextView.setText(tempPerson.getRelationship());
//
//            personItemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
//                    intent.putExtra(PersonActivity.PERSON, currPersonFamily.get(childPosition).getPersonID());
//                    startActivity(intent);
////                    Toast.makeText(PersonActivity.this, getString(R.string.good_person, currPersonFamily.get(childPosition).getFirstName()), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }
}
