package com.lxh.flashari.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<String> getBaidu(@Url String url);
}
