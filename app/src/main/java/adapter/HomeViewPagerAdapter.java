package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import UI.MainActivity.ChatListFragment;
import UI.MainActivity.MapsFragment;
import UI.MainActivity.UserFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {

    public HomeViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new ChatListFragment();
        }
        else if(position == 1) {
            return new MapsFragment();

        }
        else{
                return new UserFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
