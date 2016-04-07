package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharmha.travellerdestiny.R;

/**
 * Created by sharmha on 2/19/16.
 */
public class SplashActivity extends Activity {

    private static final int SPLASH_DELAY = 500;
    private Handler mHandler;
    private RelativeLayout mLayout;
    private ControlManager cManager;
    private RelativeLayout errLayout;
    private TextView errText;
    private ImageView close_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_loading);
        cManager = ControlManager.getmInstance();
        cManager.setCurrentActivity(this);
        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errText = (TextView) findViewById(R.id.error_sub);
        close_err = (ImageView) findViewById(R.id.err_close);

        close_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errLayout.setVisibility(View.GONE);
            }
        });
        mHandler = new Handler();
        mLayout = (RelativeLayout) findViewById(R.id.splash_layout);
        mLayout.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this,
                R.anim.fadein_splash);
        mLayout.startAnimation(anim);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }

        }, SPLASH_DELAY);
    }

    public void showError(String err) {
        errLayout.setVisibility(View.VISIBLE);
        errText.setText(err);
    }
}
