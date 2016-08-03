package co.crossroadsapp.destiny;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 2/22/16.
 */
public class LoginActivity extends BaseActivity implements Observer {

    private EditText name_login;
    private EditText pswd_login;
    private ImageView login_btn;
    private UserData user;
    private String username;
    private String password;

    private ControlManager mManager;
    private ProgressDialog dialog;
    private Intent localPushEvent;
    private TextView forgotLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            if(b.getParcelable("userdata")!=null) {
                user = b.getParcelable("userdata");
            }
        }

        if(b.containsKey("eventIntent")) {
            localPushEvent = (Intent) b.get("eventIntent");
        }

        name_login = (EditText) findViewById(R.id.login_name);
        pswd_login = (EditText) findViewById(R.id.login_pswrd);
        pswd_login.setTypeface(Typeface.DEFAULT);
        pswd_login.setTransformationMethod(new PasswordTransformationMethod());
        login_btn = (ImageView) findViewById(R.id.login_btn);

//        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
//        errText = (TextView) findViewById(R.id.error_sub);
//        close_err = (ImageView) findViewById(R.id.err_close);

        forgotLogin = (TextView) findViewById(R.id.forgot_login);

        forgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        ForgotLoginActivity.class);
                startActivity(intent);
            }
        });

//        close_err.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                errLayout.setVisibility(View.GONE);
//            }
//        });

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
                    if(username.length()==0){
                        showError(getResources().getString(R.string.username_missing));
                    } else if(password.length()==0) {
                        showError(getResources().getString(R.string.password_missing));
                    } else {
                        RequestParams params = new RequestParams();
                        params.put("userName", username);
                        params.put("passWord", password);
                        dialog.show();
                        dialog.setCancelable(false);
                        mManager.postLogin(LoginActivity.this, params, Constants.LOGIN);
                    }
                }
            }
        });

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
        login_btn.setEnabled(true);
        setErrText(err);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoMainActivity();
    }

    private void gotoMainActivity() {
        Intent signinIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(signinIntent);
        finish();
    }

    @Override
    public void update(Observable observable, Object data) {
        //dismiss progress
        dialog.dismiss();
        if (data!=null) {
            UserData ud = (UserData) data;
            if (ud!=null && ud.getUserId()!=null) {
                if(ud.getAuthenticationId() == Constants.LOGIN) {
                //mManager.getEventList();
                //save in preferrence
                Util.setDefaults("user", username, getApplicationContext());
                Util.setDefaults("password", password, getApplicationContext());

                ud.setPassword(password);
                mManager.setUserdata(ud);
                //decide landing page based on push notification available or not
                Intent regIntent;

                //decide activity to open
                regIntent = mManager.decideToOpenActivity(localPushEvent);
//            if(localPushEvent!=null) {
//                regIntent = new Intent(getApplicationContext(),
//                        ListActivityFragment.class);
//                regIntent.putExtra("eventIntent", localPushEvent);
//            } else {
//                regIntent = new Intent(getApplicationContext(),
//                        CreateNewEvent.class);
//            }
                regIntent.putExtra("userdata", ud);

                startActivity(regIntent);
                finish();
            }
            }
        }
    }
}
