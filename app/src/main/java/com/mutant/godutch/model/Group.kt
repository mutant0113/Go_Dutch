package com.mutant.godutch.model

/**
 * Created by evanfang102 on 2017/3/30.
 */

class Group {

    var id: String = ""
    var title: String = ""
    var description: String = ""
    var photoUrl: String = ""
    var subtotal: Int = 0
    var friends: List<Friend> = arrayListOf()

    constructor() {}

    constructor(id: String, title: String, description: String, photoUrl: String, subtotal: Int, friends: List<Friend>) {
        this.id = id
        this.title = title
        this.description = description
        this.photoUrl = photoUrl
        this.subtotal = subtotal
        this.friends = friends
    }

    constructor(title: String, description: String, photoUrl: String, total: Int, friends: List<Friend>) : this("", title, description, photoUrl, total, friends) {}
}
