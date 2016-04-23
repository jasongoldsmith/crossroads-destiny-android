package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.example.sharmha.travelerfordestiny.data.ActivityData;
import com.example.sharmha.travelerfordestiny.data.ActivityList;
import com.example.sharmha.travelerfordestiny.data.AppVersion;
import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.network.ActivityListNetwork;
import com.example.sharmha.travelerfordestiny.network.EventListNetwork;
import com.example.sharmha.travelerfordestiny.network.EventRelationshipHandlerNetwork;
import com.example.sharmha.travelerfordestiny.network.EventSendMessageNetwork;
import com.example.sharmha.travelerfordestiny.network.GetVersion;
import com.example.sharmha.travelerfordestiny.network.LogoutNetwork;
import com.example.sharmha.travelerfordestiny.network.ReportCrashNetwork;
import com.example.sharmha.travelerfordestiny.network.postGcmNetwork;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.example.sharmha.travelerfordestiny.data.EventList;
import com.example.sharmha.travelerfordestiny.network.LoginNetwork;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.example.sharmha.travelerfordestiny.utils.ErrorShowDialog;
import com.example.sharmha.travelerfordestiny.utils.TravellerDialogueHelper;
import com.example.sharmha.travelerfordestiny.utils.Version;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;

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
    private GetVersion getVersionNetwork;
    private EventSendMessageNetwork eventSendMsgNetwork;
    private ReportCrashNetwork crashReportNetwork;
    private UserData user;

    private Version checkVersion;

    private Activity mCurrentAct;

    private static final String TAG = ControlManager.class.getSimpleName();

    private EventList eList;
    private ArrayList<EventData> eData;
    private ArrayList<ActivityData> activityList;
    private ArrayList<ActivityData> raidActivityList;
    private ArrayList<ActivityData> crucibleActivityList;

    private static ControlManager mInstance;

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

    public void getEventList(ListActivityFragment activity) {
        try {
            eventListNtwrk = new EventListNetwork(activity);
            eventListNtwrk.addObserver(activity);
            eventListNtwrk.addObserver(this);
            eventListNtwrk.getEvents();
            getAndroidVersion(activity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void postGetActivityList(CreateNewEvent c) {
        try {
                activityListNetwork = new ActivityListNetwork(c);
                //activityListNetwork.addObserver(c);
                activityListNetwork.addObserver(this);
                activityListNetwork.postGetActivityList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postGetActivityList(ListActivityFragment c) {
        try {
            activityListNetwork = new ActivityListNetwork(c);
            activityListNetwork.addObserver(this);
            activityListNetwork.postGetActivityList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                if (activityList.get(i).getActivitySubtype().equalsIgnoreCase(subtype) && activityList.get(i).getActivityDifficulty().equalsIgnoreCase(diff)){
                    checkpointActivityList.add(activityList.get(i));
                }
            }
            return checkpointActivityList;
        }

        return null;
    }

    public void postLogin(Activity activity, RequestParams params, int postId) {
        try {
            loginNetwork = new LoginNetwork(activity);
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

    public void postLogout(ListActivityFragment act, RequestParams params) {
        try {
            logoutNetwork = new LogoutNetwork(act);
            logoutNetwork.addObserver(this);
            logoutNetwork.doLogout(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showErrorDialogue(String err) {
        if (this.mCurrentAct!=null) {
            if (mCurrentAct instanceof SplashActivity) {
                ((SplashActivity)mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof LoginActivity) {
                ((LoginActivity) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof RegisterActivity) {
                ((RegisterActivity) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof ListActivityFragment) {
                ((ListActivityFragment) mCurrentAct).showError(err);
            } else if(mCurrentAct instanceof CreateNewEvent){
                ((CreateNewEvent) mCurrentAct).dismissProgressBar();
                ((CreateNewEvent) mCurrentAct).showError(err);
            }
        }
    }

    public void getAndroidVersion(ListActivityFragment activity) {
        getVersionNetwork = new GetVersion(activity);
        getVersionNetwork.addObserver(this);
        try {
            getVersionNetwork.getAppVer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            EventList eList = (EventList) data;
            eData = eList.getEventList();
        } else if (observable instanceof LogoutNetwork){
            mCurrentAct.finish();
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
                    AlertDialog.Builder builder = TravellerDialogueHelper.createConfirmDialogBuilder(this.mCurrentAct, "New Version Available", "A new version of Traveler is available for download", "Download", "Later", null);

                    builder.setPositiveButton(R.string.download_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/GSLxIW"));
                            mCurrentAct.startActivity(browserIntent);
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog dialog = builder.create();
                    ErrorShowDialog.show(dialog);
                }

            }
        }
    }

    public void postCreateEvent(String activityId, String creator_id, int minP, int maxP, String dateTime, CreateNewEvent activity) {
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
            eventRelationshipNtwrk.addObserver(activity);
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
}
