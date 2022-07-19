package com.jaylangkung.indirisma.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiWilayah {
    fun apiRequest(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dev.farizdotid.com/api/daerahindonesia/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}