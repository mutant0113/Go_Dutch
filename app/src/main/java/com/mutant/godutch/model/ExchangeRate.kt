package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by evanfang102 on 2017/5/26.
 */

class ExchangeRate : Parcelable {

    var country: String = ""
    var rate: Double
    var lastUpdated: String = ""

    constructor(country: String, rate: Double, lastUpdated: String) {
        this.country = country
        this.rate = rate
        this.lastUpdated = lastUpdated
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
            override fun createFromParcel(source: Parcel): Event = Event(source)
            override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readDouble(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(country)
        dest.writeDouble(rate)
        dest.writeString(lastUpdated)
    }
}
