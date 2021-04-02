package UI.friend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.study.android_zenly.BuildConfig;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import UI.MainActivity.HomeFragment;
import UI.MainActivity.MainActivity;
import adapter.FriendSuggestListAdapter;
import adapter.RecentFriendListAdapter;
import data.models.User;
import ultis.FragmentTag;
import viewModel.FriendSuggestViewModel;
import viewModel.FriendViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;
import viewModel.LoginViewModel;
import viewModel.MapViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;

public class AddFriendFragment extends Fragment implements FriendSuggestListAdapter.AddFriendsFragmentCallback, RecentFriendListAdapter.Callback {

    private final String TAG = "AddFriendFragment";
    private final int CONTACT_REQUEST_ID = 10;
    TextView friendListText, friendRequestText;
    SwipeRefreshLayout swiperefreshlayout;
    //    public static AddFriendFragment newInstance() {
//        return new AddFriendFragment();
//    }
    RecyclerView friendSuggestRecyclerView;
    FriendSuggestListAdapter adapter;
    private FriendSuggestViewModel friendSuggestViewModel;
    private InvitationViewModel invitationViewModel;
    private LoginViewModel loginviewModel;
    private RequestLocationViewModel requestLocationViewModel;
    private UserViewModel userViewModel;
    private InvitingViewModel invitingViewModel;
    private FriendViewModel friendViewModel;
    private MapViewModel mapViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendSuggestViewModel = new ViewModelProvider(requireActivity()).get(FriendSuggestViewModel.class);
        invitationViewModel = new ViewModelProvider(requireActivity()).get(InvitationViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        invitingViewModel = new ViewModelProvider(requireActivity()).get(InvitingViewModel.class);
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);


        friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);
        RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);

        Log.d(TAG, String.valueOf(friendSuggestRecyclerView.getId()));

        adapter = new FriendSuggestListAdapter(requireActivity(), this);
        friendSuggestRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendSuggestRecyclerView.setLayoutManager(layoutManager);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {

            friendSuggestViewModel.init(getActivity());
            invitationViewModel.init(getActivity());
            invitingViewModel.init(getActivity());
            friendViewModel.init(requireActivity(), getViewLifecycleOwner());

            Log.d(TAG, "Dang chay ne");

            swiperefreshlayout = view.findViewById(R.id.swiperefresh);
            swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    friendSuggestViewModel.load();
                    CountDownTimer countdown = new CountDownTimer(5000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            swiperefreshlayout.setRefreshing(false);
                        }
                    }.start();
                }
            });


            RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter(requireActivity(), AddFriendFragment.this);
            recentFriendRecyclerView.setAdapter(recentFriendAdapter);
            recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            friendViewModel.getFriendsList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                @Override
                public void onChanged(List<User> users) {
                    recentFriendAdapter.setList((ArrayList<User>) users);
                    recentFriendAdapter.notifyDataSetChanged();
                }
            });

            friendSuggestViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                @Override
                public void onChanged(List<User> users) {
                    swiperefreshlayout.setRefreshing(false);
                    adapter.setItems((ArrayList<User>) users);
                    adapter.notifyDataSetChanged();
                }
            });


            Log.d(TAG, "Dang chay ne2");
            NavController navController = Navigation.findNavController(requireActivity(), R.id.friend_nav_host_fragment);
            friendListText = view.findViewById(R.id.friendSetting);
            friendListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeFragment fragment = (HomeFragment) getParentFragment().getParentFragment();
                    assert fragment != null;
                    fragment.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                    navController.navigate(R.id.action_addFriendFragment_to_searchFriendFragment);
                    ((MainActivity) getActivity()).setFragmentTag(FragmentTag.FRIEND, null, navController);


                }
            });
            friendRequestText = view.findViewById(R.id.friendRequest);
            friendRequestText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    HomeFragment fragment = (HomeFragment) getParentFragment().getParentFragment();
                    assert fragment != null;
                    fragment.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                    navController.navigate(R.id.action_addFriendFragment2_to_friendRequestFragment);
                    ((MainActivity) getActivity()).setFragmentTag(FragmentTag.FRIEND, null, navController);
                }
            });


        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_REQUEST_ID);
        }

    }


    @Override
    public void onAddButtonClick(String friendUID) {
        Log.d(TAG, "onAddButtonClick: ");
        invitationViewModel.sendInvitation(friendUID);
        friendSuggestViewModel.hideSuggest(friendUID);
        invitingViewModel.addToMyInviting(friendUID);
    }

    @Override
    public void onHideClick(String suggestUID) {
        Log.d(TAG, "onHideClick: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want hide this one ?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                friendSuggestViewModel.hideSuggest(suggestUID);
            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            AddFriendFragment.this.onCreate(null);
        } else {
            String permissionDeniedExplanation = "We need your permission to run app";
            Snackbar.make(
                    getActivity().findViewById(R.id.homeFragment),
                    "Need to acceppt permission",
                    Snackbar.LENGTH_LONG
            ).setAction("Setting", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID,
                            null
                    );
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ;
                }

            }).show();
        }

    }

    @Override
    public void onClick(String UID) {
        mapViewModel.focusOnFriend(UID);
    }
}