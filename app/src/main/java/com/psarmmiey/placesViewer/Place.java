package com.psarmmiey.placesViewer;

/**
 * Created by psarmmiey on 12/22/16.
 */

class Place {

    private static String name;
    private static String address;
    private static String website;
    private static float rating;
    private static String iconLink;
    private static String phoneNumber;

    public Place(String name, String address, String website, String rating, String iconLink, String
            phoneNumber) {
        setName(name);
        setAddress(address);
        setWebsite(website);
        setRating(Integer.parseInt(rating));
        setIconLink(iconLink);
        setPhoneNumber(phoneNumber);

    }

    public Place() {

    }

    public static String getName() {
        return name;
    }

    private static void setName(String name) {
        Place.name = name;
    }

    public static String getAddress() {
        return address;
    }

    private static void setAddress(String address) {
        Place.address = address;
    }

    public static String getWebsite() {
        return website;
    }

    private static void setWebsite(String website) {
        Place.website = website;
    }

    public static float getRating() {
        return rating;
    }

    private static void setRating(float rating) {
        Place.rating = rating;
    }

    public static String getIconLink() {
        return iconLink;
    }

    private static void setIconLink(String iconLink) {
        Place.iconLink = iconLink;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    private static void setPhoneNumber(String phoneNumber) {
        Place.phoneNumber = phoneNumber;
    }
}
