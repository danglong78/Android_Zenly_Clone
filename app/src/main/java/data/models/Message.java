package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class Message {
    private DocumentReference sender;
    private String ID;
    private String mess;
    private Timestamp time;

    public Message(DocumentReference sender, String ID, String mess, Timestamp time) {
        this.sender = sender;
        this.ID = ID;
        this.mess = mess;
        this.time = time;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public void setSender(DocumentReference sender) {
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

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", ID='" + ID + '\'' +
                ", mess='" + mess + '\'' +
                ", time=" + time +
                '}';
    }
}
