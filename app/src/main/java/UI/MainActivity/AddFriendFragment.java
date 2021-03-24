package UI.MainActivity;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendSuggestListAdapter;
import adapter.RecentFriendListAdapter;
import data.models.User;
import ultis.FragmentTag;
import viewModel.FriendSuggestViewModel;
import viewModel.LoginViewModel;

public class AddFriendFragment extends Fragment implements AddFriendsFragmentCallback{

    private final String TAG = "AddFriendFragment";
    private final int CONTACT_REQUEST_ID = 10;
    private FriendSuggestViewModel mViewModel;
    private MotionLayout motionLayout;
    TextView searchText;

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

        LoginViewModel loginviewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginviewModel.init(getActivity());
        if(loginviewModel.getAuthentication().getValue()) {
            mViewModel = new FriendSuggestViewModel();
            if( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)==
                    PackageManager.PERMISSION_GRANTED)
            {
                mViewModel.init(getActivity());

            }
            else{requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},CONTACT_REQUEST_ID);}

            RecyclerView friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);

            Log.d(TAG, "onViewCreated: " + mViewModel.getSuggestFriendList().getValue());

            FriendSuggestListAdapter adapter = new FriendSuggestListAdapter(getActivity(), (ArrayList<User>) mViewModel.getSuggestFriendList().getValue());
            friendSuggestRecyclerView.setAdapter(adapter);
            friendSuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);
            RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter();
            recentFriendRecyclerView.setAdapter(recentFriendAdapter);
            recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

            motionLayout = view.findViewById(R.id.friend_motion_layout);
            motionLayout.setTransition(R.id.start, R.id.end);

            mViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {

                @Override
                public void onChanged(List<User> users) {
                    Log.d(TAG, "onChanged: users.size() " + users.size());
                    adapter.notifyDataSetChanged();
                }
            });
            TextView searchText;
            searchText= view.findViewById(R.id.searchView);
            searchText.setOnClickListener(v->{

                MainActivity activity = (MainActivity) getActivity();
//                navController.navigate(R.id.action_chatListFragment_to_searchChatFragment);

            });

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(FriendSuggestViewModel.class);
        // TODO: Use the ViewModel
        Log.d(TAG, "onActivityCreated: Run roi nha");

    }

    @Override
    public void setProgress(float progress) {
        motionLayout.setProgress(progress);
    }
}