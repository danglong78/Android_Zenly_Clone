package data.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Arrays;

public class Conversation implements Parcelable {
    @Exclude
    private Bitmap avatar;

    private String ID;
    private String name;
    private String avatarURL;
    private Message recentMessage;
    private ArrayList<User> member;

    public Conversation(Bitmap avatar, String ID, String name, String avatarURL, Message recentMessage, ArrayList<User> member) {
        this.avatar = avatar;
        this.ID = ID;
        this.name = name;
        this.avatarURL = avatarURL;
        this.recentMessage = recentMessage;
        this.member = member;
    }

    public Conversation(){}

    protected Conversation(Parcel in) {
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        ID = in.readString();
        name = in.readString();
        avatarURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(avatar, flags);
        dest.writeString(ID);
        dest.writeString(name);
        dest.writeString(avatarURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

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

    public Message getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(Message recentMessage) {
        this.recentMessage = recentMessage;
    }

    public ArrayList<User> getMember() {
        return member;
    }

    public void setMember(ArrayList<User> member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "avatar=" + avatar +
                ", ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", recentMessage=" + recentMessage +
                ", member=" + member +
                '}';
    }
}
