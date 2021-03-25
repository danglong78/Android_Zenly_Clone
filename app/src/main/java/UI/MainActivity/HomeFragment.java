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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import UI.friend.AddFriendFragment;
import ultis.FragmentTag;
import viewModel.LoginViewModel;
import viewModel.MapViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";


    NavController navController,chatnavController;

    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    UserViewModel userViewModel;
    MapViewModel mapViewModel;

    Button chatBtn, userBtn, mapBtn, friendBtn;
    DrawerLayout drawerLayout;
    MotionLayout motionLayout;
    BottomSheetBehavior bottomSheetBehavior;
    int motionLayoutstate = 0;
    AddFriendFragment addFriendFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: order test");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        drawerLayout = rootView.findViewById(R.id.drawerlayout);
        motionLayout = rootView.findViewById(R.id.motion_layout);

        Log.d(TAG, "onCreateView: order test");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: order test");
        observeLoginAndLocationPermissions(view);
        bindingView(view);
        setEventListener();




    }

    @Override
    public void onStart() {
        super.onStart();
        if(loginviewModel.getAuthentication().getValue() ) {
            userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
            userViewModel.init(getActivity(), getViewLifecycleOwner());

            mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
            mapViewModel.setActivity(getActivity());

            userViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isInited) {
                    if (isInited) {

                        mapViewModel.init(getViewLifecycleOwner());
                        userViewModel.getIsInited().removeObserver(this);
                    }
                }
            });
        }
        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
            addFriendFragment.setProgress(1);
        }
    }

    private void bindingView(View view) {

        addFriendFragment = (AddFriendFragment) getChildFragmentManager().findFragmentById(R.id.addFriendFragment);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.navigation_drawer_bottom));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHalfExpandedRatio(0.6f);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    addFriendFragment.setProgress(0);
                if(newState == BottomSheetBehavior.STATE_EXPANDED)
                    addFriendFragment.setProgress(1);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.6f) {
                    addFriendFragment.setProgress(slideOffset);
                }
            }
        });

        chatBtn = view.findViewById(R.id.chatButton);
//        ((MaterialButton)chatBtn).setIconResource(R.drawable.ic_baseline_settings_24);

        userBtn = (Button) view.findViewById(R.id.userButton);

        mapBtn = view.findViewById(R.id.mapButton);

        friendBtn = view.findViewById(R.id.friendButton);


    }

    private void setEventListener() {
        friendBtn.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (motionLayoutstate == 0) {
                    if (drawerView.getId() == R.id.navigation_view_left) {
                        if(chatnavController.getCurrentDestination().getId()==R.id.chatListFragment )
                        {
                            motionLayout.setTransition(R.id.start,R.id.left);
                        }
                        else{
                            motionLayout.setTransition(R.id.start,R.id.hideLeft);
                        }
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
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (motionLayoutstate != 0)
                    motionLayoutstate = 0;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (drawerView.getId() == R.id.navigation_view_left && chatnavController.getCurrentDestination().getId()!=R.id.chatListFragment) {
                    NavOptions.Builder navBuilder = new NavOptions.Builder();
                    NavOptions navOptions = navBuilder.setPopUpTo(R.id.chatListFragment, true).build();
                    chatnavController.navigate(R.id.chatListFragment, null, navOptions);
                    ((MainActivity)getActivity()).setFragmentTag(FragmentTag.OTHERS,motionLayout);

                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

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
        mapBtn.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
        });
    }

    private void observeLoginAndLocationPermissions(View view) {
        navController = Navigation.findNavController(view);
        chatnavController= Navigation.findNavController(getActivity(),R.id.chat_nav_host_fragment);
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

                        HomeFragment.this.onDestroy();


                    }
                });
            } else {

                navController.navigate(R.id.action_homeFragment_to_login_nav);
                HomeFragment.this.onDestroy();
            }
        });
    }
    public  void setBottomSheetState(int state) {
        bottomSheetBehavior.setState(state);
    }


}