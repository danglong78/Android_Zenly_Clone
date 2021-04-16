package UI.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Date;

import ultis.DateFormatter;
import viewModel.UserViewModel;


public class ChangeDobFragment extends Fragment {
    NavController navController;
    DatePicker datePicker;
    TextView dobTextView;
    UserViewModel userViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_dob, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        userViewModel= new ViewModelProvider(requireActivity()).get(UserViewModel.class);
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

    }
    public void initView(View view) {

        datePicker = view.findViewById(R.id.datePicker);
        datePicker.init(2000,1,1,null);
        dobTextView=view.findViewById(R.id.dobQuestion);
        view.findViewById(R.id.dateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!verifyDate(datePicker.getDayOfMonth(),datePicker.getMonth(),datePicker.getYear())) {
                    showDialog();
                }
                else {
                    saveInformation();
                    userViewModel.setDob(DateFormatter.format(new Date(datePicker.getYear()-1900,datePicker.getDayOfMonth(),datePicker.getDayOfMonth()),"dd MMMM yyyy"))
                            .addOnCompleteListener(task -> {
                                getActivity().onBackPressed();
                            });

                }
            }
        });
    }
    public void saveInformation() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putInt("day",datePicker.getDayOfMonth());
        myEditor.putInt("month",datePicker.getMonth());
        myEditor.putInt("year",datePicker.getYear());
        myEditor.apply();
    }


    public void loadInformation() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ( (prefs != null) && prefs.contains("day")&& prefs.contains("month")&& prefs.contains("year") ) {
            int day = prefs.getInt("day",1);
            int month = prefs.getInt("month",1);
            int year = prefs.getInt("year",2000);
            datePicker.init(year,month,day,null);
        }
        if ( (prefs != null) && prefs.contains("name") ) {
            String name = prefs.getString("name","");
            if(getArguments()!=null)
                dobTextView.setText("HEY "+name.toUpperCase()+", WHEN IS YOUR BIRTHDAY ?");
        }
    }


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