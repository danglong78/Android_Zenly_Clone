package viewModel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

import data.models.User;

public class MapViewModel extends ViewModel {
    private final String TAG = "MapViewModel";

    private GoogleMap mMap;
    private Marker hostMarker = null;
    private LocationRequest locationRequest;

    public GoogleMap getMap() {
        return mMap;
    }

    public void setMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void init(Context activity) {


    }



    public void moveTo(LatLng location) {
        Log.d(TAG, "moveTo: " + location);
        Log.d(TAG, "moveTo: " + mMap);

        if (hostMarker != null) {
            hostMarker.remove();
        }
        hostMarker = mMap.addMarker(new MarkerOptions().position(location).title("You are now here").visible(true));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
    }
}
