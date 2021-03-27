package data.models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    @Exclude
    private Bitmap avatar;

    private String name;
    private String UID;
    private String avatarURL;
    private String dob;
    private String phone;
    private ArrayList<String> conversation;

    public User(Bitmap avatar, String name, String UID, String avatarURL, String dob, String phone, ArrayList<String> conversation) {
        this.avatar = avatar;
        this.name = name;
        this.UID = UID;
        this.avatarURL = avatarURL;
        this.dob = dob;
        this.phone = phone;
        this.conversation = conversation;
    }

    public User(){

    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getConversation() {
        return conversation;
    }

    public void setConversation(ArrayList<String> conversation) {
        this.conversation = conversation;
    }

    public void setNewUserConv(String convID){
        conversation = new ArrayList<String>();
        conversation.add(convID);
    }

    public boolean equals(Object o) {
        if(o instanceof User) {
            User u = (User) o;
            return this.getUID().equals(u.getUID());
        }
        else return false;
    }
}
