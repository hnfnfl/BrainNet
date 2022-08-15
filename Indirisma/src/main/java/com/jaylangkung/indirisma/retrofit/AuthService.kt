package com.jaylangkung.indirisma.retrofit

import com.jaylangkung.indirisma.retrofit.response.DataSpinnerResponse
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.retrofit.response.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthService {
    //login
    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
        @Header("indirisma") indirisma: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("auth/addToken")
    fun addToken(
        @Field("idadmin") idadmin: String,
        @Field("device_token") device_token: String,
        @Header("indirisma") indirisma: String,
    ): Call<DefaultResponse>

    //refresh auth token
    @FormUrlEncoded
    @POST("auth/refreshToken")
    fun refreshAuthToken(
        @Field("email") email: String,
        @Field("idadmin") idadmin: String,
        @Field("device_id") device_id: String,
        @Header("indirisma") indirisma: String,
    ): Call<LoginResponse>

    //logout
    @FormUrlEncoded
    @POST("auth/logout")
    fun logout(
        @Field("idadmin") idadmin: String,
        @Header("indirisma") indirisma: String,
    ): Call<DefaultResponse>


    @GET("auth/getSpinnerData")
    fun getSpinnerData(
        @Header("indirisma") indirisma: String,
    ): Call<DataSpinnerResponse>
}