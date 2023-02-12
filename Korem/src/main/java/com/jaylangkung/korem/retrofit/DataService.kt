package com.jaylangkung.korem.retrofit

import com.jaylangkung.korem.dataClass.*
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
    @POST("main/insertCuti")
    fun insertCuti(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Field("cuti") JudulCuti: String,
        @Field("jenis") JenisCuti: String,
        @Field("keterangan") KeteranganCuti: String,
        @Field("mulai") tglMulai: String,
        @Field("sampai") tglSelesai: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/getSurvey")
    fun getSurvey(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<SurveyResponse>

    @FormUrlEncoded
    @POST("main/getAset")
    fun getAset(
        @Field("kode") kode: String,
        @Header("Authorization") tokenAuth: String
    ): Call<ScanAsetResponse>

    @FormUrlEncoded
    @POST("main/getCuti")
    fun getCuti(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<CutiResponse>


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