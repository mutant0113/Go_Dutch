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
    var photoUrl: String = ""
    var debt: Int = 0
    var remind: Boolean = false
    var settleUp: Boolean = false
    var state: Int = STATE_ACCEPTED
        @State set

    constructor()

    constructor(uid: String, name: String, photoUrl: String) {
        this.uid = uid
        this.name = name
        this.photoUrl = photoUrl
    }

    constructor(uid: String, name: String, photoUrl: String, debt: Int, remind: Boolean, settleUp: Boolean, @State state: Int) {
        this.uid = uid
        this.name = name
        this.photoUrl = photoUrl
        this.debt = debt
        this.remind = remind
        this.settleUp = settleUp
        this.state = state
    }

    @IntDef(STATE_ACCEPTED.toLong(), STATE_NOT_BE_ACCEPTED.toLong(), STATE_BE_INVITED.toLong())
    annotation class State

    companion object {
        const val STATE_ACCEPTED = 0
        const val STATE_NOT_BE_ACCEPTED = 1
        const val STATE_BE_INVITED = 2

        @JvmField val CREATOR: Parcelable.Creator<Friend> = object : Parcelable.Creator<Friend> {
            override fun createFromParcel(source: Parcel): Friend = Friend(source)
            override fun newArray(size: Int): Array<Friend?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readByte().toInt() != 0,
            source.readByte().toInt() != 0,
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uid)
        dest.writeString(name)
        dest.writeString(photoUrl)
        dest.writeInt(debt)
        dest.writeByte((if (remind) 1 else 0).toByte())
        dest.writeByte((if (settleUp) 1 else 0).toByte())
        dest.writeInt(state)
    }
}
