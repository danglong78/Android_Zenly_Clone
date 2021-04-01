package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;

public class UserRefSuggest extends UserRef {
    private boolean hidden = false;

    public UserRefSuggest(){
    }

    public UserRefSuggest(DocumentReference ref, boolean hidden, Timestamp time){
        super(ref, time);
        this.hidden = hidden;
    }

    public boolean getHidden(){
        return hidden;
    }

    public void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    public static UserRefSuggest toUserRef (DocumentChange userRef){
        return userRef.getDocument().toObject(UserRefSuggest.class);
    }

    public boolean filterAddUserList(){
        return !hidden;
    }
}
