package UI.user;

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

import com.study.android_zenly.R;

public class ChangeNameFragment extends Fragment {
    NavController navController;
    EditText nameInput;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_name, container, false);
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

    }


    public void initView(View view) {
        nameInput= view.findViewById(R.id.nameInput);
        view.findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameInput.getText().toString().equals("")) {
                    showDialog();
                }
                else{
                    saveInformation();
                    //TODO change name of user on firebase

                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getActivity().getSystemService(
                                    Activity.INPUT_METHOD_SERVICE);
                    if(getActivity().getCurrentFocus()!=null)
                    {inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);}
                    navController.popBackStack();
                }
            }
        });
    }

    public void showDialog()
    {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(getActivity());
        View layoutView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        Button dialogButton = layoutView.findViewById(R.id.btnDialog);
        TextView dialogTitle= layoutView.findViewById(R.id.dialog_title);
        TextView dialogContent= layoutView.findViewById(R.id.dialog_content);
        dialogTitle.setText("EMPTY NAME");
        dialogContent.setText("You must have a name. I know you have one.");
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
    public void saveInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        String temp = nameInput.getText().toString();
        myEditor.putString("name", temp);
        myEditor.apply();
    }

    public void loadInformation() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ( (prefs != null) && (prefs.contains("name")) ) {
            String temp = prefs.getString("name","");
            nameInput.setText(temp);
        }
    }
}