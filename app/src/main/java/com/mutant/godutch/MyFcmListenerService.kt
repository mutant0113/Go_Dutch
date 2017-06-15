package com.mutant.godutch

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mutant.godutch.utils.NotificationHelper

/**
 * Created by evanfang102 on 2017/6/8.
 */

class MyFcmListenerService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        val from = message?.from
        val data = message?.data

        val title = "send from app"
        val content =  data?.get("body")
        val drawableId = R.drawable.ic_add_circle_outline_white
        val pendingIntent = NotificationHelper.getPendingIntent(this, EventsActivity::class.java)
        NotificationHelper.notify(this, NotificationHelper.NOTIFY_FLAG_DEFAULT, title, content, drawableId, NotificationHelper.NOTIFY_FLAG_DEFAULT)
    }
}
