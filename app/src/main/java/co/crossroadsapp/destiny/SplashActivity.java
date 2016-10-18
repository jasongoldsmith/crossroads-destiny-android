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

import co.crossroadsapp.destiny.data.InvitationLoginData;
import co.crossroadsapp.destiny.network.InvitePlayerNetwork;
import co.crossroadsapp.destiny.network.TrackingNetwork;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.utils.TravellerLog;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import com.crashlytics.android.Crashlytics;


/**
 * Created by sharmha on 2/19/16.
 */
public class SplashActivity extends BaseActivity implements Observer {
    private static final int SPLASH_DELAY = 1000;
    private Handler mHandler;
    private RelativeLayout mLayout;
    private ControlManager cManager;
    private boolean appInstallSuccess=false;
    MixpanelAPI mixpanel;
    InvitationLoginData inviData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fabric
        Fabric.with(this, new Answers());
        //crashlytics
        Fabric.with(this, new Crashlytics());
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
        mixpanel = MixpanelAPI.getInstance(SplashActivity.this, projectToken);
        cManager.addClientHeader("x-mixpanelid", mixpanel.getDistinctId()!=null?mixpanel.getDistinctId():"");
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
                if(inviData!=null) {
                    homeIntent.putExtra("invitation", inviData);
                }
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }

        }, SPLASH_DELAY);
    }

    private void branchInitializationAndInit() {
        Branch branch = Branch.getInstance(getApplicationContext());
        final Map<String, String> json = new HashMap<String, String>();
        branch.initSession(new Branch.BranchReferralInitListener() {
                    @Override
                    public void onInitFinished(JSONObject referringParams, BranchError error) {
                        // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                        // params will be empty if no data found
                        if (error == null) {
                            try {
                                if(referringParams.has("+clicked_branch_link") && referringParams.getBoolean("+clicked_branch_link")) {
                                    //tracking appinit
                                    json.put("source", Constants.BRANCH_SOURCE);
                                    Map<String, String> jsonBranch = new HashMap<String, String>();
                                    jsonBranch.put("ads", Constants.BRANCH_SOURCE);
                                    checkAppInstall(jsonBranch);
                                    if (referringParams.has("eventId") && referringParams.has("activityName")) {
                                        String eId = referringParams.getString("eventId");
                                        String aName = referringParams.getString("activityName");
                                        if(referringParams.has("invitees")) {
                                            //Map<String, Object> invitation = new HashMap<String, Object>();
                                            String invitee = referringParams.getString("invitees");
                                            if(referringParams.has("~referring_link") && !referringParams.isNull("~referring_link")) {
                                                inviData = new InvitationLoginData(invitee, eId, referringParams.getString("~referring_link"));
                                            }
                                        }
                                        if (eId != null) {
                                            if (cManager != null) {
                                                cManager.setDeepLinkEvent(eId, aName);
                                            }
                                        }
                                    }
                                } else {
                                    //tracking appinit
                                    json.put("source", Constants.UNKNOWN_SOURCE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //tracking appinit
                            json.put("source", Constants.BRANCH_SOURCE);
                        }
                    }
                }, this.getIntent().getData(), this);
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
        if(Util.getDefaults("user", getApplicationContext())==null) {
            //cManager.getPublicEventList(null);
            cManager.setShowFullEvent(mixpanel.booleanTweak("showFullCards", false).get());
        }
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
