package com.scc.wanandroid.network;

import com.scc.wanandroid.network.api.IWanAndroidService;
import com.scc.wanandroid.network.utils.NetworkSSL;
import com.scc.wanandroid.network.utils.TrustManager;
import com.scc.wanandroid.utils.MLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@InstallIn(SingletonComponent.class)
@Module
public class NetworkModule {
    private static int TIME_OUT = 30; //30秒超时断开连接
    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    MLog.e("--network--", URLDecoder.decode(message, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    MLog.e("--network--", message);
                }
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .sslSocketFactory(new NetworkSSL(TrustManager.trustAllCert), TrustManager.trustAllCert)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
    }
    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(IWanAndroidService.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    @Singleton
    @Provides
    IWanAndroidService providesWanAndroidService(Retrofit retrofit){
        return retrofit.create(IWanAndroidService.class);
    }
}
