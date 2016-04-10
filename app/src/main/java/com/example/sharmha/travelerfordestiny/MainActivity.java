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
    String p;
//    private NetworkEngine ntwrk;
//
//    private String url = "auth/login";

//    SharedPreferences mPrefs;
    private ControlManager mManager;

    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);
        //ntwrk = new NetworkEngine(getApplicationContext());

//        Gson gson = new Gson();
//        mPrefs = this.getSharedPreferences("user", 0);
        String u= Util.getDefaults("user", getApplicationContext());
        p = Util.getDefaults("password", getApplicationContext());

        userData = new UserData();

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);
        if (u != null && p!= null && !u.isEmpty() && !p.isEmpty()) {
            setContentView(R.layout.splash_loading);
//            String json = mPrefs.getString("userdata", "");
//            userData = gson.fromJson(json, UserData.class);

//            if (userData != null && !userData.getUser().isEmpty()) {
                // save user data
                Util.storeUserData(userData, u, p);

            RequestParams params = new RequestParams();
            params.put("userName", u);
            params.put("passWord", p);
            mManager.postLogin(MainActivity.this, params, Constants.LOGIN);
//            }
        }else {
            setContentView(R.layout.activity_main);

            register_layout = findViewById(R.id.register);
            signin_layout = findViewById(R.id.sign_in);

            register_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Register clicked",
//                        Toast.LENGTH_SHORT).show();
                    Intent regIntent = new Intent(getApplicationContext(),
                            RegisterActivity.class);
                    regIntent.putExtra("userdata", userData);
                    startActivity(regIntent);
                }
            });

            signin_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Login clicked",
//                        Toast.LENGTH_SHORT).show();
                    Intent signinIntent = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    signinIntent.putExtra("userdata", userData);
                    startActivity(signinIntent);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (userData != null) {
//            outState.putParcelable("user", userData);
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle outState) {
//        super.onRestoreInstanceState(outState);
////        TrimbleLog.w(this, "onRestoreInstanceState starts...");
//        if (outState!= null && outState.containsKey("user")) {
//            // save user data
//            Util.storeUserData((UserData) outState.getParcelable("user"));
//        }
//    }

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

//    private void doLogin(RequestParams params) throws JSONException {
//        ntwrk.post(url, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//
//                //get token for push notification
//                postGcm();
//                // If the response is JSONObject instead of expected JSONArray
//                UserData ud = getUserData();
//                if (ud!=null) {
//                    ud.toJson(response);
//                }
//                // go to eventlist page
//                Intent regIntent = new Intent(getApplicationContext(),
//                        ListActivity.class);
//                regIntent.putExtra("userdata", ud);
//                startActivity(regIntent);
//                finish();
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    Toast.makeText(MainActivity.this, "Login error from server  - " + statusCode + " " + errorResponse.getString("message"),
//                            Toast.LENGTH_LONG).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void postGcm() {
//        //post gcm token
//        Util.getGCMToken(this, mManager);
//    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void update(Observable observable, Object data) {
        UserData ud = (UserData)data;
        if (ud.getAuthenticationId() == Constants.LOGIN) {

//            //save in preferrence
//            Util.setDefaults("user", username, getApplicationContext());
//            Util.setDefaults("password", password, getApplicationContext());

            ud.setPassword(p);
            mManager.setUserdata(ud);
            // go to logout page
//            Intent regIntent = new Intent(getApplicationContext(),
//                    ListActivity.class);
            Intent regIntent = new Intent(getApplicationContext(),
                    ListActivityFragment.class);
            regIntent.putExtra("userdata", ud);
            startActivity(regIntent);
            finish();
        }
    }
}
