package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.network.NetworkEngine;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 2/22/16.
 */
public class RegisterActivity extends BaseActivity implements Observer {

    private EditText name_signup;
    private EditText pswd_signup;
    private EditText psnid_signup;
    private ImageView signup_btn;
    private NetworkEngine ntwrk;
    private Util util;
    private UserData user;
    SharedPreferences mPrefs;

    private ControlManager mManager;

    private String username;
    private String password;
    private String psnid;

//    private RelativeLayout errLayout;
//    private TextView errText;
//    private ImageView close_err;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

//        mPrefs = getPreferences(MODE_PRIVATE);

        setContentView(R.layout.register);

        dialog = new ProgressDialog(this);

        name_signup = (EditText) findViewById(R.id.signup_name);
        pswd_signup = (EditText) findViewById(R.id.signup_pswrd);
        psnid_signup = (EditText) findViewById(R.id.signup_psn);
        signup_btn = (ImageView) findViewById(R.id.signup_btn);

//        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
//        errText = (TextView) findViewById(R.id.error_sub);
//        close_err = (ImageView) findViewById(R.id.err_close);
//
//        close_err.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                errLayout.setVisibility(View.GONE);
//            }
//        });

        //ntwrk = new NetworkEngine(getApplicationContext());

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        name_signup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                //name_signup.setHint("");
                v.performClick();
            }
        });

        name_signup.addTextChangedListener(new TextWatcher() {
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

        pswd_signup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                //pswd_signup.setHint("");
                v.performClick();
            }
        });

        pswd_signup.addTextChangedListener(new TextWatcher() {
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

        psnid_signup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                //pswd_signup.setHint("");
                v.performClick();
            }
        });

        psnid_signup.addTextChangedListener(new TextWatcher() {
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

        psnid_signup.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // the user is done typing.
                    signup_btn.performClick();
                }
                return false;
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                username = name_signup.getText().toString();
                password = pswd_signup.getText().toString();
                psnid = psnid_signup.getText().toString();

                if (!username.isEmpty() && !password.isEmpty() && !psnid.isEmpty()) {
                    signup_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn_tapped));
                    RequestParams params = new RequestParams();
                    params.put("userName", username);
                    params.put("passWord", password);
                    params.put("psnId", psnid);
                    dialog.show();
                    mManager.postLogin(RegisterActivity.this, params, Constants.REGISTER);
                } else {
                    if(username.length()==0){
                        showError(getResources().getString(R.string.username_missing));
                    } else if(username.length() < 5) {
                        showError(getResources().getString(R.string.username_short));
                    } else if(password.length()==0) {
                        showError(getResources().getString(R.string.password_missing));
                    } else if(password.length()<5){
                        showError(getResources().getString(R.string.password_short));
                    } else {
                        showError("Please enter correct PsnId.");
                    }
                }
            }
        });
    }

    public void showError(String err) {
        dialog.dismiss();
        signup_btn.setEnabled(true);
        setErrText(err);
    }

    private void enableSubmitIfReady() {
        if(name_signup!=null && pswd_signup!=null) {
            if (!name_signup.getText().toString().isEmpty() && !pswd_signup.getText().toString().isEmpty() && !psnid_signup.getText().toString().isEmpty()) {
                signup_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn_tapped));
            } else {
                signup_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn));
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        UserData ud = (UserData)data;

        if (ud.getAuthenticationId()==Constants.REGISTER) {
            //save in preferrence
            Util.setDefaults("user", username, getApplicationContext());
            Util.setDefaults("password", password, getApplicationContext());

            mManager.setUserdata(ud);
            //dismiss dialog
            dialog.dismiss();
            // go to logout page
            Intent regIntent = new Intent(getApplicationContext(),
                    CreateNewEvent.class);
            regIntent.putExtra("userdata", ud);
            startActivity(regIntent);
            finish();
        }
    }

//    private void doSignup(RequestParams params) throws JSONException {
//        ntwrk.post(url, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//
//                //post gcm token
//                postGcm();
//
//                if (user != null) {
//                    user.toJson(response);
//                    //user.setUser(username);
//                    user.setPassword(password);
//
//                    //save in preferrence
//                    Util.setDefaults("user", username, getApplicationContext());
//                    Util.setDefaults("password", password, getApplicationContext());
//
////                    //save in preferrence
////                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
////                    Gson gson = new Gson();
////                    String json = gson.toJson(user); // myObject - instance of MyObject
////                    prefsEditor.putString("userdata", json);
////                    prefsEditor.commit();
//                }
//                // If the response is JSONObject instead of expected JSONArray
////                Toast.makeText(RegisterActivity.this, "Signup Success",
////                        Toast.LENGTH_SHORT).show();
//                Intent regIntent = new Intent(getApplicationContext(),
//                        LogoutActivity.class);
//                regIntent.putExtra("userdata", user);
//                startActivity(regIntent);
//                finish();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Toast.makeText(RegisterActivity.this, "Signup error from server  - " + statusCode,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void postGcm() {
//        //post gcm token
//        Util.getGCMToken(this, mManager);
//    }

}
