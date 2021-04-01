package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;

public class UserRef {
    private DocumentReference ref;
    private Timestamp time;

    public UserRef(){
    }

    public UserRef(DocumentReference ref, Timestamp time) {
        this.ref = ref;
        this.time = time;
    }

    public DocumentReference getRef(){
        return ref;
    }

    public Timestamp getTime(){return time;}

    public boolean equals(Object obj) {
        if (obj instanceof UserRef) {
            UserRef o = (UserRef) obj;
            return o.ref.getPath().equals(this.ref.getPath());
        }
        return false;
    }

    public static UserRef toUserRef(DocumentChange userRef){
        return userRef.getDocument().toObject(UserRef.class);
    }

    public void setTimestampNow(){
        this.time = Timestamp.now();
    }

}
