package com.chaocompany.chargebutler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chao on 11/1/2015.
 */
public class ChargeStationActivity extends Activity {

    private Button getDirection;

    private TextView basicInfo;

    private ListView amenityList;

    private ChargeStation mChargeStation;

    private ArrayList<String> amenity;

    private String Latitude;

    private String Longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_charge_station);

        getDirection = (Button) findViewById(R.id.stationGetDirection);

        basicInfo = (TextView) findViewById(R.id.stationBasicInfo);

        amenityList = (ListView) findViewById(R.id.amenetyList);


        // Get the charge station information

        Intent i = getIntent();

        mChargeStation = (ChargeStation) i.getSerializableExtra("chargestation");

        Latitude = i.getStringExtra("myLat");

        Longitude = i.getStringExtra("myLng");


        // Set the direction button listener
        getDirection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String path = "http://maps.google.com/maps?saddr=" + Latitude + "," + Longitude + "&" + "daddr=" +
                        mChargeStation.Latitude + "," + mChargeStation.Longitude;
                Uri gmmIntentUri = Uri.parse(path);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        AddressInfo tempAdd = mChargeStation.addInfo;

        String information = "Name: " + tempAdd.addressTitle + "\n" + "Address: " + tempAdd.addressLine1 + "\n"
                + tempAdd.town + ", " + tempAdd.stateOrProvince + ", " + tempAdd.postCode + ".\n"
                + "Distance: " + String.format("%.2f", mChargeStation.distance2MyLocation) + "\n"
                + "Phone: " + mChargeStation.contactPhone + "\n"
                + "Email: " + mChargeStation.contactEmail;

        basicInfo.setText(information);

        amenity = new ArrayList<String>();

        if (!mChargeStation.supermarkets.isEmpty()) {
            String temp = "Nearby supermarket: \n";

            int counter = 1;
            for (InterestPlace place : mChargeStation.supermarkets) {

                temp += Integer.toString(counter) + ". " + place.name + "\n";
                counter++;
            }

            amenity.add(temp);
        }

        if (!mChargeStation.coffeeshop.isEmpty()) {
            String temp = "Nearby coffee shop: \n";

            int counter = 1;
            for (InterestPlace place : mChargeStation.coffeeshop) {

                temp += Integer.toString(counter) + ". " + place.name + "\n";
                counter++;
            }

            amenity.add(temp);
        }

        if (!mChargeStation.shoppingmall.isEmpty()) {
            String temp = "Nearby shopping mall: \n";

            int counter = 1;
            for (InterestPlace place : mChargeStation.shoppingmall) {
                temp += Integer.toString(counter) + ". " + place.name + "\n";
                counter++;
            }

            amenity.add(temp);
        }

        if (!mChargeStation.cinema.isEmpty()) {
            String temp = "Nearby cinema: \n";

            int counter = 1;
            for (InterestPlace place : mChargeStation.cinema) {
                temp += Integer.toString(counter) + ". " + place.name + "\n";
                counter++;
            }

            amenity.add(temp);
        }

        if (!mChargeStation.bar.isEmpty()) {
            String temp = "Nearby bar: \n";

            int counter = 1;
            for (InterestPlace place : mChargeStation.bar) {

                temp += Integer.toString(counter) + ". " + place.name + "\n";
                counter++;
            }


            amenity.add(temp);
        }

        if (!mChargeStation.restaurant.isEmpty()) {
            String temp = "Nearby restaurant: \n";

            int counter = 1;
            for (InterestPlace place : mChargeStation.restaurant) {

                temp += Integer.toString(counter) + ". " + place.name + "\n";
                counter++;
            }

            amenity.add(temp);
        }


        ArrayAdapter amenityItemArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, amenity);
        amenityList.setAdapter(amenityItemArrayAdapter);









    }



}
