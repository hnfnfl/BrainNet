package com.jaylangkung.korem.retrofit

import com.jaylangkung.korem.dataClass.DefaultResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DataService {
    @FormUrlEncoded
    @POST("main/getAbsensi")
    fun getAbsensi(
        @Field("token") token: String,
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/editGangguan")
    fun editGangguan(
        @Field("idgangguan") idgangguan: String,
        @Field("penyelesaian") penyelesaian: String,
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/restartUser")
    fun restartUser(
        @Field("user") user: String,
        @Field("password") password: String,
        @Field("nama") nama: String,
        @Field("paket") paket: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>


    @FormUrlEncoded
    @POST("main/editProfile")
    fun editProfile(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Field("email") email: String,
        @Field("nama") nama: String,
        @Field("alamat") alamat: String,
        @Field("telp") telp: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/insertWebApp")
    fun insertWebApp(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Field("device_id") device_id: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

//    @POST("main/getNotification")
//    fun getNotification(
//        @Header("Authorization") tokenAuth: String
//    ): Call<NotifikasiResponse>
}