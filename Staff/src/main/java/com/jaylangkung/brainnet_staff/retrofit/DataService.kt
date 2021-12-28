package com.jaylangkung.brainnet_staff.retrofit

import com.jaylangkung.brainnet_staff.retrofit.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface DataService {
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

    @FormUrlEncoded
    @POST("main/getTiang")
    fun getTiang(
        @Field("serial_number") serial_number: String,
        @Header("Authorization") tokenAuth: String
    ): Call<TiangResponse>

    @FormUrlEncoded
    @POST("main/editTiang")
    fun editTiang(
        @Field("idtiang") idtiang: String,
        @Field("lat") lat: String,
        @Field("lng") lng: String,
        @Field("keterangan") keterangan: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/getHalBaik")
    fun getHalBaik(
        @Field("idadmin") idadmin: String,
        @Header("Authorization") tokenAuth: String
    ): Call<HalBaikResponse>

    @FormUrlEncoded
    @POST("main/insertHalBaik")
    fun insertHalBaik(
        @Field("idadmin") idadmin: String,
        @Field("hal_baik") hal_baik: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/editProfile")
    fun editProfile(
        @Field("idadmin") idadmin: String,
        @Field("email") email: String,
        @Field("nama") nama: String,
        @Field("alamat") alamat: String,
        @Field("telp") telp: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/insertWebApp")
    fun insertWebApp(
        @Field("idadmin") idadmin: String,
        @Field("device_id") device_id: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @POST("main/getTodo")
    fun getTodo(
        @Header("Authorization") tokenAuth: String
    ): Call<TodoResponse>

    @FormUrlEncoded
    @POST("main/insertTodo")
    fun insertTodo(
        @Field("idadmin") idadmin: String,
        @Field("todo") todo: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("main/editTodo")
    fun editTodo(
        @Field("idtodo_list") idtodo_list: String,
        @Field("idadmin") idadmin: String,
        @Header("Authorization") tokenAuth: String
    ): Call<DefaultResponse>
}