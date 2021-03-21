package viewModel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import data.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final String TAG = "UserViewModel";


    UserRepository repository;
    MutableLiveData<User> mUser;
    CollectionReference friendsRef;

    private List<User> friendListTemp = new ArrayList<>();
    public MutableLiveData<List<User>> friendList;


    public UserViewModel(@NonNull Application application) {
        super(application);
        String uid = FirebaseAuth.getInstance().getUid();

        mUser = repository.getUserWithUID(uid);
        repository = UserRepository.getInstance();
        friendList = repository.getUserFriends(uid);
        friendList.setValue(friendListTemp);
        friendsRef = repository.getFriendsCollectionRef(uid);

        friendsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if(queryDocumentSnapshots != null) {
                    if (friendListTemp.isEmpty()) {
                        friendListTemp = queryDocumentSnapshots.toObjects(User.class);
                    }

                    else {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            User friend = doc.toObject(User.class);
                            if (!friendListTemp.contains(friend)) {
                                friendListTemp.add(friend);
                            }
                        }
                    }
                }
            }
        });
    }

    public LiveData<User> getHostUser() {
        return mUser;
    }

    public LiveData<List<User>> getFriendList() {
        return friendList;
    }
}
