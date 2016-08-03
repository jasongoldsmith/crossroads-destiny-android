package co.crossroadsapp.destiny;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;


public class UpdateConsoleActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, Observer {

    private ControlManager mManager;
    private UserData user;
    private ArrayAdapter<String> adapterConsole;
    private ArrayList<String> consoleItems;
    private String selectedConsole=null;
    ArrayList<String> existingConsoles;

    TextView consoleRelatedText;
    RelativeLayout idLayout;
    TextView consoleName;
    EditText consoleEditHint;
    private String console;
    String conId;
    TextView addBtnText;

    AlertDialog dialog;


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

        final ImageView imgConsole = (ImageView) findViewById(R.id.select_console_img);

        idLayout  = (RelativeLayout) findViewById(R.id.gamertag_layout);
        consoleRelatedText = (TextView) findViewById(R.id.console_bottom_text);
        consoleName = (TextView) findViewById(R.id.console_name);
        consoleEditHint = (EditText) findViewById(R.id.console_id);

        CardView addBtn = (CardView) findViewById(R.id.add_console);
        addBtnText = (TextView) findViewById(R.id.add_btn_text);

        dropdown.setOnItemSelectedListener(this);
        // Set adapter for console selector
//        ArrayList<String> consoleItems = new ArrayList<String>();
        ArrayList<String> userConsoleList = mManager.getConsoleList();
        existingConsoles = Util.getCorrectConsoleName(userConsoleList);
        consoleItems = Util.getRemConsoleName(userConsoleList);

        selectedConsole = consoleItems.get(0);

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                if(console!=null) {
                    if (idLayout.getVisibility() == View.VISIBLE) {
                        conId = consoleEditHint.getText().toString();
                    } else {
                        conId = getConsoleId(console);
                    }
                    if (conId != null) {
                        showProgressBar();
                        RequestParams rp_console = new RequestParams();
                        rp_console.add("consoleId", conId);
                        rp_console.add("consoleType", console);
                        mManager.addOtherConsole(UpdateConsoleActivity.this, rp_console);
                    }
                }
            }
        });

        setTextForConsole();
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
                if(((TextView) v).getText().toString().equalsIgnoreCase(Constants.CONSOLEXBOXONESTRG) || ((TextView) v).getText().toString().equalsIgnoreCase(Constants.CONSOLEXBOX360STRG)) {
                    imgConsole.setImageResource(R.drawable.icon_xboxone_console);
                    consoleName.setText("XBOX GAMERTAG");
                    consoleEditHint.setHint("ENTER XBOX GAMERTAG");
                } else {
                    imgConsole.setImageResource(R.drawable.icon_psn_console);
                    consoleName.setText("PLAYSTATION ID");
                    consoleEditHint.setHint("ENTER PLAYSTATION ID");
                }
                selectedConsole = ((TextView) v).getText().toString();
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

    private String getConsoleId(String console) {
        if (console.equalsIgnoreCase(Constants.CONSOLEPS4)) {
                for (int n=0;n<user.getConsoles().size();n++) {
                    if(user.getConsoles().get(n).getcType().equalsIgnoreCase(Constants.CONSOLEPS3)) {
                        return user.getConsoles().get(n).getcId();
                    }
                }
        } else if(console.equalsIgnoreCase(Constants.CONSOLEXBOXONE)){
                for (int n=0;n<user.getConsoles().size();n++) {
                    if(user.getConsoles().get(n).getcType().equalsIgnoreCase(Constants.CONSOLEXBOX360)) {
                        return user.getConsoles().get(n).getcId();
                    }
                }
        }
        return null;
    }

    public void showError(String err) {
        hideProgressBar();
        setErrText(err);
    }

    private void setTextForConsole() {
        if (selectedConsole != null) {
            if (!existingConsoles.contains(selectedConsole)) {
                if (selectedConsole.equalsIgnoreCase(Constants.CONSOLEXBOXONESTRG) && existingConsoles.contains(Constants.CONSOLEXBOX360STRG)) {
                    idLayout.setVisibility(View.GONE);
                    consoleRelatedText.setVisibility(View.VISIBLE);
                    consoleRelatedText.setText("NOTE: Once you upgrade to Xbox One, you will no longer be able to view activities from Xbox 360.");
                } else if (selectedConsole.equalsIgnoreCase(Constants.CONSOLEPS4STRG) && existingConsoles.contains(Constants.CONSOLEPS3STRG)) {
                    idLayout.setVisibility(View.GONE);
                    consoleRelatedText.setVisibility(View.VISIBLE);
                    consoleRelatedText.setText("NOTE: Once you upgrade to PlayStation 4, you will no longer be able to view activities from PlayStation 3.");
                } else {
                    idLayout.setVisibility(View.VISIBLE);
                    consoleRelatedText.setVisibility(View.GONE);
                }
            } else {
                idLayout.setVisibility(View.VISIBLE);
                consoleRelatedText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        console = parent.getItemAtPosition(position).toString();
        if(console!=null) {
            selectedConsole = console;
            switch (console)
            {
                case "PlayStation 3" :
                    console = "PS3";
                    break;
                case "PlayStation 4" :
                    console = "PS4";
                    break;
                case "Xbox One":
                    console = "XBOXONE";
                    break;
                case "Xbox 360":
                    console = "XBOX360";
                    break;

            }
        }
        setTextForConsole();
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
//        if(position==0) {
//            card.setBackgroundColor(getResources().getColor(R.color.freelancer_background));
//            ImageView dropArw = (ImageView)row.findViewById(R.id.drop_arrow);
//            dropArw.setVisibility(View.VISIBLE);
//        }
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

    @Override
    public void update(Observable observable, Object data) {
        hideProgressBar();
        if(data!=null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateConsoleActivity.this)
                    .setTitle("Success!")
                    .setMessage("Your " +console + " " + conId + " has been added to your account")
                    .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with finish activity
                            dialog.dismiss();
                            finish();
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if(dialog!=null) {
            dialog.dismiss();
        }else {
            super.onBackPressed();
//            Intent in = new Intent(this, SendBackpressBroadcast.class);
//            this.startService(in);
            finish();
        }
    }
}
