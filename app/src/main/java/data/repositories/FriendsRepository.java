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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.models.User;
import data.models.UserFriendList;
import data.models.UserLocation;
import data.models.UserRef;
import data.models.UserRefFriend;
import data.models.UserRefSuggest;

public class FriendsRepository extends ListUsersRepository<UserRefFriend> {
    private final String TAG = "FriendsReposity";
    private final String USER_COLLECTION = "Users";
    private final String FRIEND_COLLECTION = "Friends";
    private final String BLOCK_COLLECTION = "Block";
    private final String INVITING_COLLECTION = "Inviting";
    private final String INVITATIONS_COLLECTION = "Invitations";

    private static FriendsRepository mInstance;
    private static BlockRepository blockInstance;
    private static InvitingRepository invitingInstance;
    private static InvitationsRepository invitationInstance;

    private MutableLiveData<List<UserLocation>> userLocationList;
    private MutableLiveData<List<User>> userFrozenList;
    private MutableLiveData<List<User>> userPreciseList;
    private MutableLiveData<List<UserFriendList>> userFriendList;

    private FriendsRepository(String FRIENDS_COLLECTION, String UID) {
        super(FRIENDS_COLLECTION, UID);
        myUserRef = new UserRefFriend(mDb.document(USER_COLLECTION + "/" + UID), false, null, Timestamp.now());
        userLocationList = new MutableLiveData<List<UserLocation>>();
        userLocationList.setValue(new ArrayList<UserLocation>());

        userFrozenList = new MutableLiveData<List<User>>();
        userFrozenList.setValue(new ArrayList<User>());

        userPreciseList = new MutableLiveData<List<User>>();
        userPreciseList.setValue(new ArrayList<User>());

        blockInstance = BlockRepository.getInstance(BLOCK_COLLECTION, UID);
        invitingInstance = InvitingRepository.getInstance(INVITING_COLLECTION, UID);
        invitationInstance = InvitationsRepository.getInstance(INVITATIONS_COLLECTION, UID);
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
//                Log.d(TAG, "onChanged: " + userRefs.get(0));
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

    protected void processAddFrozenUser(Task<DocumentSnapshot> task){
        User addUser =  super.processAddUser(task);
        userFrozenList.getValue().remove(addUser);
        userFrozenList.getValue().add(addUser);
        userFrozenList.postValue(listUser.getValue());
    }

    protected void processAddPreciseUser(Task<DocumentSnapshot> task){
        User addUser =  super.processAddUser(task);
        userPreciseList.getValue().remove(addUser);
        userPreciseList.getValue().add(addUser);
        userPreciseList.postValue(listUser.getValue());
    }

    protected void handleADDED(List<UserRefFriend> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleADDED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRefFriend addUserRef = UserRefFriend.toUserRef(dc);

        if (newRefList != null)
            if (!newRefList.contains(addUserRef)) {
                newRefList.add(addUserRef);
            }

        if(addUserRef.getFrozen())
            addUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    processAddFrozenUser(task);

                }
            });
        else
            addUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    processAddPreciseUser(task);
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

        if(modifyUserRef.getFrozen())
            modifyUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    processAddFrozenUser(task);

                }
            });
        else
            modifyUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    processAddPreciseUser(task);
                }
            });


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

    public MutableLiveData<List<User>> getUserFrozenList(){
        return this.userFrozenList;
    }

    public MutableLiveData<List<User>> getUserPreciseList(){
        return this.userPreciseList;
    }

    public MutableLiveData<List<UserFriendList>> getFriendListOfFriends(String friendUID){
        getListRef(friendUID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> snapshots) {
                List<UserFriendList> newList = new ArrayList<UserFriendList>();

                for (QueryDocumentSnapshot dc : snapshots.getResult()) {
                    User user = dc.toObject(User.class);
                    UserFriendList userFriendList = new UserFriendList(user);

                    if(invitingInstance.getListUser().getValue().contains(user))
                        userFriendList.setTag("Invited");

                    if(invitationInstance.getListUser().getValue().contains(user))
                        userFriendList.setTag("Pending");

                    if(listUser.getValue().contains(user))
                        userFriendList.setTag("Mutual");

                    if(!blockInstance.getListUser().getValue().contains(user)){
                        newList.add(userFriendList);
                    }
                }

                userFriendList.setValue(newList);
            }
        });

        return userFriendList;
    }

    public void resetFriendListOfFriends(){
        userFriendList.setValue((new ArrayList<UserFriendList>()));
    }
}
