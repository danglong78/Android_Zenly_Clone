package ultis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.study.android_zenly.R;

import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;

public class UpdateLocationWorker extends Worker {
    private final String TAG = "UpdateLocationWorker";
    private final String USER_COLLECTION = "Users";
    private final String USER_LOCATION_COLLECTION = "UserLocations";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private String userUID;
    private Context mContext;
    DocumentReference mHostUserLocationRef;
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder notiBuilder;


    public UpdateLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;

        userUID = getInputData().getString("userUID");
        notificationManager = NotificationManagerCompat.from(mContext);
        notiBuilder = new NotificationCompat.Builder(mContext, "ZenlyNotiChanel")
                .setSmallIcon(R.drawable.flag_vietnam)
                .setContentTitle("ZenlyClone background service")
                .setContentText("Updating your location")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        mHostUserLocationRef = FirebaseFirestore.getInstance().collection(USER_LOCATION_COLLECTION).document(userUID);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: start location update");

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        Boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            Log.d(TAG, "doWork: isGpsEnabled false");
            return Result.failure();
        }

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(123456, notiBuilder.build());

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(mContext);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {

            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                if (task.isSuccessful()) {
                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                return;
                            }
                            Log.d(TAG, "onLocationResult: length " + locationResult.getLocations().size());
                            Location location = locationResult.getLocations().get(0);

                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            HashMap data = new HashMap<String, Object>();
                            data.put("location", geoPoint);
                            mHostUserLocationRef.update(data);

                            Log.d(TAG, "onLocationResult: " + location);
                            Log.d(TAG, "Finished updating location");

                            mFusedLocationClient.removeLocationUpdates(this);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    // Start location updates
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest,
                                locationCallback,
                                Looper.getMainLooper());
                    }
                }
            }
        });
        return Result.success();
    }
}
