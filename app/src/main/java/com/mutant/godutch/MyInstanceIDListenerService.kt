package com.mutant.godutch

import android.text.TextUtils
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by evanfang102 on 2017/6/8.
 */

class MyInstanceIDListenerService : FirebaseInstanceIdService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)
        sendRegistrationToServer(refreshedToken)
    }

    companion object {

        var TAG = MyInstanceIDListenerService::class.java.simpleName!!

        // need to send token to realtime database after user logged in
        // TODO create a com.mutant.godutch.server to deliver message to other devices
        fun sendRegistrationToServer(refreshedToken: String) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (!TextUtils.isEmpty(refreshedToken) && firebaseUser != null) {
                val database = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser.uid).child("fcmToken")
                database.addListenerForSingleValueEvent(object: ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var isValueExists = false
                        val iterator = dataSnapshot.children.iterator()
                        while (iterator.hasNext()) {
                            val token: String = (iterator.next() as DataSnapshot).value as String
                            if(token == refreshedToken) {
                                isValueExists = true
                            }
                        }

                        if(isValueExists) {
                            DebugHelper.debugLog(TAG, "FCM refreshedToken already exists")
                        } else {
                            database.push().setValue(refreshedToken)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })
            }
        }
    }

}
