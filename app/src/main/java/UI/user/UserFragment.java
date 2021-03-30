package UI.user;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.BuildConfig;
import com.study.android_zenly.R;

import UI.MainActivity.MainActivity;
import adapter.UserSettingApdater;
import ultis.FragmentTag;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment implements UserSettingApdater.UserSettingCallback {
    ImageView avatar;
    TextView userName;
    NavController navController;
    Button ghostModeButton;
    private static final int REQUEST_CAMERA_REQUEST_CODE = 23;
    private static final int REQUEST_GALLERY_REQUEST_CODE = 32;
    MotionLayout homeFragmentMotionLayout;
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        homeFragmentMotionLayout = (MotionLayout) requireActivity().findViewById(R.id.motion_layout);

        super.onViewCreated(view, savedInstanceState);
        userName = (TextView) view.findViewById(R.id.userName);
        navController= Navigation.findNavController(view);
        SharedPreferences prefs = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
        if ((prefs != null) && (prefs.contains("name"))) {
            String temp = prefs.getString("name", "");
            userName.setText(temp);

        }
        userName.setOnClickListener(v->{
            navController.navigate(R.id.action_userFragment_to_changeNameFragment);
            homeFragmentMotionLayout.setTransition(R.id.right,R.id.hideRight);
            homeFragmentMotionLayout.setProgress(1);
            activity.setFragmentTag(FragmentTag.SETTINGS,homeFragmentMotionLayout,navController);


        });

        avatar = view.findViewById(R.id.avatar);
        //TODO lay avatar url truyền vào "Avatar URL"
//        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(Avatar URL);
//        if (ref != null) {
//            ref.getDownloadUrl().addOnSuccessListener(uri -> {
//                String imageURL = uri.toString();
//                Glide.with(requireActivity())
//                        .load(imageURL)
//                        .into(avatar);
//
//            });
//        }
        avatar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Which one does select photo from ?");
            builder.setItems(new String[]{"Camera","Gallery","Cancel"},new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                        {
                            boolean permissionApproved = hasPermission(Manifest.permission.CAMERA);
                            if (!permissionApproved) {
                                requestPermissionWithRationale(
                                        Manifest.permission.CAMERA,
                                        REQUEST_CAMERA_REQUEST_CODE,
                                        fineLocationRationalSnackbar(Manifest.permission.CAMERA,REQUEST_CAMERA_REQUEST_CODE));
                            } else {
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);                            }
                            break;
                        }
                        case 1:
                        {
                            boolean permissionApproved = hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                            if (!permissionApproved) {
                                requestPermissionWithRationale(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        REQUEST_GALLERY_REQUEST_CODE,
                                        fineLocationRationalSnackbar(Manifest.permission.READ_EXTERNAL_STORAGE,REQUEST_GALLERY_REQUEST_CODE));
                            } else {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 1);                            }
                            break;
                        }
                        case 2:
                        {
                            dialog.dismiss();
                            break;
                        }
                    }
                }
            });
            builder.create().show();
        });
        ghostModeButton = view.findViewById(R.id.GhostModeBtn);
        ghostModeButton.setOnClickListener(v->{
            navController.navigate(R.id.action_userFragment_to_ghostModeFragment);
            homeFragmentMotionLayout.setTransition(R.id.right,R.id.hideRight);
            homeFragmentMotionLayout.setProgress(1);
            activity.setFragmentTag(FragmentTag.SETTINGS,homeFragmentMotionLayout,navController);


        });
        RecyclerView recyclerview = view.findViewById(R.id.recyclerview);
        UserSettingApdater adapter = new UserSettingApdater(prefs);
        adapter.callback=this;
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(requireActivity()));


    }

    @Override
    public void onListClick(int position) {
        homeFragmentMotionLayout.setTransition(R.id.right,R.id.hideRight);
        homeFragmentMotionLayout.setProgress(1);
        switch (position) {
            case 0: {
                navController.navigate(R.id.action_userFragment_to_changeDobFragment);

                activity.setFragmentTag(FragmentTag.SETTINGS,homeFragmentMotionLayout,navController);
                break;
            }
            case 1: {
                navController.navigate(R.id.action_userFragment_to_searchFriendFragment2);
                activity.setFragmentTag(FragmentTag.SETTINGS,homeFragmentMotionLayout,navController);

                break;
            }
            case 2: {
                navController.navigate(R.id.action_userFragment_to_blockedUsersFragment);
                activity.setFragmentTag(FragmentTag.SETTINGS,homeFragmentMotionLayout,navController);

                break;
            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            } else {
                String permissionDeniedExplanation = "We need your permission to run app";
                Snackbar.make(
                        getActivity().findViewById(R.id.fragment_foreground_request),
                        "Need to acceppt permission",
                        Snackbar.LENGTH_LONG
                ).setAction("Setting", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                        );
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        ;
                    }
                }).show();
            }
        }
        if(requestCode == REQUEST_GALLERY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            } else {
                String permissionDeniedExplanation = "We need your permission to run app";
                Snackbar.make(
                        getActivity().findViewById(R.id.fragment_foreground_request),
                        "Need to acceppt permission",
                        Snackbar.LENGTH_LONG
                ).setAction("Setting", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                        );
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        ;
                    }
                }).show();
            }
        }
    }

    private Boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getActivity(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionWithRationale(String permission, int requestCode, Snackbar snackbar) {
        boolean provideRationale = shouldShowRequestPermissionRationale(permission);

        if (provideRationale) {
            snackbar.show();
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    private Snackbar fineLocationRationalSnackbar(String request,int requestCode) {
        return Snackbar.make(getActivity().findViewById(R.id.UserFragment),
                "You need to accept permisson for app to work",
                Snackbar.LENGTH_LONG
        ).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(
                        new String[]{request},
                        requestCode
                );
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            switch(requestCode) {
                case 0:
                    if(resultCode == RESULT_OK){
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        //TODO UPDATE DATABASE
                        avatar.setImageBitmap(imageBitmap);
                    }

                    break;
                case 1:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = data.getData();
                        //TODO UPDATE DATABASE

                        avatar.setImageURI(selectedImage);
                    }
                    break;
            }
        }
}
