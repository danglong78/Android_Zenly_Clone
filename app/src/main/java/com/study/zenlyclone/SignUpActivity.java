package com.study.zenlyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SignUpActivity extends AppCompatActivity {
    Button btnSignUp;
    TextView appName,appDescription,privacy;
    GifImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        btnSignUp = (Button) findViewById(R.id.SignUpButton);
        appName= (TextView) findViewById(R.id.label_name);
        appDescription = (TextView) findViewById(R.id.app_name_des);
        privacy =(TextView) findViewById(R.id.describePrivacy);
        img= (GifImageView) findViewById(R.id.gifImageView);
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this, NameActivity.class), ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this).toBundle());
            }
        });

        setupWindowAnimations();




    }

    private void setupWindowAnimations() {
        appName.setTranslationY(300);
        appDescription.setTranslationY(300);
        privacy.setTranslationY(300);
        btnSignUp.setTranslationY(300);

        appName.setAlpha(0);
        appDescription.setAlpha(0);
        privacy.setAlpha(0);
        img.setAlpha(0f);
        btnSignUp.setAlpha(0);

        img.animate().alpha(0.5f).setDuration(1000).setStartDelay(400).start();
        appName.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(400).start();
        appDescription.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(400).start();
        privacy.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(400).start();
        btnSignUp.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(400).start();
    }


}