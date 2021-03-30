package data.repositories;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.models.User;
import data.models.UserRef;

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
