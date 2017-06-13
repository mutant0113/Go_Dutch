package com.mutant.godutch.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.mutant.godutch.LoginActivity
import com.mutant.godutch.R
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_logout.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.addAuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity.finish()
                }
            }
            auth.signOut()
        }
    }
}
