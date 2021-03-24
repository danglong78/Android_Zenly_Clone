package data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position; // required field
    private String title; // required field
    private String snippet; // required field
    private UserLocation userLocation;
    private String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURI(String imageURL) {
        this.imageURL = imageURL;
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public ClusterMarker(LatLng position, String title, String snippet, UserLocation userLocation, String imageURL) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.userLocation = userLocation;
        this.imageURL = imageURL;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }
}