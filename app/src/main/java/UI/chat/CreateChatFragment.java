package UI.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import UI.MainActivity.MainActivity;
import adapter.ChatListAdapter;
import adapter.CreateChatAdapter;
import adapter.GhostModeListAdapter;
import data.models.Conversation;
import data.models.User;
import data.repositories.ConversationRepository;
import data.repositories.UserRepository;
import ultis.FragmentTag;
import viewModel.ChatListViewModel;
import viewModel.FriendViewModel;
import viewModel.UserViewModel;


public class CreateChatFragment extends Fragment implements CreateChatAdapter.onItemClickListener {
    CreateChatAdapter madapter;
    Button cancelBtn;
    EditText searchText;
    MotionLayout homeFragmentMotionLayout,creatChatMotionLayout;
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_chat, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        navController = Navigation.findNavController(getActivity(),R.id.chat_nav_host_fragment);
        homeFragmentMotionLayout = (MotionLayout) getActivity().findViewById(R.id.motion_layout);
        creatChatMotionLayout= (MotionLayout) view.findViewById(R.id.createChatFragment);
        cancelBtn.setOnClickListener(v->{
            ChatListFragment chatListFragment= (ChatListFragment) getParentFragment();
            chatListFragment.setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
        });
        searchText= view.findViewById(R.id.searchInput);


        RecyclerView nav_drawer_recycler_view = view.findViewById(R.id.nav_drawer_recycler_view);
        madapter = new CreateChatAdapter(getActivity(),this);
        nav_drawer_recycler_view.setAdapter(madapter);
        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChatListViewModel mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatListViewModel.class);
        FriendViewModel friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
//        List<User> listUser=new ArrayList<>();
//        listUser.add(new User(null,"Long","1234","7ddee776-92b6-11eb-a8b3-0242ac130003.png","23/9/2000","012345567",null));
//        listUser.add(new User(null,"Dai","12312334","2d6b54f0-92b6-11eb-a8b3-0242ac130003.jpg","23/9/2000","012345567",null));
//        listUser.add(new User(null,"Tri","1233434","0e974d50-8978-11eb-8dcd-0242ac130003.png","23/9/2000","012345567",null));
//        listUser.add(new User(null,"Tam","112234","0e9748c8-8978-11eb-8dcd-0242ac130003.png","23/9/2000","012345567",null));
        friendViewModel.getFriendsList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> listUser) {
                Log.d("CREATCHAT", String.valueOf(listUser.size()));
                madapter.updateListItems(listUser);
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
        Button createBtn= view.findViewById(R.id.createChatBtn);
        createBtn.setOnClickListener(v->{
            for (int childCount = nav_drawer_recycler_view.getChildCount(), i = 0; i < childCount; ++i) {
                final CreateChatAdapter.ViewHolder holder = (CreateChatAdapter.ViewHolder) nav_drawer_recycler_view.getChildViewHolder(nav_drawer_recycler_view.getChildAt(i));
                holder.unCheck();
            }
            ChatListFragment chatListFragment= (ChatListFragment) getParentFragment();
            chatListFragment.setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN);
            //TODO  Create CHat Function
            List<User> users = madapter.getCheckList();
            Log.d("checkList",Integer.toString(users.size()));
            Log.d("checkList",users.toString());
            if(users.size()==0){
//                navController.navigate(R.id.action_chatListFragment_to_chatFragment);
            }
            if(users.size()==1){
                UserViewModel userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
                userViewModel.getIsInited().observe(getViewLifecycleOwner(),new Observer<Boolean>(){
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean){
                            User aUser = userViewModel.getHostUser().getValue();
                            ArrayList<String> listID1 = aUser.getConversation();
                            ArrayList<String> listID2 = users.get(0).getConversation();
                            ArrayList<String> listID3 = new ArrayList<String>();
                            for(String s : listID1){
                                if(listID2.contains(s)){
                                    listID3.add(s);
                                }
                            }
                            LiveData<String> aId = ConversationRepository.getInstance().getP2PConv(listID3);
                            aId.observe(getViewLifecycleOwner(),new Observer<String>(){
                                @Override
                                public void onChanged(String s) {
                                    Bundle aBundle = new Bundle();
                                    aBundle.putString("id",aId.getValue());

                                    aId.removeObserver(this);
                                }
                            });
                            userViewModel.getIsInited().removeObserver(this);
                        }
                    }
                });
            }
            if(users.size()>=2){
                ArrayList<String> memberId = new ArrayList<String>();
                for(User temp : users){
                    memberId.add(temp.getUID());
                }
                UserViewModel userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
                userViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean){
                            String uidCreator = userViewModel.getHostUser().getValue().getUID();
                            LiveData<String> convID = ConversationRepository.getInstance().createGroupChat(uidCreator,memberId);
                            convID.observe(getViewLifecycleOwner(),new Observer<String>(){
                                @Override
                                public void onChanged(String s) {
                                    if(s!=null){
                                        memberId.add(uidCreator);
                                        for(String aId : memberId){
                                            UserRepository.getInstance().addConv(aId,s);
                                        }
                                    }
                                    convID.removeObserver(this);
                                }
                            });
                        }
                        userViewModel.getIsInited().removeObserver(this);
                    }
                });
            }
            //navController.navigate(R.id.action_chatListFragment_to_chatFragment);
            madapter.getCheckList().clear();
        });


    }



    @Override
    public void onItemClick() {
        if(madapter.getCheckList().size() ==0)
            creatChatMotionLayout.transitionToStart();
        else
            creatChatMotionLayout.transitionToEnd();
    }
}