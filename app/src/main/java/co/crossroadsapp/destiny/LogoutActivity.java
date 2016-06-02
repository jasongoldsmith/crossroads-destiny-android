package co.crossroadsapp.destiny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import co.crossroadsapp.destiny.R;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.network.NetworkEngine;
import co.crossroadsapp.destiny.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sharmha on 2/23/16.
 */
public class LogoutActivity extends Activity {

    private RelativeLayout logout;
    private NetworkEngine ntwrk;
    private UserData ud;

    private String url = "auth/logout";
    @Override
    protected void onCreate(Bundle outState) {
        super.onCreate(outState);

        Bundle b = getIntent().getExtras();
        ud = b.getParcelable("userdata");

        setContentView(R.layout.logout);

        logout = (RelativeLayout) findViewById(R.id.logout_btn);

        //ntwrk = new NetworkEngine(getApplicationContext());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Util.isNetworkAvailable(getApplicationContext())) {
                        doLogout();
                    } else {
                        createDialogue();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //finish();
            }
        });
    }

    private void doLogout() throws JSONException {
        ntwrk.post(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                // go to logout page
                if (ud != null) {
                    ud.setPassword("");
                    ud.setUser("");

                    Util.clearDefaults(getApplicationContext());
                }

                // go to launch page
                Intent regIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(regIntent);

                finish();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(LogoutActivity.this, "Logout error from server  - " + statusCode,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void createDialogue() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LogoutActivity.this);

        // set title
        alertDialogBuilder.setTitle("          Connection Failure");

        // set dialog message
        alertDialogBuilder
                .setMessage("             No network available")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
