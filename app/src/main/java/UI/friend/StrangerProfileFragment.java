package UI.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import adapter.ListFriendOfUserAdapter;
import viewModel.FriendViewModel;


public class StrangerProfileFragment extends Fragment implements ListFriendOfUserAdapter.onClickUser {


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
        RecyclerView friendListRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        //        ListFriendOfUserAdapter adapter= new ListFriendOfUserAdapter();
    }

    @Override
    public void onClickUser(String UID, boolean isYourFriend) {

    }
}