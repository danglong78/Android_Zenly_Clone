package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import UI.friend.AddFriendsFragmentCallback;
import data.models.User;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private List<User> list;
    private FirebaseStorage storage;
    private StorageReference ref;
    private String userUID;
    private AddFriendsFragmentCallback listener;
    private Context context;

    public FriendListAdapter(Context context, ArrayList<User> list){
        this.list = list;
        storage = FirebaseStorage.getInstance();
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if ( (prefs != null) && (prefs.contains("uid")) ) {
            userUID=prefs.getString("uid","");
        }
        this.listener=listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_item_layout, parent, false);

        return new FriendListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getUserNameTextView().setText(list.get(position).getName());

        ref= storage.getReference().child("avatars").child(list.get(position).getAvatarURL());
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getAvatar());

            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameText;
        private ImageView image;
        private Button  settingBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = (TextView) itemView.findViewById(R.id.userNameText);
            image=(ImageView) itemView.findViewById(R.id.avatar);
            settingBtn= (Button)itemView.findViewById(R.id.settingBtn);
        }
        public TextView getUserNameTextView() {
            return userNameText;
        }
        public ImageView getAvatar() {return image;}
        public Button getBtn(){return settingBtn;}
    }
}
