package fr.frodriguez.trendingtopic.utils;

import android.content.Context;
import android.util.Log;

/**
 * By Florian on 22/10/2016.
 */
@SuppressWarnings("unused")
public class Logger {

    private final static String PREFIX = "flz";
    private Context _context;
    private String _tag;

    public Logger(String appName, Context context) {
        _context = context;
        _tag = buildLogTag(appName, Utils.getAppVersion(context));
    }

    private static String buildLogTag(String appName, String version) {
        return PREFIX + "_" + appName + ">" + version;
    }

    public void d(String message) {
        if(Utils.isDebugEnabled(_context)) {
            Log.d(_tag, message);
        }
    }
    public static void d(String appName, Context context, String message) {
        if(Utils.isDebugEnabled(context)) {
            Log.d(buildLogTag(appName, Utils.getAppVersion(context)), message);
        }
    }

    public void e(String message) {
        if(Utils.isDebugEnabled(_context)) {
            Log.e(_tag, message);
        }
    }
    public void e(String message, Exception exception) {
        if(Utils.isDebugEnabled(_context)) {
            Log.e(_tag, message, exception);
        }
    }
    public static void e(String appName, Context context, String message) {
        if(Utils.isDebugEnabled(context)) {
            Log.e(buildLogTag(appName, Utils.getAppVersion(context)), message);
        }
    }
    public static void e(String appName, Context context, String message, Exception exception) {
        if(Utils.isDebugEnabled(context)) {
            Log.e(buildLogTag(appName, Utils.getAppVersion(context)), message, exception);
        }
    }

    public void i(String message) {
        if(Utils.isDebugEnabled(_context)) {
            Log.i(_tag, message);
        }
    }
    public static void i(String appName, Context context, String message) {
        if(Utils.isDebugEnabled(context)) {
            Log.i(buildLogTag(appName, Utils.getAppVersion(context)), message);
        }
    }

    public void v(String message) {
        if(Utils.isDebugEnabled(_context)) {
            Log.v(_tag, message);
        }
    }
    public static void v(String appName, Context context, String message) {
        if(Utils.isDebugEnabled(context)) {
            Log.v(buildLogTag(appName, Utils.getAppVersion(context)), message);
        }
    }

    public void w(String message) {
        if(Utils.isDebugEnabled(_context)) {
            Log.w(_tag, message);
        }
    }
    public static void w(String appName, Context context, String message) {
        if(Utils.isDebugEnabled(context)) {
            Log.w(buildLogTag(appName, Utils.getAppVersion(context)), message);
        }
    }

    public void wtf(String message) {
        if(Utils.isDebugEnabled(_context)) {
            Log.wtf(_tag, message);
        }
    }
    public static void wtf(String appName, Context context, String message) {
        if(Utils.isDebugEnabled(context)) {
            Log.wtf(buildLogTag(appName, Utils.getAppVersion(context)), message);
        }
    }
}
