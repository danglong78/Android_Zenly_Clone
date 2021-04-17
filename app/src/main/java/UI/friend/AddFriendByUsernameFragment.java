package UI.friend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.study.android_zenly.R;

import java.util.ArrayList;

import adapter.ListFriendOfUserAdapter;
import data.models.User;
import data.models.UserFriendList;
import viewModel.FriendViewModel;
import viewModel.InvitationViewModel;
import viewModel.InvitingViewModel;
import viewModel.UserViewModel;


public class AddFriendByUsernameFragment extends Fragment implements ListFriendOfUserAdapter.onClickUser {
    UserViewModel userViewModel;
    EditText searchInput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend_by_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListFriendOfUserAdapter adapter= new ListFriendOfUserAdapter(this,getActivity());
        RecyclerView friendListRecyclerView = view.findViewById(R.id.nav_drawer_recycler_view);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendListRecyclerView.setAdapter(adapter);
        userViewModel= new ViewModelProvider(getActivity()).get(UserViewModel.class);

        Button btn= view.findViewById(R.id.cancelBtn);
        btn.setOnClickListener(v->{
            if(searchInput.getText().length()==0)
            {
                getActivity().onBackPressed();
            }
            else
            {
                userViewModel.resetSearchList();
                userViewModel.getSearchList(searchInput.getText().toString()).observe(getViewLifecycleOwner(),userFriendLists -> {
                    adapter.setList((ArrayList<UserFriendList>) userFriendLists);
                });
            }
        });

        searchInput=view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0) {
                    btn.setText("CANCEL");
                }
                else
                    btn.setText("SEARCH");
            }
        });

    }

    @Override
    public void onClickUser(User user, boolean isYourFriend) {

        FriendViewModel friendViewModel=new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        String type=friendViewModel.checkUserTag(user.getUID());
        if(type.compareTo("BLOCKEDBY")!=0 && type.compareTo("BLOCK")!=0) {
            Log.d("Search by phone", user.toString());
            Bundle bundle = new Bundle();
            bundle.putString("name", user.getName());
            bundle.putString("phone", user.getPhone());
            bundle.putString("uid", user.getUID());
            bundle.putString("avatar", user.getAvatarURL());
            bundle.putString("type", type);

            NavController navController = Navigation.findNavController(getActivity(), R.id.friend_nav_host_fragment);
            if(type.compareTo("FRIEND")==0)
                navController.navigate(R.id.action_addFriendByUsernameFragment_to_friendProfileFragment2, bundle);
            else
                navController.navigate(R.id.action_addFriendByUsernameFragment_to_strangerProfileFragment2, bundle);
        }

    }

    @Override
    public void onClickAdd(String UID) {
        FriendViewModel friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        friendViewModel.addFriend(UID);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
}