package com.study.zenlyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;


public class NameActivity extends AppCompatActivity {
    EditText nameInput;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_name);
        nameInput = (EditText) findViewById(R.id.nameInput);
        nameInput.requestFocus();
        btn = (Button) findViewById(R.id.buttonNext);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(nameInput.getText());

                if(name.equals("")) {
                    AlertDialog.Builder myAlertBuilder = new
                            AlertDialog.Builder(NameActivity.this);
                    View layoutView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
                    Button dialogButton = layoutView.findViewById(R.id.btnDialog);
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
                else{
                    Intent intent = new Intent(NameActivity.this,DobActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(NameActivity.this).toBundle());

                }
            }
        });
    }

    private void setupWindowAnimations() {
        Slide slideIn = new Slide();
        slideIn.setSlideEdge(Gravity.END);
        slideIn.setDuration(400);
        slideIn.setInterpolator(new DecelerateInterpolator());
        Slide slideOut = new Slide();
        slideOut.setSlideEdge(Gravity.START);
        slideOut.setDuration(400);
        slideOut.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(slideOut);
        getWindow().setEnterTransition(slideIn);
    }
}