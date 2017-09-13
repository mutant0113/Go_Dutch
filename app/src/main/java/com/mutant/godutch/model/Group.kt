package com.mutant.godutch.model

import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude

/**
 * Created by evanfang102 on 2017/3/30.
 */

class Group {

    var title: String = ""
    var photoUrl: String = ""
    var subtotal: Int = 0
    var friendsUid: List<String> = arrayListOf()
    val timestamp: HashMap<String, Any> = hashMapOf()
    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0
    var key: String = ""
        @Exclude
        get() = key

    constructor() {}

    constructor(title: String, photoUrl: String, subtotal: Int, friendsUid: List<String>) {
        this.title = title
        this.photoUrl = photoUrl
        this.subtotal = subtotal
        this.friendsUid = friendsUid
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

}
