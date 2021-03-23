package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FriendSuggestListAdapter  extends RecyclerView.Adapter<FriendSuggestListAdapter.ViewHolder>{
    private ArrayList<User> list ;
    private FirebaseStorage storage;
    private StorageReference ref;
    private Context context;
    public FriendSuggestListAdapter(Context context) {
        list = new ArrayList<>();
        this.context = context;
        storage = FirebaseStorage.getInstance();
        list.add(new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Ho Dai Tri","123","0e974b5c-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Tran Thanh Tam","123","0e974d50-8978-11eb-8dcd-0242ac130003.png",null,null,null));
        list.add(new User(null,"Huynh Lam Hoang Dai","123","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",null,null,null));
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
        holder.getUserNameTextView().setText(list.get(position).getName());
        ref= storage.getReference().child("avatars").child(list.get(position).getAvatarURL());
        ref.getDownloadUrl().addOnSuccessListener(uri->{
            String imageURL= uri.toString();
            Glide.with(context)
                    .load(imageURL)
                    .into(holder.getAvatar());

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        private TextView userNameText;
        private ImageView image;
            public ViewHolder(@NonNull View itemView) {
            super(itemView);
                userNameText = (TextView) itemView.findViewById(R.id.userNameText);
                image=(ImageView) itemView.findViewById(R.id.avatar);

            }
        public TextView getUserNameTextView() {
            return userNameText;
        }
        public ImageView getAvatar() {return image;}

    }
}
