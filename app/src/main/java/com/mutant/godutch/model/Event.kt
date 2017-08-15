package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable
import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude
import com.mutant.godutch.widget.EventTypeWidget.TYPE

/**
 * Created by evanfang102 on 2017/5/26.
 */

class Event : Parcelable {

    var key: String = ""
    var photoUrl: String = ""
    var type: TYPE = TYPE.FOOD
    var title: String = ""
    var description: String = ""
    var subtotal: Int = 0
    var tax: Int = 0
    var total: Int = 0
    var exchangeRate: ExchangeRate? = null
    var friendsShared: List<Friend> = arrayListOf()
    var friendPaid: Friend = Friend() // TODO maybe multi people shared
    var timestamp: HashMap<String, Any> = hashMapOf()
    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0

    constructor()

    constructor(photoUrl: String, type: TYPE, title: String, description: String, subtotal: Int,
                tax: Int, total: Int, exchangeRate: ExchangeRate, friendsShared: List<Friend>, friendPaid: Friend) {
        this.photoUrl = photoUrl
        this.type = type
        this.title = title
        this.description = description
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.exchangeRate = exchangeRate
        this.friendsShared = friendsShared
        this.friendPaid = friendPaid
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

    constructor(photoUrl: String, type: TYPE, title: String, description: String, subtotal: Int,
                tax: Int, total: Int, exchangeRate: ExchangeRate, friendsShared: ArrayList<Friend>, friendPaid: Friend,
                timestamp: HashMap<String, Any>) {
        this.photoUrl = photoUrl
        this.type = type
        this.title = title
        this.description = description
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.exchangeRate = exchangeRate
        this.friendsShared = friendsShared
        this.friendPaid = friendPaid
        this.timestamp = timestamp
    }

    constructor(key: String, photoUrl: String, type: TYPE, title: String, description: String, subtotal: Int,
                tax: Int, total: Int, exchangeRate: ExchangeRate, friendsShared: ArrayList<Friend>, friendPaid: Friend, timestamp: HashMap<String, Any>)
            : this(photoUrl, type, title, description, subtotal, tax, total, exchangeRate, friendsShared, friendPaid, timestamp) {
        this.key = key
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
            source.readSerializable() as TYPE,
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readParcelable(ExchangeRate.javaClass.classLoader),
            source.readArrayList(Friend.javaClass.classLoader) as ArrayList<Friend>,
            source.readParcelable(Friend.javaClass.classLoader),
            source.readSerializable() as HashMap<String, Any>
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(photoUrl)
        dest.writeSerializable(type)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeInt(subtotal)
        dest.writeInt(tax)
        dest.writeInt(total)
        dest.writeParcelable(exchangeRate, flags)
        dest.writeList(friendsShared)
        dest.writeParcelable(friendPaid, flags)
        dest.writeSerializable(timestamp)
    }
}
