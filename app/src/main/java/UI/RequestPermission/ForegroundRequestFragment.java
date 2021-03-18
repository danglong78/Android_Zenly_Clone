package UI.RequestPermission;

import android.Manifest;

import android.content.pm.PackageManager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.snackbar.Snackbar;
import com.study.android_zenly.R;

public class ForegroundRequestFragment extends Fragment {
    private static final int REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = "PermissionRequestFrag";
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_foreground_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean permissionApproved = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (!permissionApproved) {
                    requestPermissionWithRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                            fineLocationRationalSnackbar());
                } else {
                    navController.navigate(R.id.action_foregroundRequestFragment_to_backgroundFragment);
                }
            }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            Log.d(TAG, "User interaction was cancelled.");
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            navController.navigate(R.id.action_foregroundRequestFragment_to_backgroundFragment);
        } else {
            navController.navigate(R.id.action_foregroundRequestFragment_to_deniedFragment);
//            String permissionDeniedExplanation ="We need your permission to run app";
//            Snackbar.make(
//                    getActivity().findViewById(R.id.fragment_foreground_request),
//                    "Need to acceppt permission",
//                    Snackbar.LENGTH_LONG
//            ).setAction("Setting",new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts(
//                            "package",
//                            BuildConfig.APPLICATION_ID,
//                            null
//                    );
//                    intent.setData(uri);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);;
//                }

            // Build intent that displays the App settings screen.

//            }).show();
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

    private void requestPermissionWithRationale(String permission, int requestCode, Snackbar snackbar) {
        boolean provideRationale = shouldShowRequestPermissionRationale(permission);

        if (provideRationale) {
            snackbar.show();
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    private Snackbar fineLocationRationalSnackbar() {
        return Snackbar.make(getActivity().findViewById(R.id.fragment_foreground_request),
                "You need to accept permisson for app to work",
                Snackbar.LENGTH_LONG
        ).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
                );
            }
        });
    }
}