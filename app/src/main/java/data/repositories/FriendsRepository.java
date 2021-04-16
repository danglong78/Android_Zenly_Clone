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

    private ListenerRegistration userDirectionListener;
    private MutableLiveData<UserLocation> userDirection;
    private MutableLiveData<UserRefFriend> userDirectionRef;

    private List<UserRef> lastUserRefList = new ArrayList<UserRef>();

    private FriendsRepository(String FRIENDS_COLLECTION, String UID) {
        super(FRIENDS_COLLECTION, UID);
        myUserRef = new UserRefFriend(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now(), false, null , false);
        userLocationList = new MutableLiveData<List<UserLocation>>();
        userLocationList.setValue(new ArrayList<UserLocation>());

        userFrozenList = new MutableLiveData<List<User>>();
        userFrozenList.setValue(new ArrayList<User>());

        userPreciseList = new MutableLiveData<List<User>>();
        userPreciseList.setValue(new ArrayList<User>());

        blockInstance = BlockRepository.getInstance(BLOCK_COLLECTION, UID);
        invitingInstance = InvitingRepository.getInstance(INVITING_COLLECTION, UID);
        invitationInstance = InvitationsRepository.getInstance(INVITATIONS_COLLECTION, UID);

        userFriendList = new MutableLiveData<List<UserFriendList>>();
        userFriendList.setValue(new ArrayList<>());

        userDirection = new MutableLiveData<UserLocation>();
        userDirection.setValue(null);

        userDirectionRef = new MutableLiveData<UserRefFriend>();
        userDirectionRef.setValue(null);


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

                for (UserRef userRef: lastUserRefList) {
                    if (!userRefs.contains(userRef)) {
                        Log.d(TAG, "onChanged: remove FriendLocation");
                        userLocationList.getValue().remove(new UserLocation(userRef.getRef().getId()));
                    }
                }
                userLocationList.postValue(userLocationList.getValue());

                lastUserRefList.clear();
                lastUserRefList.addAll(userRefs);

                for (UserRefFriend userRef : (List<UserRefFriend>) userRefs) {

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

    public void addToMyRepository(String UID) {
        addToMyRepository(new UserRefFriend(mDb.document(USER_COLLECTION + "/" + UID), Timestamp.now(), false, null , false));
    }

    protected void processAddFrozenUser(Task<DocumentSnapshot> task){
        Log.d(TAG, "processAddFrozenUser: asdadasdsa");
        User addUser =  super.processAddUser(task);

        userPreciseList.getValue().remove(addUser);
        userPreciseList.postValue(userPreciseList.getValue());

        userFrozenList.getValue().add(addUser);
        userFrozenList.postValue(userFrozenList.getValue());
    }

    protected void processAddPreciseUser(Task<DocumentSnapshot> task){
        Log.d(TAG, "processAddPreciseUser: dasdsadsadsa");
        User addUser =  super.processAddUser(task);

        userFrozenList.getValue().remove(addUser);
        userFrozenList.postValue(userFrozenList.getValue());

        userPreciseList.getValue().add(addUser);
        userPreciseList.postValue(userPreciseList.getValue());
    }

    protected void handleADDED(List<UserRefFriend> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleADDED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRefFriend addUserRef = UserRefFriend.toUserRef(dc);

        if (newRefList != null)
            if (!newRefList.contains(addUserRef)) {
                newRefList.add(addUserRef);
            }
        Log.d(TAG, "handleADDED: frozen " + toUID(addUserRef.getRef().getPath()) + " setfrozen " + addUserRef.getCanNotTrackMe() );
        if(!toUID(addUserRef.getRef().getPath()).equals(UID))
            if(addUserRef.getCanNotTrackMe())
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

        if(modifyUserRef.getCanNotTrackMe())
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
        DocumentReference hostOfFriend = mDb.collection(FRIEND_COLLECTION).document(friendUID).collection("List").document(hostUserUID);

        if (flag) {
            DocumentReference curLocationRef = mDb.collection("UserLocations").document(friendUID);
            curLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        UserLocation curLocation = task.getResult().toObject(UserLocation.class);
                        hostOfFriend.update("frozenLocation", curLocation.getLocation()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hostOfFriend.update("frozen", true);
                            }
                        });
                    }
                }
            });
        }
        else {
            hostOfFriend.update("frozen", false);
        }

        DocumentReference friendOfHost = mDb.collection(FRIEND_COLLECTION).document(hostUserUID).collection("List").document(friendUID);
        friendOfHost.update("canTrackMe", flag);
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
                Log.d(TAG, "asdasd: " + snapshots.getResult().size());

                for (QueryDocumentSnapshot dc : snapshots.getResult()) {

                    UserRef addUserRef = dc.toObject(UserRef.class);

                    addUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User addUser = document.toObject(User.class);


                                    if (!addUser.getUID().equals(friendUID)){
                                        UserFriendList user = new UserFriendList(addUser);

                                        if(invitingInstance.getListUser().getValue().contains(user))
                                            user.setTag("INVITED");

                                        if(invitationInstance.getListUser().getValue().contains(user))
                                            user.setTag("PENDING");

                                        if(listUser.getValue().contains(user))
                                            user.setTag("MUTUAL");

                                        if(user.getUID().compareTo(UID)==0)
                                            user.setTag("YOU");

                                        if(!blockInstance.getListUser().getValue().contains(user)){
                                            userFriendList.getValue().add(user);
                                            userFriendList.postValue(userFriendList.getValue());
                                            Log.d(TAG, "processSnapshots: " + COLLECTION + " " + friendUID + " add " + addUser.getUID() + " size=" + userFriendList.getValue().size());
                                        }

                                    }

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });

        return userFriendList;
    }

    public void resetFriendListOfFriends(){
        userFriendList.setValue((new ArrayList<UserFriendList>()));
    }

    public MutableLiveData<UserLocation> getUserDirection(String friendUID){
        if (!friendUID.equals(""))
            userDirectionListener = mDb.collection("UserLocations").document(friendUID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null && value.exists()) {
                        Log.d(TAG, "Current data: " + value.getData());

                        userDirection.setValue(value.toObject(UserLocation.class));
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

        return userDirection;
    }

    public MutableLiveData<UserRefFriend> getUserRefFriend(String friendUID){
        if (!friendUID.equals(""))
            mDb.collection("Friends").document(UID).collection(LIST_COLLECTION).document(friendUID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (value != null && value.exists()) {
                        Log.d(TAG, "Current data: " + value.getData());

                        userDirectionRef.setValue(value.toObject(UserRefFriend.class));
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

        return userDirectionRef;
    }

    public void removeUserDirectionListener() {
        if (userDirectionListener!=null)
            userDirectionListener.remove();
    }

    public void offUserDirection(){
        userDirection.setValue(null);
    }
}
