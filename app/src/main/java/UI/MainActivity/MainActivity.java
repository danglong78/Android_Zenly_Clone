package UI.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import ultis.FragmentTag;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;

public class MainActivity extends AppCompatActivity implements MainCallBacks {
    private FragmentTag tag;
    private MotionLayout motionLayout;
    private NavController navController;
    private DrawerLayout drawer;
    private BottomSheetBehavior bottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tag = FragmentTag.OTHERS;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

    }

    @Override
    public void onBackPressed() {
        switch (tag) {

            case CHAT: {

                motionLayout.setTransition(R.id.hideLeft, R.id.left);
                motionLayout.setProgress(1);
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
                }

                navController.popBackStack();
                tag = FragmentTag.CHATLIST;

                break;
            }
            case SETTINGS: {
                navController.popBackStack();

//                motionLayout.setTransition(R.id.hideRight,R.id.right);
                motionLayout.setTransition(R.id.hideRight, R.id.right);

                motionLayout.setProgress(1);
                tag = FragmentTag.USER;

                break;
            }
            case OTHERS: {
                super.onBackPressed();


                break;
            }
            case FRIEND:
            {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
                }
                navController.popBackStack();
                if(navController.getCurrentDestination().getId()==R.id.addFriendFragment2)
                    tag = FragmentTag.MAINFRIEND;
                break;
            }
            case CREATECHAT: {
                motionLayout.setTransition(R.id.hideLeft, R.id.left);
                motionLayout.setProgress(1);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                tag = FragmentTag.CHATLIST;

                break;
            }
            case CHATLIST:
            {
                motionLayout.setTransition(R.id.left,R.id.start);
                drawer.closeDrawer(Gravity.LEFT);
                tag = FragmentTag.OTHERS;

                break;
            }
            case USER:
            {
                motionLayout.setTransition(R.id.right,R.id.start);
                drawer.closeDrawer(Gravity.RIGHT);
                tag = FragmentTag.OTHERS;
                break;
            }
            case MAINFRIEND:
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                tag = FragmentTag.OTHERS;
                break;
            }


        }

    }

    @Override
    public void setFragmentTag(FragmentTag tag, MotionLayout motion, NavController navController) {
        this.tag = tag;
        motionLayout = motion;
        this.navController = navController;
        Log.d("BackPressed", "onBackPressed: " + tag.name());

    }

    @Override
    public void setFragmentTag(FragmentTag tag, MotionLayout motion, DrawerLayout drawer) {
        this.tag = tag;
        motionLayout = motion;
        this.drawer = drawer;
        Log.d("BackPressed", "onBackPressed: " + tag.name());

    }

    @Override
    public void setFragmentTag(FragmentTag tag, MotionLayout motion, BottomSheetBehavior bottomSheetBehavior) {
        this.tag = tag;
        motionLayout = motion;
        this.bottomSheetBehavior = bottomSheetBehavior;
        Log.d("BackPressed", "onBackPressed: " + tag.name());

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Zenly";
            String description = "Zenly notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ZenlyNotiChanel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}