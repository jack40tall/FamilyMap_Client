package com.example.familymap.jsmall3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.familymap.jsmall3.R;

public class EventActivity extends AppCompatActivity {

    public static final String EVENT = "selectedEvent";

    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent myIntent = getIntent();
        String currEventID = myIntent.getStringExtra(EVENT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FragmentManager fm = this.getSupportFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.eventActivity_fragmentContainer);
        if (mapFragment == null) {
            mapFragment = new MapFragment();

//            Send eventID to mapFragment
            Bundle bundle = new Bundle();
            bundle.putString(MapFragment.ARG_EVENT_ID, currEventID);
            mapFragment.setArguments(bundle);

            fm.beginTransaction()
                    .add(R.id.eventActivity_fragmentContainer, mapFragment)
                    .commit();
        }
    }
}