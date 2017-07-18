package com.mutant.godutch.model

/**
 * Created by evanfang102 on 2017/3/30.
 */

class Group {

    var title: String = ""
    var description: String = ""
    var photoUrl: String = ""
    var subtotal: Int = 0
    var friends: List<Friend> = arrayListOf()

    constructor() {}

    constructor(title: String, description: String, photoUrl: String, subtotal: Int, friends: List<Friend>) {
        this.title = title
        this.description = description
        this.photoUrl = photoUrl
        this.subtotal = subtotal
        this.friends = friends
    }

}
