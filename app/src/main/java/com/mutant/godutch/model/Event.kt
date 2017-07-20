package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable
import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude

/**
 * Created by evanfang102 on 2017/5/26.
 */

class Event : Parcelable {

    var key: String = ""
    var title: String = ""
    var description: String = ""
    var subtotal: Int = 0
    var tax: Int = 0
    var total: Int = 0
    var friendsShared: List<Friend> = arrayListOf()
    var friendWhoPaidFirst: Friend = Friend()
    var photoUrl: String = ""
    val timestamp: HashMap<String, Any> = hashMapOf()
    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0

    constructor()

    constructor(title: String, description: String, subtotal: Int, tax: Int, total: Int, friendsShared: List<Friend>, friendWhoPaidFirst: Friend) {
        this.title = title
        this.description = description
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.friendsShared = friendsShared
        this.friendWhoPaidFirst = friendWhoPaidFirst
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

    constructor(photoUrl: String, title: String, description: String, subtotal: Int, tax: Int, total: Int, friendsShared: ArrayList<Friend>, friendWhoPaidFirst: Friend, timestampCreated: Long) {
        this.title = title
        this.description = description
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.friendsShared = friendsShared
        this.friendWhoPaidFirst = friendWhoPaidFirst
        this.photoUrl = photoUrl
        this.timestampCreated = timestampCreated
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
            override fun createFromParcel(source: Parcel): Event = Event(source)
            override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readArrayList(Friend.javaClass.classLoader) as ArrayList<Friend>,
            source.readParcelable(Friend.javaClass.classLoader),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(photoUrl)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeInt(subtotal)
        dest.writeInt(tax)
        dest.writeInt(total)
        dest.writeList(friendsShared)
        dest.writeParcelable(friendWhoPaidFirst, flags)
        dest.writeLong(timestampCreated)
    }
}
