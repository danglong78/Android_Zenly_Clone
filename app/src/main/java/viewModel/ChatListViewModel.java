package viewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import data.models.Conversation;
import data.models.User;
import data.repositories.ConversationRepository;
import data.repositories.UserRepository;

public class ChatListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Conversation>> convList;
    private ConversationRepository mRepo;
    public void init(UserViewModel mUserViewModel) {
        if(convList!=null){
            return;
        }
        User aUser = mUserViewModel.getHostUser().getValue();
        mRepo = ConversationRepository.getInstance();
        ArrayList<Conversation> conv = new ArrayList<Conversation>();
        for(String s : aUser.getConversation()){
            conv.add(mRepo.getConversation(s).getValue());
        }
        convList.postValue(conv);
    }

    public MutableLiveData<ArrayList<Conversation>> getConvList() {
        return convList;
    }
}
