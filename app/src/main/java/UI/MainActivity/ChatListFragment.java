package UI.MainActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.study.android_zenly.R;

import adapter.ChatListAdapter;
import ultis.FragmentTag;


public class ChatListFragment extends Fragment implements ChatListAdapter.OnChatListListener {
    private NavController navController;
    MotionLayout homeFragmentMotionLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        View homeFragment = (View) getActivity().findViewById(R.id.main_nav_host_fragment);
        navController = Navigation.findNavController(view);
        homeFragmentMotionLayout = (MotionLayout) getActivity().findViewById(R.id.motion_layout);


        super.onViewCreated(view, savedInstanceState);
        RecyclerView nav_drawer_recycler_view = view.findViewById(R.id.nav_drawer_recycler_view);
//        view.setPadding(0,getStatusBarHeight(),0,0);
        ChatListAdapter adapter = new ChatListAdapter(getActivity(),this);
        nav_drawer_recycler_view.setAdapter(adapter);
        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onChatClick(int position) {
        homeFragmentMotionLayout.setTransition(R.id.left,R.id.hideLeft);
        homeFragmentMotionLayout.setProgress(1);
        MainActivity activity = (MainActivity) getActivity();
        activity.setFragmentTag(FragmentTag.CHAT,homeFragmentMotionLayout);
        navController.navigate(R.id.action_chatListFragment_to_chatFragment2);
    }
}