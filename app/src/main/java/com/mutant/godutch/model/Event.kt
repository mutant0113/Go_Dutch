package com.mutant.godutch.model

import android.annotation.SuppressLint
import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude
import com.mutant.godutch.widget.EventTypeWidget.TYPE
import io.mironov.smuggler.AutoParcelable

/**
 * Created by evanfang102 on 2017/5/26.
 */

@SuppressLint("ParcelCreator")
data class Event(
        var key: String = "",
        var photoUrl: String = "",
        var type: TYPE = TYPE.FOOD,
        var title: String = "",
        var subtotal: Double = 0.0,
        var tax: Double = 0.0,
        var total: Double = 0.0,
        var exchangeRate: ExchangeRate? = null,
        var friendsPaid: ArrayList<Friend> = arrayListOf(),
        var friendsShared: ArrayList<Friend> = arrayListOf(),
        var timestamp: HashMap<String, Any> = hashMapOf()) : AutoParcelable {

    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0

    constructor(photoUrl: String, type: TYPE, title: String, subtotal: Double, tax: Double, total: Double,
                exchangeRate: ExchangeRate, friendsPaid: ArrayList<Friend>, friendsShared: ArrayList<Friend>) :
            this("", photoUrl, type, title, subtotal, tax, total, exchangeRate, friendsPaid, friendsShared) {
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

    constructor(photoUrl: String, type: TYPE, title: String, subtotal: Double, tax: Double, total: Double,
                exchangeRate: ExchangeRate, friendsPaid: ArrayList<Friend>, friendsShared: ArrayList<Friend>,
                timestamp: HashMap<String, Any>) :
            this("", photoUrl, type, title, subtotal, tax, total, exchangeRate, friendsPaid, friendsShared, timestamp)
}
