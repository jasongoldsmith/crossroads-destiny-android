package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharmha.travelerfordestiny.data.CurrentEventDataHolder;
import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.PlayerData;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.network.EventRelationshipHandlerNetwork;
import com.example.sharmha.travelerfordestiny.network.EventSendMessageNetwork;
import com.example.sharmha.travelerfordestiny.network.NetworkEngine;
import com.example.sharmha.travelerfordestiny.utils.CircularImageView;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sharmha on 3/29/16.
 */
public class EventDetailActivity extends Activity implements Observer {

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

    private TextView editMsgPlayer;

//    private Button leaveEvent;
//    private Button msgAll;
    private Handler _handler;
    private boolean joinBtnActive;
    private TextView eventDetailDate;
    private boolean userIsPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_list);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        joinBtn = (TextView) findViewById(R.id.join_btn);
        leaveBtn = (TextView) findViewById(R.id.leave_btn);
        msgallBtn = (TextView) findViewById(R.id.messageall_btn);

        //setTRansparentStatusBar();

        controlManager = ControlManager.getmInstance();

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

//        leaveEvent = (Button) findViewById(R.id.leave_event);
//        msgAll = (Button) findViewById(R.id.msg_all);

        sendmsg_bckgrnd = (RelativeLayout) findViewById(R.id.sendmsg_background);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText = (EditText) findViewById(R.id.edittext);
        sendBtn = (ImageView) findViewById(R.id.send_btn);

//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
//                    // the user is done typing.
//                    sendMessage();
//                }
//                return false;
//            }
//        });

        if(currEvent.getActivityData().getActivityIconUrl()!=null) {
            Util.picassoLoadIcon(EventDetailActivity.this, eventProfileImg, currEvent.getActivityData().getActivityIconUrl(), R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.img_i_c_o_n_r_a_i_d);
        }

        if (currEvent.getActivityData().getActivitySubtype()!=null) {
            eventName.setText(currEvent.getActivityData().getActivitySubtype());
        }

        if (currEvent.getActivityData().getActivityLight()> 0) {
            eventLight.setText("+" + currEvent.getActivityData().getActivityLight());
        }

        if (user.getImageUrl()!=null){
            Util.picassoLoadIcon(EventDetailActivity.this, userProfile, user.getImageUrl(), R.dimen.player_profile_hgt, R.dimen.player_profile_width, R.drawable.img_avatar_you);
        }

        //jointeam = (RelativeLayout) findViewById(R.id.eventdetail_jointeam_layout);
//        if(inst.getJoinVisible()){
//            jointeam.setVisibility(View.VISIBLE);
//        }else {
//            leaveEvent.setVisibility(View.VISIBLE);
//        }

        checkUserIsPlayer();
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
                controlManager.postJoinEvent(EventDetailActivity.this, rp);
            }
        });

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
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

//        String allNames = "";
//        String allNamesRem = "";
//        int reqPlayer = currEvent.getActivityData().getMaxPlayer() - currEvent.getPlayerData().size();
//
//        allNames = "Created by " + currEvent.getCreatorData().getPsnId();
//        if (!currEvent.getEventStatus().equalsIgnoreCase("full")) {
//            allNamesRem = ", " + "LF" +reqPlayer+"M";
//        }
//
//        eventSubName.setText(allNames);
//        eventSubNameLF.setText(allNamesRem);
//        int i = currEvent.getPlayerData().size();

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

    }

    private void setBottomButtonSelection() {
        if(checkUserIsCreator()){
            leaveBtn.setVisibility(View.GONE);
            joinBtn.setVisibility(View.GONE);
            msgallBtn.setVisibility(View.VISIBLE);
        } else if (checkUserIsPlayer()) {
            leaveBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.GONE);
            msgallBtn.setVisibility(View.GONE);
        } else if(!currEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL)){
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
                String msg=null;
                @Override
                public void onClick(View v) {
                    _handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < currEvent.getPlayerData().size(); i++) {
                                if (i==0){
                                    msg = getEditText();
                                }
                                if (!user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())) {
                                    //v.setEnabled(false);
                                    if(msg!=null) {
                                        sendMessage(currEvent.getPlayerData().get(i).getPlayerId(), msg);
                                    }
                                }
                            }
                        }
                    }, 500);
                    //sendMessage(v, playerId);
                }
            });
        }
    }

//    private void setSendMsgToAllVisibility() {
//        if(currEvent!=null) {
//            if(currEvent.getCreatorData()!=null && currEvent.getCreatorData().getPlayerId()!=null) {
//                if(user!=null && user.getUserId()!=null){
//                    if(currEvent.getCreatorData().getPlayerId().equalsIgnoreCase(user.getUserId())) {
//                        msgAll.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        }
//    }

    private void setPlayerNames() {
        String allNames = "";
        String allNamesRem = "";
        int reqPlayer = currEvent.getActivityData().getMaxPlayer() - currEvent.getPlayerData().size();

        allNames = "Created by " + currEvent.getCreatorData().getPsnId();
        if (!currEvent.getEventStatus().equalsIgnoreCase("full")) {
            allNamesRem = ", " + "LF" +reqPlayer+"M";
        }

        eventSubName.setText(allNames);
        eventSubNameLF.setText(allNamesRem);
    }

    private void setTRansparentStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof EventRelationshipHandlerNetwork) {
            //currEvent = new EventData();
            this.currEvent = (EventData) data;
            if (currEvent != null) {
                if (currEvent.getPlayerData() != null) {
                    if (mAdapter != null) {
                        mAdapter.playerLocal.clear();
                        mAdapter.addItem(currEvent.getPlayerData());
                        mAdapter.notifyDataSetChanged();
                    }
                    setPlayerNames();
                    setBottomButtonSelection();
                    //checkJoinLeaveBtn();
                    //jointeam.setVisibility(View.GONE);
                }
                //setSendMsgToAllVisibility();
            }
        } else if(observable instanceof EventSendMessageNetwork) {
            editText.setText("");
            hideSendMsgBckground();
        }
    }

    private void hideSendMsgBckground() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        sendmsg_bckgrnd.setVisibility(View.GONE);
        hideKeyboard();
    }

//    private void checkLeaveBtn() {
//        boolean userPresent=false;
//        if(this.currEvent.getPlayerData()!=null){
//            for (int i=0; i<currEvent.getPlayerData().size(); i++){
//                if(user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())){
//                    leaveEvent.setVisibility(View.VISIBLE);
////                    jointeam.setVisibility(View.GONE);
//                    //joinBtnActive = false;
//                    userPresent = true;
//                    break;
//                }
//            }
//            if(!userPresent) {
////                jointeam.setVisibility(View.VISIBLE);
//                leaveEvent.setVisibility(View.GONE);
//                //joinBtnActive = true;
//            }
//        }
//    }

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

//    private void checkJoinLeaveBtn() {
//        boolean userPresent=false;
//        if(this.currEvent.getPlayerData()!=null){
//            for (int i=0; i<currEvent.getPlayerData().size(); i++){
//                if(user.getUserId().equalsIgnoreCase(currEvent.getPlayerData().get(i).getPlayerId())){
//                    leaveEvent.setVisibility(View.VISIBLE);
//                    jointeam.setVisibility(View.GONE);
//                    //joinBtnActive = false;
//                    userPresent = true;
//                    break;
//                }
//            }
//            if(!userPresent) {
//                jointeam.setVisibility(View.VISIBLE);
//                leaveEvent.setVisibility(View.GONE);
//                //joinBtnActive = true;
//            }
//        }
//    }

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
            if (playerLocal.get(position)!=null){
                if(playerLocal.get(position).getPlayerId()!=null){
                   currPlayerId = playerLocal.get(position).getPlayerId();
                }
                if (playerLocal.get(position).getPlayerImageUrl()!=null){
                    Util.picassoLoadIcon(EventDetailActivity.this, holder.playerProfile, playerLocal.get(position).getPlayerImageUrl(),
                            R.dimen.eventdetail_player_profile_hgt, R.dimen.eventdetail_player_profile_width, R.drawable.avatar36);
                }
                if (playerLocal.get(position).getPsnId() != null) {
                    holder.playerName.setText(playerLocal.get(position).getPsnId());
                }

//                if(inst.getJoinVisible() || playerLocal.get(position).getPlayerId().equalsIgnoreCase(user.getUserId()) || (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))) {
//                    holder.message.setVisibility(View.GONE);
//                }
// Todo fix this logic
//                if(currEvent.getCreatorData().getPlayerId().equalsIgnoreCase(user.getUserId())&&(!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))){
//                    holder.message.setVisibility(View.VISIBLE);
//                }
//
//                if ((!userIsPlayer) && (currEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL))){
//                    holder.message.setVisibility(View.GONE);
//                }
                holder.message.setVisibility(View.GONE);

                if(checkUserIsCreator() && (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))){
                    holder.message.setVisibility(View.VISIBLE);
                } if ((checkUserIsPlayer() && !checkUserIsCreator()) && (playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))){
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
                                    if(s!=null) {
                                        sendMessage(finalCurrPlayerId, s);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return playerLocal.size();
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

    }

    private void showKeyboard() {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard() {
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
//                            }
//                            //hideSendMsgBckground();
//                        }, 1000);
                    }
//                }
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        if(sendmsg_bckgrnd.getVisibility()==View.VISIBLE){
            sendmsg_bckgrnd.setVisibility(View.GONE);
            hideKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
