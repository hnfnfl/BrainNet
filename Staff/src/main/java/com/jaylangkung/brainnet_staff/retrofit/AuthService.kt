package com.jaylangkung.brainnet_staff.retrofit

import com.example.e_kan.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    //login
    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("penjual/auth_penjual/addToken")
    fun addToken(
        @Field("idpenjual") idpenjual: String,
        @Field("device_token") device_token: String,
    ): Call<DefaultResponse>

    //refresh auth token
    @FormUrlEncoded
    @POST("penjual/auth_penjual/refreshToken")
    fun refreshAuthToken(
        @Field("idpenjual") idpenjual: String
    ): Call<LoginResponse>

    //get Foto User
    @FormUrlEncoded
    @POST("penjual/auth_penjual/getUserFoto")
    fun getUserFoto(
        @Field("idpenjual") idpenjual: String
    ): Call<LoginResponse>
}