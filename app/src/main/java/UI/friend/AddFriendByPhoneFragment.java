package UI.friend;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;
import com.study.android_zenly.R;

import data.models.User;
import viewModel.UserViewModel;

public class AddFriendByPhoneFragment extends Fragment {


    EditText editTextCarrierNumber;
    CountryCodePicker ccp;
    Button searchBtn;
    UserViewModel userViewmodel;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_friend_by_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewmodel= new ViewModelProvider(getActivity()).get(UserViewModel.class);
        editTextCarrierNumber=view.findViewById(R.id.editText_carrierNumber);
        ccp= view.findViewById(R.id.ccp);
        searchBtn = view.findViewById(R.id.buttonPhone);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        searchBtn.setOnClickListener(v->{
            if(!verifyPhone(ccp.getFullNumber()))
            {
                showDialog("INVALID PHONE","Please input your phone correctly.");
            }
            else{
                String phone = editTextCarrierNumber.getText().toString();
                Log.d("AddFriendByPhoneFragment", "onViewCreated: " + phone);
                phone = (phone.startsWith("0"))? phone.substring(1) : phone;
                userViewmodel.getUserWithPhone(phone).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty())
                            {
                                showDialog("NOT FOUND","Don't exists any user with this phone");
                            }
                            else{
                                user=task.getResult().getDocuments().get(0).toObject(User.class);
                                Log.d("Search by phone",user.toString());
                                Bundle bundle = new Bundle();
                                bundle.putString("name",user.getName());
                                bundle.putString("phone",user.getPhone());
                                bundle.putString("uid",user.getUID());
                                bundle.putString("avatar",user.getAvatarURL());
                                NavController navController = Navigation.findNavController(getActivity(),R.id.friend_nav_host_fragment);
                                navController.navigate(R.id.action_addFriendByPhoneFragment_to_strangerProfileFragment2,bundle);
                            }
                        }
                    }
                });
            }
        });


    }
    public void showDialog(String title,String content) {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(getActivity());
        View layoutView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        Button dialogButton = layoutView.findViewById(R.id.btnDialog);
        TextView dialogTitle= layoutView.findViewById(R.id.dialog_title);
        TextView dialogContent= layoutView.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogContent.setText(content);
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