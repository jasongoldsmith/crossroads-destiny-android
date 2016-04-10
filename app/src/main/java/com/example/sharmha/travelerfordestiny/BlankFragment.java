package com.example.sharmha.travelerfordestiny;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharmha.travelerfordestiny.data.CurrentEventDataHolder;
import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.CircularImageView;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class BlankFragment extends Fragment {

    private View view;
    private UserData user;
    private ControlManager mManager;
    private ListActivityFragment mContext;
    private ArrayList<EventData> currentEventList;
    private ArrayList<EventData> fragmentCurrentEventList;
    private ArrayList<EventData> fragmentupcomingEventList;
    MyAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public BlankFragment() {
    }

    public BlankFragment(ListActivityFragment act, ArrayList<EventData> eData) {
        // Required empty public constructor
        mContext = act;
        currentEventList = new ArrayList<EventData>();
        currentEventList = eData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = ControlManager.getmInstance();
        user = mManager.getUserData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.event_view);
        rv.setHasFixedSize(true);
        adapter = new MyAdapter(currentEventList);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    void refreshItems() {
        //Util.updateCurrEventOnTime(currentEventList.get(0).getLaunchDate());
        // Load items
        // ...
        mManager.getEventList(mContext);

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);

        // Load complete
//        onItemsLoadComplete();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<EventData> elistLocal;

        public MyAdapter(ArrayList<EventData> currentEventList) {
            elistLocal = new ArrayList<EventData>();
            if(currentEventList!=null) {
                elistLocal = currentEventList;
            }
        }

        protected void addItem(ArrayList<EventData> a) {
            this.elistLocal.addAll(a);
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            private CardView event_card;
            protected TextView eventSubType;
            protected TextView eventPlayerNames;
            protected TextView eventaLight;
            protected TextView eventPlayerNameCnt;
            protected ImageView eventIcon;
            protected ImageView playerProfileImage1;
            protected ImageView playerProfileImage2;
            protected ImageView playerProfileImage3;
            protected TextView playerCountImage3;
            protected ImageView joinBtn;
            protected ImageView unjoinBtn;

            public MyViewHolder(View v) {
                super(v);
                view = v;
                event_card = (CardView) v.findViewById(R.id.event);
                eventSubType = (TextView) v.findViewById(R.id.activity_name);
                eventPlayerNames = (TextView) v.findViewById(R.id.activity_player_name);
                eventaLight = (TextView) v.findViewById(R.id.activity_aLight);
                eventIcon = (ImageView) v.findViewById(R.id.event_icon);
                playerProfileImage1 = (ImageView) v.findViewById(R.id.player_picture_1);
                playerProfileImage2 = (ImageView) v.findViewById(R.id.player_picture_2);
                playerProfileImage3 = (ImageView) v.findViewById(R.id.player_picture_3);
                playerCountImage3 = (TextView) v.findViewById(R.id.player_picture_text_3);
                joinBtn = (ImageView) v.findViewById(R.id.join_btn);
                unjoinBtn = (ImageView) v.findViewById(R.id.unjoin_btn);
                eventPlayerNameCnt = (TextView) v.findViewById(R.id.activity_player_name_lf);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_event, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            if (this.elistLocal.size() > 0) {
                boolean CreatorIn = false;
                boolean CreatorIsPlayer = false;
                String allNames = "";
                String allNamesRem = "";
                final EventData currEvent = elistLocal.get(position);
                final String eId = this.elistLocal.get(position).getEventId();
                String s = this.elistLocal.get(position).getActivityData().getActivitySubtype();
                int l = this.elistLocal.get(position).getActivityData().getActivityLight();
                String url = this.elistLocal.get(position).getActivityData().getActivityIconUrl();
                int reqPlayer = this.elistLocal.get(position).getActivityData().getMaxPlayer() - this.elistLocal.get(position).getPlayerData().size();
                // get players
                int i = this.elistLocal.get(position).getPlayerData().size();
                String creatorId = this.elistLocal.get(position).getCreatorData().getPlayerId();
                final String status = this.elistLocal.get(position).getEventStatus();

                if (creatorId.equalsIgnoreCase(user.getUserId())) {
                    CreatorIn = true;
                    CreatorIsPlayer = true;
                }

                for (int y = 0; y < i; y++) {
                    String n = this.elistLocal.get(position).getPlayerData().get(y).getPsnId();
                    String profileUrl = this.elistLocal.get(position).getPlayerData().get(y).getPlayerImageUrl();
                    String pId = this.elistLocal.get(position).getPlayerData().get(y).getPlayerId();
                    if (user.getUserId().equalsIgnoreCase(pId)) {
                        CreatorIn = true;
                    }

//                    if (i>1 && y<i-1) {
//                        allNames = allNames + n + ", ";
//                    } else {
//                        allNames = allNames + n;
//                   }

                    if (y < 4)
                        uploadPlayerImg(holder, profileUrl, y, i);
                }

                allNames = "Created by " + this.elistLocal.get(position).getCreatorData().getPsnId();
                if (!status.equalsIgnoreCase("full")) {
                    allNamesRem = ", " + "LF" + reqPlayer + "M";
                }

                if (status != null && !status.equalsIgnoreCase("")) {
                    if (!status.equalsIgnoreCase(Constants.STATUS_FULL) && !CreatorIn) {
                        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams rp = new RequestParams();
                                rp.put("eId", eId);
                                rp.put("player", user.getUserId());
                                mManager.postJoinEvent(mContext, rp);
                                holder.joinBtn.setClickable(false);
                            }
                        });
                    } else if (CreatorIn) {
                        holder.unjoinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams rp = new RequestParams();
                                rp.put("eId", eId);
                                rp.put("player", user.getUserId());
                                mManager.postUnJoinEvent(mContext, rp);
                                holder.unjoinBtn.setClickable(false);
                            }
                        });
                    }
                }
                final boolean showJoin;
                if (!status.equalsIgnoreCase(Constants.STATUS_FULL) && !CreatorIn) {
                    showJoin = true;
                } else {
                    showJoin = false;
                }

                if (s != "" || s != null) {
                    holder.eventSubType.setText(s);
                }

                holder.event_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currEvent != null) {
                            CurrentEventDataHolder ins = CurrentEventDataHolder.getInstance();
                            ins.setData(currEvent);
                            if (showJoin) {
                                ins.setJoinVisible(true);
                            } else {
                                ins.setJoinVisible(false);
                            }
                            //setCurrEventData(currEvent);
                        }
                        //start new activity for event
                        Intent regIntent = new Intent(mContext,
                                EventDetailActivity.class);
                        regIntent.putExtra("userdata", user);
                        startActivity(regIntent);
                    }
                });

                if (l > 0) {
                    holder.eventaLight.setText("+" + String.valueOf(l));
                }

                updateJoinButton(holder, status, CreatorIn, CreatorIsPlayer);

                holder.eventPlayerNames.setText(allNames);
                holder.eventPlayerNameCnt.setText(allNamesRem);

                Util.picassoLoadIcon(mContext, holder.eventIcon, url, R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.img_i_c_o_n_r_a_i_d);
            }
        }

        private void updateJoinButton(MyViewHolder holder, String status, boolean creatorIn, boolean creatorIsPlayer) {
            switch (status) {
                case "new":
                    if (creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    } else if (creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_g_o_i_n_g);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_j_o_i_n);
                    }
                    break;
                case "can_join":
                    if (creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    } else if (creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_g_o_i_n_g);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_j_o_i_n);
                    }
                    break;
                case "full":
                    if (creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    } else if (creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_r_e_a_d_y);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_f_u_l_l);
                    }
                    break;
                case "open":
                    if (creatorIsPlayer) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_o_w_n_e_r);
                    } else if (creatorIn) {
                        holder.unjoinBtn.setVisibility(View.VISIBLE);
                        holder.joinBtn.setImageResource(R.drawable.btn_g_o_i_n_g);
                    } else {
                        holder.unjoinBtn.setVisibility(View.GONE);
                        holder.joinBtn.setImageResource(R.drawable.btn_j_o_i_n);
                    }
                    break;
            }
        }

        private void uploadPlayerImg(MyViewHolder holder, String profileUrl, int player, int playersCount) {
            switch (player) {
                case 0:
                    holder.playerProfileImage3.setVisibility(View.GONE);
                    holder.playerProfileImage2.setVisibility(View.GONE);
                    holder.playerCountImage3.setVisibility(View.GONE);
                    Util.picassoLoadIcon(mContext, holder.playerProfileImage1, profileUrl, R.dimen.player_icon_hgt, R.dimen.player_icon_width, R.drawable.avatar);
                    break;
                case 1:
                    holder.playerProfileImage2.setVisibility(View.VISIBLE);
                    holder.playerCountImage3.setVisibility(View.GONE);
                    Util.picassoLoadIcon(mContext, holder.playerProfileImage2, profileUrl, R.dimen.player_icon_hgt, R.dimen.player_icon_width, R.drawable.avatar);
                    break;
                default:
                    holder.playerProfileImage3.setVisibility(View.VISIBLE);
                    if (player == 2 && playersCount < 4) {
                        holder.playerCountImage3.setVisibility(View.GONE);
                        Util.picassoLoadIcon(mContext, holder.playerProfileImage3, profileUrl, R.dimen.player_icon_hgt, R.dimen.player_icon_width, R.drawable.avatar);
                    } else {
                        holder.playerProfileImage3.setImageResource(R.drawable.img_avatar_empty);
                        holder.playerCountImage3.setVisibility(View.VISIBLE);
                        int p = playersCount - 2;
                        holder.playerCountImage3.setText("+" + p);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if(elistLocal!=null) {
                return this.elistLocal.size();
            }
            return 0;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    public void updateCurrListAdapter(ArrayList<EventData> eData){
        currentEventList = eData;
        if (adapter!=null){
            adapter.elistLocal.clear();
            adapter.addItem(eData);
            adapter.notifyDataSetChanged();
        }
    }

}