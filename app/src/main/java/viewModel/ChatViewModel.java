package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import adapter.ChatAdapter;
import data.models.Conversation;
import data.models.Message;
import data.repositories.MessageRepository;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "Chat Room View Model";
    private MutableLiveData<ArrayList<Message>> messages;
    private MessageRepository mRepo;
    private String convID;
    MutableLiveData<Boolean> isInited = new MutableLiveData<Boolean>();

    public void init(ChatAdapter adapter, ListenerRegistration listMessListener, String convID, Context context, LifecycleOwner lifecycleOwner){
        this.convID = convID;
        mRepo = MessageRepository.getInstance();
        messages = new MutableLiveData<ArrayList<Message>>();
        messages = mRepo.getListMess(adapter,listMessListener,convID);
        messages.observe(lifecycleOwner, new Observer<ArrayList<Message>>(){
            @Override
            public void onChanged(ArrayList<Message> mess) {
                if(mess!=null){
                    Log.d(TAG, "load xong mess list");
                    isInited.postValue(true);
                    messages.removeObserver(this);
                }
            }
        });
    }

    public LiveData<Boolean> getIsInited() {
        return isInited;
    }

    public MutableLiveData<ArrayList<Message>> getMessList() {
        return messages;
    }

    public MutableLiveData<Boolean> SendMess(String text,Context context, LifecycleOwner lifecycleOwner){
        MutableLiveData<Boolean> res = new MutableLiveData<Boolean>();
        MutableLiveData<Boolean> aRes = mRepo.sendMess(text, FirebaseAuth.getInstance().getUid(),this.convID);
        aRes.observe(lifecycleOwner,new Observer<Boolean>(){
            @Override
            public void onChanged(Boolean val) {
                if(val){
                    Log.d("send Mess", "send Mess success");
                    res.postValue(true);
                }
            }
        });
        return res;
    }


}
