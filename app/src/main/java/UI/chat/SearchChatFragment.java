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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.ListenerRegistration;
import com.study.android_zenly.R;

import java.util.Objects;

import UI.MainActivity.MainActivity;
import adapter.ChatListAdapter;
import data.models.Conversation;
import ultis.FragmentTag;
import viewModel.ChatListViewModel;
import viewModel.UserViewModel;


public class SearchChatFragment extends Fragment implements ChatListAdapter.OnChatListListener{
    ChatListAdapter madapter;
    Button cancelBtn;
    EditText searchText;
    MotionLayout homeFragmentMotionLayout;
    NavController navController;
    private ListenerRegistration listConvListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        navController = Navigation.findNavController(view);

        homeFragmentMotionLayout = (MotionLayout) getActivity().findViewById(R.id.motion_layout);

        cancelBtn.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });
        searchText= view.findViewById(R.id.searchInput);

        searchText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        RecyclerView nav_drawer_recycler_view = view.findViewById(R.id.nav_drawer_recycler_view);
//        ChatListAdapter adapter = new ChatListAdapter(getActivity(),this);
//        nav_drawer_recycler_view.setAdapter(adapter);
//        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

//        view.setPadding(0,getStatusBarHeight(),0,0);
        UserViewModel mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        ChatListViewModel mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatListViewModel.class);
        ChatListAdapter.OnChatListListener onChatListListener = this;
        madapter = new ChatListAdapter(getActivity(),onChatListListener);
        mUserViewModel.getIsInited().observe(getViewLifecycleOwner(),new Observer<Boolean>(){
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    mChatListViewModel.init(nav_drawer_recycler_view,listConvListener,madapter,mUserViewModel,getActivity(),getViewLifecycleOwner());
                    mChatListViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean isInited) {
                            if (isInited) {
                                nav_drawer_recycler_view.setAdapter(madapter);
                                nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                                mChatListViewModel.getIsInited().removeObserver(this);
                                mChatListViewModel.setIsInitedFalse();
                            }
                        }
                    });
                    mUserViewModel.getIsInited().removeObserver(this);
                }
            }
        });
        searchText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                madapter.getFilter().filter(s);
            }
        });

    }

    @Override
    public void onChatClick(int position) {
        MainActivity activity = (MainActivity) getActivity();
        activity.setFragmentTag(FragmentTag.CHAT,homeFragmentMotionLayout,navController);
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus()!=null)
        {inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);}
                Bundle args = new Bundle();
        Conversation aConv = madapter.getConv(position);
        String name = aConv.getName();
        String id = aConv.getID();
        Log.d("Them ID ne",id);
        args.putString("name",name);
        args.putString("id",id);
        navController.navigate(R.id.action_searchChatFragment_to_chatFragment2,args);
    }

    @Override
    public void onLongChatClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"Delete"},new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Delete Conversation Function
            }
        });
        builder.create().show();
    }
}