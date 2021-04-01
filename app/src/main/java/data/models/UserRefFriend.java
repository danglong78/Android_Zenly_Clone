package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

public class UserRefFriend extends UserRef {
    private boolean frozen = false;
    GeoPoint frozenLocation;

    public UserRefFriend(){

    }

    public UserRefFriend(DocumentReference ref, boolean frozen, GeoPoint frozenLocation, Timestamp time){
        super(ref, time);
        this.frozen = frozen;
        this.frozenLocation = frozenLocation;
    }

    public boolean getFrozen(){
        return frozen;
    }

    public GeoPoint getFrozenLocation () {
        return frozenLocation;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public void setFrozenLocation(GeoPoint frozenLocation) {
        this.frozenLocation = frozenLocation;
    }

    public static UserRefFriend toUserRef (DocumentChange userRef){
        return userRef.getDocument().toObject(UserRefFriend.class);
    }


}
