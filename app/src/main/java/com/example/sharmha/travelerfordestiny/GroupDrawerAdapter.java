package com.example.sharmha.travelerfordestiny;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharmha.travelerfordestiny.data.EventData;
import com.example.sharmha.travelerfordestiny.data.GroupData;
import com.example.sharmha.travelerfordestiny.data.UserData;
import com.example.sharmha.travelerfordestiny.utils.Constants;
import com.example.sharmha.travelerfordestiny.utils.Util;
import com.example.sharmha.travellerdestiny.R;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class GroupDrawerAdapter {

    private ArrayList<GroupData> localGroupList;
    private RecyclerView mRecyclerView;
    private ActivityGroupViewAdapter mAdapter;
    private ControlManager mCntrlMngr;
    private View view;
    private Context c;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_group);
//
//        mCntrlMngr = ControlManager.getmInstance();
//        mCntrlMngr.getGroupList(this);
//
//        localGroupList = new ArrayList<GroupData>();
//        //recyclerview for activity list
//        mRecyclerView = (RecyclerView) findViewById(R.id.group_recycler);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        mAdapter = new ActivityGroupViewAdapter();
//
//        mRecyclerView.setAdapter(mAdapter);
//    }

    public GroupDrawerAdapter(ListActivityFragment act) {
        c = act;

        mCntrlMngr = ControlManager.getmInstance();
        mCntrlMngr.getGroupList(act);

        localGroupList = new ArrayList<GroupData>();
        //recyclerview for activity list
        mRecyclerView = (RecyclerView) act.findViewById(R.id.group_recycler);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(act));

        mAdapter = new ActivityGroupViewAdapter();

        mRecyclerView.setAdapter(mAdapter);

    }

    public void update(Object data) {
        localGroupList = (ArrayList<GroupData>) data;
        getSelectedGrp(localGroupList);
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
                            }
                        }
                    }
                }
            }
        }
    }

    private class ActivityGroupViewAdapter extends RecyclerView.Adapter<ActivityGroupViewAdapter.MyGroupViewHolder> {

        private ArrayList<GroupData> glistLocal;
        GroupData objGroup;

        public ActivityGroupViewAdapter() {
            glistLocal = new ArrayList<GroupData>();
//            if(currentGroupList!=null) {
//                glistLocal = currentGroupList;
//            }
        }

        protected void addItem(ArrayList<GroupData> a) {
            this.glistLocal.clear();
            this.glistLocal.addAll(a);
        }

        public class MyGroupViewHolder extends RecyclerView.ViewHolder{
            protected CardView groupCard;
            protected ImageView groupImage;
            protected TextView groupName;
            protected TextView groupMemberCount;
            protected TextView groupEventCount;
            protected CheckBox groupBtn;
            protected TextView saveGrpBtn;
            protected TextView saveGrpBtnReady;
            protected TextView emptyGrpText;
            protected RelativeLayout group_layout;
            protected RelativeLayout empty_group_layout;

            public MyGroupViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                groupCard = (CardView) view.findViewById(R.id.groups);
                groupImage = (ImageView) view.findViewById(R.id.group_image);
                groupName = (TextView) view.findViewById(R.id.group_name);
                groupMemberCount = (TextView) view.findViewById(R.id.group_members);
                groupEventCount = (TextView) view.findViewById(R.id.group_events);
                groupBtn = (CheckBox) view.findViewById(R.id.group_radio_btn);
                emptyGrpText = (TextView) view.findViewById(R.id.empty_grp_text);
                saveGrpBtnReady = (TextView) ((ListActivityFragment)c).findViewById(R.id.save_group_btn_ready);
                saveGrpBtn = (TextView) ((ListActivityFragment)c).findViewById(R.id.save_group_btn);
                group_layout = (RelativeLayout) view.findViewById(R.id.group_layout);
                empty_group_layout = (RelativeLayout) view.findViewById(R.id.empty_group_layout);
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
                if(holder.empty_group_layout!=null) {
                    holder.empty_group_layout.setVisibility(View.GONE);
                }
                if (holder.group_layout!=null) {
                    holder.group_layout.setVisibility(View.VISIBLE);
                }
                objGroup = glistLocal.get(position);
                String gName = null;
                int count = 0;
                int eCount = 0;
                String url = null;
                //boolean clan = true;
                if(glistLocal.get(position)!=null) {
                    if (objGroup.getGroupName()!=null) {
                        gName = objGroup.getGroupName();
                    }
                    if(objGroup.getMemberCount()>=0) {
                        count = objGroup.getMemberCount();
                    }
                    if(objGroup.getMemberCount()>0 ) {
                        eCount = objGroup.getEventCount();
                    }
                    if(objGroup.getGroupImageUrl()!=null) {
                        url = objGroup.getGroupImageUrl();
                    }
                }

                if(holder.groupName!=null && gName!=null) {
                    holder.groupName.setText(gName);
                }

                if(holder.groupMemberCount!=null) {
                    holder.groupMemberCount.setText(count + " Members");
                }
                if(holder.groupImage!=null) {
                    Util.picassoLoadIcon(c, holder.groupImage, url, R.dimen.group_icon_hgt, R.dimen.group_icon_width, R.drawable.img_logo_badge);
                }

                if(holder.groupEventCount!=null) {
                    holder.groupEventCount.setText(eCount + " Events");
                }

                //in some cases, it will prevent unwanted situations
                holder.groupBtn.setOnCheckedChangeListener(null);

                //if true, your checkbox will be selected, else unselected
                holder.groupBtn.setChecked(objGroup.isSelected);

                holder.groupBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //set your object's last status
                        if(isChecked) {
                            for (int y=0;y<glistLocal.size();y++) {
                                if (y==position) continue;
                                objGroup = glistLocal.get(y);
                                if (objGroup.isSelected) {
                                    objGroup.setGroupSelected(false);
                                    notifyItemChanged(y);
                                }
                            }
                            glistLocal.get(position).setGroupSelected(isChecked);
                            notifyItemChanged(position);

                            //show save button ready state
                            holder.saveGrpBtn.setVisibility(View.GONE);
                            holder.saveGrpBtnReady.setVisibility(View.VISIBLE);
                        } else {
                            //show save button ready state
                            holder.saveGrpBtn.setVisibility(View.VISIBLE);
                            holder.saveGrpBtnReady.setVisibility(View.GONE);
                        }
//                        objGroup.setGroupSelected(isChecked);
//                        for (int y=0;y<glistLocal.size();y++) {
//                            if((!objGroup.getGroupId().equalsIgnoreCase(glistLocal.get(y).getGroupId())) && isChecked) {
//                                glistLocal.get(y).setGroupSelected(false);
//                                notifyItemChanged(y);
//                            }
//                        }
                    }
                });
                holder.saveGrpBtnReady.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams params = new RequestParams();
                        if(mCntrlMngr!=null) {
                        if(mCntrlMngr.getUserData()!=null) {
                            UserData user = mCntrlMngr.getUserData();
                            if (user.getUserId() != null) {
                                params.add("id", user.getUserId());
                            }

                            for (int y=0;y<glistLocal.size();y++) {
                                if (glistLocal.get(y).isSelected) {
                                    objGroup = glistLocal.get(y);
                                }
                            }

                            if (objGroup != null) {
                                if (objGroup.getGroupId() != null) {
                                    params.add("clanId", objGroup.getGroupId());
                                }
                            }
                            ((ListActivityFragment) c).hideProgress();
                            ((ListActivityFragment) c).showProgress();
                            mCntrlMngr.postSetGroup((ListActivityFragment) c, params);
                        }
                    }
                    }
                });
            } else {
                holder.empty_group_layout.setVisibility(View.GONE);
                holder.group_layout.setVisibility(View.VISIBLE);
                holder.emptyGrpText.setText(Html.fromHtml((c.getString(R.string.nogroup_text))));
            }
        }

        @Override
        public int getItemCount() {
            if(glistLocal!=null) {
                return this.glistLocal.size();
            }
            return 0;
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

}
