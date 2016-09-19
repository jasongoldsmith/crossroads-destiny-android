package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 2/22/16.
 */
public class LoginActivity extends BaseActivity implements Observer {

    private EditText name_login;
    private EditText pswd_login;
    private TextView login_btn;
    private UserData user;
    private String username;
    private String password;

    private ControlManager mManager;
    private ProgressDialog dialog;
    private Intent localPushEvent;
    private TextView forgotLogin;
    private CardView playstationBtn;
    private CardView xboxBtn;
    private TextView playstationBtnText;
    private TextView xboxBtnText;
    private ImageView showPswd;
    private String consoleType = "PS4";
    private ImageView back;
    private ImageView heroImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.img_background_map));

        //setTRansparentStatusBar();

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            if (b.containsKey("eventIntent")) {
                localPushEvent = (Intent) b.get("eventIntent");
            }
        }

        name_login = (EditText) findViewById(R.id.login_name);
        pswd_login = (EditText) findViewById(R.id.login_pswrd);
        pswd_login.setTypeface(Typeface.DEFAULT);
        pswd_login.setTransformationMethod(new PasswordTransformationMethod());
        login_btn = (TextView) findViewById(R.id.signin);

        playstationBtn = (CardView) findViewById(R.id.playstation_btn);
        xboxBtn = (CardView) findViewById(R.id.xbox_btn);
        playstationBtnText = (TextView) findViewById(R.id.playstation_text);
        xboxBtnText = (TextView) findViewById(R.id.xbox_text);
        showPswd = (ImageView) findViewById(R.id.show_pswd);

        back = (ImageView) findViewById(R.id.backbtn);

        heroImg = (ImageView) findViewById(R.id.hero_img);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                gotoMainActivity();
            }
        });

        final boolean[] showPswdState = new boolean[1];

        showPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pswd_login!=null && !pswd_login.getText().toString().isEmpty()) {
                    if(!showPswdState[0]) {
                        pswd_login.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        showPswdState[0] = true;
                    } else {
                        pswd_login.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        showPswdState[0] = false;
                    }
                }
            }
        });

        playstationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playstationBtn.setCardBackgroundColor(getResources().getColor(R.color.app_theme_color));
                playstationBtnText.setTextColor(getResources().getColor(R.color.trimbe_white));
                name_login.setHint(getResources().getString(R.string.playstation_hint));
                xboxBtn.setCardBackgroundColor(getResources().getColor(R.color.edittext_background));
                xboxBtnText.setTextColor(getResources().getColor(R.color.hinttext_color));
                consoleType = "PS4";
            }
        });

        xboxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xboxBtn.setCardBackgroundColor(getResources().getColor(R.color.app_theme_color));
                xboxBtnText.setTextColor(getResources().getColor(R.color.trimbe_white));
                name_login.setHint(getResources().getString(R.string.xbox_hint));
                playstationBtn.setCardBackgroundColor(getResources().getColor(R.color.edittext_background));
                playstationBtnText.setTextColor(getResources().getColor(R.color.hinttext_color));
                consoleType = "XBOXONE";
            }
        });

        forgotLogin = (TextView) findViewById(R.id.forgot_login);

        forgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_login.setText("");
                pswd_login.setText("");
                hideKeyboard();
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

        user = mManager.getUserData();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                username = name_login.getText().toString();
                password = pswd_login.getText().toString();
                if (username!=null && password!=null) {
                    if(username.length()==0){
                        showError(getResources().getString(R.string.username_missing), null);
                    } else if(password.length()==0) {
                        showError(getResources().getString(R.string.password_missing), null);
                    } else {
                        RequestParams params = new RequestParams();
                        HashMap<String, String> consoles = new HashMap<String, String>();
                        consoles.put("consoleType", consoleType);
                        consoles.put("consoleId", username);
                        params.put("consoles", consoles);
                        params.put("passWord", password);
                        dialog.show();
                        //login_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn_tapped));
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

        final View contentView = this.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    heroImg.setVisibility(View.GONE);
                }
                else {
                    // keyboard is closed
                    heroImg.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTRansparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }

        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void showKeyboard() {
        name_login.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard() {
        name_login.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),0);
    }

    public void showError(String err, String errorType) {
        dialog.dismiss();
        login_btn.setEnabled(true);
        name_login.setText("");
        pswd_login.setText("");
        if(errorType!=null && !errorType.isEmpty()) {
            if(errorType.equalsIgnoreCase(Constants.BUNGIE_ERROR)) {
                Intent intent = new Intent(getApplicationContext(),
                        MissingUser.class);
                intent.putExtra("id", username);
                intent.putExtra("error", err);
                startActivity(intent);
            } else if(errorType.equalsIgnoreCase(Constants.BUNGIE_LEGACY_ERROR)) {
                showGenericError("LEGACY CONSOLES", "In line with Rise of Iron, we now only support next-gen consoles. When you’ve upgraded your console, please come\n" +
                        "back and join us!", "OK");
            }
        } else {
            setErrText(err);
        }
    }

    private void enableSubmitIfReady() {
        if(name_login!=null && pswd_login!=null) {
            if (!name_login.getText().toString().isEmpty() && !pswd_login.getText().toString().isEmpty()) {
                login_btn.setBackgroundColor(getResources().getColor(R.color.signin_pressed));
            } else {
                login_btn.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
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
        //login_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_login_btn));
        if (data!=null) {
            UserData ud = (UserData) data;
            if (ud!=null && ud.getUserId()!=null) {
                if(ud.getAuthenticationId() == Constants.LOGIN) {
                //mManager.getEventList();
                //save in preferrence
                Util.setDefaults("user", username, getApplicationContext());
                Util.setDefaults("password", password, getApplicationContext());
                    Util.setDefaults("consoleType", consoleType, getApplicationContext());

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
                //regIntent.putExtra("userdata", ud);

                startActivity(regIntent);
                finish();
            }
            }
        }
    }
}
