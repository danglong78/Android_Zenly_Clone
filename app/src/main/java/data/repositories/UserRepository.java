package data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import data.models.User;
import data.models.UserFriendList;
import data.models.UserLocation;

public class UserRepository {
    private static UserRepository mInstance;
    private final String TAG = "UserRepository";
    private final String USER_COLLECTION = "Users";
    private final String USER_LOCATION_COLLECTION = "UserLocations";
    private final String FRIENDS_COLLECTION = "Friends";
    private final String BLOCK_COLLECTION = "Block";
    private final String INVITING_COLLECTION = "Inviting";
    private final String INVITATIONS_COLLECTION = "Invitations";

    private static FriendsRepository friendInstance;
    private static BlockRepository blockInstance;
    private static InvitingRepository invitingInstance;
    private static InvitationsRepository invitationInstance;

    private FirebaseFirestore mDb;

    private MutableLiveData<List<UserFriendList>> searchList;

    private UserRepository() {
        mDb = FirebaseFirestore.getInstance();


    }


    public static UserRepository getInstance() {
        if (mInstance == null) {
            mInstance = new UserRepository();
        }
        return mInstance;
    }

    public MutableLiveData<User> getHostUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        String uid = "";
        if ((prefs != null) && (prefs.contains("uid"))) {
            Log.d(TAG, "getHostUser: uid " + prefs.getString("uid", ""));
            uid = prefs.getString("uid", "");
        }
        return getUserWithUID(uid);
    }

    public MutableLiveData<UserLocation> getHostUserLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        String uid = "";
        if ((prefs != null) && (prefs.contains("uid"))) {
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
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: successfully set the user client.");
                    Log.d(TAG, task.getResult().toObject(User.class).getUID());
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
                if (task.isSuccessful()) {
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
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        newFriendList.add(doc.getDocument().toObject(User.class));
                    } else if (doc.getType() == DocumentChange.Type.REMOVED) {
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

    public void addConv(String uId, String convId) {
        mDb.collection(USER_COLLECTION).document(uId).update("conversation", FieldValue.arrayUnion(convId));
    }

    public Task<Void> setName(String UID, String name) {
        return mDb.collection(USER_COLLECTION).document(UID).update("name", name);

    }

    public Task<Void> setDob(String UID, String dob) {

        return mDb.collection(USER_COLLECTION).document(UID).update("dob", dob);
    }

    public void setAvatarURL(String UID, Uri file) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars/" + UID + file.getLastPathSegment());
        ref.putFile(file).addOnSuccessListener(task -> {
            mDb.collection(USER_COLLECTION).document(UID).update("avatarURL", UID + file.getLastPathSegment());

        });
    }

    public void setAvatarURL(String UID, Bitmap file) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars/" + UID + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        ref.putBytes(data).addOnSuccessListener(task -> {
            mDb.collection(USER_COLLECTION).document(UID).update("avatarURL", UID + ".jpg");

        });
    }

    public  Task <QuerySnapshot>  getUserWithPhone(String phone) {
        Query userRef = mDb.collection(USER_COLLECTION).whereEqualTo("phone", phone);
        return userRef.get();
    }

    public MutableLiveData<List<UserFriendList>> getUserWithName(String name, String myUID){
        friendInstance = FriendsRepository.getInstance(FRIENDS_COLLECTION, myUID);
        blockInstance = BlockRepository.getInstance(BLOCK_COLLECTION, myUID);
        invitingInstance = InvitingRepository.getInstance(INVITING_COLLECTION, myUID);
        invitationInstance = InvitationsRepository.getInstance(INVITATIONS_COLLECTION, myUID);

        mDb.collection(USER_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> snapshots) {
                List<UserFriendList> newList = new ArrayList<UserFriendList>();

                for (QueryDocumentSnapshot dc : snapshots.getResult()) {
                    User user = dc.toObject(User.class);
                    UserFriendList userFriendList = new UserFriendList(user);

                    if(!user.getName().contains(name))
                        continue;

                    if(invitingInstance.getListUser().getValue().contains(user))
                        userFriendList.setTag("Invited");

                    if(invitationInstance.getListUser().getValue().contains(user))
                        userFriendList.setTag("Pending");

                    if(friendInstance.getListUser().getValue().contains(user))
                        userFriendList.setTag("Friend");

                    if(!blockInstance.getListUser().getValue().contains(user)){
                        newList.add(userFriendList);
                    }
                }

                searchList.setValue(newList);
            }
        });

        return searchList;
    }

    public void resetSearchList(){
        searchList.setValue(new ArrayList<>());
    }
}
