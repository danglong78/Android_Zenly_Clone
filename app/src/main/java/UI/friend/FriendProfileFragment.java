package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ListFriendOfUserAdapter;
import data.models.User;
import data.models.UserFriendList;
import viewModel.FriendViewModel;


public class FriendProfileFragment extends Fragment implements ListFriendOfUserAdapter.onClickUser {
    FriendViewModel friendViewModel;
    ListFriendOfUserAdapter adapter;
    LiveData<List<UserFriendList>> u;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView userName = view.findViewById(R.id.userName);
        userName.setText(getArguments().getString("name"));
        ImageView avatar = view.findViewById(R.id.avatar);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(getArguments().getString("avatar"));

        if (ref != null) {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                Glide.with(requireContext())
                        .load(imageURL)
                        .into(avatar);
            });
        }
        RecyclerView friendListRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        adapter = new ListFriendOfUserAdapter(this, requireActivity());
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendListRecyclerView.setAdapter(adapter);
        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);

        u = friendViewModel.getFriendListOfFriends(getArguments().getString("uid"));
        u.observe(getViewLifecycleOwner(), new Observer<List<UserFriendList>>() {
            @Override
            public void onChanged(List<UserFriendList> userFriendLists) {
                Log.d("StrangerProfileFragment", "callback: " + userFriendLists.size());
                friendViewModel.setFriendListOfFriends((ArrayList<UserFriendList>) userFriendLists);
                adapter.setList((ArrayList<UserFriendList>) userFriendLists);
                Log.d("TB", String.valueOf(userFriendLists.size()));
            }
        });

        Button settingBtn = view.findViewById(R.id.userSettingBtn);
        settingBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString("name"));
            builder.setItems(new String[]{"Delete friend", "Block", "Cancel"}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: {
                            friendViewModel.deleteFriend(getArguments().getString("uid"));
                            Bundle bundle = new Bundle(getArguments());
                            bundle.putString("type", friendViewModel.checkUserTag(getArguments().getString("uid")));
                            NavController navController = Navigation.findNavController(FriendProfileFragment.this.getView());
                            navController.navigate(R.id.action_friendProfileFragment2_to_strangerProfileFragment2, bundle);

                            dialog.dismiss();
                            break;
                        }
                        case 1: {

                            friendViewModel.blockFriend(getArguments().getString("uid"));
                            getActivity().onBackPressed();
                            dialog.dismiss();
                            break;
                        }
                        case 2: {
                            dialog.dismiss();
                            break;
                        }
                    }
                }
            });
            builder.create().show();
        });

        Button callBtn = view.findViewById(R.id.callBtn);
        callBtn.setOnClickListener(v -> {
            String uri;
            if (getArguments().getString("phone") == null) {
                uri = "tel:" + "";
            } else
                uri = "tel:" + getArguments().getString("phone").trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        });

        Button ghostModeButton = view.findViewById(R.id.GhostModeBtn);
        ghostModeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Change what " + getArguments().getString("name") + " can see");
            builder.setItems(new String[]{"Precise location", "Frozen location", "Cancel"}, (dialog, which) -> {
                switch (which) {
                    case 0: {
                        //TODO: SET PRECISE LOCATION
                        dialog.dismiss();
                        friendViewModel.turnOffFrozen(getArguments().getString("uid"));
                        break;
                    }
                    case 1: {
                        //TODO: SET FROZEN LOCATION
                        dialog.dismiss();
                        friendViewModel.turnOnFrozen(getArguments().getString("uid"));
                        break;

                    }
                    case 2: {
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
        String type =friendViewModel.checkUserTag(getArguments().getString("uid"));
        if (type.compareTo("BLOCKEDBY")!=0 && type.compareTo("BLOCK")!=0) {
            {
                Bundle bundle = new Bundle();
                bundle.putString("name", user.getName());
                bundle.putString("uid", user.getUID());
                bundle.putString("avatar", user.getAvatarURL());
                bundle.putString("phone", user.getPhone());
                bundle.putString("type", type);
                NavController navController = Navigation.findNavController(this.getView());
                if (type.compareTo("FRIEND") == 0) {
                    navController.navigate(R.id.action_friendProfileFragment2_to_strangerProfileFragment2, bundle);
                } else {
                    navController.navigate(R.id.action_friendProfileFragment2_self, bundle);
                }
            }
        }
    }

    @Override
    public void onClickAdd(String UID) {
        friendViewModel.addFriend(UID);
    }

    @Override
    public void onPause() {
        super.onPause();
        u.removeObservers(getViewLifecycleOwner());
        friendViewModel.resetListFriendOfFriends();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("StrangerProfileFragment", "onResume: chay roi " + friendViewModel.getFriendListOfFriends().size());
        adapter.setList(friendViewModel.getFriendListOfFriends());
        adapter.notifyDataSetChanged();
    }
}