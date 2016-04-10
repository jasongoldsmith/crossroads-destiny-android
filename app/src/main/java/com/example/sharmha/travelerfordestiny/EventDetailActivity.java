package com.example.sharmha.travelerfordestiny;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.example.sharmha.travelerfordestiny.utils.CircularImageView;
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
    private RelativeLayout jointeam;
    private ControlManager controlManager;
    private CurrentEventDataHolder inst;
    private RelativeLayout sendMessageLayout;
    private RelativeLayout sendmsg_bckgrnd;

    private EditText editText;
    private ImageView sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_list);

        Bundle b = getIntent().getExtras();
        user = b.getParcelable("userdata");

        //setTRansparentStatusBar();

        controlManager = ControlManager.getmInstance();

        userProfile = (CircularImageView) findViewById(R.id.userProfile_event);

        currEvent = new EventData();

        inst = CurrentEventDataHolder.getInstance();
        currEvent = inst.getData();

        eventProfileImg = (ImageView) findViewById(R.id.event_detail_icon);
        eventName = (TextView) findViewById(R.id.activity_name_detail);
        eventSubName = (TextView) findViewById(R.id.activity_player_name_detail);
        eventSubNameLF = (TextView) findViewById(R.id.activity_player_name_lf_detail);
        eventLight = (TextView) findViewById(R.id.activity_aLight_detail);
        back = (ImageView) findViewById(R.id.eventdetail_backbtn);

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

        jointeam = (RelativeLayout) findViewById(R.id.eventdetail_jointeam_layout);

        if(inst.getJoinVisible()){
            jointeam.setVisibility(View.VISIBLE);
        }

        jointeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams rp = new RequestParams();
                rp.put("eId", currEvent.getEventId());
                rp.put("player", user.getUserId());
                controlManager.postJoinEvent(EventDetailActivity.this, rp);
            }
        });

        setPlayerNames();

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
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.event_player);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CurrentEventsViewAdapter(currEvent.getPlayerData());

        mRecyclerView.setAdapter(mAdapter);

    }

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
            currEvent = new EventData();
            this.currEvent = (EventData) data;
            if (currEvent != null) {
                if (currEvent.getPlayerData() != null) {
                    if (mAdapter != null) {
                        mAdapter.playerLocal.clear();
                        mAdapter.addItem(currEvent.getPlayerData());
                        mAdapter.notifyDataSetChanged();
                    }
                    setPlayerNames();
                    jointeam.setVisibility(View.GONE);
                }
            }
        } else if(observable instanceof EventSendMessageNetwork) {
            editText.setText("");
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            sendmsg_bckgrnd.setVisibility(View.GONE);
        }
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

                if(inst.getJoinVisible() || playerLocal.get(position).getPlayerId().equalsIgnoreCase(user.getUserId()) || (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))) {
                    holder.message.setVisibility(View.GONE);
                }
// Todo fix this logic
                if(currEvent.getCreatorData().getPlayerId().equalsIgnoreCase(user.getUserId())&&(!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currEvent.getCreatorData().getPlayerId()))){
                    holder.message.setVisibility(View.VISIBLE);
                }

                final String finalCurrPlayerId = currPlayerId;
                holder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sendmsg_bckgrnd != null) {
                            sendmsg_bckgrnd.setVisibility(View.VISIBLE);
                        }

                        if (sendBtn != null) {
                            sendBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendMessage(v, finalCurrPlayerId);
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

    private void sendMessage(View v, String currentPlayerId) {
        if(editText!=null){
            if(editText.getText()!=null){
                if(editText.getText().toString().length()>0){
                    if(currEvent!=null &&  currEvent.getEventId()!=null) {
                        v.setEnabled(false);
                        editText.setText("");
                        controlManager.postEventMessage(EventDetailActivity.this, editText.getText().toString(), currentPlayerId, currEvent.getEventId());
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(sendmsg_bckgrnd.getVisibility()==View.VISIBLE){
            sendmsg_bckgrnd.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
