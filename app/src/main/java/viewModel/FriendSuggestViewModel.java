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

    SuggestFriendReposity repository;
    private MutableLiveData<List<UserRef>> suggestFriendRefList;
    private MutableLiveData<List<User>> suggestFriendList;


    public void init(Context context, String UID){
        repository = SuggestFriendReposity.getInstance(UID);
        repository.initContactSuggestFriendList(context, new ViewModelProvider((FragmentActivity)context).get(UserViewModel.class).getHostUser().getValue().getPhone());
        suggestFriendRefList = repository.getSuggestFriendRefList();
        suggestFriendList = repository.getSuggestFriendList();
    }


    public LiveData<List<UserRef>> getSuggestFriendRefList() {
        Log.d(TAG, "getSuggestFriendList: " + suggestFriendRefList);
        return suggestFriendRefList;
    }

    public LiveData<List<User>> getSuggestFriendList(){
        Log.d(TAG, "getSuggestFriendList: " + suggestFriendList);
        return suggestFriendList;
    }
}