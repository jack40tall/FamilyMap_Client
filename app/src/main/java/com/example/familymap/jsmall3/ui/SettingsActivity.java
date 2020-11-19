package com.example.familymap.jsmall3.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.familymap.jsmall3.R;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.model.Settings;

public class SettingsActivity extends AppCompatActivity {

    Switch lifeStoryLinesSwitch;
    Switch familyTreeLinesSwitch;
    Switch spouseLinesSwitch;
    Switch fathersSideSwitch;
    Switch mothersSideSwitch;
    Switch maleEventsSwitch;
    Switch femaleEventsSwitch;
    LinearLayout logoutLinearLayout;

    DataCache dataCache = DataCache.getInstance();
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = dataCache.getSettings();

        lifeStoryLinesSwitch = (Switch) findViewById(R.id.settings_lifeStoryLinesSwitch);
        lifeStoryLinesSwitch.setChecked(settings.isLifeStoryLines());
        lifeStoryLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setLifeStoryLines(isChecked);
            }
        });

        familyTreeLinesSwitch = (Switch) findViewById(R.id.settings_familyTreeLinesSwitch);
        familyTreeLinesSwitch.setChecked(settings.isFamilyTreeLines());
        familyTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setFamilyTreeLines(isChecked);
            }
        });

        spouseLinesSwitch = (Switch) findViewById(R.id.settings_spouseLinesSwitch);
        spouseLinesSwitch.setChecked(settings.isSpouseLines());
        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setSpouseLines(isChecked);
            }
        });

        fathersSideSwitch = (Switch) findViewById(R.id.settings_fathersSideSwitch);
        fathersSideSwitch.setChecked(settings.isFathersSideFilter());
        fathersSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setFathersSideFilter(isChecked);
            }
        });

        mothersSideSwitch = (Switch) findViewById(R.id.settings_mothersSideSwitch);
        mothersSideSwitch.setChecked(settings.isMothersSideFilter());
        mothersSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setMothersSideFilter(isChecked);
            }
        });

        maleEventsSwitch = (Switch) findViewById(R.id.settings_maleEventsSwitch);
        maleEventsSwitch.setChecked(settings.isMaleEventsFilter());
        maleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setMaleEventsFilter(isChecked);
            }
        });

        femaleEventsSwitch = (Switch) findViewById(R.id.settings_femaleEventsSwitch);
        femaleEventsSwitch.setChecked(settings.isFemaleEventsFilter());
        femaleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setFemaleEventsFilter(isChecked);
            }
        });

        logoutLinearLayout = (LinearLayout) findViewById(R.id.logoutButton);
        logoutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void logoutUser() {
        DataCache dataCache = DataCache.getInstance();
        dataCache.clear();
        MainActivity.LOGGED_IN = false;
        MapFragment.firstTime = true;
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra(PersonActivity.PERSON, currPersonFamily.get(childPosition).getPersonID());
        startActivity(intent);
        finish();
    }
}