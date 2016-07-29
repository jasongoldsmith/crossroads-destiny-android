package co.crossroadsapp.destiny;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONObject;

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
        setContentView(R.layout.splash_loading);
        // Automatic session tracking
        //Branch.getAutoInstance(this);
        cManager = ControlManager.getmInstance();
        //cManager.setCurrentActivity(this);
//        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
//        errText = (TextView) findViewById(R.id.error_sub);
//        close_err = (ImageView) findViewById(R.id.err_close);
//
//        close_err.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                errLayout.setVisibility(View.GONE);
//            }
//        });
        mHandler = new Handler();
        mLayout = (RelativeLayout) findViewById(R.id.splash_layout);
        mLayout.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this,
                R.anim.fadein_splash);
        mLayout.startAnimation(anim);

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

//        Branch.getInstance(getApplicationContext()).initSession(new Branch.BranchReferralInitListener(){
//            @Override
//            public void onInitFinished(JSONObject referringParams, BranchError error) {
//                if(error==null) {
//
//                }
//            }
//        }, this.getIntent().getData(), this);

        Branch.getInstance(getApplicationContext()).initSession(new Branch.BranchUniversalReferralInitListener(){
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    //TravellerLog.i("BranchConfigTest", "deep link data: " + branchUniversalObject.toString());
                    if(branchUniversalObject!=null) {
                        TravellerLog.i("BranchConfigTest", "deep link data: " + branchUniversalObject.toString());
                        String d = branchUniversalObject.getMetadata().get("+clicked_branch_link");
                        System.out.println("Hardik click boolean " + d);
                        if (branchUniversalObject.getMetadata().get("+clicked_branch_link").equalsIgnoreCase("true")) {
                            if (branchUniversalObject.getMetadata().containsKey("eventId")) {
                                String eId = branchUniversalObject.getMetadata().get("eventId");
                                if (eId != null) {
                                    if (cManager != null) {
                                        cManager.setDeepLinkEvent(eId);
                                    }
//                            Intent in = new Intent("deeplink_flag");
//                            in.putExtra("eventId", eId);
//                            sendBroadcast(in);
                                }
                            }
                        }
                    }
                }
            }
        }, this.getIntent().getData(), this);
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
