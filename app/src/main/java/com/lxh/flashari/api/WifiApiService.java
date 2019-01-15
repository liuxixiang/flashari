package com.lxh.flashari.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WifiApiService {

    @GET
    Observable<String> getListDCIM(@Url String dir);

    @GET("/command.cgi?op=101")
    Observable<String> getImgsNum(@Query("DIR") String dir);

    @GET
    Observable<String> getThumbnails(@Url String url);

    @GET
    Observable<String> getFile(@Url String url);


}
