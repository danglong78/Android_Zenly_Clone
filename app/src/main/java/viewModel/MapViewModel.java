package viewModel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.models.ClusterMarker;
import data.models.User;
import data.models.UserLocation;
import data.models.UserRef;
import data.models.UserRefFriend;
import data.repositories.UserRepository;
import ultis.ClusterManagerRenderer;

public class MapViewModel extends ViewModel {
    private final String TAG = "MapViewModel";
    private static final int REQUEST_CHECK_SETTINGS = 10001;

    MutableLiveData<UserLocation> mUserLocation = new MutableLiveData<UserLocation>();
    DocumentReference mHostUserLocationRef;

    private GoogleMap mMap;
    private ClusterMarker mHostMarker = null;
    private List<ClusterMarker> mFriendMarkers = new ArrayList<ClusterMarker>();

    private LiveData<List<UserLocation>> mFriendLocationList;
    private LiveData<List<UserRefFriend>> mFriendRefList;

    private LocationRequest locationRequest;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ClusterManager mClusterManager;

    private Context activity;
    private MutableLiveData<Boolean> isInited = new MutableLiveData<Boolean>();


    public void setActivity(Context activity) {
        this.activity = activity;
    }

    public void setMap(GoogleMap mMap) {
        this.mMap = mMap;

        mClusterManager = new ClusterManager<ClusterMarker>(activity.getApplicationContext(), mMap);
        mClusterManagerRenderer = new ClusterManagerRenderer(
                activity,
                mMap,
                mClusterManager
        );
        mClusterManager.setRenderer(mClusterManagerRenderer);
    }

    public void init(LifecycleOwner lifecycleOwner) {

        mUserLocation = UserRepository.getInstance().getHostUserLocation(activity);

        mUserLocation.observe(lifecycleOwner, new Observer<UserLocation>() {
            @Override
            public void onChanged(UserLocation userLocation) {
                if (isInited.getValue() == null) {
                    Log.d(TAG, "onChanged: user location is inited");

                    //  Add host marker
                    mHostMarker = new ClusterMarker(
                            new LatLng(userLocation.getLocation().getLatitude(), userLocation.getLocation().getLongitude()),
                            userLocation.getName(),
                            "You are now here",
                            userLocation.getUserUID(),
                            userLocation.getImageURL()
                    );
                    mClusterManager.addItem(mHostMarker);
                    Log.d(TAG, "addMarker: addItem host " + mHostMarker.getUserUID());
                    moveTo(new LatLng(userLocation.getLocation().getLatitude(), userLocation.getLocation().getLongitude()));

                    // Add friend markers
                    FriendViewModel mFriendViewModel = new ViewModelProvider((FragmentActivity) activity).get(FriendViewModel.class);
                    mFriendLocationList = mFriendViewModel.getFriendLocationList();
                    mFriendLocationList.observe(lifecycleOwner, new Observer<List<UserLocation>>() {

                        @Override
                        public void onChanged(List<UserLocation> userLocations) {
                            Log.d(TAG, "onChanged: userLocations " + userLocations.size());
                            for (UserLocation userLocation : userLocations) {
                                if (userLocation.getUserUID().equals(mHostMarker.getUserUID())) continue;
                                Log.d(TAG, "onChanged: " + userLocation.getUserUID());
                                Log.d(TAG, "onChanged: userLocation " + userLocation.getUserUID());
                                Boolean isExisted = false;
                                for (ClusterMarker clusterMarker : mFriendMarkers) {
                                    if (userLocation.getUserUID().equals(clusterMarker.getUserUID())) {
                                        isExisted = true;
                                        Log.d(TAG, "onChanged: already existed");
                                        break;
                                    }
                                }

                                if (!isExisted) {
                                    addMarker(userLocation, "Your Friend " + userLocation.getName());
                                    Log.d(TAG, "onChanged: Add new marker");
                                }

                                // Update location real time
                                updateMarker(userLocation.getUserUID(), new LatLng(userLocation.getLocation().getLatitude(), userLocation.getLocation().getLongitude()));
                            }
                        }
                    });

                    // Ghost mode
                    mFriendRefList = mFriendViewModel.getFriendsRefList();
                    mFriendRefList.observe(lifecycleOwner, new Observer<List<UserRefFriend>>() {
                        @Override
                        public void onChanged(List<UserRefFriend> userRefs) {

                        }
                    });


                    mClusterManager.cluster();


                    mHostUserLocationRef = UserRepository.getInstance().getUserLocationReference(userLocation.getUserUID());

                    requestLocationUpdate(activity);
                    isInited.postValue(true);
                }
                mUserLocation.removeObserver(this);
            }
        });
    }

    public MutableLiveData<Boolean> getIsInited() {
        return isInited;
    }

    private void getLastLocation(Context activity, User hostUser, DocumentReference hostUserLocationRef) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    Log.d(TAG, "onComplete: task.getResult: " + location);
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    User user = hostUser;
                    HashMap data = new HashMap<String, Object>();
                    data.put("location", geoPoint);
                    hostUserLocationRef.update(data);

//                    moveTo(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        });
    }

    public void requestLocationUpdate(Context activity) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener((Activity) activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener((Activity) activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult((Activity) activity,
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
                mHostMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                updateHostMarker();
//                moveTo(new LatLng(location.getLatitude(), location.getLongitude()));

                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                HashMap data = new HashMap<String, Object>();
                data.put("location", geoPoint);
                mHostUserLocationRef.update(data);

                Log.d(TAG, "onLocationResult: " + location);
            }
        };

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    public void addMarker(UserLocation userLocation, String snippet) {
        ClusterMarker newClusterMarker = new ClusterMarker(
                new LatLng(userLocation.getLocation().getLatitude(), userLocation.getLocation().getLongitude()),
                userLocation.getName(),
                snippet,
                userLocation.getUserUID(),
                userLocation.getImageURL()
        );

        mClusterManager.addItem(newClusterMarker);
        Log.d(TAG, "addMarker func: addItem " + newClusterMarker.getUserUID());
        mFriendMarkers.add(newClusterMarker);
        mClusterManager.cluster();
    }

//    public void addMarkerList(List<UserLocation> userLocations) {
//        for (UserLocation userLocation : userLocations) {
//            ClusterMarker newClusterMarker = new ClusterMarker(
//                    new LatLng(userLocation.getLocation().getLatitude(), userLocation.getLocation().getLongitude()),
//                    userLocation.getName(),
//                    "You are now here",
//                    userLocation.getUserUID(),
//                    userLocation.getImageURL()
//            );
//            mClusterMarkers.add(newClusterMarker);
//            mClusterManager.addItem(newClusterMarker);
//        }
//
//        mClusterManager.cluster();
//    }

    public void updateHostMarker() {
        Log.d(TAG, "updateHostMarker: "  + mClusterManager.updateItem(mHostMarker));
        mClusterManagerRenderer.updateClusterMarker(mHostMarker);
        mClusterManager.cluster();
    }

    public void updateMarker(String uid, LatLng newLocation) {
        for (ClusterMarker marker : mFriendMarkers) {
            if (marker.getUserUID().equals(uid)) {
                Log.d(TAG, "updateMarker: " + marker.getUserUID());
                marker.setPosition(newLocation);
                mClusterManager.updateItem(marker);
                mClusterManagerRenderer.updateClusterMarker(marker);
                mClusterManager.cluster();
                break;
            }
        }
    }

    public void moveTo(LatLng location) {
        Log.d(TAG, "moveTo: " + location);
        Log.d(TAG, "moveTo: " + mMap);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
    }
}
