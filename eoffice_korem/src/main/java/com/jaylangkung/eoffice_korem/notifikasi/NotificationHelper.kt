package com.jaylangkung.eoffice_korem.notifikasi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.SplashScreen
import com.jaylangkung.eoffice_korem.surat.masuk.SuratMasukActivity
import java.util.regex.Pattern
import kotlin.random.Random

class NotificationHelper(private val context: Context) {

    fun displayNotification(title: String, message: String) {
        val intent: Intent
        var nomerAgenda: String? = null
        val pattern = Pattern.compile("nomer Agenda ([^,]+)")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            nomerAgenda = matcher.group(1)
        }

        // Mengecek hasil penemuan nomerAgenda
        intent = if (nomerAgenda != null) {
            Intent(context, SuratMasukActivity::class.java).putExtra("nomerAgenda", nomerAgenda)
        } else {
            Intent(context, SplashScreen::class.java)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.ringtone1)
        val channelId = "Default Channel"
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setColor(ContextCompat.getColor(context, R.color.primaryColor))
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLights(Color.BLUE, 200, 200)
            .setSound(sound)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
//            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 100, 100, 100)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.setSound(sound, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), mBuilder.build())
    }
}