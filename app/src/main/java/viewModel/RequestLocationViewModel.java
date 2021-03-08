package viewModel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import data.LoginRepository;

public class RequestLocationViewModel extends ViewModel {
    MutableLiveData<Boolean> hasPermission = new MutableLiveData<Boolean>();
    public void init(Context context){
        boolean hasBackgroundPermission=(ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION ) ==
                PackageManager.PERMISSION_GRANTED);
        boolean hasForegroundPermission =(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED);
        hasPermission.setValue(hasBackgroundPermission&&hasForegroundPermission);
    }
    public LiveData<Boolean> getHasPermission() {
        return hasPermission;
    }
}
