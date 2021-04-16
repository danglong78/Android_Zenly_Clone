package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;

import adapter.ListFriendOfUserAdapter;
import data.models.User;
import data.models.UserFriendList;
import viewModel.FriendViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;


public class StrangerProfileFragment extends Fragment implements ListFriendOfUserAdapter.onClickUser {
    FriendViewModel friendViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stranger_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView userName= view.findViewById(R.id.userName);
        userName.setText(getArguments().getString("name"));
        ImageView avatar = view.findViewById(R.id.avatar);
        StorageReference ref= FirebaseStorage.getInstance().getReference().child("avatars").child(getArguments().getString("avatar"));
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri->{
                String imageURL= uri.toString();
                Glide.with(requireContext())
                        .load(imageURL)
                        .into(avatar);
            });
        }


        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        ListFriendOfUserAdapter adapter= new ListFriendOfUserAdapter(this,getActivity());
        RecyclerView friendListRecyclerView = view.findViewById(R.id.recyclerview);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendListRecyclerView.setAdapter(adapter);
        friendViewModel.getFriendListOfFriends(getArguments().getString("uid")).observe(getViewLifecycleOwner(),userFriendLists -> {
            Log.d("StrangerProfileFragment", "callback: " + userFriendLists.size());

            adapter.setList((ArrayList<UserFriendList>) userFriendLists);
            Log.d("TB",String.valueOf(userFriendLists.size()));
        });


        Button settingBtn = view.findViewById(R.id.userSettingBtn);
        settingBtn.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString("name"));
            builder.setItems(new String[]{"Block user","Cancel"},new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                        {

                            //TODO: Block Friend Function
                            friendViewModel.blockFriend(getArguments().getString("uid"));
                            getActivity().onBackPressed();
                            dialog.dismiss();
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
        });

        Button addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> {
            friendViewModel.addFriend(getArguments().getString("uid"));
            addBtn.setText("INVITED");
            addBtn.setActivated(false);

        });
        if(getArguments().getString("type") == "INVITED")
        {
            addBtn.setText("INVITED");
            ((MaterialButton)addBtn).setIconResource(R.drawable.ic_baseline_check_24);
            addBtn.setActivated(false);
        }
        else if(getArguments().getString("type") == "PENDING")
        {
            addBtn.setText("PENDING");
            ((MaterialButton)addBtn).setIconResource(R.drawable.ic_baseline_check_24);

            addBtn.setActivated(false);
        }
        else if (getArguments().getString("type") == "ME"){
            addBtn.setVisibility(View.GONE);
            settingBtn.setVisibility(View.GONE);
        }




    }

    @Override
    public void onClickUser(User user, boolean isYourFriend) {
        Bundle bundle = new Bundle();
        bundle.putString("name",user.getName());
        bundle.putString("uid",user.getUID());
        bundle.putString("avatar",user.getAvatarURL());
        bundle.putString("phone",user.getPhone());
        bundle.putString("type",friendViewModel.checkUserTag(user.getUID()));
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
        friendViewModel.addFriend(UID);
    }
    @Override
    public void onPause() {
        super.onPause();
        friendViewModel.resetListFriendOfFriends();
    }
}