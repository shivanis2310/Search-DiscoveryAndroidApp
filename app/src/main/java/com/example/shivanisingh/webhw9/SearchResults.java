package com.example.shivanisingh.webhw9;

public class SearchResults {
    private String name, address, imageurl, placeId;

    public SearchResults() {
    }

    public SearchResults(String name, String address, String imageurl, String placeId) {
        this.name = name;
        this.address =address;
        this.imageurl = imageurl;
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return imageurl;
    }

    public void setImage(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchResults other = (SearchResults) obj;
        if (name != other.name)
            return false;
        if (address != other.address)
            return false;
        if (imageurl != other.imageurl)
            return false;
        if(placeId!=other.placeId && other.placeId!=null)
            return false;
        return true;
    }
}