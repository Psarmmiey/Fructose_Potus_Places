package com.psarmmiey.placesViewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by psarmmiey on 11/18/16.
 */


class WeatherArrayAdapter extends ArrayAdapter<Weather> {
	// stores already downloaded Bitmaps for reuse
	private final Map<String, Bitmap> bitmaps = new HashMap<>();
	//View popUpView;
	//private final Map<String, TextView>placeDetail = new HashMap();

	// constructor to initialize inherited member
	WeatherArrayAdapter(Context context, List<Weather> forecast) {
		super(context, -1, forecast);
	}

	// creates the custom views for the ListView's items
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		// get weather object for this specified ListView position
		final Weather day = getItem(position);
		final Place place = null;

		final ViewHolder viewHolder; // object that reference's list item's views

		// check for reusable ViewHolder from a ListView item that scrolled
		// offscreen; otherwise, create a new ViewHolder
		if (convertView == null) { // no reusable ViewHolder, so create one
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView =
					inflater.inflate(R.layout.list_item, parent, false);
			viewHolder.initialCard =
					(CardView) convertView.findViewById(R.id.initialCard);
			viewHolder.conditionImageView =
					(ImageView) convertView.findViewById(R.id.conditionImageView);
			viewHolder.dayTextView =
					(TextView) convertView.findViewById(R.id.dayTextView);
			viewHolder.listOptionButton =
					(Button) convertView.findViewById(R.id.listOptionButton);
			viewHolder.lowTextView =
					(TextView) convertView.findViewById(R.id.lowTextView);
			viewHolder.hiTextView =
					(TextView) convertView.findViewById(R.id.hiTextView);
			viewHolder.humidityTextView =
					(TextView) convertView.findViewById(R.id.humidityTextView);
			viewHolder.subGrid =
					(GridLayout) convertView.findViewById(R.id.subGrid);
			viewHolder.detailsCard =
					(CardView) convertView.findViewById(R.id.detailCard);
			viewHolder.innerLoadingSpinner =
					(ProgressBar) convertView.findViewById(R.id.innerLoadingSpinner);
			viewHolder.placeID =
					(TextView) convertView.findViewById(R.id.placeIDTextView);
			viewHolder.website =
					(TextView) convertView.findViewById(R.id.website); 
			convertView.setTag(viewHolder);


		} else { // reuse existing ViewHolder stored as the list item's tag
			viewHolder = (ViewHolder) convertView.getTag();
		}


		// if weather condition icon already downloaded, use it;
		// otherwise, download icon in a separate thread
		// download and display weather condition image
		assert day != null;
		if (bitmaps.containsKey(day.iconURL)) {
			viewHolder.conditionImageView.setImageBitmap(
					bitmaps.get(day.iconURL)
			);
		} else new LoadImageTask(viewHolder.conditionImageView).execute(
				day.iconURL);

		// get other data from Weather object and place into views
		final Context context = getContext(); // for loading String resources
		viewHolder.dayTextView.setText(context.getString(
				R.string.day_description, day.nameOfPlace, day.description));
		viewHolder.detailsCard.setVisibility(View.GONE);

		viewHolder.listOptionButton.setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(final View view) {

				switch (view.getId()) {
                    case R.id.listOptionButton:
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.getMenuInflater().inflate(R.menu.popup_list_option, popup.getMenu());
                        popup.show();

						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem menuItem) {
								switch (menuItem.getItemId()) {
									case R.id.navigate:
										Uri gmmIntentUri = Uri.parse("google.navigation:q=" + day.lat + "," + day.lng);
										Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
										mapIntent.setPackage("com.google.android.apps.maps");
										context.startActivity(mapIntent);

										break;
									case R.id.view:
										/*// viewHolder.conditionImageView.setVisibility(View.GONE);
										URL url = createURL(day.placeID);


										//innerloadingSpin.setVisibility(View.GONE);
										GetPlaceDetail getLocalWeatherTask = new GetPlaceDetail();
										getLocalWeatherTask.execute(url);
										if (viewHolder.website.getText()!= "TextView") {
											viewHolder.subGrid.setVisibility(View.VISIBLE);

										}
										else {
											viewHolder.innerLoadingSpinner.setVisibility(View.VISIBLE);
										}

										// viewHolder.detailsCard.setVisibility(View.VISIBLE);
										// context.startActivity(Intent.makeMainActivity(R.layout.activity_detail_view));
										//Intent in = new Intent(context, DetailView.class);

										// context.startActivity(in);
										//  ((MainActivity) context).setContentView(R.layout.content_detail_view);*/

									default:
										break;
								}

								return true;
							}
						});

						break;

					default:
						break;
				}
				/**/
			}
		});
		viewHolder.lowTextView.setText(
			context.getString(R.string.latitude, day.lat));
		viewHolder.hiTextView.setText(
			context.getString(R.string.longitude, day.lng));
		viewHolder.humidityTextView.setText(
				context.getString(R.string.humidity, day.humidity));
		viewHolder.placeID.setText(day.placeID);
		viewHolder.website.setText(Place.getWebsite());
		return convertView; // return completed list item to display
	}


	@Nullable
	private URL createURL(String placeID) {

		double lat = 1.0;
		String base = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
		try {
			String urlString;
			urlString = base + placeID + "&key=AIzaSyA7f3V7984G9n8LggAe5xL2wuCq0874sbs";
			return new URL(urlString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null; // URL was malformed
	}

	// class fpr reusing views as list items scroll off and onto the screen
	private static class ViewHolder {
		ImageView conditionImageView;
		TextView dayTextView;
		TextView lowTextView;
		TextView hiTextView;
		TextView humidityTextView;
		TextView placeID;
		Button listOptionButton;
		TextView website;
		RatingBar placeRating;
		CardView detailsCard;
		CardView initialCard;
		GridLayout subGrid;
		ProgressBar innerLoadingSpinner;
	}

	// AsyncTask to load weather condition icons in a separate thread
	private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
		private final ImageView imageView; // displays the thumbnail

		// store ImageView on which to set the download Bitmap
		LoadImageTask(ImageView imageView) {
			this.imageView = imageView;
		}

		// load image; params[0] is the String URL representing the image
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			HttpURLConnection connection = null;

			try {
				URL url = new URL(params[0]); // create URL for image

				// open an HttpURLConnection, get its InputStream
				// and download the image
				connection = (HttpURLConnection) url.openConnection();

				try (InputStream inputStream = connection.getInputStream()) {
					bitmap = BitmapFactory.decodeStream(inputStream);
					bitmaps.put(params[0], bitmap); // cache for later use
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				assert connection != null;
				connection.disconnect(); // close the HttpURLConnection
			}

			return bitmap;
		}
	}

	private class GetPlaceDetail extends AsyncTask<URL, Void, JSONObject> {

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
						/*
							Snackbar.make(findViewById(R.id.coordinatorLayout),
							R.string.read_error, Snackbar.LENGTH_LONG).show();
							*/
						e.printStackTrace();
					}
					return new JSONObject(builder.toString());
				} else {
					//Snackbar.make(findViewById(R.id.coordinatorLayout),
					//R.string.connect_error, Snackbar.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				/*
						Snackbar.make(findViewById(R.id.coordinatorLayout),
						R.string.connect_error, Snackbar.LENGTH_LONG).show();
						*/
			} finally {
				assert connection != null;
				connection.disconnect(); // close the httpURLConnection
			}
			return null;
		}

		// process JSON response and update ListView
		//@Override
		protected void onPostExecute(JSONObject weather) {

			convertJSONtoArrayList(weather); // repopulate weatherList
			// System.out.println(weather);

			//weatherArrayAdapter.notifyDataSetChanged(); // rebind to ListView
			//weatherListView.smoothScrollToPosition(0); // scroll to top


		}

		private void convertJSONtoArrayList(JSONObject forecast) {
			//weatherList.clear(); // clear old weather data

			try {
				// get forecast's "list" JSONArray
				JSONArray list = forecast.getJSONArray("result");


				for(int i = 0; i < list.length(); ++ i) {

					JSONObject place = list.getJSONObject(i); // get one day's data
					//JSONObject north = place.getJSONObject("geometry");
					//JSONObject location = north.getJSONObject("location");

					// set destination latitude and longitude
					//setFinalLat(location.getDouble("lat"));
					//setFinalLong(location.getDouble("lng"));

					//ProgressBar loadingSpin = (ProgressBar) findViewById(R.id.loadingBar);
					//loadingSpin.setVisibility(View.GONE);


				/*	placeList.add(new Place(
							place.getString("name")));*/


				}

			} catch(JSONException e) {
				e.printStackTrace();
			}

		}
	}

}
