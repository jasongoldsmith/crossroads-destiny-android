package com.example.sharmha.travelerfordestiny;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.sharmha.travelerfordestiny.data.ActivityData;
import com.example.sharmha.travelerfordestiny.data.ActivityList;
import com.example.sharmha.travelerfordestiny.network.EventRelationshipHandlerNetwork;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.network.ActivityListNetwork;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 3/8/16.
 */
public class CreateNewEvent extends Activity implements Observer, AdapterView.OnItemSelectedListener {

    private RecyclerView mRecyclerView;

    private RecyclerView mCheckpointRecycler;

    private ImageView userProfile;
    private TextView firstActivityType;
    private TextView secondActivityType;
    private TextView thirdActivityType;
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

    private ArrayList<ActivityData> aData;

    private ArrayList<String> checkpointItems;

    private RelativeLayout final_create_event_firstcard;

    private RelativeLayout firstActivityLayout;
    private RelativeLayout secondActivityLayout;
    private RelativeLayout thirdActivityLayout;

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
    private LinearLayout linlaHeaderProgress;

    private ControlManager mCntrlMngr;

    private RelativeLayout errLayout;
    private TextView errText;
    private ImageView close_err;

    private RelativeLayout calendar_layout;
    private MaterialCalendarView calendar;
    private RelativeLayout date;
    private TextView date_display;
    private RelativeLayout time;
    //private RelativeLayout time_layout;
    //private TimePicker timePicker;
    private TextView time_display;

    static final int TIME_DIALOG_ID = 999;
    private int hour;
    private int minute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        //set orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userProfile = (ImageView) findViewById(R.id.userCreatePageProfile);

        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.setCurrentActivity(this);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        if (user.getImageUrl()!=null) {
            Util.picassoLoadIcon(this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.avatar);
        }

        aData = new ArrayList<ActivityData>();

        mCntrlMngr.postGetActivityList(this);

        createAvtivityNameText = (TextView) findViewById(R.id.event_creation_first);

//        nextBtn = (Button) findViewById(R.id.event_next);
//        cancelBtn = (Button) findViewById(R.id.event_cancel);

        createNewEventBtn = (TextView) findViewById(R.id.create_new_event_activity);

        firstActivityLayout = (RelativeLayout) findViewById(R.id.first_img_layout);
        secondActivityLayout = (RelativeLayout) findViewById(R.id.second_img_layout);
        thirdActivityLayout = (RelativeLayout) findViewById(R.id.third_img_layout);

        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errText = (TextView) findViewById(R.id.error_sub);
        close_err = (ImageView) findViewById(R.id.err_close);

        close_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errLayout.setVisibility(View.GONE);
            }
        });

        firstActivityType = (TextView) findViewById(R.id.first);
        secondActivityType = (TextView) findViewById(R.id.second);
        thirdActivityType = (TextView) findViewById(R.id.third);

//        activity_icon_view = (ImageView) findViewById(R.id.event_icon);
//        activity_text_view = (TextView) findViewById(R.id.event_raid);
        final_act_icon_view = (ImageView) findViewById(R.id.event_icon1);
        final_act_text_view = (TextView) findViewById(R.id.event_raid1);

        customCurrentActivityList = new ArrayList<ActivityData>();

        final_create_event_firstcard = (RelativeLayout) findViewById(R.id.final_create_event_firstcard);

//        firstActivityTypeBckgrnd = (ImageView) findViewById(R.id.first_backgrnd);
//        secondActivityTypeBckgrnd = (ImageView) findViewById(R.id.second_backgrnd);
//        thirdActivityTypeBckgrnd = (ImageView) findViewById(R.id.third_backgrnd);

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

        vf = (ViewFlipper) findViewById( R.id.viewFlipper );

        firstActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                firstActivityTypeBckgrnd.setVisibility(View.VISIBLE);
//                secondActivityTypeBckgrnd.setVisibility(View.GONE);
//                thirdActivityTypeBckgrnd.setVisibility(View.GONE);
//                if (firstActivityType.getText().toString() != null && (!firstActivityType.getText().toString().isEmpty())) {
//                activity_icon_view.setImageResource(R.drawable.img_i_c_o_n_r_a_i_d);
//                activity_text_view.setText(Constants.ACTIVITY_RAID);
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
                vf.showNext();
//                }
            }
        });

        secondActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                firstActivityTypeBckgrnd.setVisibility(View.GONE);
//                secondActivityTypeBckgrnd.setVisibility(View.VISIBLE);
//                thirdActivityTypeBckgrnd.setVisibility(View.GONE);
//                if (secondActivityType.getText().toString()!=null && (!secondActivityType.getText().toString().isEmpty())) {
//                activity_icon_view.setImageResource(R.drawable.icon_weeklies);
//                activity_text_view.setText(Constants.ACTIVITY_WEEKLY);
                final_act_icon_view.setImageResource(R.drawable.icon_weeklies);
                final_act_text_view.setText(Constants.ACTIVITY_WEEKLY);
                ArrayList<ActivityData> secActivity = mCntrlMngr.getCustomActivityList(secondActivityType.getText().toString());
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
                vf.showNext();
            }
        });

        thirdActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                firstActivityTypeBckgrnd.setVisibility(View.GONE);
//                secondActivityTypeBckgrnd.setVisibility(View.GONE);
//                thirdActivityTypeBckgrnd.setVisibility(View.VISIBLE);
//                if (thirdActivityType.getText().toString()!=null && (!thirdActivityType.getText().toString().isEmpty())) {
//                activity_icon_view.setImageResource(R.drawable.icon_crucible);
//                activity_text_view.setText(Constants.ACTIVITY_CRUCIBLE);
                final_act_icon_view.setImageResource(R.drawable.icon_crucible);
                final_act_text_view.setText(Constants.ACTIVITY_CRUCIBLE);
                    ArrayList<ActivityData> thirdActivity = mCntrlMngr.getCustomActivityList(thirdActivityType.getText().toString());
                    vf.showNext();
                    if (thirdActivity != null) {
                        setActivityRecyclerView(thirdActivity);
                    }
                //}
            }
        });

        final_create_event_firstcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progress = new ProgressDialog(this);
        // CAST THE LINEARLAYOUT HOLDING THE MAIN PROGRESS (SPINNER)
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        createNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vf.getDisplayedChild() == 2) {
//                    Toast.makeText(CreateNewEvent.this, "Create new Event",
//                            Toast.LENGTH_SHORT).show();
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
                                    //disable clicks
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                    progress.setTitle("Creating New Event");
//                                    progress.show();
                                    linlaHeaderProgress.setVisibility(View.VISIBLE);
                                    String dateTime = getCreateEventDateTime();
                                    mCntrlMngr.postCreateEvent(activityId, creator_id, minP, maxP, dateTime, CreateNewEvent.this);
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

        calendar_layout = (RelativeLayout) findViewById(R.id.calendar_layout);
        calendar = (MaterialCalendarView) findViewById(R.id.calendar);
        date = (RelativeLayout) findViewById(R.id.date);
        time = (RelativeLayout) findViewById(R.id.time);
        date_display = (TextView) findViewById(R.id.date_text);

        calendar_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar_layout.setVisibility(View.GONE);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar_layout.setVisibility(View.VISIBLE);
                intializeCalendar();
            }
        });

        //time_layout = (RelativeLayout) findViewById(R.id.time_layout);
        //timePicker = (TimePicker) findViewById(R.id.timePicker);
        time = (RelativeLayout) findViewById(R.id.time);
        time_display = (TextView) findViewById(R.id.time_text);
//        time_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                time_layout.setVisibility(View.GONE);
//            }
//        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //time_layout.setVisibility(View.VISIBLE);

                showDialog(TIME_DIALOG_ID);

//                TimePickerDialog myTPDialog = new TimePickerDialog(CreateNewEvent.this,timePickerListener,0,0,false);
//                myTPDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    public void onDismiss(DialogInterface dialog) {
//                        time_layout.setVisibility(View.GONE);
//                    }
//                });
//                myTPDialog.show();
                //intializeTime();
            }
        });
    }

    private String getCreateEventDateTime() {
        if(date_display!=null) {
            if ((!date_display.getText().toString().equalsIgnoreCase(getResources().getString(R.string.date_default))) && (!time_display.getText().toString().equalsIgnoreCase(getResources().getString(R.string.time_default)))) {
                try {
                    //String t = changeTimeFormat(time_display.getText().toString());
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
    protected Dialog onCreateDialog(int id){
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                TimePickerDialog tpd = new TimePickerDialog(this, timePickerListener, hour, minute,false);
                tpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //time_layout.setVisibility(View.GONE);
                    }
                });
                return tpd;
        }
        return null;

    }

    protected TimePickerDialog.OnTimeSetListener timePickerListener =  new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;
            // set current time into textview
            String aTime = Util.updateTime(hour, minute);
//            String t = new StringBuilder().append(padding_str(hour)).append(":").append(padding_str(minute)).toString();
            if(aTime!=null) {
                time_display.setText(aTime);
            }
            //time_layout.setVisibility(View.GONE);
        }
    };
//            .setOnDismissListener(new DialogInterface.OnDismissListener() {
//        public void onDismiss(DialogInterface dialog) {
//            // Cancel code here
//        }
//    });

    private void intializeCalendar() {
//        calendar.setShowWeekNumber(false);
//        calendar.setFirstDayOfWeek(2);
//
//        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.app_theme_color));

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView materialCalendarView, CalendarDay calendarDay, boolean b) {
                int m= calendarDay.getMonth();
                int d = calendarDay.getDay();
                if(m==1 && d>29) {
                    m = 3;
                    if (d==30){
                        d = 1;
                    } else {
                        d = 2;
                    }
                } else {
                    m= m + 1;
                }
                date_display.setText(m+"-"+d
                        +"-"+calendarDay.getYear());
            }
        });
    }

    public void showError(String err) {
        dismissProgressBar();
        errLayout.setVisibility(View.VISIBLE);
        errText.setText(err);
    }

    public GradientDrawable backgroundWithBorder(int bgcolor, int brdcolor) {

        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(bgcolor);
        gdDefault.setStroke(2, brdcolor);
        gdDefault.setCornerRadii(new float[] { Util.dpToPx(2, this), Util.dpToPx(2, this), 0, 0, 0, 0,
                Util.dpToPx(2, this), Util.dpToPx(2, this) });

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

                if(aLight>0) {
                    aFinalLight = String.valueOf(aList.get(position).getActivityLight() + " Light");
                }

                if (diff.equalsIgnoreCase("null")){
                    diff = "";
                } else {
                    diff = " - " +diff;
                }

                final String activity_custom_name = aList.get(position).getActivitySubtype()+ diff +" "+ aFinalLight;

//                if (iconUrl!=null){
//                    Util.picassoLoadIcon(CreateNewEvent.this, holder.activity_icon, iconUrl, R.dimen.activity_profile_icon_hgt, R.dimen.activity_profile_icon_width, R.drawable.img_traveler_badge_icon);
//                }

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

                for (int i=0; i<checkpointActList.size(); i++) {
                    if (checkpointActList.get(i).getActivityCheckpoint()!=null && !checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase("null")) {
                        //adapterCheckpoint.add(checkpointActList.get(i).getActivityCheckpoint());
                        checkpointItems.add(checkpointActList.get(i).getActivityCheckpoint());
                        //currentCheckpointActivityPosition = i;
                    }
                }



                adapterCheckpoint = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, checkpointItems){

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

                    public View getDropDownView(int position, View convertView,ViewGroup parent) {

                        //View v = super.getDropDownView(position, convertView,parent);

                        return getCustomView(position, convertView, parent, checkpointItems);

//                        ((TextView) v).setGravity(Gravity.CENTER);
//
//                        v.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
//                        ((TextView) v).setTextColor(getResources().getColor(R.color.trimbe_white));
//                        ((TextView) v).setHeight(Util.dpToPx(50, CreateNewEvent.this));
//
//                        return v;

                    }
                };
                adapterCheckpoint.setDropDownViewResource(R.layout.empty_layout);
                dropdown.setAdapter(adapterCheckpoint);
//                adapterCheckpoint.clear();
//                adapterCheckpoint.addAll(checkpointItems);
                adapterCheckpoint.notifyDataSetChanged();
            }
        }
        createAvtivityNameText.setText(activity_custom_name);
        createNewEventBtn.setBackgroundColor(getResources().getColor(R.color.create_new_event_green));
        vf.showNext();
    }

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<String> checkpoint;

        public CustomAdapter() {
            if (checkpoint==null) {
                checkpoint = new ArrayList<String>();
            }
        }

        protected void addItem(ArrayList<String> a) {
            this.checkpoint.addAll(a);
        }

        @Override
        public int getCount() {
            return checkpoint.size();
        }

        @Override
        public Object getItem(int position) {
            return checkpoint.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, checkpoint);
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
            //EventData ed = (EventData)data;
            dismissProgressBar();
//            progress.dismiss();
            finish();
        }
//        mAdapter.addItem(aData);
//        mAdapter.notifyDataSetChanged();
    }

    public void dismissProgressBar(){
        //enable screen clicks
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // To dismiss the dialog
        linlaHeaderProgress.setVisibility(View.GONE);
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
            if(vf.getDisplayedChild()>0){
                if(calendar_layout.getVisibility()==View.VISIBLE){
                    calendar_layout.setVisibility(View.GONE);
                }else {
                    vf.setDisplayedChild(vf.getDisplayedChild() - 1);
                    createNewEventBtn.setBackgroundColor(getResources().getColor(R.color.event_user_image_background));
                }
            }else {
                finish();
            }
        }else {
            finish();
        }
    }
}
