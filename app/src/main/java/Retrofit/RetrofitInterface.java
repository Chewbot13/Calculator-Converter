package Retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("/v6/376d1a8e38a6ddeb35edaea6/latest/{currency}")
    Call<JsonObject> getCurrency(@Path("currency") String currency);
}
