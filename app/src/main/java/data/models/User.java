package data.models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;

public class User {
    @Exclude
    private Bitmap avatar;

    private String name;
    private String UID;
    private String avatarURL;
    private String dob;
    private GeoPoint location;
    private String[] conversation;


    public User(Bitmap avatar, String name, String UID, String avatarURL, String dob, GeoPoint location, String[] conversation) {
        this.avatar = avatar;
        this.name = name;
        this.UID = UID;
        this.avatarURL = avatarURL;
        this.dob = dob;
        this.location = location;
        this.conversation = conversation;
    }

    public String[] getConversation() {
        return conversation;
    }

    public void setConversation(String[] conversation) {
        this.conversation = conversation;
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
                "avatar=" + avatar +
                ", name='" + name + '\'' +
                ", UID='" + UID + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", dob='" + dob + '\'' +
                ", location=" + location +
                ", conversation=" + Arrays.toString(conversation) +
                '}';
    }
}
