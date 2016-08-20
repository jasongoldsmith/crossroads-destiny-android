package co.crossroadsapp.destiny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import co.crossroadsapp.destiny.data.ActivityData;
import co.crossroadsapp.destiny.data.ActivityList;
import co.crossroadsapp.destiny.data.AppVersion;
import co.crossroadsapp.destiny.data.ConsoleData;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.EventList;
import co.crossroadsapp.destiny.data.GroupData;
import co.crossroadsapp.destiny.data.GroupList;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.ActivityListNetwork;
import co.crossroadsapp.destiny.network.AddCommentNetwork;
import co.crossroadsapp.destiny.network.AddNewConsoleNetwork;
import co.crossroadsapp.destiny.network.ChangeCurrentConsoleNetwork;
import co.crossroadsapp.destiny.network.EventByIdNetwork;
import co.crossroadsapp.destiny.network.EventListNetwork;
import co.crossroadsapp.destiny.network.EventRelationshipHandlerNetwork;
import co.crossroadsapp.destiny.network.EventSendMessageNetwork;
import co.crossroadsapp.destiny.network.ForgotPasswordNetwork;
import co.crossroadsapp.destiny.network.GetVersion;
import co.crossroadsapp.destiny.network.GroupListNetwork;
import co.crossroadsapp.destiny.network.HelmetUpdateNetwork;
import co.crossroadsapp.destiny.network.LogoutNetwork;
import co.crossroadsapp.destiny.network.PrivacyLegalUpdateNetwork;
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
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private GetVersion getVersionNetwork;
    private EventSendMessageNetwork eventSendMsgNetwork;
    private ReportCrashNetwork crashReportNetwork;
    private UserData user;

    private Version checkVersion;

    private Activity mCurrentAct;

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

    public void getEventList(ListActivityFragment activity) {
        try {
            eventListNtwrk = new EventListNetwork(activity);
            eventListNtwrk.addObserver(activity);
            //eventListNtwrk.addObserver(this);
            eventListNtwrk.getEvents();
            //todo commenting out get android version for google release
            getAndroidVersion(activity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resendBungieMsg(ListActivityFragment activity) {
        try {
        resendBungieMsg = new ResendBungieVerification(activity);
        resendBungieMsg.addObserver(activity);
        resendBungieMsg.resendBungieMsgVerify();
    } catch (JSONException e) {
        e.printStackTrace();
    }
    }

    public void getEventList() {
        try {
            eventListNtwrk = new EventListNetwork(mCurrentAct);
            eventListNtwrk.addObserver(this);
            eventListNtwrk.getEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getGroupList(ListActivityFragment act) {
        try {
            if (act != null) {
                groupListNtwrk = new GroupListNetwork(act);
                groupListNtwrk.addObserver(this);
                groupListNtwrk.addObserver(act);
            } else {
                groupListNtwrk = new GroupListNetwork(mCurrentAct);
                groupListNtwrk.addObserver(this);
            }
            groupListNtwrk.getGroups();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postSetGroup(ListActivityFragment act, RequestParams params) {
        try{
        groupListNtwrk = new GroupListNetwork(act);
            groupListNtwrk.addObserver(this);
        groupListNtwrk.addObserver(act);
        groupListNtwrk.postSelectGroup(params);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    }

    public void postMuteNoti(ListActivityFragment act, RequestParams params) {
        try{
            if(groupListNtwrk==null) {
                groupListNtwrk = new GroupListNetwork(act);
            }
            groupListNtwrk.addObserver(act);
            groupListNtwrk.postMuteNotification(params);
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

    public void postJoinEvent(Activity activity, RequestParams params) {
        try {
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(activity);
            if (activity instanceof EventDetailActivity){
                eventRelationshipNtwrk.addObserver((EventDetailActivity)activity);
            } else if (activity instanceof ListActivityFragment){
                eventRelationshipNtwrk.addObserver((ListActivityFragment)activity);
            }
            eventRelationshipNtwrk.addObserver(this);
            eventRelationshipNtwrk.postJoin(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postEventMessage(EventDetailActivity ea, String msg, String id, String eventId){

        try {
            eventSendMsgNetwork = new EventSendMessageNetwork(ea);
            eventSendMsgNetwork.addObserver(ea);
            RequestParams rp = new RequestParams();
            rp.put("id", id);
            rp.put("message", msg);
            rp.put("eId", eventId);
            eventSendMsgNetwork.postEventMsg(rp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postGetActivityList(Activity c, RequestParams params) {
        try {
            if(c instanceof AddNewActivity) {
                activityListNetwork = new ActivityListNetwork((AddNewActivity)c);
                activityListNetwork.addObserver(this);
                activityListNetwork.addObserver((AddNewActivity)c);
            } else if (c instanceof ListActivityFragment) {
                activityListNetwork = new ActivityListNetwork((ListActivityFragment)c);
                activityListNetwork.addObserver(this);
                activityListNetwork.addObserver((ListActivityFragment)c);
            }
            if(activityList!=null) {
                activityList.clear();
            }
            activityListNetwork.postGetActivityList(params);
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

    public void postUnJoinEvent(ListActivityFragment activity, RequestParams params) {
        try {
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(activity);
            eventRelationshipNtwrk.addObserver(activity);
            eventRelationshipNtwrk.addObserver(this);
            eventRelationshipNtwrk.postUnJoin(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postUnJoinEvent(EventDetailActivity activity, RequestParams params) {
        try {
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(activity);
            eventRelationshipNtwrk.addObserver(activity);
            eventRelationshipNtwrk.addObserver(this);
            eventRelationshipNtwrk.postUnJoin(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
            loginNetwork = new LoginNetwork(activity);
            loginNetwork.addObserver(this);
            if (postId== Constants.LOGIN) {
                if (activity instanceof LoginActivity) {
                    loginNetwork.addObserver((LoginActivity) activity);
                } else if (activity instanceof MainActivity) {
                    loginNetwork.addObserver((MainActivity) activity);
                }
                loginNetwork.doSignup(params);
            } else if(postId == Constants.REGISTER) {
                loginNetwork.addObserver((RegisterActivity)activity);
                loginNetwork.doRegister(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verifyBungieId(ConsoleSelectionActivity activity, RequestParams params) {
        try {
            verifyConsoleNetwork = new VerifyConsoleIDNetwork(activity);
            verifyConsoleNetwork.addObserver(activity);
            verifyConsoleNetwork.doVerifyConsoleId(params);
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

    public void postLogout(ListActivityFragment act, RequestParams params) {
        try {
            logoutNetwork = new LogoutNetwork(act);
            //logoutNetwork.addObserver(this);
            logoutNetwork.addObserver(act);
            logoutNetwork.doLogout(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postHelmet(ListActivityFragment act) {
        try {
            helmetUpdateNetwork = new HelmetUpdateNetwork(act);
            helmetUpdateNetwork.addObserver(act);
            helmetUpdateNetwork.getHelmet();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postEventById(Activity listActivityFragment, RequestParams param) {
        try {
            eventById = new EventByIdNetwork(listActivityFragment);
            if(listActivityFragment instanceof ListActivityFragment) {
                eventById.addObserver((ListActivityFragment)listActivityFragment);
            } else if(listActivityFragment instanceof EventDetailActivity) {
                eventById.addObserver((EventDetailActivity)listActivityFragment);
            }
            eventById.getEventById(param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showErrorDialogue(String err) {
        if(err==null){
            err = "Request failed. Please wait a few seconds and refresh.";
        }
        if (this.mCurrentAct!=null) {
            if (mCurrentAct instanceof SplashActivity) {
                ((SplashActivity)mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof LoginActivity) {
                ((LoginActivity) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof RegisterActivity) {
                ((RegisterActivity) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof ListActivityFragment) {
                ((ListActivityFragment) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof AddFinalActivity){
                ((AddFinalActivity) mCurrentAct).showError(err);
            } else if (mCurrentAct instanceof EventDetailActivity){
                ((EventDetailActivity) mCurrentAct).showError(err);
            } else if (mCurrentAct instanceof ForgotLoginActivity){
                ((ForgotLoginActivity) mCurrentAct).showError(err);
            } else if (mCurrentAct instanceof ConsoleSelectionActivity) {
                ((ConsoleSelectionActivity) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof ChangePassword) {
                ((ChangePassword) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof MainActivity) {
                ((MainActivity) mCurrentAct).showError(err);
            } else if (mCurrentAct instanceof UpdateConsoleActivity) {
                ((UpdateConsoleActivity)mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof AddFinalActivity) {
                ((AddFinalActivity)mCurrentAct).showError(err);
            }
        }
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
        regIntent = new Intent(mCurrentAct.getApplicationContext(),
                ListActivityFragment.class);
        if (contentIntent != null ) {
            regIntent.putExtra("eventIntent", contentIntent);
        }
//        if (contentIntent != null ) {
//            regIntent = new Intent(mCurrentAct.getApplicationContext(),
//                    ListActivityFragment.class);
//            regIntent.putExtra("eventIntent", contentIntent);
//        } else {
//            if(user.getPsnVerify()!=null && user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
//                if(user.getClanId()!=null && user.getClanId().equalsIgnoreCase(Constants.CLAN_NOT_SET)) {
//                    regIntent = new Intent(mCurrentAct.getApplicationContext(),
//                            ListActivityFragment.class);
//                } else {
//                    if(this.eData!=null && (!this.eData.isEmpty())) {
//                        regIntent = new Intent(mCurrentAct.getApplicationContext(),
//                                ListActivityFragment.class);
//                    } else {
//                        regIntent = new Intent(mCurrentAct.getApplicationContext(),
//                                CreateNewEvent.class);
//                    }
//                }
//            } else {
//                regIntent = new Intent(mCurrentAct.getApplicationContext(),
//                        CreateNewEvent.class);
//            }
//        }
        return regIntent;
    }

    public void setCurrentActivity(Activity act) {
        this.mCurrentAct = act;
    }

    public Context getCurrentActivity() {
        if (this.mCurrentAct!=null) {
            return this.mCurrentAct;
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
            //mCurrentAct.finish();
        } else if (observable instanceof EventRelationshipHandlerNetwork) {
            EventData ed = (EventData) data;
            if (eData!= null) {
                for (int i=0; i<eData.size();i++) {
                    if (ed.getEventId().equalsIgnoreCase(eData.get(i).getEventId())) {
                        if (ed.getMaxPlayer()>0) {
                            eData.remove(i);
                            eData.add(i, ed);
                        } else {
                            eData.remove(i);
                        }
                        break;
                    }
                }
                eData.add(ed);
            }
        } else if (observable instanceof ActivityListNetwork) {
            activityList = new ArrayList<ActivityData>();
            ActivityList al = (ActivityList) data;
            activityList = al.getActivityList();
        } else if(observable instanceof GetVersion) {
            AppVersion ver = (AppVersion) data;
            if (this.mCurrentAct!=null) {
                String currVer = Util.getApplicationVersionCode(this.mCurrentAct);
                String latestVer = ver.getVersion();
                Version currVersion = new Version(currVer);
                Version latestVersion = new Version(latestVer);
                if (latestVersion.compareTo(currVersion)>0){
                    AlertDialog.Builder builder = TravellerDialogueHelper.createConfirmDialogBuilder(this.mCurrentAct, "New Version Available", "A new version of Crossroads is available for download", "Download", "Later", null);

                    builder.setPositiveButton(R.string.download_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.getAppDownloadLink()));
                            mCurrentAct.startActivity(browserIntent);
                        }
                    }).setNegativeButton(R.string.later_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if(mCurrentAct instanceof MainActivity) {
                                getAndroidVersion((MainActivity) mCurrentAct);
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
                getGroupList(null);
            }
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
            eventRelationshipNtwrk = new EventRelationshipHandlerNetwork(activity);
            eventRelationshipNtwrk.addObserver(this);
            if(activity instanceof AddFinalActivity) {
                eventRelationshipNtwrk.addObserver((AddFinalActivity)activity);
            }
            eventRelationshipNtwrk.postCreateEvent(rp);
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
            trackingNetwork.postTracking(rp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void postCrash(CrashReport c, String userId, String s) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("reporter", userId);
        requestParams.put("reportDetails", s);

        try {
            crashReportNetwork = new ReportCrashNetwork(c);
            crashReportNetwork.doCrashReport(requestParams);
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

    public void postComments(EventDetailActivity activity, RequestParams params) {
        try {
            addCommentsNetwork = new AddCommentNetwork(activity);
            addCommentsNetwork.addObserver(activity);
            addCommentsNetwork.postComments(params);

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

    public void addOtherConsole(UpdateConsoleActivity activity, RequestParams rp_console) {
        try {
            addConsoleNetwork = new AddNewConsoleNetwork(activity);
            addConsoleNetwork.addObserver(activity);
            addConsoleNetwork.doAddConsole(rp_console);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeToOtherConsole(ListActivityFragment activity, RequestParams rp_console) {
        try {
            changeCurrentConsoleNetwork = new ChangeCurrentConsoleNetwork(activity);
            changeCurrentConsoleNetwork.addObserver(activity);
            changeCurrentConsoleNetwork.doChangeConsole(rp_console);
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

    public void legalPrivacyDone(ListActivityFragment act) {
        try {
            legalPrivacyNetwork = new PrivacyLegalUpdateNetwork(act);
            legalPrivacyNetwork.addObserver(act);
            legalPrivacyNetwork.postTermsPrivacyDone();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
