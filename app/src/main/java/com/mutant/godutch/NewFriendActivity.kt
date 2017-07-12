package com.mutant.godutch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.activity_new_friend.*

/**
 * Created by evanfang102 on 2017/6/8.
 */

class NewFriendActivity : BaseActivity() {

    internal var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override val layoutId: Int
        get() = R.layout.activity_new_friend

    override fun findViews() {
    }

    override fun setup() {
        setupFirebase()
        setupZxingGenerateQrcode()
    }

    private fun setupFirebase() {
    }

    private fun setupZxingGenerateQrcode() {
        val imageViewQrcode = findViewById(R.id.imageView_qrcode) as AppCompatImageView
        imageViewQrcode.post {
            try {
                val bitmap = encodeAsBitmap(mFirebaseUser?.uid ?: "", imageViewQrcode.width, imageViewQrcode.height)
                imageViewQrcode.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(WriterException::class)
    internal fun encodeAsBitmap(str: String, width: Int, height: Int): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, height, null)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0..h - 1) {
            val offset = y * w
            for (x in 0..w - 1) {
                pixels[offset + x] = if (result.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
        return bitmap
    }

    fun onClickZxingScan(view: View) {
        val zxingPackagename = "com.google.zxing.client.android.SCAN"
        val intent = Intent(zxingPackagename)
        if (packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size == 0) {
            // 未安裝
            Snackbar.make(coordinatorLayout_parent, "請至 Play 商店安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show()
            Utility.intentToGoogleMarketToDownloadApp(this, "com.google.zxing.client.android")
        } else {
            // SCAN_MODE, 可判別所有支援的條碼
            // QR_CODE_MODE, 只判別 QRCode
            // PRODUCT_MODE, UPC and EAN 碼
            // ONE_D_MODE, 1 維條碼
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE")

            // 呼叫ZXing Scanner，完成動作後回傳 1 給 onActivityResult 的 requestCode 參數
            startActivityForResult(intent, 1)
        }
    }

    // 接收 ZXing 掃描後回傳來的結果
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // ZXing回傳的內容
                val newFriendUid = intent.getStringExtra("SCAN_RESULT")
                sendInvitationToNewFriend(newFriendUid)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "取消掃描", Toast.LENGTH_LONG).show()
            }
        }
    }

    // TODO check is user exist
    private fun sendInvitationToNewFriend(newFriendUid: String) {
        if (mFirebaseUser != null) {
            FirebaseDatabase.getInstance().reference.child("users").child(newFriendUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val newFriend = dataSnapshot.getValue(Friend::class.java)
                        newFriend.state = Friend.STATE_NOT_BE_ACCEPTED
                        val databaseFriends = FirebaseDatabase.getInstance().reference.child("friends")
                        databaseFriends.child(mFirebaseUser!!.uid).child(newFriendUid).setValue(newFriend)

                        // TODO fix me
                        //                        Friend me = new Friend(mFirebaseUser.getUid(), mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString());
                        val me = Friend(mFirebaseUser!!.uid, "Evan", "")
                        me.state = Friend.STATE_BE_INVITED
                        databaseFriends.child(newFriendUid).child(me.uid).setValue(me)
                        finish()
                    } else {
                        Snackbar.make(coordinatorLayout_parent, "此ID不存在", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        } else {
            Snackbar.make(coordinatorLayout_parent, "FireBaseUser == null", Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, NewFriendActivity::class.java)
        }
    }
}
