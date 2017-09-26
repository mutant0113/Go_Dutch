package com.mutant.godutch.model

import android.os.Parcel
import android.os.Parcelable
import com.firebase.client.ServerValue
import com.google.firebase.database.Exclude

/**
 * Created by evanfang102 on 2017/3/30.
 */

class Group : Parcelable {

    var title: String = ""
    var photoUrl: String = ""
    var subtotal: Int = 0
    var friendsUid: List<String> = arrayListOf()
    val timestamp: HashMap<String, Any> = hashMapOf()
    var timestampCreated: Long = 0
        @Exclude
        get() = timestamp["timestamp"] as Long? ?: 0

    @Exclude
    var key: String = ""

    constructor()

    constructor(title: String, photoUrl: String, subtotal: Int, friendsUid: List<String>) {
        this.title = title
        this.photoUrl = photoUrl
        this.subtotal = subtotal
        this.friendsUid = friendsUid
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(photoUrl)
        parcel.writeInt(subtotal)
        parcel.writeStringList(friendsUid)
        parcel.writeString(key)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        photoUrl = parcel.readString()
        subtotal = parcel.readInt()
        friendsUid = parcel.createStringArrayList()
        key = parcel.readString()
    }

}
