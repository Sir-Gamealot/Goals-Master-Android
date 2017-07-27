package com.goalsmaster.goalsmaster.utils;

import android.content.Context;

/**
 * Created by tudor on 7/27/2017.
 */

public class UserHelper {
    public static String getLoggedInUsername(Context context) {
        return "Not Logged In";
    }

    public static String getLoggedInRoleName() {
        return "Unknown role";
    }

    public static boolean isLoggedUserAdmin(Context context) {
        return false;
    }

    public static boolean isLoggedUserSuper(Context context) {
        return false;
    }
}
