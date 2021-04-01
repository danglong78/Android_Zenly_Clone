package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendInvitingListAdapter;
import adapter.FriendListAdapter;
import data.models.User;
import viewModel.FriendViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;

public class SearchFriendFragment extends Fragment implements FriendInvitingListAdapter.InvitedListCallback, FriendListAdapter.FriendListCallback {
    private final String TAG = "SearchFriendFragment";
    private FriendViewModel friendViewModel;
    private InvitingViewModel invitingViewModel;
    private InvitationViewModel invitationViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FriendListAdapter adapter = new FriendListAdapter(requireActivity(),SearchFriendFragment.this);
        RecyclerView recyclerView = view.findViewById(R.id.friend_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        invitationViewModel = new ViewModelProvider(requireActivity()).get(InvitationViewModel.class);

        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        friendViewModel.getFriendsList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setUsers( (ArrayList<User>)users);
                adapter.notifyDataSetChanged();
            }
        });




//        List<User> invitedList= new ArrayList<>();
//        invitedList.add(new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null));
//        invitedList.add(new User(null,"Ho Dai Tri","123","0e974b5c-8978-11eb-8dcd-0242ac130003.png",null,null,null));
//        invitedList.add(new User(null,"Tran Thanh Tam","123","0e974d50-8978-11eb-8dcd-0242ac130003.png",null,null,null));
//        invitedList.add(new User(null,"Huynh Lam Hoang Dai","123","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",null,null,null));
//        FriendInvitingListAdapter adapter2= new FriendInvitingListAdapter(requireActivity(), (ArrayList<User>) invitedList,this);


        invitingViewModel = new ViewModelProvider(requireActivity()).get(InvitingViewModel.class);
        invitingViewModel.getInvitingList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG, "getInvitingList: users.size()" + users.size());
                FriendInvitingListAdapter adapter = new FriendInvitingListAdapter(requireActivity(), (ArrayList<User>) invitingViewModel.getInvitingList().getValue(), SearchFriendFragment.this);
                RecyclerView invitedRecyclerView = view.findViewById(R.id.invitedRecyclerView);
                invitedRecyclerView.setAdapter(adapter);
                invitedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter.notifyDataSetChanged();
            }
        });

        EditText searchInput= view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onInvitedUserSettingClick(String uid,String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What about "+userName+" ?");
        builder.setItems(new String[]{"Delete","Cancle"},new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    {
                        //TODO: Delete Invited User Function
                        invitingViewModel.removeMyInviting(uid);
                        invitationViewModel.removeFriendInvitation(uid);
                        break;
                    }
                    case 1:
                    {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        });
        builder.create().show();

    }

    @Override
    public void onFriendSettingClick(String UID, String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What about "+userName+" ?");
        builder.setItems(new String[]{"Delete","Cancle"},new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    {
                        //TODO: Delete Friend Function
                        friendViewModel.deleteFriend(UID);
                        break;
                    }
                    case 1:
                    {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        });
        builder.create().show();
    }
}