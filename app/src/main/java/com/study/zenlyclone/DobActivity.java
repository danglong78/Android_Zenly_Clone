package com.study.zenlyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class DobActivity extends AppCompatActivity {
    EditText nameInput;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String value1="";
        if (bundle != null) {
             value1 = bundle.getString("name");
        }
        setupWindowAnimations();
        setContentView(R.layout.activity_dob);
        btn = (Button) findViewById(R.id.dateButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DobActivity.this,PhoneActivity.class));
            }
        });
        nameInput = (EditText) findViewById(R.id.nameInput);
        TextView textView = (TextView) findViewById(R.id.dobQuestion);
        textView.setText("HEY "+value1+", WHEN IS YOUR BIRTHDAY ?");

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