package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.study.android_zenly.R;

import java.util.Date;

import ultis.DateFormatter;

public class UserSettingApdater extends RecyclerView.Adapter<UserSettingApdater.ViewHolder> {
    public  UserSettingCallback callback;
    private  SharedPreferences prefs;
    private String dob;
    private int numFriends;
    private int numBlocks;


    public UserSettingApdater(String dob, int numFriends, int numBlocks)
    {
        this.dob = dob;
        this.numFriends = numFriends;
        this.numBlocks = numBlocks;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_setting_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (position){
            case 0:{
                holder.getTitleText().setText("Birthdays");
                holder.getDescriptionText().setText(dob);
                break;
            }
            case 1:
            {
                holder.getTitleText().setText("Friends");
                holder.getDescriptionText().setText(String.valueOf(numFriends));
                break;
            }
            case 2:
            {
                holder.getTitleText().setText("Block User");
                holder.getDescriptionText().setText(String.valueOf(numBlocks));
                break;
            }


        }
        holder.getView().setOnClickListener(v->{
            callback.onListClick(position);
        });
    }


    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text,text2;
        private UserSettingCallback callback;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            text= itemView.findViewById(R.id.userNameText);
            text2=itemView.findViewById(R.id.describeText);
        }

        public TextView getTitleText() {
            return text;
        }
        public TextView getDescriptionText() {
            return text2;
        }
        public  View getView(){
            return view;
        }
    }
    public interface UserSettingCallback{
         void onListClick(int position);
    }
}
