package data.repositories;


public class FriendsRepository extends ListUsersRepository {
    private final String TAG = "FriendsReposity";
    private final String USER_COLLECTION = "Users";

    private static FriendsRepository mInstance;

    private FriendsRepository(String FRIENDS_COLLECTION, String UID) {
        super(FRIENDS_COLLECTION, UID);
    }

    public static FriendsRepository getInstance(String FRIENDS_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new FriendsRepository(FRIENDS_COLLECTION, UID);
        }
        return mInstance;
    }
}
