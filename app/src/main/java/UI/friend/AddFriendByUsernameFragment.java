package UI.friend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.study.android_zenly.R;

import adapter.ListFriendOfUserAdapter;
import data.models.User;
import viewModel.FriendViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;


public class AddFriendByUsernameFragment extends Fragment implements ListFriendOfUserAdapter.onClickUser {
    FriendViewModel friendViewModel;
    EditText searchInput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend_by_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListFriendOfUserAdapter adapter= new ListFriendOfUserAdapter(this,getActivity());
        RecyclerView friendListRecyclerView = view.findViewById(R.id.recyclerview);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendListRecyclerView.setAdapter(adapter);
        friendViewModel= new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        searchInput=view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //SEARCH FUNCTION
            }
        });

    }

    @Override
    public void onClickUser(User user, boolean isYourFriend) {
        Bundle bundle = new Bundle();
        bundle.putString("name",user.getName());
        bundle.putString("uid",user.getUID());
        bundle.putString("avatar",user.getAvatarURL());
        bundle.putString("phone",user.getPhone());
        NavController navController = Navigation.findNavController(this.getView());
        if(!isYourFriend)
        {
            navController.navigate(R.id.action_strangerProfileFragment2_to_friendProfileFragment2,bundle);
        }
        else{
            navController.navigate(R.id.action_strangerProfileFragment2_self,bundle);
        }

    }

    @Override
    public void onClickAdd(String UID) {
        InvitationViewModel invitationViewModel = new ViewModelProvider(getActivity()).get(InvitationViewModel.class);
        InvitingViewModel invitingViewModel = new ViewModelProvider(getActivity()).get(InvitingViewModel.class);
        invitationViewModel.sendInvitation(UID);
        invitingViewModel.addToMyInviting(UID);
    }
    @Override
    public void onPause() {
        super.onPause();
        friendViewModel.resetListFriendOfFriends();
    }
}