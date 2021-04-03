package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

import data.models.User;

public class FriendSuggestListAdapter  extends RecyclerView.Adapter<FriendSuggestListAdapter.ViewHolder>{
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private ArrayList<User> list ;
    private FirebaseStorage storage;
    private StorageReference ref;
    private String userUID;
    private AddFriendsFragmentCallback listener;
    private Context context;


    public FriendSuggestListAdapter(Context context,AddFriendsFragmentCallback listener){
        this.list = new ArrayList<>();
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
    public FriendSuggestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_friend_list_item_layout, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull FriendSuggestListAdapter.ViewHolder holder, int position) {
        if(!(holder instanceof ProgressHolder)) {
            holder.getUserNameTextView().setText(list.get(position).getName());

            ref = storage.getReference().child("avatars").child(list.get(position).getAvatarURL());
            if (ref != null) {
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
                    listener.onAddButtonClick(list.get(position).getUID());
                }

            });
            holder.getDeleteIcon().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("AdapterFriendSuggest", "onClick: ");
                    listener.onHideClick(list.get(position).getUID());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public User getItem(int position) {
        return list.get(position);
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
    public void addItems(ArrayList<User> postItems) {
        list.addAll(postItems);
        notifyDataSetChanged();
    }
    public void addLoading() {
        isLoaderVisible = true;
        list.add(new User(null,"","","",null,null,null));
        notifyItemInserted(list.size() - 1);
    }
    public void removeLoading() {
        isLoaderVisible = false;
        int position = list.size() - 1;
        if(list.size() > 0) {
            User item = getItem(position);
            if (item != null) {
                list.remove(position);
                notifyItemRemoved(position);
        }
        }
    }
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == list.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        private TextView userNameText,deleteIcon;
        private ImageView image;
        private Button addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = (TextView) itemView.findViewById(R.id.userNameText);
            image=(ImageView) itemView.findViewById(R.id.avatar);
            addBtn= (Button)itemView.findViewById(R.id.addBtn);
            deleteIcon= itemView.findViewById(R.id.deleteIcon);

            }
        public TextView getUserNameTextView() {
            return userNameText;
        }
        public ImageView getAvatar() {return image;}
        public Button getBtn(){return addBtn;}
        public TextView getDeleteIcon(){return  deleteIcon;}

    }
    public interface AddFriendsFragmentCallback {
        public void onAddButtonClick(String friendUID);
        public void onHideClick(String suggestUID);
    }


    private class ProgressHolder extends ViewHolder {
        public ProgressHolder(View inflate) {
            super(inflate);
        }
    }
}
