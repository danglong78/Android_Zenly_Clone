package data.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import data.models.User;

public class InvitingRepository extends ListUsersRepository{
    private final String TAG = "InvitingRepository";
    private final String USER_COLLECTION = "Users";

    private static InvitingRepository mInstance;

    private InvitingRepository(String INVITING_COLLECTION, String UID) {
        super(INVITING_COLLECTION, UID);
    }

    public static InvitingRepository getInstance(String INVITING_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new InvitingRepository(INVITING_COLLECTION, UID);
        }
        return mInstance;
    }
}
