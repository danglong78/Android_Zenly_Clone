package viewModel;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.models.User;
import data.models.UserFriendList;
import data.models.UserLocation;
import data.repositories.UserRepository;

public class UserViewModel extends ViewModel {
    private final String TAG = "UserViewModel";

    UserRepository repository;
     private static MutableLiveData<User> mUser = new MutableLiveData<User>();


    CollectionReference friendsRef;
    private List<User> friendListTemp = new ArrayList<>();
    public MutableLiveData<List<User>> friendList;

    MutableLiveData<Boolean> isInited = new MutableLiveData<Boolean>();


    public void init(Context context, LifecycleOwner lifecycleOwner) {
        if (isInited.getValue() == null) {
            repository = UserRepository.getInstance();

            mUser = repository.getHostUser(context);
            mUser.observe(lifecycleOwner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    Log.d(TAG, "onChanged: user and user location are inited");
                    Log.d(TAG, "onChanged: " + mUser.getValue().toString());
                    isInited.postValue(true);

                    mUser.removeObserver(this);
                }
            });
        }

//        mUser.observe(lifecycleOwner, new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                friendList = repository.getUserFriends(mUser.getValue().getUID());
//                friendList.setValue(friendListTemp);
//                friendsRef = repository.getFriendsCollectionRef(mUser.getValue().getUID());
//
//                friendsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.e(TAG, "onEvent: Listen failed.", e);
//                            return;
//                        }
//
//                        if(queryDocumentSnapshots != null) {
//                            if (friendListTemp.isEmpty()) {
//                                friendListTemp = queryDocumentSnapshots.toObjects(User.class);
//                            }
//
//                            else {
//                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//
//                                    User friend = doc.toObject(User.class);
//                                    if (!friendListTemp.contains(friend)) {
//                                        friendListTemp.add(friend);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//
//                mUser.removeObserver(this);
//            }
//        });
    }

    public LiveData<Boolean> getIsInited() {
        return isInited;
    }

    public LiveData<User> getHostUser() {
        return mUser;
    }
    public DocumentReference getUserReference(String UID) {
        return repository.getUserReference(UID);
    }

    public LiveData<List<User>> getFriendList() {
        return friendList;
    }


    public LiveData<ArrayList<String>> getConvList() {
        MutableLiveData<ArrayList<String>> res = null;
        res.postValue(getHostUser().getValue().getConversation());
        return res;
    }
    public Task<Void> setName(String name){
        return repository.setName(getHostUser().getValue().getUID(),name);
    }
    public Task<Void> setDob(String dob){
        return repository.setDob(getHostUser().getValue().getUID(),dob);
    }
    public void setAvatarURL(Uri file){
        repository.setAvatarURL(getHostUser().getValue().getUID(),file);
    }
    public void setAvatarURL(Bitmap file){
        repository.setAvatarURL(getHostUser().getValue().getUID(),file);
    }
    public Task <QuerySnapshot> getUserWithPhone(String phone){
        return repository.getUserWithPhone(phone);
    }

    public LiveData<List<UserFriendList>> getSearchList(String name) {
        return repository.getUserWithName(name, mUser.getValue().getUID());
    }

    public void resetSearchList(){
        repository.resetSearchList();
    }
}
