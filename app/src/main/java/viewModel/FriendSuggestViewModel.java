package viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.List;

import data.models.User;
import data.repositories.SuggestFriendReposity;


public class FriendSuggestViewModel extends ViewModel {
    private final String TAG = "FriendSuggestViewModel";

    SuggestFriendReposity repository;
    private MutableLiveData<List<User>> suggestFriendList;


    public void init(Context context){
        repository = SuggestFriendReposity.getInstance();
        suggestFriendList = repository.getSuggestFriendList(context);
    }


    public LiveData<List<User>> getSuggestFriendList() {
        Log.d(TAG, "getSuggestFriendList: " + suggestFriendList);
        return suggestFriendList;
    }

}