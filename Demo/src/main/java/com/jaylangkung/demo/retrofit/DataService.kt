package com.jaylangkung.demo.retrofit

import com.jaylangkung.demo.retrofit.response.DefaultResponse
import retrofit2.Call
import retrofit2.http.*

interface DataService {
    @FormUrlEncoded
    @POST("main_demo/getAbsensi")
    fun getAbsensi(
        @Field("token") token: String,
        @Field("idadmin") idadmin: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main_demo/insertWebApp")
    fun insertWebApp(
        @Field("idadmin") idadmin: String,
        @Field("device_id") device_id: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main_demo/editProfile")
    fun editProfile(
        @Field("idadmin") idadmin: String,
        @Field("email") email: String,
        @Field("nama") nama: String,
        @Field("alamat") alamat: String,
        @Field("telp") telp: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>
}