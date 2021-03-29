package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import data.models.User;
import data.models.UserLocation;
import data.models.UserRef;
import data.repositories.FriendsRepository;

public class FriendViewModel extends ViewModel {
    private final String TAG = "FriendViewModel";
    private final String FRIENDS_COLLECTION = "Friends";

    FriendsRepository repository;
    private MutableLiveData<List<UserRef>> friendsRefList;
    private MutableLiveData<List<User>> friendsList;
    private MutableLiveData<List<UserLocation>> friendLocationList;

    private MutableLiveData<List<Boolean>> isInited = new MutableLiveData<List<Boolean>>();
    private String hostUser;


    public void init(Context context, LifecycleOwner lifecycleOwner) {
        if (isInited.getValue() == null) {
            Log.d(TAG, "init: Runned");
            hostUser = new ViewModelProvider((FragmentActivity) context).get(UserViewModel.class).getHostUser().getValue().getUID();
            repository = FriendsRepository.getInstance(FRIENDS_COLLECTION, hostUser);
            friendsRefList = repository.getListUserReference();
            friendsList = repository.getListUser();
            friendLocationList = repository.getUserLocationList(lifecycleOwner);

            friendsRefList.observe(lifecycleOwner, new Observer<List<UserRef>>() {

                @Override
                public void onChanged(List<UserRef> userRefs) {
                   if (isInited.getValue() == null) {
                       ArrayList<Boolean> tmp = new ArrayList<Boolean>();
                       tmp.add(true);
                       isInited.postValue(tmp);
                   }
                   else {
                       isInited.getValue().add(true);
                       isInited.postValue(isInited.getValue());
                   }

                    friendsRefList.removeObserver(this);
                }
            });

            friendsList.observe(lifecycleOwner, new Observer<List<User>>() {

                @Override
                public void onChanged(List<User> friendList) {
                    if (isInited.getValue() == null) {
                        ArrayList<Boolean> tmp = new ArrayList<Boolean>();
                        tmp.add(true);
                        isInited.postValue(tmp);
                    }
                    else {
                        isInited.getValue().add(true);
                        isInited.postValue(isInited.getValue());
                    }

                    friendsList.removeObserver(this);
                }
            });

            repository.getAll();
        }
    }

    public MutableLiveData<List<Boolean>> getIsInited() {
        return isInited;
    }

    public MutableLiveData<List<UserLocation>> getFriendLocationList() {
        return friendLocationList;
    }

    public LiveData<List<UserRef>> getFriendsRefList() {
        Log.d(TAG, "getFriendsRefList: " + friendsRefList);
        return friendsRefList;
    }

    public LiveData<List<User>> getFriendsList() {
        Log.d(TAG, "getFriendsList: " + friendsList);
        return friendsList;
    }

}
