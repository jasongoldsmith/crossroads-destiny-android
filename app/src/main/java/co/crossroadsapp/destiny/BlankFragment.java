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
import co.crossroadsapp.destiny.data.ReviewCardData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;

import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlankFragment extends Fragment {

    private View view;
    private UserData user;
    private ControlManager mManager;
    private ListActivityFragment mContext;
    private ArrayList<EventData> currentEventList;
    private ArrayList<EventData> fragmentCurrentEventList;
    private ArrayList<EventData> fragmentupcomingEventList;
    private ReviewCardData reviewCardData;
    EventCardAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<ActivityData> currentAdList;

    public BlankFragment() {
    }

    @SuppressLint("ValidFragment")
    public BlankFragment(ListActivityFragment act, ArrayList<EventData> eData, ArrayList<ActivityData> adData, ReviewCardData reviewData) {
        // Required empty public constructor
        mContext = act;
        currentEventList = new ArrayList<EventData>();
        currentAdList = new ArrayList<ActivityData>();
        reviewCardData = new ReviewCardData();
        currentEventList = eData;
        if(adData!=null) {
            currentAdList = adData;
        } else {
            //adData.clear();
        }
        if (reviewData!=null && reviewData.getmName()!=null) {
            reviewCardData = reviewData;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = ControlManager.getmInstance();
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

        adapter = new EventCardAdapter(currentEventList, currentAdList, reviewCardData, mContext, mManager, Constants.EVENT_FEED);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    void refreshItems() {
        //Util.updateCurrEventOnTime(currentEventList.get(0).getLaunchDate());
        // Load items
        // ...
        mManager.getEventList();

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);

        // Load complete
//        onItemsLoadComplete();
    }

    public boolean checkCurrentList() {
        if (currentEventList.size()==0){
            return true;
        }
        return false;
    }

    public void updateCurrListAdapter(ArrayList<EventData> eData, ArrayList<ActivityData> adActivityData, ReviewCardData reviewCardData){
        currentEventList = eData;
        currentAdList = new ArrayList<ActivityData>();

        if (adActivityData!=null) {
            currentAdList = adActivityData;
        }
        if (adapter!=null){
            adapter.elistLocal.clear();
            adapter.adList.clear();
            adapter.addItem(currentEventList, currentAdList, reviewCardData);
            adapter.notifyDataSetChanged();
        }
    }

}