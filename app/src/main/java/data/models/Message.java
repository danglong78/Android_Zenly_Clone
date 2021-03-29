package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class Message {
    private User sender;
    private String ID;
    private String mess;
    private Timestamp time;
    private String convID;

    public Message() {
    }

    public Message(User sender, String ID, String mess, Timestamp time, String convID) {
        this.sender = sender;
        this.ID = ID;
        this.mess = mess;
        this.time = time;
        this.convID = convID;
    }

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

    public String getConvID() {
        return convID;
    }

    public void setConvID(String convID) {
        this.convID = convID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", ID='" + ID + '\'' +
                ", mess='" + mess + '\'' +
                ", time=" + time +
                ", convID='" + convID + '\'' +
                '}';
    }
}
