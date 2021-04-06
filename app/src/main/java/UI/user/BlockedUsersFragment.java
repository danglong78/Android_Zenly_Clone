package UI.user;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.BlockedUserAdapter;
import data.models.User;
import viewModel.FriendViewModel;


public class BlockedUsersFragment extends Fragment implements BlockedUserAdapter.BlockedUserCallback {
    FriendViewModel friendViewModel;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blocked_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);

        recyclerView= view.findViewById(R.id.friend_recycler_view);
        BlockedUserAdapter adapter = new BlockedUserAdapter(requireActivity(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        friendViewModel.getFriendsPreciseList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.setListUsers((ArrayList<User>) users);
            }
        });

    }

    @Override
    public void onClick(String UID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"Unblock","Cancle"},new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    {
                        //TODO: Unblocked User Function
                        friendViewModel.unBlockFriend(UID);
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