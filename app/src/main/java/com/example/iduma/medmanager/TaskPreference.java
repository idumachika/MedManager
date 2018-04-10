package com.example.iduma.medmanager;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;

/**
 * Created by iduma on 4/8/18.
 */

public class TaskPreference extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.task_preference);

        // Set the time default to a numeric number only
        EditTextPreference timeDefault = (EditTextPreference) findPreference(getString(R.string.pref_default_time_from_now_key));
        timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance());
    }
}
