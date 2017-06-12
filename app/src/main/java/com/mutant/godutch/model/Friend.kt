package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef

/**
 * Created by Mutant on 2017/5/26.
 */

class Friend : Parcelable {

    var uid: String = ""
    var name: String = ""
    var proPicUrl: String = ""
    var needToPay: Int = 0
    var state: Int = STATE_ACCEPTED
        @State set

    constructor() {}

    constructor(uid: String, name: String, proPicUrl: String) {
        this.uid = uid
        this.name = name
        this.proPicUrl = proPicUrl
    }

    @IntDef(STATE_ACCEPTED.toLong(), STATE_NOT_BE_ACCEPTED.toLong(), STATE_BE_INVITED.toLong())
    annotation class State

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.uid)
        dest.writeString(this.name)
        dest.writeString(this.proPicUrl)
        dest.writeInt(this.needToPay)
        dest.writeInt(this.state)
    }

    protected constructor(`in`: Parcel) {
        this.uid = `in`.readString()
        this.name = `in`.readString()
        this.proPicUrl = `in`.readString()
        this.needToPay = `in`.readInt()
        this.state = `in`.readInt()
    }

    companion object {

        const val STATE_ACCEPTED = 0
        const val STATE_NOT_BE_ACCEPTED = 1
        const val STATE_BE_INVITED = 2

        val CREATOR: Parcelable.Creator<Friend> = object : Parcelable.Creator<Friend> {
            override fun createFromParcel(source: Parcel): Friend {
                return Friend(source)
            }

            override fun newArray(size: Int): Array<Friend?> {
                return arrayOfNulls(size)
            }
        }
    }
}
