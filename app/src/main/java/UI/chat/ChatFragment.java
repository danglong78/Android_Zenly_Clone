package UI.chat;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import UI.MainActivity.MainActivity;
import adapter.ChatAdapter;
import data.models.Conversation;
import data.models.Message;
import data.models.User;
import ultis.FragmentTag;
import viewModel.ChatViewModel;
import viewModel.FriendViewModel;


public class ChatFragment extends Fragment {

    private MainActivity activity;
    private Button sendBtn,profileBtn,closeBtn;
    private EditText inputChat;
    private ChatViewModel mChatListViewModel;
    private ListenerRegistration listMessListener;
    private NavController navController;
    private LiveData<List<User>> blockList;
    private LiveData<List<User>> blockedByList;
    private MutableLiveData<Boolean> isBlocked;
    private MutableLiveData<Boolean> Block;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Block.postValue(false);
        isBlocked.postValue(false);
        navController= Navigation.findNavController(view);
        RecyclerView recyclerView= view.findViewById(R.id.message_list_recyclerview);
        String id = getArguments().getString("id");
        String name = getArguments().getString("name");
        List<User> listUser=  getArguments().getParcelableArrayList("listUserID");
        Log.d("listUserChat",String.valueOf(listUser.size()));
        ChatAdapter adapter = new ChatAdapter(requireActivity(), FirebaseAuth.getInstance().getUid());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true));
        mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        mChatListViewModel.init(adapter,listMessListener,id,getActivity(),getViewLifecycleOwner());
        mChatListViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>(){

            @Override
            public void onChanged(Boolean isInited) {
                if(isInited){

//                    adapter.addToEnd(mChatListViewModel.getMessList().getValue(),false);
                    adapter.notifyDataSetChanged();
                    mChatListViewModel.getIsInited().removeObserver(this);
                }
            }
        });
        inputChat = view.findViewById(R.id.inputChat);
        inputChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                if(temp.length()>=1 && temp.charAt(0)=='\n'){
                    inputChat.setText("");
                    temp = "";
                }
                if(temp.length()==0)
                {
                    sendBtn.setEnabled(false);
                }
                else{
                    sendBtn.setEnabled(true);
                }
            }
        });
        sendBtn= view.findViewById(R.id.sendBtn);
        sendBtn.setEnabled(false);
        sendBtn.setOnClickListener(v->{
            String message = inputChat.getText().toString();
            message = validMess(message);
//            adapter.addToStart(tin nhan moi,false);
            Log.d("send Mess",message);
            MutableLiveData<Boolean> res = mChatListViewModel.SendMess(message,getActivity(),getViewLifecycleOwner());
            res.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean val) {
                    if(val){
                        inputChat.setText("");
                        Log.d("dem",Integer.toString(adapter.getItemCount()));
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        });
        closeBtn =view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v->{
            requireActivity().onBackPressed();
        });

        // check block and blocked:
        FriendViewModel mFriendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        blockList = mFriendViewModel.getBlockList();
        blockedByList = mFriendViewModel.getBlockedByList();
        blockList.observe(getViewLifecycleOwner(),new Observer<List<User>>(){
            @Override
            public void onChanged(List<User> users) {
                Boolean aBool = true;
                for(User auser : users){
                    Boolean check = false;
                    for(User temp : listUser){
                        if(temp.getUID().equals(auser.getUID())){
                            Block.postValue(true);
                            aBool=false;
                            check=true;
                            break;
                        }
                    }
                    if(check){
                        break;
                    }
                }
                if(aBool){
                    Block.postValue(false);
                }
            }
        });

        blockedByList.observe(getViewLifecycleOwner(),new Observer<List<User>>(){
            @Override
            public void onChanged(List<User> users) {
                Boolean aBool = true;
                for(User auser : users){
                    Boolean check = false;
                    for(User temp : listUser){
                        if(temp.getUID().equals(auser.getUID())){
                            check=true;
                            isBlocked.postValue(true);
                            aBool = false;
                            break;
                        }
                    }
                    if(check){
                        break;
                    }
                }
                if(aBool){
                    isBlocked.postValue(false);
                }
            }
        });

        Block.observe(getViewLifecycleOwner(),new Observer<Boolean>(){
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean || isBlocked.getValue()){
                    inputChat.setText("This person is unavailable on chat");
                    inputChat.setEnabled(false);
                    sendBtn.setEnabled(false);
                }else{
                    inputChat.setText("");
                    inputChat.setEnabled(true);
                    sendBtn.setEnabled(true);
                }
            }
        });

        isBlocked.observe(getViewLifecycleOwner(),new Observer<Boolean>(){
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean || Block.getValue()){
                    inputChat.setText("This person is unavailable on chat");
                    inputChat.setEnabled(false);
                    sendBtn.setEnabled(false);
                }else{
                    inputChat.setText("");
                    inputChat.setEnabled(true);
                    sendBtn.setEnabled(true);
                }
            }
        });

        profileBtn = view.findViewById(R.id.userProfileBtn);
        profileBtn.setOnClickListener(v->{
            if(listUser.size()==2)
            {
                for(User temp : listUser){
                    if(!temp.getUID().equals(FirebaseAuth.getInstance().getUid())){
                        FriendViewModel friendViewModel= new ViewModelProvider(getActivity()).get(FriendViewModel.class);
                        String type = friendViewModel.checkUserTag(temp.getUID());
                        if(type.compareTo("BLOCKEDBY")!=0&&type.compareTo("BLOCK")!=0){
                            Bundle bundle= new Bundle();
                            bundle.putString("name",temp.getName());
                            bundle.putString("phone",temp.getPhone());
                            bundle.putString("uid",temp.getUID());
                            bundle.putString("avatar",temp.getAvatarURL());
                            bundle.putString("type",type);
                            if(type.compareTo("FRIEND")==0)
                                navController.navigate(R.id.action_chatFragment_to_friendProfileFragment,bundle);
                            else
                                navController.navigate(R.id.action_chatFragment_to_strangerProfileFragment,bundle);

                        }
                        else{
                            AlertDialog.Builder myAlertBuilder = new
                                    AlertDialog.Builder(getActivity());
                            View layoutView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
                            Button dialogButton = layoutView.findViewById(R.id.btnDialog);
                            TextView dialogTitle= layoutView.findViewById(R.id.dialog_title);
                            TextView dialogContent= layoutView.findViewById(R.id.dialog_content);
                            dialogTitle.setText("User not exists");
                            dialogContent.setText("");
                            myAlertBuilder.setView(layoutView);
                            AlertDialog alert= myAlertBuilder.create();
                            alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alert.show();
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                }
                            });

                        }
                        break;
                    }
                }
            }
        });
        if (listUser.size() !=2)
        {
            profileBtn.setVisibility(View.GONE);
        }

    }

    public String validMess(String mess){
        String res = "";
        int count1 = 0;
        for(int i=0;i<mess.length();i++){
            if(mess.charAt(i)=='\n' || mess.charAt(i)==' '){
                count1++;
            }else{
                break;
            }
        }
        int count2 = 0;
        for(int i=mess.length()-1;i>=0;i--){
            if(mess.charAt(i)=='\n' || mess.charAt(i)==' '){
                count2++;
            }else{
                break;
            }
        }
        for(int i=count1;i<mess.length()-count2;i++){
            res+=mess.charAt(i);
        }
        return res;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        blockList.removeObservers(getViewLifecycleOwner());
        blockedByList.removeObservers(getViewLifecycleOwner());
        Block.removeObservers(getViewLifecycleOwner());
        isBlocked.removeObservers(getViewLifecycleOwner());
    }
}