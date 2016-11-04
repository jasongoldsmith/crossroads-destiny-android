package co.crossroadsapp.destiny;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.facebook.applinks.AppLinkData;

import java.util.Map;

import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;

public class BungieUserService extends Service {
    public BungieUserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        final ControlManager cManager = ControlManager.getmInstance();

        String csrf = Util.getDefaults("csrf", getApplicationContext());
        String cookies = Util.getDefaults("cookie", getApplicationContext());
        //network call to get current user
        if(csrf!=null && cookies!=null) {
            cManager.getBungieCurrentUser(csrf, cookies, getApplicationContext());
        }
        return Service.START_NOT_STICKY;
    }
}
