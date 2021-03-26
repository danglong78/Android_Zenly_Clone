package UI.friend;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import UI.MainActivity.AddFriendsFragmentCallback;
import UI.MainActivity.HomeFragment;
import UI.MainActivity.MainActivity;
import adapter.FriendSuggestListAdapter;
import adapter.RecentFriendListAdapter;
import data.models.User;
import viewModel.FriendSuggestViewModel;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;

public class AddFriendFragment extends Fragment implements AddFriendsFragmentCallback {

    private final String TAG = "AddFriendFragment";
    private final int CONTACT_REQUEST_ID = 10;
    private FriendSuggestViewModel mViewModel;
    TextView friendListText;

    public static AddFriendFragment newInstance() {
        return new AddFriendFragment();
    }
    RecyclerView friendSuggestRecyclerView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel=new ViewModelProvider(requireActivity()).get(FriendSuggestViewModel.class);
        LoginViewModel loginviewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        RequestLocationViewModel requestLocationViewModel = new ViewModelProvider(requireActivity()).get(RequestLocationViewModel.class);
        loginviewModel.init(getActivity());
        requestLocationViewModel.init(getActivity());
        RecyclerView friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);
        RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);

        Log.d(TAG, String.valueOf(friendSuggestRecyclerView.getId()));
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if(loginviewModel.getAuthentication().getValue() && requestLocationViewModel.getHasPermission().getValue()) {
                    mViewModel.init(getActivity(), FirebaseAuth.getInstance().getUid());
                    Log.d(TAG, "Dang chay ne");

//                    RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);
                    RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter();
                    recentFriendRecyclerView.setAdapter(recentFriendAdapter);
                    recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

                    mViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                        @Override
                        public void onChanged(List<User> users) {
                            FriendSuggestListAdapter adapter = new FriendSuggestListAdapter(getActivity(), (ArrayList<User>)mViewModel.getSuggestFriendList().getValue(),AddFriendFragment.this);
                            friendSuggestRecyclerView.setAdapter(adapter);
                            friendSuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            mViewModel.getSuggestFriendList().removeObserver(this);
                        }
                    });

                    Log.d(TAG, "Dang chay ne2");
                    NavController navController= Navigation.findNavController(requireActivity(),R.id.friend_nav_host_fragment);
                    friendListText= view.findViewById(R.id.friendSetting);
                    friendListText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HomeFragment fragment = (HomeFragment) getParentFragment().getParentFragment();
                        assert fragment != null;
                        fragment.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                        navController.navigate(R.id.action_addFriendFragment_to_searchFriendFragment);

                        }
                    });
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},CONTACT_REQUEST_ID);
                }
                userViewModel.getIsInited().removeObserver(this);
            }

            });

//            friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);
//        if(loginviewModel.getAuthentication().getValue() && requestLocationViewModel.getHasPermission().getValue()) {
//            mViewModel = new FriendSuggestViewModel();
//            if( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)==
//                    PackageManager.PERMISSION_GRANTED)
//            {
//                mViewModel.init(getActivity());
//
//                Log.d(TAG, "onViewCreated: " + mViewModel.getSuggestFriendList().getValue());
//
//                FriendSuggestListAdapter adapter = new FriendSuggestListAdapter(getActivity(), (ArrayList<User>) mViewModel.getSuggestFriendList().getValue(),this);
//                friendSuggestRecyclerView.setAdapter(adapter);
//                friendSuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//                RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);
//                RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter();
//                recentFriendRecyclerView.setAdapter(recentFriendAdapter);
//                recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//
//
//                mViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
//
//                    @Override
//                    public void onChanged(List<User> users) {
//                        Log.d(TAG, "onChanged: users.size() " + users.size());
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//
//
//            }
//            else{
//                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},CONTACT_REQUEST_ID);
//            }
//
//            RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter();
//            RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);
//            recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//
//
//            NavController navController= Navigation.findNavController(requireActivity(),R.id.friend_nav_host_fragment);
//            friendListText= view.findViewById(R.id.friendSetting);
//            friendListText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    HomeFragment fragment = (HomeFragment) getParentFragment().getParentFragment();
//                    assert fragment != null;
//                    fragment.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
//                    navController.navigate(R.id.action_addFriendFragment_to_searchFriendFragment);
//
//                }
//            });
//
//
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(FriendSuggestViewModel.class);
        // TODO: Use the ViewModel
        Log.d(TAG, "onActivityCreated: Run roi nha");

    }


    @Override
    public void onAddButtonClick(String hostUID, String FriendUID) {
        /* Tham hàm addFriend vào đây
         *
         *
         *
         */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            mViewModel.init(getActivity(), FirebaseAuth.getInstance().getUid());

            Log.d(TAG, "onViewCreated: " + mViewModel.getSuggestFriendList().getValue());

            FriendSuggestListAdapter adapter = new FriendSuggestListAdapter(getActivity(), (ArrayList<User>) mViewModel.getSuggestFriendList().getValue(),this);
            friendSuggestRecyclerView.setAdapter(adapter);
            friendSuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {

                @Override
                public void onChanged(List<User> users) {
                    Log.d(TAG, "onChanged: users.size() " + users.size());
                    adapter.notifyDataSetChanged();
                }
            });


        }

    }
}