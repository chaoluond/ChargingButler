package com.chaocompany.chargebutler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chao on 9/28/2015.
 */
public class ChargeStationList {

    ArrayList<ChargeStation> stationList = null;

    public ChargeStationList(JSONArray listJson) throws JSONException {
        stationList = new ArrayList<ChargeStation>();

        for (int n = 0; n < listJson.length(); n++) {
            JSONObject temp =listJson.getJSONObject(n);
            ChargeStation tempStation = new ChargeStation(temp);
            stationList.add(tempStation);

        }


    }


}
