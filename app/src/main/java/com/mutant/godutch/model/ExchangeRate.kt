package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by evanfang102 on 2017/5/26.
 */

class ExchangeRate : Parcelable {
    var jsonKey: String

    var rate: Double

    var lastUpdated: String = ""

    var country: String = ""

    constructor(jsonKey: String, rate: Double, lastUpdated: String, country: String) {
        this.jsonKey = jsonKey
        this.rate = rate
        this.lastUpdated = lastUpdated
        this.country = country
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ExchangeRate> = object : Parcelable.Creator<ExchangeRate> {
            override fun createFromParcel(source: Parcel): ExchangeRate = ExchangeRate(source)
            override fun newArray(size: Int): Array<ExchangeRate?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(jsonKey)
        dest.writeDouble(rate)
        dest.writeString(lastUpdated)
        dest.writeString(country)
    }
}
