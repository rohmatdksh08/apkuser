package com.jatmika.e_complaintrangkasbitung;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    Animation fromnot;
    RelativeLayout relativeLayout;
    public static int splashInterval = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        relativeLayout = findViewById(R.id.relative1);
        fromnot = AnimationUtils.loadAnimation(this, R.anim.fromnot);
        relativeLayout.startAnimation(fromnot);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);

                this.finish();
            }

            private void finish() {

            }
        }, splashInterval);
    }
}
