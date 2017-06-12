package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable
import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude
import java.util.*

/**
 * Created by evanfang102 on 2017/5/26.
 */

class Event : Parcelable{

    var id: String = ""
    var title: String = ""
    var description: String = ""
    var friendsShared: List<Friend> = arrayListOf()
    var friendWhoPaidFirst: Friend = Friend()
    var photo: String = ""
    var subtotal: Int = 0
    var tax: Int = 0
    internal set
    var total: Int = 0
    internal set
    var timestamp: HashMap<String, Any> = hashMapOf()
    internal set
    val timestampCreated: Long
    @Exclude
    get() = timestamp["timestamp"] as Long? ?: 0

    constructor() {}

    constructor(title: String, description: String, subtotal: Int, tax: Int, total: Int, friendsShared: List< Friend >) {
        this.title = title
        this.description = description
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.friendsShared = friendsShared
        this.timestamp = HashMap<String, Any>()
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

    protected constructor (`in`: Parcel) {
        this.id = `in`.readString()
        this.title = `in`.readString()
        this.description = `in`.readString()
        this.friendsShared = `in`.createTypedArrayList(Friend.CREATOR)
        this.friendWhoPaidFirst = `in`.readParcelable<Friend>(Friend::class.java.classLoader)
        this.photo = `in`.readString()
        this.subtotal = `in`.readInt()
        this.tax = `in`.readInt()
        this.total = `in`.readInt()
        this.timestamp = `in`.readSerializable() as HashMap<String, Any>
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
            override fun createFromParcel(source: Parcel): Event = Event(source)
            override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {}
}
