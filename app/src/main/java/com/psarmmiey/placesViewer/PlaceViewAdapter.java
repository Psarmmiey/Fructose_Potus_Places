package com.psarmmiey.placesViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by psarmmiey on 12/22/16.
 */

class PlaceViewAdapter extends ArrayAdapter {
    private final Map<String, Bitmap> bitmaps = new HashMap<>();

    public PlaceViewAdapter(Context context, int resource, String placeID) {
        super(context, resource);
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get weather object for this specified ListView position
        final Place place = new Place();

        ViewHolder viewHolder; // object that reference's list item's views

        // check for reusable ViewHolder from a ListView item that scrolled
        // offscreen; otherwise, create a new ViewHolder
        if (convertView == null) { // no reusable ViewHolder, so create one
            viewHolder = new PlaceViewAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView =
                    inflater.inflate(R.layout.content_detail_view, parent, false);

            viewHolder.placeImageView =
                    (ImageView) convertView.findViewById(R.id.placeImageView);
            viewHolder.placeTextView =
                    (TextView) convertView.findViewById(R.id.placeNameTextView);
            viewHolder.addressTextView =
                    (TextView) convertView.findViewById(R.id.placeAddressTextView);
            viewHolder.phoneNumberTextView =
                    (TextView) convertView.findViewById(R.id.placePhoneNumberTextView);
            viewHolder.websiteTextView =
                    (TextView) convertView.findViewById(R.id.placeWebsiteTextView);
            viewHolder.ratingBar.findViewById(R.id.placeRatingBar);
            convertView.setTag(viewHolder);


        } else { // reuse existing ViewHolder stored as the list item's tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // if weather condition icon already downloaded, use it;
        // otherwise, download icon in a separate thread
        // download and display weather condition image
        assert place != null;
        if (bitmaps.containsKey(place.getIconLink())) {
            viewHolder.placeImageView.setImageBitmap(
                    bitmaps.get(place.getIconLink())
            );
        } else new PlaceViewAdapter.LoadImageTask(viewHolder.placeImageView).execute(
                place.getIconLink());

        // get other data from Weather object and place into views
        final Context context = getContext(); // for loading String resources
        viewHolder.placeTextView.setText(place.getName());

        viewHolder.addressTextView.setText(
                context.getString(R.string.address, place.getAddress()));
        viewHolder.phoneNumberTextView.setText(
                context.getString(R.string.phoneNum, place.getPhoneNumber()));
        viewHolder.websiteTextView.setText(
                context.getString(R.string.web, place.getWebsite()));
        viewHolder.ratingBar.setRating(
                place.getRating()
        );

        return convertView; // return completed list item to display
    }

    private static class ViewHolder {
        ImageView placeImageView;
        TextView placeTextView;
        TextView addressTextView;
        TextView websiteTextView;
        TextView phoneNumberTextView;
        RatingBar ratingBar;

    }

    // AsyncTask to load weather condition icons in a separate thread
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView; // displays the thumbnail

        // store ImageView on which to set the download Bitmap
        public LoadImageTask(ImageView imageView) {
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

}
