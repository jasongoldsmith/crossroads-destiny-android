package co.crossroadsapp.destiny;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;


public class UpdateConsoleActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private ControlManager mManager;
    private UserData user;
    private ArrayAdapter<String> adapterConsole;
    private ArrayList<String> consoleItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_console);

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        Bundle b=null;
        if(getIntent()!=null && getIntent().getExtras()!=null) {
            b = getIntent().getExtras();
        }
        //user = b.getParcelable("userdata");
        if(mManager.getUserData()!=null) {
            user = mManager.getUserData();
        } else if(b!=null){
            user = b.getParcelable("userdata");
        }

        //spinner customization
        Spinner dropdown = (Spinner) findViewById(R.id.spinner_select_console);

        dropdown.setOnItemSelectedListener(this);
        // Set adapter for console selector
//        ArrayList<String> consoleItems = new ArrayList<String>();
        consoleItems = Util.getRemConsoleName(user.getConsoleType());

        //consoleItems.add(n);
        adapterConsole = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, consoleItems) {

            int FONT_STYLE = Typeface.BOLD;

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTypeface(Typeface.SANS_SERIF, FONT_STYLE);
                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.trimbe_white)
                );
                ((TextView) v).setGravity(Gravity.CENTER);

                ((TextView) v).setPadding(Util.dpToPx(0, UpdateConsoleActivity.this), 0, 0, 0);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                ((TextView) v).setText(((TextView) v).getText());

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent, consoleItems);
            }
        };
        adapterConsole.setDropDownViewResource(R.layout.empty_layout);
        dropdown.setAdapter(adapterConsole);
        adapterConsole.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(consoleItems.get(position).equalsIgnoreCase(Constants.CONSOLEPS4STRG)) {
            //set update text warning
        } else if(consoleItems.get(position).equalsIgnoreCase(Constants.CONSOLEXBOXONESTRG)){
            //set update text warning

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //console selector spinner
    private View getCustomView(int position, View convertView, ViewGroup parent, ArrayList<String> consoleItems) {
        LayoutInflater inflater=getLayoutInflater();
        View row=inflater.inflate(R.layout.console_selction_view, parent, false);
        ImageView addSymbol = (ImageView)row.findViewById(R.id.console_img);
        CardView card = (CardView)row.findViewById(R.id.console_card);
        if(position==0) {
            card.setBackgroundColor(getResources().getColor(R.color.freelancer_background));
            ImageView dropArw = (ImageView)row.findViewById(R.id.drop_arrow);
            dropArw.setVisibility(View.VISIBLE);
        }
//        if (position==consoleItems.size()-1) {
//            addSymbol.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_console));
//            card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //start new activity for event
//                    Intent regIntent = new Intent(UpdateConsoleActivity.this,
//                            UpdateConsoleActivity.class);
//                    regIntent.putExtra("userdata", user);
//                    startActivity(regIntent);
//                }
//            });
//        }
        if(consoleItems.get(position).equalsIgnoreCase("PlayStation 4") || consoleItems.get(position).equalsIgnoreCase("PlayStation 3")){
            addSymbol.setImageDrawable(getResources().getDrawable(R.drawable.icon_psn_console));
        } else if(consoleItems.get(position).equalsIgnoreCase("Xbox One") || consoleItems.get(position).equalsIgnoreCase("Xbox 360")){
            addSymbol.setImageDrawable(getResources().getDrawable(R.drawable.icon_xboxone_console));
        }

        TextView label=(TextView)row.findViewById(R.id.add_console_text);
        if (consoleItems!=null) {
            label.setText(consoleItems.get(position));
        }
        return row;
    }
}
