package data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;

import java.util.List;

import data.models.User;
import data.models.UserRef;

public class InvitingRepository extends ListUsersRepository <UserRef>{
    private final String TAG = "InvitingRepository";
    private final String USER_COLLECTION = "Users";

    private static InvitingRepository mInstance;

    private InvitingRepository(String INVITING_COLLECTION, String UID) {
        super(INVITING_COLLECTION, UID);
        myUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now());
    }

    public static InvitingRepository getInstance(String INVITING_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new InvitingRepository(INVITING_COLLECTION, UID);
        }
        return mInstance;
    }

    public void addToMyRepository(String UID){
        addToMyRepository(new UserRef(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now()) );
    }
}
