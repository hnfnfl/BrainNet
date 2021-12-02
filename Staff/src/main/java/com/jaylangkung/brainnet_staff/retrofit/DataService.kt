package com.jaylangkung.brainnet_staff.retrofit

import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.GangguanResponse
import com.jaylangkung.brainnet_staff.retrofit.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface DataService {
    //edit profil penjual
    @Multipart
    @POST("penjual/main_penjual/editProfile")
    fun editProfile(
        @Part("idpenjual") idpenjual: RequestBody,
        @Part("nama_penjual") nama_penjual: RequestBody,
        @Part("nama_toko") nama_toko: RequestBody,
        @Part("alamat_penjual") alamat_penjual: RequestBody,
        @Part("email_penjual") email_penjual: RequestBody,
        @Part("nohp_penjual") nohp_penjual: RequestBody,
        @Part filefoto: MultipartBody.Part? = null,
        @Header("Authorization") token: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/getAbsensi")
    fun getAbsensi(
        @Field("token") token: String,
        @Field("idadmin") idadmin: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @POST("main/getGangguan")
    fun getGangguan(
        @Header("Authorization") tokenAuth: String
    ): Call<GangguanResponse>

    @FormUrlEncoded
    @POST("main/editGangguan")
    fun editGangguan(
        @Field("idgangguan") idgangguan: String,
        @Field("penyelesaian") penyelesaian: String,
        @Field("idadmin") idadmin: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @POST("main/getPelanggan")
    fun getPelanggan(
        @Header("Authorization") tokenAuth: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("main/restartUser")
    fun restartUser(
        @Field("user") user: String,
        @Field("password") password: String,
        @Field("nama") nama: String,
        @Field("paket") paket: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>
}