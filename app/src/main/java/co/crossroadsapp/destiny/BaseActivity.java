package co.crossroadsapp.destiny;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.PushNotification;
import co.crossroadsapp.destiny.utils.CircularImageView;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;

public class BaseActivity extends FragmentActivity {

    protected RelativeLayout errLayout;
    protected TextView errText;
    private RelativeLayout progress;
    private RelativeLayout deeplinkError;
    private ControlManager mManager = ControlManager.getmInstance();
    protected static ArrayList<PushNotification> notiList;
    protected ArrayList<PushNotification> eventNotiList=new ArrayList<PushNotification>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromService, new IntentFilter("subtype_flag"));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(ReceivefromService);
    }

    protected void setErrText(String errorText) {
        if(errText==null ){
            errText = (TextView) findViewById(R.id.error_sub);
        }

        if (errLayout==null) {
            errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        }

        // show timed error message
        Util.showErrorMsg(errLayout, errText, errorText);

//        if(errText!=null && errLayout!=null) {
//            errLayout.setVisibility(View.GONE);
//            errLayout.setVisibility(View.VISIBLE);
//            errText.setText(errorText);
//
//            //put timer to make the error message gone after 5 seconds
//            errLayout.postDelayed(new Runnable() {
//                public void run() {
//                    if(errLayout!=null) {
//                        errLayout.setVisibility(View.GONE);
//                    }
//                }
//            }, 5000);
//        }
    }

    public void closeView(View view) {
        if(errLayout!=null) {
            errLayout.setVisibility(View.GONE);
        }
    }

    public void showDeeplinkError(int eventFull, final String deepLinkEvent, String deepLinkName, final String clanId) {
        deeplinkError = (RelativeLayout) findViewById(R.id.deeplink_error);
        TextView errMsg = (TextView) findViewById(R.id.msg);
        TextView btnText = (TextView) findViewById(R.id.btn_text);
        CardView btn = (CardView) findViewById(R.id.add_btn);
        deeplinkError.setVisibility(View.VISIBLE);
        TextView noBtn = (TextView) findViewById(R.id.no_thanks);
        ImageView close= (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deeplinkError.setVisibility(View.GONE);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deeplinkError.setVisibility(View.GONE);
            }
        });
        switch(eventFull) {
            case 2:
                errMsg.setText("Sorry, that " +deepLinkEvent+ " is no longer available. Would you like to add one of your own?");
                btnText.setText("ADD THIS ACTIVITY");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // go to create new event page
                        Intent regIntent = new Intent(getApplicationContext(),
                                AddNewActivity.class);
                        regIntent.putExtra("userdata", mManager.getUserData());
                        startActivity(regIntent);
                    }
                });
                break;
            case 1:
                errMsg.setText("You’ll need to be in the " +deepLinkEvent+ " group to join " + deepLinkName+ ". Request to join?");
                btnText.setText("VIEW GROUP ON BUNGIE.NET");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "http://www.bungie.net";
                        if(clanId!=null && !clanId.isEmpty()) {
                            uri = "http://www.bungie.net/en/clan/"+clanId;
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        mManager.getCurrentActivity().startActivity(browserIntent);
                    }
                });
                break;
            case 3:
                errMsg.setText(getResources().getString(R.string.deeplink_full_err));
                btnText.setText("YES");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // go to create new event page
                        Intent regIntent = new Intent(getApplicationContext(),
                                AddNewActivity.class);
                        regIntent.putExtra("userdata", mManager.getUserData());
                        startActivity(regIntent);
                    }
                });
                break;
            case 4:
                errMsg.setText("You’ll need to be on "+deepLinkName+" to join that activity from "+deepLinkEvent+ ". Add another console to your account?");
                btnText.setText("ADD MY "+deepLinkName);
                btnText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to create new event page
                    Intent regIntent = new Intent(getApplicationContext(),
                            UpdateConsoleActivity.class);
                    regIntent.putExtra("userdata", mManager.getUserData());
                    startActivity(regIntent);
                }
                });
                break;
        }
    }

    public void hideDeeplinkError() {
        if (deeplinkError != null) {
            deeplinkError.setVisibility(View.GONE);
        }
    }

    public class SwipeStackAdapter extends BaseAdapter {

        private ArrayList<PushNotification> data;
        private Context context;

        public SwipeStackAdapter(ArrayList<PushNotification> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if(v == null) {
//                Display display = getWindowManager().getDefaultDisplay();
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.base_notification_card, null);
            }
                if(data!=null && data.get(position)!=null) {
                    final String id = data.get(position).geteId();
                    String name = data.get(position).geteName();
                    String msg = data.get(position).getMessage();
                    boolean typeM = data.get(position).getTypeMessage();

                    final String console = data.get(position).getEventConsole();
                    final String grpId = data.get(position).getEventClanId();
                    String grpName = data.get(position).getEventClanName();
                    String grpImage = data.get(position).getEventClanImageUrl();
                    String msngrConsoleId = data.get(position).getMessengerConsoleId();
                    String msngrImage = data.get(position).getMessengerImageUrl();

                    //CardView card = (CardView) v.findViewById(R.id.base_test_card);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id != null) {
                                goToDetail(id, console, grpId);
                            }
                        }
                    });
                    TextView notiEventText = (TextView) v.findViewById(R.id.noti_text);
                    TextView notiTopText = (TextView) v.findViewById(R.id.noti_toptext);
                    TextView notiTopSubText = (TextView) v.findViewById(R.id.noti_topsubtext);
                    ImageView grpImageView = (ImageView) v.findViewById(R.id.noti_icon);
                    CircularImageView msngrImageView = (CircularImageView) v.findViewById(R.id.player_msg_pic);

                    if (typeM) {
                        if(msngrConsoleId!=null) {
                            notiTopText.setText(msngrConsoleId);
                        }
                        if(msngrImage!=null) {
                            Picasso.with(getApplicationContext())
                                    .load(msngrImage)
                                    .placeholder(R.drawable.icon_alert)
                                    .fit().centerCrop()
                                    .into(msngrImageView);
                        }
                        grpImageView.setVisibility(View.GONE);
                        msngrImageView.setVisibility(View.VISIBLE);
                        notiTopSubText.setVisibility(View.GONE);
                        if(msg!=null) {
                            notiEventText.setText(data.get(position).getMessage());
                        }
                    } else {
                        grpImageView.setVisibility(View.VISIBLE);
                        msngrImageView.setVisibility(View.GONE);
                        notiTopSubText.setVisibility(View.VISIBLE);
                        if (name!=null) {
                            notiTopSubText.setText(name.toUpperCase());
                        }
                        if (console!=null && grpName!=null) {
                            String first = console +": "+ grpName;
                            notiTopText.setText(first);
                        }
                        if(grpImage!=null && (!grpImage.isEmpty())) {
                            Picasso.with(getApplicationContext())
                                    .load(grpImage)
                                    .placeholder(R.drawable.icon_alert)
                                    .fit().centerCrop()
                                    .into(grpImageView);
                        }
                    }
                    if(msg!=null) {
                        notiEventText.setText(data.get(position).getMessage());
                    }
                }
            //}
            return v;
        }
    }

    protected void goToDetail(String s, String console, String clanId) {

    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //          if (mManager.getCurrentActivity() == ListActivityFragment) {
            PushNotification noti = new PushNotification();
            if(intent.hasExtra("subtype")) {
                String subtype = intent.getStringExtra("subtype");
                noti.seteName(subtype);
            }
            if(intent.hasExtra("playerMessage")) {
                boolean playerMsg = intent.getBooleanExtra("playerMessage", false);
                noti.setTypeMessage(playerMsg);
            }
            if(intent.hasExtra("eventId")) {
                String eventId = intent.getStringExtra("eventId");
                noti.seteId(eventId);
            }
            if(intent.hasExtra("eventUpdated")) {
                String eventUpdated = intent.getStringExtra("eventUpdated");
                noti.seteUpdated(eventUpdated);
            }
            if(intent.hasExtra("message")) {
                String msg = intent.getStringExtra("message");
                noti.setMessage(msg);
            }
            if(intent.hasExtra("eventClanId")) {
                String eventClanId = intent.getStringExtra("eventClanId");
                noti.setEventClanId(eventClanId);
            }
            if(intent.hasExtra("eventClanName")) {
                String eventClanName = intent.getStringExtra("eventClanName");
                noti.setEventClanName(eventClanName);
            }
            if(intent.hasExtra("eventClanImageUrl")) {
                String eventClanImageUrl = intent.getStringExtra("eventClanImageUrl");
                noti.setEventClanImageUrl(eventClanImageUrl);
            }
            if(intent.hasExtra("eventConsole")) {
                String eventConsole = intent.getStringExtra("eventConsole");
                noti.setEventConsole(eventConsole);
            }
            if(intent.hasExtra("messengerConsoleId")) {
                String messengerConsoleId = intent.getStringExtra("messengerConsoleId");
                noti.setMessengerConsoleId(messengerConsoleId);
            }
            if(intent.hasExtra("messengerImageUrl")) {
                String messengerImageUrl = intent.getStringExtra("messengerImageUrl");
                noti.setMessengerImageUrl(messengerImageUrl);
            }

            if (notiList == null) {
                notiList = new ArrayList<PushNotification>();
            }
            notiList.add(noti);
            //
            Context act = mManager.getCurrentActivity();
            if (act instanceof EventDetailActivity) {
                if(noti.getTypeMessage()) {
                    ((EventDetailActivity) act).showNotifications();
                }
            } else if (act instanceof ListActivityFragment) {
                ((ListActivityFragment) act).showNotifications();
            }
        }
    };

    protected void checkAndRemoveNoti(Context c, int position, SwipeStackAdapter adapter) {
        if(c!=null) {
            if (c instanceof ListActivityFragment) {
                notiList.remove(position);
                adapter.notifyDataSetChanged();
            } else {
                if(eventNotiList!=null && eventNotiList.get(position)!=null){
                    if(eventNotiList.get(position)!=null) {
                        notiList.remove(eventNotiList.get(position));
                        //removeEventNotiFromNotiList(eventNotiList.get(position).geteId());
                        eventNotiList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    protected void getEventNotification(EventData currEvent) {
        if(currEvent.getEventId()!=null) {
            String id = currEvent.getEventId();
            if (notiList!=null){
                for (int i=0; i<notiList.size(); i++) {
                    if (notiList.get(i)!=null && notiList.get(i).geteId()!=null) {
                        if(notiList.get(i).geteId().equalsIgnoreCase(id)) {
                            if(notiList.get(i).getTypeMessage()){
                                eventNotiList.add(notiList.get(i));
                            } else {
                                notiList.remove(i);
                                i--;
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeNotifyLayout() {

    }

    protected void showProgressBar() {
        if(progress == null) {
            progress = (RelativeLayout) findViewById(R.id.progress_base_layout);
        }

        if(progress!=null) {
            hideProgressBar();
            progress.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if(progress!=null) {
            progress.setVisibility(View.GONE);
        }
    }
}
