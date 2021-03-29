package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendListAdapter;
import adapter.FriendRequestAdapter;
import data.models.User;


public class FriendRequestFragment extends Fragment implements FriendRequestAdapter.FriendRequestCallback {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<User> list= new ArrayList<>();
        list.add(new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Ho Dai Tri","123","0e974b5c-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Tran Thanh Tam","123","0e974d50-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Huynh Lam Hoang Dai","123","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",null,null,null));
        FriendRequestAdapter adapter= new FriendRequestAdapter(requireActivity(), (ArrayList<User>) list,this);
        RecyclerView recyclerView = view.findViewById(R.id.friend_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onAddButtonClick(String FriendUID) {
        //TODO ACCEPT FRIEND REQUEST FUNCTION
    }

    @Override
    public void onDelButtonClick(String FriendUID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want delete this one ?");
        builder.setNegativeButton("No",null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //TODO DELETE FRIEND REQUEST FUNCTION

            }
        });
        builder.create().show();
    }
}