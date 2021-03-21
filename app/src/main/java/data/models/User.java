package data.models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class User {
    @Exclude
    private Bitmap avatar;

    private String name;
    private String UID;
    private String avatarURL;
    private String dob;
    private GeoPoint location;


    public User(Bitmap avatar, String name, String UID, String avatarURL, GeoPoint location) {
        this.avatar = avatar;
        this.name = name;
        this.UID = UID;
        this.avatarURL = avatarURL;
        this.location = location;
    }

    public User() {}

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                ", UID ='" + this.UID + '\'' +
                ", name ='" + this.name + '\'' +
                ", avatar ='" + avatar + '\'' +
                '}';
    }
}
