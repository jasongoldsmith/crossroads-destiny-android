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
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import co.crossroadsapp.destiny.utils.Util;
import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
public class SplashActivity extends BaseActivity{
    private static final int SPLASH_DELAY = 500;
    private Handler mHandler;
    private RelativeLayout mLayout;
    private ControlManager cManager;
    private GoogleApiClient mGoogleApiClient;
//    private RelativeLayout errLayout;
//    private TextView errText;
//    private ImageView close_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fabric
        Fabric.with(this, new Answers());
        //facebood adk
        FacebookSdk.sdkInitialize(SplashActivity.this);
        AppEventsLogger.activateApp(SplashActivity.this);
        setContentView(R.layout.splash_loading);
        //mixpanel token
        String projectToken =  getResources().getString(R.string.mix_panel_token);//"23f27698695b0137adfef97f173b9f91";
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(SplashActivity.this, projectToken);
        // Automatic session tracking
        //Branch.getAutoInstance(this);
        cManager = ControlManager.getmInstance();
        mHandler = new Handler();
        mLayout = (RelativeLayout) findViewById(R.id.splash_layout);
        if(mLayout!=null) {
            mLayout.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(this,
                    R.anim.fadein_splash);
            mLayout.startAnimation(anim);
        }

        //deeplink handling
        // Build GoogleApiClient with AppInvite API for receiving deep links
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(AppInvite.API)
//                .build();
//
//        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
//        // would automatically launch the deep link if one is found.
//        boolean autoLaunchDeepLink = true;
//        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
//                .setResultCallback(
//                        new ResultCallback<AppInviteInvitationResult>() {
//                            @Override
//                            public void onResult(@NonNull AppInviteInvitationResult result) {
//                                if (result.getStatus().isSuccess()) {
//                                    // Extract deep link from Intent
//                                    Intent intent = result.getInvitationIntent();
//                                    String deepLink = AppInviteReferral.getDeepLink(intent);
//
//                                    // Handle the deep link. For example, open the linked
//                                    // content, or apply promotional credit to the user's
//                                    // account.
//
//                                    mHandler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Intent homeIntent = new Intent(getApplicationContext(),
//                                                    MainActivity.class);
//                                            startActivity(homeIntent);
//                                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                                            finish();
//                                        }
//
//                                    }, SPLASH_DELAY);
//                                    // ...
//                                } else {
//                                    TravellerLog.d(this, "getInvitation: no deep link found.");
//                                }
//                            }
//                        });
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

    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance(getApplicationContext());

        Map<String, String> json = new HashMap<String, String>();

        //todo find better solution to find out if getting start from universal link click
        Intent s = getIntent();
        if (s!=null && s.getData()!=null) {
            if (s.getData().toString().toLowerCase().contains("dlcsrd")) {
                //tracking
                    json.put("source", "branch");

                branch.initSession(new Branch.BranchUniversalReferralInitListener() {
                    @Override
                    public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                        if (error == null) {
                            //todo need to reset user session as it's not getting automatically updated and looks like a bug with branch
                            Branch.getInstance().resetUserSession();
                            // params are the deep linked params associated with the link that the user clicked before showing up
                            if (branchUniversalObject != null) {
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
                json.put("source", "unknown");
            }
        } else {
            //tracking
            json.put("source", "unknown");
        }

        Util.postTracking(json, SplashActivity.this, cManager);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    public void showError(String err) {
        setErrText(err);
//        errLayout.setVisibility(View.VISIBLE);
//        errText.setText(err);
    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
}
