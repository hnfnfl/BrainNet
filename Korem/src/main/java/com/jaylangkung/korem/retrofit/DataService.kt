package com.jaylangkung.korem.retrofit

import com.jaylangkung.korem.dataClass.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("main/getPostTerbaru")
    fun getPostTerbaru(
        @Header("Authorization") tokenAuth: String
    ): Call<PostResponse>

    @FormUrlEncoded
    @POST("main/getGiat")
    fun getGiat(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<GiatResponse>

    @Multipart
    @POST("main/insertGiat")
    fun insertGiat(
        @Part("iduser_aktivasi") iduser: RequestBody,
        @Part("iddepartemen") iddepartemen: RequestBody,
        @Part("jenis") jenis: RequestBody,
        @Part("tujuan") tujuan: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("mulai") mulai: RequestBody,
        @Part("sampai") sampai: RequestBody,
        @Part("proses") proses: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part("posisi_giat") posisiGiat: RequestBody,
        @Part("posisi_anggota") posisiAnggota: RequestBody,
        @Part foto_giat: MultipartBody.Part? = null,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/getNotification")
    fun getNotification(
        @Field("iduser_aktivasi") iduser: String,
        @Header("Authorization") tokenAuth: String
    ): Call<NotifikasiResponse>

    @GET("main/getSiapOpsGerak")
    fun getSiapOpsGerak(
        @Header("Authorization") tokenAuth: String
    ): Call<SiapOpsGerakResponse>

    @FormUrlEncoded
    @POST("main/getPengaduan")
    fun getPengaduan(
        @Field("iduser_aktivasi") iduser: String,
        @Header("Authorization") tokenAuth: String
    ): Call<PengaduanResponse>

    @Multipart
    @POST("main/insertPengaduan")
    fun insertPengaduan(
        @Part("iduser_aktivasi") iduser: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("pengaduan") pengaduan: RequestBody,
        @Part foto_pengaduan: MultipartBody.Part? = null,
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