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
    var friends: List<Friend> = arrayListOf()
    val timestamp: HashMap<String, Any> = hashMapOf()
    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0

    constructor() {}

    constructor(title: String, photoUrl: String, subtotal: Int, friends: List<Friend>) {
        this.title = title
        this.photoUrl = photoUrl
        this.subtotal = subtotal
        this.friends = friends
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

}
