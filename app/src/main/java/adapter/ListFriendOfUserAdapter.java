package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import data.models.User;
import data.models.UserFriendList;

public class ListFriendOfUserAdapter extends RecyclerView.Adapter<ListFriendOfUserAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_BUTTON = 0x00;
    private static final int VIEW_TYPE_TEXT = 0x01;
    private List<UserFriendList> list;

    private onClickUser callback;
    private Context context;


    public ListFriendOfUserAdapter(onClickUser callback, Context context) {
        this.list = new ArrayList<>();
        this.callback=callback;
        this.context=context;

    }


    private <HOLDER extends BaseViewHolder>
    ListFriendOfUserAdapter.BaseViewHolder getHolder(ViewGroup parent, @LayoutRes int layout, Class<HOLDER> holderClass) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        try {
            Constructor<HOLDER> constructor = holderClass.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            HOLDER holder = constructor.newInstance(v);
            return holder;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getTag().compareTo("")!=0)
        {
            return VIEW_TYPE_TEXT;
        }
        else {
            return VIEW_TYPE_BUTTON;
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v;
        switch (viewType) {
            case VIEW_TYPE_TEXT: {
                return Objects.requireNonNull(getHolder(parent, R.layout.friend_list_item_of_your_friend_text_layout, TextViewHolder.class));
            }
            case VIEW_TYPE_BUTTON: {
                return Objects.requireNonNull(getHolder(parent, R.layout.friend_list_item_of_your_friend_button_layout, ButtonViewHolder.class));
            }

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        String name=list.get(position).getName();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(list.get(position).getAvatarURL());
        if(ref!=null)
        {
            ref.getDownloadUrl().addOnSuccessListener(uri->{
                String imageURL= uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getAvatar());
            });
        }
        boolean isYourfriend= false;

        if(list.get(position).getTag().compareTo("YOU")==0)
        {
            holder.onBind(name,"YOU");
        }
        else
        {
            holder.onBind(name,list.get(position).getTag());
            holder.itemView.setOnClickListener(v->{
                callback.onClickUser(list.get(position),list.get(position).getTag().compareTo("MUTUAL")==0);
            });
            if(list.get(position).getTag().compareTo("")==0)
                ((ButtonViewHolder)holder).getButton().setOnClickListener(v->{
                    list.get(position).setTag("INVITED");
                    callback.onClickAdd(list.get(position).getUID());
                    notifyItemChanged(position);
                });
        }

    }
    public void setList(ArrayList<UserFriendList> newList){
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;

        public BaseViewHolder(View itemView) {
            super(itemView);
            image=(ImageView) itemView.findViewById(R.id.avatar);
        }
        public ImageView getAvatar(){
            return image;
        }
        public void onBind(String name,String type) {

        }
    }
    public class TextViewHolder extends  BaseViewHolder {
        TextView username;
        TextView text;
        View item;
        public TextViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.userNameText);
            text= itemView.findViewById(R.id.text);
            this.item=itemView;
        }
        @Override
        public void onBind(String name,String type) {
            super.onBind(name,type);
            username.setText(name);
            text.setText(type);

        }
    }
    public class ButtonViewHolder extends  BaseViewHolder {
        TextView username;
        Button button;
        public ButtonViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.userNameText);
            button=itemView.findViewById(R.id.button);
        }


        @Override
        public void onBind(String name,String type) {
            super.onBind(name,type);
            button.setText("ADD");
            username.setText(name);
        }
        public Button getButton(){
            return button;
        }

    }
    public interface onClickUser{
        void onClickUser(User user,boolean isYourFriend);
        void onClickAdd(String UID);
    }

}
