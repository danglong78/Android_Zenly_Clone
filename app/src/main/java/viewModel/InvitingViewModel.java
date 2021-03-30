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
import data.repositories.InvitationsRepository;
import data.repositories.InvitingRepository;

public class InvitingViewModel extends ViewModel {
    private final String TAG = "InvitingViewModel";
    private final String INVITING_COLLECTION = "Inviting";

    InvitingRepository repository;

    private MutableLiveData<List<UserRef>> invitingRefList;
    private MutableLiveData<List<User>> invitingList;

    private boolean checkInit = false;
    private String hostUser;

    public void init(Context context) {
        if (!checkInit) {
            Log.d(TAG, "init: Runned");
            hostUser = new ViewModelProvider((FragmentActivity) context).get(UserViewModel.class).getHostUser().getValue().getUID();
            repository = InvitingRepository.getInstance(INVITING_COLLECTION, hostUser);

            invitingRefList = repository.getListUserReference();
            invitingList = repository.getListUser();

            repository.getPagination();
            repository.listenPaginationChange();

            checkInit = true;
        }
    }

    public LiveData<List<UserRef>> getInvitingRefList() {
        Log.d(TAG, "getInvitationsRefList: " + invitingRefList);
        return invitingRefList;
    }

    public LiveData<List<User>> getInvitingList() {
        Log.d(TAG, "getInvitationsList: " + invitingList);
        return invitingList;
    }

    public void addToMyInviting(String UID) {
        Log.d(TAG, "addToInviting: ");
        repository.addToMyRepository(UID);
    }

    public void removeMyInviting(String UID) {
        repository.removeFromPaginations(UID, repository.getListUser());
    }

    public void removeFriendInviting(String UID) {
        repository.removeFromOtherRepository(UID);
    }
}
