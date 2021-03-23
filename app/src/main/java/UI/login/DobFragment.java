package UI.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.study.android_zenly.R;

import java.util.Calendar;


public class DobFragment extends Fragment implements LoginFragmentInterface {

    NavController navController;
    DatePicker datePicker;
    TextView dobTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dob, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
//        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
//        if ( (prefs != null) && (prefs.contains("step"))) {
//            int step=prefs.getInt("step",-1);
//            if(step!=R.id.dobFragment && step!=-1 && getArguments().getBoolean("skip",false)) {}
//                navController.navigate(R.id.action_dobFragment_to_phoneFragment,createBundle(true));
//        }
        initView( view);

    }
    @Override
    public void onPause() {
        super.onPause();
        saveInformation();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInformation();
        saveCheckPoint();

    }

    @Override
    public void initView(View view) {

        datePicker = view.findViewById(R.id.datePicker);
        dobTextView=view.findViewById(R.id.dobQuestion);
        view.findViewById(R.id.dateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!verifyDate(datePicker.getDayOfMonth(),datePicker.getMonth(),datePicker.getYear())) {
                    showDialog();
                }
                else {
                    saveInformation();
                    navController.navigate(R.id.action_dobFragment_to_phoneFragment,createBundle(false));
                }
            }
        });
    }

    @Override
    public void saveCheckPoint() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putInt("step",R.id.dobFragment);
        myEditor.apply();
    }

    @Override
    public Bundle createBundle(boolean isSkip) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("skip", isSkip);
        return bundle;
    }

    @Override
    public void saveInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putInt("day",datePicker.getDayOfMonth());
        myEditor.putInt("month",datePicker.getMonth());
        myEditor.putInt("year",datePicker.getYear());
        myEditor.apply();
    }

    @Override
    public void loadInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ( (prefs != null) && prefs.contains("day")&& prefs.contains("month")&& prefs.contains("year") ) {
            int day = prefs.getInt("day",1);
            int month = prefs.getInt("month",1);
            int year = prefs.getInt("year",1);
            datePicker.init(year,month,day,null);
        }
        if ( (prefs != null) && prefs.contains("name") ) {
            String name = prefs.getString("name","");
            if(getArguments()!=null)
                dobTextView.setText("HEY "+name.toUpperCase()+", WHEN IS YOUR BIRTHDAY ?");
        }
    }

    @Override
    public void showDialog() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(getActivity());
        View layoutView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        Button dialogButton = layoutView.findViewById(R.id.btnDialog);
        TextView dialogTitle= layoutView.findViewById(R.id.dialog_title);
        TextView dialogContent= layoutView.findViewById(R.id.dialog_content);
        dialogTitle.setText("INVALID DATE");
        dialogContent.setText("Please choose your date of birth correctly.");
        myAlertBuilder.setView(layoutView);
        AlertDialog alert= myAlertBuilder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }
    private boolean verifyDate(int d,int m,int y){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month  = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(y>=year)
            return false;
        else{
            if(y==year)
            {
                if(m>month)
                    return false;
                else{
                    if(m==month)
                    {
                        return d <= day;
                    }
                }
            }
        }
        return true;
    }

}
