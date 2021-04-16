package UI.MainActivity;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import ultis.FragmentTag;

public interface MainCallBacks {
    void setFragmentTag(FragmentTag tag, MotionLayout motion, NavController navController);
    void setFragmentTag(FragmentTag tag, MotionLayout motion, DrawerLayout drawer);
    void setFragmentTag(FragmentTag tag, MotionLayout motion, BottomSheetBehavior bottomSheetBehavior);

}
