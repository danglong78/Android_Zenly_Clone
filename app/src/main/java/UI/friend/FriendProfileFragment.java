    package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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


public class FriendProfileFragment extends Fragment implements ListFriendOfUserAdapter.onClickUser{
    FriendViewModel friendViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView userName= view.findViewById(R.id.userName);
        userName.setText(getArguments().getString("name"));
        ImageView avatar = view.findViewById(R.id.avatar);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(getArguments().getString("avatar"));

        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri->{
                String imageURL= uri.toString();
                Glide.with(requireContext())
                        .load(imageURL)
                        .into(avatar);
            });
        }
        RecyclerView friendListRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        ListFriendOfUserAdapter adapter= new ListFriendOfUserAdapter(this,requireActivity());
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendListRecyclerView.setAdapter(adapter);
        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        friendViewModel.getFriendListOfFriends(getArguments().getString("uid")).observe(getViewLifecycleOwner(),userFriendLists -> {
            adapter.setList((ArrayList<UserFriendList>) userFriendLists);

        });
        Button settingBtn = view.findViewById(R.id.userSettingBtn);
        settingBtn.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString("name"));
            builder.setItems(new String[]{"Delete friend","Block","Cancel"},new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                        {
                            dialog.dismiss();
                            friendViewModel.deleteFriend(getArguments().getString("uid"));
                            break;
                        }
                        case 1:
                        {
                            dialog.dismiss();
                            friendViewModel.blockFriend(getArguments().getString("uid"));
                            break;
                        }
                        case 2:
                        {
                            dialog.dismiss();
                            break;
                        }
                    }
                }
            });
            builder.create().show();
        });

        Button callBtn = view.findViewById(R.id.callBtn);
        callBtn.setOnClickListener(v->{
            String uri;
            if(getArguments().getString("phone")==null)
            {
                uri= "tel:" + "" ;
            }
            else
                uri= "tel:" + getArguments().getString("phone").trim() ;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        });

        Button ghostModeButton = view.findViewById(R.id.GhostModeBtn);
        ghostModeButton.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Change what "+getArguments().getString("name")+" can see");
            builder.setItems(new String[]{"Precise location","Frozen location","Cancel"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                    {
                        //TODO: SET PRECISE LOCATION
                        dialog.dismiss();
                        friendViewModel.turnOffFrozen(getArguments().getString("uid"));
                        break;
                    }
                    case 1:
                    {
                        //TODO: SET FROZEN LOCATION
                        dialog.dismiss();
                        friendViewModel.turnOnFrozen(getArguments().getString("uid"));
                        break;

                    }
                    case 2:
                    {
                        dialog.dismiss();
                        break;
                    }
                }
            });
            builder.create().show();
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
            navController.navigate(R.id.action_friendProfileFragment2_to_strangerProfileFragment2,bundle);
        }
        else{
            navController.navigate(R.id.action_friendProfileFragment2_self,bundle);
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