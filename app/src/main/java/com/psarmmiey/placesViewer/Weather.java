// Weather.java
// Maintains one day's weather information

package com.psarmmiey.placesViewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by psarmmiey on 11/18/16.
 */


public class Weather {

    private static String placeID;
    public final double lat;
    public final double lng;
    public final double humidity;
    public final String iconURL;
    public  String nameOfPlace = "";
    public  String description = "";

    // constructor
    public Weather(String name, double lat, double lng,
                   double humidity, String description, String iconName, String placeID) {
        // NumberFormat to format double temperatures rounded to integers
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.nameOfPlace= name;
        this.lat = lat;
        this.lng = lng;
        this.humidity = humidity;
        this.description = description;
        this.iconURL = iconName;
        this.placeID = placeID;


    }

    public static String getPlaceID() {
        return placeID;
    }

// --Commented out by Inspection START (12/20/16 2:07 PM):
//    public Weather(String name, double lat, double aLong, double distance, String icon) {
//    }
// --Commented out by Inspection STOP (12/20/16 2:07 PM)

    // convert timestamp to a day's name(e.g., Monday, Tuesday, ...)
    private static String convertTimeStampToDay(long timestamp) {
        Calendar calendar = Calendar.getInstance(); // create Calendar
        calendar.setTimeInMillis(timestamp * 1000); // set time
        TimeZone tz = TimeZone.getDefault(); // get device's time zone
        // adjust tie for device's time zone
        calendar.add(Calendar.MILLISECOND,
                tz.getOffset(calendar.getTimeInMillis()));

        // SimpleDateFormat that returns the day's name
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
        return dateFormatter.format(calendar.getTime());
    }

}
