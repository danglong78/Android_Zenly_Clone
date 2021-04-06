package data.repositories;

import com.google.firebase.Timestamp;

import java.util.List;

import data.models.UserRef;

public class BlockedByRepository extends ListUsersRepository<UserRef> {
    private final String TAG = "BlockRepository";
    private final String USER_COLLECTION = "Users";

    private static BlockedByRepository mInstance;

    private BlockedByRepository(String BLOCKEDBY_COLLECTION, String UID) {
        super(BLOCKEDBY_COLLECTION, UID);
        myUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now());
    }

    public static BlockedByRepository getInstance(String BLOCKEDBY_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new BlockedByRepository(BLOCKEDBY_COLLECTION, UID);
        }
        return mInstance;
    }
}
