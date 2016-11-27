package com.chaocompany.chargebutler;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.connection.Connections;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, OnMapReadyCallback {


    private String[] mPlanetTitles = new String[]{"My Profile", "Preference", "Logout"};

    private String[] placeOfInterest = new String[]{"supermarket", "coffee+shop", "shopping+mall",
            "cinema", "bar", "restaurant"};

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private CharSequence mTitle;

    private ActionBarDrawerToggle mDrawerToggle;

    private MapFragment mapFragment;

    private GoogleMap mGoogleMap;

    private GoogleApiClient mGoogleApiClient;

    private Location mCurrentLocation;

    private boolean mRequestingLocationUpdates = true;

    private LocationRequest mLocationRequest;

    private ChargeOpenMapConnector mMapConnector;

    private ChargeStationList mchargeStationList;

    int ICONS[] = {R.drawable.ic_launcher,R.drawable.ic_launcher, R.drawable.ic_launcher};

    private String serverKey = "AIzaSyAsJr-MwUb8L86XEHHN9C7gw1KuHG7YOqE";

    private final String httpURLBase = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    private String NAME = null;

    private String EMAIL = null;

    private String FACEBOOKID = null;

    private Bitmap PICTURE = null;

    private RetrieveFacebookProfile mRetrieveFacebookProfile;




    private RecyclerView mRecyclerView;                           // Declaring RecyclerView

    private RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View

    private RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager

    private DrawerLayout Drawer;

    private SlidingUpPanelLayout slidingLayout;

    private ImageButton btnShow;

    private Button btnHide;

    private Button btnExpand;

    private ListView stationListView;

    private String[] stringArray ;

    private ArrayAdapter stationItemArrayAdapter;

    private TextView resultIndicatorView;


    /*****************************************************************
     * Create the current activity and some initialization
     * @param savedInstanceState
     *****************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        // facebook stuff
        FacebookSdk.sdkInitialize(getApplicationContext());


        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            NAME = response.getJSONObject().getString("name");
                            EMAIL = response.getJSONObject().getString("email");
                            FACEBOOKID = response.getJSONObject().getString("id");

                            URL img_value = null;
                            img_value = new URL("https://graph.facebook.com/"+ FACEBOOKID +"/picture?type=large");
                            mRetrieveFacebookProfile = new RetrieveFacebookProfile();
                            mRetrieveFacebookProfile.execute(img_value);


                            // set sharedpreference value
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("name", NAME);
                            editor.putString("email", EMAIL);
                            editor.commit();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();



        btnShow = (ImageButton)findViewById(R.id.buttonShow);

        btnHide = (Button)findViewById(R.id.slideHide);

        btnExpand = (Button)findViewById(R.id.slideExpand);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        //some "demo" event
        //slidingLayout.setPanelSlideListener(onSlideListener());

        btnHide.setOnClickListener(onHideListener());

        btnShow.setOnClickListener(onShowListener());

        btnExpand.setOnClickListener(onExpandListener());




        Drawer = (DrawerLayout) findViewById(R.id.drawer_layout);        // Drawer object Assigned to the view

        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, R.drawable.ic_drawer,
                R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        };

        // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        // Connect to Google Location API
        mGoogleApiClient = buildGoogleApiClient();
        mGoogleApiClient.connect();



        // Get the handler of the fragment
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragmentShowMap);

        mapFragment.getMapAsync(this);


        // Show my location button click handler
        ImageButton btnMyLocation = (ImageButton) findViewById(R.id.buttonMyLocation);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCurrentLocation();
            }
        });
    }



    /**
     * Request show sliding layout when clicked
     * @return
     */
    private View.OnClickListener onShowListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show sliding layout in bottom of screen (not expand it)
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                btnShow.setVisibility(View.GONE);
            }
        };
    }



    /**
     * Hide sliding layout when click button
     * @return
     */
    private View.OnClickListener onHideListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide sliding layout
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                btnShow.setVisibility(View.VISIBLE);
            }
        };
    }


    /**
     * Request slide up the panel when clicked
     * @return
     */
    private View.OnClickListener onExpandListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Expand the panel
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        };
    }


    /*private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                //textView.setText("panel is sliding");
            }

            @Override
            public void onPanelCollapsed(View view) {
                //textView.setText("panel Collapse");
            }

            @Override
            public void onPanelExpanded(View view) {
                //textView.setText("panel expand");
            }

            @Override
            public void onPanelAnchored(View view) {
                //textView.setText("panel anchored");
            }

            @Override
            public void onPanelHidden(View view) {
                //textView.setText("panel is Hidden");
            }
        };
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }



    /*****************************************************************************
     * onPostCreate() method
     * @param savedInstanceState
     */

    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }*/

    /***********************************************************************************
     * onPause() method Pause the activity
     ***********************************************************************************/
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        mGoogleApiClient.disconnect();
    }




    /***********************************************************************************
     * onResume() method Resume the activity
     **********************************************************************************/
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    protected void startLocationUpdates() {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }






    /***********************************************************************************
     * Connect to Google Location API and connect
     **********************************************************************************/

    protected synchronized GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }






    /***************************************************************************************************************
     * googleMap rendering
     ***************************************************************************************************************/
    // Get Google map and display the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap; // Later on we can update camarer using mGoogleMap

        mGoogleMap.setMyLocationEnabled(true);


        // Get the UiSettings of the map
        UiSettings mapUI = mGoogleMap.getUiSettings();

        // Set the UIs
        mapUI.setZoomControlsEnabled(false);
        mapUI.setCompassEnabled(true);
        mapUI.setMyLocationButtonEnabled(false);

        // set map type
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



    }

    // Display the current location and place a marker
    private void displayCurrentLocation(){
        if (mCurrentLocation != null) {
            // Get latitude of the current location
            double latitude = mCurrentLocation.getLatitude();

            // Get longitude of the current location
            double longitude = mCurrentLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);



            // Show the current location in Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

            mMapConnector = new ChargeOpenMapConnector();
            mMapConnector.execute(latLng);



        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(100000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        displayCurrentLocation();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }



    /*********************************************************************************************
     * Show maps menu
     ********************************************************************************************/
    public void showMapMenu(View view){
        //ActionBar actionBar = getSupportActionBar();
        PopupMenu popupMenu=new PopupMenu(this,view);
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        MapMenuEventHandler mapMenuEventHandler=new MapMenuEventHandler(getApplicationContext(), mGoogleMap);
        popupMenu.setOnMenuItemClickListener(mapMenuEventHandler);
        menuInflater.inflate(R.menu.menu_maps, popupMenu.getMenu());
        popupMenu.show();
    }



    public void prepareNavigation(View view) {
        // Starts an intent for the navigation preparation activity
        Intent navigateIntent = new Intent(this, NavigationActivity.class);
        navigateIntent.putExtra("CurLocation", mCurrentLocation);
        startActivity(navigateIntent);
    }


    class RetrieveFacebookProfile extends AsyncTask<URL, Void, Bitmap> {


        Bitmap figure;
        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL mURL = urls[0];
            try {
                figure = BitmapFactory.decodeStream(mURL.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return figure;





        }

        @Override
        protected void onPostExecute (Bitmap figure) {

            PICTURE = figure;

            /* Assinging the toolbar object ot the view and setting the the Action bar to our toolbar*/

            mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

            mAdapter = new MyAdapter(mPlanetTitles, ICONS, NAME, EMAIL, PICTURE);
            // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
            // And passing the titles,icons,header view name, header view email,
            // and header view profile picture


            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

            mLayoutManager = new LinearLayoutManager(getBaseContext());                 // Creating a layout Manager

            mRecyclerView.setLayoutManager(mLayoutManager);


        }
    }


    class ChargeOpenMapConnector extends AsyncTask<LatLng, Void, ChargeStationList> {

        public String result;
        public ChargeStationList stationList = null;
        public double mLocationLat = 0;
        public double mLocationLng = 0;
        public double radius = 16; // kilometers
        SharedPreferences sharedPref;
        public ChargeOpenMapConnector() {}

        @Override
        protected ChargeStationList doInBackground(LatLng... latlngs){
            try {
                mLocationLat = latlngs[0].latitude;
                mLocationLng = latlngs[0].longitude;
                readJSON(latlngs[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                stationList = populateChargeStation();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Calculate the score for each charging station candidate
            for (ChargeStation station : stationList.stationList) {
                String lat = station.Latitude;
                String log = station.Longitude;



                for (String place : placeOfInterest) {

                    String urlPath = httpURLBase + "location=" + lat + "," + log + "&radius=200&sensor=true" + "&keyword=" + place +
                            "&key=" + serverKey;

                    JSONObject placeResult = null;
                    BufferedReader reader = null;
                    JSONArray placeResultArray = null;


                    try {

                        URL url = new URL(urlPath);
                        reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuffer buffer = new StringBuffer();
                        int read;
                        char[] chars = new char[1024];
                        while ((read = reader.read(chars)) != -1)
                            buffer.append(chars, 0, read);


                        placeResult = new JSONObject(buffer.toString());


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        if (placeResult.getString("status").equals("OK")) {
                            placeResultArray = placeResult.getJSONArray("results");

                            switch (place) {
                                case "supermarket" :
                                {
                                    for (int num = 0; num < placeResultArray.length(); num++) {
                                        if (placeResultArray.getJSONObject(num).has("rating"))
                                            station.supermarkets.add(new InterestPlace(placeResultArray.getJSONObject(num)));
                                    }

                                    System.out.println("supermarket");
                                }
                                break;

                                case "coffee+shop" :
                                {
                                    for (int num = 0; num < placeResultArray.length(); num++) {
                                        if (placeResultArray.getJSONObject(num).has("rating"))
                                            station.coffeeshop.add(new InterestPlace(placeResultArray.getJSONObject(num)));
                                    }

                                    System.out.println("coffee shop");
                                }
                                break;

                                case "shopping+mall" :
                                {
                                    for (int num = 0; num < placeResultArray.length(); num++) {
                                        if (placeResultArray.getJSONObject(num).has("rating"))
                                            station.shoppingmall.add(new InterestPlace(placeResultArray.getJSONObject(num)));
                                    }

                                    System.out.println("shopping mall");
                                }
                                break;

                                case "cinema" :
                                {
                                    for (int num = 0; num < placeResultArray.length(); num++) {
                                        if (placeResultArray.getJSONObject(num).has("rating"))
                                            station.cinema.add(new InterestPlace(placeResultArray.getJSONObject(num)));
                                    }

                                    System.out.println("cinema");
                                }
                                break;


                                case "bar" :
                                {
                                    for (int num = 0; num < placeResultArray.length(); num++) {
                                        if (placeResultArray.getJSONObject(num).has("rating"))
                                            station.bar.add(new InterestPlace(placeResultArray.getJSONObject(num)));
                                    }

                                    System.out.println("bar");
                                }
                                break;

                                case "restaurant" :
                                {
                                    for (int num = 0; num < placeResultArray.length(); num++) {
                                        if (placeResultArray.getJSONObject(num).has("rating"))
                                            station.restaurant.add(new InterestPlace(placeResultArray.getJSONObject(num)));
                                    }

                                    System.out.println("restaurant");
                                }
                                break;

                                default:
                                    throw new IllegalArgumentException("Invalid input");


                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }


            }

            sortChargingStation(); // Calculate the score for each charge station candidate

            Collections.sort(stationList.stationList, new StationComp());

            for (ChargeStation c : stationList.stationList)
                System.out.println("score is " + c.score);

            return stationList;
        }


        @Override
        protected void onPostExecute (ChargeStationList result) { // Display markers on the map

            mchargeStationList = result;
            stringArray = new String[result.stationList.size()];
            int count = 0;
            for (ChargeStation station : result.stationList) {
                LatLng point = new LatLng(Double.parseDouble(station.Latitude), Double.parseDouble(station.Longitude));
                mGoogleMap.addMarker(new MarkerOptions().position(point));

                AddressInfo tempAdd = station.addInfo;
                stringArray[count] = "No. " + Integer.toString(count + 1) + " " + tempAdd.addressTitle + ", "
                        + tempAdd.addressLine1 + ", " + tempAdd.town + "  " +
                        String.format("%.2f", station.distance2MyLocation) + " kilometers";
                count++;

            }

            resultIndicatorView = (TextView) findViewById(R.id.textStationList);
            resultIndicatorView.setText("Charging Station List");
            stationItemArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, stringArray);
            stationListView = (ListView) findViewById(R.id.stationList);
            stationListView.setAdapter(stationItemArrayAdapter);
            stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    String item = ((TextView) view).getText().toString();

                    Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getBaseContext(), ChargeStationActivity.class);

                    intent.putExtra("chargestation", mchargeStationList.stationList.get(position));

                    intent.putExtra("myLat", Double.toString(mLocationLat));

                    intent.putExtra("myLng", Double.toString(mLocationLng));

                    startActivity(intent);



                }
            });
        }


        public void readJSON(LatLng latlng) throws Exception {
            BufferedReader reader = null;
            String latitude = Double.toString(latlng.latitude);
            String longitude = Double.toString(latlng.longitude);
            String distance = "10";


            String urlpath = "http://api.openchargemap.io/v2/poi/?output=json&countrycode=US&maxresults=10";

            urlpath += ("&latitude=" + latitude + "&longitude=" + longitude + "&distance=" + distance
                    + "&distanceunit=Miles");



            try {
                URL url = new URL(urlpath);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer buffer = new StringBuffer();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read);

                result = buffer.toString();

            } finally {
                if (reader != null)
                    reader.close();
            }
        }



        public ChargeStationList populateChargeStation() throws JSONException {
            JSONArray obj = new JSONArray(result);

            return new ChargeStationList(obj);



        }

        public void sortChargingStation() {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            for (ChargeStation station : stationList.stationList) {
                double dist = distFrom(Double.parseDouble(station.Latitude), Double.parseDouble(station.Longitude),
                        mLocationLat, mLocationLng);

                station.distance2MyLocation = dist; // in kilometers

                double tempscore = 0;
                System.out.println("value is use is " + sharedPref.getString("distance", ""));
                System.out.println("distance is " + dist + ", raidus is " + radius);
                tempscore += Double.parseDouble(sharedPref.getString("distance", "")) * (dist / radius);

                if (!station.supermarkets.isEmpty())
                    tempscore += Double.parseDouble(sharedPref.getString("supermarket", ""));

                if (!station.coffeeshop.isEmpty())
                    tempscore += Double.parseDouble(sharedPref.getString("coffeeshop", ""));

                if (!station.shoppingmall.isEmpty())
                    tempscore += Double.parseDouble(sharedPref.getString("shoppingmall", ""));

                if (!station.cinema.isEmpty())
                    tempscore += Double.parseDouble(sharedPref.getString("cinema", ""));

                if (!station.bar.isEmpty())
                    tempscore += Double.parseDouble(sharedPref.getString("bar", ""));

                if (!station.restaurant.isEmpty())
                    tempscore += Double.parseDouble(sharedPref.getString("restaurant", ""));

                station.score = tempscore;


            }

        }


        //Calculates the distance in km between two lat/long points
        public double distFrom(double lat1, double lng1, double lat2, double lng2) {
            double earthRadius = 6371; // average radius of the earth in km
            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);
            double sindLat = Math.sin(dLat / 2);
            double sindLng = Math.sin(dLng / 2);
            double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                    * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = earthRadius * c;
            System.out.println(dist);
            return dist;
        }
    }


    class StationComp implements Comparator<ChargeStation> {

        @Override
        public int compare(ChargeStation c1, ChargeStation c2) {
            if(c1.score < c2.score){
                return 1;
            } else {
                return -1;
            }
        }
    }



}








