package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import data.models.Conversation;
import data.models.Message;
import data.repositories.MessageRepository;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "Chat Room View Model";
    private MutableLiveData<ArrayList<Message>> messages;
    private MessageRepository mRepo;
    MutableLiveData<Boolean> isInited = new MutableLiveData<Boolean>();

    public void init(String convID, Context context, LifecycleOwner lifecycleOwner){
        mRepo = MessageRepository.getInstance();
        messages = new MutableLiveData<ArrayList<Message>>();
        messages = mRepo.getListMess(convID);
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
}
