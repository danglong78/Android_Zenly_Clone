package UI.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.study.android_zenly.R;


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
        editTextCarrierNumber = view.findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        view.findViewById(R.id.buttonPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = createBundle();
                navController.navigate(R.id.action_phoneFragment_to_confirmOtpFragment,bundle);
            }
        });
    }

    @Override
    public void saveCheckPoint() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putInt("step",R.id.action_introduceFragment_to_phoneFragment);
        myEditor.apply();
    }

    @Override
    public Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber",ccp.getFullNumberWithPlus());
        return bundle;
    }

    @Override
    public void saveInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        String temp = ccp.getSelectedCountryNameCode();
        myEditor.putString("countrycode", temp);
        myEditor.putString("phone",editTextCarrierNumber.getText().toString());
        myEditor.apply();
    }

    @Override
    public void loadInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ( (prefs != null) && (prefs.contains("name")) ) {
            String temp = prefs.getString("countrycode","");
            ccp.setDefaultCountryUsingNameCode(temp);
            ccp.resetToDefaultCountry();
            editTextCarrierNumber.setText(prefs.getString("phone",""));
        }
    }
}