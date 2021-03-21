package data.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

/*
    Singleton Pattern
*/
public class LoginRepository {
    private static LoginRepository instance;
    private boolean isAuthenticated;
//    private String name;
//    private String Dob;
//    private String Phone;
    public static LoginRepository getInstance(){
        if(instance == null){
            instance= new LoginRepository();
        }
        return instance;
    }
    public MutableLiveData<Boolean> getIsAuthenticated(Context activity){
        MutableLiveData<Boolean> data= new MutableLiveData<Boolean>();
        checkIsAuthenticated(activity);
        data.setValue(isAuthenticated);
        return data;
    }
    public void checkIsAuthenticated(Context activity) {
        SharedPreferences prefs = activity.getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ( (prefs != null) && (prefs.contains("isAuthenticated")) ) {
            this.isAuthenticated=prefs.getBoolean("isAuthenticated",false);
            return;
        }
        this.isAuthenticated=false;
    }
}
