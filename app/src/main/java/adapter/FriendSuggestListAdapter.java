package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.study.android_zenly.R;

import java.util.ArrayList;

public class FriendSuggestListAdapter  extends RecyclerView.Adapter<FriendSuggestListAdapter.ViewHolder>{
    private ArrayList<String> list ;

    public FriendSuggestListAdapter() {
        list = new ArrayList<>();
        list.add("Dang Minh Hoang Long");
        list.add("Huynh Lam Hoang Dai");
        list.add("Ho Dai Tri");
        list.add("Tran Thanh Tam");

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
        holder.getUserNameTextView().setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        private TextView userNameText;
            public ViewHolder(@NonNull View itemView) {
            super(itemView);
                userNameText = (TextView) itemView.findViewById(R.id.userNameText);

            }
        public TextView getUserNameTextView() {
            return userNameText;
        }

    }
}
