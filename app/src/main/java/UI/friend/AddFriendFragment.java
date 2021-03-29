package UI.friend;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import ultis.PaginationListener;
import viewModel.FriendSuggestViewModel;
import viewModel.InvitationViewModel;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;

import static ultis.PaginationListener.PAGE_START;

public class AddFriendFragment extends Fragment implements FriendSuggestListAdapter.AddFriendsFragmentCallback{

    private final String TAG = "AddFriendFragment";
    private final int CONTACT_REQUEST_ID = 10;

    private FriendSuggestViewModel friendSuggestViewModel;
    private InvitationViewModel invitationViewModel;
    private LoginViewModel loginviewModel;
    private RequestLocationViewModel requestLocationViewModel;

    TextView friendListText,friendRequestText;

//    public static AddFriendFragment newInstance() {
//        return new AddFriendFragment();
//    }
    RecyclerView friendSuggestRecyclerView;
    FriendSuggestListAdapter adapter;
    private int currentPage = PAGE_START;
    private int totalPage = 10;
    private boolean isLoading = false;
    private boolean isInited=false;
    int itemCount = 0;


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
        loginviewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        requestLocationViewModel = new ViewModelProvider(requireActivity()).get(RequestLocationViewModel.class);

        loginviewModel.init(getActivity());
        requestLocationViewModel.init(getActivity());

        friendSuggestRecyclerView = view.findViewById(R.id.suggest_friend_recycler_view);
        RecyclerView recentFriendRecyclerView = view.findViewById(R.id.recent_friend_recycler_view);

        Log.d(TAG, String.valueOf(friendSuggestRecyclerView.getId()));

        adapter= new FriendSuggestListAdapter(requireActivity(),this);
        friendSuggestRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getActivity());
        friendSuggestRecyclerView.setLayoutManager(layoutManager);

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (loginviewModel.getAuthentication().getValue() && requestLocationViewModel.getHasPermission().getValue()) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) ==
                            PackageManager.PERMISSION_GRANTED) {

                        friendSuggestViewModel.init(getActivity());
                        invitationViewModel.init(getActivity());

                        Log.d(TAG, "Dang chay ne");



                        RecentFriendListAdapter recentFriendAdapter = new RecentFriendListAdapter();
                        recentFriendRecyclerView.setAdapter(recentFriendAdapter);
                        recentFriendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        friendSuggestViewModel.getSuggestFriendList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                            @Override
                            public void onChanged(List<User> users) {
                                if(isInited==true) {
                                    isLoading = false;
                                    adapter.removeLoading();
                                    adapter.setItems((ArrayList<User>) users);
                                    adapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    adapter.setItems((ArrayList<User>) users);
                                    adapter.notifyDataSetChanged();
                                    isInited=true;
                            }
                        }});
                        friendSuggestRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
                            @Override
                            protected void loadMoreItems() {
                                isLoading=true;
                                adapter.addLoading();
                                friendSuggestViewModel.load();
                            }

                            @Override
                            public boolean isLoading() {
                                return isLoading;
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
                                ((MainActivity)getActivity()).setFragmentTag(FragmentTag.FRIEND,null,navController);


                            }
                        });
                        friendRequestText= view.findViewById(R.id.friendRequest);
                        friendRequestText.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                HomeFragment fragment = (HomeFragment) getParentFragment().getParentFragment();
                                assert fragment != null;
                                fragment.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                                navController.navigate(R.id.action_addFriendFragment2_to_friendRequestFragment);
                                ((MainActivity)getActivity()).setFragmentTag(FragmentTag.FRIEND,null,navController);
                            }
                        });

                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_REQUEST_ID);
                    }
                    userViewModel.getIsInited().removeObserver(this);
                }

            }

        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(FriendSuggestViewModel.class);
        // TODO: Use the ViewModel
        Log.d(TAG, "onActivityCreated: Run roi nha");

    }


    @Override
    public void onAddButtonClick(String friendUID) {
        Log.d(TAG, "onAddButtonClick: ");
        invitationViewModel.sendInvitation(friendUID);
        friendSuggestViewModel.hideSuggest(friendUID);
    }

    @Override
    public void onHideClick(String suggestUID) {
        Log.d(TAG, "onHideClick: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want hide this one ?");
        builder.setNegativeButton("No",null);
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

}