package data.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class User implements Parcelable {
    @Exclude
    protected Bitmap avatar;

    protected String name;
    protected String UID;
    protected String avatarURL;
    protected String dob;
    protected String phone;
    protected ArrayList<String> conversation;

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

    protected User(Parcel in) {
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        UID = in.readString();
        avatarURL = in.readString();
        dob = in.readString();
        phone = in.readString();
        conversation = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(avatar, flags);
        dest.writeString(name);
        dest.writeString(UID);
        dest.writeString(avatarURL);
        dest.writeString(dob);
        dest.writeString(phone);
        dest.writeStringList(conversation);
    }

}
