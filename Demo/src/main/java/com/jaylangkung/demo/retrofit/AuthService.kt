package com.jaylangkung.demo.retrofit

import com.jaylangkung.demo.retrofit.response.DefaultResponse
import com.jaylangkung.demo.retrofit.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    //login
    @FormUrlEncoded
    @POST("auth_demo/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("auth_demo/addToken")
    fun addToken(
        @Field("idadmin") idadmin: String,
        @Field("device_token") device_token: String,
    ): Call<DefaultResponse>

    //refresh auth token
    @FormUrlEncoded
    @POST("auth_demo/refreshToken")
    fun refreshAuthToken(
        @Field("email") email: String,
        @Field("idadmin") idadmin: String,
        @Field("device_id") device_id: String
    ): Call<LoginResponse>

    //get Foto User
    @FormUrlEncoded
    @POST("penjual/auth_penjual/getUserFoto")
    fun getUserFoto(
        @Field("idpenjual") idpenjual: String
    ): Call<LoginResponse>

    //logout
    @FormUrlEncoded
    @POST("auth_demo/logout")
    fun logout(
        @Field("idadmin") idadmin: String,
    ): Call<DefaultResponse>
}