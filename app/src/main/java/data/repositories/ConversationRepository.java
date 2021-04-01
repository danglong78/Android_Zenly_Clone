package data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.ChatListAdapter;
import data.models.Conversation;
import data.models.Message;
import data.models.User;
import ultis.MyCallBack;

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

    public void getConversation(String convID,Conversation des, MyCallBack callBack){
        Log.d(TAG, convID);
        DocumentReference convRef = mDb.collection(CONV_COLLECTION).document(convID);
        MutableLiveData<Conversation> conv = new MutableLiveData<Conversation>();
        convRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "dit me zo");
                Conversation aConv = task.getResult().toObject(Conversation.class);
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
                                        aConv.setAvatarURL(aUser.getAvatarURL());
                                        aConv.setName(aUser.getName());
                                        conv.postValue(aConv);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                                callBack.onCallback(aConv,des);
                            }
                        });
            }
        });
    }

    public MutableLiveData<ArrayList<Conversation>> getListConv(RecyclerView nav_drawer_recycler_view,ListenerRegistration listConvListener, ChatListAdapter madapter, ArrayList<String> listID){
        CollectionReference convRef = mDb.collection(CONV_COLLECTION);
        MutableLiveData<ArrayList<Conversation>> conv = new MutableLiveData<ArrayList<Conversation>>();
        ArrayList<Conversation> convList = new ArrayList<Conversation>();
        listConvListener = convRef.whereIn("id",listID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Load List Mess", "onEvent: Listen failed.", e);
                            return;
                        }if(queryDocumentSnapshots != null){
                            convList.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Conversation temp = doc.toObject(Conversation.class);
                                int count=0;
                                User u = new User();
                                for(User aUser : temp.getMember()){
                                    count++;
                                    if(!aUser.getUID().equals(FirebaseAuth.getInstance().getUid())){
                                        u = aUser;
                                    }
                                }
                                if(count<=2){
                                    temp.setAvatarURL(u.getAvatarURL());
                                    temp.setName(u.getName());
                                }
                                convList.add(temp);
                            }
                            Collections.sort(convList, new Comparator<Conversation>() {
                                @Override
                                public int compare(Conversation o1, Conversation o2) {
                                    return o2.getRecentMessage().getTime().compareTo(o1.getRecentMessage().getTime());
                                }
                            });
                            Log.d("abcxyz",convList.toString());
                            conv.postValue(convList);
                            Log.d(TAG,"TriNe123");
                            madapter.setConversationList(convList);
                            nav_drawer_recycler_view.setAdapter(madapter);
                        }
                    }
                });
        return conv;
    }

    public String creteServerConv(){
        DocumentReference convRef = mDb.collection(CONV_COLLECTION).document();
        Conversation newConv = new Conversation();
        newConv.setID(convRef.getId());
        newConv.setRecentMessage(MessageRepository.getInstance().createServerMess(convRef.getId()));
        ArrayList<User> member = new ArrayList<User>();
        member.add(new User(null,"Zenly Clone","zenlyserver","Zenly_appicon.png",null,null,null));
        newConv.setMember(member);
        convRef.set(newConv).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Tao xong new conv");
            }
        });
        return convRef.getId();
    }

    public MutableLiveData<Boolean> setRecentMess(Message aMess,String conID){
        DocumentReference conRef = mDb.collection(CONV_COLLECTION).document(conID);
        MutableLiveData<Boolean> res = new MutableLiveData<Boolean>();
        conRef.update("recentMessage",aMess).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                res.postValue(true);
            }
        });
        return res;
    }
}
