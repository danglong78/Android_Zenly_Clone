package data.models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;

import java.util.Arrays;

public class Conversation {
    @Exclude
    private Bitmap avatar;

    private String ID;
    private String name;
    private String avatarURL;
    private Message recentMessage;

    public Conversation(Bitmap avatar, String ID, String name, String avatarURL, Message recentMessage) {
        this.avatar = avatar;
        this.ID = ID;
        this.name = name;
        this.avatarURL = avatarURL;
        this.recentMessage = recentMessage;
    }

    public Message getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(Message recentMessage) {
        this.recentMessage = recentMessage;
    }

    public Conversation(){

    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }




    @Override
    public String toString() {
        return "Conversation{" +
                "avatar=" + avatar +
                ", ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' + '\'' +
                '}';
    }
}
