package UI.MainActivity;

import android.content.Context;
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
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ultis.FragmentTag;
import ultis.UpdateLocationWorker;
import viewModel.FriendViewModel;
import viewModel.LoginViewModel;
import viewModel.MapViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";


    NavController navController,chatnavController,usernavController;

    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    UserViewModel userViewModel;
    MapViewModel mapViewModel;
    FriendViewModel friendViewModel;


    Button chatBtn, userBtn, mapBtn, friendBtn;
    DrawerLayout drawerLayout;
    MotionLayout motionLayout,bottomSheetMotionLayout;
    BottomSheetBehavior bottomSheetBehavior;
    int motionLayoutstate = 0;



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
        bindingView(view);
        setEventListener();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: order test");
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        super.onStart();
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        mapViewModel.setActivity(getActivity());
        mapViewModel.init(getViewLifecycleOwner());
        runLocationUpdateWorker(userViewModel.getHostUser().getValue().getUID());

//        if(loginviewModel.getAuthentication().getValue() ) {
//
//            mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
//            mapViewModel.setActivity(getActivity());
//
//            friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
//
//            if (userViewModel.getIsInited().getValue() == null) {
//                userViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//                    @Override
//                    public void onChanged(Boolean isInited) {
//                        if (isInited) {
//                            Log.d(TAG, "onChanged: observe inited user event");
//
//
//                            // Friends
//                            friendViewModel.init(requireActivity(), getViewLifecycleOwner());
//                            friendViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<List<Boolean>>() {
//
//                                @Override
//                                public void onChanged(List<Boolean> isInitedList) {
//                                    Boolean isAllInited = true;
//                                    for (Boolean isInited : isInitedList) {
//                                        if (!isInited) {
//                                            isAllInited = false;
//                                            break;
//                                        }
//                                    }
//
//                                    if (isAllInited) {
//                                        // Add friend markers
//                                        // Map
//                                        mapViewModel.init(getViewLifecycleOwner());
//
//                                        friendViewModel.getIsInited().removeObserver(this);
//                                    }
//                                }
//
//
//                            });
//
//                            userViewModel.getIsInited().removeObserver(this);
//                        }
//                    }
//                });
//            }
//
//            userViewModel.init(getActivity(), getViewLifecycleOwner());
//
//        }
        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetMotionLayout.setProgress(1);
        }
    }

    private void runLocationUpdateWorker(String userUID) {
        // Create Network constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWork =
                new PeriodicWorkRequest.Builder(UpdateLocationWorker.class, 15, TimeUnit.MINUTES)
                        .addTag("Update Location Worker")
                        .setConstraints(constraints)
                        .setInputData(new Data.Builder()
                                .putString("userUID", userUID)
                                .build())
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getActivity()).enqueueUniquePeriodicWork(
                "Update Location Worker",
                ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                periodicWork //work request
        );
    }

    private void bindingView(View view) {
        chatnavController=Navigation.findNavController(requireActivity(),R.id.chat_nav_host_fragment);
        navController=Navigation.findNavController(view);
        usernavController=Navigation.findNavController(requireActivity(),R.id.user_nav_host_fragment);
        bottomSheetMotionLayout = (MotionLayout) view.findViewById(R.id.friend_motion_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.navigation_drawer_bottom));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHalfExpandedRatio(0.6f);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    bottomSheetMotionLayout.setProgress(0);
                if(newState == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetMotionLayout.setProgress(1);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.6f) {
                    bottomSheetMotionLayout.setProgress(slideOffset);
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
                        if(usernavController.getCurrentDestination().getId()==R.id.userFragment )
                        {
                            motionLayout.setTransition(R.id.start,R.id.right);
                        }
                        else{
                            motionLayout.setTransition(R.id.start,R.id.hideRight);
                        }
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
                    ((MainActivity)getActivity()).setFragmentTag(FragmentTag.OTHERS,motionLayout,navController);

                }
                if (drawerView.getId() == R.id.navigation_view_right && usernavController.getCurrentDestination().getId()!=R.id.userFragment) {
                    NavOptions.Builder navBuilder = new NavOptions.Builder();
                    NavOptions navOptions = navBuilder.setPopUpTo(R.id.chatListFragment, true).build();
                    usernavController.navigate(R.id.userFragment, null, navOptions);
                    ((MainActivity)getActivity()).setFragmentTag(FragmentTag.OTHERS,motionLayout,navController);

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
            mapViewModel.focusOnHost();
        });
    }



    public  void setBottomSheetState(int state) {
        bottomSheetBehavior.setState(state);
    }


}