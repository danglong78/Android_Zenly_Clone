package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import data.models.Conversation;
import data.models.User;

public class ConversationRepository {
    private final String CONV_COLLECTION = "Conversations";
    private final String MESS_COLLECTION = "Messages";

    private static ConversationRepository mInstance;
    private FirebaseFirestore mDb;

    public ConversationRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    public static ConversationRepository getInstance(){
        if(mInstance == null){
            mInstance = new ConversationRepository();
        }
        return mInstance;
    }

    public MutableLiveData<Conversation> getConversation(String convID){
        DocumentReference convRef = mDb.collection(CONV_COLLECTION).document(convID);
        MutableLiveData<Conversation> conv = null;
        convRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    conv.postValue(task.getResult().toObject(Conversation.class));
                }
            }
        });
        return conv;
    }
}
