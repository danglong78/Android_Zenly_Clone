package data.repositories;


public class InvitationsRepository extends ListUsersRepository{
    private final String TAG = "InvitationsReposity";
    private final String USER_COLLECTION = "Users";

    private static InvitationsRepository mInstance;

    private InvitationsRepository(String INVITATIONS_COLLECTION, String UID) {
        super(INVITATIONS_COLLECTION, UID);
    }

    public static InvitationsRepository getInstance(String INVITATIONS_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new InvitationsRepository(INVITATIONS_COLLECTION, UID);
        }
        return mInstance;
    }
}
