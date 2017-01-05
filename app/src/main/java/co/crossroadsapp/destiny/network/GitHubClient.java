package co.crossroadsapp.destiny.network;

import org.json.JSONObject;

import co.crossroadsapp.destiny.data.BungieResponseData;
import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.data.UserDataNetwork;
import co.crossroadsapp.destiny.data.ValidateUserRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by sharmha on 11/16/16.
 */
public interface GitHubClient {
    @Headers("Content-Type: application/json")
    @POST("api/v1/auth/validateUserLogin")
    Call<UserDataNetwork> createUser(@Body ValidateUserRequest validateUserRequest);

    //@Headers("x-api-key: f091c8d36c3c4a17b559c21cd489bec0")
    @GET("https://www.bungie.net/Platform/User/GetCurrentBungieAccount")
    Call<BungieResponseData> getBungieUser(@Header("x-csrf") String csrf, @Header("Cookie") String cookie, @Header("x-api-key") String key);

}
