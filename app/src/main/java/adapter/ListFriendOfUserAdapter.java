package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
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

public class ListFriendOfUserAdapter extends RecyclerView.Adapter<ListFriendOfUserAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_BUTTON = 0x00;
    private static final int VIEW_TYPE_TEXT = 0x01;
    private List<User> list;
    private List<String> UID_Friend_List;
    private List<String> UID_Invited_List;
    private String hostUserUID;
    private onClickUser callback;
    private Context context;


    public ListFriendOfUserAdapter(List<User> list, String hostUserUID, List<String>UID_Friend_List, List<String>UID_Invited_List, List<String> UID_Blocked_list, onClickUser callback, Context context) {
        this.list = new ArrayList<>();
        this.hostUserUID = hostUserUID;
        this.UID_Friend_List=UID_Friend_List;
        this.UID_Invited_List=UID_Invited_List;
        this.callback=callback;
        this.context=context;
        for(User user: list)
        {
            if(!UID_Blocked_list.contains(user.getUID()))
                this.list.add(user);
        }
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
        if(UID_Invited_List.contains(list.get(position).getUID())||UID_Friend_List.contains(list.get(position).getUID()) || list.get(position).getUID().compareTo(hostUserUID)==0)
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
        if(list.get(position).getUID().compareTo(hostUserUID)==0)
        {
            holder.onBind(name,"YOU");
        }
        else if(UID_Friend_List.contains(list.get(position).getUID()))
        {
            holder.onBind(name,"MUTUAL");
            holder.getItem().setOnClickListener(v->{
                callback.onClickUser(list.get(position).getUID(),true );
            });
        }
        else if (UID_Invited_List.contains(list.get(position).getUID()))
        {
            holder.onBind(name,"INVITED");
            holder.getItem().setOnClickListener(v->{
                callback.onClickUser(list.get(position).getUID(),false );
            });
        }
        else
        {
            holder.onBind(name,"ADD");
            holder.getItem().setOnClickListener(v->{
                callback.onClickUser(list.get(position).getUID(),false );
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        protected View item;
        protected ImageView image;

        public BaseViewHolder(View itemView) {
            super(itemView);
            item=itemView;
            image=(ImageView) itemView.findViewById(R.id.avatar);
        }
        public View getItem() {
            return item;
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
        }


        @Override
        public void onBind(String name,String type) {
            super.onBind(name,type);
            button.setText("ADD");
            username.setText(name);
        }

    }
    public interface onClickUser{
        void onClickUser(String UID,boolean isYourFriend);
    }

}
