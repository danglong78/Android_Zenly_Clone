package UI.MainActivity;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.navigation.NavController;

import ultis.FragmentTag;

public interface MainCallBacks {
    void setFragmentTag(FragmentTag tag, MotionLayout motion, NavController navController);
}
