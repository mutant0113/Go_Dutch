package com.mutant.godutch

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex

import com.firebase.client.Firebase
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Mutant on 2017/3/22.
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this.applicationContext)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }
}
