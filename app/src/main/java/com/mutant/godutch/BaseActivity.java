package com.mutant.godutch;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mutant.godutch.model.Friend;

/**
 * Created by jackie780919 on 2016/2/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public abstract int getLayoutId();

    public abstract void findViews();

    public abstract void setup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        overridePendingTransition(0, 0);
        findViews();
        setup();
        setOrientationLand();
    }

    protected void setOrientationLand() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Nullable
    protected Friend getMeInFriend() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            String mePhotoUrl = "";
            if(firebaseUser.getPhotoUrl() != null) {
                mePhotoUrl = firebaseUser.getPhotoUrl().toString();
            }

            return new Friend(firebaseUser.getUid(), "Me", mePhotoUrl);
        }
        return null;
    }
}
