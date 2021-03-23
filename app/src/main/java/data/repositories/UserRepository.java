package data.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.models.Conversation;
import data.models.User;

public class UserRepository {
    private final String TAG = "UserRepository";
    private final String USER_COLLECTION = "Users";
    private final String FRIENDS_COLLECTION = "Friends";

    private static UserRepository mInstance;
    private FirebaseFirestore mDb;

    private UserRepository() {
        mDb = FirebaseFirestore.getInstance();
    }


    public static UserRepository getInstance(){
        if(mInstance == null){
            mInstance = new UserRepository();
        }
        return mInstance;
    }

    public MutableLiveData<User> getHostUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        String uuid = "";
        if ( (prefs != null) && (prefs.contains("uid")) ) {
            uuid = prefs.getString("uid", "");
        }
        return getUserWithUID(uuid);
    }

    public MutableLiveData<User> getUserWithUID(String UID) {
        DocumentReference userRef = mDb.collection(USER_COLLECTION).document(UID);
        MutableLiveData<User> user = new MutableLiveData<User>();
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully set the user client.");
                    user.postValue(task.getResult().toObject(User.class));
                }
            }
        });
        return user;
    }

    public MutableLiveData<List<User>> getUserFriends(String UID) {
        CollectionReference friendsRef = mDb.collection(USER_COLLECTION).document(UID).collection(FRIENDS_COLLECTION);
        MutableLiveData<List<User>> friendList = new MutableLiveData<List<User>>(new ArrayList<User>());

        friendsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                List<User> newFriendList = new ArrayList<>();

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if(doc.getType() == DocumentChange.Type.ADDED) {
                        newFriendList.add(doc.getDocument().toObject(User.class));
                    }
                    else if (doc.getType() == DocumentChange.Type.REMOVED) {
                        friendList.getValue().remove(doc.getDocument().toObject(User.class));
                    }
                }

                friendList.getValue().addAll(newFriendList);
                friendList.postValue(friendList.getValue());
            }
        });
        return friendList;
    }

    public CollectionReference getFriendsCollectionRef(String UID) {
        return mDb.collection(USER_COLLECTION).document(UID).collection(FRIENDS_COLLECTION);
    }

    public DocumentReference getUserReference(String UID) {
        return mDb.collection(USER_COLLECTION).document(UID);
    }

    public MutableLiveData<List<Conversation>> getListConversations(){
        User user = getUserWithUID(FirebaseAuth.getInstance().getUid()).getValue();
        ConversationRepository convRepo = new ConversationRepository();
        List<Conversation> list = new ArrayList<Conversation>();
        for(String id : user.getConversation()){
            list.add(convRepo.getConversation(id).getValue());
        }
        MutableLiveData<List<Conversation>> res = null;
        res.postValue(list);
        return res;
    }
}
