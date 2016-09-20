package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import co.crossroadsapp.destiny.data.AppVersion;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.EventList;
import co.crossroadsapp.destiny.network.EventListNetwork;
import co.crossroadsapp.destiny.network.GetVersion;
import co.crossroadsapp.destiny.network.LoginNetwork;
import co.crossroadsapp.destiny.network.LogoutNetwork;
import co.crossroadsapp.destiny.utils.TravellerLog;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.utils.Version;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends BaseActivity implements Observer {

    //private View register_layout;
    private TextView signin_layout;
    public UserData userData;
    Intent contentIntent;
    private String p;
    private String u;
    private ControlManager mManager;
    private RecyclerView horizontal_recycler_view;
    private ArrayList<EventData> horizontalList;
    private EventCardAdapter horizontalAdapter;
    private TextView privacyTerms;
    private WebView webView;
    private String console;
    private TextView countText;

    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);
        setContentView(R.layout.splash_loading);
        setTRansparentStatusBar();
        TravellerLog.w(this, "MainActivity.onCreate starts...");
        u= Util.getDefaults("user", getApplicationContext());
        p = Util.getDefaults("password", getApplicationContext());
        console = Util.getDefaults("consoleType", getApplicationContext());

        userData = new UserData();

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        // getting contentIntent from push notification click
        if (this.getIntent().hasExtra(Constants.TRAVELER_NOTIFICATION_INTENT)) {
            //tracking OS pushnotification initiation
            Map<String, Boolean> json = new HashMap<>();
            json.put("inApp", false);
            Util.postTracking(json, MainActivity.this, mManager, Constants.APP_PUSHNOTIFICATION);
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
        if (err != null){
            if(!err.isEmpty()) {
                //Util.clearDefaults(this);
                launchLogin();
                finish();
            } else {
                forwardAfterVersionCheck();
            }
        }
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

    private void forwardAfterVersionCheck() {
        if (u != null && p!= null && console!=null && !u.isEmpty() && !p.isEmpty() && !console.isEmpty()) {
            //todo check how to minimize api calls to get full event list in future from multiple locations
            TravellerLog.w(this, "Logging user in the background as user data available");
            //check if existing user with version below 1.1.0
            String newUser = Util.getDefaults("showUnverifiedMsg", getApplicationContext());
            if(newUser==null) {
                // continue with delete
                RequestParams rp = new RequestParams();
                rp.put("userName", u);
                mManager.postLogout(MainActivity.this, rp);
            } else {
                mManager.getEventList();
                if (mManager.getEventListCurrent() != null) {
                    if (mManager.getEventListCurrent().isEmpty()) {
                        mManager.getEventList();
                    }
                } else {
                    mManager.getEventList();
                }
                if (mManager.getCurrentGroupList() != null) {
                    if (mManager.getCurrentGroupList().isEmpty()) {
                        mManager.getGroupList(null);
                    }
                } else {
                    mManager.getGroupList(null);
                }
                Util.storeUserData(userData, u, p);
                RequestParams params = new RequestParams();
                HashMap<String, String> consoles = new HashMap<String, String>();
                consoles.put("consoleType", console);
                consoles.put("consoleId", u);
                params.put("consoles", consoles);
                params.put("passWord", p);
                mManager.postLogin(MainActivity.this, params, Constants.LOGIN);
            }
        }else {
            launchMainLayout();
        }
    }

    private void launchMainLayout() {
        TravellerLog.w(this, "Show main activity layout as user data not available");
        setContentView(R.layout.activity_main);

        mManager.getPublicEventList(MainActivity.this);

        privacyTerms = (TextView) findViewById(R.id.privacy_terms);

        countText = (TextView) findViewById(R.id.player_count);

        webView = (WebView) findViewById(R.id.web);

        setTextViewHTML(privacyTerms, getString(R.string.terms_conditions));

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontalList=new ArrayList<EventData>();
        if(mManager.getEventListCurrent()!=null) {
            horizontalList = mManager.getEventListCurrent();
        }
        horizontalAdapter=new EventCardAdapter(horizontalList, null, MainActivity.this, mManager, Constants.PUBLIC_EVENT_FEED);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        startSpinner();

//        if(mManager!=null && mManager.getEventListCurrent()!=null && !mManager.getEventListCurrent().isEmpty()) {
//            horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
//            horizontalList=new ArrayList<EventData>();
//            horizontalList = mManager.getEventListCurrent();
//            horizontalAdapter=new EventCardAdapter(horizontalList, null, MainActivity.this, mManager, Constants.PUBLIC_EVENT_FEED);
//            LinearLayoutManager horizontalLayoutManagaer
//                    = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
//            horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
//            horizontal_recycler_view.setAdapter(horizontalAdapter);
//
//            if(horizontalAdapter.elistLocal.size()>1) {
//                final int speedScroll = 2000;
//                final Handler handler = new Handler();
//                final Runnable runnable = new Runnable() {
//                    int count = 0;
//
//                    @Override
//                    public void run() {
//                        if (count < horizontalAdapter.elistLocal.size()) {
//                            horizontal_recycler_view.smoothScrollToPosition(++count);
//                            handler.postDelayed(this, speedScroll);
//                        } else {
//                            count = 0;
//                            horizontal_recycler_view.scrollToPosition(count);
//                            handler.postDelayed(this, speedScroll);
//                        }
//                    }
//                };
//
//                handler.postDelayed(runnable, speedScroll);
//            }
//
//        } else {
//            mManager.getPublicEventList(MainActivity.this);
//        }
        //register_layout = findViewById(R.id.register);
        signin_layout = (TextView) findViewById(R.id.signin);
//            register_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //tracking signup initiation
//                    Map<String, String> json = new HashMap<String, String>();
//                    Util.postTracking(json, MainActivity.this, mManager, Constants.APP_SIGNUP);
//                    TravellerLog.w(this, "Launch console selection page activity");
//                    Intent regIntent = new Intent(getApplicationContext(),
//                            ConsoleSelectionActivity.class);
//                    //regIntent.putExtra("userdata", userData);
//                    startActivity(regIntent);
//                    finish();
//                }
//            });
        signin_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravellerLog.w(this, "Launch login page activity");

                launchLogin();
                finish();
            }
        });
    }

    private void startSpinner() {
        if(horizontalAdapter!=null && horizontal_recycler_view!=null) {
            if (horizontalAdapter.elistLocal!=null && horizontalAdapter.elistLocal.size() > 1) {
                final int speedScroll = 2000;
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count < horizontalAdapter.elistLocal.size()) {
                            horizontal_recycler_view.smoothScrollToPosition(++count);
                            handler.postDelayed(this, speedScroll);
                        } else {
                            count = 0;
                            horizontal_recycler_view.scrollToPosition(count);
                            handler.postDelayed(this, speedScroll);
                        }
                    }
                };

                handler.postDelayed(runnable, speedScroll);
            }
        }
    }

    private void launchLogin() {
        Intent signinIntent = new Intent(getApplicationContext(),
                LoginActivity.class);
        //signinIntent.putExtra("userdata", userData);
        if(contentIntent!=null) {
            signinIntent.putExtra("eventIntent", contentIntent);
        }
        if(mManager!=null && mManager.getEventListCurrent()!=null) {
            mManager.getEventListCurrent().clear();
        }
        startActivity(signinIntent);
        finish();
    }

    protected void setTextViewHTML(TextView text, String html)
    {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
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
        if(webView!=null && webView.getVisibility()==View.VISIBLE) {
            webView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            finish();
        }
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
                    //regIntent.putExtra("userdata", ud);
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
        }else if(observable instanceof LogoutNetwork) {
            launchMainLayout();
            showGenericError("CHANGES TO SIGN IN", "Your gamertag now replaces your Crossroads username when logging in.\n" +
                    "(your password is still the same)", "OK");
        } else if(observable instanceof EventListNetwork) {
            if(data!=null) {
                horizontalAdapter.elistLocal.clear();
                horizontalAdapter.addItem(mManager.getEventListCurrent(), null);
                horizontalAdapter.notifyDataSetChanged();
                startSpinner();
            }
        }
    }

    public void setUserCount(String userCount) {
        if(countText!=null) {
            countText.setVisibility(View.VISIBLE);
            countText.setText(userCount);
        }
    }
}
