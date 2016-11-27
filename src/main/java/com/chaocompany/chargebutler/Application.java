package com.chaocompany.chargebutler;


import android.preference.PreferenceManager;

import com.parse.Parse;

/**
 * Created by Chao on 9/13/2015.
 */
public class Application extends android.app.Application{


    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Connect to Parse
        Parse.initialize(this, "tm0qeLPgr39b4Y8y6O9DkCOHt1OF7ndkw1QIKVyE", "4CMr5vw2e9hEliKFZRiuC1Kgrqczii7bQhBpXHN6");
        PreferenceManager.setDefaultValues(this, R.xml.my_profile, false);
    }


}
