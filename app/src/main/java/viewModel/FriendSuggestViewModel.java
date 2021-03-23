package viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.List;

import data.models.User;
import data.repositories.SuggestFriendReposity;


public class FriendSuggestViewModel extends AndroidViewModel {
    private final String TAG = "FriendSuggestViewModel";

    SuggestFriendReposity repository;
    private MutableLiveData<List<User>> suggestFriendList;

    public FriendSuggestViewModel(@NonNull Application application){
        super(application);

        repository = SuggestFriendReposity.getInstance();
        suggestFriendList = repository.getSuggestFriendList(application);
    }


    public LiveData<List<User>> getSuggestFriendList() {
        return suggestFriendList;
    }

}