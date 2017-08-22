package com.practice.heliguang.opengles2library;

import android.util.Log;

/**
 * Created by heliguang on 2017/8/21.
 */

public class Logger {
    public static void v(String tag, String msg) {
        Log.v("OES20/" + tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d("OES20/" + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i("OES20/" + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e("OES20/" + tag, msg);
    }
}
