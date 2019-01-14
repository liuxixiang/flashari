package com.lxh.flashari.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WifiApiService {

    @GET
    Call<String> getListDCIM(@Url String dir);

    @GET("/command.cgi?op=101")
    Call<String> getImgsNum(@Query("DIR") String dir);

    @GET
    Call<String> getThumbnails(@Url String url);

    @GET
    Call<String> getFile(@Url String url);


}
