package co.crossroadsapp.destiny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import co.crossroadsapp.destiny.data.AppVersion;
import co.crossroadsapp.destiny.network.GetVersion;
import co.crossroadsapp.destiny.network.LoginNetwork;
import co.crossroadsapp.destiny.utils.TravellerLog;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.utils.Version;
import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends BaseActivity implements Observer {

    private View register_layout;
    private View signin_layout;
    public UserData userData;
    Intent contentIntent;
    private String p;
    private String u;
    private ControlManager mManager;

    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);
        setContentView(R.layout.splash_loading);
        TravellerLog.w(this, "MainActivity.onCreate starts...");
        u= Util.getDefaults("user", getApplicationContext());
        p = Util.getDefaults("password", getApplicationContext());

        userData = new UserData();

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        // getting contentIntent from push notification click
        if (this.getIntent().hasExtra(Constants.TRAVELER_NOTIFICATION_INTENT)) {
            TravellerLog.w(this, "Push notification intent present");
            Intent messageIntent = (Intent) this.getIntent().getExtras().get(Constants.TRAVELER_NOTIFICATION_INTENT);
            if (messageIntent == null) {
                return;
            }
            contentIntent = null;
            if(messageIntent.getExtras() != null) {
                contentIntent = (Intent) messageIntent.getExtras().get(Constants.NOTIFICATION_INTENT_CHANNEL);
            }
        }

        //check android version for dev builds
        mManager.getAndroidVersion(this);

        forwardAfterVersionCheck();

        TravellerLog.w(this, "MainActivity.onCreate ends...");
    }

    public void showError(String err) {
        Util.clearDefaults(this);
        launchLogin();
        finish();
    }

    private void forwardAfterVersionCheck() {
        if (u != null && p!= null && !u.isEmpty() && !p.isEmpty()) {
            //todo check how to minimize api calls to get full event list in future from multiple locations
            TravellerLog.w(this, "Logging user in the background as user data available");
            mManager.getEventList();
            mManager.getGroupList(null);
            Util.storeUserData(userData, u, p);
            RequestParams params = new RequestParams();
            params.put("userName", u);
            params.put("passWord", p);
            mManager.postLogin(MainActivity.this, params, Constants.LOGIN);
        }else {
            TravellerLog.w(this, "Show main activity layout as user data not available");
            setContentView(R.layout.activity_main);

            register_layout = findViewById(R.id.register);
            signin_layout = findViewById(R.id.sign_in);

            register_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent regIntent = new Intent(getApplicationContext(),
//                            RegisterActivity.class);
                    TravellerLog.w(this, "Launch console selection page activity");
                    Intent regIntent = new Intent(getApplicationContext(),
                            ConsoleSelectionActivity.class);
                    regIntent.putExtra("userdata", userData);
                    startActivity(regIntent);
                    finish();
                }
            });

            signin_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TravellerLog.w(this, "Launch login page activity");
                    launchLogin();
                }
            });
        }
    }

    private void launchLogin() {
        Intent signinIntent = new Intent(getApplicationContext(),
                LoginActivity.class);
        signinIntent.putExtra("userdata", userData);
        if(contentIntent!=null) {
            signinIntent.putExtra("eventIntent", contentIntent);
        }
        startActivity(signinIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        TravellerLog.w(this, "Registering ReceivefromBackpressService ");
        registerReceiver(ReceivefromBackpressService, new IntentFilter("backpress_flag"));
    }

    private BroadcastReceiver ReceivefromBackpressService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        TravellerLog.w(this, "Unregistering ReceivefromBackpressService ");
        unregisterReceiver(ReceivefromBackpressService);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    public UserData getUserData() {
        if (userData!=null) {
            return userData;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof GetVersion) {
            TravellerLog.w(this, "Update observer for getversion network call response");
            AppVersion ver = (AppVersion) data;
            String currVer = Util.getApplicationVersionCode(this);
            String latestVer = ver.getVersion();
            Version currVersion = new Version(currVer);
            Version latestVersion = new Version(latestVer);
            if (latestVersion.compareTo(currVersion) > 0) {
                //mManager.getAndroidVersion(this);
            } else {
                forwardAfterVersionCheck();
            }
        } else if(observable instanceof LoginNetwork) {
            if (data!=null) {
                TravellerLog.w(this, "Update observer for LoginNetwork network call response");
                UserData ud = (UserData) data;
                if (ud!=null && ud.getUserId()!=null) {
                    if((ud.getAuthenticationId() == Constants.LOGIN)) {
                    ud.setPassword(p);
                    mManager.setUserdata(ud);
                    Intent regIntent;

                    //decide for activity
                    regIntent = mManager.decideToOpenActivity(contentIntent);

//                if (contentIntent != null) {
//                    regIntent = new Intent(getApplicationContext(),
//                            ListActivityFragment.class);
//                    regIntent.putExtra("eventIntent", contentIntent);
//                } else {
//                    regIntent = new Intent(getApplicationContext(),
//                            CreateNewEvent.class);
//                }
                    regIntent.putExtra("userdata", ud);
                    startActivity(regIntent);
                    finish();
                } else {
                        setContentView(R.layout.activity_main);
                    }
                } else {
                    setContentView(R.layout.activity_main);
                }
            } else {
                TravellerLog.w(this, "Show main activity layout as user data not available from login response");
                setContentView(R.layout.activity_main);
            }
        }
    }
}
