package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import data.models.UserRef;

public class ListUsersRepository {
    private final String TAG = "ListUsersReposity";
    private final String USER_COLLECTION = "Users";
    private final String LIST_COLLECTION = "List";
    private final int pagination = 8;

    private String COLLECTION;
    private boolean isInited = false;
    protected String UID;
    protected FirebaseFirestore mDb;

    protected CollectionReference listRef;
    protected MutableLiveData<List<UserRef>> listUserRef;
    protected MutableLiveData<List<User>> listUser;

    protected DocumentSnapshot lastPaginated;

    private CollectionReference getListRef(String UID) {
        mDb.collection(COLLECTION).document(UID).collection(LIST_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
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

        lastPaginated = null;
    }

    public MutableLiveData<List<User>> getListUser() {
        return listUser;
    }

    public MutableLiveData<List<UserRef>> getListUserReference() {
        return listUserRef;
    }

    private void handleREMOVED(List<UserRef> newRefList, DocumentChange dc){
        Log.d(TAG, "handleREMOVED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRef removeUserRef = UserRef.toUserRef(dc);
        newRefList.remove(removeUserRef);
        removeList(toUID(removeUserRef.getRef().getPath()));
    }

    private void handleMODIFIED(List<UserRef> newRefList, DocumentChange dc) {
        Log.d(TAG, "handleMODIFIED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRef modifyUserRef = UserRef.toUserRef(dc);
        newRefList.remove(modifyUserRef);

        Log.d(TAG, "onEventMODIFIED: " + COLLECTION + " " + modifyUserRef.getRef().getPath());
        if (modifyUserRef.getHidden())
            removeList(toUID(modifyUserRef.getRef().getPath()));

        newRefList.add(modifyUserRef);
    }

    private void handleADDED(List<UserRef> newRefList, DocumentChange dc) {
        Log.d(TAG, "handleADDED: " + COLLECTION + " " + UserRef.toUserRef(dc));
        UserRef addUserRef = UserRef.toUserRef(dc);

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

                            if (isInited) {
                                Log.d(TAG, "processSnapshots: " + COLLECTION + " " + UID + " add " + addUser.getUID());
                                listUser.getValue().add(addUser);
                                listUser.postValue(listUser.getValue());
                            } else {
                                Log.d(TAG, "processSnapshots: " + COLLECTION + " " + UID + " init " + addUser.getUID());
                                List<User> newList = new ArrayList<>();
                                newList.add(addUser);
                                listUser.setValue(newList);
                                isInited = true;
                            }

                            String check = "";
                            for (User u : listUser.getValue()) {
                                check += u.getUID() + " ";
                            }
                            Log.d(TAG, "processSnapshots: listUser " + check);


                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
    }

    private void processAllSnapshots(QuerySnapshot snapshots) {
        List<UserRef> newRefList = null;

        if (listUserRef.getValue() == null)
            newRefList = new ArrayList<UserRef>();
        else newRefList = listUserRef.getValue();


        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    handleADDED(newRefList, dc);
                    break;
                case MODIFIED:
                    handleMODIFIED(newRefList, dc);
                    break;
                case REMOVED:
                    handleREMOVED(newRefList, dc);
                    break;
            }
        }

        listUserRef.postValue(newRefList);
//        Log.d(TAG, "onEvent: " + COLLECTION + " "   + listUserRef.getValue().size());
    }

    private void processPaginationSnapshots(QuerySnapshot snapshots) {
        List<UserRef> newRefList = null;

        if (listUserRef.getValue() == null)
            newRefList = new ArrayList<UserRef>();
        else newRefList = listUserRef.getValue();


        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    handleADDED(newRefList, dc);
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
        Log.d(TAG, "addToMyRepository: Runned");
        UserRef addUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + addUID), hidden);
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
        UserRef myUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + UID), false);

        ref.document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "addToOtherRepository doc.exist(): " + COLLECTION + " " + document.getData());
                    } else {
                        ref.document(UID).set(myUserRef);
                        Log.d(TAG, "addToOtherRepository: " + COLLECTION +" "+ UID + " added by " + otherUID);
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

    public void removeFromOtherRepository(String otherUID){
        CollectionReference ref = getListRef(otherUID);
        ref.document(UID).delete();
    }

    public void removeFromPaginations(String removeUID) {
        removeFromMyRepository(removeUID);
        removeList(removeUID);
        Log.d(TAG, "removeOnPaginations: " + COLLECTION + " " + UID + " remove " + removeUID);
    }

    private String toUID(String userRef) {
        return userRef.substring(userRef.lastIndexOf('/') + 1);
    }

    protected void removeList(String UID) {
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
        Query query = null;

        if (lastPaginated == null) {
            query = listRef.limit(pagination);
        } else {
            query = listRef.startAfter(lastPaginated).limit(pagination);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                if (snapshots.size() != 0)
                    lastPaginated = snapshots.getDocuments().get((snapshots.size() - 1));

                processPaginationSnapshots(snapshots);
            }
        });
    }

    public void listenPaginationChange(){
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

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            // Skip
                            break;
                        case MODIFIED:
                            handleMODIFIED(newRefList, dc);
                            break;
                        case REMOVED:
                            handleREMOVED(newRefList, dc);
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

