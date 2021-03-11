package UI.MainActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import adapter.ChatListAdapter;
import adapter.HomeViewPagerAdapter;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;

public class HomeFragment extends Fragment {
    NavController navController;
    LoginViewModel loginviewModel;
    RequestLocationViewModel requestLocationViewModel;
    Button chatBtn,userBtn;
    DrawerLayout drawerLayout;
    MotionLayout motionLayout;
    BottomSheetBehavior bottomSheetBehavior;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        drawerLayout= rootView.findViewById(R.id.drawerlayout);
        motionLayout=  rootView.findViewById(R.id.motion_layout);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int parentHeight= view.getHeight();
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.navigation_drawer_bottom));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if(drawerView.getId()==R.id.navigation_view_left)
                {
                    motionLayout.setTransition(R.id.chatButtonAnimation);
                }
                else
                {
                    motionLayout.setTransition(R.id.userButtonAnimation);

                }
                motionLayout.setProgress(slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        chatBtn= (Button)view.findViewById(R.id.chatButton);
        chatBtn.setOnClickListener(v -> {
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                return;
            }
            if(drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
            drawerLayout.openDrawer(Gravity.LEFT);
        });
        chatBtn.bringToFront();
        userBtn=(Button) view.findViewById(R.id.userButton);
        userBtn.bringToFront();
        userBtn.setOnClickListener(v -> {
            if(drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
                return;
            }
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            drawerLayout.openDrawer(Gravity.RIGHT);
        });

        navController= Navigation.findNavController(view);
        loginviewModel= new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginviewModel.init(getActivity());
        loginviewModel.getAuthentication().observe(getViewLifecycleOwner(),(Observer<Boolean>) isAuthenticated ->{
            if(isAuthenticated)
            {

                requestLocationViewModel = new ViewModelProvider(getActivity()).get(RequestLocationViewModel.class);
                requestLocationViewModel.init(getActivity());
                requestLocationViewModel.getHasPermission().observe(getViewLifecycleOwner(),(Observer<Boolean>) hasPermission ->{
                    if(hasPermission) {

                    }
                    else{
                        navController.navigate(R.id.action_homeFragment_to_request_location_nav);

                    }
                });
            }
            else{
                navController.navigate(R.id.action_homeFragment_to_login_nav);
                ;
            }
        });

    }
}