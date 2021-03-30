package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;

public class UserRef {
    private DocumentReference ref;
    private boolean hidden = false;
    private Timestamp time;

    public UserRef(){
    }

    public UserRef(DocumentReference ref, boolean hidden, Timestamp time) {
        this.ref = ref;
        this.hidden = hidden;
        this.time = time;
    }

    public void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    public DocumentReference getRef(){
        return ref;
    }

    public boolean getHidden(){
        return hidden;
    }

    public Timestamp getTime(){return time;}

    public boolean equals(Object obj) {
        if (obj instanceof UserRef) {
            UserRef o = (UserRef) obj;
            return o.ref == this.ref;
        }
        return false;
    }

    public static UserRef toUserRef(DocumentChange userRef){
        return userRef.getDocument().toObject(UserRef.class);
    }
}
