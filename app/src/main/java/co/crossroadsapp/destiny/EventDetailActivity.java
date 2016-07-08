package co.crossroadsapp.destiny;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.crossroadsapp.destiny.data.CurrentEventDataHolder;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.PlayerData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.EventByIdNetwork;
import co.crossroadsapp.destiny.network.EventRelationshipHandlerNetwork;
import co.crossroadsapp.destiny.network.EventSendMessageNetwork;
import co.crossroadsapp.destiny.utils.CircularImageView;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 3/29/16.
 */
public class EventDetailActivity extends BaseActivity implements Observer {

    EventData currEvent;
    private TextView eventName;
    private TextView eventSubName;
    private TextView eventSubNameLF;
    private ImageView eventProfileImg;
    private TextView eventLight;
    private UserData user;
    private ImageView back;
    View view;

    private RecyclerView mRecyclerView;
    private CurrentEventsViewAdapter mAdapter;
    private CircularImageView userProfile;
    private ControlManager controlManager;
    private CurrentEventDataHolder inst;
    private RelativeLayout sendMessageLayout;
    private RelativeLayout sendmsg_bckgrnd;

    private EditText editText;
    private ImageView sendBtn;
    private TextView joinBtn;
    private TextView leaveBtn;
    private TextView msgallBtn;

    private RelativeLayout notiBar;
    private TextView notiMessage;
    private TextView notiTopText;
    private TextView notiEventText;

    private TextView editMsgPlayer;
    private Handler _handler;
    private boolean joinBtnActive;
    private TextView eventDetailDate;
    private boolean userIsPlayer;
    private ImageView notif_close;
    private RelativeLayout progressBar;
    private TextView eventCheckpoint;
    private TextView mCharacter;
    private RelativeLayout bottomBtnLayout;
    private ValueEventListener listener;
    private Firebase refFirebase;
//    private TextView errText;
//    private ImageView close_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_list);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        joinBtn = (TextView) findViewById(R.id.join_btn);
        leaveBtn = (TextView) findViewById(R.id.leave_btn);
        msgallBtn = (TextView) findViewById(R.id.messageall_btn);
        bottomBtnLayout = (RelativeLayout) findViewById(R.id.detail_bottom_btn);

        progressBar = (RelativeLayout) findViewById(R.id.progress_bar_eventdetail_layout);

        eventCheckpoint = (TextView) findViewById(R.id.eventDetailCheckpoint);

        //setTRansparentStatusBar();

        controlManager = ControlManager.getmInstance();

        controlManager.setCurrentActivity(EventDetailActivity.this);

        _handler = new Handler();

        userProfile = (CircularImageView) findViewById(R.id.userProfile_event);

        currEvent = new EventData();

        inst = CurrentEventDataHolder.getInstance();
        currEvent = inst.getData();
        //joinBtnActive = inst.getJoinVisible();

        eventProfileImg = (ImageView) findViewById(R.id.event_detail_icon);
        eventName = (TextView) findViewById(R.id.activity_name_detail);
        eventSubName = (TextView) findViewById(R.id.activity_player_name_detail);
        eventSubNameLF = (TextView) findViewById(R.id.activity_player_name_lf_detail);
        eventLight = (TextView) findViewById(R.id.activity_aLight_detail);
        back = (ImageView) findViewById(R.id.eventdetail_backbtn);
        eventDetailDate = (TextView) findViewById(R.id.eventDetailDate);

        //Firebase.setAndroidContext(this);

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

        sendmsg_bckgrnd = (RelativeLayout) findViewById(R.id.sendmsg_background);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText = (EditText) findViewById(R.id.edittext);
        editText.addTextChangedListener(mTextEditorWatcher);

        mCharacter = (TextView) findViewById(R.id.character_count);

        sendBtn = (ImageView) findViewById(R.id.send_btn);

        notiBar = (RelativeLayout) findViewById(R.id.notification_bar);
        notiEventText = (TextView) findViewById(R.id.noti_text);

        notiTopText = (TextView) findViewById(R.id.noti_toptext);
        notiMessage = (TextView) findViewById(R.id.noti_subtext);
        notiMessage.setMovementMethod(new ScrollingMovementMethod());
        notif_close = (ImageView) findViewById(R.id.noti_close);
        notif_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notiBar.setVisibility(View.GONE);
            }
        });

        if(currEvent.getActivityData().getActivityIconUrl()!=null) {
            Util.picassoLoadIcon(EventDetailActivity.this, eventProfileImg, currEvent.getActivityData().getActivityIconUrl(), R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.icon_ghost_default);
        }

        if (currEvent.getActivityData().getActivitySubtype()!=null) {
            eventName.setText(currEvent.getActivityData().getActivitySubtype());
        }

        if (currEvent.getActivityData().getActivityLight()> 0) {
            // unicode to show star
            String st = "\u2726";
            eventLight.setText(st + currEvent.getActivityData().getActivityLight());
        } else if(currEvent.getActivityData().getActivityLevel()>0) {
            eventLight.setText("lvl " + currEvent.getActivityData().getActivityLevel());
        }

        if(currEvent.getActivityData().getActivityCheckpoint()!=null && (!currEvent.getActivityData().getActivityCheckpoint().equalsIgnoreCase("null"))){
            eventCheckpoint.setVisibility(View.VISIBLE);
            eventCheckpoint.setText(currEvent.getActivityData().getActivityCheckpoint());
        }

        if (user.getImageUrl()!=null){
            Util.picassoLoadIcon(EventDetailActivity.this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.img_avatar_you);
        }

        checkUserIsPlayer();
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
                hideProgress();
                showProgress();
                controlManager.postJoinEvent(EventDetailActivity.this, rp);
            }
        });

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
                hideProgress();
                showProgress();
                controlManager.postUnJoinEvent(EventDetailActivity.this, rp);
            }
        });

        if (currEvent.getLaunchEventStatus().equalsIgnoreCase(Constants.LAUNCH_STATUS_UPCOMING)) {
            String date  = Util.convertUTCtoReadable(currEvent.getLaunchDate());
            if (date!=null){
                eventDetailDate.setText(date);
            }
        } else {
            eventDetailDate.setText("");
        }

        setPlayerNames();

        userIsPlayer = checkUserIsPlayer();

        setBottomButtonSelection();

        sendMessageLayout = (RelativeLayout) findViewById(R.id.send_message_layout);

        sendmsg_bckgrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg_bckgrnd.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

        editMsgPlayer = (TextView) findViewById(R.id.textmsg_player);

        //checkLeaveBtn();

        mRecyclerView = (RecyclerView) findViewById(R.id.event_player);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CurrentEventsViewAdapter(currEvent.getPlayerData());

        mRecyclerView.setAdapter(mAdapter);

        //setSendMsgToAllVisibility();

        msgallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToAll();
            }
        });

        registerFirbase();

    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            mCharacter.setText(String.valueOf(80 - s.toString().length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(ReceivefromService, new IntentFilter("subtype_flag"));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(ReceivefromService);
        unregisterFirebase();
    }

    private void registerFirbase() {
        setupEventListener();
        if(controlManager!=null) {
            if(controlManager.getUserData()!=null) {
                this.user = controlManager.getUserData();
            }
        }
        if(user!=null && user.getClanId()!=null) {
            if(currEvent!=null && currEvent.getEventId()!=null && currEvent.getClanId()!=null) {
                refFirebase = new Firebase(Util.getFirebaseUrl(currEvent.getClanId(), currEvent.getEventId(), Constants.EVENT_CHANNEL));
                if (listener != null) {
                    refFirebase.addValueEventListener(listener);
                }
            }
        }
    }

    private void setupEventListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                            if(currEvent!=null && currEvent.getEventId()!=null) {
                                String id = currEvent.getEventId();
                                RequestParams param = new RequestParams();
                                param.add("id", id);
                                controlManager.postEventById(EventDetailActivity.this, param);
                            }
                } else {
                    launchListActivityAndFinish();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
    }

    private void unregisterFirebase() {
        if(listener!=null) {
            refFirebase.removeEventListener(listener);
            //refFirebase.removeValue();
        }
    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String subtype = intent.getStringExtra("subtype");
            boolean playerMsg = intent.getBooleanExtra("playerMessage", false);
            String msg = intent.getStringExtra("message");
            notiEventText.setText(subtype);
            if(playerMsg){
                notiTopText.setText("FIRETEAM MESSAGE");
                notiMessage.setVisibility(View.VISIBLE);
                notiMessage.setText(msg);
            }else {
                notiEventText.setText("Your Fireteam is ready!");
                notiTopText.setText(subtype);
                notiMessage.setVisibility(View.GONE);
                //mManager.getEventList(ListActivityFragment.this);
            }
            notiBar.setVisibility(View.VISIBLE);
        }
    };

    private void setBottomButtonSelection() {
        if(checkUserIsCreator()){
            if((this.currEvent.getPlayerData()!=null) && this.currEvent.getPlayerData().size()>1) {
                bottomBtnLayout.setVisibility(View.VISIBLE);
                leaveBtn.setVisibility(View.GONE);
                joinBtn.setVisibility(View.GONE);
                msgallBtn.setVisibility(View.VISIBLE);
            } else {
                bottomBtnLayout.setVisibility(View.VISIBLE);
                leaveBtn.setVisibility(View.VISIBLE);
                joinBtn.setVisibility(View.GONE);
                msgallBtn.setVisibility(View.GONE);
            }
        } else if (checkUserIsPlayer()) {
            bottomBtnLayout.setVisibility(View.VISIBLE);
            leaveBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);
            msgallBtn.setVisibility(View.GONE);
        } else if(!currEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL)){
            bottomBtnLayout.setVisibility(View.VISIBLE);
            leaveBtn.setVisibility(View.GONE);
            joinBtn.setVisibility(View.VISIBLE);
            msgallBtn.setVisibility(View.GONE);
        }
    }

    private void sendMsgToAll() {
        if (sendmsg_bckgrnd != null) {
            sendmsg_bckgrnd.setVisibility(View.VISIBLE);
            editMsgPlayer.setText("All Players");
            showKeyboard();
        }

        if (sendBtn != null) {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                String msg = null;

                @Override
                public void onClick(View v) {
                    _handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < currEvent.getPlayerData().size(); i++) {
                                if (i == 0) {
                                    msg = getEditText();
                                }
                                if (!user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())) {
                                    //v.setEnabled(false);
                                    if (msg != null) {
                                        sendMessage(currEvent.getPlayerData().get(i).getPlayerId(), msg);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void showProgress() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if(progressBar!=null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setPlayerNames() {
        String allNames = "";
        String allNamesRem = "";
        int reqPlayer = currEvent.getActivityData().getMaxPlayer() - currEvent.getPlayerData().size();

        allNames = currEvent.getCreatorData().getPsnId();
        if (!currEvent.getEventStatus().equalsIgnoreCase("full")) {
            allNamesRem = " " + "LF" +reqPlayer+"M";
        }

        eventSubName.setText(allNames);
        eventSubNameLF.setText(allNamesRem);
    }

    @Override
    public void update(Observable observable, Object data) {
            hideProgress();
            if (observable instanceof EventRelationshipHandlerNetwork || observable instanceof EventByIdNetwork) {
                this.currEvent = (EventData) data;
                if (currEvent != null) {
                    if ((currEvent.getPlayerData() != null) && (currEvent.getPlayerData().size()>0)) {
                        if (mAdapter != null) {
                            mAdapter.playerLocal.clear();
                            mAdapter.addItem(currEvent.getPlayerData());
                            mAdapter.notifyDataSetChanged();
                        }
                        setPlayerNames();
                        setBottomButtonSelection();
                    } else {
                        launchListActivityAndFinish();
                    }
                }
            } else if (observable instanceof EventSendMessageNetwork) {
                editText.setText("");
                hideSendMsgBckground();
            }
    }

    private void hideSendMsgBckground() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        sendmsg_bckgrnd.setVisibility(View.GONE);
        hideKeyboard();
    }

    private boolean checkUserIsPlayer(){
        if(this.currEvent.getPlayerData()!=null) {
            for (int i = 0; i < currEvent.getPlayerData().size(); i++) {
                if (user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkUserIsCreator() {
        if(this.currEvent.getPlayerData()!=null) {
            if (user.getUserId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId())) {
                return true;
            }
        }
        return false;
    }

    public void showError(String err) {
        hideProgress();
//        errLayout.setVisibility(View.VISIBLE);
//        errText.setText(err);
        setErrText(err);
    }

    private class CurrentEventsViewAdapter extends RecyclerView.Adapter<CurrentEventsViewAdapter.CurrentEventsViewHolder> {

        private ArrayList<PlayerData> playerLocal;

        protected void addItem(ArrayList<PlayerData> a) {
            this.playerLocal.addAll(a);
        }

        public CurrentEventsViewAdapter(ArrayList<PlayerData> playerData) {
            playerLocal = new ArrayList<PlayerData>();
            playerLocal = playerData;
        }


        public class CurrentEventsViewHolder extends RecyclerView.ViewHolder {
            private CircularImageView playerProfile;
            private TextView playerName;
            private CardView playerCard;
            private ImageView message;

            public CurrentEventsViewHolder(View itemView) {
                super(itemView);
                view = itemView;

                playerProfile = (CircularImageView) itemView.findViewById(R.id.event_detail_player_profile);
                playerName = (TextView) itemView.findViewById(R.id.player_name);
                playerCard = (CardView) itemView.findViewById(R.id.activity_card);
                message = (ImageView) itemView.findViewById(R.id.event_detail_message);
            }
        }

        @Override
        public CurrentEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_eventdetail_playercard, null);
            return new CurrentEventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CurrentEventsViewHolder holder, final int position) {
            String currPlayerId = null;
            if(playerLocal!=null) {
                if (position >= playerLocal.size() && getMaxPlayer() > playerLocal.size() ) {
                    holder.playerName.setText("searching...");
                    holder.message.setVisibility(View.GONE);
                    holder.playerProfile.setImageResource(R.drawable.img_profile_blank);
                } else {
                    if (playerLocal.get(position) != null) {
                        if (playerLocal.get(position).getPlayerId() != null) {
                            currPlayerId = playerLocal.get(position).getPlayerId();
                        }
                        if (playerLocal.get(position).getPlayerImageUrl() != null) {
                            Util.picassoLoadIcon(EventDetailActivity.this, holder.playerProfile, playerLocal.get(position).getPlayerImageUrl(),
                                    R.dimen.eventdetail_player_profile_hgt, R.dimen.eventdetail_player_profile_width, R.drawable.img_profile_blank);
                        }
                        if (playerLocal.get(position).getPsnId() != null) {
                            holder.playerName.setText(playerLocal.get(position).getPsnId());
                        }

                        holder.message.setVisibility(View.GONE);

                        if (checkUserIsCreator() && (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))) {
                            holder.message.setVisibility(View.VISIBLE);
                        }
                        if ((checkUserIsPlayer() && !checkUserIsCreator()) && (playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))) {
                            holder.message.setVisibility(View.VISIBLE);
                        }

                        final String finalCurrPlayerId = currPlayerId;
                        holder.message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (sendmsg_bckgrnd != null) {
                                    sendmsg_bckgrnd.setVisibility(View.VISIBLE);
                                    editMsgPlayer.setText(playerLocal.get(position).getPsnId());
                                    showKeyboard();
                                }

                                if (sendBtn != null) {
                                    sendBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //v.setEnabled(false);
                                            String s = getEditText();
                                            if (s != null) {
                                                sendMessage(finalCurrPlayerId, s);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        holder.playerProfile.invalidate();
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return getMaxPlayer();
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

    }

    private int getMaxPlayer() {
        if(currEvent!=null){
            if(currEvent.getActivityData()!=null){
                if(currEvent.getActivityData().getMaxPlayer()>0){
                    return currEvent.getActivityData().getMaxPlayer();
                }
            }
        }
        return 0;
    }

    private void showKeyboard() {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard() {
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private String getEditText() {
        if (editText != null) {
            if (editText.getText() != null) {
                if (editText.getText().toString().length() > 0) {
                    return editText.getText().toString();
                }
            }
        }
        return null;
    }

    private void sendMessage(final String currentPlayerId, final String msg) {
        //final String pId = currentPlayerId;
//        if (editText != null) {
//            if (editText.getText() != null) {
//                if (editText.getText().toString().length() > 0) {
                    if (currEvent != null && currEvent.getEventId() != null) {
                        editText.setText("");
//                        _handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                                controlManager.postEventMessage(EventDetailActivity.this, msg, currentPlayerId, currEvent.getEventId());
                        hideSendMsgBckground();
//                            }
//                            //hideSendMsgBckground();
//                        }, 1000);
                    }
//                }
//            }
//        }
    }

    private void launchListActivityAndFinish() {
        Intent i=new Intent (this, ListActivityFragment.class);
        i.putExtra("userdata", user);
        startActivity(i);
        currEvent = null;
        finish();
    }

    @Override
    public void onBackPressed() {
        if(sendmsg_bckgrnd.getVisibility()==View.VISIBLE){
            sendmsg_bckgrnd.setVisibility(View.GONE);
            hideKeyboard();
        } else {
            launchListActivityAndFinish();
        }
    }
}
