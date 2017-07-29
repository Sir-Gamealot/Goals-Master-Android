package com.goalsmaster.goalsmaster.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by tudor on 7/27/2017.
 */

public class UserHelper {
    public static String getLoggedInUsername(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null)
            return "Not Logged In";
        return
                auth.getCurrentUser().getDisplayName();
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
