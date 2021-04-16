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
import data.models.UserFriendList;
import data.models.UserLocation;
import data.models.UserRef;
import data.models.UserRefFriend;
import data.repositories.BlockRepository;
import data.repositories.BlockedByRepository;
import data.repositories.FriendsRepository;
import data.repositories.InvitingRepository;
import data.repositories.SuggestFriendRepository;

public class FriendViewModel extends ViewModel {
    private final String TAG = "FriendViewModel";
    private final String FRIENDS_COLLECTION = "Friends";
    private final String BLOCK_COLLECTION = "Block";
    private final String SUGGESTIONS_COLLECTION = "Suggestions";

    FriendsRepository repository;
    private MutableLiveData<List<UserRefFriend>> friendsRefList;
    private MutableLiveData<List<User>> friendsList;
    private MutableLiveData<List<UserLocation>> friendLocationList;
    private MutableLiveData<List<Boolean>> isInited = new MutableLiveData<List<Boolean>>();
    private MutableLiveData<List<User>> friendsPreciseList;
    private MutableLiveData<List<User>> friendsFrozenList;
    private MutableLiveData<UserLocation> userDirection;

        BlockRepository blockRepository;
    private MutableLiveData<List<User>> blockList;

    BlockedByRepository blockedByRepository;
    private MutableLiveData<List<User>> blockedByList;

    SuggestFriendRepository suggestRepository;
    InvitingRepository invitingRepository;

    private String hostUserUID;


    public void init(Context context, LifecycleOwner lifecycleOwner) {
        if (isInited.getValue() == null) {
            Log.d(TAG, "init: Runned");
            hostUserUID = new ViewModelProvider((FragmentActivity) context).get(UserViewModel.class).getHostUser().getValue().getUID();
            repository = FriendsRepository.getInstance(FRIENDS_COLLECTION, hostUserUID);
            friendsRefList =  (MutableLiveData<List<UserRefFriend>>) repository.getListUserReference();
            friendsList = repository.getListUser();
            friendLocationList = repository.getUserLocationList(lifecycleOwner);
            friendsPreciseList = repository.getUserPreciseList();
            friendsFrozenList = repository.getUserFrozenList();
            repository.getAll();

            friendsRefList.observe(lifecycleOwner, new Observer<List<UserRefFriend>>() {

                @Override
                public void onChanged(List<UserRefFriend> userRefs) {
                    Log.d(TAG, "onChanged: friendsRefList");
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

            blockRepository = BlockRepository.getInstance(BLOCK_COLLECTION, hostUserUID);
            blockList = blockRepository.getListUser();
            blockRepository.getAll();

            blockedByRepository = BlockedByRepository.getInstance(BLOCK_COLLECTION, hostUserUID);
            blockedByList = blockedByRepository.getListUser();
            blockRepository.getAll();

            suggestRepository = SuggestFriendRepository.getInstance(SUGGESTIONS_COLLECTION, hostUserUID);

            userDirection = repository.getUserDirection("");
        }
    }

    public MutableLiveData<List<Boolean>> getIsInited() {
        return isInited;
    }

    public MutableLiveData<List<UserLocation>> getFriendLocationList() {
        return friendLocationList;
    }

    public LiveData<List<UserRefFriend>> getFriendsRefList() {
        Log.d(TAG, "getFriendsRefList: " + friendsRefList);
        return friendsRefList;
    }

    public LiveData<List<User>> getFriendsList() {
        Log.d(TAG, "getFriendsList: " + friendsList);
        return friendsList;
    }

    public LiveData<List<User>> getFriendsPreciseList(){
        Log.d(TAG, "getFriendsPreciseList: " + friendsPreciseList);
        return friendsPreciseList;
    }

    public LiveData<List<User>> getFriendsFrozenList(){
        Log.d(TAG, "getFriendsPreciseList: " + friendsFrozenList);
        return friendsFrozenList;
    }

    public void acceptFriendRequest(String friendUID){
        repository.addToOtherRepository(friendUID);
        repository.addToMyRepository(friendUID);

    }

    public void deleteFriend(String friendUID){
        repository.removeFromMyRepository(friendUID);
        repository.removeFromOtherRepository(friendUID);
    }

    public void turnOnFrozen(String friendUID) {
        Log.d(TAG, "turnOnFrozen: hostUserID " + hostUserUID);
        Log.d(TAG, "turnOnFrozen: friend " + friendUID);
        repository.toggleFrozen(hostUserUID, friendUID, true);
    }

    public void turnOffFrozen(String friendUID) {
        Log.d(TAG, "turnOnFrozen: hostUserID " + hostUserUID);
        Log.d(TAG, "turnOnFrozen: friend " + friendUID);
        repository.toggleFrozen(hostUserUID, friendUID, false);
    }

    public LiveData<List<User>> getBlockList(){
        return blockList;
    }

    public LiveData<List<User>> getBlockedByList() {
        return blockedByList;
    }

    public void blockFriend(String friendUID){
        blockRepository.addToMyRepository(friendUID);
        blockedByRepository.addToOtherRepository(friendUID);
        suggestRepository.modify(friendUID, true);
        repository.removeFromMyRepository(friendUID);
    }

    public void unBlockFriend(String friendUID){
        blockRepository.removeFromMyRepository(friendUID);
        blockedByRepository.removeFromOtherRepository(friendUID);
        suggestRepository.modify(friendUID, false);
    }

    public LiveData<List<UserFriendList>> getFriendListOfFriends(String friendUID){
        return repository.getFriendListOfFriends(friendUID);
    }

    public void resetListFriendOfFriends(){
        repository.resetFriendListOfFriends();
    }

    public LiveData<UserLocation> getUserDirection(String friendUID){
        return repository.getUserDirection(friendUID);
    }

    public void offUserDirection(){
        repository.offUserDirection();
    }

    public LiveData<UserRefFriend> getUserRefFriend(String friendUID){
        return repository.getUserRefFriend(friendUID);
    }
}
