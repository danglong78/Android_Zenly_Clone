package data.models;

import com.google.firebase.firestore.GeoPoint;

public class UserLocation {
    String userUID;
    String name;
    GeoPoint location;
    String snippet;
    String imageURL;


    public UserLocation() {

    }

    public UserLocation(String userUID, String name, GeoPoint location, String snippet, String imageURL) {
        this.userUID = userUID;
        this.name = name;
        this.location = location;
        this.snippet = snippet;
        this.imageURL = imageURL;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }


    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
