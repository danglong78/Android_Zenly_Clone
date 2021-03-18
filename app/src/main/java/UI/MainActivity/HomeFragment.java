package UI.MainActivity;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;

public class HomeFragment extends Fragment {
    NavController navController;
    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    Button chatBtn, userBtn,mapBtn,friendBtn;
    DrawerLayout drawerLayout;
    MotionLayout motionLayout;
    BottomSheetBehavior bottomSheetBehavior;
    int motionLayoutstate = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        drawerLayout = rootView.findViewById(R.id.drawerlayout);
        motionLayout = rootView.findViewById(R.id.motion_layout);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setPadding(0,getStatusBarHeight()+30,0,0);

        bindingView(view);
        observeLoginAndLocationPermissions(view);


    }
    private void bindingView(View view)
    {
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.navigation_drawer_bottom));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        friendBtn = view.findViewById(R.id.friendButton);
        friendBtn.setOnClickListener(v->{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (motionLayoutstate == 0) {
                    if (drawerView.getId() == R.id.navigation_view_left) {

                        motionLayout.setTransition(R.id.chatButtonAnimation);
                    } else {
                        motionLayout.setTransition(R.id.userButtonAnimation);

                    }
                    motionLayout.setProgress(slideOffset);

                }
                if (motionLayoutstate == 1 && drawerView.getId() == R.id.navigation_view_left) {
                    motionLayout.setProgress(slideOffset);
                }
                if (motionLayoutstate == 2 && drawerView.getId() == R.id.navigation_view_right) {
                    motionLayout.setProgress(slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (motionLayoutstate != 0)
                    motionLayoutstate = 0;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        chatBtn = (Button) view.findViewById(R.id.chatButton);
        chatBtn.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                return;
            }
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                motionLayout.setTransition(R.id.userButtonAnimationToChatButtonAnimation);
                motionLayoutstate = 1;
                drawerLayout.closeDrawer(Gravity.RIGHT);

            }
            drawerLayout.openDrawer(Gravity.LEFT);
        });
        chatBtn.bringToFront();
        userBtn = (Button) view.findViewById(R.id.userButton);
        userBtn.bringToFront();
        userBtn.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
                return;
            }
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                motionLayout.setTransition(R.id.chatButtonAnimationToUserButonAnimation);
                motionLayoutstate = 2;
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            drawerLayout.openDrawer(Gravity.RIGHT);
        });
        mapBtn= view.findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(v -> {
           drawerLayout.closeDrawers();
        });

    }
    private void observeLoginAndLocationPermissions(View view) {
        navController = Navigation.findNavController(view);
        loginviewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginviewModel.init(getActivity());
        loginviewModel.getAuthentication().observe(getViewLifecycleOwner(), (Observer<Boolean>) isAuthenticated -> {
            if (isAuthenticated) {

                requestLocationViewModel = new ViewModelProvider(getActivity()).get(RequestLocationViewModel.class);
                requestLocationViewModel.init(getActivity());
                requestLocationViewModel.getHasPermission().observe(getViewLifecycleOwner(), (Observer<Boolean>) hasPermission -> {
                    if (hasPermission) {

                    } else {
                        navController.navigate(R.id.action_homeFragment_to_request_location_nav);

                    }
                });
            } else {
                navController.navigate(R.id.action_homeFragment_to_login_nav);
                ;
            }
        });
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}