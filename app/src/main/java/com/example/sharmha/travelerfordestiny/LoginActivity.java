package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.Constants;

import com.loopj.android.http.*;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 2/22/16.
 */
public class LoginActivity extends Activity implements Observer {

    private EditText name_login;
    private EditText pswd_login;
    private ImageView login_btn;
//    private NetworkEngine ntwrk;
//    private Util util;
    private UserData user;
    private String username;
    private String password;

    private String url = "auth/login";
    private ControlManager mManager;

    private RelativeLayout errLayout;
    private TextView errText;
    private ImageView close_err;
    private ProgressDialog dialog;
    private Intent localPushEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        if(b.containsKey("eventIntent")) {
            localPushEvent = (Intent) b.get("eventIntent");
        }

        name_login = (EditText) findViewById(R.id.login_name);
        pswd_login = (EditText) findViewById(R.id.login_pswrd);
        login_btn = (ImageView) findViewById(R.id.login_btn);

        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errText = (TextView) findViewById(R.id.error_sub);
        close_err = (ImageView) findViewById(R.id.err_close);

        close_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errLayout.setVisibility(View.GONE);
            }
        });

        dialog = new ProgressDialog(this);

//        mPrefs = getSharedPreferences("user", 0);
//        ntwrk = new NetworkEngine(getApplicationContext());

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                username = name_login.getText().toString();
                password = pswd_login.getText().toString();
                if (username!=null && password!=null) {
                    if (username.length()>0 && password.length()>0) {
                        RequestParams params = new RequestParams();
                        params.put("userName", username);
                        params.put("passWord", password);
                        dialog.show();
                        mManager.postLogin(LoginActivity.this, params, Constants.LOGIN);
                    } else {
                        showError("Username or Password is empty");
                    }
                }
            }
        });

//        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                int heightDiff = root.getRootView().getHeight() - root.getHeight();
//                // IF height diff is more then 150, consider keyboard as visible.
//            }
//        });

        pswd_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        pswd_login.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // the user is done typing.
                    login_btn.requestFocus();
                    login_btn.performClick();
                }
                return false;
            }
        });

        name_login.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // the user is done typing.
//                    pswd_login.requestFocus();
//                    return true; // consume.
                }
                return false;
            }
        });

        name_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    public void showError(String err) {
        dialog.dismiss();
        errLayout.setVisibility(View.VISIBLE);
        errText.setText(err);
    }

    private void enableSubmitIfReady() {
        if(name_login!=null && pswd_login!=null) {
            if (!name_login.getText().toString().isEmpty() && !pswd_login.getText().toString().isEmpty()) {
                login_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn_tapped));
            } else {
                login_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn));
            }
        }
    }

//    private void doLogin(RequestParams params) throws JSONException {
//        ntwrk.post(url, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // If the response is JSONObject instead of expected JSONArray
////                Toast.makeText(LoginActivity.this, "Login Success",
////                        Toast.LENGTH_SHORT).show();
//
//                //post gcm token
//                postGcm();
//
//                if (user != null) {
//                    user.toJson(response);
//                    user.setPassword(password);
//
//                    //save in preferrence
//                    Util.setDefaults("user", username, getApplicationContext());
//                    Util.setDefaults("password", password, getApplicationContext());
//
////                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
////                    Gson gson = new Gson();
////                    String json = gson.toJson(user); // myObject - instance of MyObject
////                    prefsEditor.putString("userdata", json);
////                    prefsEditor.commit();
//
//                    // go to logout page
//                    Intent regIntent = new Intent(getApplicationContext(),
//                            ListActivity.class);
//                    regIntent.putExtra("userdata", user);
//                    startActivity(regIntent);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    Toast.makeText(LoginActivity.this, "Login error from server  - " + statusCode + " " + errorResponse.getString("message"),
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
    public void update(Observable observable, Object data) {
        UserData ud = (UserData)data;
        if (ud.getAuthenticationId()==Constants.LOGIN) {

            //save in preferrence
            Util.setDefaults("user", username, getApplicationContext());
            Util.setDefaults("password", password, getApplicationContext());

            ud.setPassword(password);
            mManager.setUserdata(ud);
            //dismiss progress
            dialog.dismiss();
            // go to logout page
//            Intent regIntent = new Intent(getApplicationContext(),
//                    ListActivity.class);
            Intent regIntent = new Intent(getApplicationContext(),
                    ListActivityFragment.class);
            if(localPushEvent!=null){
                regIntent.putExtra("eventIntent", localPushEvent);
            }
            regIntent.putExtra("userdata", ud);
            startActivity(regIntent);
            finish();
        }
    }
}
