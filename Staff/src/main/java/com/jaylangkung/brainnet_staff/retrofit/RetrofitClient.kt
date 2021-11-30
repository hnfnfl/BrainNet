package com.jaylangkung.brainnet_staff.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    fun apiRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.absensi.jaylangkung.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}