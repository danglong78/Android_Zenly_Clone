package data.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

public class UserRefFriend extends UserRef {
    private boolean frozen;
    GeoPoint frozenLocation;
    private boolean cannotTrackMe;

    public UserRefFriend(){

    }

    public UserRefFriend(DocumentReference ref, Timestamp time, boolean frozen, GeoPoint frozenLocation, boolean cannotTrackMe) {
        super(ref, time);
        this.frozen = frozen;
        this.frozenLocation = frozenLocation;
        this.cannotTrackMe = cannotTrackMe;
    }

    public boolean getCannotTrackMe() {
        return cannotTrackMe;
    }

    public void setCannotTrackMe(boolean cannotTrackMe) {
        this.cannotTrackMe = cannotTrackMe;
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
