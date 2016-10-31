package fr.frodriguez.trendingtopic.utils;

/**
 * By Florian on 27/10/2016.
 */

public class Settings {

    public final static String TWITTER_WOEID_LABELS[] = {"World", "France", "US", "UK"};
    public final static int    TWITTER_WOEID_VALUES[] = {1, 23424819, 23424977, 23424975};

    public final static String  SHARED_PREFERENCES         = "fr.frodriguez.trendingtopic.lastTT";
    public final static String  SP_DEBUG                   = "debug";
    public final static boolean SP_DEBUG_DEFAULT           = false;
    public final static String  SP_TT                      = "last_tt";
    public final static String  SP_TT_DEFAULT              = "";
    public final static String  SP_BOOT_ENABLED            = "boot_enabled";
    public final static boolean SP_BOOT_ENABLED_DEFAULT    = true;
    public final static String  SP_WOEID                   = "woeid";
    public final static int     SP_WOEID_DEFAULT           = TWITTER_WOEID_VALUES[0];

    public final static int NOTIFICATION_ID = 0;

    public final static String OAUTH_CONSUMER_KEY        = "Zn4nPDBCy6AQctonRzVGlAsHj";
    public final static String OAUTH_CONSUMER_SECRET     = "nLksHtxGDQFhMVJZRoh0BiOMPkQR2Gpu4OQsFLI6HTSsvhiWGA";
    public final static String OAUTH_ACCESS_TOKEN        = "210685890-T4AN6FbswBPSQdbxIxVa599ojhBVbOxX8AgnBppu";
    public final static String OAUTH_ACCESS_TOKEN_SECRET = "ca51aNstTB33j7NToPtKpXWVq8SkceiwmvKZYuMSxeUtb";
}
