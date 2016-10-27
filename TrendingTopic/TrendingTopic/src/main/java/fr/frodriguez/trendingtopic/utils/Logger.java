package fr.frodriguez.trendingtopic.utils;

import android.content.Context;
import android.util.Log;
import static fr.frodriguez.trendingtopic.utils.Utils.buildTag;

/**
 * By Florian on 22/10/2016.
 */
@SuppressWarnings("unused")
public class Logger {

    private String _tag;


    public Logger(String appName, Context context) {
        _tag = buildTag(appName, Utils.getAppVersion(context));
    }

    public void d(String message) {
        Log.d(_tag, message);
    }
    public static void d(String appName, Context context, String message) {
        Log.d(buildTag(appName, Utils.getAppVersion(context)), message);
    }

    public void e(String message) {
        Log.e(_tag, message);
    }
    public void e(String message, Exception exception) {
        Log.e(_tag, message, exception);
    }
    public static void e(String appName, Context context, String message) {
        Log.e(buildTag(appName, Utils.getAppVersion(context)), message);
    }
    public static void e(String appName, Context context, String message, Exception exception) {
        Log.e(buildTag(appName, Utils.getAppVersion(context)), message, exception);
    }

    public void i(String message) {
        Log.i(_tag, message);
    }
    public static void i(String appName, Context context, String message) {
        Log.i(buildTag(appName, Utils.getAppVersion(context)), message);
    }

    public void v(String message) {
        Log.v(_tag, message);
    }
    public static void v(String appName, Context context, String message) {
        Log.v(buildTag(appName, Utils.getAppVersion(context)), message);
    }

    public void w(String message) {
        Log.w(_tag, message);
    }
    public static void w(String appName, Context context, String message) {
        Log.w(buildTag(appName, Utils.getAppVersion(context)), message);
    }

    public void wtf(String message) {
        Log.wtf(_tag, message);
    }
    public static void wtf(String appName, Context context, String message) {
        Log.wtf(buildTag(appName, Utils.getAppVersion(context)), message);
    }
}
