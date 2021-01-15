package com.example.jamiaaty;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    ImageView imageView;
    TextView nameTv, name2Tv;
    long animationTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.iv_spalash_logo);
        nameTv = findViewById(R.id.tv_spalsh_name);
        name2Tv = findViewById(R.id.tv_spalsh_name2);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y", 400f);
        ObjectAnimator animatorName = ObjectAnimator.ofFloat(nameTv,"x",100f);
        animatorY.setDuration(animationTime);
        animatorName.setDuration(animationTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorName,animatorY);
        animatorSet.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}