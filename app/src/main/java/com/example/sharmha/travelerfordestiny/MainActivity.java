package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends Activity implements Observer {

    private View register_layout;
    private View signin_layout;
    public UserData userData;
    Intent contentIntent;
    String p;
    private ControlManager mManager;

    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);
        String u= Util.getDefaults("user", getApplicationContext());
        p = Util.getDefaults("password", getApplicationContext());

        userData = new UserData();

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        // getting contentIntent from push notification click
        if (this.getIntent().hasExtra(Constants.TRAVELER_NOTIFICATION_INTENT)) {
            Intent messageIntent = (Intent) this.getIntent().getExtras().get(Constants.TRAVELER_NOTIFICATION_INTENT);
            if (messageIntent == null) {
                return;
            }
            contentIntent = null;
            if(messageIntent.getExtras() != null) {
                contentIntent = (Intent) messageIntent.getExtras().get(Constants.NOTIFICATION_INTENT_CHANNEL);
            }
        }

        if (u != null && p!= null && !u.isEmpty() && !p.isEmpty()) {
            setContentView(R.layout.splash_loading);
            Util.storeUserData(userData, u, p);

            RequestParams params = new RequestParams();
            params.put("userName", u);
            params.put("passWord", p);
            mManager.postLogin(MainActivity.this, params, Constants.LOGIN);

        }else {
            setContentView(R.layout.activity_main);

            register_layout = findViewById(R.id.register);
            signin_layout = findViewById(R.id.sign_in);

            register_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent regIntent = new Intent(getApplicationContext(),
                            RegisterActivity.class);
                    regIntent.putExtra("userdata", userData);
                    startActivity(regIntent);
                }
            });

            signin_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signinIntent = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    signinIntent.putExtra("userdata", userData);
                    if(contentIntent!=null) {
                        signinIntent.putExtra("eventIntent", contentIntent);
                    }
                    startActivity(signinIntent);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromBackpressService, new IntentFilter("backpress_flag"));
    }

    private BroadcastReceiver ReceivefromBackpressService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    public UserData getUserData() {
        if (userData!=null) {
            return userData;
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void update(Observable observable, Object data) {
        UserData ud = (UserData)data;
        if (ud.getAuthenticationId() == Constants.LOGIN) {

            ud.setPassword(p);
            mManager.setUserdata(ud);
            Intent regIntent = new Intent(getApplicationContext(),
                    ListActivityFragment.class);
            regIntent.putExtra("userdata", ud);
            if(contentIntent!=null) {
                regIntent.putExtra("eventIntent", contentIntent);
            }
            startActivity(regIntent);
            finish();
        }
    }
}
