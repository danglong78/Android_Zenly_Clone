   package UI.login;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.study.android_zenly.R;

import java.util.concurrent.TimeUnit;


public class PhoneFragment extends Fragment implements LoginFragmentInterface{


    NavController navController;
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        initView(view);
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
        navController= Navigation.findNavController(view);
        ccp =  view.findViewById(R.id.ccp);
        ccp.setDefaultCountryUsingPhoneCode(84);
        editTextCarrierNumber = view.findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        view.findViewById(R.id.buttonPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verifyPhone(ccp.getFullNumber()))
                {
                    showDialog();
                }
                else {
                saveInformation();
                    Bundle bundle = new Bundle();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            ccp.getFullNumberWithPlus()
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
                                    bundle.putString("verificationId",verificationId);
                                    InputMethodManager inputMethodManager =
                                            (InputMethodManager) getActivity().getSystemService(
                                                    Activity.INPUT_METHOD_SERVICE);
                                    if(getActivity().getCurrentFocus()!=null)
                                    {inputMethodManager.hideSoftInputFromWindow(
                                            getActivity().getCurrentFocus().getWindowToken(), 0);}
                                    navController.navigate(R.id.action_phoneFragment_to_confirmOtpFragment,bundle);
                                }
                            });
                }
            }
        });
    }

    @Override
    public void saveCheckPoint() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putInt("step",R.id.phoneFragment);
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
        String temp = ccp.getSelectedCountryNameCode();
        myEditor.putString("countrycode", temp);
        myEditor.putString("codePhone",ccp.getSelectedCountryCodeWithPlus());
        myEditor.putString("phone",editTextCarrierNumber.getText().toString());
        myEditor.apply();
    }

    @Override
    public void loadInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ( (prefs != null) && prefs.contains("phone")&& prefs.contains("countrycode") ) {
            String temp = prefs.getString("countrycode","");
            ccp.setDefaultCountryUsingNameCode(temp);
            ccp.resetToDefaultCountry();
            editTextCarrierNumber.setText(prefs.getString("phone",""));
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
        dialogTitle.setText("INVALID PHONE");
        dialogContent.setText("Please input your phone correctly.");
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
    private boolean verifyPhone(String phone)
    {
        if(editTextCarrierNumber.getText().toString().equals("")) {
            return false;
        }
        String regex="^[+]?[0-9]{10,13}$";
        String phoneNumber= ccp.getFullNumber();
        return phoneNumber.length() >= 10 && phoneNumber.length() <= 13 && phoneNumber.matches(regex) != false;
    }

}