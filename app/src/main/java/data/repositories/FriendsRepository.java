package data.repositories;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import data.models.UserLocation;
import data.models.UserRef;
import data.models.UserRefFriend;

public class FriendsRepository extends ListUsersRepository<UserRefFriend> {
    private final String TAG = "FriendsReposity";
    private final String USER_COLLECTION = "Users";

    private static FriendsRepository mInstance;

    private FriendsRepository(String FRIENDS_COLLECTION, String UID) {
        super(FRIENDS_COLLECTION, UID);
        myUserRef = new UserRefFriend(mDb.document(USER_COLLECTION + "/" + UID), false, null, Timestamp.now());
    }

    public static FriendsRepository getInstance(String FRIENDS_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new FriendsRepository(FRIENDS_COLLECTION, UID);
        }
        return mInstance;
    }

    public MutableLiveData<List<UserLocation>> getUserLocationList(LifecycleOwner lifecycleOwner) {
        MutableLiveData<List<UserLocation>> userLocationList = new MutableLiveData<List<UserLocation>>();
        userLocationList.setValue(new ArrayList<UserLocation>());

        listUserRef.observe(lifecycleOwner, new Observer<List<UserRefFriend>>() {

            @Override
            public void onChanged(List<UserRefFriend> userRefs) {
                for (UserRefFriend userRef : userRefs) {
                    boolean isExisted = false;
                    for (UserLocation userLocation : userLocationList.getValue()) {
                        if (userLocation.getUserUID().equals(userRef.getRef().getId())) {
                            isExisted = true;
                            break;
                        }
                    }
                    if (!isExisted) {
                        mDb.collection("UserLocations").document(userRef.getRef().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: getUserLocationList " + task.getResult().toObject(UserLocation.class));
                                    userLocationList.getValue().add(task.getResult().toObject(UserLocation.class));
                                    userLocationList.postValue(userLocationList.getValue());
                                }
                            }
                        });

                        mDb.collection("UserLocations").document(userRef.getRef().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {

                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                UserLocation changedUserLocation = value.toObject(UserLocation.class);
                                for (UserLocation userLocation : userLocationList.getValue()) {
                                    if (userLocation.getUserUID().equals(changedUserLocation.getUserUID())) {
                                        userLocation.setLocation(changedUserLocation.getLocation());
                                        break;
                                    }
                                }

                                userLocationList.postValue(userLocationList.getValue());
                            }
                        });
                    }

                    if (!userRef.getFrozen()) Log.d(TAG, "onChanged: false hidden " + userRef.getRef().getId() + " " + userRefs.size());

                    if (userRef.getFrozen()) {
                        Log.d(TAG, "onChanged: Hidden user " + userRef.getFrozen());
                        for (UserLocation userLocation : userLocationList.getValue()) {
                            if (userLocation.getUserUID().equals(userRef.getRef().getId())) {
                                Log.d(TAG, "onChanged: Found hidden user " + userLocation.getUserUID());
                                userLocationList.getValue().remove(userLocation);
                                userLocationList.postValue(userLocationList.getValue());
                                break;
                            }
                        }
                    }
                }
            }
        });

        return userLocationList;
    }

    public void addToMyRepository(String UID){
        addToMyRepository(new UserRefFriend(mDb.document(USER_COLLECTION + "/" + UID), false, null, Timestamp.now()) );
    }
}
