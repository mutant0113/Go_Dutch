package com.mutant.godutch.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.mutant.godutch.LoginActivity
import com.mutant.godutch.R
import com.mutant.godutch.server.WebAgent
import com.mutant.godutch.utils.Utility.Companion.calculateExchangeRate
import kotlinx.android.synthetic.main.fragment_settings.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

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
                if (firebaseAuth.currentUser == null && activity != null) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity.finish()
                }
            }
            auth.signOut()
        }

        button_exchange_rate.setOnClickListener {
            WebAgent.fetchExchangeRate(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val jsonStr = response.body().string()
                    Log.d("erJson", "MOP -> TWD:" + calculateExchangeRate(jsonStr, "USDMOP"))
                    Log.d("erJson", "HKD -> TWD:" + calculateExchangeRate(jsonStr, "USDHKD"))
                }

                override fun onFailure(call: Call, e: IOException) {
                    // TODO
                }

            })
        }
    }


}
