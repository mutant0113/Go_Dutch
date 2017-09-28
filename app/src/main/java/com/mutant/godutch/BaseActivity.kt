package com.mutant.godutch

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.mutant.godutch.model.Friend

/**
 * Created by jackie780919 on 2016/2/16.
 */
abstract class BaseActivity : AppCompatActivity() {

    abstract val layoutId: Int

    abstract fun setup()

    internal var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        overridePendingTransition(0, 0)
        setupToolbar()
        setup()
        setOrientationLand()
    }

    internal fun hideToobar() {
        findViewById(R.id.tool_bar)?.visibility = View.GONE
    }

    internal fun setupToolbar() {
        mToolbar = findViewById(R.id.tool_bar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    internal fun setupToolbar(rootView: View) {
        mToolbar = rootView.findViewById(R.id.tool_bar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setOrientationLand() {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    val me: Friend
        get() {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                var mePhotoUrl = ""
                if (firebaseUser.photoUrl != null) {
                    mePhotoUrl = firebaseUser.photoUrl!!.toString()
                }

                return Friend(firebaseUser.uid, firebaseUser.displayName!!, mePhotoUrl)
            }
            return Friend()
        }
}
