package co.crossroadsapp.destiny;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.crossroadsapp.destiny.data.ActivityData;
import co.crossroadsapp.destiny.data.CurrentEventDataHolder;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

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
    private ArrayList<ActivityData> currentAdList;

    public BlankFragment() {
    }

    @SuppressLint("ValidFragment")
    public BlankFragment(ListActivityFragment act, ArrayList<EventData> eData, ArrayList<ActivityData> adData) {
        // Required empty public constructor
        mContext = act;
        currentEventList = new ArrayList<EventData>();
        currentAdList = new ArrayList<ActivityData>();
        currentEventList = eData;
        if(adData!=null) {
            currentAdList = adData;
        } else {
            //adData.clear();
        }
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

        adapter = new MyAdapter(currentEventList, currentAdList);
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

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<EventData> elistLocal;
        private ArrayList<ActivityData> adList;

        public MyAdapter(ArrayList<EventData> currentEventList, ArrayList<ActivityData> currentAdList) {
            elistLocal = new ArrayList<EventData>();
            adList = new ArrayList<ActivityData>();
            if(currentEventList!=null) {
                elistLocal = currentEventList;
            }
            if(BlankFragment.this.currentAdList !=null) {
                adList = currentAdList;
            }
        }

        protected void addItem(ArrayList<EventData> a, ArrayList<ActivityData> ad) {
            this.elistLocal.addAll(a);
            this.adList.addAll(ad);
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout event_card_mainLayout;
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
            protected TextView eventDate;
            protected TextView checkpointText;

            public MyViewHolder(View v) {
                super(v);
                view = v;
                event_card_mainLayout = (RelativeLayout) v.findViewById(R.id.fragment_event_mainlayout);
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
                eventDate = (TextView) v.findViewById(R.id.event_time);
                checkpointText = (TextView) v.findViewById(R.id.checkoint_text);
            }
        }

        //Viewholder for ad cards
        public class MyViewHolder2 extends RecyclerView.ViewHolder {
            private RelativeLayout event_adcard_mainLayout;
            private ImageView adCardImg;
            private CardView event_adcard;
            private CardView addBtn;
            protected TextView eventadHeader;
            protected TextView eventAdSubheader;
            protected ImageView eventAdIcon;

            public MyViewHolder2(View v) {
                super(v);
                view = v;
                adCardImg = (ImageView) v.findViewById(R.id.adcard_img);
                event_adcard_mainLayout = (RelativeLayout) v.findViewById(R.id.fragment_adevent_mainlayout);
                event_adcard = (CardView) v.findViewById(R.id.ad_event);
                eventadHeader = (TextView) v.findViewById(R.id.ad_header);
                eventAdSubheader = (TextView) v.findViewById(R.id.ad_subheader);
                eventAdIcon = (ImageView) v.findViewById(R.id.adevent_icon);
                addBtn = (CardView) v.findViewById(R.id.adcard_addbtn);
            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            if(position==0 && elistLocal.size()==0) {
                return 2;
            }else if(position<elistLocal.size()) {
                return 0;
            } else {
                return 2;
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            RecyclerView.ViewHolder vh = null;

            switch (viewType) {
                case 0:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fragment_event, null);
                    return new MyViewHolder(view);
                case 2:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fragment_adevent, null);
                    return new MyViewHolder2(view);
            }
            //return new MyViewHolder(view);
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            switch (viewHolder.getItemViewType()) {

                case 0:
                    final MyViewHolder holder = (MyViewHolder) viewHolder;
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
                        int level = this.elistLocal.get(position).getActivityData().getActivityLevel();
                        String checkpoint = this.elistLocal.get(position).getActivityData().getActivityCheckpoint();
                        String creatorId = this.elistLocal.get(position).getCreatorData().getPlayerId();
                        final String status = this.elistLocal.get(position).getEventStatus();

                        holder.checkpointText.setVisibility(View.GONE);
                        holder.eventDate.setVisibility(View.GONE);
                        setCardViewLayoutParams(holder.event_card_mainLayout, 137);

                        if (creatorId != null) {
                            if (user != null && user.getUserId() != null) {
                                if (creatorId.equalsIgnoreCase(user.getUserId())) {
                                    CreatorIn = true;
                                    CreatorIsPlayer = true;
                                }
                            }
                        }
                        if (elistLocal.get(position).getLaunchEventStatus().equalsIgnoreCase(Constants.LAUNCH_STATUS_UPCOMING) || (checkpoint != null)) {
                            if (elistLocal.get(position).getLaunchEventStatus().equalsIgnoreCase(Constants.LAUNCH_STATUS_UPCOMING)) {
                                String date = Util.convertUTCtoReadable(elistLocal.get(position).getLaunchDate());
                                if (date != null) {
                                    holder.eventDate.setVisibility(View.VISIBLE);
                                    holder.eventDate.setText(date);
                                }
                                if (checkpoint != null && checkpoint.length() > 0 && (!checkpoint.equalsIgnoreCase("null"))) {
                                    holder.checkpointText.setVisibility(View.VISIBLE);
                                    holder.checkpointText.setText(checkpoint);
                                    setCardViewLayoutParams(holder.event_card_mainLayout, 177);
                                } else {
                                    holder.checkpointText.setVisibility(View.GONE);
                                    setCardViewLayoutParams(holder.event_card_mainLayout, 155);
                                }
                            } else if (checkpoint != null && checkpoint.length() > 0 && (!checkpoint.equalsIgnoreCase("null"))) {
                                holder.checkpointText.setVisibility(View.VISIBLE);
                                holder.checkpointText.setText(checkpoint);
                                setCardViewLayoutParams(holder.event_card_mainLayout, 155);
                            }
                        }

                        for (int y = 0; y < i; y++) {
                            String n = this.elistLocal.get(position).getPlayerData().get(y).getPsnId();
                            String profileUrl = this.elistLocal.get(position).getPlayerData().get(y).getPlayerImageUrl();
                            String pId = this.elistLocal.get(position).getPlayerData().get(y).getPlayerId();
                            if (user != null && user.getUserId() != null) {
                                if (user.getUserId().equalsIgnoreCase(pId)) {
                                    CreatorIn = true;
                                }
                            }

//                    if (i>1 && y<i-1) {
//                        allNames = allNames + n + ", ";
//                    } else {
//                        allNames = allNames + n;
//                   }

                            if (y < 4)
                                uploadPlayerImg(holder, profileUrl, y, i);
                        }

                        allNames = this.elistLocal.get(position).getCreatorData().getPsnId();
                        if (!status.equalsIgnoreCase("full")) {
                            allNamesRem = " " + "LF" + reqPlayer + "M";
                        }

                        if (status != null && !status.equalsIgnoreCase("")) {
                            if (!status.equalsIgnoreCase(Constants.STATUS_FULL) && !CreatorIn) {
                                holder.joinBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RequestParams rp = new RequestParams();
                                        rp.put("eId", eId);
                                        rp.put("player", user.getUserId());
                                        mContext.hideProgress();
                                        mContext.showProgress();
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
                                        mContext.hideProgress();
                                        mContext.showProgress();
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
                                if (mContext != null) {
                                    //start new activity for event
                                    Intent regIntent = new Intent(mContext,
                                            EventDetailActivity.class);
                                    if (regIntent != null) {
                                        regIntent.putExtra("userdata", user);
                                        startActivity(regIntent);
                                        mContext.finish();
                                    }
                                }
                            }
                        });

                        holder.eventaLight.setText("");
                        if (l > 0) {
                            // unicode to show star
                            String st = "\u2726";
                            holder.eventaLight.setText(st + String.valueOf(l));
                        } else if (level > 0) {
                            holder.eventaLight.setText("lvl " + String.valueOf(level));
                        }

                        updateJoinButton(holder, status, CreatorIn, CreatorIsPlayer);

                        holder.eventPlayerNames.setText(allNames);
                        holder.eventPlayerNameCnt.setText(allNamesRem);

                        holder.eventaLight.invalidate();
                        holder.event_card_mainLayout.invalidate();
                        holder.checkpointText.invalidate();
                        holder.eventDate.invalidate();

                        Util.picassoLoadIcon(mContext, holder.eventIcon, url, R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.icon_ghost_default);
                    }
                    break;
                case 2:
                    MyViewHolder2 adHolder = (MyViewHolder2) viewHolder;
                    adHolder.eventadHeader.setText(adList.get(position-elistLocal.size()).getAdCardData().getAdCardHeader());
                    adHolder.eventAdSubheader.setText(adList.get(position-elistLocal.size()).getAdCardData().getAdCardSubHeader());
                    String cardBackgroundImageUrl = adList.get(position-elistLocal.size()).getAdCardData().getAdCardBaseUrl() + adList.get(position-elistLocal.size()).getAdCardData().getAdCardImagePath();
                    String iconImageUrl = adList.get(position-elistLocal.size()).getActivityIconUrl();
                    Util.picassoLoadIcon(mContext, adHolder.eventAdIcon, iconImageUrl, R.dimen.activity_icon_hgt, R.dimen.activity_icon_width, R.drawable.icon_ghost_default);
                    //Util.picassoLoadIcon(mContext, adHolder.adCardImg, cardBackgroundImageUrl, R.dimen.ad_hgt, R.dimen.ad_width, R.drawable.img_adcard_raid_golgoroth);

                    Picasso.with(mContext)
                            .load(cardBackgroundImageUrl)
                            .placeholder(R.drawable.img_adcard_raid_golgoroth)
                            .fit().centerCrop()
                            .into(adHolder.adCardImg);

                    adHolder.addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.showProgressBar();
                            mContext.setAdCardPosition(adList.get(position-elistLocal.size()).getId());
                            RequestParams rp = new RequestParams();
                            rp.add("aType", adList.get(position-elistLocal.size()).getActivityType());
                            rp.add("includeTags", "true");
                            mManager.postGetActivityList(mContext, rp);
                            //mManager.postCreateEvent(adList.get(position-elistLocal.size()).getId(), user.getUserId(), adList.get(position-elistLocal.size()).getMinPlayer(), adList.get(position-elistLocal.size()).getMaxPlayer(), null, mContext);
                        }
                    });
                    break;
            }
        }

        private void setCardViewLayoutParams(RelativeLayout event_card_mainLayout, int i) {
            int pix = Util.dpToPx(i, mContext);
            if(pix>0) {
                event_card_mainLayout.getLayoutParams().height = pix;
                event_card_mainLayout.requestLayout();
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
            if(elistLocal!=null && adList!=null) {
                return this.elistLocal.size()+this.adList.size();
            }
            return 0;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    public boolean checkCurrentList() {
        if (currentEventList.size()==0){
            return true;
        }
        return false;
    }

    public void updateCurrListAdapter(ArrayList<EventData> eData, ArrayList<ActivityData> adActivityData){
        currentEventList = eData;
        currentAdList = new ArrayList<ActivityData>();
        if (adActivityData!=null) {
            currentAdList = adActivityData;
        }
        if (adapter!=null){
            adapter.elistLocal.clear();
            adapter.adList.clear();
            adapter.addItem(currentEventList, currentAdList);
            adapter.notifyDataSetChanged();
        }
    }

}