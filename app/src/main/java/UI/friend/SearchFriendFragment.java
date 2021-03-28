package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendInvitingListAdapter;
import adapter.FriendListAdapter;
import data.models.User;

public class SearchFriendFragment extends Fragment implements FriendInvitingListAdapter.InvitedListCallback, FriendListAdapter.FriendListCallback {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<User> list= new ArrayList<>();
        list.add(new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Ho Dai Tri","123","0e974b5c-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Tran Thanh Tam","123","0e974d50-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Huynh Lam Hoang Dai","123","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",null,null,null));
        FriendListAdapter adapter= new FriendListAdapter(requireActivity(), (ArrayList<User>) list,this);
        RecyclerView recyclerView = view.findViewById(R.id.friend_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<User> invitedList= new ArrayList<>();
        invitedList.add(new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        invitedList.add(new User(null,"Ho Dai Tri","123","0e974b5c-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        invitedList.add(new User(null,"Tran Thanh Tam","123","0e974d50-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        invitedList.add(new User(null,"Huynh Lam Hoang Dai","123","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",null,null,null));
        FriendInvitingListAdapter adapter2= new FriendInvitingListAdapter(requireActivity(), (ArrayList<User>) invitedList,this);
        RecyclerView invitedRecyclerView = view.findViewById(R.id.invitedRecyclerView);
        invitedRecyclerView.setAdapter(adapter2);
        invitedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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