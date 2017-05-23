package com.mutant.godutch;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mutant on 2017/3/22.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
//        MultiDex.install(this);
        Fabric.with(this.getApplicationContext(), new Crashlytics());
    }
}
