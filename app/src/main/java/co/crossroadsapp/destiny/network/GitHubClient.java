package co.crossroadsapp.destiny.network;

import co.crossroadsapp.destiny.data.UserData;
import co.crossroadsapp.destiny.data.ValidateUserRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by sharmha on 11/16/16.
 */
public interface GitHubClient {
    @Headers("Content-Type: application/json")
    @POST("api/v1/auth/validateUserLogin")
    Call<UserData> createUser(@Body ValidateUserRequest validateUserRequest);
}
