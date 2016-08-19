package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import co.crossroadsapp.destiny.data.ActivityData;
import co.crossroadsapp.destiny.data.CurrentEventDataHolder;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;


public class AddFinalActivity extends BaseActivity implements Observer, AdapterView.OnItemSelectedListener {

    private ControlManager mCntrlMngr;
    private UserData user;
    private ImageView back;
    ImageView actIcon;
    ImageView background;
    TextView modifier;
    TextView actSubtype;
    TextView actSubtypeDropdownText;
    TextView checkpointText;
    TextView detailText;
    TextView levelTextView;
    private RelativeLayout checkpointLayout;
    private RelativeLayout subtypeLayout;
    private RelativeLayout detailLayout;
    private ArrayList<ActivityData> activity;
    private ArrayList<ActivityData> checkpointActList;
    private ArrayList<String> checkpointItems;
    private ArrayAdapter<String> adapterCheckpoint;
    private Spinner dropdownCheckpoint;
    private ArrayList<ActivityData> actSubList;
    private ArrayList<String> actSubTypeList;
    private Spinner dropdownSubtype;

    private RelativeLayout date;
    private TextView date_display;
    private RelativeLayout time;
    private TextView time_display;

    static final int TIME_DIALOG_ID = 999;
    static final int DATE_DIALOG_ID = 888;
    private int hour;
    private int minute;
    private DatePickerDialog mDatePickerDai;
    private TimePickerDialog tpd;
    private ArrayList<String> tagList;
    private String activityType;
    private String subActType;
    private String subtypeDifficulty;
    private Spinner dropdownDetails;
    private ArrayList<ActivityData> tagActList;
    private ActivityData finalAct;
    private TextView createNewEventBtn;
    private ArrayAdapter<String> adapterTags;
    private ArrayAdapter<String> adapterSubtypes;
    private ImageView tagDropdownArw;
    private ImageView checkpointDropdownArw;
    private ImageView subtypeDropdownArw;
    private TextView lightTextView;
    private RelativeLayout modifiersLayout;
    private RelativeLayout modifiersLayout2;
    private RelativeLayout modifiersLayout3;
    private RelativeLayout levelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_final);

        setTRansparentStatusBar();
        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.setCurrentActivity(this);
        //mCntrlMngr.getAllActivities(this);

        Bundle b = getIntent().getExtras();
        //user = b.getParcelable("userdata");

        user = mCntrlMngr.getUserData();

        boolean ads=false;
        String adP=null;

        if(b!=null) {
            ads = b.getBoolean("adcard");
            adP = b.getString("adCardId");
        }

        back = (ImageView) findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        actIcon = (ImageView) findViewById(R.id.event_icon1);

        background = (ImageView) findViewById(R.id.background);

        actSubtype = (TextView) findViewById(R.id.act_subtype);

        levelTextView = (TextView) findViewById(R.id.level_text);

        levelLayout = (RelativeLayout) findViewById(R.id.level_layout);

        lightTextView = (TextView) findViewById(R.id.light);

        actSubtypeDropdownText = (TextView) findViewById(R.id.act_subtype_text);

        subtypeLayout = (RelativeLayout) findViewById(R.id.final_create_event_firstcard);
        detailLayout = (RelativeLayout) findViewById(R.id.event_creation_detail_layout);

        activity = mCntrlMngr.getCurrentActivityList();

        //modifier = (TextView) findViewById(R.id.modifier);

        checkpointText = (TextView) findViewById(R.id.ad_checkpoint_text);

        checkpointLayout = (RelativeLayout) findViewById(R.id.event_creation_checkpoint_layout);

        modifiersLayout = (RelativeLayout) findViewById(R.id.modifier_layout);
        modifiersLayout2 = (RelativeLayout) findViewById(R.id.modifier_layout2);
        modifiersLayout3 = (RelativeLayout) findViewById(R.id.modifier_layout3);

        detailText = (TextView) findViewById(R.id.ad_detail_text);
        subtypeDropdownArw = (ImageView) findViewById(R.id.subtype_downarw);
        checkpointDropdownArw = (ImageView) findViewById(R.id.checkpoint_downarw);
        tagDropdownArw = (ImageView) findViewById(R.id.tag_downarw);

        //checkpoint dropdown
        dropdownCheckpoint = (Spinner)findViewById(R.id.event_creation_checkpoint);
        checkpointItems = new ArrayList<String>();
        dropdownCheckpoint.setOnItemSelectedListener(AddFinalActivity.this);

        //activitySubtype dropdown
        dropdownSubtype = (Spinner)findViewById(R.id.event_creation_subtype);
        actSubTypeList = new ArrayList<String>();
        dropdownSubtype.setOnItemSelectedListener(AddFinalActivity.this);

        //details
        dropdownDetails = (Spinner)findViewById(R.id.event_creation_detail);
        tagList = new ArrayList<String>();
        dropdownDetails.setOnItemSelectedListener(AddFinalActivity.this);

        refreshActivityUI();

        if(ads) {
            ActivityData ad= mCntrlMngr.getAdsActivity(adP);
            if(ad!=null) {
                String subtypeDiff = ad.getActivitySubtype();
                if(!ad.getActivityDifficulty().isEmpty()) {
                    subtypeDiff = subtypeDiff + " - " + ad.getActivityDifficulty();
                }
                dropdownSubtype.setSelection(actSubTypeList.indexOf(subtypeDiff));
                if(!ad.getActivityCheckpoint().isEmpty()) {
                    dropdownCheckpoint.setSelection(checkpointItems.indexOf(ad.getActivityCheckpoint()));
                }
                if(!ad.getTag().isEmpty()) {
                    dropdownDetails.setSelection(tagList.indexOf(ad.getTag()));
                }
            }
        }

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

        createNewEventBtn = (TextView) findViewById(R.id.create_new_event_activity);

        createNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalAct!=null) {
                    hideProgressBar();
                    showProgressBar();
                                    //linlaHeaderProgress.setVisibility(View.VISIBLE);
                    String dateTime = getCreateEventDateTime();
                    mCntrlMngr.postCreateEvent(finalAct.getId(), user.getUserId(), finalAct.getMinPlayer(), finalAct.getMaxPlayer(), dateTime, AddFinalActivity.this);
                }
            }
        });

//        GradientDrawable gd = new GradientDrawable();
//        //gd.setColor(); // Changes this drawbale to use a single color instead of a gradient
//        gd.setCornerRadius(5);
//        gd.setStroke(2, 0xFFFFFFFF);
//        modifier.setBackgroundDrawable(gd);
//
//        //load activity icon
//        Util.picassoLoadImageWithoutMeasurement(getApplicationContext(), actIcon, activity.get(0).getActivityIconUrl(), R.drawable.icon_ghost_default);
//
//        //load background image
//        Util.picassoLoadImageWithoutMeasurement(getApplicationContext(), background, activity.get(0).getaImagePath(), R.drawable.img_b_g_d_e_f_a_u_l_t);
//
//        actSubtype.setText(activity.get(0).getActivitySubtype());
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

    private void refreshActivityUI() {

        String actIconUrl = null;
        String backg = null;
        String subType = null;
        int level;
        int light;
        String description = null;
        String levelText = null;
        String lightText = null;
        String subtypeDiff = null;
        String check = null;
        String localTag = null;
        String location = null;
        String next = null;
        ArrayList<String> modi = new ArrayList<String>();
        ArrayList<String> bonus = new ArrayList<String>();

        if (finalAct != null && !finalAct.getActivitySubtype().isEmpty()) {
            actIconUrl = finalAct.getActivityIconUrl();
            backg = finalAct.getaImagePath();
            subType = finalAct.getActivitySubtype();
            level = finalAct.getActivityLevel();
            light = finalAct.getActivityLight();
            subtypeDiff = finalAct.getActivitySubtype();
            if (!finalAct.getActivityDifficulty().isEmpty()) {
                subtypeDiff = subtypeDiff + " - " + finalAct.getActivityDifficulty();
            }
            check = finalAct.getActivityCheckpoint();
            localTag = finalAct.getTag();
            description = finalAct.getaDescription();
            location = finalAct.getaLocation();
            if (finalAct.getModifierList() != null && !finalAct.getModifierList().isEmpty()) {
                for (int y = 0; y < finalAct.getModifierList().size(); y++) {
                    modi.add(finalAct.getModifierList().get(y).getaModifierName());
                }
            }
            if (finalAct.getBonusList() != null && !finalAct.getBonusList().isEmpty()) {
                for (int y = 0; y < finalAct.getBonusList().size(); y++) {
                    bonus.add(finalAct.getBonusList().get(y).getaBonusName());
                }
            }
        } else {
            actIconUrl = activity.get(0).getActivityIconUrl();
            backg = activity.get(0).getaImagePath();
            subType = activity.get(0).getActivitySubtype();
            level = activity.get(0).getActivityLevel();
            light = activity.get(0).getActivityLight();
            description = activity.get(0).getaDescription();
            location = activity.get(0).getaLocation();
            if (activity.get(0).getModifierList() != null && !activity.get(0).getModifierList().isEmpty()) {
                for (int y = 0; y < activity.get(0).getModifierList().size(); y++) {
                    modi.add(activity.get(0).getModifierList().get(y).getaModifierName());
                }
            }
            if (activity.get(0).getBonusList() != null && !activity.get(0).getBonusList().isEmpty()) {
                for (int y = 0; y < activity.get(0).getBonusList().size(); y++) {
                    bonus.add(activity.get(0).getBonusList().get(y).getaBonusName());
                }
            }
        }

        lightTextView.setText("");

        if (level != 0) {
            levelText = "LEVEL " + level;
            if (light != 0) {
                //next = "<font color='#ffc600'>\u2726 </font>";
                levelText = levelText + "  Recommended Light: ";
                lightTextView.setText("\u2726" + light);
            } else {
                levelText = levelText + "  " + location;
            }
        } else {
            levelText = description;
        }

        if(!levelText.isEmpty()) {
            levelLayout.setVisibility(View.VISIBLE);
            levelTextView.setText(levelText);
        } else {
            levelLayout.setVisibility(View.GONE);
        }

        //show modifiers and bonuses
        modifiersLayout.removeAllViews();
        modifiersLayout2.removeAllViews();
        modifiersLayout3.removeAllViews();
        Random rnd = new Random();
        int prevTextViewId = 0;
        int pad = Util.dpToPx(5, AddFinalActivity.this);
        if (!modi.isEmpty() || !bonus.isEmpty()) {
            int listSize = modi.size() + bonus.size();
            if (listSize < 11) {
                for (int i = 0; i < listSize; i++) {
                    final TextView textView = new TextView(this);
                    if (i < modi.size()) {
                        textView.setText(modi.get(i));
                    } else {
                        textView.setText(bonus.get(listSize - i - 1));
                    }
                    textView.setTextColor(getResources().getColor(R.color.trimbe_white));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
                    textView.setAllCaps(true);
                    textView.setPadding(pad, pad, pad, pad);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setCornerRadius(5);
                    gd.setStroke(2, 0xFFFFFFFF);
                    textView.setBackgroundDrawable(gd);

                    int curTextViewId = prevTextViewId + 1;
                    textView.setId(curTextViewId);
                    final RelativeLayout.LayoutParams params =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);

                    params.addRule(RelativeLayout.RIGHT_OF, prevTextViewId);
                    params.leftMargin = pad;
                    textView.setLayoutParams(params);

                    prevTextViewId = curTextViewId;
                    modifiersLayout.setVisibility(View.GONE);
                    modifiersLayout2.setVisibility(View.GONE);
                    modifiersLayout3.setVisibility(View.GONE);
                    if (i < 2) {
                        modifiersLayout3.setVisibility(View.VISIBLE);
                        modifiersLayout3.addView(textView, params);
                    } else if (i < 6) {
                        modifiersLayout2.setVisibility(View.VISIBLE);
                        modifiersLayout2.addView(textView, params);
                    } else {
                        modifiersLayout.setVisibility(View.VISIBLE);
                        modifiersLayout.addView(textView, params);
                    }
                }
        }
        }

        //load activity icon
        Util.picassoLoadImageWithoutMeasurement(getApplicationContext(), actIcon, actIconUrl, R.drawable.icon_ghost_default);

        //load background image
        Util.picassoLoadImageWithoutMeasurement(getApplicationContext(), background, backg, R.drawable.img_b_g_d_e_f_a_u_l_t);

        actSubtype.setText(subType);
        actSubtype.setAllCaps(true);

        if(finalAct==null) {
            //subtypes and difficulty level
            actSubList = mCntrlMngr.getCustomActivityList(activity.get(0).getActivityType());
            activityType = actSubList.get(0).getActivityType();
            actSubTypeList = new ArrayList<String>();
            if (actSubList != null && !actSubList.isEmpty()) {
                for (int n = 0; n < actSubList.size(); n++) {
                    String subtypeDifficultyName = "";
                        if (actSubList.get(n).getActivitySubtype() != null && !actSubList.get(n).getActivitySubtype().isEmpty()) {
                            subActType = actSubList.get(n).getActivitySubtype();
                            subtypeDifficultyName = subActType;
                            if (actSubList.get(n).getActivityDifficulty() != null && !actSubList.get(n).getActivityDifficulty().isEmpty()) {
                                subtypeDifficulty = actSubList.get(n).getActivityDifficulty();
                                subtypeDifficultyName = subtypeDifficultyName + " - " + subtypeDifficulty;
                            }
                        }
                    actSubTypeList.add(subtypeDifficultyName);
                }
            }

            actSubTypeList = Util.removeListDuplicates(actSubTypeList);
            sortList(actSubTypeList);

            //checkpoints
            if (checkpointActList == null) {
                checkpointActList = new ArrayList<ActivityData>();
            }else {
                checkpointActList.clear();
            }
//            checkpointActList = mCntrlMngr.getCheckpointActivityList(activity.get(0).getActivitySubtype(), activity.get(0).getActivityDifficulty());

            tagList = new ArrayList<String>();

//            for (int i = 0; i < checkpointActList.size(); i++) {
//                if (checkpointActList.get(i).getActivityCheckpoint() != null && !checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase("null") && !checkpointActList.get(i).getActivityCheckpoint().isEmpty()) {
//                    checkpointItems.add(checkpointActList.get(i).getActivityCheckpoint());
//                }
//            }
//
//            //remove duplicates
//            checkpointItems = Util.removeListDuplicates(checkpointItems);
//            if(checkpointItems.isEmpty()) {
//                checkpointActList.clear();
//            }
//
//            getTagList(null);

            updateDrawerSubtype(actSubTypeList);
//            updateDrawer(checkpointItems);
//            updateDrawerTags(tagList);
        }
    }

    private void sortList(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    private void getTagList(String checkPoint) {
        String cp = "";
        if(checkpointItems!=null && !checkpointItems.isEmpty()) {
            if(checkPoint!=null) {
                cp = checkPoint;
            }else {
                cp = checkpointItems.get(0);
            }
        }
        tagList.clear();
        tagActList = new ArrayList<ActivityData>();
        if (checkpointActList != null && !checkpointActList.isEmpty()) {
            for (int i = 0; i < checkpointActList.size(); i++) {
                if (checkpointActList.get(i).getActivitySubtype() != null && !checkpointActList.get(i).getActivitySubtype().isEmpty()) {
                    //subActType = actSubList.get(n).getActivitySubtype();
                    if (checkpointActList.get(i).getActivityDifficulty() != null && !checkpointActList.get(i).getActivityDifficulty().isEmpty()) {
                        if (subActType.equalsIgnoreCase(checkpointActList.get(i).getActivitySubtype()) && subtypeDifficulty.equalsIgnoreCase(checkpointActList.get(i).getActivityDifficulty())) {
                            if (checkpointActList.get(i).getActivityCheckpoint() != null && !checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase("null") && !checkpointActList.get(i).getActivityCheckpoint().isEmpty()) {
                                if (checkpointActList.get(i).getActivitySubtype().equalsIgnoreCase(subActType) && checkpointActList.get(i).getActivityDifficulty().equalsIgnoreCase(subtypeDifficulty)) {
                                    if (checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase(cp)) {
                                        if (checkpointActList.get(i).getTag() != null) {
                                            if(!checkpointActList.get(i).getTag().isEmpty()) {
                                                tagList.add(checkpointActList.get(i).getTag().replace("#",""));
                                            }else {
                                                tagList.add("None");
                                            }
                                            tagActList.add(checkpointActList.get(i));
                                        }
                                    }
                                }
                            } else {
                                if (checkpointActList.get(i).getActivitySubtype().equalsIgnoreCase(subActType)) {
                                    if (checkpointActList.get(i).getTag() != null) {
                                        if(!checkpointActList.get(i).getTag().isEmpty()) {
                                            tagList.add(checkpointActList.get(i).getTag().replace("#",""));
                                        }else {
                                            tagList.add("None");
                                        }
                                        tagActList.add(checkpointActList.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        if (checkpointActList.get(i).getActivityCheckpoint() != null && !checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase("null") && !checkpointActList.get(i).getActivityCheckpoint().isEmpty()) {
                            if (checkpointActList.get(i).getActivitySubtype().equalsIgnoreCase(subActType)) {
                                if (checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase(cp)) {
                                    if (checkpointActList.get(i).getTag() != null) {
                                        if(!checkpointActList.get(i).getTag().isEmpty()) {
                                            tagList.add(checkpointActList.get(i).getTag().replace("#",""));
                                        }else {
                                            tagList.add("None");
                                        }
                                        tagActList.add(checkpointActList.get(i));
                                    }
                                }
                            }
                        } else {
                            if (checkpointActList.get(i).getActivitySubtype().equalsIgnoreCase(subActType)) {
                                if (checkpointActList.get(i).getTag() != null) {
                                    if(!checkpointActList.get(i).getTag().isEmpty()) {
                                        tagList.add(checkpointActList.get(i).getTag().replace("#",""));
                                    }else {
                                        tagList.add("None");
                                    }
                                    tagActList.add(checkpointActList.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }
        if(tagList.isEmpty()) {
            tagList.add("None");
        }
        updateDrawerTags(tagList);
    }

    private void getCheckpointList() {
        //checkpoints
        if(checkpointActList==null) {
            checkpointActList = new ArrayList<ActivityData>();
        }else {
            checkpointActList.clear();
        }
        checkpointActList = mCntrlMngr.getCheckpointActivityList(subActType, subtypeDifficulty);

        checkpointItems.clear();

        for (int i = 0; i < checkpointActList.size(); i++) {
            if (checkpointActList.get(i).getActivityCheckpoint() != null && !checkpointActList.get(i).getActivityCheckpoint().equalsIgnoreCase("null") && !checkpointActList.get(i).getActivityCheckpoint().isEmpty()) {
                checkpointItems.add(checkpointActList.get(i).getActivityCheckpoint());
            }
        }

        //remove duplicates
        checkpointItems = Util.removeListDuplicates(checkpointItems);
        sortList(checkpointItems);

        updateDrawer(checkpointItems);

        getTagList(null);
    }

    private void updateDrawer(final ArrayList<String> dataList) {
        if(dataList !=null && !dataList.isEmpty()) {
            if (checkpointLayout != null) {
                checkpointLayout.setVisibility(View.VISIBLE);
            }
            if(dataList.size()>1) {
                checkpointDropdownArw.setVisibility(View.VISIBLE);
                adapterCheckpoint = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataList) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        checkpointText.setText(((TextView) v).getText());
                        ((TextView) v).setVisibility(View.GONE);
                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getCustomView(position, convertView, parent, dataList);
                    }
                };
                adapterCheckpoint.setDropDownViewResource(R.layout.empty_layout);
                dropdownCheckpoint.setEnabled(true);
                dropdownCheckpoint.setAdapter(adapterCheckpoint);
                adapterCheckpoint.notifyDataSetChanged();
            }else {
                dropdownCheckpoint.setEnabled(false);
                checkpointText.setText(dataList.get(0).toString());
                checkpointDropdownArw.setVisibility(View.GONE);
            }
        } else {
            //todo checkpoint layout visibility gone
            if(checkpointLayout!=null) {
                checkpointLayout.setVisibility(View.GONE);
            }
        }
    }

    private void updateDrawerTags(final ArrayList<String> dataList) {
        if(dataList !=null && !dataList.isEmpty()) {
            if (dataList.size() > 1) {
                tagDropdownArw.setVisibility(View.VISIBLE);
                adapterTags = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataList) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        if (((TextView) v).getText().toString().equalsIgnoreCase("None")) {
                            detailText.setText("Details (Optional)");
                        } else {
                            detailText.setText(((TextView) v).getText());
                        }
                        ((TextView) v).setVisibility(View.GONE);
                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getCustomView(position, convertView, parent, dataList);
                    }
                };
                adapterTags.setDropDownViewResource(R.layout.empty_layout);
                dropdownDetails.setEnabled(true);
                dropdownDetails.setAdapter(adapterTags);
                adapterTags.notifyDataSetChanged();
            } else {
                if (dataList.get(0).toString().equalsIgnoreCase("None")) {
                    detailText.setText("Details (Optional)");
                }
                dropdownDetails.setEnabled(false);
                tagDropdownArw.setVisibility(View.GONE);
            }
        }
    }

    private void updateDrawerSubtype(final ArrayList<String> dataList) {
        if(dataList !=null && !dataList.isEmpty()) {
            if (dataList.size() > 1) {
                subtypeDropdownArw.setVisibility(View.VISIBLE);
                adapterSubtypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataList) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                            if(activityType!=null && !activityType.isEmpty()) {
                                actSubtypeDropdownText.setText(activityType + " - " + ((TextView) v).getText());
                            }
                        ((TextView) v).setVisibility(View.GONE);
                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        return getCustomView(position, convertView, parent, dataList);
                    }
                };
                adapterSubtypes.setDropDownViewResource(R.layout.empty_layout);
                dropdownSubtype.setEnabled(true);
                dropdownSubtype.setAdapter(adapterSubtypes);
                adapterSubtypes.notifyDataSetChanged();
            } else {
                actSubtypeDropdownText.setText(dataList.get(0).toString());
                dropdownSubtype.setEnabled(true);
                subtypeDropdownArw.setVisibility(View.GONE);
            }
        }
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, ArrayList<String> adapterCheckpoint) {
        LayoutInflater inflater=getLayoutInflater();
        View row=inflater.inflate(R.layout.fragment_checkpoint, parent, false);
        CardView card = (CardView)row.findViewById(R.id.activity_checkpoint_card);
        RelativeLayout cardLayout = (RelativeLayout)row.findViewById(R.id.activity_checkpoint_card_frag);
        cardLayout.setBackgroundColor(getResources().getColor(R.color.consoleAddColor));
        card.setCardBackgroundColor(getResources().getColor(R.color.freelancer_background));
        TextView label=(TextView)row.findViewById(R.id.activity_checkpoint_text);
        if (adapterCheckpoint!=null) {
            label.setText(adapterCheckpoint.get(position));
        }
        return row;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        switch (id) {
            case TIME_DIALOG_ID:
                Calendar c = Calendar.getInstance();
                // set time picker as current time
                tpd = new TimePickerDialog(this, timePickerListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),false);

                tpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                return tpd;

            case DATE_DIALOG_ID:
                Date d = Util.getCurrentCalendar();
                mDatePickerDai = new DatePickerDialog(this, myDateListener, d.getYear(), d.getMonth(), d.getDate());
                //disable previous dates for datepicker
                mDatePickerDai.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                return mDatePickerDai;
            //return new DatePickerDialog(this, myDateListener, d.getYear(), d.getMonth(), d.getDate());

            default: return null;
        }
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
//            date_display.setText(arg1 + "-" + arg2+1
//                    + "-" + arg3);
            //showDate(arg1, arg2+1, arg3);

            int m = arg2;
            int d = arg3;
            int y = arg1;

            //todo will change with new calendar but for now hacking to reset date if user selects previous date
            String date=null;
            Date currentDate = Util.getCurrentCalendar();

            Date selectedDate = new Date(y, m, d);

            if(selectedDate.compareTo(currentDate)<0){
                Toast.makeText(getApplicationContext(), "Cannot set previous date.", Toast.LENGTH_SHORT).show();
                m =  currentDate.getMonth();
                d = currentDate.getDate();
                y = currentDate.getYear();

                //set datepicker view to current date
                updateDatePickerCalendar();
            }
//            else {
            date = m + "-" + d
                    + "-" + y;
//            }

            if (m == 1 && d > 29) {
                m = 3;
                if (d == 30) {
                    d = 1;
                } else {
                    d = 2;
                }
            } else {
                m = m + 1;
            }
            date_display.setText(m + "-" + d
                    + "-" + y);
            Util.checkTimePicker(date_display, time_display, AddFinalActivity.this);
        }
    };

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

    protected TimePickerDialog.OnTimeSetListener timePickerListener =  new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            Calendar c = Calendar.getInstance();
            hour = selectedHour;
            minute = selectedMinute;
            String aTime=null;
            int currHour = c.get(Calendar.HOUR_OF_DAY);
            int currMin = c.get(Calendar.MINUTE);

            if(Util.isPresentDay(AddFinalActivity.this, date_display)) {
                if (hour >= currHour) {
                    if (hour == currHour && minute < currMin) {
                        aTime = Util.getCurrentTime(currHour, currMin, AddFinalActivity.this);
                    } else {
                        // set current time into textview
                        aTime = Util.updateTime(hour, minute);
                    }
                }else {
                    aTime = Util.getCurrentTime(currHour, currMin, AddFinalActivity.this);
                }
            }else {
                // set current time into textview
                aTime = Util.updateTime(hour, minute);
            }
            if (aTime != null) {
                time_display.setText(aTime);
            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.event_creation_subtype:
                filterReqList(position);
                break;
            case R.id.event_creation_checkpoint:
                filterReqTagList(position);
                break;
            case R.id.event_creation_detail:
                setFinalAct(position);
                break;
        }
    }

    private void filterReqTagList(int position) {
        String chckpoint = checkpointItems.get(position);
        getTagList(chckpoint);
    }

    private void filterReqList(int position) {
        String sub = actSubTypeList.get(position);
        String[] parts = sub.split("\\-");
        subActType = parts[0];
        subActType = subActType.trim();
        subtypeDifficulty = "";
        if(parts.length>1) {
            subtypeDifficulty = parts[1];
            subtypeDifficulty = subtypeDifficulty.trim();
        }
        getCheckpointList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    @Override
//    public void onBackPressed() {
//        if (vf!=null) {
//            if(adcardEventId!=null && (!adcardEventId.isEmpty())) {
//                launchListActivityAndFinish();
//            }else if(vf.getDisplayedChild()>0){
//                setVfPrevAnimation();
//                //todo fix the hack to reset date, time and checkpoint
//                setCalendarCheckpointToDefault();
//                vf.setDisplayedChild(vf.getDisplayedChild() - 1);
//            }else {
//                vf.clearAnimation();
//                launchListActivityAndFinish();
//            }
//        }else {
//            launchListActivityAndFinish();
//        }
//    }

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

    private void launchEventDetailAndFinish(EventData eData) {
        CurrentEventDataHolder ins = CurrentEventDataHolder.getInstance();
        ins.setData(eData);
        Intent i=new Intent (this, EventDetailActivity.class);
        startActivity(i);
        finish();
    }

    public void showError(String err) {
        hideProgressBar();
        setErrText(err);
    }

    public void setFinalAct(int position) {
        if(!tagActList.isEmpty()) {
            this.finalAct = tagActList.get(position);
        }
        refreshActivityUI();
    }

    @Override
    public void update(Observable observable, Object data) {
        hideProgressBar();
        if(data!=null) {
            launchEventDetailAndFinish((EventData)data);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
