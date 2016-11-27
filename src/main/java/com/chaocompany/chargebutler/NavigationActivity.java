package com.chaocompany.chargebutler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private final double BUFFER = 20; // in kilometer

    // Semi-axes of WGS-84 geoidal reference
    private final double WGS84_a = 6378137.0; // Major semiaxis [m]
    private final double WGS84_b = 6356752.3; // Minor semiaxis [m]


    private GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteViewFrom;

    private AutoCompleteTextView mAutocompleteViewTo;

    private TextView mPlaceDetailsText;

    private TextView mPlaceDetailsAttribution;

    private Location mCurrentLocation;

    private String placeIdFrom = null;

    private String placeIdTo = null;

    private String latOrigin;

    private String logOrigin;

    private String latDestination;

    private String logDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mGoogleApiClient.connect();

        mCurrentLocation = (Location) getIntent().getExtras().getParcelable("CurLocation");

        LatLngBounds bounds = setLatLngBound(mCurrentLocation);

        setContentView(R.layout.activity_navigation);


        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteViewFrom = (AutoCompleteTextView) findViewById(R.id.navigation_from);
        mAutocompleteViewTo = (AutoCompleteTextView) findViewById(R.id.navigation_to);


        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteViewFrom.setOnItemClickListener(new MyAutocompleteClickListener(mAutocompleteViewFrom));
        mAutocompleteViewTo.setOnItemClickListener(new MyAutocompleteClickListener((mAutocompleteViewTo)));


        // Retrieve the TextViews that will display details and attributions of the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);



        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, bounds, null);
        mAutocompleteViewFrom.setAdapter(mAdapter);
        mAutocompleteViewTo.setAdapter(mAdapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_route:
                prepareRoute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    /**
     *
     */

    private void prepareRoute() {
        String str = "";
        boolean isValid = true;


        if (placeIdFrom == null && !mAutocompleteViewFrom.getText().toString().equals("Current Location")) {
            str += "Please enter a valid origin.";
            isValid = false;
        }
        if (placeIdTo == null) {
            str += " Please enter a valid destination";
            isValid = false;
        }

        if (!isValid) {

            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        }
        else {

            if (mAutocompleteViewFrom.getText().toString().equals("Current Location")) {
                latOrigin = Double.toString(mCurrentLocation.getLatitude());
                logOrigin = Double.toString(mCurrentLocation.getLongitude());
            }

            String path = "http://maps.google.com/maps?saddr=" + latOrigin + "," + logOrigin + "&" + "daddr=" +
                    latDestination + "," + logDestination;
            Uri gmmIntentUri = Uri.parse(path);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        }
    }



    /**
     * Set the latitude and longitude bound according to the current location
     * @param mCurrentLocation
     * @return
     */


    private LatLngBounds setLatLngBound(Location mCurrentLocation) {
        double latitude = mCurrentLocation.getLatitude();
        double longitude = mCurrentLocation.getLongitude();

        double latRadi = Math.PI * latitude / 180.0;
        double longRadi = Math.PI * longitude / 180.0;

        double halfLength = BUFFER * 1000;

        // Radius of Earth at given latitude
        double radius = WGS84EarthRadius(latRadi);
        // Radius of the parallel at given latitude
        double pradius = radius * Math.cos(latRadi);

        double latRadiMin = (latRadi - halfLength/radius) * 180.0 / Math.PI;
        double latRadiMax = (latRadi + halfLength/radius) * 180.0 / Math.PI;
        double lonRadiMin = (longRadi - halfLength/pradius) * 180.0 / Math.PI;
        double lonRadiMax = (longRadi + halfLength/pradius) * 180.0 / Math.PI;


        return new LatLngBounds(new LatLng(latRadiMin, lonRadiMin), new LatLng(latRadiMax, lonRadiMax));




    }

    private double WGS84EarthRadius(double lat)
    {
        // http://en.wikipedia.org/wiki/Earth_radius
        double An = WGS84_a * WGS84_a * Math.cos(lat);
        double Bn = WGS84_b * WGS84_b * Math.sin(lat);
        double Ad = WGS84_a * Math.cos(lat);
        double Bd = WGS84_b * Math.sin(lat);
        return Math.sqrt((An*An + Bn*Bn) / (Ad*Ad + Bd*Bd));
    }




    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */

    class MyAutocompleteClickListener implements AdapterView.OnItemClickListener {
        private AutoCompleteTextView v;

        public MyAutocompleteClickListener(AutoCompleteTextView v) {
            this.v = v;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);


            if (v == mAutocompleteViewFrom) {
                placeIdFrom = placeId;
            }
            else {
                placeIdTo = placeId;
            }




            //Log.i("TAG", "Autocomplete item selected: " + item.description);


            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new mUpdatePlaceDetailsCallback(v));


            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            //Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);

        }
    }


    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */

    class mUpdatePlaceDetailsCallback implements  ResultCallback<PlaceBuffer> {
        private AutoCompleteTextView v;

        public mUpdatePlaceDetailsCallback(AutoCompleteTextView v) {

            this.v = v;

        }


        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                // Log.e("TAG", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            if (v == mAutocompleteViewFrom) {
                latOrigin = Double.toString(place.getLatLng().latitude);
                logOrigin = Double.toString(place.getLatLng().longitude);

            } else {
                latDestination = Double.toString(place.getLatLng().latitude);
                logDestination = Double.toString(place.getLatLng().longitude);
            }

            // Format details of the place for display and show it in a TextView.
            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            //Log.i("TAG", "Place details received: " + place.getName());

            places.release();


        }
    }



    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        //Log.e("TAG", res.getString(R.string.place_details, name, id, address, phoneNumber,
        // websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("TAG", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }










}
