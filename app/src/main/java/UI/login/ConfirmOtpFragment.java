package UI.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.study.android_zenly.R;

import java.util.concurrent.TimeUnit;

import data.models.User;
import data.repositories.UserRepository;


public class ConfirmOtpFragment extends Fragment {
    private final String TAG = "ConfirmmOtpFragment";


    NavController navController;
    TextView countdownText,describe;
    Button btn,sendAgainButton;
    EditText codeInput;
    ProgressBar progressBar;
    String verificationId;
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
        sendAgainButton = view.findViewById(R.id.sendAgainBtn);
        codeInput = (EditText) view.findViewById(R.id.codeInput);
        verificationId = getArguments().getString("verificationId");
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarConfirmOTP);
        describe = (TextView)view.findViewById(R.id.phoneDescribe);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if(prefs!=null && prefs.contains("phone")&& prefs.contains("codePhone")) {
            describe.setText("I SENT TO "+prefs.getString("codePhone","")+prefs.getString("phone",""));
        }
        sendAgainButton.setOnClickListener(v->{
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    prefs.getString("phone","")
                    ,60
                    , TimeUnit.SECONDS
                    ,requireActivity()
                    ,new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            Toast.makeText(getActivity(), "On Complete", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(getActivity(), "On Fail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            ConfirmOtpFragment.this.verificationId=verificationId;
                        }
                    });
            countdownText.setVisibility(View.VISIBLE);
            CountDownTimer countdown= new CountDownTimer(120000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countdownText.setText("You can ask for the new code in " + millisUntilFinished / 1000+" seconds");
                }

                public void onFinish() {
                    countdownText.setVisibility(View.INVISIBLE);
                    sendAgainButton.setVisibility(View.VISIBLE);

                }
            }.start();
        });

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
                if(s.length()==6){
                    progressBar.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.INVISIBLE);
                    if (verificationId != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, s.toString());

                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor myEditor = prefs.edit();
                                            myEditor.putBoolean("isAuthenticated", true);
                                            myEditor.putString("uid", FirebaseAuth.getInstance().getUid());
                                            myEditor.commit();

                                            User newUser = new User();

                                            newUser.setName(prefs.getString("name", ""));
                                            newUser.setUID(FirebaseAuth.getInstance().getUid());
                                            newUser.setAvatarURL(new String());
                                            newUser.setLocation(new GeoPoint(1,1));
                                            DocumentReference newUserRef = UserRepository.getInstance().getUserReference(FirebaseAuth.getInstance().getUid());
                                            newUser.setPhone(prefs.getString("phone", ""));

                                            newUserRef.set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: newUserRef: success");
                                                    }
                                                    else {
                                                        Log.d(TAG, "onComplete: failed " + task.getException());
                                                    }
                                                }
                                            });

                                            navController.navigate(R.id.action_global_homeFragment);
                                        } else {
                                            Toast.makeText(getActivity(), "Your verify code wrong!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
//                    SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor myEditor = prefs.edit();
//                    myEditor.putBoolean("isAuthenticated", true);
//                    myEditor.commit();
//
////                    startActivity(new Intent(getActivity(), RequestPermissionActivity.class));
//                    navController.navigate(R.id.action_global_homeFragment);
                    codeInput.setText("");
                    progressBar.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);
                }
            }
        });
        countdownText = (TextView) view.findViewById(R.id.countdownText);
        view.findViewById(R.id.buttonConfirmSMS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeInput.getText().toString().length()==6)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.INVISIBLE);
                   SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor myEditor = prefs.edit();
                    myEditor.putBoolean("isAuthenticated", true);
                    myEditor.putString("uid", FirebaseAuth.getInstance().getUid());
                    myEditor.commit();

                    User newUser = new User();

                    newUser.setName(prefs.getString("name", ""));
                    newUser.setUID(FirebaseAuth.getInstance().getUid());
                    DocumentReference newUserRef = UserRepository.getInstance().getUserReference(FirebaseAuth.getInstance().getUid());


                    newUserRef.set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: newUserRef: success");
                            }
                            else {
                                Log.d(TAG, "onComplete: failed " + task.getException());
                            }
                        }
                    });

                    navController.navigate(R.id.action_global_homeFragment);
                }
                else
                    showDialog();
                codeInput.setText("");
                progressBar.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.VISIBLE);
            }
        });
        CountDownTimer countdown= new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownText.setText("You can ask for the new code in " + millisUntilFinished / 1000+" seconds");
            }

            public void onFinish() {
                countdownText.setVisibility(View.INVISIBLE);
                sendAgainButton.setVisibility(View.VISIBLE);

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
