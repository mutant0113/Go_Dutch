package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable
import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude
import com.mutant.godutch.widget.EventTypeWidget.TYPE

@Suppress("UNCHECKED_CAST")
/**
 * Created by evanfang102 on 2017/5/26.
 */

class Event : Parcelable {

    var key: String = ""
    var photoUrl: String = ""
    var type: TYPE = TYPE.FOOD
    var title: String = ""
    var subtotal: Double = 0.0
    var tax: Double = 0.0
    var total: Double = 0.0
    var exchangeRate: ExchangeRate? = null
    var friendsShared: List<Friend> = arrayListOf()
    var friendPaid: List<Friend> = arrayListOf()
    var timestamp: HashMap<String, Any> = hashMapOf()
    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0

    constructor()

    constructor(photoUrl: String, type: TYPE, title: String, subtotal: Double, tax: Double, total: Double,
                exchangeRate: ExchangeRate, friendPaid: List<Friend>, friendsShared: List<Friend>) {
        this.photoUrl = photoUrl
        this.type = type
        this.title = title
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.exchangeRate = exchangeRate
        this.friendPaid = friendPaid
        this.friendsShared = friendsShared
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

    constructor(photoUrl: String, type: TYPE, title: String, subtotal: Double, tax: Double, total: Double,
                exchangeRate: ExchangeRate, friendPaid: List<Friend>, friendsShared: List<Friend>,
                timestamp: HashMap<String, Any>) {
        this.photoUrl = photoUrl
        this.type = type
        this.title = title
        this.subtotal = subtotal
        this.tax = tax
        this.total = total
        this.exchangeRate = exchangeRate
        this.friendPaid = friendPaid
        this.friendsShared = friendsShared
        this.timestamp = timestamp
    }

    constructor(key: String, photoUrl: String, type: TYPE, title: String, subtotal: Double,
                tax: Double, total: Double, exchangeRate: ExchangeRate, friendPaid: List<Friend>,
                friendsShared: List<Friend>, timestamp: HashMap<String, Any>)
            : this(photoUrl, type, title, subtotal, tax, total, exchangeRate, friendPaid, friendsShared, timestamp) {
        this.key = key
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
            override fun createFromParcel(source: Parcel): Event = Event(source)
            override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readSerializable() as TYPE,
            source.readString(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readParcelable(ExchangeRate.javaClass.classLoader),
            source.readArrayList(Friend.javaClass.classLoader) as ArrayList<Friend>,
            source.readArrayList(Friend.javaClass.classLoader) as ArrayList<Friend>,
            source.readSerializable() as HashMap<String, Any>
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(photoUrl)
        dest.writeSerializable(type)
        dest.writeString(title)
        dest.writeDouble(subtotal)
        dest.writeDouble(tax)
        dest.writeDouble(total)
        dest.writeParcelable(exchangeRate, flags)
        dest.writeList(friendsShared)
        dest.writeList(friendPaid)
        dest.writeSerializable(timestamp)
    }
}
