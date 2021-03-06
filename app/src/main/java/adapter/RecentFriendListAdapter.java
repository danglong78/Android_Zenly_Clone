package adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import data.models.User;

public class RecentFriendListAdapter extends RecyclerView.Adapter<RecentFriendListAdapter.ViewHolder>{
    ArrayList<User> list;
    Callback callback;
    Context context;
    public RecentFriendListAdapter(Context context,Callback callback) {
        list=new ArrayList<User>();
        this.context=context;
        this.callback=callback;

    }

    @NonNull
    @Override
    public RecentFriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_friend_list_item_layout, parent, false);

        return new RecentFriendListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecentFriendListAdapter.ViewHolder holder, int position) {
        Log.d("RecentAdapter",list.get(position).getName());
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(list.get(position).getAvatarURL());
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getImage());

            });
        }
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(list.get(position).getUID());
            }
        });
        holder.getView().setOnLongClickListener(v->{
            callback.onLongClick(list.get(position),holder.getisDirectionMode(),holder);
            return true;
        });
    }
    public void setList(ArrayList<User>newList){
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
                User y = newList.get(newItemPosition);
                return x.getUID().equals(y.getUID());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final User x = list.get(oldItemPosition);
                final User y = newList.get(newItemPosition);

                return x.getName().equals(y.getName()) && x.getAvatarURL().equals(y.getAvatarURL());
            }
        };
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        list.clear();
        list.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        notifyDataSetChanged();

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        Log.d("RecentAdapter","change");
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private View item;
        private boolean isDirectionMode=false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=(ImageView) itemView.findViewById(R.id.avatar);
            item=itemView;
        }
        public ImageView getImage(){
            return image;
        }
        public View getView() {
            return item;
        }

        public boolean getisDirectionMode() {
            return isDirectionMode;
        }
        public void setDirectionMode(boolean isDirectionMode) {
            this.isDirectionMode = isDirectionMode;
        }
    }
    public interface Callback {
        void onClick(String UID);
        void onLongClick(User user,boolean isDirectionMode,ViewHolder viewHolder);

    }
}
