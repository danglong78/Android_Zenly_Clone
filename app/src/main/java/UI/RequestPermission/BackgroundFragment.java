package UI.RequestPermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.study.android_zenly.BuildConfig;
import com.study.android_zenly.R;


public class BackgroundFragment extends Fragment {

    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_background, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        view.findViewById(R.id.SettingsButton).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
            );
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        view.setPadding(0,getStatusBarHeight()+30,0,0);
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            if(hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                Toast.makeText(getActivity(),
                        "Location permission is approved success",
                        Toast.LENGTH_LONG).show();
                navController.navigate(R.id.action_global_homeFragment);
            }

        }
        else
        {
            navController.navigate(R.id.action_backgroundFragment_to_deniedFragment);
        }


    }
    private Boolean hasPermission(String permission) {

        // Background permissions didn't exit prior to Q, so it's approved by default.
        if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            return true;
        }

        return ActivityCompat.checkSelfPermission(getActivity(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
}