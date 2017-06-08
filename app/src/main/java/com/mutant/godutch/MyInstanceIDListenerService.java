package com.mutant.godutch;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by evanfang102 on 2017/6/8.
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    public static String TAG = MyInstanceIDListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    // need to send token to realtime database after user logged in
    // TODO create a server to deliver message to other devices
    public static void sendRegistrationToServer(String refreshedToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(!TextUtils.isEmpty(refreshedToken) && firebaseUser != null) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
            database.child("fcmToken").setValue(refreshedToken);
        }
    }

}
