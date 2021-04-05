package data.models;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

public class UserLocation {
    String userUID;
    String name;
    GeoPoint location;
    String snippet;
    String imageURL;



    @Exclude
    boolean isFrozen = false;
    @Exclude
    ListenerRegistration listener = null;


    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public ListenerRegistration getListener() {
        return listener;
    }

    public void setListener(ListenerRegistration listener) {
        this.listener = listener;
    }



    public UserLocation() {

    }

    public UserLocation(String userUID, String name, GeoPoint location, String snippet, String imageURL) {
        this.userUID = userUID;
        this.name = name;
        this.location = location;
        this.snippet = snippet;
        this.imageURL = imageURL;
    }

    public UserLocation(String userUID) {
        this.userUID = userUID;
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



//    @Override
//    public boolean equals(@Nullable Object obj) {
//        return userUID.equals(((UserLocation)obj).getUserUID());
//    }
}
