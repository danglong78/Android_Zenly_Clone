package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.Collection;
import java.util.List;

import data.models.Conversation;
import data.models.User;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> implements Filterable {

    private List<User> list,listAll;

    private FirebaseStorage storage;
    private StorageReference ref;
    private String userUID;
    private Context context;
    private FriendListCallback callback;

    public FriendListAdapter(Context context,FriendListCallback callback){
        this.list = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        if ( (prefs != null) && (prefs.contains("uid")) ) {
            userUID=prefs.getString("uid","");
        }
        this.callback = callback;
        listAll= new ArrayList<>(list);
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
        holder.getBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFriendSettingClick(list.get(position).getUID(),list.get(position).getName());
            }
        });
    }

    public void setUsers(ArrayList<User> newList) {
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
        listAll=new ArrayList<>(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<User>();
            if( constraint.toString().isEmpty())
                filteredList.addAll(listAll);
            else{
                for(User user:listAll)
                {
                    if(user.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredList.add(user);
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredList;
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<User>) results.values);
            notifyDataSetChanged();

        }
    };

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
    public interface FriendListCallback {
        public void onFriendSettingClick(String UID, String userName);
    }
}
