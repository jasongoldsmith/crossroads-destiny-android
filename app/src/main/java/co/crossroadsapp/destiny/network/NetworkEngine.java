package co.crossroadsapp.destiny.network;

import android.content.Context;

import co.crossroadsapp.destiny.utils.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * Created by sharmha on 2/23/16.
 */
public class NetworkEngine {

    PersistentCookieStore myCookieStore;
    private static NetworkEngine mInstance;

    //Switch development or production server
    //development server
    //private final String BASE_URL = "https://travelerbackend.herokuapp.com/api/v1/";
    private final String BASE_URL = Util.getNetworkUrl();
    private AsyncHttpClient client;
    //production server
    //private final static String BASE_URL = "https://travelerbackendproduction.herokuapp.com/api/v1/";

    private NetworkEngine(Context c) {
        client = new AsyncHttpClient();
        myCookieStore = new PersistentCookieStore(c);
        //myCookieStore.clear();
        if(myCookieStore!=null) {
            client.setCookieStore(myCookieStore);
        }
    }

    public static NetworkEngine getmInstance(Context context) {
        if (mInstance==null) {
            mInstance = new NetworkEngine(context);
            return mInstance;
        } else {
            mInstance = new NetworkEngine(context);
            return mInstance;
        }
    }

    public PersistentCookieStore getCookie() {
        return myCookieStore;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler){
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

//    public String  performPostCall(String requestURL,
//                                   HashMap<String, String> postDataParams) {
//
//        URL url;
//        String response = "";
//        try {
//            url = new URL(requestURL);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(15000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(getPostDataString(postDataParams));
//
//            writer.flush();
//            writer.close();
//            os.close();
//            int responseCode=conn.getResponseCode();
//
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                String line;
//                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line=br.readLine()) != null) {
//                    response+=line;
//                }
//            }
//            else {
//                response="";
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return response;
//    }
//
//    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//        for(Map.Entry<String, String> entry : params.entrySet()){
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//        }
//
//        return result.toString();
//    }

}
