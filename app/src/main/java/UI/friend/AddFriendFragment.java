package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import UI.MainActivity.HomeFragment;
import UI.MainActivity.MainActivity;
import adapter.FriendSuggestListAdapter;
import data.models.User;
import ultis.FragmentTag;
import viewModel.FriendSuggestViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;
import viewModel.UserViewModel;


public class AddFriendFragment extends Fragment implements FriendSuggestListAdapter.AddFriendsFragmentCallback {

    private RecyclerView friendSuggestRecyclerView;
    private FriendSuggestListAdapter adapter;
    private FriendSuggestViewModel friendSuggestViewModel;
    private InvitationViewModel invitationViewModel;
    private InvitingViewModel invitingViewModel;
    private NavController navController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);
        adapter = new FriendSuggestListAdapter(requireActivity(), this);
        friendSuggestRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendSuggestRecyclerView.setLayoutManager(layoutManager);

         navController = Navigation.findNavController(requireActivity(), R.id.friend_nav_host_fragment);

        friendSuggestViewModel = new ViewModelProvider(requireActivity()).get(FriendSuggestViewModel.class);
        invitationViewModel = new ViewModelProvider(requireActivity()).get(InvitationViewModel.class);
        invitingViewModel = new ViewModelProvider(requireActivity()).get(InvitingViewModel.class);
        friendSuggestViewModel.init(getActivity());
        invitationViewModel.init(getActivity());
        invitingViewModel.init(getActivity());
        friendSuggestViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setList((ArrayList<User>) users);
//                    adapter.notifyDataSetChanged();
            }
        });

        View addUsernameBtn= view.findViewById(R.id.addUsernameBtn);
        addUsernameBtn.setOnClickListener(v->{
            navController.navigate(R.id.action_addFriendFragment_to_addFriendByUsernameFragment);
        });
        View addPhoneBtn= view.findViewById(R.id.addPhoneBtn);
        addPhoneBtn.setOnClickListener(v->{
            navController.navigate(R.id.action_addFriendFragment_to_addFriendByPhoneFragment);
        });

    }

    @Override
    public void onAddButtonClick(String friendUID) {

        invitationViewModel.sendInvitation(friendUID);
        friendSuggestViewModel.hideSuggest(friendUID);
        invitingViewModel.addToMyInviting(friendUID);
    }

    @Override
    public void onHideClick(String suggestUID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want hide this one ?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                friendSuggestViewModel.hideSuggest(suggestUID);
            }
        });
        builder.create().show();
    }

    @Override
    public void onClickVIew(User user) {
        Bundle bundle= new Bundle();
        bundle.putString("name",user.getName());
        bundle.putString("phone",user.getPhone());
        bundle.putString("uid",user.getUID());
        bundle.putString("avatar",user.getAvatarURL());
        HomeFragment fragment = (HomeFragment) getParentFragment().getParentFragment();
        assert fragment != null;
        fragment.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
        ((MainActivity) getActivity()).setFragmentTag(FragmentTag.FRIEND, null, navController);
        navController.navigate(R.id.action_addFriendFragment_to_strangerProfileFragment2,bundle);
    }
}