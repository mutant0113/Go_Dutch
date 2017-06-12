package com.mutant.godutch

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mutant.godutch.model.Friend

/**
 * Created by jackie780919 on 2016/2/16.
 */
abstract class BaseActivity : AppCompatActivity() {

    abstract val layoutId: Int

    abstract fun findViews()

    abstract fun setup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        overridePendingTransition(0, 0)
        findViews()
        setup()
        setOrientationLand()
    }

    protected fun setOrientationLand() {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    protected val meInFriend: Friend
        get() {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                var mePhotoUrl = ""
                if (firebaseUser.photoUrl != null) {
                    mePhotoUrl = firebaseUser.photoUrl!!.toString()
                }

                return Friend(firebaseUser.uid, "Me", mePhotoUrl)
            }
            return Friend()
        }
}
