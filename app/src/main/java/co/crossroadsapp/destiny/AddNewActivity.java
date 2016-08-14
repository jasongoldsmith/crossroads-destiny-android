package co.crossroadsapp.destiny;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;

import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;

public class AddNewActivity extends BaseActivity implements Observer {

    private ControlManager mCntrlMngr;
    private UserData user;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        setTRansparentStatusBar();

        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.setCurrentActivity(this);
        //mCntrlMngr.getAllActivities(this);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");
        boolean ads= b.getBoolean("adcard");
        String adP = b.getString("adCardId");

        checkIfAdFlow(ads, adP);

        back = (ImageView) findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Raid
        ImageView featuredAct = (ImageView) findViewById(R.id.first_act);
        featuredAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Featured");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView raiddAct = (ImageView) findViewById(R.id.second_act);
        raiddAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Raid");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView arenaAct = (ImageView) findViewById(R.id.third_act);
        arenaAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Arena");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView crucibleAct = (ImageView) findViewById(R.id.fourth_act);
        crucibleAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Crucible");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView strikeAct = (ImageView) findViewById(R.id.fifth_act);
        strikeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Strike");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView patrolAct = (ImageView) findViewById(R.id.sixth_act);
        patrolAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Patrol");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView storyAct = (ImageView) findViewById(R.id.seventh_act);
        storyAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Story");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView questAct = (ImageView) findViewById(R.id.eigth_act);
        questAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Quest");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });

        ImageView exoticAct = (ImageView) findViewById(R.id.ninth_act);
        exoticAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                RequestParams rp = new RequestParams();
                rp.add("aType", "Exotic");
                rp.add("includeTags", "true");
                mCntrlMngr.postGetActivityList(AddNewActivity.this, rp);
            }
        });
        //todo handle adscard here
//        if(b.containsKey("adsCardId")) {
//            adcardEventId = b.getString("adsCardId");
//        }
    }

    private void checkIfAdFlow(boolean ads, String adP) {
        if(ads) {
            //start new activity for add event creation
            Intent regIntent = new Intent(AddNewActivity.this,
                    AddFinalActivity.class);
            regIntent.putExtra("userdata", user);
            regIntent.putExtra("adcard", ads);
            regIntent.putExtra("adCardId", adP);
            startActivity(regIntent);
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

    @Override
    public void update(Observable observable, Object data) {
        hideProgressBar();
        if(mCntrlMngr.getCurrentActivityList()!=null && !mCntrlMngr.getCurrentActivityList().isEmpty()) {
            //start new activity for add event creation
            Intent regIntent = new Intent(AddNewActivity.this,
                    AddFinalActivity.class);
            regIntent.putExtra("userdata", user);
            startActivity(regIntent);
        }
    }

    private void launchListActivityAndFinish() {
        Intent i=new Intent (this, ListActivityFragment.class);
        i.putExtra("userdata", user);
//        if(localPushEventObj!=null){
//            i.putExtra("eventIntent", localPushEventObj);
//        }
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        launchListActivityAndFinish();
    }
}
