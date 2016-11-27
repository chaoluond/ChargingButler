package com.chaocompany.chargebutler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Chao on 10/19/2015.
 */
public class MyProfileActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_settings);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();



    }


    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener{


        SharedPreferences sharedPref;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.my_profile);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            setSummaryValue(sharedPref);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            setSummaryValue(sharedPref);

        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("vehicle")) {
                ListPreference pref = (ListPreference) findPreference(key);
                pref.setSummary(pref.getEntry().toString());
            }
            /*else if (key.equals("name")) {
                System.out.println("name");
                Preference pref = findPreference(key);
                pref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals("email")){
                System.out.println("email");
                Preference pref = findPreference(key);
                pref.setSummary(sharedPreferences.getString(key, ""));
            }*/
        }

        public void setSummaryValue(SharedPreferences sharedPref) {
            Preference prefName = findPreference("name");
            Preference prefEmail = findPreference("email");
            prefName.setSummary(sharedPref.getString("name", ""));
            prefEmail.setSummary(sharedPref.getString("email", ""));

        }
    }
}
