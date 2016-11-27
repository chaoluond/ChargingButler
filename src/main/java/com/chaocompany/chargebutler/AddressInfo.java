package com.chaocompany.chargebutler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Chao on 9/27/2015.
 */
public class AddressInfo implements Serializable{

    public String addressTitle = null;

    public String addressLine1 = null;

    public String addressLine2 = null;

    public String town = null;

    public String stateOrProvince = null;

    public String postCode = null;

    public String countryCode = null;

    public AddressInfo(JSONObject addinfoJson) throws JSONException {

        addressTitle = addinfoJson.getString("Title");
        addressLine1 = addinfoJson.getString("AddressLine1");
        addressLine2 = addinfoJson.getString("AddressLine2");
        town = addinfoJson.getString("Town");
        stateOrProvince = addinfoJson.getString("StateOrProvince");
        postCode = addinfoJson.getString("Postcode");
        countryCode = addinfoJson.getJSONObject("Country").getString("ISOCode");

    }


}
