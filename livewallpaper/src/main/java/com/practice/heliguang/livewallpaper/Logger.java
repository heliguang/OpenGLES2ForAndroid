package com.practice.heliguang.livewallpaper;

import android.util.Log;

/**
 * Created by heliguang on 2017/8/21.
 */

public class Logger {
    public static void v(String tag, String msg) {
        Log.v("AH/" + tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d("AH/" + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i("AH/" + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e("AH/" + tag, msg);
    }
}
