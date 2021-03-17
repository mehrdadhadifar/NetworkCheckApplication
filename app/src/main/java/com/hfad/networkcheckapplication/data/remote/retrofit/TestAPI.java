package com.hfad.networkcheckapplication.data.remote.retrofit;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;


public interface TestAPI {

    @POST("api/v1/ping")
    Call<ResponseBody> ping();

}
