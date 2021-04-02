package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class CreateChatAdapter extends RecyclerView.Adapter<CreateChatAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<User> userList,userListAll;
    private ArrayList<User> checkList;
    private onItemClickListener callback;

    public CreateChatAdapter( Context context,onItemClickListener callback) {
        this.context = context;
        this.callback = callback;
        userList = new ArrayList<User>();
        userListAll = new ArrayList<User>();
        checkList = new ArrayList<User>();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.create_chat_list_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageReference ref= FirebaseStorage.getInstance().
                getReference().child("avatars").child(userList.get(position).getAvatarURL());
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getAvatar());

            });
        }
        holder.itemView.setOnClickListener(v->{
            callback.onItemClick();

            if(checkList.contains(userList.get(position)))
               checkList.remove(userList.get(position));
            else{
            checkList.add(userList.get(position));
            }
            notifyItemChanged(position);
        });
        holder.getName().setText(userList.get(position).getName());
        holder.getCheckbox().setChecked(checkList.contains(userList.get(position)));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateListItems(List<User> newList) {
        DiffUtil.Callback diffUtilCallback= new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return userListAll.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                User x=userListAll.get(oldItemPosition);
                User y = userList.get(newItemPosition);
                return x.equals(y);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final User x = userListAll.get(oldItemPosition);
                final User y = newList.get(newItemPosition);

                return x.getUID().equals(y.getUID());
            }
        };
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        userListAll.clear();
        userListAll.addAll(newList);
        userList.clear();
        userList.addAll(newList);
        checkList= new ArrayList<>();
        diffResult.dispatchUpdatesTo(this);
    }

    public List<User> getCheckList(){
        return checkList;
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        CheckBox checkbox;
        ImageView avatar;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox=itemView.findViewById(R.id.checkbox);
            avatar=itemView.findViewById(R.id.avatar);
            name=itemView.findViewById(R.id.userNameText);
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
        public TextView getName(){
            return name;
        }
    }
    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if( constraint.toString().isEmpty())
                filteredList.addAll(userListAll);
            else{
                for(User user:userListAll)
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
            userList.clear();
            userList.addAll((Collection<User>) results.values);
            notifyDataSetChanged();

        }
    };

    public interface onItemClickListener {
        void onItemClick();
    }
}
