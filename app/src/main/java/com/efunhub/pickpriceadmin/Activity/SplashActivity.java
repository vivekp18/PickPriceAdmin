package com.efunhub.pickpriceadmin.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivSplashLogo;
    private TextView tvAppName;
    private Animation anmTop, anmBottom;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        ivSplashLogo.setAnimation(anmTop);
        tvAppName.setAnimation(anmBottom);

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    sessionManager.checkLogin();
                    finish();
                    //startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void init() {
        ivSplashLogo = findViewById(R.id.ivSplashLogo);
        tvAppName = findViewById(R.id.tvAppName);
        anmTop = AnimationUtils.loadAnimation(this, R.anim.top_anm);
        anmBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anm);
        sessionManager = new SessionManager(getApplicationContext());
    }
}
