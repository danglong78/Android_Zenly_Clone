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
    public UserSettingApdater(SharedPreferences prefs) {}
    {
        this.prefs = prefs;
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
                if ( (prefs != null) && prefs.contains("day")&& prefs.contains("month")&& prefs.contains("year") ) {
                    int day = prefs.getInt("day",1);
                    int month = prefs.getInt("month",1);
                    int year = prefs.getInt("year",2000);
                    holder.getDescriptionText().setText(DateFormatter.format(new Date(year,month+1,day),"dd MMMM yyyy"));
                }
                break;
            }
            case 1:
            {
                holder.getTitleText().setText("Friends");
                break;
            }
            case 2:
            {
                holder.getTitleText().setText("Block User");
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
            return text;
        }
        public  View getView(){
            return view;
        }
    }
    public interface UserSettingCallback{
         void onListClick(int position);
    }
}
