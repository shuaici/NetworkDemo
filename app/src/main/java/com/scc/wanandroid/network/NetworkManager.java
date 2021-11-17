package com.scc.wanandroid.network;

import com.scc.wanandroid.network.api.IWanAndroidService;
import com.scc.wanandroid.network.utils.NetworkSSL;
import com.scc.wanandroid.network.utils.TrustManager;
import com.scc.wanandroid.utils.MLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    private static volatile NetworkManager instances;
    private static volatile OkHttpClient okHttpClient;
    private static volatile Retrofit retrofit;

    public static NetworkManager getInstance() {
        if (instances == null) {
            synchronized (NetworkManager.class) {
                if (instances == null) {
                    instances = new NetworkManager();
                }
            }

        }
        return instances;
    }

    private static int TIME_OUT = 30; //30秒超时断开连接

    private OkHttpClient initClient() {
        if (okHttpClient == null) {
            synchronized (NetworkManager.class) {
                if (okHttpClient == null) {
                    //请求日志打印
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                        try {
                            MLog.e(URLDecoder.decode(message, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            MLog.e(message);
                        }
                    });
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    //注释1：设置OkHttpClient
                    okHttpClient = new OkHttpClient.Builder()
                            .sslSocketFactory(new NetworkSSL(TrustManager.trustAllCert), TrustManager.trustAllCert)
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .addInterceptor(loggingInterceptor)
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;

    }
    public Retrofit initRetrofit() {
        if (retrofit == null) {
            synchronized (NetworkManager.class) {
                if (retrofit == null) {
                    //注释2：设置Retrofit
                    retrofit = new Retrofit.Builder()
                            .client(initClient())//选填
                            .baseUrl(IWanAndroidService.BASE_URL)//必填
                            .addConverterFactory(GsonConverterFactory.create())//选填(数据转换器，解析)
                            .build();
                }
            }
        }
        return retrofit;
    }

    public Retrofit initRetrofitRxJava() {
        if (retrofit == null) {
            synchronized (NetworkManager.class) {
                if (retrofit == null) {
                    //注释2：设置Retrofit
                    retrofit = new Retrofit.Builder()
                            .client(initClient())//选填
                            .baseUrl(IWanAndroidService.BASE_URL)//必填
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//新增网络请求适配器
                            .addConverterFactory(GsonConverterFactory.create())//选填(数据转换器，解析)
                            .build();
                }
            }
        }
        return retrofit;
    }
}
