package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import co.crossroadsapp.destiny.data.ActivityData;
import co.crossroadsapp.destiny.data.ActivityList;
import co.crossroadsapp.destiny.network.EventRelationshipHandlerNetwork;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.ActivityListNetwork;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 3/8/16.
 */
public class CreateNewEvent extends BaseActivity implements Observer, AdapterView.OnItemSelectedListener {

    private RecyclerView mRecyclerView;

    private RecyclerView mCheckpointRecycler;

    private ImageView userProfile;
    private TextView firstActivityType;
    private TextView secondActivityType;
    private TextView thirdActivityType;
    private TextView fourthActivityType;
    private TextView fifthActivityType;
    private TextView sixthActivityType;

    private TextView createAvtivityNameText;

    private ImageView activity_icon_view;
    private ImageView final_act_icon_view;
    private TextView activity_text_view;
    private TextView final_act_text_view;

    private Spinner dropdown;

    private int currentCheckpointActivityPosition;

    private ArrayList<ActivityData> checkpointActList;

    private ActivityData currentActivity;

    private TextView createNewEventBtn;
    private RelativeLayout createNewEventLayout;

    private ArrayList<ActivityData> aData;

    private ArrayList<String> checkpointItems;

    private RelativeLayout final_create_event_firstcard;

    private RelativeLayout firstActivityLayout;
    private RelativeLayout secondActivityLayout;
    private RelativeLayout thirdActivityLayout;
    private RelativeLayout fourthActivityLayout;
    private RelativeLayout fifthActivityLayout;
    private RelativeLayout sixthActivityLayout;

    private ImageView backButton;
    private UserData user;

    private ArrayAdapter<String> adapterCheckpoint;

    private ActivityList activityList;
    private ActivityViewAdapter mAdapter;
    private ActivityViewAdapter mCheckpointAdapter;
    private View view;
    private ViewFlipper vf;

    private ArrayList<ActivityData> customCurrentActivityList;

    private ProgressDialog progress;
    private RelativeLayout linlaHeaderProgress;

    private ControlManager mCntrlMngr;

//    private RelativeLayout errLayout;
//    private TextView errText;
//    private ImageView close_err;

    private RelativeLayout calendar_layout;
    private MaterialCalendarView calendar;
    private RelativeLayout date;
    private TextView date_display;
    private RelativeLayout time;
    private TextView time_display;

    static final int TIME_DIALOG_ID = 999;
    static final int DATE_DIALOG_ID = 888;
    private int hour;
    private int minute;

    private RelativeLayout notiBar;
    private TextView notiMessage;
    private TextView notiTopText;
    private TextView notiEventText;
    private ImageView notif_close;
    private ImageView createActivityIconview;
    private RelativeLayout checkpointLayout;
    Intent localPushEventObj;
    private DatePickerDialog mDatePickerDai;
    private TimePickerDialog tpd;
    private String adcardEventId;
    private ActivityData adAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        //hideStatusBar();
        setTRansparentStatusBar();

        //set orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userProfile = (ImageView) findViewById(R.id.userCreatePageProfile);

        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.setCurrentActivity(this);
        //mCntrlMngr.getAllActivities(this);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        if(b.containsKey("adsCardId")) {
            adcardEventId = b.getString("adsCardId");
        }

        checkIfPSNVerified();

        checkIfClanSet();

        if(b.containsKey("eventIntent")) {
            localPushEventObj = (Intent) b.get("eventIntent");
        }


        if (user.getImageUrl()!=null) {
            Util.picassoLoadIcon(this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.avatar);
        }

        aData = new ArrayList<ActivityData>();

        //mCntrlMngr.postGetActivityList(this);

        //createLocalActivityLists();

        createAvtivityNameText = (TextView) findViewById(R.id.event_creation_first);
        createActivityIconview = (ImageView) findViewById(R.id.firstcard_img);

        createNewEventBtn = (TextView) findViewById(R.id.create_new_event_activity);
        createNewEventLayout = (RelativeLayout) findViewById(R.id.create_new_event_activity_layout);

        firstActivityLayout = (RelativeLayout) findViewById(R.id.first_img_layout);
        secondActivityLayout = (RelativeLayout) findViewById(R.id.second_img_layout);
        thirdActivityLayout = (RelativeLayout) findViewById(R.id.third_img_layout);
        fourthActivityLayout = (RelativeLayout) findViewById(R.id.fourth_img_layout);
        fifthActivityLayout = (RelativeLayout) findViewById(R.id.fifth_img_layout);
        sixthActivityLayout = (RelativeLayout) findViewById(R.id.sixth_img_layout);

        firstActivityType = (TextView) findViewById(R.id.first);
        secondActivityType = (TextView) findViewById(R.id.second);
        thirdActivityType = (TextView) findViewById(R.id.third);
        fourthActivityType = (TextView) findViewById(R.id.fourth);
        fifthActivityType = (TextView) findViewById(R.id.fifth);
        sixthActivityType = (TextView) findViewById(R.id.sixth);

        final_act_icon_view = (ImageView) findViewById(R.id.event_icon1);
        final_act_text_view = (TextView) findViewById(R.id.event_raid1);

        customCurrentActivityList = new ArrayList<ActivityData>();

        final_create_event_firstcard = (RelativeLayout) findViewById(R.id.final_create_event_firstcard);

        checkpointLayout = (RelativeLayout) findViewById(R.id.event_creation_checkpoint_layout);
        backButton = (ImageView) findViewById(R.id.back_btn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //recyclerview for activity list
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ActivityViewAdapter();

        mRecyclerView.setAdapter(mAdapter);


        // recyclerview for checkpoint list
        mCheckpointRecycler = (RecyclerView) findViewById(R.id.activity_checkpoint_list);

        mCheckpointRecycler.setLayoutManager(new LinearLayoutManager(this));

        mCheckpointAdapter = new ActivityViewAdapter();

        //mCheckpointAdapter = new CustomAdapter();

        mCheckpointRecycler.setAdapter(mCheckpointAdapter);

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

        vf = (ViewFlipper) findViewById( R.id.viewFlipper );

        firstActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_act_icon_view.setImageResource(R.drawable.img_featured_icon);
                final_act_text_view.setText(Constants.ACTIVITY_FEATURED);
                ArrayList<ActivityData> thirdActivity = mCntrlMngr.getCustomActivityList(Constants.ACTIVITY_FEATURED);
                vfShowNext();
                if (thirdActivity != null) {
                    setActivityRecyclerView(thirdActivity);
                }
                //}
            }
        });

        secondActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_act_icon_view.setImageResource(R.drawable.img_i_c_o_n_r_a_i_d);
                final_act_text_view.setText(Constants.ACTIVITY_RAID);
                ArrayList<ActivityData> raidActivity = mCntrlMngr.getCustomActivityList(Constants.ACTIVITY_RAID);
                customCurrentActivityList.clear();
                if (raidActivity!=null) {
                    customCurrentActivityList.addAll(raidActivity);
                    ArrayList<ActivityData> filterRaidActivity = new ArrayList<ActivityData>();
                    int counter = 0;
                    for (int i = 0; i < raidActivity.size(); i++) {
                        if (i == 0) {
                            filterRaidActivity.add(raidActivity.get(i));
                        } else {
                            counter = 0;
                            for (int y = 0; y < filterRaidActivity.size(); y++) {
                                if (raidActivity.get(i).getActivitySubtype().equalsIgnoreCase(filterRaidActivity.get(y).getActivitySubtype()) && raidActivity.get(i).getActivityDifficulty().equalsIgnoreCase(filterRaidActivity.get(y).getActivityDifficulty())) {
                                    counter++;
                                }
                            }
                            if (counter == 0) {
                                filterRaidActivity.add(raidActivity.get(i));
                            }
                        }
                    }
                    if (filterRaidActivity != null) {
                        setActivityRecyclerView(filterRaidActivity);
                    }
                }
                vfShowNext();
            }
        });

        thirdActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_act_icon_view.setImageResource(R.drawable.img_arena_icon);
                final_act_text_view.setText(Constants.ACTIVITY_ARENA);
                ArrayList<ActivityData> secActivity = mCntrlMngr.getCustomActivityList(thirdActivityType.getText().toString());
                customCurrentActivityList.clear();
                if (secActivity != null) {
                    customCurrentActivityList.addAll(secActivity);
                    ArrayList<ActivityData> filterRaidActivity = new ArrayList<ActivityData>();
                    int counter = 0;
                    for (int i = 0; i < secActivity.size(); i++) {
                        if (i == 0) {
                            filterRaidActivity.add(secActivity.get(i));
                        } else {
                            counter = 0;
                            for (int y = 0; y < filterRaidActivity.size(); y++) {
                                if (secActivity.get(i).getActivitySubtype().equalsIgnoreCase(filterRaidActivity.get(y).getActivitySubtype()) && secActivity.get(i).getActivityDifficulty().equalsIgnoreCase(filterRaidActivity.get(y).getActivityDifficulty())) {
                                    counter++;
                                }
                            }
                            if (counter == 0) {
                                filterRaidActivity.add(secActivity.get(i));
                            }
                        }
                    }


                    if (secActivity != null) {
                        setActivityRecyclerView(filterRaidActivity);
                    }
                    //}
                }
                vfShowNext();
            }
        });

        fourthActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_act_icon_view.setImageResource(R.drawable.img_crucible_icon);
                final_act_text_view.setText(Constants.ACTIVITY_CRUCIBLE);
                    ArrayList<ActivityData> thirdActivity = mCntrlMngr.getCustomActivityList(fourthActivityType.getText().toString());
                    vfShowNext();
                    if (thirdActivity != null) {
                        setActivityRecyclerView(thirdActivity);
                    }
                //}
            }
        });

        fifthActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_act_icon_view.setImageResource(R.drawable.img_strikes_icon);
                final_act_text_view.setText(Constants.ACTIVITY_STRIKES);
                ArrayList<ActivityData> thirdActivity = mCntrlMngr.getCustomActivityList("Strike");
                vfShowNext();
                if (thirdActivity != null) {
                    setActivityRecyclerView(thirdActivity);
                }
                //}
            }
        });

        sixthActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_act_icon_view.setImageResource(R.drawable.img_patrol_icon);
                final_act_text_view.setText(Constants.ACTIVITY_PATROL);
                ArrayList<ActivityData> secActivity = mCntrlMngr.getCustomActivityList(sixthActivityType.getText().toString());
                customCurrentActivityList.clear();
                if (secActivity != null) {
                    customCurrentActivityList.addAll(secActivity);
                    ArrayList<ActivityData> filterRaidActivity = new ArrayList<ActivityData>();
                    int counter = 0;
                    for (int i = 0; i < secActivity.size(); i++) {
                        if (i == 0) {
                            filterRaidActivity.add(secActivity.get(i));
                        } else {
                            counter = 0;
                            for (int y = 0; y < filterRaidActivity.size(); y++) {
                                if (secActivity.get(i).getActivitySubtype().equalsIgnoreCase(filterRaidActivity.get(y).getActivitySubtype()) && secActivity.get(i).getActivityDifficulty().equalsIgnoreCase(filterRaidActivity.get(y).getActivityDifficulty())) {
                                    counter++;
                                }
                            }
                            if (counter == 0) {
                                filterRaidActivity.add(secActivity.get(i));
                            }
                        }
                    }


                    if (secActivity != null) {
                        setActivityRecyclerView(filterRaidActivity);
                    }
                    //}
                }
                vfShowNext();
            }
        });

        final_create_event_firstcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vf.getDisplayedChild() == 2) {
                    if (mAdapter != null && mAdapter.aList != null) {
                        if (checkpointActList != null && currentCheckpointActivityPosition >= 0) {
                            if (checkpointActList.get(currentCheckpointActivityPosition).getId() != null && !checkpointActList.get(currentCheckpointActivityPosition).getId().isEmpty()) {
                                String activityId = checkpointActList.get(currentCheckpointActivityPosition).getId();
                                String creator_id = user.getUserId();
                                int maxP = checkpointActList.get(currentCheckpointActivityPosition).getMaxPlayer();
                                int minP = checkpointActList.get(currentCheckpointActivityPosition).getMinPlayer();
                                ArrayList<String> players = new ArrayList<String>();
                                players.add(creator_id);
                                if (activityId != null && creator_id != null) {
                                    hideProgressBar();
                                    showProgressBar();
                                    //linlaHeaderProgress.setVisibility(View.VISIBLE);
                                    String dateTime = getCreateEventDateTime();
                                    //mCntrlMngr.postCreateEvent(activityId, creator_id, minP, maxP, dateTime, CreateNewEvent.this);
                                }
                            }
                        }
                    }
                }
            }
        });

        dropdown = (Spinner)findViewById(R.id.event_creation_checkpoint);
        dropdown.setBackground(backgroundWithBorder(getResources()
                        .getColor(R.color.app_theme_color),
                getResources().getColor(R.color.app_theme_color)));
        checkpointItems = new ArrayList<String>();
        dropdown.setOnItemSelectedListener(this);

        date = (RelativeLayout) findViewById(R.id.date);
        date_display = (TextView) findViewById(R.id.date_text);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        time = (RelativeLayout) findViewById(R.id.time);
        time_display = (TextView) findViewById(R.id.time_text);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        adCardFlow();
    }

    private void adCardFlow() {
        if(adcardEventId!=null && (!adcardEventId.isEmpty())) {
            adAct = mCntrlMngr.getAdsActivity(adcardEventId);
            if(adAct!=null) {
                final_act_text_view.setText(adAct.getActivityType());
                if (adAct.getActivityType()!=null) {
                    switch(adAct.getActivityType().toUpperCase()) {
                        case (Constants.ACTIVITY_ARENA):
                            final_act_icon_view.setImageResource(R.drawable.img_arena_icon);
                            break;
                        case (Constants.ACTIVITY_CRUCIBLE):
                            final_act_icon_view.setImageResource(R.drawable.img_crucible_icon);
                            break;
                        case (Constants.ACTIVITY_FEATURED):
                            final_act_icon_view.setImageResource(R.drawable.img_featured_icon);
                            break;
                        case (Constants.ACTIVITY_PATROL):
                            final_act_icon_view.setImageResource(R.drawable.img_patrol_icon);
                            break;
                        case (Constants.ACTIVITY_STRIKES):
                            final_act_icon_view.setImageResource(R.drawable.img_strikes_icon);
                            break;
                    }
                }
                Util.picassoLoadIcon(CreateNewEvent.this, createActivityIconview, adAct.getActivityIconUrl(), R.dimen.activity_icon_hgt_createevent, R.dimen.activity_icon_width_createevent, R.drawable.icon_ghost_default);
                createAvtivityNameText.setText(adAct.getActivitySubtype()!=null?adAct.getActivitySubtype():"" + " - " + adAct.getActivityDifficulty()!=null?adAct.getActivityDifficulty():"" );
                TextView adCheckpoint = (TextView) findViewById(R.id.ad_checkpoint_text);
                if(adAct.getActivityCheckpoint()!=null) {
                    adCheckpoint.setText("Checkpoint - " + adAct.getActivityCheckpoint());
                }else {
                    checkpointLayout.setVisibility(View.GONE);
                }

                final_create_event_firstcard.setClickable(false);
                checkpointLayout.setClickable(false);
                if(createNewEventLayout!=null) {
                    createNewEventLayout.setVisibility(View.VISIBLE);
                }

                createNewEventBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideProgressBar();
                        showProgressBar();
                        String dateTime = getCreateEventDateTime();
                        if (adAct.getId() != null && user.getUserId() != null){
                            //mCntrlMngr.postCreateEvent(adAct.getId(), user.getUserId(), adAct.getMinPlayer(), adAct.getMaxPlayer(), dateTime, CreateNewEvent.this);
                    }
                    }
                });
                vfShowNext();
                vfShowNext();
            }else {
                launchListActivityAndFinish();
            }
        }
    }

//    @Override
//    protected Dialog onCreateDialog(int id) {
//        // TODO Auto-generated method stub
//        switch (id) {
//            case TIME_DIALOG_ID:
//                Calendar c = Calendar.getInstance();
//                // set time picker as current time
//                tpd = new TimePickerDialog(this, timePickerListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),false);
//
//                tpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                    }
//                });
//                return tpd;
//
//            case DATE_DIALOG_ID:
//                Date d = Util.getCurrentCalendar();
//                mDatePickerDai = new DatePickerDialog(this, myDateListener, d.getYear(), d.getMonth(), d.getDate());
//                //disable previous dates for datepicker
//                mDatePickerDai.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//                return mDatePickerDai;
//                //return new DatePickerDialog(this, myDateListener, d.getYear(), d.getMonth(), d.getDate());
//
//            default: return null;
//        }
//    }

//    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
//            // TODO Auto-generated method stub
////            date_display.setText(arg1 + "-" + arg2+1
////                    + "-" + arg3);
//            //showDate(arg1, arg2+1, arg3);
//
//            int m = arg2;
//            int d = arg3;
//            int y = arg1;
//
//            //todo will change with new calendar but for now hacking to reset date if user selects previous date
//            String date=null;
//            Date currentDate = Util.getCurrentCalendar();
//
//            Date selectedDate = new Date(y, m, d);
//
//            if(selectedDate.compareTo(currentDate)<0){
//                Toast.makeText(getApplicationContext(), "Cannot set previous date.", Toast.LENGTH_SHORT).show();
//                m =  currentDate.getMonth();
//                d = currentDate.getDate();
//                y = currentDate.getYear();
//
//                //set datepicker view to current date
//                updateDatePickerCalendar();
//            }
////            else {
//            date = m + "-" + d
//                        + "-" + y;
////            }
//
//            if (m == 1 && d > 29) {
//                m = 3;
//                if (d == 30) {
//                    d = 1;
//                } else {
//                    d = 2;
//                }
//            } else {
//                m = m + 1;
//            }
//            date_display.setText(m + "-" + d
//                    + "-" + y);
//            Util.checkTimePicker(date_display, time_display, CreateNewEvent.this);
//        }
//    };

    private void setVfNextAnimation() {
        if(vf!=null) {
            // Next screen comes in from left.
            vf.setInAnimation(this, R.anim.slide_in_from_right);
            // Current screen goes out from right.
            vf.setOutAnimation(this, R.anim.slide_out_to_left);
        }
    }

    private void setVfPrevAnimation() {
        if(vf!=null) {
            // Next screen comes in from left.
            vf.setInAnimation(this, R.anim.slide_in_from_left);
            // Current screen goes out from right.
            vf.setOutAnimation(this, R.anim.slide_out_to_right);
        }
    }

    private void checkIfPSNVerified() {
        if(user!=null) {
            if (user.getPsnVerify() != null) {
                if (!user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                    launchListActivityAndFinish();
                }
            }
        }
    }

    private void checkIfClanSet() {
        if (user!=null) {
            if (user.getClanId()!=null) {
                if (user.getClanId().equalsIgnoreCase(Constants.CLAN_NOT_SET)) {
                    launchListActivityAndFinish();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTRansparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.createnewevent_topbar));
        }
    }

    private String getCreateEventDateTime() {
        if(date_display!=null) {
            if ((!date_display.getText().toString().equalsIgnoreCase(getResources().getString(R.string.date_default))) || (!time_display.getText().toString().equalsIgnoreCase(getResources().getString(R.string.time_default)))) {
                try {
                    if(date_display.getText().toString().equalsIgnoreCase(getResources().getString(R.string.date_default))) {
                        date_display.setText(Util.getCurrentDate());
                    }
                    if(time_display.getText().toString().equalsIgnoreCase(getResources().getString(R.string.time_default))) {
                        time_display.setText(Util.getCurrentTime());
                    }
                    String d = Util.changeTimeFormat(time_display.getText().toString(), date_display.getText().toString());
                    return d;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromService, new IntentFilter("subtype_flag"));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(ReceivefromService);
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
//                notiEventText.setText(msg);
//                notiTopText.setText(subtype);
//                notiMessage.setVisibility(View.GONE);
//                //mManager.getEventList(ListActivityFragment.this);
//            }
//            notiBar.setVisibility(View.VISIBLE);
            //put timer to make the notification message gone after 5 seconds
//            notiBar.postDelayed(new Runnable() {
//                public void run() {
//                    if(notiBar!=null) {
//                        notiBar.setVisibility(View.GONE);
//                    }
//                }
//            }, 7000);
        }
    };

//    protected TimePickerDialog.OnTimeSetListener timePickerListener =  new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
//            Calendar c = Calendar.getInstance();
//            hour = selectedHour;
//            minute = selectedMinute;
//            String aTime=null;
//            int currHour = c.get(Calendar.HOUR_OF_DAY);
//            int currMin = c.get(Calendar.MINUTE);
//
//            if(Util.isPresentDay(CreateNewEvent.this, date_display)) {
//                if (hour >= currHour) {
//                    if (hour == currHour && minute < currMin) {
//                        aTime = Util.getCurrentTime(currHour, currMin, CreateNewEvent.this);
//                    } else {
//                        // set current time into textview
//                        aTime = Util.updateTime(hour, minute);
//                    }
//                }else {
//                    aTime = Util.getCurrentTime(currHour, currMin, CreateNewEvent.this);
//                }
//            }else {
//                // set current time into textview
//                aTime = Util.updateTime(hour, minute);
//            }
//            if (aTime != null) {
//                time_display.setText(aTime);
//            }
//        }
//    };

    public void showError(String err) {
        hideProgressBar();
        setErrText(err);
    }

    public GradientDrawable backgroundWithBorder(int bgcolor, int brdcolor) {

        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(bgcolor);
        gdDefault.setStroke(2, brdcolor);
        gdDefault.setCornerRadii(new float[]{Util.dpToPx(2, this), Util.dpToPx(2, this), 0, 0, 0, 0,
                Util.dpToPx(2, this), Util.dpToPx(2, this)});

        return gdDefault;

    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        currentCheckpointActivityPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ActivityViewAdapter extends RecyclerView.Adapter<ActivityViewAdapter.ActivityViewHolder> {
        private ArrayList<ActivityData> aList;
        public ActivityViewAdapter() {
            if (aList==null) {
                aList = new ArrayList<>();
            }
        }

        protected void addItem(ArrayList<ActivityData> a) {
            this.aList.addAll(a);
        }

        public class ActivityViewHolder extends RecyclerView.ViewHolder {

            private CardView activity_card;
            private ImageView activity_icon;
            private TextView activity_detail_name;
            // each data item is just a string in this case

            public ActivityViewHolder(View v) {
                super(v);
                view = v;
                activity_card = (CardView) v.findViewById(R.id.activity_card);
//                activity_icon = (ImageView) v.findViewById(R.id.activity_icon);
                activity_detail_name = (TextView) v.findViewById(R.id.activity_text);
            }
        }

        @Override
        public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_activity, null);
            return new ActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityViewHolder holder, final int position) {
            if (aList!=null) {
                String iconUrl = aList.get(position).getActivityIconUrl();
                String diff = aList.get(position).getActivityDifficulty();
                int aLight = aList.get(position).getActivityLight();
                String aFinalLight=" ";

                if (diff==null || diff.equalsIgnoreCase("null")){
                    diff = "";
                } else {
                    diff = " - " +diff;
                }

                final String activity_custom_name = aList.get(position).getActivitySubtype()+ diff +" "+ aFinalLight;

                if (!activity_custom_name.isEmpty()) {
                    holder.activity_detail_name.setText(activity_custom_name);
                }

                holder.activity_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToCreateNewEvent(aList.get(position), activity_custom_name);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return this.aList.size();
        }
    }

    private void goToCreateNewEvent(ActivityData a, String activity_custom_name) {
        currentActivity = a;
        checkpointItems.clear();
        if (currentActivity!=null) {
            if (currentActivity.getActivitySubtype()!= null && currentActivity.getActivityDifficulty()!=null) {
                checkpointActList = mCntrlMngr.getCheckpointActivityList(currentActivity.getActivitySubtype(), currentActivity.getActivityDifficulty());

                for (int i = 0; i < checkpointActList.size(); i++) {
                    if (checkpointActList.get(i).getActivityCheckpoint() != null && !checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase("null")) {
                        checkpointItems.add(checkpointActList.get(i).getActivityCheckpoint());
                    }
                }

                if ((checkpointItems != null) && (!checkpointItems.isEmpty())) {
                    checkpointLayout.setVisibility(View.VISIBLE);
                    adapterCheckpoint = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, checkpointItems) {

                        int FONT_STYLE = Typeface.BOLD;

                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            ((TextView) v).setTypeface(Typeface.SANS_SERIF, FONT_STYLE);
                            ((TextView) v).setTextColor(
                                    getResources().getColorStateList(R.color.trimbe_white)
                            );
                            ((TextView) v).setGravity(Gravity.LEFT);

                            ((TextView) v).setPadding(Util.dpToPx(55, CreateNewEvent.this), 0, 0, 0);
                            ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            ((TextView) v).setText("Checkpoint - " + ((TextView) v).getText());

                            return v;
                        }

                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            return getCustomView(position, convertView, parent, checkpointItems);
                        }
                    };
                adapterCheckpoint.setDropDownViewResource(R.layout.empty_layout);
                dropdown.setAdapter(adapterCheckpoint);
                adapterCheckpoint.notifyDataSetChanged();
            } else {
                    checkpointLayout.setVisibility(View.GONE);
                }
            }
        }
        createAvtivityNameText.setText(activity_custom_name);
        //createActivityIconview.
        Util.picassoLoadIcon(CreateNewEvent.this, createActivityIconview, currentActivity.getActivityIconUrl(), R.dimen.activity_icon_hgt_createevent, R.dimen.activity_icon_width_createevent, R.drawable.icon_ghost_default);
        if(createNewEventLayout!=null) {
            createNewEventLayout.setVisibility(View.VISIBLE);
        }
        vfShowNext();
    }

    private void vfShowNext() {
        if (vf!=null) {
            if(adcardEventId==null) {
                setVfNextAnimation();
            }
            vf.showNext();
        }
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, ArrayList<String> adapterCheckpoint) {
        LayoutInflater inflater=getLayoutInflater();
        View row=inflater.inflate(R.layout.fragment_checkpoint, parent, false);
        TextView label=(TextView)row.findViewById(R.id.activity_checkpoint_text);
        if (adapterCheckpoint!=null) {
            label.setText(adapterCheckpoint.get(position));
        }
        return row;
    }

    private void setActivityRecyclerView(ArrayList<ActivityData> raidActivity) {
        mAdapter.aList.clear();
        mAdapter.addItem(raidActivity);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof ActivityListNetwork) {
            activityList = (ActivityList) data;
            aData = activityList.getActivityList();
        } else if (observable instanceof EventRelationshipHandlerNetwork) {
            hideProgressBar();
            launchListActivityAndFinish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCntrlMngr!=null) {
            mCntrlMngr.setCurrentActivity(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (vf!=null) {
            if(adcardEventId!=null && (!adcardEventId.isEmpty())) {
                launchListActivityAndFinish();
            }else if(vf.getDisplayedChild()>0){
                setVfPrevAnimation();
                //todo fix the hack to reset date, time and checkpoint
                setCalendarCheckpointToDefault();
                vf.setDisplayedChild(vf.getDisplayedChild() - 1);
            }else {
                vf.clearAnimation();
                launchListActivityAndFinish();
            }
        }else {
            launchListActivityAndFinish();
        }
    }

    private void setCalendarCheckpointToDefault() {
        if(vf!=null){
            if(vf.getDisplayedChild()==2) {
                if(date_display!=null) {
                    date_display.setText(getResources().getString(R.string.date_default));
                    updateDatePickerCalendar();
                }

                if(time_display!=null) {
                    time_display.setText(getResources().getString(R.string.time_default));
                    updateTimePicker();
                }
                //todo hack to fix for bug showing checkpoint for wrong event
                if(checkpointLayout!=null) {
                    checkpointLayout.setVisibility(View.GONE);
                }

                if(createNewEventLayout!=null) {
                    createNewEventLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    private void updateDatePickerCalendar() {
        if(mDatePickerDai!=null) {
            if (mDatePickerDai.getDatePicker() != null) {
                Date d = Util.getCurrentCalendar();
                mDatePickerDai.getDatePicker().updateDate(d.getYear(), d.getMonth(), d.getDate());
            }
        }
    }

    private void updateTimePicker() {
        if(tpd!=null) {
            Calendar c = Calendar.getInstance();
            tpd.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }
    }

    private void launchListActivityAndFinish() {
        Intent i=new Intent (this, ListActivityFragment.class);
        i.putExtra("userdata", user);
        if(localPushEventObj!=null){
            i.putExtra("eventIntent", localPushEventObj);
        }
        startActivity(i);
        finish();
    }
}
