package co.crossroadsapp.destiny.network;

import com.loopj.android.http.RequestParams;

import java.util.List;

import co.crossroadsapp.destiny.data.UserData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by sharmha on 11/16/16.
 */
public interface GitHubClient {
    @POST("api/v1/a/user/listById")
    Call<UserData> createUser(@Body RequestParams rp);
}
