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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import data.models.User;
import data.models.UserLocation;
import data.models.UserRef;
import data.models.UserRefFriend;
import data.models.UserRefSuggest;

public class FriendsRepository extends ListUsersRepository<UserRefFriend> {
    private final String TAG = "FriendsReposity";
    private final String USER_COLLECTION = "Users";
    private final String FRIEND_COLLECTION = "Friends";

    private static FriendsRepository mInstance;
    MutableLiveData<List<UserLocation>> userLocationList;

    private FriendsRepository(String FRIENDS_COLLECTION, String UID) {
        super(FRIENDS_COLLECTION, UID);
        myUserRef = new UserRefFriend(mDb.document(USER_COLLECTION + "/" + UID), false, null, Timestamp.now());
        userLocationList = new MutableLiveData<List<UserLocation>>();
        userLocationList.setValue(new ArrayList<UserLocation>());
    }

    public static FriendsRepository getInstance(String FRIENDS_COLLECTION, String UID) {
        if (mInstance == null) {
            mInstance = new FriendsRepository(FRIENDS_COLLECTION, UID);
        }
        return mInstance;
    }


    public MutableLiveData<List<UserLocation>> getUserLocationList(LifecycleOwner lifecycleOwner) {
        Log.d(TAG, "getUserLocationList: ");
//        Log.d(TAG, "getUserLocationList: listUserRef " + listUserRef.getValue().size());
        listUserRef.observe(lifecycleOwner, new Observer<List<? extends UserRef>>() {

            @Override
            public void onChanged(List<? extends UserRef> userRefs) {
                Log.d(TAG, "onChanged: userRef changed");
                UserLocation foundUserLocation = null;
                Log.d(TAG, "onChanged: " + userRefs.get(0));
                for (UserRefFriend userRef : (List<UserRefFriend>)userRefs) {
                    boolean isExisted = false;
                    for (UserLocation userLocation : userLocationList.getValue()) {
                        if (userLocation.getUserUID().equals(userRef.getRef().getId())) {
                            isExisted = true;
                            foundUserLocation = userLocation;

                            break;
                        }
                    }
                    if (!isExisted) {

                        Log.d(TAG, "onChanged: isExisted false " + userRef.getRef().getId());
                        mDb.collection("UserLocations").document(userRef.getRef().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: getUserLocationList " + task.getResult().toObject(UserLocation.class));
                                    UserLocation newUserLocation = task.getResult().toObject(UserLocation.class);
                                    userLocationList.getValue().add(newUserLocation);
                                    if (userRef.getFrozen()) {
                                        newUserLocation.setFrozen(true);
                                        newUserLocation.setLocation(userRef.getFrozenLocation());
                                    }
                                    else {
                                        newUserLocation.setFrozen(false);
                                    }
                                    userLocationList.postValue(userLocationList.getValue());

                                    if (!userRef.getFrozen()) {
                                        ListenerRegistration listener = mDb.collection("UserLocations").document(userRef.getRef().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {

                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                UserLocation changedUserLocation = value.toObject(UserLocation.class);
                                                boolean found = false;
                                                for (UserLocation userLocation : userLocationList.getValue()) {
                                                    if (userLocation.getUserUID().equals(changedUserLocation.getUserUID())) {
                                                        userLocation.setLocation(changedUserLocation.getLocation());
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                                userLocationList.postValue(userLocationList.getValue());
                                            }
                                        });
                                        newUserLocation.setListener(listener);
                                    }
                                }
                            }
                        });
                    }
                    else {
                        if (userRef.getFrozen()) {
                            if (!foundUserLocation.isFrozen()) {
                                Log.d(TAG, "onChanged: on frozen");
                                foundUserLocation.setFrozen(true);
                                foundUserLocation.getListener().remove();
                            }
                        }
                        else {
                            if (foundUserLocation.isFrozen()) {
                                Log.d(TAG, "onChanged: off frozen");
                                foundUserLocation.setFrozen(false);
                                ListenerRegistration listener = mDb.collection("UserLocations").document(userRef.getRef().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {

                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        UserLocation changedUserLocation = value.toObject(UserLocation.class);
                                        boolean found = false;
                                        for (UserLocation userLocation : userLocationList.getValue()) {
                                            if (userLocation.getUserUID().equals(changedUserLocation.getUserUID())) {
                                                userLocation.setLocation(changedUserLocation.getLocation());
                                                found = true;
                                                break;
                                            }
                                        }
                                        userLocationList.postValue(userLocationList.getValue());
                                    }
                                });
                                foundUserLocation.setListener(listener);
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

    protected void handleADDED(List<UserRefFriend> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleADDED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRefFriend addUserRef = UserRefFriend.toUserRef(dc);

        if (newRefList != null)
            if (!newRefList.contains(addUserRef)) {
                newRefList.add(addUserRef);
            }

        if(addUserRef.filterAddUserList())
            addUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    processAddUser(task);
                }
            });

        if(listUserRef.getValue()!=null){
            String x = "size() = " + listUserRef.getValue().size();
            for(UserRef u : listUserRef.getValue()){
                x += u.getRef().getPath() +" ";
            }
            Log.d(TAG, "listRefChange: " + COLLECTION + " " + x);
        }
    }

    protected void handleMODIFIED(List<UserRefFriend> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleMODIFIED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRefFriend modifyUserRef = UserRefFriend.toUserRef(dc);

        if (newRefList != null)
            newRefList.remove(modifyUserRef);

        Log.d(TAG, "onEventMODIFIED: " + COLLECTION + " " + modifyUserRef.getRef().getPath());

        if(modifyUserRef.filterAddUserList()){
            Log.d(TAG, "handleMODIFIED: if yes");
            modifyUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    processAddUser(task);
                }
            });
        }
        else{
            Log.d(TAG, "handleMODIFIED: if no");
            removeList(toUID(modifyUserRef.getRef().getPath()), listUser);
        }


        if (newRefList != null)
            newRefList.add(modifyUserRef);

        if(listUserRef.getValue()!=null){
            String x = "size() = " + listUserRef.getValue().size();
            for(UserRef u : listUserRef.getValue()) {
                x += u.getRef().getPath() +" ";
            }
            Log.d(TAG, "listRefChange: " + COLLECTION + " " + x);
        }
    }

    public void toggleFrozen(String hostUserUID, String friendUID, boolean flag) {
        DocumentReference friend = mDb.collection(FRIEND_COLLECTION).document(hostUserUID).collection("List").document(friendUID);
        friend.update("frozen", flag);

        DocumentReference curLocationRef = mDb.collection("UserLocations").document(friendUID);
        curLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserLocation curLocation = task.getResult().toObject(UserLocation.class);
                    friend.update("frozenLocation", curLocation.getLocation());
                }
            }
        });
    }
}
