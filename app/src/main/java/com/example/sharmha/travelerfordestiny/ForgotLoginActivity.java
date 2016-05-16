package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharmha.travellerdestiny.R;
import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;

public class ForgotLoginActivity extends Activity implements Observer {

    private ImageView backBtn;
    private EditText psnId;
    private CardView resetPassword;

    private RelativeLayout errLayout;
    private TextView errText;
    private ImageView close_err;
    private ProgressDialog dialog;
    private ControlManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        backBtn = (ImageView) findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialog(this);

        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errText = (TextView) findViewById(R.id.error_sub);
        close_err = (ImageView) findViewById(R.id.err_close);

        close_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errLayout.setVisibility(View.GONE);
            }
        });

        resetPassword = (CardView) findViewById(R.id.send_forgotlogin);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psnId!=null) {
                    String psnString = psnId.getText().toString();
                    if ((!psnString.isEmpty()) && psnString.length() > 3) {
                        dialog.show();
                        resetPassword.setEnabled(false);
                        RequestParams params = new RequestParams();
                        params.put("psnId", psnString);
                        mManager.postResetPassword(ForgotLoginActivity.this, params);
                    } else {
                        showError("Please enter complete PSN ID");
                    }
                }
            }
        });

        psnId = (EditText) findViewById(R.id.forgot_psn);

        psnId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // the user is done typing.
                    resetPassword.performClick();
                }
                return false;
            }
        });
    }

    public void showError(String err) {
        dialog.dismiss();
        resetPassword.setEnabled(true);
        errLayout.setVisibility(View.GONE);
        errLayout.setVisibility(View.VISIBLE);
        errText.setText(err);
    }

    @Override
    public void update(Observable observable, Object data) {
        dialog.dismiss();
        finish();
    }
}
