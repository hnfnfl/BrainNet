package com.jaylangkung.korem.retrofit

import com.jaylangkung.korem.dataClass.*
import retrofit2.Call
import retrofit2.http.*

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
    @POST("main/insertSurvey")
    fun insertSurvey(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Field("jawaban") jawaban: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

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

    @GET("main/getPost")
    fun getPost(
        @Header("Authorization") tokenAuth: String
    ): Call<PostResponse>

    @FormUrlEncoded
    @POST("main/getGiat")
    fun getGiat(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<GiatResponse>

    @FormUrlEncoded
    @POST("main/insertGiat")
    fun insertGiat(
        @Field("iduser_aktivasi") iduser: String,
        @Field("iddepartemen") iddepartemen: String,
        @Field("jenis") jenis: String,
        @Field("tujuan") tujuan: String,
        @Field("keterangan") keterangan: String,
        @Field("mulai") mulai: String,
        @Field("sampai") sampai: String,
        @Field("proses") proses: String,
        @Field("lokasi") lokasi: String,
        @Field("posisi_giat") posisiGiat: String,
        @Field("posisi_anggota") posisiAnggota: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/getNotification")
    fun getNotification(
        @Field("iduser_aktivasi") iduser: String,
        @Header("Authorization") tokenAuth: String
    ): Call<NotifikasiResponse>

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