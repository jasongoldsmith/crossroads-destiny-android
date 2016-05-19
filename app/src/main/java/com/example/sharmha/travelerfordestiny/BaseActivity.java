package com.example.sharmha.travelerfordestiny;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharmha.travellerdestiny.R;

public class BaseActivity extends Activity {

    protected RelativeLayout errLayout;
    protected TextView errText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setErrText(String errorText) {
        if(errText==null ){
            errText = (TextView) findViewById(R.id.error_sub);
        }

        if (errLayout==null) {
            errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        }
        if(errText!=null && errLayout!=null) {
            errLayout.setVisibility(View.GONE);
            errLayout.setVisibility(View.VISIBLE);
            errText.setText(errorText);
        }
    }

    public void closeView(View view) {
        if(errLayout!=null) {
            errLayout.setVisibility(View.GONE);
        }
    }
}
