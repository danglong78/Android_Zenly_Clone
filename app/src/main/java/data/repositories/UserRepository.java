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
import data.models.UserLocation;
import ultis.MyCallBack;

public class UserRepository {
    private final String TAG = "UserRepository";
    private final String USER_COLLECTION = "Users";
    private final String USER_LOCATION_COLLECTION = "UserLocations";
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
        String uid = "";
        if ( (prefs != null) && (prefs.contains("uid")) ) {
            Log.d(TAG, "getHostUser: uid " + prefs.getString("uid", ""));
            uid = prefs.getString("uid", "");
        }
        return getUserWithUID(uid);
    }

    public MutableLiveData<UserLocation> getHostUserLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        String uid = "";
        if ( (prefs != null) && (prefs.contains("uid")) ) {
            Log.d(TAG, "getHostUserLocation: uid " + prefs.getString("uid", ""));
            uid = prefs.getString("uid", "");
        }
        return getUserLocationWithUID(uid);
    }

    public MutableLiveData<User> getUserWithUID(String UID) {
        DocumentReference userRef = mDb.collection(USER_COLLECTION).document(UID);
        MutableLiveData<User> user = new MutableLiveData<User>();
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully set the user client.");
                    Log.d(TAG,task.getResult().toObject(User.class).getUID());
                    user.postValue(task.getResult().toObject(User.class));
                }
            }
        });
        return user;
    }

    public MutableLiveData<UserLocation> getUserLocationWithUID(String UID) {
        DocumentReference userLocationRef = mDb.collection(USER_LOCATION_COLLECTION).document(UID);
        MutableLiveData<UserLocation> userLocation = new MutableLiveData<UserLocation>();
        userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully set the user client.");
                    userLocation.postValue(task.getResult().toObject(UserLocation.class));
                }
            }
        });
        return userLocation;
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

    public DocumentReference getUserLocationReference(String UID) {
        return mDb.collection(USER_LOCATION_COLLECTION).document(UID);
    }

    public MutableLiveData<List<Conversation>> getListConversations(){
        User user = getUserWithUID(FirebaseAuth.getInstance().getUid()).getValue();
        ConversationRepository convRepo = new ConversationRepository();
        List<Conversation> list = new ArrayList<Conversation>();
        for(String id : user.getConversation()){
            Conversation temp = new Conversation();
            convRepo.getConversation(id, temp, new MyCallBack() {
                @Override
                public void onCallback(Conversation pos, Conversation des) {
                    des.setID(pos.getID());
                    des.setName(pos.getName());
                    des.setRecentMessage(pos.getRecentMessage());
                    des.setAvatarURL(pos.getAvatarURL());
                }
            });
            list.add(temp);
        }
        MutableLiveData<List<Conversation>> res = null;
        res.postValue(list);
        return res;
    }
}
