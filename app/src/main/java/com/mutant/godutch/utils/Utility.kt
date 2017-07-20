package com.mutant.godutch.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.InputFilter
import android.text.format.DateUtils
import android.widget.EditText
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

/**
 * Created by evanfang102 on 2017/5/23.
 */

class Utility {

    companion object {
        /**
         * 設定editText輸入長度

         * @param editText
         * *
         * @param maxLength
         */
        fun setMaxLength(editText: EditText, maxLength: Int) {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
        }

        fun getRelativeTimeSpanDate(timestamp: Long): CharSequence {
            return android.text.format.DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS)
        }

        fun intentToGoogleMarketToDownloadApp(context: Context, appMarketId: String) {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appMarketId))
            context.startActivity(marketIntent)
        }

        fun uploadImage(filePath: String, bitmap: Bitmap, onFailureListener: OnFailureListener, onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val firebaseStorage = FirebaseStorage.getInstance().reference.child(filePath)
            val uploadTask = firebaseStorage?.putBytes(data)
            if(uploadTask != null) {
                uploadTask.addOnFailureListener(onFailureListener).addOnSuccessListener(onSuccessListener)
            }
        }

    }

}
