package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import co.crossroadsapp.destiny.data.UserData;

public class AddNewActivity extends Activity {

    private ControlManager mCntrlMngr;
    private UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        setTRansparentStatusBar();

        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.setCurrentActivity(this);
        //mCntrlMngr.getAllActivities(this);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        //todo handle adscard here
//        if(b.containsKey("adsCardId")) {
//            adcardEventId = b.getString("adsCardId");
//        }
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
}
