package com.scc.wanandroid.utils;

import android.util.Log;

/**
  * 创建人：帅次
  * 创建时间：2021/11/15
  * 功能：日志打印
  */
public class MLog {
    public static final String TAG_START = "-sc-";
    private MLog() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void e(Class<?> cla, String msg) {
        e(cla.getSimpleName(), msg);
    }

    public static void e(String msg) {
        Log.e(TAG_START, msg + "");
    }
    public static void e(String tag, String msg) {
        Log.e(TAG_START + tag, msg + "");
    }

}
