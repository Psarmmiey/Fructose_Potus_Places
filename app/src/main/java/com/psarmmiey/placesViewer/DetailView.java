package com.psarmmiey.placesViewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

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

public class DetailView extends AppCompatActivity {
    private final List<Place> placeList = new ArrayList<>();

    private PlaceViewAdapter placeViewAdapter;
    private ListView placeListView; // displays place info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Hello");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPlaceTask getLocalWeatherTask = new GetPlaceTask();
                URL url = createURL(Weather.getPlaceID());
                getLocalWeatherTask.execute(url);
            }
        });

    }


    private URL createURL(String places) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);
        double lat = 1.0;
        String order = "&rankby=distance&";
        //  String location = "-33.8670522,151.1957362&";


        try {
            // create URL for specified city and imperial units (Fahrenheit)
            //  System.out.print(getmLat());
            String urlString;
            String placeId;
            urlString =
                    baseUrl + places + "&key=" + apiKey;

            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // URL was malformed
    }

    private void convertJSONtoArrayList(JSONObject forecast) {
        placeList.clear(); // clear old weather data

        try {
            // get forecast's "list" JSONArray
            JSONArray list = forecast.getJSONArray("results");
            if (forecast.getJSONArray("results") == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.read_error, Snackbar.LENGTH_LONG).show();
            }

            for (int i = 0; i < list.length(); ++i) {

                JSONObject place = list.getJSONObject(i); // get one day's data
                JSONObject north = place.getJSONObject("geometry");
                JSONObject location = north.getJSONObject("location");

                // set destination latitude and longitude
                //setFinalLat(location.getDouble("lat"));
                // setFinalLong(location.getDouble("lng"));

                ProgressBar loadingSpin = (ProgressBar) findViewById(R.id.loadingBar);
                loadingSpin.setVisibility(View.GONE);


                placeList.add(new Place(
                        place.getString("name"),
                        place.getString("formatted_address"),
                        place.getString("website"),
                        place.getJSONObject("rating").toString(),
                        place.getString("icon"),
                        place.getString("formatted_phone_number")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class GetPlaceTask extends AsyncTask<URL, Void, JSONObject> {
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
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
            } finally {
                assert connection != null;
                connection.disconnect(); // close the httpURLConnection
            }
            return null;
        }

        // process JSON response and update ListView
        //@Override
        protected void onPostExecute(JSONObject place) {

            convertJSONtoArrayList(place); // repopulate weatherList
            System.out.println(place);

            placeViewAdapter.notifyDataSetChanged(); // rebind to ListView
            // weatherListView.smoothScrollToPosition(0); // scroll to top


        }
    }



}
