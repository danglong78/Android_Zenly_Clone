package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.ChatAdapter;
import data.models.Conversation;
import data.models.Message;
import data.models.User;

public class MessageRepository {
    private final String MESS_COLLECTION = "Messages";
    private static MessageRepository mInstance;
    private FirebaseFirestore mDb;

    public MessageRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    public static MessageRepository getInstance(){
        if(mInstance == null){
            mInstance = new MessageRepository();
        }
        return mInstance;
    }

    public Message createServerMess(String convID){
        DocumentReference messRef = mDb.collection(MESS_COLLECTION).document();
        Message aMess = new Message();
        aMess.setID(messRef.getId());
        aMess.setMess("Wellcome to Zenly Clone!!!");
        aMess.setSenderID("zenlyserver");
        aMess.setTime(Timestamp.now());
        aMess.setConvID(convID);
        messRef.set(aMess).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        return aMess;
    };

    public MutableLiveData<ArrayList<Message>> getListMess(ChatAdapter adapter, ListenerRegistration listMessListener, String convID){
        CollectionReference colRef = mDb.collection("Messages");
        MutableLiveData<ArrayList<Message>> mess = new MutableLiveData<ArrayList<Message>>();
        ArrayList<Message> listMess = new ArrayList<Message>();
        Log.d("mess repo", "dang load convID: "+convID);
        listMessListener = colRef
                .whereEqualTo("convID",convID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Load List Mess", "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){
                            listMess.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                listMess.add(doc.toObject(Message.class));
                            }
                            Log.d("send Mess", "Load xong mess");
                            Log.d("mess repo", Integer.toString(listMess.size()));
                            Collections.sort(listMess, new Comparator<Message>() {
                                @Override
                                public int compare(Message o1, Message o2) {
                                    return o2.getTime().compareTo(o1.getTime());
                                }
                            });
                            mess.postValue(listMess);
                            adapter.deleteAll();
                            Log.d("send Mess","delete adapter");
                            Log.d("send Mess",Integer.toString(adapter.getItemCount()));
                            adapter.addToEnd(listMess,false);
                            Log.d("send Mess","add to adapter again");
                            adapter.notifyDataSetChanged();
                        }else{
                            Log.d("mess repo", "fail roi");
                        }
                    }
                });
        return mess;
    }

    public Message createMess(String convID,String text,String sender){
        DocumentReference messRef = mDb.collection(MESS_COLLECTION).document();
        Message aMess = new Message();
        aMess.setID(messRef.getId());
        aMess.setMess(text);
        aMess.setSenderID(sender);
        aMess.setTime(Timestamp.now());
        aMess.setConvID(convID);
        messRef.set(aMess).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        return aMess;
    };

    public MutableLiveData<Boolean> sendMess(String text,String senderID,String convID){
        MutableLiveData<Boolean> res = new MutableLiveData<Boolean>();
        DocumentReference docRef = mDb.collection("Messages").document();
        Message aMess = new Message(senderID,docRef.getId(),text,Timestamp.now(),convID);
        docRef.set(aMess).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ConversationRepository.getInstance().setRecentMess(aMess,convID);
                res.postValue(true);
            }
        });
        return res;
    }

    public Message createInitMess(String convID,String id1,String id2){
        DocumentReference docRef1 = mDb.collection(MESS_COLLECTION).document();
        Message aMess = new Message();
        aMess.setConvID(convID);
        aMess.setTime(Timestamp.now());
        aMess.setSenderID(id2);
        aMess.setMess("We are firend now... Let's have fun (auto generated message)");
        docRef1.set(aMess);
        DocumentReference docRef2 = mDb.collection(MESS_COLLECTION).document();
        Message aMess2 = new Message();
        aMess2.setConvID(convID);
        aMess2.setTime(Timestamp.now());
        aMess2.setSenderID(id1);
        aMess2.setMess("We are firend now... Let's have fun (auto generated message)");
        docRef2.set(aMess2);
        return aMess2;
    }

    public Message createInitMessGroup(String convID,String creatorID){
        DocumentReference docRef1 = mDb.collection(MESS_COLLECTION).document();
        Message aMess = new Message();
        aMess.setConvID(convID);
        aMess.setTime(Timestamp.now());
        aMess.setSenderID(creatorID);
        aMess.setMess("Welcome to our new Group Chat (auto generated message)");
        docRef1.set(aMess);
        return aMess;
    }
}
