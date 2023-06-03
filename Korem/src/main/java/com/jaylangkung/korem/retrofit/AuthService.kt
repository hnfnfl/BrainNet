package com.jaylangkung.korem.retrofit

import com.jaylangkung.korem.dataClass.DataSpinnerResponse
import com.jaylangkung.korem.dataClass.DefaultResponse
import com.jaylangkung.korem.dataClass.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    //login user
    @FormUrlEncoded
    @POST("auth/loginUser")
    fun login(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("auth/addToken")
    fun addToken(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
        @Field("device_token") device_token: String,
    ): Call<DefaultResponse>

    //refresh auth token
    @FormUrlEncoded
    @POST("auth/refreshToken")
    fun refreshAuthToken(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
    ): Call<UserResponse>

    //logout
    @FormUrlEncoded
    @POST("auth/logout")
    fun logout(
        @Field("iduser_aktivasi") iduser_aktivasi: String,
    ): Call<DefaultResponse>

    @GET("auth/getSpinnerData")
    fun getSpinnerData(): Call<DataSpinnerResponse>

    @GET("auth/getSpinnerSurat")
    fun getSuratSpinnerData(): Call<DataSpinnerResponse>
}