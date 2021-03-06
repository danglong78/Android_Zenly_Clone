package UI.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.study.android_zenly.R;

import UI.RequestPermission.RequestPermissionActivity;


public class ConfirmOtpFragment extends Fragment {

    NavController navController;
    TextView countdownText,describe;
    Button btn;
    EditText codeInput;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        codeInput = (EditText) view.findViewById(R.id.codeInput);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarConfirmOTP);
        describe = (TextView)view.findViewById(R.id.phoneDescribe);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if(prefs!=null && prefs.contains("phone")&& prefs.contains("codePhone")) {
            describe.setText("I SENT TO "+prefs.getString("codePhone","")+prefs.getString("phone",""));
        }
        progressBar.setVisibility(View.GONE);
        btn = (Button) view.findViewById(R.id.buttonConfirmSMS);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==4){
                    progressBar.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), RequestPermissionActivity.class));
                }
            }
        });
        countdownText = (TextView) view.findViewById(R.id.countdownText);
        view.findViewById(R.id.buttonConfirmSMS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeInput.getText().toString().length()==4)
                    startActivity(new Intent(getActivity(), RequestPermissionActivity.class));
                else
                    showDialog();
            }
        });
        CountDownTimer countdown= new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownText.setText("You can ask for the new code in " + millisUntilFinished / 1000+" seconds");
            }

            public void onFinish() {
                countdownText.setAlpha(0f);
            }
        }.start();
    }
    public void showDialog() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(getActivity());
        View layoutView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        Button dialogButton = layoutView.findViewById(R.id.btnDialog);
        TextView dialogTitle= layoutView.findViewById(R.id.dialog_title);
        TextView dialogContent= layoutView.findViewById(R.id.dialog_content);
        dialogTitle.setText("INVALID CODE");
        dialogContent.setText("Please input your code correctly.");
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
}