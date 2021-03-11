package UI.MainActivity;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.study.android_zenly.R;

import adapter.ChatListAdapter;


public class ChatListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView nav_drawer_recycler_view= view.findViewById(R.id.nav_drawer_recycler_view);
        ChatListAdapter adapter = new ChatListAdapter();
        nav_drawer_recycler_view.setAdapter(adapter);
        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

}