package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travellerdestiny.R;
import com.loopj.android.http.RequestParams;

/**
 * Created by sharmha on 3/31/16.
 */
public class CrashReport extends Activity {

    private EditText crash_text;
    private TextView send_crash;
    private UserData user;
    private ControlManager controlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_report_activity);

        controlManager = ControlManager.getmInstance();

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        crash_text = (EditText) findViewById(R.id.crash_edittext);
        send_crash = (TextView) findViewById(R.id.send_crash);

        send_crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(crash_text.getText().toString().length()>0){
                    //send crash report
                    if(user.getUserId()!=null) {
                        controlManager.postCrash(CrashReport.this, user.getUserId(), crash_text.getText().toString());
                    }
                    finish();
                }
            }
        });
    }
}
