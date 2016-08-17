package co.crossroadsapp.destiny;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.PlayerData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.CircularImageView;
import co.crossroadsapp.destiny.utils.Util;

/**
 * Created by sharmha on 8/16/16.
 */
public class EventDetailsFragments extends Fragment {


    private View view;
    private int page;
    private ControlManager mManager;
    private UserData user;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventData currentEvent;
    private CurrentEventsViewAdapter mAdapter;

    // newInstance constructor for creating fragment with arguments
    public static EventDetailsFragments newInstance(int page, EventDetailActivity c, EventData data) {
        EventDetailsFragments fragmentFirst = new EventDetailsFragments();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        mManager = ControlManager.getmInstance();
        user = mManager.getUserData();
        currentEvent = ((EventDetailActivity)getActivity()).getCurrentEventData();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        TextView clanTag = (TextView) view.findViewById(R.id.clan_tag_text);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        RelativeLayout rvLayout = (RelativeLayout) view.findViewById(R.id.rv_layout);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.event_view);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mSwipeRefreshLayout.getLayoutParams();
//        params.setMargins(0, 300, 0, 0);
//        mSwipeRefreshLayout.setLayoutParams(params);
//        mSwipeRefreshLayout.requestLayout();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(false);

        if(page==0) {
            // fireteam fragment
            if(currentEvent!=null && currentEvent.getPlayerData()!=null) {
                if(clanTag!=null) {
                    //// TODO: 8/16/16 check clanTag in activity
                    if(user!=null && user.getClanTag()!=null) {
                        clanTag.setVisibility(View.VISIBLE);
                        clanTag.setText(user.getClanTag());
                    }
                }
                mAdapter = new CurrentEventsViewAdapter(currentEvent.getPlayerData());
                rv.setAdapter(mAdapter);
            }
        } else if(page==1) {
            //comments fragment
            clanTag.setVisibility(View.GONE);
        }

        return view;
    }

    private void refreshItems() {

    }

    public void updateCurrListAdapter(EventData currEvent) {
        if(currEvent!=null) {
            currentEvent = currEvent;
            if (mAdapter!=null) {
                mAdapter.playerLocal.clear();
                mAdapter.addItem(currentEvent.getPlayerData());
                mAdapter.notifyDataSetChanged();
            }
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
                        Util.picassoLoadIcon(((EventDetailActivity)getActivity()), holder.playerProfile, playerLocal.get(position).getPlayerImageUrl(),
                                R.dimen.eventdetail_player_profile_hgt, R.dimen.eventdetail_player_profile_width, R.drawable.img_profile_blank);
                    }
                    if (playerLocal.get(position).getPsnId() != null) {
                        holder.playerName.setText(playerLocal.get(position).getPsnId());
                    }

                    holder.message.setVisibility(View.GONE);

                    if (checkUserIsCreator() && (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId()))) {
                        holder.message.setVisibility(View.VISIBLE);
                    }
                    if ((checkUserIsPlayer() && !checkUserIsCreator()) && (playerLocal.get(position).getPlayerId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId()))) {
                        holder.message.setVisibility(View.VISIBLE);
                    }

                    final String finalCurrPlayerId = currPlayerId;
//                    holder.message.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (sendmsg_bckgrnd != null) {
//                                sendmsg_bckgrnd.setVisibility(View.VISIBLE);
//                                editMsgPlayer.setText(playerLocal.get(position).getPsnId());
//                                showKeyboard();
//                            }
//
//                            if (sendBtn != null) {
//                                sendBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        //v.setEnabled(false);
//                                        String s = getEditText();
//                                        if (s != null) {
//                                            sendMessage(finalCurrPlayerId, s);
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    });
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
        if(currentEvent!=null){
            if(currentEvent.getActivityData()!=null){
                if(currentEvent.getActivityData().getMaxPlayer()>0){
                    return currentEvent.getActivityData().getMaxPlayer();
                }
            }
        }
        return 0;
    }

    private boolean checkUserIsCreator() {
        if(this.currentEvent.getPlayerData()!=null) {
            if (user.getUserId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserIsPlayer(){
        if(this.currentEvent.getPlayerData()!=null) {
            for (int i = 0; i < currentEvent.getPlayerData().size(); i++) {
                if (user.getUserId().equalsIgnoreCase(currentEvent.getPlayerData().get(i).getPlayerId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
