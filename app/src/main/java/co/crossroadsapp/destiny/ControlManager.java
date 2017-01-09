package co.crossroadsapp.destiny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;

import co.crossroadsapp.destiny.data.ActivityData;
import co.crossroadsapp.destiny.data.ActivityList;
import co.crossroadsapp.destiny.data.AppVersion;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.EventList;
import co.crossroadsapp.destiny.data.GroupData;
import co.crossroadsapp.destiny.data.GroupList;
import co.crossroadsapp.destiny.data.InvitationLoginData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.ActivityListNetwork;
import co.crossroadsapp.destiny.network.AddCommentNetwork;
import co.crossroadsapp.destiny.network.AddNewConsoleNetwork;
import co.crossroadsapp.destiny.network.BungieMessageNetwork;
import co.crossroadsapp.destiny.network.BungieUserNetwork;
import co.crossroadsapp.destiny.network.ChangeCurrentConsoleNetwork;
import co.crossroadsapp.destiny.network.ConfigNetwork;
import co.crossroadsapp.destiny.network.EventByIdNetwork;
import co.crossroadsapp.destiny.network.EventListNetwork;
import co.crossroadsapp.destiny.network.EventRelationshipHandlerNetwork;
import co.crossroadsapp.destiny.network.EventSendMessageNetwork;
import co.crossroadsapp.destiny.network.ForgotPasswordNetwork;
import co.crossroadsapp.destiny.network.GetVersion;
import co.crossroadsapp.destiny.network.GroupListNetwork;
import co.crossroadsapp.destiny.network.HelmetUpdateNetwork;
import co.crossroadsapp.destiny.network.InvitePlayerNetwork;
import co.crossroadsapp.destiny.network.LogoutNetwork;
import co.crossroadsapp.destiny.network.PrivacyLegalUpdateNetwork;
import co.crossroadsapp.destiny.network.ReportCommentNetwork;
import co.crossroadsapp.destiny.network.ReportCrashNetwork;
import co.crossroadsapp.destiny.network.ResendBungieVerification;
import co.crossroadsapp.destiny.network.TrackingNetwork;
import co.crossroadsapp.destiny.network.VerifyConsoleIDNetwork;
import co.crossroadsapp.destiny.network.postGcmNetwork;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.network.LoginNetwork;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.ErrorShowDialog;
import co.crossroadsapp.destiny.utils.TravellerDialogueHelper;
import co.crossroadsapp.destiny.utils.Version;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.mixpanel.android.mpmetrics.MPConfig;
import com.shaded.fasterxml.jackson.core.JsonGenerationException;
import com.shaded.fasterxml.jackson.core.JsonParseException;
import com.shaded.fasterxml.jackson.core.type.TypeReference;
import com.shaded.fasterxml.jackson.databind.JsonMappingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 2/29/16.
 */
public class ControlManager implements Observer{

    private EventListNetwork eventListNtwrk;
    private EventRelationshipHandlerNetwork eventRelationshipNtwrk;
    private ActivityListNetwork activityListNetwork;
    private postGcmNetwork gcmTokenNetwork;
    private LoginNetwork loginNetwork;
    private LogoutNetwork logoutNetwork;
    private ResendBungieVerification resendBungieMsg;
    private EventSendMessageNetwork eventSendMsgNetwork;
    private ReportCrashNetwork crashReportNetwork;
    private UserData user;

    private Version checkVersion;

    private WeakReference<Activity> mCurrentAct;

    private static final String TAG = ControlManager.class.getSimpleName();

    private EventList eList;
    private GroupList gList;
    private ArrayList<EventData> eData;
    private ArrayList<GroupData> gData;
    private ArrayList<ActivityData> activityList;
    private ArrayList<ActivityData> raidActivityList;
    private ArrayList<ActivityData> crucibleActivityList;

    private static ControlManager mInstance;
    private ForgotPasswordNetwork forgotPasswordNetwork;
    private GroupListNetwork groupListNtwrk;
    private VerifyConsoleIDNetwork verifyConsoleNetwork;
    private EventByIdNetwork eventById;
    private HelmetUpdateNetwork helmetUpdateNetwork;
    private String deepLinkEvent;
    private ArrayList<ActivityData> adActivityData;
    private ArrayList<String> consoleList;
    private AddNewConsoleNetwork addConsoleNetwork;
    private ChangeCurrentConsoleNetwork changeCurrentConsoleNetwork;
    private String deepLinkActivityName;
    private PrivacyLegalUpdateNetwork legalPrivacyNetwork;
    private AddCommentNetwork addCommentsNetwork;
    private TrackingNetwork trackingNetwork;
    private AsyncHttpClient client;
    private Boolean showFullEvent;
    private ReportCommentNetwork reportCommentNetwork;
    private InvitePlayerNetwork invitePlayersNetwork;
    private BungieUserNetwork bugieGetUser;
    private ConfigNetwork getConfigNetwork;
    private String bungieCurrentUserUrl;
    private String psnURL;
    private String xboxURL;
    private BungieMessageNetwork bungieMsgNtwrk;

    public ControlManager() {
    }

    public static ControlManager getmInstance() {
        if (mInstance==null) {
            mInstance = new ControlManager();
            return mInstance;
        } else {
            return mInstance;
        }
    }

    public void setUserdata(UserData ud) {
        if(user!=null && user.getUser()!=null){

        }else {
            user = new UserData();
        }
        user = ud;
    }

    public UserData getUserData(){
        return user;
    }

    public ArrayList<EventData> getEventListCurrent() {
        if (eData!= null && !eData.isEmpty()) {
            return eData;
        }
        return null;
    }

    public void setEventList(EventList el) {
        if(eData==null) {
            eData = new ArrayList<EventData>();
        } else {
            eData.clear();
        }
        eData = el.getEventList();
    }

    public void setadList(ActivityList al) {
        if(adActivityData==null) {
            adActivityData = new ArrayList<ActivityData>();
        }
        adActivityData = al.getActivityList();
    }

    public ArrayList<ActivityData> getAdsActivityList() {
        if (adActivityData!= null && !adActivityData.isEmpty()) {
            return adActivityData;
        }
        return null;
    }

    public void getEventList() {
        try {
            if(mCurrentAct!=null && mCurrentAct.get()!=null) {
                eventListNtwrk = new EventListNetwork(mCurrentAct.get());
                if(mCurrentAct.get() instanceof ListActivityFragment) {
                    eventListNtwrk.addObserver((ListActivityFragment) mCurrentAct.get());
                    //todo commenting out get android version for google release
                    getAndroidVersion((ListActivityFragment)mCurrentAct.get());
                }
                eventListNtwrk.addObserver(this);
                eventListNtwrk.getEvents(Constants.EVENT_FEED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resendBungieMsg() {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof ListActivityFragment) {
                resendBungieMsg = new ResendBungieVerification(mCurrentAct.get());
                resendBungieMsg.addObserver((ListActivityFragment)mCurrentAct.get());
                resendBungieMsg.resendBungieMsgVerify();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void getEventList() {
//        try {
//            eventListNtwrk = new EventListNetwork(mCurrentAct.get());
//            eventListNtwrk.addObserver(this);
//            eventListNtwrk.getEvents(Constants.EVENT_FEED);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void getPublicEventList() {
        try {
            if(mCurrentAct!=null && mCurrentAct.get() instanceof MainActivity) {
                eventListNtwrk = new EventListNetwork(mCurrentAct.get());
                eventListNtwrk.addObserver(this);
                eventListNtwrk.addObserver((MainActivity)mCurrentAct.get());
                eventListNtwrk.getEvents(Constants.PUBLIC_EVENT_FEED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getGroupList() {
        try {
            if(mCurrentAct.get()!=null) {
                groupListNtwrk = new GroupListNetwork(mCurrentAct.get());
                if(mCurrentAct.get() instanceof ListActivityFragment) {
                    groupListNtwrk.addObserver((ListActivityFragment) mCurrentAct.get());
                }
                groupListNtwrk.addObserver(this);
                groupListNtwrk.getGroups();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postSetGroup(RequestParams params) {
        try{
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof ListActivityFragment) {
                groupListNtwrk = new GroupListNetwork(mCurrentAct.get());
                groupListNtwrk.addObserver(this);
                groupListNtwrk.addObserver((ListActivityFragment)mCurrentAct.get());
                groupListNtwrk.postSelectGroup(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postMuteNoti(RequestParams params) {
        try{
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof ListActivityFragment) {
                if (groupListNtwrk == null) {
                    groupListNtwrk = new GroupListNetwork(mCurrentAct.get());
                }
                groupListNtwrk.addObserver((ListActivityFragment)mCurrentAct.get());
                groupListNtwrk.postMuteNotification(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public EventData getEventObj(String eId) {
        if(eData!=null && (!eData.isEmpty())) {
            for (int i=0; i<eData.size(); i++) {
                if (eData.get(i)!=null) {
                    if(eData.get(i).getEventId()!=null) {
                        if(eData.get(i).getEventId().equalsIgnoreCase(eId)) {
                            EventData event = new EventData();
                            event = eData.get(i);
                            return event;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void postJoinEvent(RequestParams params) {
        try {
            if(mCurrentAct!=null) {
                eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
                if (mCurrentAct.get() != null && mCurrentAct.get() instanceof EventDetailActivity) {
                    eventRelationshipNtwrk.addObserver((EventDetailActivity) mCurrentAct.get());
                } else if (mCurrentAct.get() != null && mCurrentAct.get() instanceof ListActivityFragment) {
                    eventRelationshipNtwrk.addObserver((ListActivityFragment) mCurrentAct.get());
                }
                eventRelationshipNtwrk.addObserver(this);
                eventRelationshipNtwrk.postJoin(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postEventMessage(String msg, String id, String eventId){

        try {
            if (mCurrentAct.get() != null && mCurrentAct.get() instanceof ListActivityFragment) {
                eventSendMsgNetwork = new EventSendMessageNetwork(mCurrentAct.get());
                eventSendMsgNetwork.addObserver((EventDetailActivity)mCurrentAct.get());
                RequestParams rp = new RequestParams();
                rp.put("id", id);
                rp.put("message", msg);
                rp.put("eId", eventId);
                eventSendMsgNetwork.postEventMsg(rp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postGetActivityList(RequestParams params) {
        try {
            if(mCurrentAct.get()!=null) {
                activityListNetwork = new ActivityListNetwork(mCurrentAct.get());
                if (mCurrentAct.get() instanceof AddNewActivity) {
                    //activityListNetwork.addObserver(this);
                    activityListNetwork.addObserver((AddNewActivity) mCurrentAct.get());
                } else if (mCurrentAct.get() instanceof ListActivityFragment) {
                    //activityListNetwork.addObserver(this);
                    activityListNetwork.addObserver((ListActivityFragment) mCurrentAct.get());
                }
                if (activityList != null) {
                    activityList.clear();
                }
                activityListNetwork.postGetActivityList(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void postGetActivityList(ListActivityFragment c) {
//        try {
//            activityListNetwork = new ActivityListNetwork(c);
//            activityListNetwork.addObserver(this);
//            activityListNetwork.postGetActivityList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void postUnJoinEvent(RequestParams params) {
        try {
            if (mCurrentAct != null && mCurrentAct.get() != null) {
                eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
                if (mCurrentAct.get() instanceof ListActivityFragment) {
                    eventRelationshipNtwrk.addObserver((ListActivityFragment) mCurrentAct.get());
                } else if(mCurrentAct.get() instanceof EventDetailActivity) {
                    eventRelationshipNtwrk.addObserver((EventDetailActivity)mCurrentAct.get());
                }
                eventRelationshipNtwrk.addObserver(this);
                eventRelationshipNtwrk.postUnJoin(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void postUnJoinEvent(EventDetailActivity activity, RequestParams params) {
//        try {
//            if (mCurrentAct.get() != null && mCurrentAct.get() instanceof EventDetailActivity) {
//                eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
//                eventRelationshipNtwrk.addObserver((EventDetailActivity)mCurrentAct.get());
//                eventRelationshipNtwrk.addObserver(this);
//                eventRelationshipNtwrk.postUnJoin(params);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public ArrayList<ActivityData> getCustomActivityList(String tempActivityName) {
        if (this.activityList!= null) {
            raidActivityList = new ArrayList<ActivityData>();

            if(tempActivityName.equalsIgnoreCase(Constants.ACTIVITY_FEATURED)) {
                for (int i = 0; i < activityList.size(); i++) {
                    if (activityList.get(i).getActivityFeature()) {
                        raidActivityList.add(activityList.get(i));
                    }
                }
            } else {
                for (int i = 0; i < activityList.size(); i++) {
                    if (activityList.get(i).getActivityType().equalsIgnoreCase(tempActivityName)) {
                        raidActivityList.add(activityList.get(i));
                    }
                }
            }
            return this.raidActivityList;
        }
        return null;
    }

    public ArrayList<ActivityData> getCheckpointActivityList(String subtype, String diff) {
        if (this.activityList!= null) {
            ArrayList<ActivityData> checkpointActivityList = new ArrayList<ActivityData>();
            for (int i=0; i<activityList.size();i++) {
                if(diff!=null) {
                    if (activityList.get(i).getActivitySubtype().equalsIgnoreCase(subtype) && activityList.get(i).getActivityDifficulty().equalsIgnoreCase(diff)) {
                        checkpointActivityList.add(activityList.get(i));
                    }
                }else {
                    if (activityList.get(i).getActivitySubtype().equalsIgnoreCase(subtype)) {
                        checkpointActivityList.add(activityList.get(i));
                    }
                }
            }
            return checkpointActivityList;
        }

        return null;
    }

    public ArrayList<ActivityData> getCurrentActivityList() {
        return activityList;
    }

    public void postLogin(Activity activity, RequestParams params, int postId) {
        try {
            if(mCurrentAct!=null) {
                loginNetwork = new LoginNetwork(mCurrentAct.get());
                loginNetwork.addObserver(this);
                if (postId == Constants.LOGIN) {
                    if (activity instanceof LoginActivity) {
                        loginNetwork.addObserver((LoginActivity) activity);
                    } else if (mCurrentAct != null && mCurrentAct.get() instanceof MainActivity) {
                        loginNetwork.addObserver((MainActivity) mCurrentAct.get());
                    }
                    loginNetwork.doSignup(params);
                } else if (postId == Constants.REGISTER) {
                    loginNetwork.addObserver((RegisterActivity) activity);
                    loginNetwork.doRegister(params);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verifyBungieId(RequestParams params) {
        try {
            if (mCurrentAct.get() != null && mCurrentAct.get() instanceof ConsoleSelectionActivity) {
                verifyConsoleNetwork = new VerifyConsoleIDNetwork(mCurrentAct.get());
                verifyConsoleNetwork.addObserver((ConsoleSelectionActivity)mCurrentAct.get());
                verifyConsoleNetwork.doVerifyConsoleId(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postResetPassword(ForgotLoginActivity activity, RequestParams params) {
        try {
            forgotPasswordNetwork = new ForgotPasswordNetwork(activity);
            forgotPasswordNetwork.addObserver(activity);
            forgotPasswordNetwork.doResetPassword(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postLogout(RequestParams params) {
        try {
            if(mCurrentAct!=null && mCurrentAct.get()!=null) {
                logoutNetwork = new LogoutNetwork(mCurrentAct.get());
                //logoutNetwork.addObserver(this);
                if(mCurrentAct.get() instanceof ListActivityFragment) {
                    logoutNetwork.addObserver((ListActivityFragment) mCurrentAct.get());
                } else if(mCurrentAct.get() instanceof MainActivity) {
                    logoutNetwork.addObserver((MainActivity)mCurrentAct.get());
                }
                logoutNetwork.doLogout(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void postLogout(MainActivity act, RequestParams params) {
//        try {
//            if(mCurrentAct!=null && mCurrentAct.get() instanceof MainActivity) {
//                logoutNetwork = new LogoutNetwork(mCurrentAct.get());
//                //logoutNetwork.addObserver(this);
//                logoutNetwork.addObserver((MainActivity)mCurrentAct.get());
//                logoutNetwork.doLogout(params);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void postHelmet() {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof ListActivityFragment) {
                helmetUpdateNetwork = new HelmetUpdateNetwork(mCurrentAct.get());
                helmetUpdateNetwork.addObserver((ListActivityFragment) mCurrentAct.get());
                helmetUpdateNetwork.getHelmet();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postEventById(RequestParams param) {
        try {
            if(mCurrentAct!=null && mCurrentAct.get() != null) {
                eventById = new EventByIdNetwork(mCurrentAct.get());
                if (mCurrentAct.get() instanceof ListActivityFragment) {
                    eventById.addObserver((ListActivityFragment) mCurrentAct.get());
                } else if (mCurrentAct.get() instanceof EventDetailActivity) {
                    eventById.addObserver((EventDetailActivity) mCurrentAct.get());
                }
                eventById.getEventById(param);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showErrorDialogue(String err) {
//        if(err==null){
//            //err = "Request failed. Please wait a few seconds and refresh.";
//        }
        //if(err!=null) {
            if (this.mCurrentAct.get() != null) {
                if (mCurrentAct.get() instanceof SplashActivity) {
                    ((SplashActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof LoginActivity) {
                    ((LoginActivity) mCurrentAct.get()).showError(err, null);
                } else if (mCurrentAct.get() instanceof RegisterActivity) {
                    ((RegisterActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof ListActivityFragment) {
                    ((ListActivityFragment) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof AddFinalActivity) {
                    ((AddFinalActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof EventDetailActivity) {
                    ((EventDetailActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof ForgotLoginActivity) {
                    ((ForgotLoginActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof ConsoleSelectionActivity) {
                    ((ConsoleSelectionActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof ChangePassword) {
                    ((ChangePassword) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof MainActivity) {
                    ((MainActivity) mCurrentAct.get()).showError(err, null);
                } else if (mCurrentAct.get() instanceof UpdateConsoleActivity) {
                    ((UpdateConsoleActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof AddFinalActivity) {
                    ((AddFinalActivity) mCurrentAct.get()).showError(err);
                } else if (mCurrentAct.get() instanceof CrashReport) {
                    ((CrashReport) mCurrentAct.get()).showError(err);
                }
            }
        //}
    }

    public void getAndroidVersion(ListActivityFragment activity) {
//        getVersionNetwork = new GetVersion(activity);
//        getVersionNetwork.addObserver(this);
//        try {
//            getVersionNetwork.getAppVer();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void getAndroidVersion(MainActivity activity) {
//        getVersionNetwork = new GetVersion(activity);
//        getVersionNetwork.addObserver(activity);
//        getVersionNetwork.addObserver(this);
//        try {
//            getVersionNetwork.getAppVer();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public Intent decideToOpenActivity(Intent contentIntent) {

        Intent regIntent;
        regIntent = new Intent(mCurrentAct.get().getApplicationContext(),
                ListActivityFragment.class);
        if (contentIntent != null ) {
            regIntent.putExtra("eventIntent", contentIntent);
        }
//        if (contentIntent != null ) {
//            regIntent = new Intent(mCurrentAct.get().getApplicationContext(),
//                    ListActivityFragment.class);
//            regIntent.putExtra("eventIntent", contentIntent);
//        } else {
//            if(user.getPsnVerify()!=null && user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
//                if(user.getClanId()!=null && user.getClanId().equalsIgnoreCase(Constants.CLAN_NOT_SET)) {
//                    regIntent = new Intent(mCurrentAct.get().getApplicationContext(),
//                            ListActivityFragment.class);
//                } else {
//                    if(this.eData!=null && (!this.eData.isEmpty())) {
//                        regIntent = new Intent(mCurrentAct.get().get().getApplicationContext(),
//                                ListActivityFragment.class);
//                    } else {
//                        regIntent = new Intent(mCurrentAct.get().getApplicationContext(),
//                                CreateNewEvent.class);
//                    }
//                }
//            } else {
//                regIntent = new Intent(mCurrentAct.get().getApplicationContext(),
//                        CreateNewEvent.class);
//            }
//        }
        return regIntent;
    }

    public void setCurrentActivity(Activity act) {
        mCurrentAct = new WeakReference<Activity>(act);
    }

    public Context getCurrentActivity() {
        if (this.mCurrentAct.get()!=null) {
            return this.mCurrentAct.get();
        }
        return null;
    }
    @Override
    public void update(Observable observable, Object data) {

        if (observable instanceof EventListNetwork) {
            eData = new ArrayList<EventData>();
            if (data instanceof EventListNetwork) {
                EventList eList = ((EventListNetwork) data).getEventList();
                eData = eList.getEventList();
                ActivityList adList = ((EventListNetwork) data).getActList();
                adActivityData = adList.getActivityList();
            }else {
                EventList eList = (EventList) data;
                eData = eList.getEventList();
            }
        } else if (observable instanceof LogoutNetwork){
            //mCurrentAct.get().finish();
        } else if (observable instanceof EventRelationshipHandlerNetwork) {
            EventData ed = (EventData) data;
            boolean eventExist=true;
            if (eData!= null) {
                for (int i=0; i<eData.size();i++) {
                    if (ed.getEventId().equalsIgnoreCase(eData.get(i).getEventId())) {
                        eventExist = false;
                        if (ed.getMaxPlayer()>0) {
                            eData.remove(i);
                            eData.add(i, ed);
                        } else {
                            eData.remove(i);
                        }
                        break;
                    }
                }
                if(eventExist) {
                    eData.add(ed);
                }
            }
        } else if (observable instanceof ActivityListNetwork) {
            updateActivityList(data!=null?data:null);
        } else if(observable instanceof GetVersion) {
            AppVersion ver = (AppVersion) data;
            if (this.mCurrentAct.get()!=null) {
                String currVer = Util.getApplicationVersionCode(this.mCurrentAct.get());
                String latestVer = ver.getVersion();
                Version currVersion = new Version(currVer);
                Version latestVersion = new Version(latestVer);
                if (latestVersion.compareTo(currVersion)>0){
                    AlertDialog.Builder builder = TravellerDialogueHelper.createConfirmDialogBuilder(this.mCurrentAct.get(), "New Version Available", "A new version of Crossroads is available for download", "Download", "Later", null);

                    builder.setPositiveButton(R.string.download_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.getAppDownloadLink()));
                            mCurrentAct.get().startActivity(browserIntent);
                        }
                    }).setNegativeButton(R.string.later_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if(mCurrentAct.get() instanceof MainActivity) {
                                getAndroidVersion((MainActivity) mCurrentAct.get());
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    ErrorShowDialog.show(dialog);
                }

            }
        } else if(observable instanceof GroupListNetwork) {
            if(data instanceof UserData) {
                setUserdata((UserData) data);
            } else if(data instanceof GroupData) {
                //
            } else {
                if(gData!=null) {
                    gData.clear();
                } else {
                    gData = new ArrayList<GroupData>();
                }
                gData = (ArrayList<GroupData>) data;
            }
        } else if(observable instanceof LoginNetwork) {
            if (data!=null) {
                getEventList();
                getGroupList();
            }
        } else if(observable instanceof BungieUserNetwork) {
            if(data!=null) {
                try {
                    String platform = getCurrentPlatform();
                    if(platform!=null) {
                        try {
                            HashMap<String,Object> map =
                                    new ObjectMapper().readValue(data.toString(), HashMap.class);
                            RequestParams rp = new RequestParams();
                            rp.put("bungieResponse", map);
                            rp.put("consoleType", platform);
                            rp.put("bungieURL", getBungieCurrentUserUrl()!=null?getBungieCurrentUserUrl():Constants.BUGIE_CURRENT_USER);
                            if(mCurrentAct.get() instanceof MainActivity) {
                                loginNetwork = new LoginNetwork(mCurrentAct.get());
                                InvitationLoginData notificationObj = ((MainActivity) mCurrentAct.get()).getInvitationObject();
                                if(notificationObj!=null) {
                                    rp.put("invitation", notificationObj.getRp());
                                }
                                loginNetwork.addObserver(this);
                                loginNetwork.addObserver(((MainActivity) mCurrentAct.get()));
                                loginNetwork.doSignup(rp);
                            }
                        } catch (JsonGenerationException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//        } else if(observable instanceof BungieMessageNetwork) {
//            if(data!=null) {
//                try {
//                    RequestParams rp = new RequestParams();
//                    if(((BungieMessageNetwork) observable).getId()!=null) {
//                        rp.put("responseType", "sendBungieMessage");
//                        HashMap<String,Object> map = new ObjectMapper().readValue(data.toString(), HashMap.class);
//                        rp.put("gatewayResponse", map);
//                        Map<String, String> map2 = new HashMap();
//                        map2.put("pendingEventInvitationId", ((BungieMessageNetwork) observable).getId());
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    public void updateActivityList(Object data) {
        if(data!=null) {
            activityList = new ArrayList<ActivityData>();
            ActivityList al = (ActivityList) data;
            activityList = al.getActivityList();
        }
    }

    public ArrayList<GroupData> getCurrentGroupList() {

        if(gData!=null) {
            return gData;
        }
        return null;
    }

    public GroupData getGroupObj(String id) {
        if (gData!=null) {
            for (int i =0; i<gData.size(); i++) {
                if(gData.get(i)!=null) {
                    if (gData.get(i).getGroupId()!=null) {
                        if (gData.get(i).getGroupId().equalsIgnoreCase(id)) {
                            return gData.get(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    public void postCreateEvent(String activityId, String creator_id, int minP, int maxP, String dateTime, Context activity) {
        ArrayList<String> players = new ArrayList<String>();
        players.add(creator_id);

        RequestParams rp = new RequestParams();
        rp.put("eType", activityId);
        rp.put("minPlayers", minP);
        rp.put("maxPlayers", maxP);
        rp.put("creator", creator_id);
        rp.put("players", players);
        rp.put("launchDate", dateTime);

        try {
            if(mCurrentAct.get()!=null) {
                eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
                eventRelationshipNtwrk.addObserver(this);
                if (mCurrentAct.get() instanceof AddFinalActivity) {
                    eventRelationshipNtwrk.addObserver((AddFinalActivity) mCurrentAct.get());
                }
                eventRelationshipNtwrk.postCreateEvent(rp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postGCMToken(String token, Context context) {

        RequestParams rp = new RequestParams();
        rp.put("deviceToken", token);

        gcmTokenNetwork = new postGcmNetwork(context);
        try {
            gcmTokenNetwork.postGcmToken(rp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postTracking(RequestParams rp, Context context) {
        trackingNetwork = new TrackingNetwork(context);
        try {
            if(context instanceof SplashActivity)
            trackingNetwork.addObserver((SplashActivity)context);
            trackingNetwork.postTracking(rp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void postCrash(RequestParams requestParams, int report_type) {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof CrashReport) {
                crashReportNetwork = new ReportCrashNetwork(mCurrentAct.get());
                crashReportNetwork.addObserver((CrashReport)mCurrentAct.get());
                crashReportNetwork.doCrashReport(requestParams, report_type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void postChangePassword(ChangePassword activity, RequestParams params) {
        try {
            forgotPasswordNetwork = new ForgotPasswordNetwork(activity);
            forgotPasswordNetwork.addObserver(activity);
            forgotPasswordNetwork.doChangePassword(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postComments(RequestParams params) {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof EventDetailActivity) {
                addCommentsNetwork = new AddCommentNetwork(mCurrentAct.get());
                addCommentsNetwork.addObserver((EventDetailActivity)mCurrentAct.get());
                addCommentsNetwork.postComments(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDeepLinkEvent(String deepLinkEvent, String actName) {
        this.deepLinkEvent = deepLinkEvent;
        this.deepLinkActivityName = actName;
    }

    public String getDeepLinkEvent() {
        return deepLinkEvent;
    }

    public String getDeepLinkActivityName() {
        return deepLinkActivityName;
    }

    public ArrayList<String> getConsoleList() {
        consoleList = new ArrayList<>();
        if(user!=null) {
            if(user.getConsoleType()!=null) {
                consoleList.add(user.getConsoleType());
                for (int n = 0; n < user.getConsoles().size(); n++) {
                    if (!user.getConsoles().get(n).getcType().equalsIgnoreCase(user.getConsoleType())) {
                        consoleList.add(user.getConsoles().get(n).getcType());
                    }
                }
            }
        }
        return consoleList;
    }

    public void addOtherConsole(RequestParams rp_console) {
        try {
            if (mCurrentAct.get() != null && mCurrentAct.get() instanceof UpdateConsoleActivity) {
                addConsoleNetwork = new AddNewConsoleNetwork(mCurrentAct.get());
                addConsoleNetwork.addObserver((UpdateConsoleActivity)mCurrentAct.get());
                addConsoleNetwork.doAddConsole(rp_console);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeToOtherConsole(RequestParams rp_console) {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof ListActivityFragment) {
                changeCurrentConsoleNetwork = new ChangeCurrentConsoleNetwork(mCurrentAct.get());
                changeCurrentConsoleNetwork.addObserver((ListActivityFragment)mCurrentAct.get());
                changeCurrentConsoleNetwork.doChangeConsole(rp_console);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void invitePlayers(RequestParams rp) {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof EventDetailActivity) {
                invitePlayersNetwork = new InvitePlayerNetwork(mCurrentAct.get());
                invitePlayersNetwork.addObserver((EventDetailActivity)mCurrentAct.get());
                invitePlayersNetwork.postInvitedList(rp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ActivityData getAdsActivity(String adcardEventId) {
        for (int n=0;n<adActivityData.size();n++) {
            if (adActivityData.get(n).getId().equalsIgnoreCase(adcardEventId)) {
                return adActivityData.get(n);
            }
        }
        return null;
    }

    public void legalPrivacyDone() {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof ListActivityFragment) {
                legalPrivacyNetwork = new PrivacyLegalUpdateNetwork(mCurrentAct.get());
                legalPrivacyNetwork.addObserver((ListActivityFragment)mCurrentAct.get());
                legalPrivacyNetwork.postTermsPrivacyDone();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public AsyncHttpClient getBungieClient(String csrf, String cookies) {
            AsyncHttpClient clientT = new AsyncHttpClient();
            clientT.setTimeout(30000);
            if(csrf!=null) {
                clientT.addHeader("x-csrf", csrf);
            }
            if(cookies!=null) {
                clientT.addHeader("Cookie", cookies);
            }
            clientT.addHeader("x-api-key", "f091c8d36c3c4a17b559c21cd489bec0");
        return clientT;
    }

    public void setClient(Context c) {
        client = new AsyncHttpClient();
        client.setTimeout(30000);
        ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        client.addHeader("config_token", Constants.CONFIG_TOKEN);
        client.addHeader("$wifi", String.valueOf(mWifi.isConnected()));
        client.addHeader("$screen_dpi", String.valueOf(metrics.densityDpi));
        client.addHeader("$screen_height", String.valueOf(metrics.heightPixels));
        client.addHeader("$screen_width", String.valueOf(metrics.widthPixels));
        client.addHeader("$lib_version", MPConfig.VERSION);
        client.addHeader("$os", "Android");
        client.addHeader("$os_version", Build.VERSION.RELEASE == null ? "UNKNOWN" : Build.VERSION.RELEASE);
        client.addHeader("$manufacturer", Build.MANUFACTURER == null ? "UNKNOWN" : Build.MANUFACTURER);
        client.addHeader("$brand", Build.BRAND == null ? "UNKNOWN" : Build.BRAND);
        client.addHeader("$model", Build.MODEL == null ? "UNKNOWN" : Build.MODEL);
        try {
            if (c != null && c.getPackageManager() != null) {
                if (c.getPackageName() != null) {
                    PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (version != null) {
                        client.addHeader("$app_version", version);
                        client.addHeader("$app_version_code", Integer.toString(pInfo.versionCode));
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        client.addHeader("x-fbooksdk", "facebook-android-sdk:[4,5)");
        client.addHeader("x-fbasesdk", "firebase-9.2.0");
        client.addHeader("x-mpsdk", "mixpanel-android:4.+");
        client.addHeader("x-branchsdk", "branch-library:1.+");
        client.addHeader("x-fabricsdk", "answers:1.3.8@aar");

        try {
            try {
                final int servicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
                switch (servicesAvailable) {
                    case ConnectionResult.SUCCESS:
                        client.addHeader("$google_play_services", "available");
                        break;
                    case ConnectionResult.SERVICE_MISSING:
                        client.addHeader("$google_play_services", "missing");
                        break;
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                        client.addHeader("$google_play_services", "out of date");
                        break;
                    case ConnectionResult.SERVICE_DISABLED:
                        client.addHeader("$google_play_services", "disabled");
                        break;
                    case ConnectionResult.SERVICE_INVALID:
                        client.addHeader("$google_play_services", "invalid");
                        break;
                }
            } catch (RuntimeException e) {
                // Turns out even checking for the service will cause explosions
                // unless we've set up meta-data
                client.addHeader("$google_play_services", "not configured");
            }

        } catch (NoClassDefFoundError e) {
            client.addHeader("$google_play_services", "not included");
        }

        NfcManager managerNfc = (NfcManager) c.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = managerNfc.getDefaultAdapter();
        if (adapter != null) {
            client.addHeader("$has_nfc", String.valueOf(adapter.isEnabled()));
        }

        TelephonyManager manager = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
        client.addHeader("$carrier", manager.getNetworkOperator()!=null?manager.getNetworkOperator():"UNKNOWN");
    }

    public void addClientHeader(String headerKey, String headerValue) {
        if(client!=null) {
            client.addHeader(headerKey, headerValue);
        }
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public void setShowFullEvent(Boolean showFullEvent) {
        this.showFullEvent = showFullEvent;
    }

    public boolean getshowFullEvent() {
        if(showFullEvent!=null) {
            return showFullEvent;
        }
        return false;
    }

    public void postCommentReporting(RequestParams requestParams) {
        try {
            if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof EventDetailActivity) {
                reportCommentNetwork = new ReportCommentNetwork(mCurrentAct.get());
                reportCommentNetwork.addObserver((EventDetailActivity) mCurrentAct.get());
                reportCommentNetwork.doCommentReporting(requestParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBungieCurrentUser(String csrf, String cookies, Context applicationContext) {
        try {
            bugieGetUser = new BungieUserNetwork(csrf, cookies, applicationContext, getBungieCurrentUserUrl()!=null?getBungieCurrentUserUrl():Constants.BUGIE_CURRENT_USER);
            bugieGetUser.addObserver(this);
            bugieGetUser.getBungieCurrentUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentPlatform() {
        String c = Util.getDefaults("cookie", mCurrentAct.get());
        String[] pair = c.split(";");
        for(int i=0; i<pair.length;i++) {
            String temp = pair[i].substring(0, pair[i].indexOf('=')).trim();
            if(temp.equalsIgnoreCase("bunglesony")) {
                return Constants.CONSOLEPS4;
            } else if(temp.equalsIgnoreCase("bunglemsa")) {
                return Constants.CONSOLEXBOXONE;
            }
        }
        return null;
    }

    public void getConfig() {
        try {
            if(mCurrentAct!=null && mCurrentAct.get() instanceof MainActivity) {
                getConfigNetwork = new ConfigNetwork(mCurrentAct.get());
                getConfigNetwork.addObserver((MainActivity)mCurrentAct.get());
                getConfigNetwork.getConfig();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBungieCurrentUserUrl() {
        if (bungieCurrentUserUrl!=null && !bungieCurrentUserUrl.isEmpty()) {
            return bungieCurrentUserUrl;
        }
        return Util.getDefaults("playerDetailsURL", mCurrentAct.get());
    }

    protected String getPSNLoginUrl() {
        if (psnURL!=null && !psnURL.isEmpty()) {
            return psnURL;
        }
        return Util.getDefaults("psnLoginURL", mCurrentAct.get());
    }

    protected String getXboxLoginUrl() {
        if (xboxURL!=null && !xboxURL.isEmpty()) {
            return xboxURL;
        }
        return Util.getDefaults("xboxLoginURL", mCurrentAct.get());
    }

    public void parseAndSaveConfigUrls(JSONObject data) {
        try {
            if(data.has("playerDetailsURL") && !data.isNull("playerDetailsURL")) {
                if(!data.getString("playerDetailsURL").isEmpty()) {
                    bungieCurrentUserUrl = data.getString("playerDetailsURL");
                    Util.setDefaults("playerDetailsURL", bungieCurrentUserUrl, mCurrentAct.get());
                }
            }
            if(data.has("psnLoginURL") && !data.isNull("psnLoginURL")) {
                if(!data.getString("psnLoginURL").isEmpty()) {
                    psnURL = data.getString("psnLoginURL");
                    Util.setDefaults("psnLoginURL", psnURL, mCurrentAct.get());
                }
            }
            if(data.has("xboxLoginURL") && !data.isNull("xboxLoginURL")) {
                if(!data.getString("xboxLoginURL").isEmpty()) {
                    xboxURL = data.getString("xboxLoginURL");
                    Util.setDefaults("xboxLoginURL", xboxURL, mCurrentAct.get());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserFromNetwork(RequestParams rp) {
        try {
            if(mCurrentAct!=null && mCurrentAct.get() instanceof MainActivity) {
                LoginNetwork getUserNtwrk = new LoginNetwork(mCurrentAct.get());
                getUserNtwrk.addObserver(this);
                getUserNtwrk.addObserver((MainActivity)mCurrentAct.get());
                getUserNtwrk.getUser(rp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendInviteBungieMsg(String msgUrl, String body, Context applicationContext, String id) {
        try {
            String csrf = Util.getDefaults("csrf", applicationContext);
            String cookies = Util.getDefaults("cookie", applicationContext);

            if(csrf!=null && !csrf.isEmpty() && cookies!=null && !cookies.isEmpty()) {
                ByteArrayEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                bungieMsgNtwrk = new BungieMessageNetwork(csrf, cookies, applicationContext, msgUrl);
                bungieMsgNtwrk.addObserver(this);
                bungieMsgNtwrk.postBungieMsg(entity, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postAcceptInvite(RequestParams rp) {
        if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof EventDetailActivity) {
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
            eventRelationshipNtwrk.addObserver((EventDetailActivity)mCurrentAct.get());
            eventRelationshipNtwrk.addObserver(this);
            eventRelationshipNtwrk.postEventPlayerRelation(rp, Constants.ACCEPT_EVENT_URL);
        }
    }

    public void postKickPlayer(RequestParams requestParams) {
        if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof EventDetailActivity) {
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
            eventRelationshipNtwrk.addObserver((EventDetailActivity)mCurrentAct.get());
            eventRelationshipNtwrk.addObserver(this);
            eventRelationshipNtwrk.postEventPlayerRelation(requestParams, Constants.KICK_PLAYER_URL);
        }
    }

    public void postCancelPlayer(RequestParams requestParams) {
        if(mCurrentAct.get()!=null && mCurrentAct.get() instanceof EventDetailActivity) {
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(mCurrentAct.get());
            eventRelationshipNtwrk.addObserver((EventDetailActivity)mCurrentAct.get());
            eventRelationshipNtwrk.addObserver(this);
            eventRelationshipNtwrk.postEventPlayerRelation(requestParams, Constants.CANCEL_PLAYER_URL);
        }
    }
}
