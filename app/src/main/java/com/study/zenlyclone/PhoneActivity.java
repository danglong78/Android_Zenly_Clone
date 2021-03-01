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

import com.hbb20.CountryCodePicker;

public class PhoneActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_phone);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        btn = (Button) findViewById(R.id.buttonPhone);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneActivity.this,ConfirmOtpActivity.class));
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