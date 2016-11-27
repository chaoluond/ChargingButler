package com.chaocompany.chargebutler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chao on 9/27/2015.
 */
public class ChargeStation implements Serializable {

    public String UUID = null;

    public String Latitude = null;

    public String Longitude = null;

    public AddressInfo addInfo = null;

    public String contactPhone = null;

    public String contactEmail = null;

    public String connectionType = null;

    public String chargeLevel = null;

    public double score;

    public double distance2MyLocation;

    public ArrayList<InterestPlace> supermarkets;

    public ArrayList<InterestPlace> coffeeshop;

    public ArrayList<InterestPlace> shoppingmall;

    public ArrayList<InterestPlace> cinema;

    public ArrayList<InterestPlace> bar;

    public ArrayList<InterestPlace> restaurant;


    public ChargeStation(JSONObject stationJSONObject) throws JSONException {

        UUID = stationJSONObject.get("UUID").toString();
        JSONObject addinfoJson = stationJSONObject.getJSONObject("AddressInfo");
        Latitude = addinfoJson.get("Latitude").toString();
        Longitude = addinfoJson.get("Longitude").toString();
        addInfo = new AddressInfo(addinfoJson);
        contactPhone = addinfoJson.get("ContactTelephone1").toString();
        contactEmail = addinfoJson.get("ContactEmail").toString();

        JSONObject connectionJson = stationJSONObject.getJSONArray("Connections").getJSONObject(0);
        connectionType = connectionJson.getJSONObject("ConnectionType").get("Title").toString();
        chargeLevel = connectionJson.getJSONObject("Level").get("Title").toString();


        score = 0;
        distance2MyLocation = 0;
        supermarkets = new ArrayList<InterestPlace>();
        coffeeshop = new ArrayList<InterestPlace>();
        shoppingmall = new ArrayList<InterestPlace>();
        cinema = new ArrayList<InterestPlace>();
        bar = new ArrayList<InterestPlace>();
        restaurant = new ArrayList<InterestPlace>();


    }







}
