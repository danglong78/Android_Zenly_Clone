package UI.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.study.android_zenly.R;

import UI.MainActivity.MainActivity;
import adapter.ChatAdapter;
import data.models.Conversation;
import ultis.FragmentTag;
import viewModel.ChatViewModel;


public class ChatFragment extends Fragment {

    private MainActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView= view.findViewById(R.id.message_list_recyclerview);
        String id = savedInstanceState.getString("id");
        String name = savedInstanceState.getString("name");
        ChatViewModel mChatListViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        mChatListViewModel.init(id,getActivity(),getViewLifecycleOwner());
        mChatListViewModel.getIsInited().observe(getViewLifecycleOwner(), new Observer<Boolean>(){

            @Override
            public void onChanged(Boolean isInited) {
                if(isInited){
                    ChatAdapter adapter = new ChatAdapter(mChatListViewModel.getMessList(), FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true));
                    mChatListViewModel.getIsInited().removeObserver(this);
                }
            }
        });
    }
}