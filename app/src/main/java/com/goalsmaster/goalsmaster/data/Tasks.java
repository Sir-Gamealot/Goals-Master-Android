package com.goalsmaster.goalsmaster.data;

import android.content.Context;

import com.goalsmaster.goalsmaster.events.LogOffRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by tudor on 7/29/2017.
 */

public class Tasks {

    /**
     * Returns a valid reference to the Goal list path for the current user.
     *
     * @return
     */
    public static DatabaseReference getFirebaseNodeRef() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null) {
            EventBus.getDefault().post(new LogOffRequest(false));
            // TODO decide how to proceed with the call given that something very wrong must have happened to generate null userId.
        }
        String userId = (user != null ? user.getUid() : "error_null_user");
        DatabaseReference goalsRef = db.getReference("Tasks");
        DatabaseReference userRef = goalsRef.child(userId); // Goals/userId/children
        return userRef;
    }

    public static Object getFirebaseItemNodeRef(String id) {
        return getFirebaseNodeRef().child(id);
    }
}
