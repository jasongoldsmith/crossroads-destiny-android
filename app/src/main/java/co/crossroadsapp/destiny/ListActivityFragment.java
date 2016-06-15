package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import co.crossroadsapp.destiny.data.GroupData;
import co.crossroadsapp.destiny.network.EventByIdNetwork;
import co.crossroadsapp.destiny.network.GroupListNetwork;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.crossroadsapp.destiny.data.CurrentEventDataHolder;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.EventList;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.EventListNetwork;
import co.crossroadsapp.destiny.network.EventRelationshipHandlerNetwork;
import co.crossroadsapp.destiny.network.LogoutNetwork;
import co.crossroadsapp.destiny.network.ResendBungieVerification;
import co.crossroadsapp.destiny.utils.CircularImageView;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.SendBackpressBroadcast;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 4/8/16.
 */
public class ListActivityFragment extends AppCompatActivity implements Observer {

    UserData user;
    ControlManager mManager;
    private EventList eList;
    private ArrayList<EventData> eData;
    private ArrayList<EventData> fragmentCurrentEventList;
    private ArrayList<EventData> fragmentupcomingEventList;
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    private ImageView userProfile;
    private CircularImageView userProfileDrawer;
    private TextView userNameDrawer;
    private DrawerLayout drawerLayout;
    private TextView logout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout notiBar;
    private TextView notiEventText;
    private TextView notiTopText;
    private TextView notiMessage;
    private ImageView notif_close;
    private RelativeLayout errLayout;
    private TextView errText;
    private ImageView close_err;
    private TextView createNewEventBtn;
    private TextView crash_report;
    private TextView showVersion;
    private RelativeLayout progress;
    private ValueEventListener listener;
    private TextView verify_logout;

    private Firebase refFirebase;

    private EventData pushEventObject;

    private RelativeLayout unverifiedUserScreen;
    private TextView verify_username;
    private TextView verify_user_bottom_text;
    protected ImageView appIcon;

    private GroupDrawerAdapter gpAct;
    private TextView changePassword;
    private ValueEventListener userListener;
    private Firebase refUFirebase;
    private ImageView grpIcon;

    private WebView legalView;
    private TextView termsService;
    private TextView privacy;
    private TextView license;
    private TextView sendMsgAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        //hideStatusBar();
        setTRansparentStatusBar();

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        Bundle b = getIntent().getExtras();
        //user = b.getParcelable("userdata");
        if(mManager.getUserData()!=null) {
            user = mManager.getUserData();
        } else {
            user = b.getParcelable("userdata");
        }

        if(b.containsKey("eventIntent")){
            Intent localPushEventObj = (Intent)b.get("eventIntent");
            if(localPushEventObj.hasExtra("message")) {
                String payload = localPushEventObj.getStringExtra("message");
                createPushEventObj(payload);
            }
            //Util.clearNotification(localPushEventObj.getExtras());
            this.getIntent().removeExtra("eventIntent");
        }

        mManager.postGetActivityList(this);

        // get existing event list
        getExistingList();

        mManager.getEventList(this);

        mManager.getGroupList(this);

        Firebase.setAndroidContext(this);

        //register event listener
        registerFirbase();

//        //register user listener
//        registerUserFirebase();

        unverifiedUserScreen = (RelativeLayout) findViewById(R.id.verify_fail);

        sendMsgAgain = (TextView) findViewById(R.id.send_msg_again);

        sendMsgAgain.setText(Html.fromHtml(getResources().getString(R.string.message_missing_tryagain)));

        //check user erification
        checkUserPSNVerification();

        progress = (RelativeLayout) findViewById(R.id.progress_bar_layout);
        userProfile = (ImageView) findViewById(R.id.userProfile);
        userProfileDrawer = (CircularImageView) findViewById(R.id.profile_avatar);
        userNameDrawer = (TextView) findViewById(R.id.profile_name);

        legalView = (WebView) findViewById(R.id.legal_web);

        appIcon = (ImageView) findViewById(R.id.badge_icon);

        if (user.getImageUrl()!=null) {
            Util.picassoLoadIcon(this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.avatar);
            Util.picassoLoadIcon(this, userProfileDrawer, user.getImageUrl(), R.dimen.player_profile_drawer_hgt, R.dimen.player_profile_drawer_width, R.drawable.avatar);
        }

        if (user.getUser()!=null){
            userNameDrawer.setText(user.getUser());
        }

        fragmentCurrentEventList = new ArrayList<EventData>();
        fragmentupcomingEventList = new ArrayList<EventData>();

        drawerLayout = (DrawerLayout) findViewById(R.id.out);

        logout = (TextView) findViewById(R.id.logout_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, 0, 0){//new ActionBarDrawerToggle(this, null,
            //null, 0, 0) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                changeProfileUsername("", false);
//                startCouchMarks(Constants.HOME_INTRODUCTION);
            }

            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    if (!drawerLayout.isDrawerOpen(Gravity.RIGHT)) {

                    }
                } else if (newState == DrawerLayout.STATE_SETTLING) {

                } else if (newState == DrawerLayout.STATE_IDLE) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.bringToFront();
            }
        };

        // Set the drawer toggle as the DrawerListener
        this.drawerLayout.setDrawerListener(mDrawerToggle);

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileDrawer(Gravity.LEFT);
            }
        });

        appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileDrawer(Gravity.RIGHT);
            }
        });

        notiBar = (RelativeLayout) findViewById(R.id.notification_bar);
        notiEventText = (TextView) findViewById(R.id.noti_text);
        notiTopText = (TextView) findViewById(R.id.noti_toptext);
        notiMessage = (TextView) findViewById(R.id.noti_subtext);
        notiMessage.setMovementMethod(new ScrollingMovementMethod());
        notif_close = (ImageView) findViewById(R.id.noti_close);
        notif_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notiBar.setVisibility(View.GONE);
            }
        });

        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errText = (TextView) findViewById(R.id.error_sub);
        close_err = (ImageView) findViewById(R.id.err_close);

        close_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errLayout.setVisibility(View.GONE);
            }
        });

        verify_logout = (TextView) findViewById(R.id.verify_logout);

        verify_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        sendMsgAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideProgress();
                showProgress();
                mManager.resendBungieMsg(ListActivityFragment.this);
            }
        });

        createNewEventBtn = (TextView) findViewById(R.id.create_new_event);

        createNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to create new event page
                Intent regIntent = new Intent(getApplicationContext(),
                        CreateNewEvent.class);
                regIntent.putExtra("userdata", user);
                startActivity(regIntent);
                finish();
            }
        });

        changePassword = (TextView) findViewById(R.id.reset);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to create new event page
                Intent regIntent = new Intent(getApplicationContext(),
                        ChangePassword.class);
                startActivity(regIntent);
            }
        });
        crash_report = (TextView) findViewById(R.id.crash_btn);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), ListActivityFragment.this);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

        showVersion = (TextView) findViewById(R.id.build_version);
        if(Util.getApplicationVersionCode(ListActivityFragment.this)!=null){
            showVersion.setText(Util.getApplicationVersionCode(ListActivityFragment.this));
        }

        termsService = (TextView) findViewById(R.id.legal1);
        privacy = (TextView) findViewById(R.id.legal2);
        license = (TextView) findViewById(R.id.legal3);

        termsService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLegalWebView(Constants.TERMS_OF_SERVICE);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLegalWebView(Constants.PRIVACY_POLICY);
            }
        });

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLegalWebView(Constants.LICENCE);
            }
        });

//        showVersion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(legalView!=null) {
//                    closeProfileDrawer(Gravity.LEFT);
//                    legalView.setVisibility(View.VISIBLE);
//                    legalView.loadUrl(Constants.LEGAL);
//                }
//            }
//        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                //hack to update upcoming fragment
//                if(position==1){
//                    updateCurrentFrag();
//                    //getSupportFragmentManager().getFragments().get(position).
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        crash_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(getApplicationContext(),
                        CrashReport.class);
                regIntent.putExtra("userdata", user);
                startActivity(regIntent);
            }
        });

        gpAct = new GroupDrawerAdapter(this);

        checkClanSet();

        //setGroupImageUrl();

    }

    private void showLogoutDialog() {
        // alert dailogue
        new AlertDialog.Builder(ListActivityFragment.this)
                .setMessage(getResources().getString(R.string.logout_popup_msg))
                .setPositiveButton(getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        closeProfileDrawer(Gravity.LEFT);
                        showProgress();
                        // continue with delete
                        RequestParams rp = new RequestParams();
                        if (user.getUser() != null) {
                            rp.put("userName", user.getUser());
                        }
                        mManager.postLogout(ListActivityFragment.this, rp);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void showLegalWebView(final String service) {
                if(legalView!=null) {
                    closeProfileDrawer(Gravity.LEFT);
                    legalView.setVisibility(View.VISIBLE);
                    legalView.setWebViewClient(new WebViewClient());
                    legalView.loadUrl(service);
                }
    }

    private void checkUserPSNVerification() {
        if (user.getPsnVerify()!=null) {
            if (!user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                //register user listener
                registerUserFirebase();
                verify_username = (TextView) findViewById(R.id.top_text);
                verify_user_bottom_text = (TextView) findViewById(R.id.verify_bottom_text);
                verify_user_bottom_text.setText(Html.fromHtml((getString(R.string.verify_accnt_bottomtext))));
                verify_user_bottom_text.setMovementMethod(LinkMovementMethod.getInstance());
                verify_username.setText("Welcome " + user.getUser() + "!");
                unverifiedUserScreen.setVisibility(View.VISIBLE);
            } else {
                if (unverifiedUserScreen!=null) {
                    unverifiedUserScreen.setVisibility(View.GONE);
                    checkClanSet();
                }
                unregisterUserFirebase();
            }
        }
    }

    private void checkClanSet() {
        if (user != null) {
            if(user.getPsnVerify()!=null) {
                if (user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                    if (user.getClanId() != null) {
                        if (user.getAuthenticationId() == Constants.REGISTER) {
                            openProfileDrawer(Gravity.RIGHT);
                        } else {
                            setGroupImageUrl();
                        }
                    }
                }
            }
        }
    }

    private void setGroupImageUrl() {
        GroupData gd = mManager.getGroupObj(user.getClanId());
        if(gd!=null) {
            String imageUrl = gd.getGroupImageUrl();
            if(imageUrl!=null) {
                setgrpIcon(imageUrl);
            }
        }
    }

    private void getExistingList() {
        if (mManager!=null) {
            if (mManager.getEventListCurrent()!=null && (!mManager.getEventListCurrent().isEmpty())) {
                createUpcomingCurrentList(mManager.getEventListCurrent());
            }
        }
    }

    private void registerFirbase() {
        setupEventListener();
        if(user!=null && user.getClanId()!=null) {
            refFirebase = new Firebase(Util.getFirebaseUrl(this.user.getClanId(), Constants.EVENT_CHANNEL));
            if (listener != null) {
                refFirebase.addValueEventListener(listener);
            }
        }
    }

    private void unregisterFirebase() {
        if(listener!=null) {
            refFirebase.removeEventListener(listener);
            refFirebase.removeValue();
        }
    }

    private void registerUserFirebase() {
        if(user.getUserId()!=null) {
            setupUserListener();
            refUFirebase = new Firebase(Util.getFirebaseUrl(this.user.getUserId(), Constants.USER_CHANNEL));
            if (userListener != null) {
                refUFirebase.addValueEventListener(userListener);
            }
        }
    }

    private void unregisterUserFirebase() {
        if(userListener!=null) {
            if(refUFirebase!=null) {
                refUFirebase.removeEventListener(userListener);
                refUFirebase.removeValue();
            }
        }
    }

    private void setupUserListener() {
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot!=null) {
                    UserData ud = new UserData();
                    String psnV = null;
                    if (snapshot.hasChild("value")) {
                            JSONObject userObj = new JSONObject ((HashMap)snapshot.getValue());
                            ud.toJson(userObj);
                            if(ud.getPsnVerify()!=null) {
                                psnV = ud.getPsnVerify();
                            }

                            if (psnV != null && psnV.equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                                if (mManager != null && mManager.getUserData() != null) {
                                    UserData u = mManager.getUserData();
                                    u.setPsnVerify(psnV);
                                }
                                checkUserPSNVerification();
                            }
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
    }

    private void setupEventListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mManager.getEventList(ListActivityFragment.this);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(pushEventObject==null) {
            if (this.getIntent().hasExtra("eventIntent")) {
                Intent cIntent = (Intent) this.getIntent().getExtras().get("eventIntent");
                String contentIntent = cIntent.getExtras().get("message").toString();
                createPushEventObj(contentIntent);
            }
        }
    }

    public void showProgress() {
        if(progress!=null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if(progress!=null) {
            progress.setVisibility(View.GONE);
        }
    }

    private void createPushEventObj(String payload) {
        pushEventObject = new EventData();
        try {
            JSONObject jsonObj = new JSONObject(payload);

            if(jsonObj.has("eventId")) {
                String id = (String) jsonObj.get("eventId");
                if(mManager!=null) {
                    if(mManager.getEventObj(id)!=null) {
                        pushEventObject = mManager.getEventObj(id);
                    } else {
                        RequestParams param = new RequestParams();
                        param.add("id", id);
                        mManager.postEventById(this, param);
                    }
                }
            } else {
                pushEventObject.toJson(jsonObj);
            }
//            if(jsonObj.has("event")) {
//                pushEventObject.toJson((JSONObject) jsonObj.get("event"));
//            } else {
//                pushEventObject.toJson(jsonObj);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void launchPushEventDetail() {
        if(pushEventObject!=null) {
            if (pushEventObject.getActivityData() != null) {
                if (pushEventObject.getActivityData().getActivitySubtype() != null) {
                    CurrentEventDataHolder ins = CurrentEventDataHolder.getInstance();
                    ins.setData(pushEventObject);
                    //launch eventdetailactivity
                    //start new activity for event
                    Intent regIntent = new Intent(this,
                            EventDetailActivity.class);
                    regIntent.putExtra("userdata", user);
                    startActivity(regIntent);
                }
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

    private void openProfileDrawer(int gravity) {
        if(gravity==Gravity.RIGHT && gpAct!=null) {
            gpAct.setSelectedGroup();
        }
        mManager.getGroupList(this);
        this.drawerLayout.openDrawer(gravity);
    }

    private void closeProfileDrawer(int gravity) {
        if(this.drawerLayout.isDrawerOpen(gravity)) {
            this.drawerLayout.closeDrawer(gravity);
        }
    }

    protected void closeAllDrawers(){
        if(drawerLayout!=null) {
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                closeProfileDrawer(Gravity.LEFT);
            } else if(drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                closeProfileDrawer(Gravity.RIGHT);
            }
        }
    }

    public void showError(String err) {
        hideProgress();
        closeProfileDrawer(Gravity.RIGHT);
        closeProfileDrawer(Gravity.LEFT);

        //show timed error message
        Util.showErrorMsg(errLayout, errText, err);

//        errLayout.setVisibility(View.GONE);
//        errLayout.setVisibility(View.VISIBLE);
//        errText.setText(err);
//        //timer for error msg visibility
//        errLayout.postDelayed(new Runnable() {
//            public void run() {
//                if(errLayout!=null) {
//                    errLayout.setVisibility(View.GONE);
//                }
//            }
//        }, 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        //get latest event list
        if (mManager!= null) {
            mManager.setCurrentActivity(this);
            getExistingList();
            mManager.getEventList(this);
        }

        //registerFirbase
        if (user!=null && user.getPsnVerify()!=null) {
            if (!user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                //register user listener
                registerUserFirebase();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //unregisterFirebase();
        unregisterUserFirebase();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromService, new IntentFilter("subtype_flag"));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterFirebase();
        //unregisterUserFirebase();
        unregisterReceiver(ReceivefromService);
    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mManager.getCurrentActivity() == ListActivityFragment.this) {
                String subtype = intent.getStringExtra("subtype");
                boolean playerMsg = intent.getBooleanExtra("playerMessage", false);
                String msg = intent.getStringExtra("message");
                notiEventText.setText(subtype);
                if (playerMsg) {
                    notiTopText.setText("FIRETEAM MESSAGE");
                    notiMessage.setVisibility(View.VISIBLE);
                    notiMessage.setText(msg);
                } else {
                    notiEventText.setText(msg);
                    notiTopText.setText(subtype.toUpperCase());
                    notiMessage.setVisibility(View.GONE);
                    mManager.getEventList(ListActivityFragment.this);
                }
                notiBar.setVisibility(View.VISIBLE);
                //put timer to make the notification message gone after 5 seconds
//                notiBar.postDelayed(new Runnable() {
//                    public void run() {
//                        if(notiBar!=null) {
//                            notiBar.setVisibility(View.GONE);
//                        }
//                    }
//                }, 7000);
            }
        }
    };

    public void setgrpIcon(String groupImageUrl) {
        if(appIcon!=null) {
            if(groupImageUrl!=null) {
                Util.picassoLoadIcon(this, appIcon, groupImageUrl, R.dimen.group_icon_list_hgt, R.dimen.group_icon_list_width, R.drawable.img_logo_badge_group);
                appIcon.invalidate();
            }
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "CURRENT", "UPCOMING" };
        Context context;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new BlankFragment(ListActivityFragment.this, fragmentCurrentEventList);
                case 1:
                    return new BlankFragment(ListActivityFragment.this, fragmentupcomingEventList);
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            //registeredFragments.put(position, fragment);
            return fragment;
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(ListActivityFragment.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setTypeface(Typeface.SANS_SERIF, 1);
            tv.setPadding(Util.dpToPx(43, ListActivityFragment.this), 25, 0, 0);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

    private void updateCurrentFrag() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 0);
        if(page!=null) {
            ((BlankFragment) page).updateCurrListAdapter(fragmentCurrentEventList);
        }

        page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 1);
        if(page!=null){
            ((BlankFragment)page).updateCurrListAdapter(fragmentupcomingEventList);
        }

        //
//        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
//        // based on the current position you can then cast the page to the correct
//        // class and call the method:
//        if (viewPager.getCurrentItem() == 0 && page != null) {
//            ((BlankFragment)page).updateCurrListAdapter(fragmentCurrentEventList);
//        }else if (viewPager.getCurrentItem() == 1 && page != null) {
//            ((BlankFragment)page).updateCurrListAdapter(fragmentupcomingEventList);
//        }
    }

    @Override
    public void update(Observable observable, Object data) {
            if (observable instanceof EventListNetwork) {
                if (data != null) {
                    eList = (EventList) data;
                    if (eData != null) {
                        eData.clear();
                    }
                    eData = eList.getEventList();
                    createUpcomingCurrentList(eData);
                    //Checking if current launch happened due to push notification click
                    if (pushEventObject != null) {
                        launchPushEventDetail();
                        pushEventObject = null;
                    }
                    updateCurrentFrag();
                }
            } else if (observable instanceof EventRelationshipHandlerNetwork) {
                if (data != null) {
                    EventData ed = new EventData();
                    ed = (EventData) data;
                    if (this.eData != null) {
                        for (int i = 0; i < this.eData.size(); i++) {
                            if (ed.getEventId().equalsIgnoreCase(this.eData.get(i).getEventId())) {
                                if (ed.getMaxPlayer() > 0) {
                                    this.eData.remove(i);
                                    this.eData.add(i, ed);
                                } else {
                                    this.eData.remove(i);
                                }
                                createUpcomingCurrentList(eData);
                                updateCurrentFrag();
                                hideProgress();
                                break;
                            }
                        }
                    }
                    //to update all lists in the background
                    mManager.getEventList(ListActivityFragment.this);
                }
            } else if(observable instanceof GroupListNetwork) {
                if (data != null) {
                    if (data instanceof UserData) {
                        mManager.getEventList(this);
                        hideProgress();
                        closeProfileDrawer(Gravity.RIGHT);
                        mManager.getGroupList(this);
                        //gpAct.setSelectedGroup();
                    } else {
                        //setGroupImageUrl();
                        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                            gpAct.update(data);
                        }
                    }
                }
            } else if(observable instanceof ResendBungieVerification) {
                hideProgress();
                Toast.makeText(this, "Verification Message Sent to Your Bungie.net Account",
                        Toast.LENGTH_LONG).show();
            } else if(observable instanceof EventByIdNetwork) {
                if(data!=null) {
                    pushEventObject = new EventData();
                    pushEventObject = (EventData) data;
                    launchPushEventDetail();
                    pushEventObject = null;
                }
            } else if(observable instanceof LogoutNetwork) {
                Intent regIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(regIntent);
                finish();
            }
    }

    private void createUpcomingCurrentList(ArrayList<EventData> currentEventList) {
        if (currentEventList!=null){
            fragmentCurrentEventList = new ArrayList<EventData>();
            fragmentupcomingEventList = new ArrayList<EventData>();
            if (currentEventList.size()>0) {
                for (int i = 0; i < currentEventList.size(); i++) {
                    if (currentEventList.get(i).getLaunchEventStatus() != null) {
                        if (currentEventList.get(i).getLaunchEventStatus().equalsIgnoreCase(Constants.LAUNCH_STATUS_UPCOMING)) {
                            fragmentupcomingEventList.add(currentEventList.get(i));
                        } else {
                            fragmentCurrentEventList.add(currentEventList.get(i));
                        }
                    }
                }
            }
        }
    }

    private boolean checkDrawerOpen() {
        if(drawerLayout!=null) {
            if(drawerLayout.isDrawerOpen(Gravity.RIGHT) || drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(legalView.getVisibility()==View.VISIBLE) {
            legalView.setVisibility(View.GONE);
            openProfileDrawer(Gravity.LEFT);
        } else if(checkDrawerOpen()){
            closeAllDrawers();
        }else {
            super.onBackPressed();
            Intent in = new Intent(this, SendBackpressBroadcast.class);
            this.startService(in);
            finish();
        }
    }
}
