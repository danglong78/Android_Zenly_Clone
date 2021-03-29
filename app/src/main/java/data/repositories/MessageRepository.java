package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        aMess.setSender(new User(null,"Zenly Clone","zenlyserver","Zenly_appicon.png",null,null,null));
        aMess.setTime(Timestamp.now());
        aMess.setConvID(convID);
        messRef.set(aMess).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        return aMess;
    };

    public MutableLiveData<ArrayList<Message>> getListMess(String convID){
        CollectionReference colRef = mDb.collection("Messages");
        MutableLiveData<ArrayList<Message>> mess = new MutableLiveData<ArrayList<Message>>();
        ArrayList<Message> listMess = new ArrayList<Message>();
        Log.d("mess repo", "dang load convID: "+convID);
        colRef.whereEqualTo("convID",convID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("mess repo", "dang load mess list");
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        listMess.add(doc.toObject(Message.class));
                    }
                    Log.d("mess repo", "Load xong mess 1");
                    Log.d("mess repo", Integer.toString(listMess.size()));
                    Collections.sort(listMess, new Comparator<Message>() {
                        @Override
                        public int compare(Message o1, Message o2) {
                            return o1.getTime().compareTo(o2.getTime());
                        }
                    });
                    mess.postValue(listMess);
                }else{
                    Log.d("mess repo", "fail roi");
                }
            }
        });
        return mess;
    }


}
