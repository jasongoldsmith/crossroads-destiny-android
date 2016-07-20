package co.crossroadsapp.destiny;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeDeck;

import java.util.ArrayList;
import java.util.List;

import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.utils.Util;

public class BaseActivity extends FragmentActivity {

    protected RelativeLayout errLayout;
    protected TextView errText;
    private RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setErrText(String errorText) {
        if(errText==null ){
            errText = (TextView) findViewById(R.id.error_sub);
        }

        if (errLayout==null) {
            errLayout = (RelativeLayout) findViewById(R.id.error_layout);
        }

        // show timed error message
        Util.showErrorMsg(errLayout, errText, errorText);

//        if(errText!=null && errLayout!=null) {
//            errLayout.setVisibility(View.GONE);
//            errLayout.setVisibility(View.VISIBLE);
//            errText.setText(errorText);
//
//            //put timer to make the error message gone after 5 seconds
//            errLayout.postDelayed(new Runnable() {
//                public void run() {
//                    if(errLayout!=null) {
//                        errLayout.setVisibility(View.GONE);
//                    }
//                }
//            }, 5000);
//        }
    }

    public void closeView(View view) {
        if(errLayout!=null) {
            errLayout.setVisibility(View.GONE);
        }
    }

    public class SwipeDeckAdapter extends BaseAdapter {

        private List<String> data;
        private Context context;

        public SwipeDeckAdapter(List<String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if(v == null){
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.base_notification, parent, false);
            }
            ((TextView) v.findViewById(R.id.base_textView2)).setText(data.get(position));

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = (String)getItem(position);
                    Log.i("BaseActivity", item);
                }
            });

            return v;
        }
    }

    protected void showNotifications(Context c) {
        LayoutInflater inflater = getLayoutInflater();
        View view = ((ListActivityFragment)c).getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
        if(view!=null) {
            View v = inflater.inflate(R.layout.base_notification, (ViewGroup) view);
            RelativeLayout base = (RelativeLayout) v.findViewById(R.id.rel_base_layout);
            base.setVisibility(View.VISIBLE);
            final SwipeDeck cardStack = (SwipeDeck) v.findViewById(R.id.swipe_deck);

            final ArrayList<String> testData = new ArrayList<>();
            testData.add("0");
            testData.add("1");
            testData.add("2");
            testData.add("3");
            testData.add("4");

            final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, this);
            cardStack.setAdapter(adapter);

            cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
                @Override
                public void cardSwipedLeft(int position) {
                    Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                    cardStack.swipeTopCardLeft(500);
                }

                @Override
                public void cardSwipedRight(int position) {
                    Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                    cardStack.swipeTopCardRight(2);
                }

                @Override
                public void cardsDepleted() {
                    Log.i("MainActivity", "no more cards");
                }

                @Override
                public void cardActionDown() {

                }

                @Override
                public void cardActionUp() {

                }
            });
        }
    }

    protected void showProgressBar() {
        if(progress == null) {
            progress = (RelativeLayout) findViewById(R.id.progress_base_layout);
        }

        if(progress!=null) {
            hideProgressBar();
            progress.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if(progress!=null) {
            progress.setVisibility(View.GONE);
        }
    }
}
