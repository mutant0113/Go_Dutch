package com.mutant.godutch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.mutant.godutch.LoginActivity;
import com.mutant.godutch.R;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class SettingsFragment extends Fragment {

    Button mButtonLogout;

    public SettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mButtonLogout = (Button) view.findViewById(R.id.button_logout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() == null) {
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    }
                });
                auth.signOut();
            }
        });
    }
}
