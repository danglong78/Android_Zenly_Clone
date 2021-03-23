package UI.MainActivity;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.android_zenly.R;

import adapter.FriendSuggestListAdapter;
import adapter.RecentFriendListAdapter;
import viewModel.FriendSuggestViewModel;

public class AddFriendFragment extends Fragment implements AddFriendsFragmentCallback{

    private FriendSuggestViewModel mViewModel;
    private MotionLayout motionLayout;
    public static AddFriendFragment newInstance() {
        return new AddFriendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);
        FriendSuggestListAdapter adapter = new FriendSuggestListAdapter(getActivity());
        friendSuggestRecyclerView.setAdapter(adapter);
        friendSuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);
        RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter();
        recentFriendRecyclerView.setAdapter(recentFriendAdapter);
        recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        motionLayout=view.findViewById(R.id.friend_motion_layout);
        motionLayout.setTransition(R.id.start,R.id.end);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(FriendSuggestViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void setProgress(float progress) {
        motionLayout.setProgress(progress);
    }
}