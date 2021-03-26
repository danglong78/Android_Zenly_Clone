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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;

import UI.friend.AddFriendsFragmentCallback;
import data.models.User;

public class FriendSuggestListAdapter  extends RecyclerView.Adapter<FriendSuggestListAdapter.ViewHolder>{
    private ArrayList<User> list ;
    private FirebaseStorage storage;
    private StorageReference ref;
    private String userUID;
    private AddFriendsFragmentCallback listener;
    private Context context;
    public FriendSuggestListAdapter(Context context,AddFriendsFragmentCallback listener) {
        list = new ArrayList<>();
        this.context = context;
        storage = FirebaseStorage.getInstance();
        list.add(new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Ho Dai Tri","123","0e974b5c-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Tran Thanh Tam","123","0e974d50-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Huynh Lam Hoang Dai","123","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",null,null,null));
    }

    public FriendSuggestListAdapter(Context context, ArrayList<User> list,AddFriendsFragmentCallback listener){
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
    public FriendSuggestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggest_friend_list_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSuggestListAdapter.ViewHolder holder, int position) {
        Log.d("FRiend",list.get(position).getName());
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
                listener.onAddButtonClick(userUID,list.get(position).getUID());
            }

        });
        holder.getDeleteIcon().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onAddButtonClick(userUID,list.get(position).getUID());

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
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

}
