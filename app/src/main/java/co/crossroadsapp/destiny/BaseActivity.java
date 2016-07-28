package co.crossroadsapp.destiny;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.PushNotification;
import co.crossroadsapp.destiny.utils.Util;

public class BaseActivity extends FragmentActivity {

    protected RelativeLayout errLayout;
    protected TextView errText;
    private RelativeLayout progress;
    private ControlManager mManager = ControlManager.getmInstance();
    private static ArrayList<PushNotification> notiList;
    private ArrayList<PushNotification> eventNotiList=new ArrayList<PushNotification>();

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
                if(data!=null && data.get(position)!=null) {
                    final String id = data.get(position).geteId();
                    String name = data.get(position).geteName();
                    String msg = data.get(position).getMessage();
                    boolean typeM = data.get(position).getTypeMessage();

                    CardView card = (CardView) v.findViewById(R.id.base_test_card);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (id != null) {
                                goToDetail(id);
                            }
                        }
                    });
                    TextView notiEventText = (TextView) v.findViewById(R.id.noti_text);
                    TextView notiTopText = (TextView) v.findViewById(R.id.noti_toptext);
                    TextView notiMessage = (TextView) v.findViewById(R.id.noti_subtext);
                    if(name!=null) {
                        notiEventText.setText(data.get(position).geteName());
                    }
                    if (typeM) {
                        notiTopText.setText("FIRETEAM MESSAGE");
                        notiMessage.setVisibility(View.VISIBLE);
                        if(msg!=null) {
                            notiMessage.setText(data.get(position).getMessage());
                        }
                    } else {
                        if(msg!=null) {
                            notiEventText.setText(data.get(position).getMessage());
                        }
                        if (name!=null) {
                            notiTopText.setText(name.toUpperCase());
                        }
                        notiMessage.setVisibility(View.GONE);
                        //mManager.getEventList(ListActivityFragment.this);
                    }
                }
            }
            return v;
        }
    }

    protected void goToDetail(String s) {

    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
  //          if (mManager.getCurrentActivity() == ListActivityFragment) {
            PushNotification noti = new PushNotification();
                String subtype = intent.getStringExtra("subtype");
                boolean playerMsg = intent.getBooleanExtra("playerMessage", false);
            String eventId = intent.getStringExtra("eventId");
            String eventUpdated = intent.getStringExtra("eventUpdated");
                String msg = intent.getStringExtra("message");
            noti.seteId(eventId);
            noti.seteName(subtype);
            noti.seteUpdated(eventUpdated);
            noti.setTypeMessage(playerMsg);
            noti.setMessage(msg);
            if(notiList==null) {
                notiList = new ArrayList<PushNotification>();
            }
            notiList.add(noti);
            if(context instanceof ListActivityFragment) {
                showNotifications(context);
            } else if(context instanceof EventDetailActivity) {
                if(eventNotiList==null){
                    eventNotiList = new ArrayList<PushNotification>();
                }
                if(noti.getTypeMessage()) {
                    eventNotiList.add(noti);
                    showNotifications(context);
                }
            }
                //put timer to make the notification message gone after 5 seconds
//                notiBar.postDelayed(new Runnable() {
//                    public void run() {
//                        if(notiBar!=null) {
//                            notiBar.setVisibility(View.GONE);
//                        }
//                    }
//                }, 7000);
  //          }
        }
    };

    SwipeStackAdapter adapter;
    View view;

    protected void showNotifications(final Context c) {
//        LayoutInflater inflater = getLayoutInflater();
        if(c instanceof ListActivityFragment) {
            view = ((ListActivityFragment)c).getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
        } else if (c instanceof EventDetailActivity) {
            view = ((EventDetailActivity)c).getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
            if(eventNotiList==null) {
                eventNotiList = new ArrayList<PushNotification>();
            }
            if(((EventDetailActivity)c).currEvent!=null) {
                getEventNotification(((EventDetailActivity)c).currEvent);
            }
        }
        if(view!=null) {
            //final View v = inflater.inflate(R.layout.base_notification, (ViewGroup) view);
            SwipeDeck cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
            if(adapter != null) {
                adapter = null;
            }
            if (notiList==null) {
                notiList = new ArrayList<PushNotification>();
            }
            adapter = new SwipeStackAdapter(c instanceof ListActivityFragment?notiList:eventNotiList, c);
            cardStack.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
                @Override
                public void cardSwipedLeft(int position) {
                    checkAndRemoveNoti(c, position);
                    //notiList.remove(position);

                }

                @Override
                public void cardSwipedRight(int position) {
                    checkAndRemoveNoti(c, position);
                }

                @Override
                public void cardsDepleted() {
                    //removeNotifyLayout();
                }

                @Override
                public void cardActionDown() {

                }

                @Override
                public void cardActionUp() {

                }
            });
        }
    }

    private void checkAndRemoveNoti(Context c, int position) {
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

    private void removeEventNotiFromNotiList(String id) {
        if (notiList!=null){
            for (int i=0; i<notiList.size(); i++) {
                if (notiList.get(i)!=null && notiList.get(i).geteId()!=null) {
                    if(notiList.get(i).geteId().equalsIgnoreCase(id)) {
                        notiList.remove(i);
                        i--;
                    }
                }
            }
        }
    }

    private void getEventNotification(EventData currEvent) {
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
