package com.scc.wanandroid;

import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.scc.wanandroid.bean.HomeBanner;
import com.scc.wanandroid.bean.HomeBean;
import com.scc.wanandroid.bean.RegisterData;
import com.scc.wanandroid.bean.ResponseData;
import com.scc.wanandroid.databinding.ActivityLoginBinding;
import com.scc.wanandroid.network.api.IWanAndroidService;
import com.scc.wanandroid.network.NetworkManager;
import com.scc.wanandroid.network.ResponseTransformer;
import com.scc.wanandroid.network.exception.ApiException;
import com.scc.wanandroid.network.exception.ErrorConsumer;
import com.scc.wanandroid.utils.MLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    @Inject
    IWanAndroidService iWanAndroidService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loginBtnGet.setOnClickListener(v -> {
//            getRetrofit();
//            getRetrofitRxJava();
//            getRetrofitRxJavaOptimization();
            getRetrofitRxJavaHilt();
        });
        binding.loginBtnPost.setOnClickListener(v -> {
            String account = binding.loginEtInputAccount.getText().toString();
            String passsword = binding.loginEtInputPassword.getText().toString();
            Map<String, String> map = new HashMap<>();
            map.put("username", account);
            map.put("password", passsword);
            map.put("repassword", passsword);
//            postRetrofit(map);
//            postRetrofitRxJava(map);
//            postRetrofitRxJavaOptimization(map);
            postRetrofitRxJavaHilt(map);
        });
    }


    //Retrofit
    private void getRetrofit() {
        //注释1：动态获取IWanAndroidService对象
        IWanAndroidService service = NetworkManager.getInstance().initRetrofit().create(IWanAndroidService.class);
        //注释2：进行GET网络请求
        service.homeBannerRetrofit().enqueue(new Callback<ResponseData<List<HomeBanner>>>() {
            @Override
            public void onResponse(Call<ResponseData<List<HomeBanner>>> call, Response<ResponseData<List<HomeBanner>>> response) {
                if (response.body().getData() != null) {
                    MLog.e(response.body().getData().get(0).toString());
                    binding.loginTvContent.setText(response.body().getData().get(0).toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<List<HomeBanner>>> call, Throwable t) {
                //请求失败
                MLog.e(t.getMessage());
            }
        });
    }

    //Retrofit+RxJava
    private void getRetrofitRxJava() {
        NetworkManager.getInstance().initRetrofitRxJava()
                .create(IWanAndroidService.class)
                .homeBanner()
                .subscribeOn(Schedulers.io())//切换到IO线程
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程
                // 添加订阅
                .subscribe(new Consumer<ResponseData<List<HomeBanner>>>() {
                    @Override
                    public void accept(ResponseData<List<HomeBanner>> listResponseData) throws Exception {
                        //请求成功
                        if (listResponseData != null) {
                            MLog.e(listResponseData.getData().get(0).toString());
                            binding.loginTvContent.setText(listResponseData.getData().get(0).toString());
                        }
                    }
                }, throwable -> {
                    //请求失败
                    MLog.e(throwable.getMessage());
                });
    }

    //Retrofit+RxJava进一步优化
    private void getRetrofitRxJavaOptimization() {
        NetworkManager.getInstance().initRetrofitRxJava()
                .create(IWanAndroidService.class)
                .homeBanner()
                .compose(ResponseTransformer.obtain())
                .subscribe(new Consumer<List<HomeBanner>>() {
                    @Override
                    public void accept(List<HomeBanner> homeBanners) throws Exception {
                        //请求成功
                        if (homeBanners != null) {
                            MLog.e(homeBanners.get(0).toString());
                            binding.loginTvContent.setText(homeBanners.get(0).toString());
                        }
                    }
                }, new ErrorConsumer() {
                    @Override
                    protected void error(ApiException e) {
                        //请求失败
                        MLog.e(e.getDisplayMessage() + e.getCode());
                    }
                });
    }

    //Retrofit+RxJava+Hilt
    private void getRetrofitRxJavaHilt() {
        iWanAndroidService.homeBanner()
                .compose(ResponseTransformer.obtain())
                .subscribe(new Consumer<List<HomeBanner>>() {
                    @Override
                    public void accept(List<HomeBanner> homeBanners) throws Exception {
                        //请求成功
                        if (homeBanners != null) {
                            MLog.e(homeBanners.get(0).toString());
                            binding.loginTvContent.setText(homeBanners.get(0).toString());
                        }
                    }
                }, new ErrorConsumer() {
                    @Override
                    protected void error(ApiException e) {
                        //请求失败
                        MLog.e(e.getDisplayMessage() + e.getCode());
                    }
                });
    }

    //Retrofit
    private void postRetrofit(Map<String, String> map) {
        //注释3：链式调用
        NetworkManager.getInstance().initRetrofit().create(IWanAndroidService.class)
                .registerRetrofit(map)
                .enqueue(new Callback<ResponseData<RegisterData>>() {
                    @Override
                    public void onResponse(Call<ResponseData<RegisterData>> call, Response<ResponseData<RegisterData>> response) {
                        //请求成功
                        if (response.body().getData().toString() != null) {
                            MLog.e(response.body().getData().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData<RegisterData>> call, Throwable t) {
                        //失败
                        MLog.e(t.getMessage());
                    }
                });
    }

    //Retrofit+RxJava
    private void postRetrofitRxJava(Map<String, String> map) {
        //注释3：链式调用
        NetworkManager.getInstance().initRetrofitRxJava().create(IWanAndroidService.class)
                .register(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseData<RegisterData>>() {
                    @Override
                    public void accept(ResponseData<RegisterData> registerDataResponseData) throws Exception {
                        if (registerDataResponseData != null) {
                            MLog.e(registerDataResponseData.getData().toString());
                        }
                    }
                }, new ErrorConsumer() {
                    @Override
                    protected void error(ApiException e) {
                        MLog.e(e.getDisplayMessage() + e.getCode());
                    }
                });
    }

    //Retrofit+RxJava进一步优化
    private void postRetrofitRxJavaOptimization(Map<String, String> map) {
        NetworkManager.getInstance().initRetrofitRxJava()
                .create(IWanAndroidService.class)
                .register(map)
                .compose(ResponseTransformer.obtain())
                .subscribe(new Consumer<RegisterData>() {
                    @Override
                    public void accept(RegisterData registerData) throws Exception {
                        //请求成功
                        if (registerData != null) {
                            MLog.e(registerData.toString());
                            binding.loginTvContent.setText(registerData.toString());
                        }
                    }
                }, new ErrorConsumer() {
                    @Override
                    protected void error(ApiException e) {
                        //请求失败
                        MLog.e(e.getDisplayMessage() + e.getCode());
                    }
                });
    }

    //Retrofit+RxJava+Hilt
    @SuppressLint("CheckResult")
    private void postRetrofitRxJavaHilt(Map<String, String> map) {
        iWanAndroidService.register(map)
                .compose(ResponseTransformer.obtain())
                .subscribe(registerData -> {
                    //请求成功
                    if (registerData != null) {
                        MLog.e(registerData.toString());
                        binding.loginTvContent.setText(registerData.toString());
                    }
                }, new ErrorConsumer() {
                    @Override
                    protected void error(ApiException e) {
                        //请求失败
                        MLog.e(e.getDisplayMessage() + e.getCode());
                    }
                });
    }
    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    public static String readAssetsTxt(Context context, String fileName){
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName+".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }
}