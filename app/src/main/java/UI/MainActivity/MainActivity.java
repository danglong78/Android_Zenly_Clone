package UI.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.study.android_zenly.R;

import ultis.FragmentTag;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;

public class MainActivity extends AppCompatActivity implements MainCallBacks{
    private FragmentTag tag;
    private MotionLayout motionLayout;
    private NavController chatnavController,navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tag=FragmentTag.OTHERS;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch(tag){

        case CHAT:
        {
            motionLayout.setTransition(R.id.hideLeft,R.id.left);
            motionLayout.setProgress(1);
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null)
            {inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), 0);}
            tag=FragmentTag.OTHERS;
            break;
        }
            case SETTINGS:
            {
//                motionLayout.setTransition(R.id.hideRight,R.id.right);
                motionLayout.setProgress(1);
                break;
            }
            case OTHERS:
            {

                break;
            }

        }
    }

    @Override
    public void setFragmentTag(FragmentTag tag,MotionLayout motion) {
        this.tag= tag;
        motionLayout=motion;
    }

}