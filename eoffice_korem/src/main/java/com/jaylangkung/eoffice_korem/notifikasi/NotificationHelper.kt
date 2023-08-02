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
import java.util.regex.Pattern
import kotlin.random.Random

class NotificationHelper(private val context: Context) {

    fun displayNotification(title: String, message: String) {
        var intent = Intent()
        val regexNoAgenda = Pattern.compile("nomer Agenda ([^,]+)").matcher(message)
        if (regexNoAgenda.find()) {
            val nomerAgenda = regexNoAgenda.group(1) as String
            val regexSuratType = Pattern.compile("E-OFFICE/(\\w)").matcher(nomerAgenda)
            if (regexSuratType.find()) {
                val suratType = regexSuratType.group(1) as String
                intent = Intent(context, SplashScreen::class.java).apply {
                    putExtra("suratType", suratType)
                    putExtra("nomerAgenda", nomerAgenda)
                }
            }
        } else {
            intent = Intent(context, SplashScreen::class.java)
        }

        // Mengecek hasil penemuan nomerAgenda
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.ringtone1)
        val channelId = "Default Channel"
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId).apply {
            color = ContextCompat.getColor(context, R.color.primaryColor)
            setContentTitle(title)
            setSmallIcon(R.mipmap.ic_launcher)
            setStyle(NotificationCompat.BigTextStyle().bigText(message))
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_HIGH
            setLights(Color.BLUE, 200, 200)
            setSound(sound)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val audioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                enableLights(true)
                lightColor = Color.BLUE
                setSound(sound, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), mBuilder.build())
    }
}