package com.example.sharmha.travelerfordestiny;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.EventList;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.network.EventListNetwork;
import com.example.sharmha.travelerfordestiny.network.EventRelationshipHandlerNetwork;
import com.example.sharmha.travelerfordestiny.utils.CircularImageView;
import com.example.sharmha.travelerfordestiny.utils.SendBackpressBroadcast;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
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
    private ImageView logout;
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
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);


        setTRansparentStatusBar();
        //hideStatusBar();
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        mManager.getEventList(this);

        userProfile = (ImageView) findViewById(R.id.userProfile);
        userProfileDrawer = (CircularImageView) findViewById(R.id.profile_avatar);
        userNameDrawer = (TextView) findViewById(R.id.profile_name);

        if (user.getImageUrl()!=null) {
            Util.picassoLoadIcon(this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.avatar);
            Util.picassoLoadIcon(this, userProfileDrawer, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.avatar);
        }

        if (user.getUser()!=null){
            userNameDrawer.setText(user.getUser());
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.out);

        logout = (ImageView) findViewById(R.id.logout_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                if (user.getUser() != null) {
                    rp.put("userName", user.getUser());
                }
                mManager.postLogout(ListActivityFragment.this, rp);
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
            }
        };

        // Set the drawer toggle as the DrawerListener
        this.drawerLayout.setDrawerListener(mDrawerToggle);

        userProfile.setOnClickListener(new View.OnClickListener() {
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

        createNewEventBtn = (TextView) findViewById(R.id.create_new_event);

        createNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to create new event page
                Intent regIntent = new Intent(getApplicationContext(),
                        CreateNewEvent.class);
                regIntent.putExtra("userdata", user);
                startActivity(regIntent);
            }
        });

        crash_report = (TextView) findViewById(R.id.crash_btn);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter =
                new PagerAdapter(getSupportFragmentManager(), ListActivityFragment.this);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Refresh items
//                refreshItems();
//            }
//        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        crash_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(getApplicationContext(),
                        CrashReport.class);
                regIntent.putExtra("userdata", user);
                startActivity(regIntent);
            }
        });

    }

    private void setTRansparentStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void openProfileDrawer(int gravity) {
        this.drawerLayout.openDrawer(gravity);
    }

    private void closeProfileDrawer(int gravity) {
        this.drawerLayout.closeDrawer(gravity);
    }

    public void showError(String err) {
        errLayout.setVisibility(View.VISIBLE);
        errText.setText(err);
    }

    @Override
    public void onResume() {
        super.onResume();
        //get current updated list from manager
//        if (mManager!= null) {
//            if (mManager.getEventListCurrent() != null) {
//                mAdapter.elistLocal.clear();
//                mAdapter.addItem(mManager.getEventListCurrent());
//                mAdapter.notifyDataSetChanged();
//            }
//        }

        //get latest event list
        if (mManager!= null) {
            mManager.setCurrentActivity(this);
            mManager.getEventList(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromService, new IntentFilter("subtype_flag"));
    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String subtype = intent.getStringExtra("subtype");
            boolean playerMsg = intent.getBooleanExtra("playerMessage", false);
            String msg = intent.getStringExtra("message");
            notiEventText.setText(subtype);
            if(playerMsg){
                notiTopText.setText("YOUR FIRETEAM IS SAYING");
                notiMessage.setText(msg);
            }else {
                notiMessage.setVisibility(View.GONE);
                mManager.getEventList(ListActivityFragment.this);
            }
            notiBar.setVisibility(View.VISIBLE);
        }
    };

//    void refreshItems() {
//        // Load items
//        // ...
//        mManager.getEventList(this);
//
//        // Stop refresh animation
//        mSwipeRefreshLayout.setRefreshing(false);
//
//        // Load complete
////        onItemsLoadComplete();
//    }

    //public getEventList

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "CURRENT", "UPCOMING" };
        Context context;

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

        public View getTabView(int position) {
            View tab = LayoutInflater.from(ListActivityFragment.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setTypeface(Typeface.SANS_SERIF, 1);
            tv.setPadding(Util.dpToPx(42, ListActivityFragment.this), 25, 0, 0);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tv.setText(tabTitles[position]);
            return tab;
        }

    }

    private void updateCurrentFrag() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        if (viewPager.getCurrentItem() == 0 && page != null) {
            ((BlankFragment)page).updateCurrListAdapter(fragmentCurrentEventList);
        }else if (viewPager.getCurrentItem() == 1 && page != null) {
            ((BlankFragment)page).updateCurrListAdapter(fragmentupcomingEventList);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof EventListNetwork) {
            eList = (EventList)data;
            eData = eList.getEventList();
            createUpcomingCurrentList(eData);
            updateCurrentFrag();
        }else if (observable instanceof EventRelationshipHandlerNetwork) {
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
                        break;
                    }
                }
            }
        }
    }

    private void createUpcomingCurrentList(ArrayList<EventData> currentEventList) {
        if (currentEventList!=null && currentEventList.size()>0){
            fragmentCurrentEventList = new ArrayList<EventData>();
            fragmentupcomingEventList = new ArrayList<EventData>();
            for (int i=0;i<currentEventList.size();i++) {
                if(currentEventList.get(i).getLaunchDate()!=null) {
                    if (Util.updateCurrEventOnTime(currentEventList.get(i).getLaunchDate())) {
                        fragmentCurrentEventList.add(currentEventList.get(i));
                    } else {
                        fragmentupcomingEventList.add(currentEventList.get(i));
                    }
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(this, SendBackpressBroadcast.class);
        this.startService(in);
        finish();
    }
}
