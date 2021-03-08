package UI.MainActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.study.android_zenly.R;

import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;

public class HomeFragment extends Fragment {
    NavController navController;
    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getActivity(),
                "onViewCreated",
                Toast.LENGTH_LONG).show();
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        loginviewModel= new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginviewModel.init(getActivity());
        loginviewModel.getAuthentication().observe(getViewLifecycleOwner(),(Observer<Boolean>) isAuthenticated ->{
            if(isAuthenticated)
            {
                Toast.makeText(getActivity(),
                        "Login Success",
                        Toast.LENGTH_LONG).show();
                requestLocationViewModel = new ViewModelProvider(getActivity()).get(RequestLocationViewModel.class);
                requestLocationViewModel.init(getActivity());
                requestLocationViewModel.getHasPermission().observe(getViewLifecycleOwner(),(Observer<Boolean>) hasPermission ->{
                    if(hasPermission) {
                        Toast.makeText(getActivity(),
                                "Has Permission",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        navController.navigate(R.id.action_homeFragment_to_request_location_nav);
                    }
                });
            }
            else{
                Toast.makeText(getActivity(),
                        "Not login Success",
                        Toast.LENGTH_LONG).show();
                navController.navigate(R.id.action_homeFragment_to_login_nav);
                ;
            }
        });

    }
}