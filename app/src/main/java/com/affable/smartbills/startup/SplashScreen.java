package com.affable.smartbills.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.utils.Constant;
import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        animationView = findViewById(R.id.animationView);
        textView = findViewById(R.id.title_text);

        textView.animate().alpha(0).setDuration(0);
        textView.setVisibility(View.VISIBLE);
        animationView.playAnimation();

        //check which activity to launch
        SharedPreferences preferences = getSharedPreferences(Constant.PREF_REMEMBER, Context.MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(Constant.PREF_REMEMBER_FIRST_LAUNCH, true);
        boolean isLogInSaved = preferences.getBoolean(Constant.PREF_KEY_REMEMBER_LOGIN, false);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textView.animate().alpha(1).setDuration(1500);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Intent intent;

                if (isFirstLaunch){
                    intent = new Intent(SplashScreen.this, IntroSlider.class);
                }else {
                    if (isLogInSaved) {
                        intent = new Intent(SplashScreen.this, MainActivity.class);
                    } else {
                        intent = new Intent(SplashScreen.this, LoginActivity.class);
                    }
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //type your code here
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //type your code here
            }
        });


    }
}