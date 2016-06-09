package co.crossroadsapp.destiny;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.utils.Util;

public class BaseActivity extends Activity {

    protected RelativeLayout errLayout;
    protected TextView errText;
    private RelativeLayout progress;

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

        // show timed error message
        Util.showErrorMsg(errLayout, errText, errorText);

//        if(errText!=null && errLayout!=null) {
//            errLayout.setVisibility(View.GONE);
//            errLayout.setVisibility(View.VISIBLE);
//            errText.setText(errorText);
//
//            //put timer to make the error message gone after 5 seconds
//            errLayout.postDelayed(new Runnable() {
//                public void run() {
//                    if(errLayout!=null) {
//                        errLayout.setVisibility(View.GONE);
//                    }
//                }
//            }, 5000);
//        }
    }

    public void closeView(View view) {
        if(errLayout!=null) {
            errLayout.setVisibility(View.GONE);
        }
    }

    protected void showProgressBar() {
        if(progress == null) {
            progress = (RelativeLayout) findViewById(R.id.progress_base_layout);
        }

        if(progress!=null) {
            hideProgressBar();
            progress.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if(progress!=null) {
            progress.setVisibility(View.GONE);
        }
    }
}
