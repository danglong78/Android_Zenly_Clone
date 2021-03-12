package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.study.android_zenly.R;

import java.util.ArrayList;

public class ChatListAdapter  extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{
    private ArrayList<String> list ;
    public ChatListAdapter() {
        list = new ArrayList<>();
        for(int i=0;i<=30;i++)
        {
            list.add("Dang Minh Hoang Long");
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_user_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getUserNameTextView().setText(list.get(position));
        holder.getLastMessageTextView().setText("Hello");
        holder.getTimeText().setText("Today");


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView userNameText,lastMessageText,timeText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = (TextView) itemView.findViewById(R.id.userNameText);
            lastMessageText = (TextView) itemView.findViewById(R.id.lastMessageText);
            timeText = (TextView) itemView.findViewById(R.id.timeText);
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



    }

}
