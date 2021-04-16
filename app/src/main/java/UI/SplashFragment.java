package UI;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.study.android_zenly.R;

import java.util.List;

import viewModel.FriendViewModel;
import viewModel.LoginViewModel;
import viewModel.MapViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;


public class SplashFragment extends Fragment {


    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    NavController navController;
    UserViewModel userViewModel;
    MapViewModel mapViewModel;
    FriendViewModel friendViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(view);

        loginviewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginviewModel.init(getActivity());
        loginviewModel.getAuthentication().observe(getViewLifecycleOwner(), (Observer<Boolean>) isAuthenticated -> {

            if (isAuthenticated) {

                requestLocationViewModel = new ViewModelProvider(getActivity()).get(RequestLocationViewModel.class);
                requestLocationViewModel.init(getActivity());
                requestLocationViewModel.getHasPermission().observe(getViewLifecycleOwner(), (Observer<Boolean>) hasPermission -> {
                    if (hasPermission) {
                        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

                        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
                        mapViewModel.setActivity(getActivity());

                        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
                        userViewModel.init(getActivity(), getViewLifecycleOwner());

                        if (userViewModel.getIsInited().getValue() == null) {
                            userViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean isInited) {
                                    if (isInited) {

//                                        runLocationUpdateWorker(userViewModel.getHostUser().getValue().getUID());

                                        // Friends
                                        friendViewModel.init(requireActivity(), requireActivity());
                                        friendViewModel.getIsInited().observe(requireActivity(), new Observer<List<Boolean>>() {

                                            @Override
                                            public void onChanged(List<Boolean> isInitedList) {
                                                Boolean isAllInited = true;
                                                for (Boolean isInited : isInitedList) {
                                                    if (!isInited) {
                                                        isAllInited = false;
                                                        break;
                                                    }
                                                }

                                                if (isAllInited) {
                                                    // Add friend markers
                                                    // Map
//                                                    mapViewModel.init(getViewLifecycleOwner());

                                                    friendViewModel.getIsInited().removeObserver(this);
                                                    navController.navigate(R.id.action_splashFragment_to_homeFragment);
                                                }
                                            }


                                        });

                                        userViewModel.getIsInited().removeObserver(this);
                                    }
                                }
                            });
                        }


                    } else {
                        navController.navigate(R.id.action_splashFragment_to_request_location_nav);
                        SplashFragment.this.onDestroy();

                    }
                });
            } else {

                navController.navigate(R.id.action_splashFragment_to_login_nav);
                SplashFragment.this.onDestroy();
            }
        });
    }
}