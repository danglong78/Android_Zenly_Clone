package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class Message {
    private User sender;
    private String ID;
    private String mess;
    private Timestamp time;

    public Message(User sender, String ID, String mess, Timestamp time) {
        this.sender = sender;
        this.ID = ID;
        this.mess = mess;
        this.time = time;
    }

    public Message(){};

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
