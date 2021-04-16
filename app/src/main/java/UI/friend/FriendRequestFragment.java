package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendListAdapter;
import adapter.FriendRequestAdapter;
import data.models.User;
import data.repositories.ConversationRepository;
import data.repositories.UserRepository;
import viewModel.FriendSuggestViewModel;
import viewModel.FriendViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;


public class FriendRequestFragment extends Fragment implements FriendRequestAdapter.FriendRequestCallback {

    private InvitationViewModel invitationViewModel;
    private FriendViewModel friendViewModel;
    private InvitingViewModel invitingViewModel;
    private FriendSuggestViewModel friendSuggestViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        invitingViewModel = new ViewModelProvider(requireActivity()).get(InvitingViewModel.class);
        invitationViewModel = new ViewModelProvider(requireActivity()).get(InvitationViewModel.class);
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        friendSuggestViewModel = new ViewModelProvider(requireActivity()).get(FriendSuggestViewModel.class);
        FriendRequestAdapter adapter = new FriendRequestAdapter(requireActivity(), FriendRequestFragment.this);
        RecyclerView recyclerView = view.findViewById(R.id.friend_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        invitationViewModel.getInvitationsList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                 adapter.setUsers((ArrayList<User>) users);

            }
        });


    }

    @Override
    public void onAddButtonClick(String friendUID) {
        //TODO ACCEPT FRIEND REQUEST FUNCTION
        friendViewModel.acceptFriendRequest(friendUID);

        //TODO CREATE A CONVERSATION WITH NEW FRIEND
        LiveData<String> convID = ConversationRepository.getInstance().createNewConv(FirebaseAuth.getInstance().getUid(),friendUID);
        convID.observe(getViewLifecycleOwner(),new Observer<String>() {
            @Override
            public void onChanged(String convId) {
                if(convId!=null){
                    if(!convId.equals("no")){
                        UserRepository.getInstance().addConv(FirebaseAuth.getInstance().getUid(),convId);
                        UserRepository.getInstance().addConv(friendUID,convId);
                    }

                }
            }
        });
    }

    @Override
    public void onDelButtonClick(String friendUID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want delete this one ?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO DELETE FRIEND REQUEST FUNCTION
                friendViewModel.deleteFriendRequest(friendUID);
            }
        });
        builder.create().show();
    }
}