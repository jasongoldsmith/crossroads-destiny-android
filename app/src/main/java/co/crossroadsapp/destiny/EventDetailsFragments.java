package co.crossroadsapp.destiny;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import co.crossroadsapp.destiny.data.CommentData;
import co.crossroadsapp.destiny.data.EventData;
import co.crossroadsapp.destiny.data.PlayerData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.CircularImageView;
import co.crossroadsapp.destiny.utils.Constants;
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
    private RelativeLayout fireteamBanner;
    private Animation anim;
    private TextView clanTag;
    private TextView fireteamBannerMsg;

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
        if(getActivity()!=null) {
            final View contentView = ((EventDetailActivity) getActivity()).findViewById(android.R.id.content);
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    contentView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = contentView.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        ((EventDetailActivity) getActivity()).setInviteBtnMargin(keypadHeight-Util.softNavigationPresent(getActivity()!=null?getActivity():null));
                        if(currentEvent!=null && currentEvent.getEventStatus()!=null && currentEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL) && checkUserIsPlayer()) {
                            if(fireteamBanner!=null && fireteamBanner.getVisibility()==View.VISIBLE) {
                                fireteamBanner.setVisibility(View.VISIBLE);
                                anim = AnimationUtils.loadAnimation(getActivity(),
                                        R.anim.slide_out_to_top);
                                fireteamBanner.startAnimation(anim);
                                fireteamBanner.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        // keyboard is closed
                        ((EventDetailActivity) getActivity()).setInviteBtnMarginZero();
                        if(currentEvent.getEventStatus()!=null && currentEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL) && checkUserIsPlayer()) {
                            if(fireteamBanner!=null && fireteamBanner.getVisibility()!=View.VISIBLE) {
                                fireteamBanner.setVisibility(View.VISIBLE);
                                anim = AnimationUtils.loadAnimation(getActivity(),
                                        R.anim.slide_in_from_top);
                                fireteamBanner.startAnimation(anim);
                            }
                        }
                    }
                }
            });
        }
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

        fireteamBanner = (RelativeLayout) view.findViewById(R.id.fireteam_banner);
        fireteamBannerMsg = (TextView) view.findViewById(R.id.banner_msg);

        clanTag = (TextView) view.findViewById(R.id.clan_tag_text);

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

        setBanners();
        return view;
    }

    private void setBanners() {
        if(currentEvent.getEventStatus()!=null && currentEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL)) {
            if (clanTag != null) {
                clanTag.setVisibility(View.GONE);
            }
            if (checkUserIsPlayer()) {
                fireteamBannerMsg.setText("Send " + currentEvent.getCreatorData().getPsnId() + " " +getString(R.string.banner_member_msg));
                fireteamBanner.setVisibility(View.VISIBLE);
            }
            if (checkUserIsCreator()) {
                fireteamBannerMsg.setText(getString(R.string.banner_leader_msg));
            }
        } else {
            fireteamBanner.setVisibility(View.GONE);
        }
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
        private ImageView leader_tag;
        private ImageView inviteIcon;


        public CurrentEventsViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            playerProfile = (CircularImageView) itemView.findViewById(R.id.event_detail_player_profile);
            playerName = (TextView) itemView.findViewById(R.id.player_name);
            playerCard = (CardView) itemView.findViewById(R.id.activity_card);
            message = (ImageView) itemView.findViewById(R.id.event_detail_message);
            leader_tag = (ImageView) itemView.findViewById(R.id.leader_ear);
            inviteIcon = (ImageView) itemView.findViewById(R.id.invite_icon);
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
            if(clanTag!=null) {
                clanTag.setVisibility(View.VISIBLE);
            }
            setBanners();

            holder.inviteIcon.setVisibility(View.GONE);
            if (position >= playerLocal.size() && getMaxPlayer() > playerLocal.size() ) {
                holder.playerName.setText("searching...");
                holder.playerName.setTextColor(getResources().getColor(R.color.trimbe_white));
                holder.message.setVisibility(View.GONE);
                holder.playerProfile.setImageResource(R.drawable.img_profile_blank);
                if(position==playerLocal.size()) {
                    if (checkUserIsPlayer()) {
                        holder.inviteIcon.setVisibility(View.VISIBLE);
                        holder.inviteIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((EventDetailActivity) getActivity()).showAnimatedInviteView();
                            }
                        });
                    }
                }
            } else {
                if (playerLocal.get(position) != null) {
                    if (playerLocal.get(position).getPlayerId() != null) {
                        currPlayerId = playerLocal.get(position).getPlayerId();
                    }

                    holder.message.setVisibility(View.GONE);

                    holder.leader_tag.setVisibility(View.GONE);

                    if (playerLocal.get(position).getPsnId() != null) {
                        String name = playerLocal.get(position).getPsnId();
                        if(ifPlayerisUserAndVerified(name)) {
                            if (playerLocal.get(position).getClanTag() != null && !playerLocal.get(position).getClanTag().isEmpty()) {
                                name = name + " [" + playerLocal.get(position).getClanTag() + "]";
                            }
                            if (playerLocal.get(position).getPlayerImageUrl() != null) {
                                Util.picassoLoadIcon(((EventDetailActivity)getActivity()), holder.playerProfile, playerLocal.get(position).getPlayerImageUrl(),
                                        R.dimen.eventdetail_player_profile_hgt, R.dimen.eventdetail_player_profile_width, R.drawable.profile_image);
                            }
                        } else {
                            Util.picassoLoadIcon(((EventDetailActivity)getActivity()), holder.playerProfile, null,
                                    R.dimen.eventdetail_player_profile_hgt, R.dimen.eventdetail_player_profile_width, R.drawable.profile_image);

                        }
                        holder.playerName.setText(name);
                        holder.playerName.setTextColor(getResources().getColor(R.color.activity_light_color));

                       if(decideLeaderTag(position, playerLocal.get(position).getPsnId())) {
                           holder.leader_tag.setVisibility(View.VISIBLE);
                       }
                    }

//                    if (checkUserIsCreator() && (!playerLocal.get(position).getPlayerId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId()))) {
//                        //holder.message.setVisibility(View.VISIBLE);
//                    }
//                    if ((checkUserIsPlayer() && !checkUserIsCreator()) && (playerLocal.get(position).getPlayerId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId()))) {
//                        //holder.message.setVisibility(View.VISIBLE);
//                    }

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

    private boolean checkPlayerIsAvailable(String id) {
        if(id!=null) {
            if(currentEvent!=null && currentEvent.getPlayerData()!=null) {
                for(int i=0; i<currentEvent.getPlayerData().size();i++) {
                    if (id.equalsIgnoreCase(currentEvent.getPlayerData().get(i).getPlayerId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkUserIsCreator() {
        if(this.currentEvent.getPlayerData()!=null) {
            if (user.getUserId().equalsIgnoreCase(currentEvent.getCreatorData().getPlayerId())) {
                return true;
            }
        }
        return false;
    }

    private boolean ifPlayerisUserAndVerified(String pId) {
//        if(user!=null && user.getPsnId()!=null) {
//            if(user.getPsnVerify()!=null && !user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
//                return true;
//            }
//        }
        if(pId!=null && currentEvent!=null && this.currentEvent.getPlayerData()!=null) {
            for (int i = 0; i < currentEvent.getPlayerData().size(); i++) {
                if (pId.equalsIgnoreCase(currentEvent.getPlayerData().get(i).getPsnId())) {
                    if(currentEvent.getPlayerData().get(i).getPsnVerify()!=null && currentEvent.getPlayerData().get(i).getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean decideLeaderTag(int position, String playerId) {
        if(currentEvent!=null && currentEvent.getEventStatus()!=null && currentEvent.getEventStatus().equalsIgnoreCase(Constants.STATUS_FULL)) {
            if(playerId!=null) {
                if(currentEvent.getCreatorData()!=null && currentEvent.getCreatorData().getPsnId()!=null && currentEvent.getCreatorData().getPsnId().equalsIgnoreCase(playerId)) {
                    return true;
                }
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
        public void onBindViewHolder(CurrentEventsCommentsViewHolder holder, final int position) {
            setBanners();
            if(commentsLocal!=null && !commentsLocal.isEmpty()) {
                if(commentsLocal.get(position)!=null) {
                    boolean c=true;
                    if(c){
                        if(clanTag!=null) {
                            clanTag.setVisibility(View.GONE);
                        }
                        c=false;
                    }
                    boolean reported = commentsLocal.get(position).getReported();
                    holder.leader_comment_tag.setVisibility(View.GONE);
                    if(reported) {
                        holder.playerNameComment.setVisibility(View.GONE);
                        holder.playerProfileComment.setImageDrawable(getResources().getDrawable(R.drawable.img_profile_blank));
                    } else {
                        holder.commentCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!commentsLocal.get(position).getReported()) {
                                    RequestParams rp = new RequestParams();
                                if (currentEvent != null && currentEvent.getEventId() != null && !currentEvent.getEventId().isEmpty()) {
                                    rp.put("eId", currentEvent.getEventId());
                                    if (commentsLocal.get(position).getId() != null && !commentsLocal.get(position).getId().isEmpty()) {
                                        rp.put("commentId", commentsLocal.get(position).getId());
                                    }
                                    if (mManager!=null && mManager.getUserData()!=null && mManager.getUserData().getMaxReported()) {
                                        ((EventDetailActivity) getActivity()).showGenericError(getString(R.string.report_issue_header), getString(R.string.report_issue_next), "NEXT", Constants.REPORT_COMMENT_NEXT, rp, false);
                                    } else {
                                        ((EventDetailActivity) getActivity()).showGenericError(getString(R.string.report_issue_header), getString(R.string.report_issue), "REPORT", Constants.REPORT_COMMENT, rp, false);
                                    }
                                }
                            }
                            }
                        });
                        if (commentsLocal.get(position).getPsnId() != null) {
                            String name = commentsLocal.get(position).getPsnId();
                            if (commentsLocal.get(position).getPsnVerify()!=null && commentsLocal.get(position).getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
                                if (commentsLocal.get(position).getClanTag() != null) {
                                    name = name + " [" + commentsLocal.get(position).getClanTag() + "]";
                                }
                                if (commentsLocal.get(position).getPlayerImageUrl() != null && !commentsLocal.get(position).getPlayerImageUrl().isEmpty()) {
                                    String playerImg = commentsLocal.get(position).getPlayerImageUrl();
                                    Util.picassoLoadImageWithoutMeasurement(getActivity(), holder.playerProfileComment, playerImg, R.drawable.profile_image);
                                }
                            } else {
                                Util.picassoLoadImageWithoutMeasurement(getActivity(), holder.playerProfileComment, null, R.drawable.profile_image);
                            }
                            holder.playerNameComment.setVisibility(View.VISIBLE);
                            holder.playerNameComment.setText(name);
                            holder.playerNameComment.setTextColor(getResources().getColor(R.color.activity_light_color));

                            if(decideLeaderTag(position, commentsLocal.get(position).getPsnId())){
                                holder.leader_comment_tag.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    if (commentsLocal.get(position).getComment() != null && !commentsLocal.get(position).getComment().isEmpty()) {
                        String text = commentsLocal.get(position).getComment();
                        if(reported) {
                            text  = "[comment removed]";
                        }
                        holder.playerCommentText.setText(text);
                        holder.playerCommentText.setGravity(Gravity.CENTER_VERTICAL);
                        holder.playerCommentText.setTextColor(getResources().getColor(R.color.trimbe_white));
                    }

                    if(commentsLocal.get(position).getPlayerId()!=null) {
                        if (!checkPlayerIsAvailable(commentsLocal.get(position).getPlayerId())) {
                            holder.playerNameComment.setTextColor(getResources().getColor(R.color.player_left_comment));
                            holder.playerProfileComment.setImageDrawable(getResources().getDrawable(R.drawable.img_profile_blank));
                            holder.playerCommentText.setTextColor(getResources().getColor(R.color.player_left_comment));
                        }
                    }

                    if (commentsLocal.get(position).getCreated() != null) {
                        String time = Util.updateLastReceivedDate(commentsLocal.get(position).getCreated(), getActivity().getResources());
                        if (time != null) {
                            holder.time.setText(time);
                        }
                    }
                }
            }
            holder.playerProfileComment.invalidate();
            holder.playerCommentText.invalidate();
            holder.playerNameComment.invalidate();
        }

        @Override
        public int getItemCount() {
            return commentsLocal.size();
        }

        public class CurrentEventsCommentsViewHolder extends RecyclerView.ViewHolder {
            private CircularImageView playerProfileComment;
            private CardView commentCard;
            private TextView playerNameComment;
            private TextView playerCommentText;
            private TextView time;
            private ImageView leader_comment_tag;
            public CurrentEventsCommentsViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                commentCard = (CardView) itemView.findViewById(R.id.eventdetail_commentcard);
                playerProfileComment = (CircularImageView) itemView.findViewById(R.id.event_detail_comment_player_profile);
                playerNameComment = (TextView) itemView.findViewById(R.id.player_name_comment);
                playerCommentText = (TextView) itemView.findViewById(R.id.comment_text);
                time = (TextView) itemView.findViewById(R.id.comment_time);
                leader_comment_tag = (ImageView) itemView.findViewById(R.id.leader_comment_ear);
            }
        }
    }
}
