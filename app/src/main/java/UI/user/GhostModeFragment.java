package UI.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.study.android_zenly.R;

import java.util.ArrayList;
import java.util.List;

import adapter.GhostModeListAdapter;
import data.models.User;
import viewModel.FriendViewModel;


public class GhostModeFragment extends Fragment implements GhostModeListAdapter.GhostModeCallback {
    private List<User> tempList;
    private FriendViewModel friendViewModel;
    MotionLayout motionLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ghost_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tempList= new ArrayList<>();
//        List<User> users= new ArrayList<>();
//        users.add(new User(null,"Long","1234","7ddee776-92b6-11eb-a8b3-0242ac130003.png","23/9/2000","012345567",null));
//        users.add(new User(null,"Dai","12312334","2d6b54f0-92b6-11eb-a8b3-0242ac130003.jpg","23/9/2000","012345567",null));
//        users.add(new User(null,"Tri","1233434","0e974d50-8978-11eb-8dcd-0242ac130003.png","23/9/2000","012345567",null));
//        users.add(new User(null,"Tam","112234","0e9748c8-8978-11eb-8dcd-0242ac130003.png","23/9/2000","012345567",null));
//        List<User> users2= new ArrayList<>();
//        users2.add(new User(null,"Thu","234","7ddee776-92b6-11eb-a8b3-0242ac130003.png","23/9/2000","012345567",null));
//        users2.add(new User(null,"Tai","2312334","2d6b54f0-92b6-11eb-a8b3-0242ac130003.jpg","23/9/2000","012345567",null));
//        users2.add(new User(null,"Minh","233434","0e974d50-8978-11eb-8dcd-0242ac130003.png","23/9/2000","012345567",null));
//        users2.add(new User(null,"VaN","12234","0e9748c8-8978-11eb-8dcd-0242ac130003.png","23/9/2000","012345567",null));
        friendViewModel = new ViewModelProvider(getActivity()).get(FriendViewModel.class);
        RecyclerView frozenList = view.findViewById(R.id.frozenRecyclerView);
        GhostModeListAdapter frozenAdapter = new GhostModeListAdapter(requireActivity(),this);
        frozenList.setAdapter(frozenAdapter);
        frozenList.setLayoutManager(new GridLayoutManager(getActivity(),4));
        friendViewModel.getFriendsFrozenList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d("asdasdasd", "frozen: " + users.size());
                frozenAdapter.updateListItems((ArrayList<User>) users);
            }
        });

        RecyclerView preciseList = view.findViewById(R.id.preciseRecyclerView);
        GhostModeListAdapter preciseAdapter = new GhostModeListAdapter(requireActivity(),this);
        preciseList.setAdapter(preciseAdapter);
        preciseList.setLayoutManager(new GridLayoutManager(getActivity(),4));
        friendViewModel.getFriendsPreciseList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d("asdasdasd", "precise: " + users.size());
                preciseAdapter.updateListItems((ArrayList<User>) users);
            }
        });

        motionLayout=view.findViewById(R.id.ghostModeFragment);
        Button frozenBtn= view.findViewById(R.id.frozenBtn);
        frozenBtn.setOnClickListener(v->{
            //TODO ADD TEMP LIST TO FROZEN LIST
//            frozenAdapter.addUser(tempList);
//            preciseAdapter.deleteUser(tempList);
            for(User user : tempList)
            {
                friendViewModel.turnOnFrozen(user.getUID());
            }
            tempList.clear();
            motionLayout.transitionToStart();
            for (int childCount = preciseList.getChildCount(), i = 0; i < childCount; ++i) {
                final GhostModeListAdapter.ViewHolder holder = (GhostModeListAdapter.ViewHolder) preciseList.getChildViewHolder(preciseList.getChildAt(i));
                holder.unCheck();
            }
            for (int childCount = frozenList.getChildCount(), i = 0; i < childCount; ++i) {
                final GhostModeListAdapter.ViewHolder holder = (GhostModeListAdapter.ViewHolder) frozenList.getChildViewHolder(frozenList.getChildAt(i));
                holder.unCheck();
            }


        });
        Button preciseBtn = view.findViewById(R.id.preciseBtn);
        preciseBtn.setOnClickListener(v->{
//            preciseAdapter.addUser(tempList);
//            frozenAdapter.deleteUser(tempList);
            for(User user : tempList)
            {
                friendViewModel.turnOffFrozen(user.getUID());
            }
            tempList.clear();
            motionLayout.transitionToStart();
            for (int childCount = preciseList.getChildCount(), i = 0; i < childCount; ++i) {
                final GhostModeListAdapter.ViewHolder holder = (GhostModeListAdapter.ViewHolder) preciseList.getChildViewHolder(preciseList.getChildAt(i));
                holder.unCheck();
            }
            for (int childCount = frozenList.getChildCount(), i = 0; i < childCount; ++i) {
                final GhostModeListAdapter.ViewHolder holder = (GhostModeListAdapter.ViewHolder) frozenList.getChildViewHolder(frozenList.getChildAt(i));
                holder.unCheck();
            }
        });
    }

    @Override
    public void onClickCheckbox(User user, CheckBox checkbox) {
        if(checkbox.isChecked()) {
            checkbox.setChecked(false);
            tempList.remove(user);
        }
        else{
            checkbox.setChecked(true);
            tempList.add(user);
        }
        if(tempList.size() == 0){
            motionLayout.transitionToStart();
        }
        else{
            motionLayout.transitionToEnd();
        }
    }
}