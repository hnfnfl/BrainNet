package com.jaylangkung.brainnet_staff.utils

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.jaylangkung.brainnet_staff.data_class.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var myPreferences: MySharedPreferences
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["message"]

        NotificationHelper(applicationContext).displayNotification(title!!, body!!)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        myPreferences = MySharedPreferences(this@MyFirebaseMessagingService)
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val newToken = Firebase.messaging.token.result.toString()
        addToken(idadmin, newToken)
        Log.d("TAG", "Refreshed token: $token")
    }

    private fun addToken(idadmin: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.addToken(idadmin, deviceID).enqueue(object : Callback<DefaultResponse> {
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