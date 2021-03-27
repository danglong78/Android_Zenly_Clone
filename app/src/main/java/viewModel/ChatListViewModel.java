package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import data.models.Conversation;
import data.models.User;
import data.repositories.ConversationRepository;
import data.repositories.UserRepository;
import ultis.MyCallBack;

public class ChatListViewModel extends ViewModel {
    private static final String TAG = "Chat List View Model";
    private MutableLiveData<ArrayList<Conversation>> convList;
    private ConversationRepository mRepo;
    MutableLiveData<Boolean> isInited = new MutableLiveData<Boolean>();


    public void init(UserViewModel mUserViewModel) {
        if(convList!=null){
            return;
        }
        convList  = new MutableLiveData<ArrayList<Conversation>>();
        User aUser = mUserViewModel.getHostUser().getValue();
        Log.d(TAG, aUser.getConversation().get(0));
        mRepo = ConversationRepository.getInstance();
        ArrayList<Conversation> conv = new ArrayList<Conversation>();
        ArrayList<String> convID = aUser.getConversation();
        Log.d(TAG, Integer.toString(convID.size()));
        if(convID.size()>0){
            for(String s : aUser.getConversation()){
                Conversation temp = new Conversation();
                mRepo.getConversation(s, temp, new MyCallBack() {
                    @Override
                    public void onCallback(Conversation pos, Conversation des) {
                        des.setID(pos.getID());
                        des.setName(pos.getName());
                        des.setRecentMessage(pos.getRecentMessage());
                        des.setAvatarURL(pos.getAvatarURL());
                        conv.add(des);
                        convList.postValue(conv);
                    }
                });
            }
        }
    }

    public void init(UserViewModel mUserViewModel,Context context, LifecycleOwner lifecycleOwner){
        mRepo = ConversationRepository.getInstance();
        convList  = new MutableLiveData<ArrayList<Conversation>>();
        User aUser = mUserViewModel.getHostUser().getValue();
        ArrayList<String> convID = aUser.getConversation();
        int count = convID.size();
        convList = mRepo.getListConv(convID);
        convList.observe(lifecycleOwner, new Observer<ArrayList<Conversation>>(){
            @Override
            public void onChanged(ArrayList<Conversation> conversations) {
                if(conversations.size()==count){
                    Log.d(TAG, "load xong conv");
                    isInited.postValue(true);
                    convList.removeObserver(this);
                }else{
                    Log.d(TAG, "chua load xong conv");
                }
            }
        });
    }

    public LiveData<Boolean> getIsInited() {
        return isInited;
    }

    public MutableLiveData<ArrayList<Conversation>> getConvList() {
        return convList;
    }
}
