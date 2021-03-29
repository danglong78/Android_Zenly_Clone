package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import data.models.User;
import data.models.UserRef;
import data.repositories.FriendsRepository;

public class FriendViewModel extends ViewModel {
    private final String TAG = "FriendViewModel";
    private final String FRIENDS_COLLECTION = "Friends";

    FriendsRepository repository;
    private MutableLiveData<List<UserRef>> friendsRefList;
    private MutableLiveData<List<User>> friendsList;

    private boolean checkInit = false;
    private String hostUser;


    public void init(Context context) {
        if (!checkInit) {
            Log.d(TAG, "init: Runned");
            hostUser = new ViewModelProvider((FragmentActivity) context).get(UserViewModel.class).getHostUser().getValue().getUID();
            repository = FriendsRepository.getInstance(FRIENDS_COLLECTION, hostUser);
            friendsRefList = repository.getListUserReference();
            friendsList = repository.getListUser();
            repository.getAll();
            checkInit = true;
        }
    }

    public LiveData<List<UserRef>> getFriendsRefList() {
        Log.d(TAG, "getFriendsRefList: " + friendsRefList);
        return friendsRefList;
    }

    public LiveData<List<User>> getFriendsList() {
        Log.d(TAG, "getFriendsList: " + friendsList);
        return friendsList;
    }

    public void acceptFriendRequest(String friendUID){
        repository.addToOtherRepository(friendUID);
        repository.addToMyRepository(friendUID);
    }

    public void deleteFriend(String friendUID){
        repository.removeFromMyRepository(friendUID);
        repository.removeFromOtherRepository(friendUID);
    }
}
