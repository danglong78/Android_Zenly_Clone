package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import adapter.ChatListAdapter;
import data.models.Conversation;
import data.models.User;
import data.repositories.ConversationRepository;
import data.repositories.UserRepository;
import ultis.MyCallBack;

public class ChatListViewModel extends ViewModel {
    private static final String TAG = "Chat List View Model";
    private LiveData<ArrayList<Conversation>> convList = new MutableLiveData<ArrayList<Conversation>>();
    private ConversationRepository mRepo;
    MutableLiveData<Boolean> isInited = new MutableLiveData<Boolean>();


//    public void init(RecyclerView nav_drawer_recycler_view,ListenerRegistration listConvListener, ChatListAdapter madapter, UserViewModel mUserViewModel, Context context, LifecycleOwner lifecycleOwner){
//        mRepo = ConversationRepository.getInstance();
//        convList  = new MutableLiveData<ArrayList<Conversation>>();
//        mUserViewModel.getIsInited().observe(lifecycleOwner,new Observer<Boolean>(){
//            @Override
//            public void onChanged(Boolean abool) {
//                if(abool){
//                    User aUser = mUserViewModel.getHostUser().getValue();
//                    ArrayList<String> convID = aUser.getConversation();
//                    int count = convID.size();
//                    convList = mRepo.getListConv(nav_drawer_recycler_view,listConvListener,madapter,convID);
//                    convList.observe(lifecycleOwner, new Observer<ArrayList<Conversation>>(){
//                        @Override
//                        public void onChanged(ArrayList<Conversation> conversations) {
//                            if(conversations.size()==count){
//                                Log.d(TAG, "load xong conv");
//                                isInited.postValue(true);
//                                convList.removeObserver(this);
//                            }else{
//                                Log.d(TAG, "chua load xong conv");
//                            }
//                        }
//                    });
//                    mUserViewModel.getIsInited().removeObserver(this);
//                }
//            }
//        });
//    }

    public void init(LifecycleOwner lifecycleOwner){
        if(isInited.getValue() == null){
            mRepo = ConversationRepository.getInstance();
            convList = mRepo.getListConv();
            convList.observe(lifecycleOwner, new Observer<ArrayList<Conversation>>() {
                @Override
                public void onChanged(ArrayList<Conversation> conversations) {
                    if(conversations!=null){
                        isInited.setValue(true);
                        convList.removeObserver(this);
                    }
                }
            });
        }
    }

    public LiveData<Boolean> getIsInited() {
        return isInited;
    }

    public LiveData<ArrayList<Conversation>> getConvList() {
        return convList;
    }

    public void setIsInitedFalse(){
        this.isInited.postValue(false);
    }
}
