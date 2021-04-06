package data.repositories;

import com.google.firebase.Timestamp;

import data.models.UserRef;
import data.models.UserRefFriend;

public class BlockRepository extends ListUsersRepository<UserRef> {
    private final String TAG = "BlockRepository";
    private final String USER_COLLECTION = "Users";

    private static BlockRepository mInstance;

    private BlockRepository(String BLOCK_COLLECTION, String UID) {
        super(BLOCK_COLLECTION, UID);
        myUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now());
    }

    public static BlockRepository getInstance(String BLOCK_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new BlockRepository(BLOCK_COLLECTION, UID);
        }
        return mInstance;
    }

    public void addToMyRepository(String UID){
        addToMyRepository(new UserRef(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now()) );
    }
    

}
