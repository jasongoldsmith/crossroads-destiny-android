package co.crossroadsapp.destiny;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;

import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.R;

/**
 * Created by sharmha on 3/31/16.
 */
public class CrashReport extends BaseActivity implements Observer {

    private EditText crash_text;
    private TextView send_crash;
    private UserData user;
    private ControlManager controlManager;
    private ImageView backBtn;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_report_activity);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.img_background_map));

        controlManager = ControlManager.getmInstance();

        Bundle b = getIntent().getExtras();
        //user = b.getParcelable("userdata");
        user = controlManager.getUserData();

        controlManager.setCurrentActivity(CrashReport.this);

        crash_text = (EditText) findViewById(R.id.crash_edittext);
        email = (EditText) findViewById(R.id.crash_email);
        send_crash = (TextView) findViewById(R.id.send_crash);
        backBtn = (ImageView) findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        crash_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // the user is done typing.
                    send_crash.requestFocus();
                    send_crash.performClick();
                }
                return false;
            }
        });

        send_crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(crash_text.getText().toString().length()>0 && !email.getText().toString().isEmpty()){
                    //send crash report
                    hideKeyboard();
                    showProgressBar();
                    RequestParams requestParams = new RequestParams();
                    if(user!=null && user.getUserId()!=null) {
                        requestParams.put("reporter", user.getUserId());
                    }
                    requestParams.put("reporterEmail", email.getText().toString());
                    requestParams.put("reportDetails", crash_text.getText().toString());
                    controlManager.postCrash(CrashReport.this, requestParams);
                    //showToastAndClose();
                }
            }
        });
    }

    public void showError(String err) {
        hideProgressBar();
        showError(err);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(CrashReport.this.getCurrentFocus().getWindowToken(),0);
    }

    private void showToastAndClose() {
        Toast.makeText(this, "Message Sent",
                Toast.LENGTH_SHORT).show();
        long timeInMillisecondTheToastIsShowingFor = 1000;
        (new Handler())
                .postDelayed(
                        new Runnable() {
                            public void run() {
                                // finish this activity here
                                finish();
                            }
                        }, timeInMillisecondTheToastIsShowingFor);
    }

    @Override
    public void update(Observable observable, Object data) {
        hideProgressBar();
        Intent intent = new Intent(getApplicationContext(),
                MessageSent.class);
        startActivity(intent);
        finish();
    }
}
