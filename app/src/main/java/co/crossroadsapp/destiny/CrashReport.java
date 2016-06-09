package co.crossroadsapp.destiny;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.R;

/**
 * Created by sharmha on 3/31/16.
 */
public class CrashReport extends Activity {

    private EditText crash_text;
    private CardView send_crash;
    private UserData user;
    private ControlManager controlManager;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_report_activity);

        controlManager = ControlManager.getmInstance();

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        crash_text = (EditText) findViewById(R.id.crash_edittext);
        send_crash = (CardView) findViewById(R.id.send_crash);
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
                if(crash_text.getText().toString().length()>0){
                    //send crash report
                    if(user.getUserId()!=null) {
                        controlManager.postCrash(CrashReport.this, user.getUserId(), crash_text.getText().toString());
                    }
                    showToastAndClose();
                }
            }
        });
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
}
