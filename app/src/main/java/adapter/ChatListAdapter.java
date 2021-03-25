package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import data.models.Conversation;
import data.models.Message;
import data.models.User;
import data.repositories.ConversationRepository;
import ultis.DateFormatter;

public class ChatListAdapter  extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> implements Filterable {
    private ArrayList<Conversation> convList,convListAll;
    private FirebaseStorage storage;
    private StorageReference ref;
    private Context context;
    private OnChatListListener onChatListListener;
    private static ConversationRepository convRepo = new ConversationRepository();

    public ChatListAdapter(Context context,OnChatListListener onChatListListener) {
        convList = new ArrayList<>();
        this.onChatListListener= onChatListListener;
        this.context = context;
        storage= FirebaseStorage.getInstance();
        convList.add(new Conversation(null,"123","Dang Minh Hoang Long","0e9748c8-8978-11eb-8dcd-0242ac130003.png",new Message(null,"1","Hello",Timestamp.now())));
        convList.add(new Conversation(null,"123","Ho Dai Tri","0e974b5c-8978-11eb-8dcd-0242ac130003.png",new Message(null,"1","Hello",Timestamp.now())));
        convList.add(new Conversation(null,"123","Tran Thanh Tam","0e974d50-8978-11eb-8dcd-0242ac130003.png",new Message(null,"1","Hello",Timestamp.now())));
        convList.add(new Conversation(null,"123","Huynh Lam Hoang Dai","0e974e36-8978-11eb-8dcd-0242ac130003.jpg",new Message(null,"1","Hello",Timestamp.now())));
        convListAll=new ArrayList<>(convList);
    }

    public ChatListAdapter(String[] convID,Context context,OnChatListListener onChatListListener) {
        convList = new ArrayList<>();
        this.onChatListListener= onChatListListener;
        this.context = context;
        storage= FirebaseStorage.getInstance();
        for(String s:convID){
            convList.add(convRepo.getConversation(s).getValue());
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_user_item_layout, parent, false);

        return new ViewHolder(view,onChatListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.getUserNameTextView().setText(list.get(position).getName());
//        holder.getLastMessageTextView().setText("Hello");
//        holder.getTimeText().setText("Today");
//        ref= storage.getReference().child("avatars").child(list.get(position).getAvatarURL());
//        if(ref!=null) {
//            ref.getDownloadUrl().addOnSuccessListener(uri->{
//                String imageURL= uri.toString();
//                Glide.with(context)
//                        .load(imageURL)
//                        .into(holder.getAvatar());
//
//            });
//        }

        holder.getUserNameTextView().setText(convList.get(position).getName());
        holder.getLastMessageTextView().setText(convList.get(position).getRecentMessage().getMess());
        holder.getTimeText().setText(DateFormatter.format(convList.get(position).getRecentMessage().getTime().toDate(), DateFormatter.Template.TIME));
        ref= storage.getReference().child("avatars").child(convList.get(position).getAvatarURL());
        if(ref!=null) {
            ref.getDownloadUrl().addOnSuccessListener(uri->{
                String imageURL= uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .into(holder.getAvatar());

            });
        }



    }

    @Override
    public int getItemCount() {
        return convList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private TextView userNameText,lastMessageText,timeText;
        private ImageView avatar;
        OnChatListListener onChatListListener;
        public ViewHolder(@NonNull View itemView,OnChatListListener onChatListListener) {
            super(itemView);
            this.onChatListListener=onChatListListener;
            userNameText = (TextView) itemView.findViewById(R.id.userNameText);
            lastMessageText = (TextView) itemView.findViewById(R.id.lastMessageText);
            timeText = (TextView) itemView.findViewById(R.id.timeText);
            avatar= (ImageView) itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }
        public TextView getUserNameTextView() {
            return userNameText;
        }
        public TextView getLastMessageTextView() {
            return lastMessageText;
        }

        public TextView getTimeText() {
            return timeText;
        }
        public ImageView getAvatar() {
            return avatar;
        }

        @Override
        public void onClick(View v) {
            onChatListListener.onChatClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
    public interface OnChatListListener {
        void onChatClick(int position);
    }
    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Conversation> filteredList = new ArrayList<Conversation>();
            if( constraint.toString().isEmpty())
                filteredList.addAll(convListAll);
            else{
                for(Conversation user:convListAll)
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
            convList.clear();
            convList.addAll((Collection<Conversation>) results.values);
            notifyDataSetChanged();

        }
    };

}
