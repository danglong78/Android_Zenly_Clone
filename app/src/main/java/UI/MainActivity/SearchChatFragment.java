package UI.MainActivity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.study.android_zenly.R;

import java.util.Objects;

import adapter.ChatListAdapter;
import ultis.FragmentTag;


public class SearchChatFragment extends Fragment implements ChatListAdapter.OnChatListListener{

    Button cancelBtn;
    EditText searchText;
    MotionLayout homeFragmentMotionLayout;
    NavController navController;

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
        ChatListAdapter adapter = new ChatListAdapter(getActivity(),this);
        nav_drawer_recycler_view.setAdapter(adapter);
        nav_drawer_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onChatClick(int position) {
        MainActivity activity = (MainActivity) getActivity();
        activity.setFragmentTag(FragmentTag.CHAT,homeFragmentMotionLayout);
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus()!=null)
        {inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);}
        navController.navigate(R.id.action_searchChatFragment_to_chatFragment2);
    }
}