package co.crossroadsapp.destiny.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;

import com.mixpanel.android.mpmetrics.MPConfig;

import java.io.IOException;

import co.crossroadsapp.destiny.ControlManager;
import co.crossroadsapp.destiny.utils.Constants;
import co.crossroadsapp.destiny.utils.Util;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sharmha on 11/16/16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = Util.getNetworkUrl();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);

//    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request original = chain.request();
////            ControlManager cm = ControlManager.getmInstance();
// //           if(cm.getCurrentActivity()!=null) {
//                //ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
//                //NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                //DisplayMetrics metrics = c.getResources().getDisplayMetrics();
//                Request request = original.newBuilder()
//                        .header("config_token", Constants.CONFIG_TOKEN)
//                        .header("$lib_version", MPConfig.VERSION)
//                        .header("$os", "Android")
//                        .header("$os_version", Build.VERSION.RELEASE == null ? "UNKNOWN" : Build.VERSION.RELEASE)
//                        .header("$manufacturer", Build.MANUFACTURER == null ? "UNKNOWN" : Build.MANUFACTURER)
//                        .header("$brand", Build.BRAND == null ? "UNKNOWN" : Build.BRAND)
//                        .header("$model", Build.MODEL == null ? "UNKNOWN" : Build.MODEL)
//                        .method(original.method(), original.body())
//                        .build();
//
//                return chain.proceed(request);
////            }
////            return null;
//        }
//    });

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
