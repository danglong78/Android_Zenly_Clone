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
import android.widget.DatePicker;
import android.widget.TextView;

import com.study.android_zenly.R;


public class DobFragment extends Fragment implements LoginFragmentInterface {

    NavController navController;
    DatePicker datePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dob, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        navController= Navigation.findNavController(view);
        TextView dobTextView =view.findViewById(R.id.dobQuestion);
        datePicker = view.findViewById(R.id.datePicker);
        dobTextView.setText("HEY "+getArguments().getString("name").toUpperCase()+", WHEN IS YOUR BIRTHDAY ?");
        view.findViewById(R.id.dateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = createBundle();
                navController.navigate(R.id.action_dobFragment_to_phoneFragment,bundle);
            }
        });
    }

    @Override
    public void saveCheckPoint() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putInt("step",R.id.action_introduceFragment_to_dobFragment);
        myEditor.apply();
    }

    @Override
    public Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("day",datePicker.getDayOfMonth());
        bundle.putInt("month",datePicker.getMonth()+1);
        bundle.putInt("year",datePicker.getYear());
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
        if ( (prefs != null) && (prefs.contains("name")) ) {
            int day = prefs.getInt("day",1);
            int month = prefs.getInt("month",1);
            int year = prefs.getInt("year",1);
            datePicker.init(day,month,year,null);
        }
    }
}