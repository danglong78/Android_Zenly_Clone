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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;


import data.models.User;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    private ArrayList<User> list;
    private FirebaseStorage storage;
    private StorageReference ref;
    private String userUID;
    private FriendRequestCallback callback;
    private Context context;

    public FriendRequestAdapter(Context context,FriendRequestCallback callback){
        this.list=new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if ( (prefs != null) && (prefs.contains("uid")) ) {
            userUID=prefs.getString("uid","");
        }
        this.callback= callback;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_request_list_item_layout, parent, false);

        return new FriendRequestAdapter.ViewHolder(view);
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
        holder.getAddBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddButtonClick(list.get(position).getUID());
            }
        });
        holder.getDelBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDelButtonClick(list.get(position).getUID());
            }
        });
    }
    public void setUsers(ArrayList<User> newList)
    {
        DiffUtil.Callback diffUtilCallback= new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return list.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                User x=list.get(oldItemPosition);
                User y = list.get(newItemPosition);
                return x.equals(y);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final User x = list.get(oldItemPosition);
                final User y = newList.get(newItemPosition);

                return x.getUID().equals(y.getUID());
            }
        };
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        list.clear();
        list.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameText;
        private ImageView image;
        private Button acceptBtn,delBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = (TextView) itemView.findViewById(R.id.userNameText);
            image=(ImageView) itemView.findViewById(R.id.avatar);
            acceptBtn= (Button)itemView.findViewById(R.id.addBtn);
            delBtn = (Button)itemView.findViewById(R.id.deleteBtn);
        }
        public TextView getUserNameTextView() {
            return userNameText;
        }
        public ImageView getAvatar() {return image;}
        public Button getAddBtn(){return acceptBtn;}
        public Button getDelBtn(){return delBtn;}

    }
    public interface FriendRequestCallback {
        public void onAddButtonClick(String FriendUID);
        public void onDelButtonClick(String FriendUID);
    }
}
