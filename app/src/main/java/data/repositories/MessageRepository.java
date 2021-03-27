package data.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
        messRef.set(aMess).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        return aMess;
    };


}
