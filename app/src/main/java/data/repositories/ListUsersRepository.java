package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import data.models.User;
import data.models.UserLocation;
import data.models.UserRef;

public class ListUsersRepository {
    private final String TAG = "ListUsersReposity";
    protected final String USER_COLLECTION = "Users";
    protected final String LIST_COLLECTION = "List";
    protected final int pagination = 8;

    protected String COLLECTION;
    protected String UID;
    protected FirebaseFirestore mDb;

    protected CollectionReference listRef;
    protected MutableLiveData<List<UserRef>> listUserRef;
    protected MutableLiveData<List<User>> listUser;

    protected DocumentSnapshot lastPaginated;

    private CollectionReference getListRef(String UID) {
        Log.d(TAG, "getListRef: " + COLLECTION);
        mDb.collection(COLLECTION).document(UID).collection(LIST_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d(TAG, "Init Collection: " + COLLECTION);
                    addInit();
                }
            }
        });

        return mDb.collection(COLLECTION).document(UID).collection(LIST_COLLECTION);
    }

    public ListUsersRepository(String COLLECTION, String UID) {
        this.COLLECTION = COLLECTION;

        this.UID = UID;
        mDb = FirebaseFirestore.getInstance();
        listRef = getListRef(this.UID);

        listUserRef = new MutableLiveData<List<UserRef>>();

        listUser = new MutableLiveData<List<User>>();
        listUser.setValue(new ArrayList<User>());

        lastPaginated = null;
    }

    public MutableLiveData<List<User>> getListUser() {
        return listUser;
    }

    public MutableLiveData<List<UserRef>> getListUserReference() {
        return listUserRef;
    }

    protected void handleREMOVED(List<UserRef> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleREMOVED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRef removeUserRef = UserRef.toUserRef(dc);

        if (newRefList != null)
            newRefList.remove(removeUserRef);

        removeList(toUID(removeUserRef.getRef().getPath()), listUser);

        if(listUserRef.getValue()!=null){
            String x = "size() = " + listUserRef.getValue().size();
            for(UserRef u : listUserRef.getValue()){
                x += u.getRef().getPath() +" ";
            }
            Log.d(TAG, "listRefChange: " + COLLECTION + " " + x);
        }
    }

    protected void handleMODIFIED(List<UserRef> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleMODIFIED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRef modifyUserRef = UserRef.toUserRef(dc);

        if (newRefList != null)
            newRefList.remove(modifyUserRef);


        Log.d(TAG, "onEventMODIFIED: " + COLLECTION + " " + modifyUserRef.getRef().getPath());
        if (modifyUserRef.getHidden())
            removeList(toUID(modifyUserRef.getRef().getPath()), listUser);
        

        if (newRefList != null)
            newRefList.add(modifyUserRef);

        if(listUserRef.getValue()!=null){
            String x = "size() = " + listUserRef.getValue().size();
            for(UserRef u : listUserRef.getValue()){
                x += u.getRef().getPath() +" ";
            }
            Log.d(TAG, "listRefChange: " + COLLECTION + " " + x);
        }
    }

    protected void handleADDED(List<UserRef> newRefList, MutableLiveData<List<User>> listUser, DocumentChange dc) {
        Log.d(TAG, "handleADDED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRef addUserRef = UserRef.toUserRef(dc);

        if (newRefList != null)
            if (!newRefList.contains(addUserRef)) {
                newRefList.add(addUserRef);
            }

        if (!addUserRef.getHidden())
            addUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User addUser = document.toObject(User.class);

                            Log.d(TAG, "processSnapshots: " + COLLECTION + " " + UID + " add " + addUser.getUID());
                            if (!listUser.getValue().contains(addUser)) {
                                listUser.getValue().add(addUser);
                                listUser.postValue(listUser.getValue());
                            }

//                            if (listUser.getValue() != null) {
//                                Log.d(TAG, "processSnapshots: " + COLLECTION + " " + UID + " add " + addUser.getUID());
//                                listUser.getValue().add(addUser);
//                                listUser.postValue(listUser.getValue());
//                            } else {
//                                Log.d(TAG, "processSnapshots: " + COLLECTION + " " + UID + " init " + addUser.getUID());
//                                List<User> newList = new ArrayList<>();
//                                newList.add(addUser);
//                                listUser.setValue(newList);
////                                isInited = true;
//                            }

                            String check = "";
                            for (User u : listUser.getValue()) {
                                check += u.getUID() + " ";
                            }
                            Log.d(TAG, "processSnapshots: listUser " + COLLECTION + " " + check);


                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
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

    private void processAllSnapshots(QuerySnapshot snapshots) {
        List<UserRef> newRefList = null;

        if (listUserRef.getValue() == null)
            newRefList = new ArrayList<UserRef>();
        else newRefList = listUserRef.getValue();


        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    handleADDED(newRefList, listUser, dc);
                    break;
                case MODIFIED:
                    handleMODIFIED(newRefList, listUser, dc);
                    break;
                case REMOVED:
                    handleREMOVED(newRefList, listUser, dc);
                    break;
            }
        }

        listUserRef.postValue(newRefList);
//        Log.d(TAG, "onEvent: " + COLLECTION + " "   + listUserRef.getValue().size());
    }

    protected void processPaginationSnapshots(QuerySnapshot snapshots) {
        List<UserRef> newRefList = null;

        if (listUserRef.getValue() == null)
            newRefList = new ArrayList<UserRef>();
        else newRefList = listUserRef.getValue();


        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    handleADDED(newRefList, listUser, dc);
                    break;
                case MODIFIED:
                    // Skip
                    break;
                case REMOVED:
                    // Skip
                    break;
            }
        }

        listUserRef.postValue(newRefList);
//        Log.d(TAG, "onEvent: " + COLLECTION + " "   + listUserRef.getValue().size());
    }

    public void getAll() {
        listRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                processAllSnapshots(snapshots);
            }
        });
    }

    public void addToMyRepository(String addUID) {
        addToMyRepository(addUID, false);
    }

    public void addInit() {
        addToMyRepository(UID, true);
    }

    private void addToMyRepository(String addUID, boolean hidden) {
        Log.d(TAG, "addToMyRepository: " + COLLECTION);
        UserRef addUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + addUID), hidden, Timestamp.now());
        Log.d(TAG, "add: create " + COLLECTION + " " + addUserRef);

        listRef.document(addUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "addToMyRepository doc.exist(): " + COLLECTION + " " + document.getData());
                    } else {
                        Log.d(TAG, "addToMyRepository: " + COLLECTION + " " + UID + " add " + addUID);
                        listRef.document(addUID).set(addUserRef);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void addToOtherRepository(String otherUID) {
        Log.d(TAG, "addToOtherRepository: Runned");
        CollectionReference ref = getListRef(otherUID);
        UserRef myUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + UID), false, Timestamp.now());

        ref.document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "addToOtherRepository doc.exist(): " + COLLECTION + " " + document.getData());
                    } else {
                        ref.document(UID).set(myUserRef);
                        Log.d(TAG, "addToOtherRepository: " + COLLECTION + " " + UID + " added by " + otherUID);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void removeFromMyRepository(String removeUID) {
        listRef.document(removeUID).delete();
        Log.d(TAG, "remove: " + COLLECTION + " " + UID + " remove " + removeUID);
    }

    public void removeFromOtherRepository(String otherUID) {
        CollectionReference ref = getListRef(otherUID);
        ref.document(UID).delete();
    }

    public void removeFromPaginations(String removeUID, MutableLiveData<List<User>> listUser) {
        removeFromMyRepository(removeUID);
        removeList(removeUID, listUser);
        Log.d(TAG, "removeOnPaginations: " + COLLECTION + " " + UID + " remove " + removeUID);
    }

    private String toUID(String userRef) {
        return userRef.substring(userRef.lastIndexOf('/') + 1);
    }

    protected void removeList(String UID, MutableLiveData<List<User>> listUser) {
        Log.d(TAG, "removeList: " + UID);
        List<User> list = listUser.getValue();
        for (User u : list) {
            if (u.getUID().equals(UID)) {
                list.remove(u);
                Log.d(TAG, "removeList: list.size() " + COLLECTION + " " + list.size());
                break;
            }
        }
        listUser.postValue(list);
    }

    public void getPagination() {
        Log.d(TAG, "getPagination: run " + COLLECTION);
        Query query = null;

        if (lastPaginated == null) {
            query = listRef.orderBy("time").limit(pagination);
        } else {
            query = listRef.orderBy("time").startAfter(lastPaginated).limit(pagination);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                Log.d(TAG, "getPagination onSuccess: " + COLLECTION);

                Log.d(TAG, "getPagination: " + COLLECTION + " size() " + snapshots.size());

                if (snapshots.size() != 0) {
                    lastPaginated = snapshots.getDocuments().get((snapshots.size() - 1));
                    //Log.d(TAG, "getPagination: " + COLLECTION + " lastPaginated " + lastPaginated);
                }


                processPaginationSnapshots(snapshots);
            }
        });
    }

    public void listenPaginationChange() {
        listRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                List<UserRef> newRefList = null;

                if (listUserRef.getValue() == null)
                    newRefList = new ArrayList<UserRef>();
                else newRefList = listUserRef.getValue();

                Log.d(TAG, "listenPaginationChange: " + COLLECTION + " size() " + snapshots.getDocumentChanges().size());

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "listenPaginationChange: ADDED " + COLLECTION);

                            if (listUser.getValue().size() < pagination) {
                                Log.d(TAG, "listenPaginationChange: if " + COLLECTION);
                                getPagination();
                            }

                            break;
                        case MODIFIED:
                            handleMODIFIED(newRefList, listUser, dc);
                            break;
                        case REMOVED:
                            handleREMOVED(newRefList, listUser, dc);
                            break;
                    }
                }

                listUserRef.postValue(newRefList);
            }
        });
    }

//    public void add(String addUID){
//        listRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "onComplete: ");
//                    List<DocumentReference> list = (List<DocumentReference>) task.getResult().get(FIELD);
//
//                    list.add(mDb.document(USER_COLLECTION + "/" + addUID));
//
//                    WriteBatch batch = mDb.batch();
//                    batch.set(listRef, list);
//                }
//            }
//        });
//    }
//
//    public void remove(String removeUID){
//        listRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "onComplete: ");
//                    List<DocumentReference> list = (List<DocumentReference>) task.getResult().get(FIELD);
//
//                    list.remove(mDb.document(USER_COLLECTION + "/" + removeUID));
//
//                    WriteBatch batch = mDb.batch();
//                    batch.set(listRef, list);
//                }
//            }
//        });
//    }


}

