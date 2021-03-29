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
    protected String UID;
    protected FirebaseFirestore mDb;

    protected CollectionReference listRef;
    protected MutableLiveData<List<UserRef>> listUserRef;
    protected MutableLiveData<List<User>> listUser;

    protected DocumentSnapshot lastPaginated;

    private CollectionReference getListRef(String UID) {
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

    private void processSnapshots(QuerySnapshot snapshots) {
        List<UserRef> newRefList;

        if(listUserRef.getValue() == null)
            newRefList = new ArrayList<UserRef>();
        else newRefList = listUserRef.getValue();



        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    Log.d(TAG, "ADD: " + COLLECTION + " "  + UserRef.toUserRef(dc));
                    UserRef addUserRef = UserRef.toUserRef(dc);

                    if(!newRefList.contains(addUserRef)){
                        newRefList.add(addUserRef);
                    }

                    if (!addUserRef.getHidden())
                        addUserRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot getListUser: " + COLLECTION + " " + document.getData());
                                        User addUser = document.toObject(User.class);

                                        if((listUser.getValue() != null)){
                                            if(!listUser.getValue().contains(addUser)){
                                                listUser.getValue().add(addUser);
                                                listUser.postValue(listUser.getValue());
                                            }
                                        } else {
                                            List<User> newList = new ArrayList<>();
                                            newList.add(addUser);
                                            listUser.postValue(newList);
                                        }

                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    break;
                case MODIFIED:
                    Log.d(TAG, "MODIFIED: " + COLLECTION + " "  + UserRef.toUserRef(dc));
                    UserRef modifyUserRef = UserRef.toUserRef(dc);
                    newRefList.remove(modifyUserRef);

                    Log.d(TAG, "onEventMODIFIED: " + COLLECTION + " "  + modifyUserRef.getRef().getPath());
                    if (modifyUserRef.getHidden())
                        removeList(toUID(modifyUserRef.getRef().getPath()));

                    newRefList.add(modifyUserRef);
                    break;
                case REMOVED:
                    Log.d(TAG, "REMOVED: " + COLLECTION + " "  + UserRef.toUserRef(dc));
                    UserRef removeUserRef = UserRef.toUserRef(dc);
                    newRefList.remove(removeUserRef);
                    removeList(toUID(removeUserRef.getRef().getPath()));
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

                processSnapshots(snapshots);
            }
        });
    }

    public void addToMyRepository(String addUID) {
        UserRef addUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + addUID));
        Log.d(TAG, "add: create " + COLLECTION + " "   + addUserRef);

        listRef.document(addUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot add: " + COLLECTION + " "  + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        listRef.document(addUID).set(addUserRef);

                        Log.d(TAG,  COLLECTION + " " + UID + " add " + addUID);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void addToOtherRepository(String otherUID) {
        CollectionReference ref = getListRef(otherUID);
        UserRef myUserRef = new UserRef(mDb.document(USER_COLLECTION + "/" + UID));

        ref.document(otherUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot add: " + COLLECTION + " "  + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        ref.document(otherUID).set(myUserRef);
                        Log.d(TAG, UID + " added by " + otherUID);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void remove(String removeUID) {
        listRef.document(removeUID).delete();
        Log.d(TAG, "remove: " + COLLECTION + " "  + UID + " remove " + removeUID);
    }

    public void removeOnPaginations(String removeUID){
        remove(removeUID);
        removeList(removeUID);
        Log.d(TAG, "removeOnPaginations: " + COLLECTION + " "  + UID + " remove " + removeUID);
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
                Log.d(TAG, "removeList: list.size() " + COLLECTION + " "  + list.size());
                break;
            }
        }
        listUser.postValue(list);
    }

    public void getPaginations() {
        Query query = null;

        if (lastPaginated == null) {
            query = listRef.limit(pagination);
        } else {
            query = listRef.startAfter(lastPaginated).limit(pagination);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                processSnapshots(snapshots);

                if (snapshots.size() != 0)
                    lastPaginated = snapshots.getDocuments().get(snapshots.size() - 1);
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

