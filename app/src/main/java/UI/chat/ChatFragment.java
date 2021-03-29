package UI.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import UI.MainActivity.MainActivity;
import adapter.ChatAdapter;
import data.models.Conversation;
import data.models.Message;
import ultis.FragmentTag;
import viewModel.ChatViewModel;


public class ChatFragment extends Fragment {

    private MainActivity activity;
    private Button sendBtn;
    private EditText inputChat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView= view.findViewById(R.id.message_list_recyclerview);
        String id = getArguments().getString("id");
        String name = getArguments().getString("name");
        ChatAdapter adapter = new ChatAdapter(requireActivity(), FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true));
        ChatViewModel mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        mChatListViewModel.init(id,getActivity(),getViewLifecycleOwner());
        mChatListViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>(){

            @Override
            public void onChanged(Boolean isInited) {
                if(isInited){
                    adapter.addToEnd(mChatListViewModel.getMessList().getValue(),false);
                    mChatListViewModel.getMessList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Message>>(
                    ) {
                        @Override
                        public void onChanged(ArrayList<Message> messages) {
                            adapter.addToEnd(messages,false);
                        }
                    });
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
                if(s.length()==0)
                {
                    sendBtn.setActivated(false);
                }
                else{
                    sendBtn.setActivated(true);
                }
            }
        });
        sendBtn= view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(v->{
            String message = inputChat.getText().toString();
//            adapter.addToStart(tin nhan moi,false);
        });

    }
}