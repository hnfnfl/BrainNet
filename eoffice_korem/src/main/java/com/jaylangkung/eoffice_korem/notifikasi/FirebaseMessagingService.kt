package com.jaylangkung.eoffice_korem.notifikasi

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.retrofit.AuthService
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var myPreferences: MySharedPreferences

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification!!.title
        val body = remoteMessage.notification!!.body

        NotificationHelper(applicationContext).displayNotification(title!!, body!!)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        myPreferences = MySharedPreferences(this@MyFirebaseMessagingService)
        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newToken = task.result
                addToken(iduser, newToken!!)
            } else {
                // Handle the error
                val exception = task.exception
                ErrorHandler().responseHandler(
                    this@MyFirebaseMessagingService, "onNewToken", exception.toString()
                )
            }
        }
        Log.d("TAG", "Refreshed token: $token")
    }

    private fun addToken(iduserAktivasi: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.addToken(iduserAktivasi, deviceID).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Log.d("addToken", response.body()!!.message)
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@MyFirebaseMessagingService, "addToken | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MyFirebaseMessagingService, "addToken | onFailure", t.message.toString()
                )
            }
        })
    }
}