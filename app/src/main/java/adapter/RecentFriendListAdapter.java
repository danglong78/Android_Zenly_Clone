package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.study.android_zenly.R;

public class RecentFriendListAdapter extends RecyclerView.Adapter<RecentFriendListAdapter.ViewHolder>{
    public RecentFriendListAdapter(){

    }
    @NonNull
    @Override
    public RecentFriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_friend_list_item_layout, parent, false);

        return new RecentFriendListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentFriendListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
