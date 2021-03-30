package adapter;

import android.content.Context;
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


import data.models.User;

public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.ViewHolder> {
    ArrayList<User> list;
    Context context;
    BlockedUserCallback callback;

    public BlockedUserAdapter(Context context,BlockedUserCallback callback)
    {
        this.context=context;
        list= new ArrayList<User>();
        this.callback=callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blocked_user_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getUserNameTextView().setText(list.get(position).getName());

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(list.get(position).getAvatarURL());
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getAvatar());

            });

        }
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(list.get(position).getUID());
            }
        });
    }
    public void setListUsers(ArrayList<User> users) {
        this.list=users;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameText;
        private ImageView image;
        private View item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = (TextView) itemView.findViewById(R.id.userNameText);
            image=(ImageView) itemView.findViewById(R.id.avatar);
            this.item=itemView;

        }
        public TextView getUserNameTextView() {
            return userNameText;
        }
        public ImageView getAvatar() {return image;}
        public View getView(){
            return item;
        }
    }
    public interface BlockedUserCallback {
        void onClick(String UID);
    }
}
