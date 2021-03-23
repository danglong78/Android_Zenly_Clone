package UI.MainActivity;


import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;

import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.study.android_zenly.R;

import java.util.HashMap;
import java.util.Map;

import data.models.User;
import viewModel.LoginViewModel;
import viewModel.MapViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    private final String TAG = "HomeFragment";


    NavController navController;

    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    UserViewModel userViewModel;
    MapViewModel mapViewModel;

    Button chatBtn, userBtn, mapBtn, friendBtn;
    DrawerLayout drawerLayout;
    MotionLayout motionLayout;
    BottomSheetBehavior bottomSheetBehavior;
    int motionLayoutstate = 0;
    MotionLayout friendMotionLayout;



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
        observeLoginAndLocationPermissions(view);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.init(getActivity(), getViewLifecycleOwner());
        // wait for hostUser result from Firebase (should use timeout instead)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        if (userViewModel.getHostUser() != null) {
            getLastLocation();
            requestLocationUpdate();
        }


//        userViewModel.getHostUser().observe(getViewLifecycleOwner(), new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                Log.d(TAG, "onChanged: " + user.toString());
//
//                userViewModel.getHostUser().removeObserver(this);
//            }
//        });


    }

    private void requestLocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Log.d(TAG, "onLocationResult: length " + locationResult.getLocations().size());
                Location location = locationResult.getLocations().get(0);
                mapViewModel.moveTo(new LatLng(location.getLatitude(),location.getLongitude()));
                Log.d(TAG, "onLocationResult: " + location);
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Start location updates
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    Log.d(TAG, "onComplete: task.getResult: " + location);
                    Log.d(TAG, "onComplete: mapViewModel" + mapViewModel);
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    User user = userViewModel.getHostUser().getValue();
                    user.setLocation(geoPoint);

                    HashMap data = new HashMap<String, Object>();
                    data.put("location", geoPoint);
                    userViewModel.getUserReference(user.getUID()).update(data);

                    mapViewModel.moveTo(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        });
    }

    private void bindingView(View view)
    {
        AddFriendFragment chatListFragment = (AddFriendFragment) getChildFragmentManager().findFragmentById(R.id.addFriendFragment);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.navigation_drawer_bottom));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHalfExpandedRatio( 0.6f);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    chatListFragment.setProgress(0);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if(slideOffset >0.6f)
                {
                    chatListFragment.setProgress(slideOffset);
                }
            }
        });

        chatBtn = (Button) view.findViewById(R.id.chatButton);

        userBtn = (Button) view.findViewById(R.id.userButton);

        mapBtn= view.findViewById(R.id.mapButton);

        friendBtn = view.findViewById(R.id.friendButton);


    }
    private void setEventListener() {
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
        });}
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

}