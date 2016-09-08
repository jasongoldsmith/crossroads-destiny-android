package co.crossroadsapp.destiny;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.mixpanel.android.mpmetrics.MPConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import co.crossroadsapp.destiny.network.TrackingNetwork;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.utils.TravellerLog;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;


/**
 * Created by sharmha on 2/19/16.
 */
//public class SplashActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
public class SplashActivity extends BaseActivity implements Observer {
    private static final int SPLASH_DELAY = 1000;
    private Handler mHandler;
    private RelativeLayout mLayout;
    private ControlManager cManager;
    private boolean appInstallSuccess=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fabric
        Fabric.with(this, new Answers());
        //facebood sdk
        FacebookSdk.sdkInitialize(SplashActivity.this);
        AppEventsLogger.activateApp(SplashActivity.this);
        setContentView(R.layout.splash_loading);
        mHandler = new Handler();
        cManager = ControlManager.getmInstance();
        cManager.setClient(SplashActivity.this);
        cManager.setCurrentActivity(SplashActivity.this);
        //mixpanel token
        String projectToken =  getResources().getString(R.string.mix_panel_token);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(SplashActivity.this, projectToken);
        cManager.addClientHeader("x-mixpanelid", mixpanel.getDistinctId()!=null?mixpanel.getDistinctId():"");

        // Automatic session tracking
        //Branch.getAutoInstance(this);
        mLayout = (RelativeLayout) findViewById(R.id.splash_layout);
        if(mLayout!=null) {
            mLayout.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(this,
                    R.anim.fadein_splash);
            mLayout.startAnimation(anim);
        }

        //Starting service for listening ad callbacks
        String s = Util.getDefaults("appInstall", SplashActivity.this);
        if (s==null) {
            Util.setDefaults("appInstall", Constants.UNKNOWN_SOURCE, SplashActivity.this);
            Map<String, String> jsonOrganic = new HashMap<String, String>();
            jsonOrganic.put("ads", Constants.ORGANIC_SOURCE);
            Util.postTracking(jsonOrganic, SplashActivity.this, cManager, Constants.APP_INSTALL);
            Intent in = new Intent(SplashActivity.this, CallbackService.class);
            SplashActivity.this.startService(in);
        } else {
            if(s.equalsIgnoreCase(Constants.UNKNOWN_SOURCE)) {
                Util.setDefaults("appInstall", Constants.ORGANIC_SOURCE, SplashActivity.this);
                Map<String, String> jsonOrganic = new HashMap<String, String>();
                jsonOrganic.put("ads", Constants.ORGANIC_SOURCE);
                Util.postTracking(jsonOrganic, SplashActivity.this, cManager, Constants.APP_INSTALL);
            } else {
                branchInitializationAndInit();
                launchNextActivity();
            }
        }
    }

    private void launchNextActivity() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }

        }, SPLASH_DELAY);
    }

    private void branchInitializationAndInit() {
        Branch branch = Branch.getInstance(getApplicationContext());

        Map<String, String> json = new HashMap<String, String>();

        //todo find better solution to find out if getting start from universal link click
        Intent s = getIntent();
        if (s!=null && s.getData()!=null) {
            if (s.getData().toString().toLowerCase().contains("dlcsrd")) {
                //tracking
                json.put("source", Constants.BRANCH_SOURCE);
                branch.initSession(new Branch.BranchUniversalReferralInitListener() {
                    @Override
                    public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                        if (error == null) {
                            //todo need to reset user session as it's not getting automatically updated and looks like a bug with branch
                            Branch.getInstance().resetUserSession();
                            // params are the deep linked params associated with the link that the user clicked before showing up
                            if (branchUniversalObject != null) {
                                Map<String, String> jsonBranch = new HashMap<String, String>();
                                jsonBranch.put("ads", Constants.BRANCH_SOURCE);
//                                Util.postTracking(jsonBranch, SplashActivity.this, cManager, "appInstall");
//                                Util.setDefaults("appInstall", "branch", SplashActivity.this);
                                checkAppInstall(jsonBranch);
                                if (branchUniversalObject.getMetadata().containsKey("eventId") && branchUniversalObject.getMetadata().containsKey("activityName")) {
                                    String eId = branchUniversalObject.getMetadata().get("eventId");
                                    String aName = branchUniversalObject.getMetadata().get("activityName");
                                    if (eId != null) {
                                        if (cManager != null) {
                                            cManager.setDeepLinkEvent(eId, aName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, this.getIntent().getData(), this);
            } else {
                //tracking
                json.put("source", Constants.UNKNOWN_SOURCE);
            }
        } else {
            //tracking
            json.put("source", Constants.UNKNOWN_SOURCE);
        }

        Util.postTracking(json, SplashActivity.this, cManager, Constants.APP_INIT);
    }

    private void checkAppInstall(final Map<String, String> json) {
        String s = Util.getDefaults("appInstall", SplashActivity.this);
        if (s==null || s.equalsIgnoreCase(Constants.UNKNOWN_SOURCE)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Util.postTracking(json, SplashActivity.this, cManager, Constants.APP_INSTALL);
                    if (json != null && json.containsKey("ads")) {
                        Util.setDefaults("appInstall", Constants.BRANCH_SOURCE, SplashActivity.this);
                    }
                }

            }, 500);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    public void showError(String err) {
        branchInitializationAndInit();
        launchNextActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof TrackingNetwork) {
            if(!appInstallSuccess) {
                branchInitializationAndInit();
                launchNextActivity();
                appInstallSuccess = true;
            }
        }
    }

}
