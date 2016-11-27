package com.chaocompany.chargebutler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Chao on 10/22/2015.
 */
public class InterestPlace implements Serializable {

    public String name = null;

    public String Latitude = null;

    public String Longitude = null;

    public String distance2center = null;

    public InterestPlace(JSONObject placeJSONObject) throws JSONException {
        name = placeJSONObject.getString("name");

        Latitude = placeJSONObject.getJSONObject("geometry").getJSONObject("location").getString("lat");

        Longitude = placeJSONObject.getJSONObject("geometry").getJSONObject("location").getString("lng");

        distance2center = "0"; // To do list


    }
}
