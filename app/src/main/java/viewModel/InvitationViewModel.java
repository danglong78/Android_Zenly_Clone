package viewModel;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import data.models.User;
import data.models.UserRef;
import data.repositories.InvitationsRepository;

public class InvitationViewModel extends ViewModel {
    private final String TAG = "InvitationViewModel";
    private final String INVITATIONS_COLLECTION = "Invitations";

    InvitationsRepository repository;

    private MutableLiveData<List<UserRef>> invitationsRefList;
    private MutableLiveData<List<User>> invitaionsList;

    private boolean checkInit = false;
    private String hostUser;



    public void init(Context context) {
        if (!checkInit) {
            Log.d(TAG, "init: Runned");
            hostUser = new ViewModelProvider((FragmentActivity) context).get(UserViewModel.class).getHostUser().getValue().getUID();
            repository = InvitationsRepository.getInstance(INVITATIONS_COLLECTION, hostUser);

            invitationsRefList = repository.getListUserReference();
            invitaionsList = repository.getListUser();

            repository.getPagination();
            repository.listenPaginationChange();

            checkInit = true;
        }
    }

    public LiveData<List<UserRef>> getInvitationsRefList() {
        Log.d(TAG, "getInvitationsRefList: " + invitationsRefList);
        return invitationsRefList;
    }

    public LiveData<List<User>> getInvitationsList() {
        Log.d(TAG, "getInvitationsList: " + invitaionsList);
        return invitaionsList;
    }

    public void sendInvitation(String UID) {
        Log.d(TAG, "sendInvitation: ");
        repository.addToOtherRepository(UID);
    }

    public void removeMyInvitation(String UID){
        Log.d(TAG, "deleteInvitation: ");
        repository.removeFromPaginations(UID, repository.getListUser());
    }

    public void removeFriendInvitation(String UID){
        Log.d(TAG, "removeInvitation: ");
        repository.removeFromOtherRepository(UID);
    }

    public void load(){
        repository.getPagination();
    }
}
