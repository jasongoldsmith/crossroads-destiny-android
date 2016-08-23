package co.crossroadsapp.destiny;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.utils.Util;

import com.loopj.android.http.RequestParams;

import java.util.Observable;
import java.util.Observer;


public class ForgotLoginActivity extends BaseActivity implements Observer, AdapterView.OnItemSelectedListener {

    private ImageView backBtn;
    private EditText psnId;
    private CardView resetPassword;
    private String console=null;

//    private RelativeLayout errLayout;
//    private TextView errText;
//    private ImageView close_err;
    private ProgressDialog dialog;
    private ControlManager mManager;
    private Spinner consoleType;
    private TextView psnId_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);

        mManager = ControlManager.getmInstance();
        mManager.setCurrentActivity(this);

        backBtn = (ImageView) findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialog(this);

//        errLayout = (RelativeLayout) findViewById(R.id.error_layout);
//        errText = (TextView) findViewById(R.id.error_sub);
//        close_err = (ImageView) findViewById(R.id.err_close);
//
//        close_err.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                errLayout.setVisibility(View.GONE);
//            }
//        });

        resetPassword = (CardView) findViewById(R.id.send_forgotlogin);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psnId!=null && console!=null) {
                    String psnString = psnId.getText().toString();
                    if (!psnString.isEmpty()) {
                        dialog.show();
                        resetPassword.setEnabled(false);
                        RequestParams params = new RequestParams();
                        //params.put("userName", psnString);
                        params.add("consoleId", psnString);
                        params.add("consoleType", console);
                        mManager.postResetPassword(ForgotLoginActivity.this, params);
                    } else {
                        showError("Please enter ID");
                    }
                }
            }
        });

        consoleType = (Spinner) findViewById(R.id.spinner_forgot_login);

        setSpinnerAdapter();

        consoleType.setOnItemSelectedListener(this);

        psnId_text = (TextView) findViewById(R.id.psn_forgot_login);

        psnId = (EditText) findViewById(R.id.forgot_psn);

        psnId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // the user is done typing.
                    resetPassword.performClick();
                }
                return false;
            }
        });
    }

    private void setSpinnerAdapter() {
        // Array of console types
        final String[] objects = { "PlayStation 4", "PlayStation 3", "Xbox One", "Xbox 360"};

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, objects){
            int FONT_STYLE = Typeface.BOLD;

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTypeface(Typeface.SANS_SERIF, FONT_STYLE);
                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.trimbe_white)
                );
                ((TextView) v).setGravity(Gravity.CENTER);

                ((TextView) v).setPadding(Util.dpToPx(0, ForgotLoginActivity.this), 0, 0, 0);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                ((TextView) v).setText(((TextView) v).getText());

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                //View v = super.getDropDownView(position, convertView,parent);

                return getCustomView(position, convertView, parent, objects);

//                        ((TextView) v).setGravity(Gravity.CENTER);
//
//                        v.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
//                        ((TextView) v).setTextColor(getResources().getColor(R.color.trimbe_white));
//                        ((TextView) v).setHeight(Util.dpToPx(50, CreateNewEvent.this));
//
//                        return v;

            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        consoleType.setAdapter(spinnerArrayAdapter);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, String[] adapterCheckpoint) {

        LayoutInflater inflater=getLayoutInflater();
        View row=inflater.inflate(R.layout.fragment_checkpoint, parent, false);
        TextView label=(TextView)row.findViewById(R.id.activity_checkpoint_text);
        if (adapterCheckpoint!=null) {
            label.setText(adapterCheckpoint[position]);
        }
        return row;
    }

    public void showError(String err) {
        dialog.dismiss();
        resetPassword.setEnabled(true);
        setErrText(err);
    }

    @Override
    public void update(Observable observable, Object data) {
        dialog.dismiss();
        Toast.makeText(this, "Instructions for resetting your password have been sent to your Bungie.net account. Follow the instructions to choose a new password.",
                Toast.LENGTH_LONG).show();
        long timeInMillisecondTheToastIsShowingFor = 3000;
        (new Handler())
                .postDelayed(
                        new Runnable() {
                            public void run() {
                                // finish this activity here
                                finish();
                            }
                        }, timeInMillisecondTheToastIsShowingFor);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        console = parent.getItemAtPosition(position).toString();
        if(console!=null) {
            switch (console)
            {
                case "PlayStation 3" :
                    psnId_text.setText("PLAYSTATION ID");
                    psnId.setHint("Enter PlayStation ID");
                    console = "PS3";
                    break;
                case "PlayStation 4" :
                    psnId_text.setText("PLAYSTATION ID");
                    psnId.setHint("Enter PlayStation ID");
                    console = "PS4";
                    break;
                case "Xbox One":
                    psnId_text.setText("XBOX GAMERTAG");
                    psnId.setHint("Enter Xbox Gamertag");
                    console = "XBOXONE";
                    break;
                case "Xbox 360":
                    psnId_text.setText("XBOX GAMERTAG");
                    psnId.setHint("Enter Xbox Gamertag");
                    console = "XBOX360";
                    break;

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
