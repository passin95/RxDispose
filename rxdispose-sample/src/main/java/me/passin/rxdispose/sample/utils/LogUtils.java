package me.passin.rxdispose.sample.utils;

import android.util.Log;

/**
 * @author: zbb 33775
 * @date: 2019/5/15 15:47
 * @desc:
 */
public class LogUtils {

    private static final String TAG = "RxDispose";

    private static boolean isDebug = true;

    public static void setDebug(boolean isDebug) {
        LogUtils.isDebug = isDebug;
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String subTag, String msg) {
        if (isDebug) {
            Log.d(TAG, "[" + subTag + "]: " + msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String subTag, String msg) {
        if (isDebug) {
            Log.i(TAG, "[" + subTag + "]: " + msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String subTag, String msg) {
        if (isDebug) {
            Log.e(TAG, "[" + subTag + "]: " + msg);
        }
    }

}
