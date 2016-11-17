package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.crossroadsapp.destiny.data.AppVersion;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.InvitationLoginData;
import co.crossroadsapp.destiny.network.ConfigNetwork;
import co.crossroadsapp.destiny.network.EventListNetwork;
import co.crossroadsapp.destiny.network.GetVersion;
import co.crossroadsapp.destiny.network.GitHubClient;
import co.crossroadsapp.destiny.network.LoginNetwork;
import co.crossroadsapp.destiny.network.LogoutNetwork;
import co.crossroadsapp.destiny.network.ServiceGenerator;
import co.crossroadsapp.destiny.utils.TravellerLog;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.utils.Version;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import retrofit2.Call;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends BaseActivity implements Observer {

    //private View register_layout;
    private RelativeLayout signin_playstation;
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
    private Runnable runnable;
    private Handler handler;
    private LinearLayoutManager horizontalLayoutManagaer;
    private InvitationLoginData invitationRp;
    private RelativeLayout signin_xbox;
    private String cookies;
    private WebView webViewPS;
    private WebView webViewXBOX;
    private RelativeLayout topBar;
    private ImageView topBarBack;

    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);
        setContentView(R.layout.splash_loading);
        setTRansparentStatusBar();
        TravellerLog.w(this, "MainActivity.onCreate starts...");
        u= Util.getDefaults("user", getApplicationContext());
        p = Util.getDefaults("password", getApplicationContext());
        cookies = Util.getDefaults("cookie", getApplicationContext());
        console = Util.getDefaults("consoleType", getApplicationContext());

        Bundle b = getIntent().getExtras();

        if(b!=null) {
            if(b!=null && b.containsKey("invitation")) {
                invitationRp = (InvitationLoginData)b.getSerializable("invitation");
            }
        }

        userData = new UserData();

//        if(userData!=null && userData.getPsnId()!=null) {
//            u = userData.getPsnId();
//        }

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        userData = mManager.getUserData();

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
        mManager.getConfig(MainActivity.this);
        TravellerLog.w(this, "MainActivity.onCreate ends...");
    }

    protected InvitationLoginData getInvitationObject() {
        if(invitationRp!=null) {
            return this.invitationRp;
        }
        return null;
    }

    public void showError(String err, String errorType) {
        hideWebviews();
        hideProgressBar();
        if (err != null){
            if(errorType!=null && !errorType.isEmpty()) {
                if(errorType.equalsIgnoreCase(Constants.BUNGIE_ERROR)) {
                    Util.clearDefaults(getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(),
                            MissingUser.class);
                    //intent.putExtra("id", username);
                    String errT = "We were unable to connect to your Bungie.net account via " + console + ". Please try again.";
                    intent.putExtra("error", errT);
                    startActivity(intent);
                } else if(errorType.equalsIgnoreCase(Constants.BUNGIE_LEGACY_ERROR)) {
                    // continue with delete
                    Util.clearDefaults(getApplicationContext());
                    showGenericError("LEGACY CONSOLES", getString(R.string.legacy_error), getString(R.string.ok_btn), null, Constants.GENERAL_ERROR, null, false);
                } else if(errorType.equalsIgnoreCase(Constants.BUNGIE_CONNECT_ERROR)) {
                    // continue with delete
                    RequestParams rp = new RequestParams();
                    if(u!=null && !u.isEmpty()) {
                        rp.put("userName", u);
                    }
                    mManager.postLogout(MainActivity.this, rp);
                    Util.clearDefaults(getApplicationContext());
                }
            } else {
                setErrText(err);
                //hideWebviews();
            }


//            if(!err.isEmpty()) {
//                Intent intent = new Intent(getApplicationContext(),
//                        MissingUser.class);
//                //intent.putExtra("id", username);
//                String errT = "We were unable to connect to your Bungie.net account via " + console + ". Please try again.";
//                intent.putExtra("error", errT);
//                startActivity(intent);
//            } else {
//                // continue with delete
//                RequestParams rp = new RequestParams();
//                rp.put("userName", u);
//                mManager.postLogout(MainActivity.this, rp);
//                //forwardAfterVersionCheck();
//            }
        } else {
            setErrText(getString(R.string.server_error_tryagain));
        }
    }

    private boolean hideWebviews() {
        if(webView!=null && webView.getVisibility()==View.VISIBLE && topBar!=null) {
            webView.setVisibility(View.GONE);
            topBar.setVisibility(View.GONE);
            return false;
        } else if(webViewPS!=null && webViewPS.getVisibility()==View.VISIBLE && topBar!=null) {
            webViewPS.setVisibility(View.GONE);
            topBar.setVisibility(View.GONE);
            intializeLoginWebViews();
            return false;
        } else if (webViewXBOX!=null && webViewXBOX.getVisibility()==View.VISIBLE && topBar!=null){
            webViewXBOX.setVisibility(View.GONE);
            topBar.setVisibility(View.GONE);
            intializeLoginWebViews();
            return false;
        }
        return true;
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
//        // Create a very simple REST adapter which points the GitHub API endpoint.
//        GitHubClient client = ServiceGenerator.createService(GitHubClient.class);
//
//        // Fetch and print a list of the contributors to this library.
//        Call<List<Contributor>> call =
//                client.contributors();
//
//        try {
//            List<Contributor> contributors = call.execute().body();
//            for (Contributor contributor : contributors) {
//                System.out.println(
//                        "Hardik - " + contributor.value + " (" + contributor.contributions + ")");
//            }
//        } catch (IOException e) {
//            // handle errors
//            System.out.println("Hardik - " + e);
//        }


        //if (u != null && p!= null && console!=null && !u.isEmpty() && !p.isEmpty() && !console.isEmpty()) {
        String isCookieValid = Util.getDefaults(Constants.COOKIE_VALID_KEY, getApplicationContext());
        if(cookies!=null && !cookies.isEmpty() && isCookieValid!=null && isCookieValid.equalsIgnoreCase("true")) {
            //todo check how to minimize api calls to get full event list in future from multiple locations
            TravellerLog.w(this, "Logging user in the background as user data available");

            if(u!=null && !u.isEmpty()) {
                RequestParams rp = new RequestParams();
                rp.put("id", u);
                mManager.getUserFromNetwork(MainActivity.this, rp);
            }

            Thread t = new Thread(){
                public void run(){
                    //start service for validating cookie
                    Intent intent = new Intent(MainActivity.this, BungieUserService.class);
                    startService(intent);
                }
            };
            t.start();

            //check if existing user with version below 1.1.0
            String newUser = Util.getDefaults("showUnverifiedMsg", getApplicationContext());
//            if(newUser==null) {
//                // continue with delete
//                RequestParams rp = new RequestParams();
//                rp.put("userName", u);
//                mManager.postLogout(MainActivity.this, rp);
//            } else {
//                mManager.getEventList();
//                if (mManager.getEventListCurrent() != null) {
//                    if (mManager.getEventListCurrent().isEmpty()) {
//                        mManager.getEventList();
//                    }
//                } else {
//                    mManager.getEventList();
//                }
//                if (mManager.getCurrentGroupList() != null) {
//                    if (mManager.getCurrentGroupList().isEmpty()) {
//                        mManager.getGroupList(null);
//                    }
//                } else {
//                    mManager.getGroupList(null);
//                }
//                Util.storeUserData(userData, u, p);
//                RequestParams params = new RequestParams();
//                HashMap<String, String> consoles = new HashMap<String, String>();
//                consoles.put("consoleType", console);
//                consoles.put("consoleId", u);
//                params.put("consoles", consoles);
//                params.put("passWord", p);
//                mManager.postLogin(MainActivity.this, params, Constants.LOGIN);
            //let user move on to app
//            Intent regIntent;
//
//            //decide for activity
//            regIntent = mManager.decideToOpenActivity(contentIntent);
//            startActivity(regIntent);
//            finish();

//                String csrf;
//                String[] pair = cookies.split(";");
//                for(int i=0; i<pair.length;i++) {
//                    String temp = pair[i].substring(0, pair[i].indexOf('=')).trim();
//                    if(temp.equalsIgnoreCase("bungled")) {
//                        csrf = pair[i].substring(pair[i].indexOf('=') + 1, pair[i].length());
//                        Util.setDefaults("csrf", csrf, MainActivity.this);
//                        Util.setDefaults("cookie", cookies, MainActivity.this);
//                        //network call to get current user
//                        mManager.getBungieCurrentUser(csrf, cookies, MainActivity.this);
//                        return;
//                    }
//                }
//            }
        }else {
            if(cookies==null && p!=null) {
                showGenericError("CHANGES TO SIGN IN", "Good news! You can now sign in using your Xbox or PlayStation account (the same one you use for Bungie.net)", "OK", null, Constants.GENERAL_ERROR, null, false);
            }
            if(u!=null && !u.isEmpty()) {
                // continue with delete
                RequestParams rp = new RequestParams();
                rp.put("userName", u);
                mManager.postLogout(MainActivity.this, rp);
            } else {
                Util.clearDefaults(getApplicationContext());
            }
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
        webViewPS = (WebView) findViewById(R.id.web_ps);
        webViewXBOX = (WebView) findViewById(R.id.web_xbox);

        intializeLoginWebViews();

        setTextViewHTML(privacyTerms, getString(R.string.terms_conditions));

        topBar = (RelativeLayout) findViewById(R.id.top_header);
        topBarBack = (ImageView) findViewById(R.id.main_backbtn);

        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                intializeLoginWebViews();
            }
        });

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontalList=new ArrayList<EventData>();
        if(mManager.getEventListCurrent()!=null) {
            horizontalList = mManager.getEventListCurrent();
        }
        horizontalAdapter=new EventCardAdapter(horizontalList, null, MainActivity.this, mManager, Constants.PUBLIC_EVENT_FEED);
        horizontalLayoutManagaer
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
        signin_playstation = (RelativeLayout) findViewById(R.id.playstation);
        signin_xbox = (RelativeLayout) findViewById(R.id.xbox);
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
        signin_playstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravellerLog.w(this, "Launch login page activity");
                console = Constants.PLAYSTATION;
                launchLogin();
                //finish();
            }
        });
        signin_xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravellerLog.w(this, "Launch login page activity");
                console = Constants.XBOX;
                launchLogin();
                //finish();
            }
        });
    }

    private void intializeLoginWebViews() {
        intializeWebViews(webViewPS, mManager.getPSNLoginUrl()!=null?mManager.getPSNLoginUrl():Constants.BUNGIE_PSN_LOGIN);
        intializeWebViews(webViewXBOX, mManager.getXboxLoginUrl()!=null?mManager.getXboxLoginUrl():Constants.BUNGIE_XBOX_LOGIN);
    }

    private void intializeWebViews(WebView wb, String url) {
        if(wb!=null) {
            wb.removeAllViews();
            wb.getSettings().setJavaScriptEnabled(true);
            wb.getSettings().setLoadWithOverviewMode(true);
            wb.getSettings().setUseWideViewPort(true);
            wb.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if(url.equalsIgnoreCase("https://www.bungie.net/")){
                        showBungieProgressBar();
                        //hideWebviews();
                    }
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    cookies = CookieManager.getInstance().getCookie(url);
                    String csrf;
                    String[] pair = cookies.split(";");
                    for (int i = 0; i < pair.length; i++) {
                        String temp = pair[i].substring(0, pair[i].indexOf('=')).trim();
                        if (temp.equalsIgnoreCase("bungled")) {
//                        webView.setVisibility(View.GONE);
                            csrf = pair[i].substring(pair[i].indexOf('=') + 1, pair[i].length());
                            Util.setDefaults("csrf", csrf, MainActivity.this);
                            Util.setDefaults("cookie", cookies, MainActivity.this);
                            //network call to get current user
                            mManager.getBungieCurrentUser(csrf, cookies, getApplicationContext());
                            return;
                        }
                    }
                }
            });
            if(url!=null) {
                wb.loadUrl(url);
            }
        }
    }

    private void smoothScroll(final int position) {
        horizontal_recycler_view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    horizontal_recycler_view.setOnScrollListener(null);

                    // Fix for scrolling bug
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            horizontalLayoutManagaer.scrollToPositionWithOffset(position, 0);
                        }
                    });
                }
            }
        });

        horizontal_recycler_view.smoothScrollToPosition(position);
    }

    private void startSpinner() {
        if(horizontalAdapter!=null && horizontal_recycler_view!=null) {
            if (horizontalAdapter.elistLocal!=null && horizontalAdapter.elistLocal.size() > 1) {
                final int speedScroll = 2000;
                handler = new Handler();
                runnable = new Runnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count < horizontalAdapter.elistLocal.size()) {
                            //horizontal_recycler_view.smoothScrollToPosition(++count);
                            smoothScroll(++count);
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

    private void stopSpinner() {
        if(handler!=null && runnable!=null) {
            handler.removeCallbacks(runnable);
        }
    }


    private void launchLogin() {
        if(console!=null){
            if(console.equalsIgnoreCase(Constants.PLAYSTATION)) {
                if(webViewPS!=null) {
                    topBar.setVisibility(View.VISIBLE);
                    webViewPS.setVisibility(View.VISIBLE);
                }
            } else if(console.equalsIgnoreCase(Constants.XBOX)) {
                if(webViewXBOX!=null) {
                    topBar.setVisibility(View.VISIBLE);
                    webViewXBOX.setVisibility(View.VISIBLE);
                }
            }
        }

        //webView = new WebView(this);
//        if(webView!=null && url!=null) {
//            webView.removeAllViews();
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url){
//                cookies = CookieManager.getInstance().getCookie(url);
//                String csrf;
//                String[] pair = cookies.split(";");
//                for(int i=0; i<pair.length;i++) {
//                    String temp = pair[i].substring(0, pair[i].indexOf('=')).trim();
//                    if(temp.equalsIgnoreCase("bungled")) {
//                        showProgressBar();
////                        webView.setVisibility(View.GONE);
//                        csrf = pair[i].substring(pair[i].indexOf('=') + 1, pair[i].length());
//                        Util.setDefaults("csrf", csrf, MainActivity.this);
//                        Util.setDefaults("cookie", cookies, MainActivity.this);
//                        //network call to get current user
//                        mManager.getBungieCurrentUser(csrf, cookies, MainActivity.this);
//                        return;
//                    }
//                }
//            }
//        });
//
//            webView.loadUrl(url);
//            webView.setVisibility(View.VISIBLE);
////        Intent signinIntent = new Intent(getApplicationContext(),
////                LoginActivity.class);
////        //signinIntent.putExtra("userdata", userData);
////        if(contentIntent!=null) {
////            signinIntent.putExtra("eventIntent", contentIntent);
////        }
////        if(mManager!=null && mManager.getEventListCurrent()!=null) {
////            mManager.getEventListCurrent().clear();
////        }
////        if(invitationRp!=null) {
////            signinIntent.putExtra("invitation", invitationRp);
////        }
////        startActivity(signinIntent);
////        finish();
//        }
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
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(span.getURL());
                topBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.VISIBLE);
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
        startSpinner();
    }

    private BroadcastReceiver ReceivefromBackpressService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public void onStop() {
        stopSpinner();
        TravellerLog.w(this, "Unregistering ReceivefromBackpressService ");
        unregisterReceiver(ReceivefromBackpressService);
        hideProgressBar();
        hideWebviews();
        super.onStop();
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
        if(hideWebviews()) {
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
                if (ud!=null && ud.getValue().getId()!=null) {
//                    if((ud.getAuthenticationId() == Constants.LOGIN)) {
                        //save in preferrence
                        Util.setDefaults("user", ud.getValue().getId(), getApplicationContext());
//                        Util.setDefaults("password", password, getApplicationContext());
                        Util.setDefaults("consoleType", console, getApplicationContext());
                    //ud.setPassword(p);
                    mManager.setUserdata(ud);
                    Intent regIntent;

                    //decide for activity
                    //regIntent = mManager.decideToOpenActivity(contentIntent);

                        regIntent = new Intent(getApplicationContext(),
                                ListActivityFragment.class);
                        if (contentIntent != null ) {
                            regIntent.putExtra("eventIntent", contentIntent);
                        }

                        //clear invitation req params
                        if(invitationRp!=null) {
                            invitationRp.clearRp();
                        }

                    startActivity(regIntent);
                    finish();
//                } else {
//                        setContentView(R.layout.activity_main);
//                    }
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
                    "(your password is still the same)", "OK", null, Constants.GENERAL_ERROR, null, false);
        } else if(observable instanceof EventListNetwork) {
            if(data!=null) {
                horizontalAdapter.elistLocal.clear();
                horizontalAdapter.addItem(mManager.getEventListCurrent(), null);
                horizontalAdapter.notifyDataSetChanged();
                startSpinner();
            }
        } else if(observable instanceof ConfigNetwork) {
            if(data!=null) {
                if(mManager!=null) {
                    mManager.parseAndSaveConfigUrls((JSONObject) data);
                }
                forwardAfterVersionCheck();
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
