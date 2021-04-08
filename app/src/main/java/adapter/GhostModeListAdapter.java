package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import data.models.User;

public class GhostModeListAdapter extends RecyclerView.Adapter<GhostModeListAdapter.ViewHolder> {
    private List<User> list;
    private Context context;
    private GhostModeCallback callback;
    public GhostModeListAdapter(Context context,GhostModeCallback callback){
        list=new ArrayList<User>();
        this.context=context;
        this.callback=callback;

    }
    public void addUser(List<User> userList){
        for(User user: userList) {
            if(!list.contains(user)){
                list.add(user);
                notifyItemInserted(list.size()-1);
            }
        }
    }

    public void deleteUser(List<User> userList){
        for(User user: userList) {
            if(list.contains(user)){
                list.remove(user);
            }
        }
        notifyDataSetChanged();
    }
    public void updateListItems(List<User> newList) {
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
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new GhostModeListAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_checkbox_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageReference ref= FirebaseStorage.getInstance().getReference().child("avatars").child(list.get(position).getAvatarURL());
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getAvatar());

            });
        }
        holder.itemView.setOnClickListener(v->{
            callback.onClickCheckbox(list.get(position),holder.getCheckbox());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageView avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox=itemView.findViewById(R.id.checkbox);
            avatar=itemView.findViewById(R.id.avatar);
        }


        public ImageView getAvatar() {
            return avatar;
        }
        public CheckBox getCheckbox(){
            return checkbox;
        }
        public void unCheck(){
            checkbox.setChecked(false);
        }


    }
    public interface GhostModeCallback {
        void onClickCheckbox(User user,CheckBox checkbox);

    }
}
