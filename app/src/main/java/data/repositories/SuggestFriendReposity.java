package data.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.models.User;

public class SuggestFriendReposity {
    private final String TAG = "SuggestFriendReposity";
    private final String USER_COLLECTION = "Users";

    private FirebaseFirestore mDb;
    private static SuggestFriendReposity mInstance;


    private SuggestFriendReposity() {
        mDb = FirebaseFirestore.getInstance();
    }

    public static SuggestFriendReposity getInstance() {
        if (mInstance == null) {
            mInstance = new SuggestFriendReposity();
        }
        return mInstance;
    }

    private ArrayList<String> getPhoneNumbersFromContact(Context context) {
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

                        phones.add(phoneNo);
                        Log.i(TAG, "Phone: " + phoneNo);

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

    public MutableLiveData<List<User>> getSuggestFriendList(Context context) {
        ArrayList<String> phones = getPhoneNumbersFromContact(context);
        MutableLiveData<List<User>> suggestFriendList = new MutableLiveData<List<User>>();
        suggestFriendList.setValue(new ArrayList<User>());


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

                            List<User> newSuggestFriendList = new ArrayList<>();

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                if(dc.getType() == DocumentChange.Type.ADDED){
                                    newSuggestFriendList.add(dc.getDocument().toObject(User.class));
                                    Log.d(TAG, "SuggestFriend: " + dc.getDocument().get("phone"));
                                }
                            }

                            suggestFriendList.getValue().addAll(newSuggestFriendList);
                        }
                    });
        }

        Log.d(TAG, "getSuggestFriendList: " + suggestFriendList.getValue());

        return suggestFriendList;
        }
    }
