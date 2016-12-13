package com.psarmmiey.weatherviewer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // List of weather objects representing the forecast
    private List<Weather> weatherList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;

    private double mlong;
    private double mlat;
    private double finalLat;

final int PERMISSION_ACCESS_COARSE_LOCATION = 0;

    private double finalLong;

    // ArrayAdapter for binding weather objects to a ListView
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView; // displays weather info
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // autogenerated code to inflate layout and configure Toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }


        // create ArrayAdapter to bind weatherList to the weatherListView
        weatherListView = (ListView) findViewById(R.id.weatherListView);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);



        EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
        locationEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText locationEditText =
                        (EditText) findViewById(R.id.locationEditText);
                if(keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                    URL url = createURL(locationEditText.getText().toString());

                    // hide keyboard and initiate a GetWeatherTask to download
                    // weather data from OpenWeatherMap.org in a separate thread
                    if (url != null) {
                        dismissKeyboard(locationEditText);
                        GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                        getLocalWeatherTask.execute(url);
                    }
                    else {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        // configure FAB to hide keyboard and initiate web services request
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get text from locationEditText and create web service URL
                EditText locationEditText =
                        (EditText) findViewById(R.id.locationEditText);
                URL url = createURL(locationEditText.getText().toString());

                // hide keyboard and initiate a GetWeatherTask to download
                // weather data from OpenWeatherMap.org in a separate thread
                if (url != null) {
                   dismissKeyboard(locationEditText);
                   Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.loading, Snackbar.LENGTH_LONG).show();
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }

    // programmatically dismiss keyboard when user touches FAB
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onConnected(Bundle connectionHint) {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                setMlat(mLastLocation.getLatitude());
                setMlong(mLastLocation.getLongitude());
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public double getMlong() {
        return mlong;
    }

    public void setMlong(double mlong) {
        this.mlong = mlong;
    }

    public double getMlat() {
        return mlat;
    }

    public void setMlat(double mlat) {
        this.mlat = mlat;
    }

    @Override
    public void onConnectionSuspended(int i) {}

    // create openweathermap.org web service URL using city
    @Nullable
    private URL createURL(String places) {
        this.city = places;
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);
        double lat = 1.0;
        double longitude = 2.0;
        String order = "&rankby=distance&";
        String location = "-33.8670522,151.1957362&";


        try {
            // create URL for specified city and imperial units (Fahrenheit)
            System.out.print(getMlat());
            String urlString;
            urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+getMlat()+","+getMlong()+"&rankby=distance"+"&name="+places+"&key=AIzaSyA7f3V7984G9n8LggAe5xL2wuCq0874sbs";

               return new URL(urlString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null; // URL was malformed
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    // makes the REST web services call to get weather data and
    // saves the data to a local HTML file

    private class GetWeatherTask
        extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
            }
            finally {
                 connection.disconnect(); // close the httpURLConnection
            }
            return null;
        }

        // process JSON response and update ListView
        //@Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList( weather); // repopulate weatherList
            System.out.println(weather);
            weatherArrayAdapter.notifyDataSetChanged(); // rebind to ListView
            weatherListView.smoothScrollToPosition(0); // scroll to top
        }
    }


    // create Weather objects from JSONObject containing the forecast
    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear(); // clear old weather data

        try {
            // get forecast's "list" JSONAr
            // ray
            JSONArray list = forecast.getJSONArray("results");
            if (forecast.getJSONArray("results") == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.read_error, Snackbar.LENGTH_LONG).show();
            }
            //  list = setJSONArray("a") ;
            // convert each element of list to a Weather object
            for (int i = 0; i < list.length(); ++i) {
                JSONObject place = list.getJSONObject(i); // get one day's data
                JSONObject north = place.getJSONObject("geometry");
                JSONObject location =   north.getJSONObject("location");

                // set destination latitude and logitude
                setFinalLat(location.getDouble("lat"));
                setFinalLong(location.getDouble("lng"));
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.loading, Snackbar.LENGTH_LONG).show();
                weatherList.add(new Weather(
                        place.getString("name").toString(), // name of place
                        getFinalLat(), // distance between current location and destination
                        getFinalLong(),// maximum temperature-
                        calcDistance(getMlat(),getMlong(),location.getDouble("lat"), location.getDouble("lng")), // Distance
                        place.getString("vicinity").toString(), // place description
                        place.getString("icon").toString())); // icon name
                weatherListView.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Uri gmmIntentUri;
                                gmmIntentUri = Uri.parse(new StringBuilder().append("google.navigation:q=")
                                        .append(getFinalLat()).append(",").append(getFinalLong()).toString());

                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        }
                );

            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private  double calcDistance(double latA, double longA, double latB, double longB) {
        Location  locationA = new Location("Initial");
        locationA.setLatitude(latA);
        locationA.setLongitude(longA);
        Location locationB = new Location("Final");
        locationB.setLatitude(latB);
        locationB.setLongitude(longB);
        double distance = (locationA.distanceTo(locationB))/1000;
        return distance;
    }

    public double getFinalLong() {
        return finalLong;
    }

    public void setFinalLong(double finalLong) {
        this.finalLong = finalLong;
    }

    public double getFinalLat() {
        return finalLat;
    }

    public void setFinalLat(double finalLat) {
        this.finalLat = finalLat;
    }
}
