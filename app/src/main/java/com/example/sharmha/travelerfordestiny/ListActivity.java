package com.example.sharmha.travelerfordestiny;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharmha.travelerfordestiny.data.CurrentEventDataHolder;
import com.example.sharmha.travelerfordestiny.network.EventListNetwork;
import com.example.sharmha.travelerfordestiny.network.EventRelationshipHandlerNetwork;
import com.example.sharmha.travelerfordestiny.network.NetworkEngine;
import com.example.sharmha.travelerfordestiny.utils.CircularImageView;
import com.example.sharmha.travelerfordestiny.utils.SendBackpressBroadcast;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.EventList;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 2/25/16.
 */
public class ListActivity extends Activity implements Observer{


    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventsViewAdapter mAdapter;

    private static ListActivity mInstance;

    private TextView createNewEventBtn;
    private RelativeLayout notiBar;
    private TextView notiEventText;
    private TextView notiTopText;
    private TextView notiMessage;
    private ImageView notif_close;

    private ArrayList<EventData> eData;

    private EventList eList;

    private ImageView logout;
    private TextView crash_report;

    View view;

    private UserData user;
    private ImageView userProfile;

    private ControlManager mManager;

    private static final String TAG = ListActivity.class.getName();

    private String url = "a/event/list";

    private NetworkEngine ntwrk;
    private RelativeLayout errLayout;
    private TextView errText;
    private ImageView close_err;
    private CircularImageView profile;
    private DrawerLayout drawerLayout;
    private CircularImageView userProfileDrawer;
    private TextView userNameDrawer;
    private EventData currEventData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        //set orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userProfile = (ImageView) findViewById(R.id.userProfile);
        userProfileDrawer = (CircularImageView) findViewById(R.id.profile_avatar);
        userNameDrawer = (TextView) findViewById(R.id.profile_name);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

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
                if(user.getUser()!=null) {
                    rp.put("userName", user.getUser());
                }
                mManager.postLogout(ListActivity.this,rp);
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
//                CacheManager.HostUserInfoCache cache = (CacheManager.HostUserInfoCache) CacheManager.getCacheManager().findOnCache(CacheManager.getCacheManager().findIndex(CacheManager.USER_DATA_KEY));
//                try {
//                    Utils.checkForNull(cache.getUser());
//                    cancelHomeCouchMarks();
//                    cancelChatTrayCouchMarks();
//                    cancelHomeSwipeCouchMarks();
//                    HostUser host = cache.getUser();
//                    TextView profile_avatar = (TextView) findViewById(R.id.profile_name);
//                    if (profile_avatar != null) {
//                        changeProfileUsername(host.getName(), true);
//                    }
//                    final View logout = findViewById(R.id.logout_btn);
//                    if (logout != null) {
//                        logout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                logoutUser();
//                            }
//                        });
//                    }
//                    TextView version = (TextView) findViewById(R.id.watchat_version_name);
//                    if (version != null) {
//                        version.setText(String.format(getString(R.string.watchat_version_name), Utils.getApplicationVersion()));
//                        version.setVisibility(View.VISIBLE);
//                    }
//                    TextView server = (TextView) findViewById(R.id.watchat_server);
//                    if (server != null) {
//                        server.setText(String.format(getString(R.string.watchat_server_name), (AppEngine.DEBUG_APP == 1 ? "prod" : "staging")));
//                        server.setVisibility(View.VISIBLE);
//                    }
//                    Utils.checkForNull(host);
//                    if (host.getProfileUrl() != null) {
//                        refreshProfileAvatar(host.getProfileUrl());
//                    }
//                } catch (TrimbleException e) {
//                    launchLoginScreen();
//                }
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

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        eData = new ArrayList<EventData>();

        mManager.getEventList(this);
//        ntwrk = new NetworkEngine(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.event_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new EventsViewAdapter();

        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
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

    public static ListActivity getmInstance() {
        if (mInstance==null) {
            mInstance = new ListActivity();
            return mInstance;
        } else {
            return mInstance;
        }
    }

    public EventData getCurrEventData(){
        if (this.currEventData!=null){
            return this.currEventData;
        }
        return null;
    }

    public void setCurrEventData(EventData ed){
        currEventData = new EventData();
        this.currEventData = (EventData) ed;
    }

    private ActionBarDrawerToggle mDrawerToggle;

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

    void refreshItems() {
        // Load items
        // ...
        mManager.getEventList(this);

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);

        // Load complete
//        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
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
            }
            notiBar.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void update(Observable observable, Object data) {
//        Toast.makeText(ListActivity.this, "list fetching complete",
//                        Toast.LENGTH_SHORT).show();

        if (observable instanceof EventListNetwork) {
            eList = (EventList)data;

            eData = eList.getEventList();

            mAdapter.elistLocal.clear();
            mAdapter.addItem(eData);
            mAdapter.notifyDataSetChanged();
        }else if (observable instanceof EventRelationshipHandlerNetwork) {
            EventData ed = new EventData();
            ed = (EventData) data;
            if (this.eData!= null) {
                for (int i=0; i<this.eData.size();i++) {
                    if (ed.getEventId().equalsIgnoreCase(this.eData.get(i).getEventId())) {
                        if (ed.getMaxPlayer()>0) {
                            this.eData.remove(i);
                            this.eData.add(i, ed);
                            mAdapter.elistLocal.remove(i);
                            mAdapter.elistLocal.add(i, ed);
                            mAdapter.notifyItemChanged(i);
                        } else {
                            this.eData.remove(i);
                            mAdapter.elistLocal.remove(i);
                            mAdapter.notifyItemRemoved(i);
                        }

                        break;
                    }
                }
//                eData.add(ed);
//                mAdapter.elistLocal.add(ed);
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

//    public void getEvents() throws JSONException {
//        if (Util.isNetworkAvailable(this)) {
//            ntwrk.get(url, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    // If the response is JSONObject instead of expected JSONArray
////                    Toast.makeText(mContext, "List server call Success",
////                            Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                    // If the response is JSONObject instead of expected JSONArray
////                    Toast.makeText(mContext, "List server call Success",
////                            Toast.LENGTH_SHORT).show();
//
//
//                    try {
//                        parseEventList(response);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    Toast.makeText(mContext, "List error from server  - " + statusCode + " ",
//                            Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    try {
//                        Toast.makeText(mContext, "List error from server  - " + statusCode + " " + errorResponse.getString("message"),
//                                Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }else {
//            Util.createNoNetworkDialogue(mContext);
//        }
//    }

    public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewAdapter.EventsViewHolder> {
        private ArrayList<EventData> elistLocal;

        public EventsViewAdapter() {
            elistLocal = new ArrayList<EventData>();
        }

        protected void addItem(ArrayList<EventData> a) {
            this.elistLocal.addAll(a);
        }

        public class EventsViewHolder extends RecyclerView.ViewHolder {

            private CardView event_card;
            protected TextView eventSubType;
            protected TextView eventPlayerNames;
            protected TextView eventaLight;
            protected TextView eventPlayerNameCnt;
            protected ImageView eventIcon;
            protected ImageView playerProfileImage1;
            protected ImageView playerProfileImage2;
            protected ImageView playerProfileImage3;
            protected TextView playerCountImage3;
            protected ImageView joinBtn;
            protected ImageView unjoinBtn;
            // each data item is just a string in this case

            public EventsViewHolder(View v) {
                super(v);
                view = v;
                event_card = (CardView) v.findViewById(R.id.event);
                eventSubType = (TextView) v.findViewById(R.id.activity_name);
                eventPlayerNames = (TextView) v.findViewById(R.id.activity_player_name);
                eventaLight = (TextView) v.findViewById(R.id.activity_aLight);
                eventIcon = (ImageView) v.findViewById(R.id.event_icon);
                playerProfileImage1 = (ImageView) v.findViewById(R.id.player_picture_1);
                playerProfileImage2 = (ImageView) v.findViewById(R.id.player_picture_2);
                playerProfileImage3 = (ImageView) v.findViewById(R.id.player_picture_3);
                playerCountImage3 = (TextView) v.findViewById(R.id.player_picture_text_3);
                joinBtn = (ImageView) v.findViewById(R.id.join_btn);
                unjoinBtn = (ImageView) v.findViewById(R.id.unjoin_btn);
                eventPlayerNameCnt = (TextView) v.findViewById(R.id.activity_player_name_lf);
            }
        }

        //animating the card
//        private void toggleProductDescriptionHeight(final View v) {
//
//            int descriptionViewMinHeight = Util.dpToPx(112, ListActivity.this);
//            int descriptionViewFullHeight = Util.dpToPx(350, ListActivity.this);
//            if (v.getHeight() == descriptionViewMinHeight) {
//                // expand
//                ValueAnimator anim = ValueAnimator.ofInt(v.getMeasuredHeightAndState(),
//                        descriptionViewFullHeight);
//                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                        int val = (Integer) valueAnimator.getAnimatedValue();
//                        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
//                        layoutParams.height = val;
//                        v.setLayoutParams(layoutParams);
//                    }
//                });
//                anim.start();
//            } else {
//                // collapse
//                ValueAnimator anim = ValueAnimator.ofInt(v.getMeasuredHeightAndState(),
//                        descriptionViewMinHeight);
//                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                        int val = (Integer) valueAnimator.getAnimatedValue();
//                        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
//                        layoutParams.height = val;
//                        v.setLayoutParams(layoutParams);
//                    }
//                });
//                anim.start();
//            }
//        }

        @Override
        public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_event, null);
            return new EventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventsViewHolder holder, int position) {
            if (this.elistLocal.size()>0) {
                boolean CreatorIn=false;
                boolean CreatorIsPlayer=false;
                String allNames = "";
                String allNamesRem = "";
                final EventData currEvent = elistLocal.get(position);
                final String eId = this.elistLocal.get(position).getEventId();
                String s = this.elistLocal.get(position).getActivityData().getActivitySubtype();
                int l = this.elistLocal.get(position).getActivityData().getActivityLight();
                String url = this.elistLocal.get(position).getActivityData().getActivityIconUrl();
                int reqPlayer = this.elistLocal.get(position).getActivityData().getMaxPlayer() - this.elistLocal.get(position).getPlayerData().size();
                // get players
                int i = this.elistLocal.get(position).getPlayerData().size();
                String creatorId = this.elistLocal.get(position).getCreatorData().getPlayerId();
                final String status = this.elistLocal.get(position).getEventStatus();

                if (creatorId.equalsIgnoreCase(user.getUserId())){
                    CreatorIn = true;
                    CreatorIsPlayer = true;
                }

                for (int y =0; y< i; y++) {
                    String n = this.elistLocal.get(position).getPlayerData().get(y).getPsnId();
                    String profileUrl = this.elistLocal.get(position).getPlayerData().get(y).getPlayerImageUrl();
                    String pId = this.elistLocal.get(position).getPlayerData().get(y).getPlayerId();
                    if(user.getUserId().equalsIgnoreCase(pId)) {
                        CreatorIn = true;
                    }

//                    if (i>1 && y<i-1) {
//                        allNames = allNames + n + ", ";
//                    } else {
//                        allNames = allNames + n;
//                   }

                    if(y<4)
                    uploadPlayerImg(holder, profileUrl, y, i);
                }

                allNames = "Created by " + this.elistLocal.get(position).getCreatorData().getPsnId();
                if (!status.equalsIgnoreCase("full")) {
                    allNamesRem = ", " + "LF" +reqPlayer+"M";
                }

                if (status != null && !status.equalsIgnoreCase("")) {
                    if (!status.equalsIgnoreCase(Constants.STATUS_FULL) && !CreatorIn) {
                        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams rp = new RequestParams();
                                rp.put("eId", eId);
                                rp.put("player", user.getUserId());
                                mManager.postJoinEvent(ListActivity.this, rp);
                                holder.joinBtn.setClickable(false);
                            }
                        });
                    } else if (CreatorIn){
                        holder.unjoinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams rp = new RequestParams();
                                rp.put("eId", eId);
                                rp.put("player", user.getUserId());
                                mManager.postUnJoinEvent(ListActivity.this, rp);
                                holder.unjoinBtn.setClickable(false);
                            }
                        });
                    }
                }
                final boolean showJoin;
                if (!status.equalsIgnoreCase(Constants.STATUS_FULL) && !CreatorIn) {
                    showJoin = true;
                } else {
                    showJoin = false;
                }

                if (s != "" || s != null) {
                    holder.eventSubType.setText(s);
                }

                holder.event_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currEvent!=null) {
                            CurrentEventDataHolder ins = CurrentEventDataHolder.getInstance();
                            ins.setData(currEvent);
                            if (showJoin) {
                                ins.setJoinVisible(true);
                            }else {
                                ins.setJoinVisible(false);
                            }
                            //setCurrEventData(currEvent);
                        }
                        //start new activity for event
                        Intent regIntent = new Intent(getApplicationContext(),
                                EventDetailActivity.class);
                                regIntent.putExtra("userdata", user);
                        startActivity(regIntent);
                    }
                });

                if(l>0) {
                    holder.eventaLight.setText("+" + String.valueOf(l));
                }

                updateJoinButton(holder, status, CreatorIn, CreatorIsPlayer);

                holder.eventPlayerNames.setText(allNames);
                holder.eventPlayerNameCnt.setText(allNamesRem);

                Util.picassoLoadIcon(ListActivity.this, holder.eventIcon, url, R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.img_i_c_o_n_r_a_i_d);
            }
        }

        private void updateJoinButton(EventsViewHolder holder, String status, boolean creatorIn, boolean creatorIsPlayer) {
            switch (status) {
                case "new":
                    if(creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    }else if(creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_g_o_i_n_g);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_j_o_i_n);
                    }
                    break;
                case "can_join":
                    if(creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    }else if(creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_g_o_i_n_g);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_j_o_i_n);
                    }
                    break;
                case "full":
                    if(creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    }else if(creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_r_e_a_d_y);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_f_u_l_l);
                    }
                    break;
                case "open":
                    if(creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    }else if(creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_g_o_i_n_g);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_j_o_i_n);
                    }
                    break;
            }
        }

        private void uploadPlayerImg(EventsViewHolder holder, String profileUrl, int player, int playersCount) {
            switch (player) {
                case 0:
                    holder.playerProfileImage3.setVisibility(View.GONE);
                    holder.playerProfileImage2.setVisibility(View.GONE);
                    holder.playerCountImage3.setVisibility(View.GONE);
                    Util.picassoLoadIcon(ListActivity.this, holder.playerProfileImage1, profileUrl, R.dimen.player_icon_hgt, R.dimen.player_icon_width, R.drawable.avatar);
                    break;
                case 1:
                    holder.playerProfileImage2.setVisibility(View.VISIBLE);
                    holder.playerCountImage3.setVisibility(View.GONE);
                    Util.picassoLoadIcon(ListActivity.this, holder.playerProfileImage2, profileUrl, R.dimen.player_icon_hgt, R.dimen.player_icon_width, R.drawable.avatar);
                    break;
                default:
                    holder.playerProfileImage3.setVisibility(View.VISIBLE);
                    if (player==2 && playersCount<4) {
                        holder.playerCountImage3.setVisibility(View.GONE);
                        Util.picassoLoadIcon(ListActivity.this, holder.playerProfileImage3, profileUrl, R.dimen.player_icon_hgt, R.dimen.player_icon_width, R.drawable.avatar);
                    } else {
                        holder.playerProfileImage3.setImageResource(R.drawable.img_avatar_empty);
                        holder.playerCountImage3.setVisibility(View.VISIBLE);
                        int p = playersCount-2;
                        holder.playerCountImage3.setText("+" + p);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return this.elistLocal.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
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
