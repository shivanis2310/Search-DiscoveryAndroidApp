package com.example.shivanisingh.webhw9;

public class PlaceDetails {
    private String latitude, longitude, place_id, address, phone, rating, pageURL, website;

    public PlaceDetails() {
    }

    public PlaceDetails(String latitude, String longitude, String place_id) {
        this.latitude = latitude;
        this.longitude =longitude;
        this.place_id = place_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

}