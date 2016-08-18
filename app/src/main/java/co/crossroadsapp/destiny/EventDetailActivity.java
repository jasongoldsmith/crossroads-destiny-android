package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.crossroadsapp.destiny.data.CurrentEventDataHolder;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.GroupData;
import co.crossroadsapp.destiny.data.PlayerData;
import co.crossroadsapp.destiny.data.PushNotification;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.AddCommentNetwork;
import co.crossroadsapp.destiny.network.EventByIdNetwork;
import co.crossroadsapp.destiny.network.EventRelationshipHandlerNetwork;
import co.crossroadsapp.destiny.network.EventSendMessageNetwork;
import co.crossroadsapp.destiny.utils.CircularImageView;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.TravellerLog;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 3/29/16.
 */
public class EventDetailActivity extends BaseActivity implements Observer {

    EventData currEvent;
    private TextView eventName;
    private TextView eventSubName;
    private TextView eventSubNameLF;
    private ImageView eventProfileImg;
    private TextView eventLight;
    private UserData user;
    private ImageView back;
    View view;

    private RecyclerView mRecyclerView;
    private CurrentEventsViewAdapter mAdapter;
    private CircularImageView userProfile;
    private ControlManager controlManager;
    private CurrentEventDataHolder inst;
    private RelativeLayout sendMessageLayout;
    private RelativeLayout sendmsg_bckgrnd;

    private EditText editText;
    private ImageView sendBtn;
    private TextView joinBtn;
    private TextView leaveBtn;
    private TextView msgallBtn;

    private RelativeLayout notiBar;
    private TextView notiMessage;
    private TextView notiTopText;
    private TextView notiEventText;

    private TextView editMsgPlayer;
    private Handler _handler;
    private boolean joinBtnActive;
    private TextView eventDetailDate;
    private boolean userIsPlayer;
    private ImageView notif_close;
    private RelativeLayout progressBar;
    private TextView eventCheckpoint;
    private TextView mCharacter;
    private RelativeLayout bottomBtnLayout;
    private ValueEventListener listener;
    private Firebase refFirebase;
    private ImageView share;

    private int reqPlayer;
    private BranchUniversalObject branchUniversalObject;
    private String eventId;
    private SwipeFrameLayout cardStackLayout;
    private String upcomingDate;
    private ImageView background;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private RelativeLayout commentLayout;
    private EditText sendMsg;
    private ImageView sendMsgBtn;
//    private TextView errText;
//    private ImageView close_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_list);

        Bundle b = getIntent().getExtras();
//        user = b.getParcelable("userdata");

        setTRansparentStatusBar();

        joinBtn = (TextView) findViewById(R.id.join_btn);
        leaveBtn = (TextView) findViewById(R.id.leave_btn);
        msgallBtn = (TextView) findViewById(R.id.messageall_btn);
        bottomBtnLayout = (RelativeLayout) findViewById(R.id.detail_bottom_btn);

        background = (ImageView) findViewById(R.id.background);

        progressBar = (RelativeLayout) findViewById(R.id.progress_bar_eventdetail_layout);

        eventCheckpoint = (TextView) findViewById(R.id.eventDetailCheckpoint);

        //setTRansparentStatusBar();

        controlManager = ControlManager.getmInstance();

        controlManager.setCurrentActivity(EventDetailActivity.this);

        user = controlManager.getUserData();

        _handler = new Handler();

        //userProfile = (CircularImageView) findViewById(R.id.userProfile_event);

        currEvent = new EventData();

        inst = CurrentEventDataHolder.getInstance();
        currEvent = inst.getData();
        //joinBtnActive = inst.getJoinVisible();

        TextView tagText = (TextView) findViewById(R.id.event_tag_text);

        if(currEvent!=null && currEvent.getActivityData().getTag()!=null && !currEvent.getActivityData().getTag().isEmpty()) {
            tagText.setText(currEvent.getActivityData().getTag());
            Util.roundCorner(tagText, EventDetailActivity.this);
        }

        //load background image
        Util.picassoLoadImageWithoutMeasurement(getApplicationContext(), background, currEvent.getActivityData().getaImagePath(), R.drawable.img_b_g_d_e_f_a_u_l_t);

        eventProfileImg = (ImageView) findViewById(R.id.event_detail_icon);
        eventName = (TextView) findViewById(R.id.activity_name_detail);
//        eventSubName = (TextView) findViewById(R.id.activity_player_name_detail);
//        eventSubNameLF = (TextView) findViewById(R.id.activity_player_name_lf_detail);
//        eventLight = (TextView) findViewById(R.id.activity_aLight_detail);
        back = (ImageView) findViewById(R.id.eventdetail_backbtn);
        share = (ImageView) findViewById(R.id.eventdetail_sharebtn);
        eventDetailDate = (TextView) findViewById(R.id.eventDetailDate);

        commentLayout = (RelativeLayout) findViewById(R.id.send_comment_layout);

        sendMsg = (EditText) findViewById(R.id.comment_edittext);
        sendMsg.addTextChangedListener(mTextEditorWatcher);
        sendMsgBtn = (ImageView) findViewById(R.id.send_msg_btn);

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgBtnClick();
            }
        });

        sendmsg_bckgrnd = (RelativeLayout) findViewById(R.id.sendmsg_background);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIt();
            }
        });

        editText = (EditText) findViewById(R.id.edittext);
        editText.addTextChangedListener(mTextEditorWatcher);

        mCharacter = (TextView) findViewById(R.id.character_count);

        sendBtn = (ImageView) findViewById(R.id.send_btn);

        cardStackLayout = (SwipeFrameLayout) findViewById(R.id.notification_bar_layout);

//        notiBar = (RelativeLayout) findViewById(R.id.notification_bar);
//        notiEventText = (TextView) findViewById(R.id.noti_text);
//
//        notiTopText = (TextView) findViewById(R.id.noti_toptext);
//        notiMessage = (TextView) findViewById(R.id.noti_subtext);
//        notiMessage.setMovementMethod(new ScrollingMovementMethod());
//        notif_close = (ImageView) findViewById(R.id.noti_close);
//        notif_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notiBar.setVisibility(View.GONE);
//            }
//        });

        if (currEvent.getActivityData().getActivityIconUrl() != null) {
            Util.picassoLoadIcon(EventDetailActivity.this, eventProfileImg, currEvent.getActivityData().getActivityIconUrl(), R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.icon_ghost_default);
        }

        if (currEvent.getActivityData().getActivitySubtype() != null) {
            eventName.setText(currEvent.getActivityData().getActivitySubtype());
        }

//        if (currEvent.getActivityData().getActivityLight() > 0) {
//            // unicode to show star
//            String st = "\u2726";
//            eventLight.setText(st + currEvent.getActivityData().getActivityLight());
//        } else if (currEvent.getActivityData().getActivityLevel() > 0) {
//            eventLight.setText("lvl " + currEvent.getActivityData().getActivityLevel());
//        }

        if (currEvent.getActivityData().getActivityCheckpoint() != null && (!currEvent.getActivityData().getActivityCheckpoint().equalsIgnoreCase("null"))) {
            eventCheckpoint.setVisibility(View.VISIBLE);
            eventCheckpoint.setText(currEvent.getActivityData().getActivityCheckpoint());
        }

//        if (user.getImageUrl() != null) {
//            Util.picassoLoadIcon(EventDetailActivity.this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.img_avatar_you);
//        }

        checkUserIsPlayer();
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
                hideProgress();
                showProgress();
                controlManager.postJoinEvent(EventDetailActivity.this, rp);
            }
        });

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
                hideProgress();
                showProgress();
                //todo fix when fire base rules are in
                if(currEvent.getPlayerData().size()==1){
                    unregisterFirebase();
                }
                controlManager.postUnJoinEvent(EventDetailActivity.this, rp);
            }
        });

        if (currEvent.getLaunchEventStatus().equalsIgnoreCase(Constants.LAUNCH_STATUS_UPCOMING)) {
            upcomingDate = Util.convertUTCtoReadable(currEvent.getLaunchDate());
            if (upcomingDate != null) {
                eventDetailDate.setText(upcomingDate);
            }
        } else {
            eventDetailDate.setText("");
        }

        setPlayerNames();

        userIsPlayer = checkUserIsPlayer();

        sendMessageLayout = (RelativeLayout) findViewById(R.id.send_message_layout);

        sendmsg_bckgrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg_bckgrnd.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

        editMsgPlayer = (TextView) findViewById(R.id.textmsg_player);

        //checkLeaveBtn();

//        mRecyclerView = (RecyclerView) findViewById(R.id.event_player);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        mAdapter = new CurrentEventsViewAdapter(currEvent.getPlayerData());
//
//        mRecyclerView.setAdapter(mAdapter);

        msgallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToAll();
            }
        });

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.eventdetail_viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), EventDetailActivity.this);
        viewPager.setAdapter(pagerAdapter);

        setBottomButtonSelection();

        //viewpager change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==1) {
                    changeBottomButtomForComments();
                } else if(position==0) {
                    setBottomButtonSelection();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.eventdetail_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        generateBranchObject();

//        String actName = currEvent.getActivityData().getActivitySubtype();
//        String grpName = controlManager.getGroupObj(currEvent.getClanId()).getGroupName();
//        String deepLinkTitle=" ";
//        String deepLinkMsg=" ";
//        if(userIsPlayer){
//            deepLinkTitle = "Join My Fireteam";
//            if(reqPlayer==0) {
//                deepLinkTitle = currEvent.getActivityData().getActivitySubtype();
//            }
//            deepLinkMsg = getDeepLinkConsoleType()+": I need "+reqPlayer+ " more for "+ actName + " in the " +grpName+ " group";
//        }else {
//            deepLinkTitle = "Searching for Guardians";
//            if(reqPlayer==0) {
//                deepLinkTitle = currEvent.getActivityData().getActivitySubtype();
//            }
//            deepLinkMsg = getDeepLinkConsoleType() +": This fireteam needs " + reqPlayer + " more for " + actName+" in the " + grpName + " group";
//
//            if(currEvent.getEventStatus().equalsIgnoreCase("Upcoming")) {
//                deepLinkMsg = getDeeplinkContent(upcomingDate);
//            }
//        }
//
//        if(reqPlayer==0){
//            if(currEvent.getEventStatus().equalsIgnoreCase("Upcoming")) {
//                deepLinkMsg = getDeepLinkConsoleType() +": Check out this " + actName + " on " + upcomingDate + " in the " + grpName + " group";
//            }else {
//                deepLinkMsg = getDeepLinkConsoleType() +": Check out this " + actName + " in the " + grpName + " group";
//            }
//        }
//
//        // Create a BranchUniversal object for the content referred on this activity instance
//        branchUniversalObject = new BranchUniversalObject()
//                .setCanonicalIdentifier("item/12345")
//                .setCanonicalUrl("https://branch.io/deepviews")
//                .setTitle(deepLinkTitle)
//                .setContentDescription(deepLinkMsg)
//                .setContentImageUrl(Constants.DEEP_LINK_IMAGE + currEvent.getEventId()+".png")
//                //.setContentExpiration(new Date(1476566432000L)) // set contents expiration time if applicable
//                .addContentMetadata("activityName", currEvent.getActivityData().getActivitySubtype())
//                .addContentMetadata("eventId", currEvent.getEventId());

        registerFirbase();

        showNotifications();

    }

    private void changeBottomButtomForComments() {
        if(userIsPlayer) {
            bottomBtnLayout.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);
            leaveBtn.setVisibility(View.GONE);
            msgallBtn.setVisibility(View.GONE);
            commentLayout.setVisibility(View.VISIBLE);
        } else {
            setBottomButtonSelection();
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "FIRETEAM", "COMMENTS" };
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
                    return EventDetailsFragments.newInstance(0, EventDetailActivity.this, currEvent);
                case 1:
                    return EventDetailsFragments.newInstance(1, EventDetailActivity.this, null);
                default:
                    return null;
            }
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
            View tab = LayoutInflater.from(EventDetailActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setTypeface(Typeface.SANS_SERIF, 1);
            tv.setPadding(Util.dpToPx(43, EventDetailActivity.this), 25, 0, 0);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

    private void updateCurrentFrag() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.eventdetail_viewpager + ":" + 0);
        if (page != null) {
            ((EventDetailsFragments) page).updateCurrListAdapter(currEvent, 0);
        }

        page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.eventdetail_viewpager + ":" + 1);
        if (page != null) {
            ((EventDetailsFragments) page).updateCurrListAdapter(currEvent, 1);
        }
    }

    private void generateBranchObject() {
        if (currEvent != null) {
            String actName = currEvent.getActivityData().getActivitySubtype();
            String grpName = controlManager.getGroupObj(currEvent.getClanId()).getGroupName();
            String deepLinkTitle = " ";
            String deepLinkMsg = " ";
            String console = getDeepLinkConsoleType();
            if (checkUserIsPlayer()) {
                deepLinkTitle = "Join My Fireteam";
                if (currEvent.getLaunchEventStatus().equalsIgnoreCase("upcoming")) {
                    deepLinkMsg = getDeepLinkConsoleType() + ": I need " + reqPlayer + " more for " + actName + " on " + upcomingDate + " in the " + grpName + " group";
                }else {
                    deepLinkMsg = getDeepLinkConsoleType() + ": I need " + reqPlayer + " more for " + actName + " in the " + grpName + " group";
                }
            } else {
                deepLinkTitle = "Searching for Guardians";
                if (currEvent.getLaunchEventStatus().equalsIgnoreCase("upcoming")) {
                    deepLinkMsg = console + ": This fireteam needs " + reqPlayer + " more for " + actName + " on " + upcomingDate + " in the " + grpName + " group";
                }else {
                    deepLinkMsg = console + ": This fireteam needs " + reqPlayer + " more for " + actName + " in the " + grpName + " group";
                }
            }

            if (reqPlayer == 0) {
                deepLinkTitle = currEvent.getActivityData().getActivitySubtype();
                if (currEvent.getLaunchEventStatus().equalsIgnoreCase("Upcoming")) {
                    deepLinkMsg = console + ": Check out this " + actName + " on " + upcomingDate + " in the " + grpName + " group";
                } else {
                    deepLinkMsg = console + ": Check out this " + actName + " in the " + grpName + " group";
                }
            }

            // Create a BranchUniversal object for the content referred on this activity instance
            branchUniversalObject = new BranchUniversalObject()
                    .setCanonicalIdentifier("item/12345")
                    .setCanonicalUrl("https://branch.io/deepviews")
                    .setTitle(deepLinkTitle)
                    .setContentDescription(deepLinkMsg)
                    .setContentImageUrl(Constants.DEEP_LINK_IMAGE + currEvent.getEventId() + ".png")
                    //.setContentExpiration(new Date(1476566432000L)) // set contents expiration time if applicable
                    .addContentMetadata("activityName", currEvent.getActivityData().getActivitySubtype())
                    .addContentMetadata("eventId", currEvent.getEventId());
        }
    }

    private String getDeepLinkConsoleType() {
        String s;
        switch (user.getConsoleType()) {
            case "XBOX360":
                s = "360";
                break;
            case "XBOXONE":
                s = "XB1";
                break;
            default:
                s = user.getConsoleType();
                break;
        }
        return s;
    }

    private void shareIt() {
//        //deeplink creation

        LinkProperties linkProperties = new LinkProperties();

        generateBranchObject();

        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    final Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    if(url!=null) {
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                        startActivity(Intent.createChooser(sharingIntent, "Share"));
                    }
                }
            }
        });
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            mCharacter.setText(String.valueOf(80 - s.toString().length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromService, new IntentFilter("subtype_flag"));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(ReceivefromService);
        unregisterFirebase();
    }

    private void registerFirbase() {
        setupEventListener();
        if(controlManager!=null) {
            if(controlManager.getUserData()!=null) {
                this.user = controlManager.getUserData();
            }
        }
        if(user!=null && user.getClanId()!=null) {
            if(currEvent!=null && currEvent.getEventId()!=null && currEvent.getClanId()!=null) {
                refFirebase = new Firebase(Util.getFirebaseUrl(currEvent.getClanId(), currEvent.getEventId(), Constants.EVENT_CHANNEL));
                if (listener != null) {
                    refFirebase.addValueEventListener(listener);
                }
            }
        }
    }

    public EventData getCurrentEventData() {
        if(currEvent!=null) {
            return currEvent;
        }
        return null;
    }

    private void setupEventListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(currEvent!=null && currEvent.getEventId()!=null) {
                    String id = currEvent.getEventId();
                    RequestParams param = new RequestParams();
                    param.add("id", id);
                    controlManager.postEventById(EventDetailActivity.this, param);
                }
//                if(snapshot.exists()) {
//                            if(currEvent!=null && currEvent.getEventId()!=null) {
//                                String id = currEvent.getEventId();
//                                RequestParams param = new RequestParams();
//                                param.add("id", id);
//                                controlManager.postEventById(EventDetailActivity.this, param);
//                            }
//                } else {
//                    launchListActivityAndFinish();
//                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
    }

    private void unregisterFirebase() {
        if(listener!=null) {
            refFirebase.removeEventListener(listener);
        }
    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String subtype = intent.getStringExtra("subtype");
//            boolean playerMsg = intent.getBooleanExtra("playerMessage", false);
//            String msg = intent.getStringExtra("message");
//            notiEventText.setText(subtype);
//            if(playerMsg){
//                notiTopText.setText("FIRETEAM MESSAGE");
//                notiMessage.setVisibility(View.VISIBLE);
//                notiMessage.setText(msg);
//            }else {
//                notiEventText.setText("Your Fireteam is ready!");
//                notiTopText.setText(subtype);
//                notiMessage.setVisibility(View.GONE);
//                //mManager.getEventList(ListActivityFragment.this);
//            }
//            notiBar.setVisibility(View.VISIBLE);
        }
    };

    private void setBottomButtonSelection() {
        int p=5;
        if(viewPager!=null) {
            p = viewPager.getCurrentItem();
        }
        if(p==1) {
            if(checkUserIsPlayer()) {
                bottomBtnLayout.setVisibility(View.VISIBLE);
                joinBtn.setVisibility(View.GONE);
                leaveBtn.setVisibility(View.GONE);
                msgallBtn.setVisibility(View.GONE);
                commentLayout.setVisibility(View.VISIBLE);
            }else {
                setBottomBtn();
            }
        }else {
            setBottomBtn();
        }
    }

    private void setBottomBtn() {
        commentLayout.setVisibility(View.GONE);
        if (checkUserIsCreator()) {
            if ((this.currEvent.getPlayerData() != null) && this.currEvent.getPlayerData().size() > 1) {
                bottomBtnLayout.setVisibility(View.VISIBLE);
                leaveBtn.setVisibility(View.GONE);
                joinBtn.setVisibility(View.GONE);
                msgallBtn.setVisibility(View.VISIBLE);
            } else {
                bottomBtnLayout.setVisibility(View.VISIBLE);
                leaveBtn.setVisibility(View.VISIBLE);
                joinBtn.setVisibility(View.GONE);
                msgallBtn.setVisibility(View.GONE);
            }
        } else if (checkUserIsPlayer()) {
            bottomBtnLayout.setVisibility(View.VISIBLE);
            leaveBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);
            msgallBtn.setVisibility(View.GONE);
        } else if (!currEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL)) {
            bottomBtnLayout.setVisibility(View.VISIBLE);
            leaveBtn.setVisibility(View.GONE);
            joinBtn.setVisibility(View.VISIBLE);
            msgallBtn.setVisibility(View.GONE);
        }
    }

    private void sendMsgToAll() {
        if (sendmsg_bckgrnd != null) {
            sendmsg_bckgrnd.setVisibility(View.VISIBLE);
            editMsgPlayer.setText("All Players");
            showKeyboard();
        }

        if (sendBtn != null) {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                String msg = null;

                @Override
                public void onClick(View v) {
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < currEvent.getPlayerData().size(); i++) {
                                if (i == 0) {
                                    msg = getEditText();
                                }
                                if (!user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())) {
                                    //v.setEnabled(false);
                                    if (msg != null) {
                                        sendMessage(currEvent.getPlayerData().get(i).getPlayerId(), msg);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void showProgress() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setPlayerNames() {
        String allNames = "";
        String allNamesRem = "";
        reqPlayer = currEvent.getActivityData().getMaxPlayer() - currEvent.getPlayerData().size();

        allNames = currEvent.getCreatorData().getPsnId();
        if (!currEvent.getEventStatus().equalsIgnoreCase("full")) {
            allNamesRem = " " + "LF" +reqPlayer+"M";
        }

//        eventSubName.setText(allNames);
//        eventSubNameLF.setText(allNamesRem);
    }

    @Override
    public void update(Observable observable, Object data) {
        hideProgress();
        hideProgressBar();
            if (observable instanceof EventRelationshipHandlerNetwork || observable instanceof EventByIdNetwork || observable instanceof AddCommentNetwork) {
                this.currEvent = (EventData) data;
                if (currEvent != null) {
                    if ((currEvent.getPlayerData() != null) && (currEvent.getPlayerData().size()>0)) {
                        updateCurrentFrag();
                        if (mAdapter != null) {
                            mAdapter.playerLocal.clear();
                            mAdapter.addItem(currEvent.getPlayerData());
                            mAdapter.notifyDataSetChanged();
                        }
                        setPlayerNames();
                        setBottomButtonSelection();
                        generateBranchObject();
                    } else {
                        launchListActivityAndFinish();
                    }
                }
            } else if (observable instanceof EventSendMessageNetwork) {
                editText.setText("");
                hideSendMsgBckground();
            }
    }

    private void hideSendMsgBckground() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        sendmsg_bckgrnd.setVisibility(View.GONE);
        hideKeyboard();
    }

    private boolean checkUserIsPlayer(){
        if(this.currEvent.getPlayerData()!=null) {
            for (int i = 0; i < currEvent.getPlayerData().size(); i++) {
                if (user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())) {
                    return true;
                }
            }
        }
        return false;
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

    private boolean checkUserIsCreator() {
        if(this.currEvent.getPlayerData()!=null) {
            if (user.getUserId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId())) {
                return true;
            }
        }
        return false;
    }

    public void showError(String err) {
        hideProgress();
//        errLayout.setVisibility(View.VISIBLE);
//        errText.setText(err);
        setErrText(err);
    }

    public String getGroupName() {
        if(user!=null && user.getClanId()!=null) {
            if(controlManager!=null) {
                GroupData grp = controlManager.getGroupObj(user.getClanId());
                if(grp!=null && grp.getGroupName()!=null) {
                    return grp.getGroupName();
                }
            }
        }
        return null;
    }

    private class CurrentEventsViewAdapter extends RecyclerView.Adapter<CurrentEventsViewAdapter.CurrentEventsViewHolder> {

        private ArrayList<PlayerData> playerLocal;

        protected void addItem(ArrayList<PlayerData> a) {
            this.playerLocal.addAll(a);
        }

        public CurrentEventsViewAdapter(ArrayList<PlayerData> playerData) {
            playerLocal = new ArrayList<PlayerData>();
            playerLocal = playerData;
        }


        public class CurrentEventsViewHolder extends RecyclerView.ViewHolder {
            private CircularImageView playerProfile;
            private TextView playerName;
            private CardView playerCard;
            private ImageView message;

            public CurrentEventsViewHolder(View itemView) {
                super(itemView);
                view = itemView;

                playerProfile = (CircularImageView) itemView.findViewById(R.id.event_detail_player_profile);
                playerName = (TextView) itemView.findViewById(R.id.player_name);
                playerCard = (CardView) itemView.findViewById(R.id.activity_card);
                message = (ImageView) itemView.findViewById(R.id.event_detail_message);
            }
        }

        @Override
        public CurrentEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_eventdetail_playercard, null);
            return new CurrentEventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CurrentEventsViewHolder holder, final int position) {
            String currPlayerId = null;
            if(playerLocal!=null) {
                if (position >= playerLocal.size() && getMaxPlayer() > playerLocal.size() ) {
                    holder.playerName.setText("searching...");
                    holder.message.setVisibility(View.GONE);
                    holder.playerProfile.setImageResource(R.drawable.img_profile_blank);
                } else {
                    if (playerLocal.get(position) != null) {
                        if (playerLocal.get(position).getPlayerId() != null) {
                            currPlayerId = playerLocal.get(position).getPlayerId();
                        }
                        if (playerLocal.get(position).getPlayerImageUrl() != null) {
                            Util.picassoLoadIcon(EventDetailActivity.this, holder.playerProfile, playerLocal.get(position).getPlayerImageUrl(),
                                    R.dimen.eventdetail_player_profile_hgt, R.dimen.eventdetail_player_profile_width, R.drawable.img_profile_blank);
                        }
                        if (playerLocal.get(position).getPsnId() != null) {
                            holder.playerName.setText(playerLocal.get(position).getPsnId());
                        }

                        holder.message.setVisibility(View.GONE);

                        if (checkUserIsCreator() && (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))) {
                            holder.message.setVisibility(View.VISIBLE);
                        }
                        if ((checkUserIsPlayer() && !checkUserIsCreator()) && (playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))) {
                            holder.message.setVisibility(View.VISIBLE);
                        }

                        final String finalCurrPlayerId = currPlayerId;
                        holder.message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (sendmsg_bckgrnd != null) {
                                    sendmsg_bckgrnd.setVisibility(View.VISIBLE);
                                    editMsgPlayer.setText(playerLocal.get(position).getPsnId());
                                    showKeyboard();
                                }

                                if (sendBtn != null) {
                                    sendBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //v.setEnabled(false);
                                            String s = getEditText();
                                            if (s != null) {
                                                sendMessage(finalCurrPlayerId, s);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        holder.playerProfile.invalidate();
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return getMaxPlayer();
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

    }

    private int getMaxPlayer() {
        if(currEvent!=null){
            if(currEvent.getActivityData()!=null){
                if(currEvent.getActivityData().getMaxPlayer()>0){
                    return currEvent.getActivityData().getMaxPlayer();
                }
            }
        }
        return 0;
    }

    private void showKeyboard() {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard() {
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private String getEditText() {
        if (editText != null) {
            if (editText.getText() != null) {
                if (editText.getText().toString().length() > 0) {
                    return editText.getText().toString();
                }
            }
        }
        return null;
    }

    private String getSendMsgEditText() {
        if (sendMsg != null) {
            if (sendMsg.getText() != null) {
                if (sendMsg.getText().toString().length() > 0) {
                    return sendMsg.getText().toString();
                }
            }
        }
        return null;
    }

    private void sendMsgBtnClick() {
        showProgressBar();
        if (currEvent != null && currEvent.getEventId() != null) {
            String msg = getSendMsgEditText();
            sendMsg.setText("");
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if(msg!=null) {
                RequestParams params = new RequestParams();
                params.put("eId", currEvent.getEventId());
                params.put("text", msg);
                controlManager.postComments(EventDetailActivity.this, params);
            }
        }
    }

    private void sendMessage(final String currentPlayerId, final String msg) {
        //final String pId = currentPlayerId;
//        if (editText != null) {
//            if (editText.getText() != null) {
//                if (editText.getText().toString().length() > 0) {
                    if (currEvent != null && currEvent.getEventId() != null) {
                        editText.setText("");
//                        _handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                                controlManager.postEventMessage(EventDetailActivity.this, msg, currentPlayerId, currEvent.getEventId());
                        hideSendMsgBckground();
//                            }
//                            //hideSendMsgBckground();
//                        }, 1000);
                    }
//                }
//            }
//        }
    }

    private void launchListActivityAndFinish() {
        Intent i=new Intent (this, ListActivityFragment.class);
        //i.putExtra("userdata", user);
        startActivity(i);
        currEvent = null;
        finish();
    }

    @Override
    public void onBackPressed() {
        if(sendmsg_bckgrnd.getVisibility()==View.VISIBLE){
            sendmsg_bckgrnd.setVisibility(View.GONE);
            hideKeyboard();
        } else {
            launchListActivityAndFinish();
        }
    }

    SwipeStackAdapter adapter;
    //View view;
    SwipeDeck cardStack;
    int n=0;

    public void showNotifications() {

        ArrayList<PushNotification> localNoti = new ArrayList<PushNotification>();
        eventNotiList = new ArrayList<PushNotification>();
        // filter event related message notification
        getEventNotification(currEvent);
        if(eventNotiList!=null && (!eventNotiList.isEmpty())) {
            //cardStackLayout.setVisibility(View.VISIBLE);
        }
        if(n==0) {
            if(eventNotiList!=null && (!eventNotiList.isEmpty())){
                localNoti.add(eventNotiList.get(eventNotiList.size()-1));
                n++;}
        }else {
            if(eventNotiList!=null && (!eventNotiList.isEmpty())) {
                localNoti = eventNotiList;
            }
        }


//        cardStack = null;
//        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
//        if(adapter != null) {
//            adapter = null;
//        }
//
//        adapter = new SwipeStackAdapter(eventNotiList, this);
//        cardStack.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

//        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
//            @Override
//            public void cardSwipedLeft(int position) {
//                checkAndRemoveNoti(EventDetailActivity.this, position, adapter);
//                //notiList.remove(position);
//
//            }
//
//            @Override
//            public void cardSwipedRight(int position) {
//                checkAndRemoveNoti(EventDetailActivity.this, position, adapter);
//            }
//
//            @Override
//            public void cardsDepleted() {
//                if(eventNotiList.isEmpty()) {
//                    cardStackLayout.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void cardActionDown() {
//
//            }
//
//            @Override
//            public void cardActionUp() {
//
//            }
//        });

    }
}
