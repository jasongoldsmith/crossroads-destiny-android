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

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import co.crossroadsapp.destiny.data.CommentData;
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
    private CurrentEventsCommentsViewAdapter mAdapterComment;
    private RelativeLayout commentLayout;
    private RecyclerView rv;

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_eventdetail);
        SwipeRefreshLayout mSwipeRefreshLayoutOther = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mSwipeRefreshLayoutOther.setVisibility(View.GONE);

        TextView clanTag = (TextView) view.findViewById(R.id.clan_tag_text);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        rv = (RecyclerView) view.findViewById(R.id.eventdetail_view);
        //rv = (RecyclerView) view.findViewById(R.id.event_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);

        if(page==0) {
            // fireteam fragment
            if(currentEvent!=null && currentEvent.getPlayerData()!=null) {
                    if(currentEvent.getClanName()!=null) {
                        clanTag.setVisibility(View.VISIBLE);
                        clanTag.setText(currentEvent.getClanName());
                        clanTag.setBackgroundColor(getResources().getColor(R.color.eventdetail_clan));
                    }
                mAdapter = new CurrentEventsViewAdapter(currentEvent.getPlayerData());
                rv.setAdapter(mAdapter);
            }
        } else if(page==1) {
            if(currentEvent!=null && currentEvent.getCommentDataList()!=null) {
                //comments fragment
                if(clanTag!=null) {
                    clanTag.setVisibility(View.GONE);
                }
                mAdapterComment = new CurrentEventsCommentsViewAdapter(currentEvent.getCommentDataList());
                rv.setAdapter(mAdapterComment);
                scrollToEnd();
            }
        }

        return view;
    }

    private void refreshItems() {
        if(currentEvent!=null && currentEvent.getEventId()!=null) {
            String id = currentEvent.getEventId();
            RequestParams param = new RequestParams();
            param.add("id", id);
            mManager.postEventById(((EventDetailActivity)getActivity()), param);
        }
    }

    public void updateCurrListAdapter(EventData currEvent, int page ) {
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
        if(currEvent!=null) {
            currentEvent = currEvent;
            if(page==0) {
                if (mAdapter != null) {
                    mAdapter.playerLocal.clear();
                    mAdapter.addItem(currentEvent.getPlayerData());
                    mAdapter.notifyDataSetChanged();
                }
            } else if(page==1) {
                if (mAdapterComment != null) {
                    mAdapterComment.commentsLocal.clear();
                    mAdapterComment.commentsLocal = currEvent.getCommentDataList();
                    mAdapterComment.notifyDataSetChanged();
                    scrollToEnd();
                }
            }
        }
    }

    private void scrollToEnd() {
        if(rv!=null) {
            if(page==1) {
                if (mAdapterComment != null && !mAdapterComment.commentsLocal.isEmpty()) {
                    rv.scrollToPosition(mAdapterComment.commentsLocal.size()-1);
                }
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
                holder.playerName.setTextColor(getResources().getColor(R.color.trimbe_white));
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
                        String name = playerLocal.get(position).getPsnId();
                        if (playerLocal.get(position).getClanTag()!=null && !playerLocal.get(position).getClanTag().isEmpty()) {
                            name = name + " [" + playerLocal.get(position).getClanTag() + "]";
                        }
                        holder.playerName.setText(name);
                        holder.playerName.setTextColor(getResources().getColor(R.color.activity_light_color));
                    }

                    holder.message.setVisibility(View.GONE);

                    if (checkUserIsCreator() && (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId()))) {
                        //holder.message.setVisibility(View.VISIBLE);
                    }
                    if ((checkUserIsPlayer() && !checkUserIsCreator()) && (playerLocal.get(position).getPlayerId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId()))) {
                        //holder.message.setVisibility(View.VISIBLE);
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

    private class CurrentEventsCommentsViewAdapter extends RecyclerView.Adapter<CurrentEventsCommentsViewAdapter.CurrentEventsCommentsViewHolder>{
        private ArrayList<CommentData> commentsLocal;

        public CurrentEventsCommentsViewAdapter(ArrayList<CommentData> commentDataList) {
            commentsLocal = new ArrayList<CommentData>();
            commentsLocal = commentDataList;
        }

        @Override
        public CurrentEventsCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_eventdetail_commentcard, null);
            return new CurrentEventsCommentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CurrentEventsCommentsViewHolder holder, int position) {
            if(commentsLocal!=null && !commentsLocal.isEmpty()) {
                if(commentsLocal.get(position)!=null) {
                    if(commentsLocal.get(position).getComment()!=null && !commentsLocal.get(position).getComment().isEmpty()) {
                        String text = commentsLocal.get(position).getComment();
                        holder.playerCommentText.setText(text);
                    }
                    if(commentsLocal.get(position).getPlayerImageUrl()!=null && !commentsLocal.get(position).getPlayerImageUrl().isEmpty()) {
                        String playerImg = commentsLocal.get(position).getPlayerImageUrl();
                        Util.picassoLoadImageWithoutMeasurement(getActivity(), holder.playerProfileComment, playerImg, R.drawable.img_profile_blank);
                    }
                    if(commentsLocal.get(position).getPsnId()!=null) {
                        String name=commentsLocal.get(position).getPsnId();
                        if(commentsLocal.get(position).getClanTag()!=null) {
                            name = name + " [" + commentsLocal.get(position).getClanTag() + "]";
                        }
                        holder.playerNameComment.setText(name);
                    }
                    if(commentsLocal.get(position).getCreated()!=null) {
                        String time = Util.updateLastReceivedDate(commentsLocal.get(position).getCreated(), getActivity().getResources());
                        if(time!=null) {
                            holder.time.setText(time);
                        }
                    }
                }
            }
            holder.playerProfileComment.invalidate();
        }

        @Override
        public int getItemCount() {
            return commentsLocal.size();
        }

        public class CurrentEventsCommentsViewHolder extends RecyclerView.ViewHolder {
            private CircularImageView playerProfileComment;
            private TextView playerNameComment;
            private TextView playerCommentText;
            private TextView time;
            public CurrentEventsCommentsViewHolder(View itemView) {
                super(itemView);
                view = itemView;

                playerProfileComment = (CircularImageView) itemView.findViewById(R.id.event_detail_comment_player_profile);
                playerNameComment = (TextView) itemView.findViewById(R.id.player_name_comment);
                playerCommentText = (TextView) itemView.findViewById(R.id.comment_text);
                time = (TextView) itemView.findViewById(R.id.comment_time);
            }
        }
    }
}