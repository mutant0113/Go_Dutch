package com.mutant.godutch.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import io.fabric.sdk.android.services.settings.IconRequest.build

/**
 * Created by evanfang102 on 2016/5/11.
 */
object NotificationHelper {

    val NOTIFY_FLAG_DEFAULT = -1
    val NOTIFY_ID_DEFAULT = 0

    fun getNotificationBuilder(context: Context, notifyId: Int, title: String, content: String, iconId: Int, flags: Int): Notification.Builder {
        return getNotifyBuilder(context, title, content, iconId, null, flags)
    }

    fun notify(context: Context, notifyId: Int, title: String, content: String, iconId: Int, pendingIntent: PendingIntent, flags: Int) {
        var notifyId = notifyId
        //取得Notification服務
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotifyBuilder(context, title, content, iconId, pendingIntent, flags).build()
        if (flags == NOTIFY_FLAG_DEFAULT) {
            notifyId = System.currentTimeMillis().toInt()
            notification.flags = Notification.FLAG_AUTO_CANCEL
        } else {
            notification.flags = flags
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            notification.vibrate = LongArray(0)
            notificationManager.notify(notifyId, notification) // 發送通知
        }
    }

    fun notify(context: Context, titleId: Int, contentId: Int, iconId: Int, pendingIntent: PendingIntent) {
        val title = context.getString(titleId)
        val content = context.getString(contentId)
        notify(context, NOTIFY_ID_DEFAULT, title, content, iconId, pendingIntent, -1)
    }

    fun notify(context: Context, notifyId: Int, builder: Notification.Builder) {
        notify(context, notifyId, builder.build())
    }

    fun notify(context: Context, notifyId: Int, notification: Notification) {
        //取得Notification服務
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notifyId == NOTIFY_FLAG_DEFAULT) {
            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            notification.vibrate = LongArray(0)
        }
        notificationManager.notify(notifyId, notification) // 發送通知
    }

    fun notify(context: Context, title: String, content: String, iconId: Int, pendingIntent: PendingIntent) {
        notify(context, NOTIFY_ID_DEFAULT, title, content, iconId, pendingIntent, NOTIFY_FLAG_DEFAULT)
    }

    private fun getNotificationIcon(whiteIcon: Int, normalIcon: Int): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        val selectIconId = if (useWhiteIcon) whiteIcon else normalIcon
        return selectIconId
    }

    fun getNotifyBuilder(context: Context, title: String, content: String, iconId: Int, pendingIntent: PendingIntent?, flags: Int): Notification.Builder {
        val autoCancel = true // 點擊通知後是否要自動移除掉通知
        val bigTextStyle = Notification.BigTextStyle() // 建立BigTextStyle
        bigTextStyle.setBigContentTitle(title) // 當BigTextStyle顯示時，用BigTextStyle的setBigContentTitle覆蓋setContentTitle的設定
        bigTextStyle.bigText(content) // 設定BigTextStyle的文字內容
        return Notification.Builder(context).setSmallIcon(iconId).setContentTitle(title).setContentText(content).setStyle(bigTextStyle).setAutoCancel(autoCancel).setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
    }

    //設定當按下這個通知之後要執行的activity
    fun getPendingIntent(context: Context, cls: Class<*>): PendingIntent {
        val notifyIntent = Intent(context, cls)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(context, 0, notifyIntent, 0)
    }

    fun getPendingIntent(context: Context, intent: Intent): PendingIntent {
        val requestCode = System.currentTimeMillis().toInt() // must define different requestCode to prevent from going to the same page
        return PendingIntent.getActivity(context, requestCode, intent, 0)
    }

    fun isNotificationExists(context: Context, notifyId: Int): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notifications = notificationManager.activeNotifications
            for (notification in notifications) {
                if (notification.id == notifyId) {
                    return true
                }
            }
        }
        return false
    }

    fun dismissNotification(context: Context, notifyId: Int) {
        if (isNotificationExists(context, notifyId)) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notifyId)
        }
    }

}
