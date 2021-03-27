package viewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import java.util.List;

import data.models.User;
import data.models.UserRef;
import data.repositories.SuggestFriendReposity;


public class FriendSuggestViewModel extends ViewModel {
    private final String TAG = "FriendSuggestViewModel";
    private final String SUGGESTIONS_COLLECTION = "Suggestions";

    SuggestFriendReposity repository;
    private MutableLiveData<List<UserRef>> suggestFriendRefList;
    private MutableLiveData<List<User>> suggestFriendList;

    private String hostUID;
    private String hostPhone;


    public void init(Context context){
        Log.d(TAG, "init: Runned");
        hostUID = new ViewModelProvider((FragmentActivity)context).get(UserViewModel.class).getHostUser().getValue().getUID();
        hostPhone = new ViewModelProvider((FragmentActivity)context).get(UserViewModel.class).getHostUser().getValue().getPhone();
        repository = SuggestFriendReposity.getInstance(SUGGESTIONS_COLLECTION,hostUID);
        repository.initContactSuggestFriendList(context, hostPhone);
        suggestFriendRefList = repository.getListUserReference();
        suggestFriendList = repository.getListUser();
        repository.getPaginations();
    }

    public LiveData<List<UserRef>> getSuggestFriendRefList() {
        Log.d(TAG, "getSuggestFriendList: " + suggestFriendRefList);
        return suggestFriendRefList;
    }

    public LiveData<List<User>> getSuggestFriendList(){
        Log.d(TAG, "getSuggestFriendList: " + suggestFriendList);
        return suggestFriendList;
    }

    public void hideSuggest(String suggestUID){
        repository.modify(suggestUID, true);
    }

    public void load(){
        repository.getPaginations();
    }
}