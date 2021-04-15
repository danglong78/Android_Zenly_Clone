package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

public class UserRefFriend extends UserRef {
    private boolean frozen = false;
    GeoPoint frozenLocation;
    private boolean isSetFrozen = false;

    public UserRefFriend(){

    }

    public UserRefFriend(DocumentReference ref, boolean frozen, GeoPoint frozenLocation, Timestamp time){
        super(ref, time);
        this.frozen = frozen;
        this.frozenLocation = frozenLocation;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean isSetFrozen() {
        return isSetFrozen;
    }

    public void setSetFrozen(boolean setFrozen) {
        isSetFrozen = setFrozen;
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

    public static <T extends UserRef> T  toUserRef (DocumentChange userRef){
        return (T) userRef.getDocument().toObject(UserRefFriend.class);
    }


}
