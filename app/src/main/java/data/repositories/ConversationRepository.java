package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import data.models.Conversation;
import data.models.User;

import static android.content.ContentValues.TAG;

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
        final Conversation[] aConv = new Conversation[1];
        convRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    aConv[0] = task.getResult().toObject(Conversation.class);
                }
            }
        });
        convRef.collection("Member List")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            User aUser = new User();
                            for (QueryDocumentSnapshot user : task.getResult()) {
                                count++;
                                User temp = user.toObject(User.class);
                                if(!temp.getUID().equals(FirebaseAuth.getInstance().getUid())){
                                    aUser = temp;
                                }
                            }
                            if(count<=2){
                                aConv[0].setAvatarURL(aUser.getAvatarURL());
                                aConv[0].setName(aUser.getName());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        conv.postValue(aConv[0]);
        return conv;
    }
}
