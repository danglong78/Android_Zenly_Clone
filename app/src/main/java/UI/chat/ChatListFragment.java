package UI.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.google.firebase.firestore.ListenerRegistration;
import com.study.android_zenly.R;

import java.util.List;

import UI.MainActivity.MainActivity;
import adapter.ChatListAdapter;
import data.models.Conversation;
import ultis.FragmentTag;
import viewModel.ChatListViewModel;
import viewModel.LoginViewModel;
import viewModel.RequestLocationViewModel;
import viewModel.UserViewModel;


public class ChatListFragment extends Fragment implements ChatListAdapter.OnChatListListener {
    private NavController navController;
    MotionLayout homeFragmentMotionLayout;
    TextView searchText;
    private ChatListViewModel mChatListViewModel;
    private ChatListAdapter madapter;
    private ListenerRegistration listConvListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        homeFragmentMotionLayout = (MotionLayout) requireActivity().findViewById(R.id.motion_layout);
        searchText= view.findViewById(R.id.searchView);
        searchText.setOnClickListener(v->{
            homeFragmentMotionLayout.setTransition(R.id.left,R.id.hideLeft);
            homeFragmentMotionLayout.setProgress(1);
            MainActivity activity = (MainActivity) getActivity();
            activity.setFragmentTag(FragmentTag.CHAT,homeFragmentMotionLayout,navController);
            navController.navigate(R.id.action_chatListFragment_to_searchChatFragment);

        });
        LoginViewModel loginviewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        RequestLocationViewModel requestLocationViewModel = new ViewModelProvider(requireActivity()).get(RequestLocationViewModel.class);
        loginviewModel.init(getActivity());
        requestLocationViewModel.init(getActivity());
        if(loginviewModel.getAuthentication().getValue() && requestLocationViewModel.getHasPermission().getValue())
        {
            RecyclerView nav_drawer_recycler_view = view.findViewById(R.id.nav_drawer_recycler_view);

//        view.setPadding(0,getStatusBarHeight(),0,0);
            UserViewModel mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
            ChatListAdapter.OnChatListListener onChatListListener = this;
            madapter = new ChatListAdapter(getActivity(),onChatListListener);
            if(mUserViewModel.getIsInited().getValue()==null){
                mUserViewModel.getIsInited().observe(getViewLifecycleOwner(),new Observer<Boolean>(){
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean){
                            mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatListViewModel.class);
                            mChatListViewModel.init(nav_drawer_recycler_view,listConvListener,madapter,mUserViewModel,getActivity(),getViewLifecycleOwner());

                            mChatListViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean isInited) {
                                    if (isInited) {
                                        nav_drawer_recycler_view.setAdapter(madapter);
                                        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        mChatListViewModel.getIsInited().removeObserver(this);
                                    }
                                }
                            });
                            mUserViewModel.getIsInited().removeObserver(this);
                        }
                    }
                });
            }
        }
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
        activity.setFragmentTag(FragmentTag.CHAT,homeFragmentMotionLayout,navController);
        Bundle args = new Bundle();
        Conversation aConv = madapter.getConv(position);
        String name = aConv.getName();
        String id = aConv.getID();
        Log.d("Them ID ne",id);
        args.putString("name",name);
        args.putString("id",id);
        navController.navigate(R.id.action_chatListFragment_to_chatFragment,args);
    }

    @Override
    public void onLongChatClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"Delete"},new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Delete Conversation Function
                madapter.deleteConversation( position);
            }
        });
        builder.create().show();
    }
}