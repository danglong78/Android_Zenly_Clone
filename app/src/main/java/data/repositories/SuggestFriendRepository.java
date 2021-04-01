package data.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import data.models.User;
import data.models.UserRefSuggest;

public class SuggestFriendRepository extends ListUsersRepository<UserRefSuggest> {
    private final String TAG = "SuggestFriendReposity";
    private final String USER_COLLECTION = "Users";

    private static SuggestFriendRepository mInstance;

    private SuggestFriendRepository(String SUGGESTIONS_COLLECTION, String UID) {
        super(SUGGESTIONS_COLLECTION, UID);
        this.myUserRef = new UserRefSuggest(mDb.document(USER_COLLECTION + "/" + UID), true, Timestamp.now());
    }

    public static SuggestFriendRepository getInstance(String SUGGESTIONS_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new SuggestFriendRepository(SUGGESTIONS_COLLECTION, UID);
        }
        return mInstance;
    }

    private ArrayList<String> getPhoneNumbersFromContact(Context context, String userPhone) {
        ArrayList<String> phones = new ArrayList<>();


        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phoneNo = "+84" + phoneNo.substring(1);

                        if (!phoneNo.equals(userPhone)) {
                            phones.add(phoneNo);
                            Log.i(TAG, "Phone: " + phoneNo);
                        }


                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return phones;
    }

    public void initContactSuggestFriendList(Context context, String userPhone) {
        addInit();

        ArrayList<String> phones = getPhoneNumbersFromContact(context, userPhone);

        for (String phone : phones) {
            mDb.collection(USER_COLLECTION)
                    .whereEqualTo("phone", phone)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    User newSuggest = dc.getDocument().toObject(User.class);
                                    addToMyRepository(new UserRefSuggest(mDb.document(USER_COLLECTION + "/" + newSuggest.getUID()), false, Timestamp.now()));
                                }
                            }
                        }
                    });
        }
    }

    public void modify(String modifyUID, boolean hidden) {
        listRef.document(modifyUID).update("hidden", hidden);
        Log.d(TAG, "modify: " + UID + " modify " + modifyUID + " " + hidden);
    }
}
