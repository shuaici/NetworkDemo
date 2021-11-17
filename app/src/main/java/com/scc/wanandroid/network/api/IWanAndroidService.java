package com.scc.wanandroid.network.api;

import com.scc.wanandroid.bean.HomeBanner;
import com.scc.wanandroid.bean.RegisterData;
import com.scc.wanandroid.bean.ResponseData;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IWanAndroidService {
    String BASE_URL = "https://www.wanandroid.com/";
    //OkHttp+Retrofit
    @GET("banner/json")
    Call<ResponseData<List<HomeBanner>>> homeBannerRetrofit();

    @POST("user/register")
    @FormUrlEncoded
    Call<ResponseData<RegisterData>> registerRetrofit(@FieldMap Map<String,String> map);

    //OkHttp+Retrofit+RxJava
    @GET("banner/json")
    Observable<ResponseData<List<HomeBanner>>> homeBanner();

    @POST("user/register")
    @FormUrlEncoded
    Observable<ResponseData<RegisterData>> register(@FieldMap Map<String,String> map);

}
