package UI.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.ListenerRegistration;
import com.study.android_zenly.R;

import java.util.ArrayList;

import java.io.Serializable;

import UI.MainActivity.MainActivity;
import adapter.ChatListAdapter;
import data.models.Conversation;
import ultis.FragmentTag;
import viewModel.ChatListViewModel;
import viewModel.UserViewModel;


public class ChatListFragment extends Fragment implements ChatListAdapter.OnChatListListener

{
    MotionLayout homeFragmentMotionLayout;
    TextView searchText;
    private NavController navController;
    private ChatListViewModel mChatListViewModel;
    private ChatListAdapter madapter;
    private ListenerRegistration listConvListener;
    private LiveData<ArrayList<Conversation>>convList;
    private BottomSheetBehavior bottomsheet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeFragmentMotionLayout = (MotionLayout) requireActivity().findViewById(R.id.motion_layout);
        navController = Navigation.findNavController(view);
        bottomsheet = BottomSheetBehavior.from(view.findViewById(R.id.chat_navigation_drawer_bottom));
        bottomsheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        settingBottomSheet();

        searchText = view.findViewById(R.id.searchView);
        searchText.setOnClickListener(v -> {
            homeFragmentMotionLayout.setTransition(R.id.left, R.id.hideLeft);
            homeFragmentMotionLayout.setProgress(1);
            MainActivity activity = (MainActivity) getActivity();
            activity.setFragmentTag(FragmentTag.CHAT, homeFragmentMotionLayout, navController);
            navController.navigate(R.id.action_chatListFragment_to_searchChatFragment);
        });

        RecyclerView nav_drawer_recycler_view = view.findViewById(R.id.nav_drawer_recycler_view);
        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        nav_drawer_recycler_view.setItemAnimator(new DefaultItemAnimator());
        madapter = new ChatListAdapter(getActivity(), this);
        mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatListViewModel.class);
        mChatListViewModel.init(getViewLifecycleOwner());
        mChatListViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>(){
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    convList = mChatListViewModel.getConvList();
                    convList.observe(getViewLifecycleOwner(), new Observer<ArrayList<Conversation>>(){
                        @Override
                        public void onChanged(ArrayList<Conversation> conversations) {
                            madapter.setConversationList(conversations);
                            nav_drawer_recycler_view.setAdapter(madapter);
                        }
                    });
                    mChatListViewModel.getIsInited().removeObserver(this);
                }
            }
        });
        Button createChatButton=  view.findViewById(R.id.createChatButton);
        createChatButton.setOnClickListener(v->{

            homeFragmentMotionLayout.setTransition(R.id.left, R.id.hideLeft);
            homeFragmentMotionLayout.transitionToEnd();
            MainActivity activity = (MainActivity) getActivity();
            activity.setFragmentTag(FragmentTag.CREATECHAT, homeFragmentMotionLayout, bottomsheet);
            bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED);

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        convList.removeObservers(getViewLifecycleOwner());
    }


    @Override
    public void onChatClick(int position) {
        homeFragmentMotionLayout.setTransition(R.id.left, R.id.hideLeft);
        homeFragmentMotionLayout.setProgress(1);
        MainActivity activity = (MainActivity) getActivity();
        activity.setFragmentTag(FragmentTag.CHAT, homeFragmentMotionLayout, navController);
        Bundle args = new Bundle();
        Conversation aConv = madapter.getConv(position);
        String name = aConv.getName();
        String id = aConv.getID();
        Log.d("Them ID ne", id);
        args.putString("name", name);
        args.putString("id", id);
        args.putParcelableArrayList("listUserID", aConv.getMember());
        navController.navigate(R.id.action_chatListFragment_to_chatFragment, args);
    }

    @Override
    public void onLongChatClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"Delete"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Delete Conversation Function
                madapter.deleteConversation(position);
            }
        });
        builder.create().show();
    }
    private void settingBottomSheet(){
        bottomsheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState ==BottomSheetBehavior.STATE_HIDDEN)
                {
                    homeFragmentMotionLayout.setTransition(R.id.left, R.id.hideLeft);
                    homeFragmentMotionLayout.setProgress(0f);}
                else
                {
                    homeFragmentMotionLayout.setTransition(R.id.left, R.id.hideLeft);
                    homeFragmentMotionLayout.setProgress(1f);}

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }
    public  void setBottomSheetState(int state) {
        bottomsheet.setState(state);
    }
}