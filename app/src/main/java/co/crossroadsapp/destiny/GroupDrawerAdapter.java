package co.crossroadsapp.destiny;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import co.crossroadsapp.destiny.data.GroupData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import co.crossroadsapp.destiny.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class GroupDrawerAdapter {

    private final ImageView groupSelectedImage;
    private final TextView groupSelectedName;
    private final TextView groupSelectedMemberCount;
    private final TextView groupSelectedEventCount;
    private final ToggleButton groupSelectedMute;
    //private final CheckBox groupSelectedBtn;
    private ArrayList<GroupData> localGroupList;
    private RecyclerView mRecyclerView;
    private ActivityGroupViewAdapter mAdapter;
    private ControlManager mCntrlMngr;
    private View view;
    private Context c;
    private UserData user;
    private RelativeLayout empty_group_layout;
    private TextView emptyGrpText;
    private TextView gotoBungie;
    private RelativeLayout selectedGrpCard;

    public GroupDrawerAdapter(final ListActivityFragment act) {
        c = act;

        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.getGroupList();
        if(mCntrlMngr.getUserData()!=null) {
            user = mCntrlMngr.getUserData();
        }

        groupSelectedImage = (ImageView) act.findViewById(R.id.group_selected_image);
        groupSelectedName = (TextView) act.findViewById(R.id.group_selected_name);
        groupSelectedMemberCount = (TextView) act.findViewById(R.id.group_selected_members);
        groupSelectedEventCount = (TextView) act.findViewById(R.id.group_selected_events);
        groupSelectedMute = (ToggleButton) act.findViewById(R.id.mute_toggle);
        selectedGrpCard = (RelativeLayout) act.findViewById(R.id.group_selected_mainlayout);

        empty_group_layout = (RelativeLayout) act.findViewById(R.id.empty_group_layout);
        emptyGrpText = (TextView) act.findViewById(R.id.empty_grp_text);
        gotoBungie = (TextView) act.findViewById(R.id.goto_bungie);

        emptyGrpText.setText(Html.fromHtml((c.getString(R.string.unverified_bungie_text))));
        emptyGrpText.setMovementMethod(LinkMovementMethod.getInstance());
        gotoBungie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bungie.net"));
                c.startActivity(browserIntent);
            }
        });

        localGroupList = new ArrayList<GroupData>();

        getGrpList();

        checkSignleGrpView();

        setSelectedGroup();

        getSelectedGrp(localGroupList);

        //recyclerview for activity list
        mRecyclerView = (RecyclerView) act.findViewById(R.id.group_recycler);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(act));

        mAdapter = new ActivityGroupViewAdapter(localGroupList);

        mRecyclerView.setAdapter(mAdapter);

        if(empty_group_layout!=null) {
            empty_group_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListActivityFragment)c).showUnverifiedUserMsg();
                }
            });
        }
    }

    private void getGrpList() {
        if(mCntrlMngr.getCurrentGroupList()!=null) {
            ArrayList<GroupData> ll = mCntrlMngr.getCurrentGroupList();
            for (int i=0;i<ll.size();i++) {
                localGroupList.add(ll.get(i));
            }
        }
    }

    private void checkSignleGrpView() {
        if(user!=null && user.getPsnVerify()!=null && user.getPsnVerify().equalsIgnoreCase(Constants.PSN_VERIFIED)) {
            if (empty_group_layout != null) {
                empty_group_layout.setVisibility(View.GONE);
            }
        } else {
            if (empty_group_layout != null) {
                empty_group_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setMuteButton(final ToggleButton mute, boolean mn, final String id) {
        if (mute!=null) {
            mute.setEnabled(true);
            mute.setChecked(!mn);

            mute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (id != null) {
                        RequestParams muteParams = new RequestParams();
                        muteParams.add("groupId", id);
                        if (mute.isChecked()) {
                            muteParams.add("muteNotification", "false");
                        } else {
                            muteParams.add("muteNotification", "true");
                        }
                        mCntrlMngr.postMuteNoti(muteParams);
                    }
                }
            });
        }
    }

    public void setSelectedGroup() {
        if(mCntrlMngr.getUserData()!=null) {
            user = mCntrlMngr.getUserData();
            if (user.getClanId() != null) {
                GroupData gd = mCntrlMngr.getGroupObj(user.getClanId());
                if (gd != null) {
                    if (gd.getGroupImageUrl() != null) {
                        int ge_num = gd.getEventCount();
                        int gm_num = gd.getMemberCount();
                        boolean mute = gd.getMuteNotification();
                        //String first_gm = gd.getMemberCount() + c.getResources().getQuantityString(R.plurals.grp_member, gd.getMemberCount());
                        String first_gm = gm_num+ c.getResources().getString(R.string.grp_member);
                        String first_ge = ge_num + c.getResources().getQuantityString(R.plurals.grp_event, ge_num);
                        groupSelectedEventCount.setText(first_ge);
                        groupSelectedMemberCount.setText(first_gm);
                        if (ge_num>0) {
                            groupSelectedEventCount.setTextColor(c.getResources().getColor(R.color.activity_light_color));
                        }
                        groupSelectedName.setText(gd.getGroupName());
                        if (gd.getGroupId()!=null) {
                            setMuteButton(groupSelectedMute, mute, gd.getGroupId());
                            if (selectedGrpCard!=null) {
                                if (gd.getGroupId().equalsIgnoreCase(Constants.FREELANCER_GROUP)) {
                                    selectedGrpCard.setBackgroundColor(c.getResources().getColor(R.color.freelancer_background));
                                } else {
                                    selectedGrpCard.setBackgroundColor(c.getResources().getColor(R.color.logout_btn_background));
                                }
                            }
                        }
                        Util.picassoLoadIcon(c, groupSelectedImage, gd.getGroupImageUrl(), R.dimen.group_icon_hgt, R.dimen.group_icon_width, R.drawable.img_logo_badge);
                    }
                }
            }
        }
    }

    public void update(Object data) {
        //localGroupList = (ArrayList<GroupData>) data;
        ArrayList<GroupData> ll = (ArrayList<GroupData>) data;
        localGroupList = new ArrayList<GroupData>();
        for (int i=0;i<ll.size();i++) {
            localGroupList.add(ll.get(i));
        }
        setSelectedGroup();
        getSelectedGrp(localGroupList);
        checkSignleGrpView();
        mAdapter.addItem(localGroupList);
        mAdapter.notifyDataSetChanged();
    }

    private void getSelectedGrp(ArrayList<GroupData> localGroupList) {
        if (mCntrlMngr!=null) {
            if (localGroupList!=null && (!localGroupList.isEmpty())) {
                if (mCntrlMngr.getUserData()!=null) {
                    UserData u = mCntrlMngr.getUserData();
                    for (int i=0;i<localGroupList.size();i++) {
                        if(u.getClanId()!=null && (!u.getClanId().equalsIgnoreCase(Constants.CLAN_NOT_SET))) {
                            if(u.getClanId().equalsIgnoreCase(localGroupList.get(i).getGroupId())) {
                                localGroupList.get(i).setGroupSelected(true);
                                localGroupList.remove(i);

                            }
                        }
                    }
                }
            }
        }
    }

    public void updateGrpData(Object data) {
        GroupData gd = (GroupData) data;
        if (mAdapter.glistLocal!=null) {
            for (int i = 0; i < mAdapter.glistLocal.size(); i++) {
                if (gd.getGroupId() != null) {
                    if (mAdapter.glistLocal.get(i)!=null && mAdapter.glistLocal.get(i).getGroupId()!=null) {
                        if (gd.getGroupId().equalsIgnoreCase(mAdapter.glistLocal.get(i).getGroupId())) {
                            mAdapter.glistLocal.get(i).setMuteNotification(gd.getMuteNotification());
                            mAdapter.notifyItemChanged(i);
                        }
                    }
                }
            }
        }
    }

    private class ActivityGroupViewAdapter extends RecyclerView.Adapter<ActivityGroupViewAdapter.MyGroupViewHolder> {

        private ArrayList<GroupData> glistLocal;
        GroupData objGroup;

        public ActivityGroupViewAdapter(ArrayList<GroupData> currentGroupList) {
            glistLocal = new ArrayList<GroupData>();
            if(currentGroupList!=null) {
                glistLocal = currentGroupList;
            }
        }

        protected void addItem(ArrayList<GroupData> a) {
            this.glistLocal.clear();
            this.glistLocal.addAll(a);
        }

        protected ArrayList<GroupData> getList() {
            if (glistLocal!=null) {
                return glistLocal;
            }
            return null;
        }

        public class MyGroupViewHolder extends RecyclerView.ViewHolder{
            protected CardView groupCard;
            protected RelativeLayout groupCardLayout;
            protected ImageView groupImage;
            protected TextView groupName;
            protected TextView groupMemberCount;
            protected TextView groupEventCount;
            protected CheckBox groupBtn;
            protected TextView gotoBungie;
            protected TextView saveGrpBtnReady;
            protected ToggleButton muteBtn;
            protected TextView emptyGrpText;
            protected RelativeLayout group_layout;
            protected RelativeLayout empty_group_layout;
            protected ImageView grp_overlay;

            public MyGroupViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                groupCard = (CardView) view.findViewById(R.id.groups);
                groupCardLayout = (RelativeLayout) view.findViewById(R.id.group_mainlayout);
                groupImage = (ImageView) view.findViewById(R.id.group_image);
                groupName = (TextView) view.findViewById(R.id.group_name);
                groupMemberCount = (TextView) view.findViewById(R.id.group_members);
                groupEventCount = (TextView) view.findViewById(R.id.group_events);
                //groupBtn = (CheckBox) view.findViewById(R.id.group_radio_btn);
                muteBtn = (ToggleButton) view.findViewById(R.id.mute_toggle);
                emptyGrpText = (TextView) view.findViewById(R.id.empty_grp_text);
                //saveGrpBtnReady = (TextView) ((ListActivityFragment)c).findViewById(R.id.save_group_btn_ready);
                gotoBungie = (TextView) view.findViewById(R.id.goto_bungie);
                group_layout = (RelativeLayout) view.findViewById(R.id.group_layout);
                empty_group_layout = (RelativeLayout) view.findViewById(R.id.empty_group_layout);
                grp_overlay = (ImageView) view.findViewById(R.id.grp_overlay);
            }
        }
        @Override
        public ActivityGroupViewAdapter.MyGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_cardview, null);
            return new MyGroupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyGroupViewHolder holder, final int position) {
            if(glistLocal.size()>0) {
                objGroup = glistLocal.get(position);
                if (user != null) {
                    if (user.getClanId()!=null) {
                    if ((!user.getClanId().equalsIgnoreCase(Constants.CLAN_NOT_SET))) {
                        if (!objGroup.getGroupId().equalsIgnoreCase(user.getClanId())) {

                            if ((objGroup.getGroupId().equalsIgnoreCase(Constants.FREELANCER_GROUP))) {
                                holder.groupCardLayout.setBackgroundColor(c.getResources().getColor(R.color.freelancer_background));
                            } else {
                                holder.groupCardLayout.setBackgroundColor(c.getResources().getColor(R.color.logout_btn_background));
                            }

                            if (holder.empty_group_layout != null) {
                                holder.empty_group_layout.setVisibility(View.GONE);
                            }
                            if (holder.group_layout != null) {
                                holder.group_layout.setVisibility(View.VISIBLE);
                            }
                            if(holder.grp_overlay!=null) {
                                holder.grp_overlay.setVisibility(View.GONE);
                            }
                            String gName = null;
                            //String id = null;
                            int count = 0;
                            int eCount = 0;
                            String url = null;
                            boolean mn = false;
                            //boolean clan = true;
                            if (glistLocal.get(position) != null) {
                                if (objGroup.getGroupName() != null) {
                                    gName = objGroup.getGroupName();
                                }
                                mn = objGroup.getMuteNotification();
                                if (objGroup.getMemberCount() >= 0) {
                                    count = objGroup.getMemberCount();
                                }
                                if (objGroup.getEventCount() > 0) {
                                    eCount = objGroup.getEventCount();
                                }
                                if (objGroup.getGroupImageUrl() != null) {
                                    url = objGroup.getGroupImageUrl();
                                }
//                                if (objGroup.getGroupId()!=null) {
//                                    id = objGroup.getGroupId();
//                                }
                            }

                            if (holder.groupName != null && gName != null) {
                                holder.groupName.setText(gName);
                            }

                            if (holder.groupMemberCount != null) {
                                //String gm = count + c.getResources().getQuantityString(R.plurals.grp_member, count);
                                String gm = count + c.getResources().getString(R.string.grp_member);
                                holder.groupMemberCount.setText(gm);
                            }
                            if (holder.groupImage != null) {
                                Util.picassoLoadIcon(c, holder.groupImage, url, R.dimen.group_icon_hgt, R.dimen.group_icon_width, R.drawable.img_logo_badge_group);
                            }

                            if (holder.groupEventCount != null) {
                                if (eCount>0) {
                                    holder.groupEventCount.setTextColor(c.getResources().getColor(R.color.activity_light_color));
                                }else {
                                    holder.groupEventCount.setTextColor(c.getResources().getColor(R.color.orbit_text_color));
                                }
                                String ge = eCount + c.getResources().getQuantityString(R.plurals.grp_event, eCount);
                                holder.groupEventCount.setText(ge);
                            }

                            setMuteButton(holder.muteBtn, mn, glistLocal.get(position).getGroupId());

//                            if (holder.muteBtn!=null) {
//                                holder.muteBtn.setChecked(mn);
//
//                                holder.muteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                    @Override
//                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                        if (id != null) {
//                                            RequestParams muteParams = new RequestParams();
//                                            muteParams.add("groupId", id);
//                                            if (!isChecked) {
//                                                muteParams.add("muteNotification", "1");
//                                            } else {
//                                                muteParams.add("muteNotification", "0");
//                                            }
//                                            mCntrlMngr.postMuteNoti((ListActivityFragment) c, muteParams);
//                                        }
//                                    }
//                                });
//                            }

                            //in some cases, it will prevent unwanted situations
                            //holder.groupBtn.setOnCheckedChangeListener(null);

                            //if true, your checkbox will be selected, else unselected
                            //holder.groupBtn.setChecked(objGroup.isSelected);

                            holder.groupCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //holder.groupBtn.performClick();
                                    for (int y = 0; y < glistLocal.size(); y++) {
                                        if (y == position) continue;
                                        objGroup = glistLocal.get(y);
                                        if (objGroup.isSelected) {
                                            objGroup.setGroupSelected(false);
                                            notifyItemChanged(y);
                                        }
                                    }
                                    glistLocal.get(position).setGroupSelected(true);
                                    notifyItemChanged(position);

                                    //change grp icon
                                    ((ListActivityFragment) c).setgrpIcon(glistLocal.get(position).getGroupImageUrl());

                                    //close drawer
                                    ((ListActivityFragment) c).closeAllDrawers();

                                    //save selected grp
                                    RequestParams params = new RequestParams();
                                    if (mCntrlMngr != null) {
                                        if (mCntrlMngr.getUserData() != null) {
                                            UserData user = mCntrlMngr.getUserData();
                                            if (user.getUserId() != null) {
                                                params.add("id", user.getUserId());
                                            }
                                            params.add("clanId", glistLocal.get(position).getGroupId());
                                            params.add("clanName", glistLocal.get(position).getGroupName());
                                            params.add("clanImage", glistLocal.get(position).getGroupImageUrl());
                                            ((ListActivityFragment) c).hideProgress();
                                            ((ListActivityFragment) c).showProgress();
                                            mCntrlMngr.postSetGroup(params);
                                        }
                                    }
                                }
                            });
                            holder.groupEventCount.invalidate();
                            holder.groupMemberCount.invalidate();
                            holder.muteBtn.invalidate();
                        }
                    }
                }
            } else {
                    //setSelectedGroup();
                }
            } else {
                if(holder.grp_overlay!=null) {
                    holder.grp_overlay.setVisibility(View.VISIBLE);
                }
                if (holder.groupMemberCount != null) {
                    String gm_dummy = 82 + c.getResources().getString(R.string.grp_member);
                    holder.groupMemberCount.setText(gm_dummy);
                }
                if (holder.groupEventCount != null) {
                    holder.groupEventCount.setTextColor(c.getResources().getColor(R.color.activity_light_color));
                    String ge_dummy = 4 + c.getResources().getQuantityString(R.plurals.grp_event, 4);
                    holder.groupEventCount.setText(ge_dummy);
                }
                if (holder.muteBtn!=null) {
                    holder.muteBtn.setEnabled(false);
                }

                checkSignleGrpView();
            }
        }

        @Override
        public int getItemCount() {
            if(glistLocal!=null) {
                if (glistLocal.size()==0) {
                    return 1;
                } else {
                    return this.glistLocal.size();
                }
            }
            return 0;
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }
}
